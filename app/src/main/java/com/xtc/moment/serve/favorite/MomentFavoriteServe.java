package com.xtc.moment.serve.favorite;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.architecture.mvp.BaseServe;
import com.xtc.log.LogUtil;
import com.xtc.moment.db.bean.DbFavoriteMoment;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.db.dao.FavoriteMomentDao;
import com.xtc.moment.favorite.FavoriteSnapshot;
import com.xtc.moment.serve.ServerCache;
import com.xtc.moment.util.EventData;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.utils.encode.JSONUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 本地动态收藏服务实现。
 *
 * <p>收藏态判断走内存集合，避免列表滚动时逐条查库；集合在第一次使用时从库里全量加载，
 * 之后由 {@link #collect} / {@link #uncollect} 增量维护。
 *
 * <p>动态被删除时三张表都会被硬删，因此这里把动态、评论与点赞记录一起写进
 * {@link DbFavoriteMoment#getSnapshotJson()}，收藏列表在动态消失后回退到快照渲染。
 */
public class MomentFavoriteServe extends BaseServe implements IMomentFavoriteServe {

    private static final String TAG = "MomentFavoriteServe";

    /** 快照里最多保留的评论条数：卡片本身最多展示 5 条，多存没有意义。 */
    private static final int SNAPSHOT_COMMENT_LIMIT = 5;

    /** 快照里最多保留的点赞记录条数，避免单条快照过大。 */
    private static final int SNAPSHOT_LIKE_LIMIT = 50;

    private final FavoriteMomentDao favoriteMomentDao;

    /** 已收藏的 momentId 集合，只在内存里维护。 */
    private final Set<String> collectedIds = new HashSet<>();

    private volatile boolean cacheLoaded;

    private MomentFavoriteServe(Context context) {
        super(context);
        this.favoriteMomentDao = ServerCache.getDao(context, FavoriteMomentDao.class);
        ServerCache.putBusinessServer(this);
        // 列表绑定时要同步判断收藏态，这里先在后台把内存集合准备好，尽量不占用主线程。
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                MomentFavoriteServe.this.ensureCacheLoaded();
            }
        });
    }

    public static IMomentFavoriteServe getInstance(Context context) {
        return ServerCache.getBusinessServer(context, MomentFavoriteServe.class);
    }

    @Override
    public boolean isCollected(String momentId) {
        if (TextUtils.isEmpty(momentId)) {
            return false;
        }
        ensureCacheLoaded();
        synchronized (this.collectedIds) {
            return this.collectedIds.contains(momentId);
        }
    }

    @Override
    public boolean collect(DbMoment moment, List<DbMomentComment> comments, List<DbLikeMessage> likes) {
        if (moment == null || TextUtils.isEmpty(moment.getMomentId())) {
            LogUtil.w(TAG, "collect: moment is invalid");
            return false;
        }
        String snapshotJson = buildSnapshotJson(moment, comments, likes);
        long now = System.currentTimeMillis();
        DbFavoriteMoment favorite = new DbFavoriteMoment();
        favorite.setMomentId(moment.getMomentId());
        favorite.setMomentWatchId(moment.getWatchId());
        favorite.setMomentName(moment.getName());
        favorite.setSnapshotJson(snapshotJson);
        favorite.setCreateTime(Long.valueOf(now));
        favorite.setUpdateTime(Long.valueOf(now));
        boolean success = this.favoriteMomentDao.insertOrUpdate(favorite);
        LogUtil.i(TAG, "collect momentId = " + moment.getMomentId() + ", success = " + success);
        if (success) {
            synchronized (this.collectedIds) {
                this.collectedIds.add(moment.getMomentId());
            }
            this.cacheLoaded = true;
            notifyFavoriteChanged(moment);
        }
        return success;
    }

    @Override
    public boolean uncollect(String momentId) {
        if (TextUtils.isEmpty(momentId)) {
            return false;
        }
        boolean success = this.favoriteMomentDao.deleteByMomentId(momentId);
        LogUtil.i(TAG, "uncollect momentId = " + momentId + ", success = " + success);
        synchronized (this.collectedIds) {
            this.collectedIds.remove(momentId);
        }
        this.cacheLoaded = true;
        if (success) {
            DbMoment moment = new DbMoment();
            moment.setMomentId(momentId);
            notifyFavoriteChanged(moment);
        }
        return success;
    }

    @Override
    public boolean refreshSnapshot(DbMoment moment, List<DbMomentComment> comments,
            List<DbLikeMessage> likes) {
        if (moment == null || TextUtils.isEmpty(moment.getMomentId())) {
            return false;
        }
        DbFavoriteMoment local = this.favoriteMomentDao.queryByMomentId(moment.getMomentId());
        if (local == null) {
            return false;
        }
        local.setSnapshotJson(buildSnapshotJson(moment, comments, likes));
        local.setMomentWatchId(moment.getWatchId());
        local.setMomentName(moment.getName());
        local.setUpdateTime(Long.valueOf(System.currentTimeMillis()));
        return this.favoriteMomentDao.updateBy(local, DbFavoriteMoment.MOMENT_ID_FIELD_NAME,
                local.getMomentId());
    }

    @Override
    public List<DbFavoriteMoment> loadAll() {
        return this.favoriteMomentDao.queryAllOrderByCreateTimeDesc();
    }

    @Override
    public FavoriteSnapshot parseSnapshot(DbFavoriteMoment favorite) {
        if (favorite == null) {
            return null;
        }
        return parseSnapshot(favorite.getSnapshotJson());
    }

    @Override
    public FavoriteSnapshot parseSnapshot(String snapshotJson) {
        if (TextUtils.isEmpty(snapshotJson)) {
            return null;
        }
        FavoriteSnapshot snapshot = JSONUtil.fromJSON(snapshotJson, FavoriteSnapshot.class);
        if (snapshot == null || snapshot.getMoment() == null) {
            LogUtil.w(TAG, "parseSnapshot: snapshot is invalid");
            return null;
        }
        return snapshot;
    }

    @Override
    public void invalidateCache() {
        synchronized (this.collectedIds) {
            this.collectedIds.clear();
        }
        this.cacheLoaded = false;
    }

    /** 第一次判断收藏态时把整张收藏表读进内存。 */
    private void ensureCacheLoaded() {
        if (this.cacheLoaded) {
            return;
        }
        synchronized (this) {
            if (this.cacheLoaded) {
                return;
            }
            List<DbFavoriteMoment> favorites = this.favoriteMomentDao.queryAllOrderByCreateTimeDesc();
            synchronized (this.collectedIds) {
                this.collectedIds.clear();
                if (favorites != null) {
                    for (DbFavoriteMoment favorite : favorites) {
                        if (favorite != null && !TextUtils.isEmpty(favorite.getMomentId())) {
                            this.collectedIds.add(favorite.getMomentId());
                        }
                    }
                }
            }
            this.cacheLoaded = true;
            LogUtil.i(TAG, "ensureCacheLoaded: size = " + this.collectedIds.size());
        }
    }

    /** 把动态、评论与点赞记录打包成一份 JSON 快照。 */
    private String buildSnapshotJson(DbMoment moment, List<DbMomentComment> comments,
            List<DbLikeMessage> likes) {
        FavoriteSnapshot snapshot = new FavoriteSnapshot();
        snapshot.setMoment(moment);
        snapshot.setComments(limitList(comments, SNAPSHOT_COMMENT_LIMIT));
        snapshot.setLikes(limitList(likes, SNAPSHOT_LIKE_LIMIT));
        return JSONUtil.toJSON(snapshot);
    }

    private <T> List<T> limitList(List<T> source, int limit) {
        if (source == null) {
            return new ArrayList<>(0);
        }
        if (source.size() <= limit) {
            return new ArrayList<>(source);
        }
        return new ArrayList<>(source.subList(0, limit));
    }

    private void notifyFavoriteChanged(DbMoment moment) {
        EventBus.getDefault().post(new EventData<>(EventData.CHANGE_FAVORITE, moment));
    }
}
