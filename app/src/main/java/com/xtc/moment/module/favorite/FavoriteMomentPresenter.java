package com.xtc.moment.module.favorite;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.moment.LogTag;
import com.xtc.moment.MomentApp;
import com.xtc.moment.base.BaseInteractPresenter;
import com.xtc.moment.db.bean.DbFavoriteMoment;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.db.bean.prerogative.DbMomentPrerogativeLike;
import com.xtc.moment.favorite.FavoriteSnapshot;
import com.xtc.moment.module.illegal.handler.IllegalMessageHandler;
import com.xtc.moment.prerogative.MomentPrerogativeServeImpl;
import com.xtc.moment.serve.MessageTransitionServe;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.moment.serve.favorite.IMomentFavoriteServe;
import com.xtc.moment.serve.favorite.MomentFavoriteServe;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.LikeDrawableCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 收藏的动态 Presenter：纯本地读取，不请求网络。
 *
 * <p>每条收藏先看本地 {@code moment} 表里还有没有这条动态：
 * <ul>
 *     <li>还在：用实时数据渲染（点赞数、评论列表都是最新的），同时把快照刷新一份；</li>
 *     <li>已经被删：本地三张表的数据都没了，回退到收藏时留下的快照，并标记为已删除。</li>
 * </ul>
 * 这样动态被删之后收藏列表既不丢条目，也还能看到「最后一次的点赞数和评论列表」。
 */
public class FavoriteMomentPresenter extends BaseInteractPresenter<IFavoriteMomentView> {

    private static final String TAG = LogTag.tag("FavoriteMomentPresenter");

    private final IMomentFavoriteServe favoriteServe;
    private final LikeDrawableCache likeDrawableCache;

    public FavoriteMomentPresenter(Context context) {
        this.mContext = context;
        this.iMomentServe = MomentServeImpl.getInstance(context);
        this.favoriteServe = MomentFavoriteServe.getInstance(context);
        this.likeDrawableCache = new LikeDrawableCache(context);
    }

    public LikeDrawableCache getLikeDrawableCache() {
        return this.likeDrawableCache;
    }

    /** 加载全部收藏。收藏是用户自选的数据，量不大，一次性读完即可。 */
    public void loadFavorites() {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                final List<DbMoment> moments = new ArrayList<>();
                final Map<String, List<DbLikeMessage>> praiseRecords = new HashMap<>();
                final Set<String> deletedIds = new HashSet<>();
                List<DbFavoriteMoment> favorites = FavoriteMomentPresenter.this.favoriteServe.loadAll();
                if (favorites != null) {
                    for (DbFavoriteMoment favorite : favorites) {
                        if (favorite == null || TextUtils.isEmpty(favorite.getMomentId())) {
                            continue;
                        }
                        FavoriteSnapshot snapshot = FavoriteMomentPresenter.this.favoriteServe
                                .parseSnapshot(favorite);
                        // 动态还在本地库里：优先用实时数据，顺手把快照刷成最新的。
                        DbMoment live = loadLiveMoment(favorite.getMomentId());
                        if (live != null) {
                            List<DbLikeMessage> liveLikes = FavoriteMomentPresenter.this.iMomentServe
                                    .getLikeMessageByMomentId(favorite.getMomentId());
                            FavoriteMomentPresenter.this.favoriteServe.refreshSnapshot(live,
                                    live.getComments(), liveLikes);
                            moments.add(MessageTransitionServe.dealMsgTypeTransition(live));
                            if (liveLikes != null && !liveLikes.isEmpty()) {
                                praiseRecords.put(favorite.getMomentId(), liveLikes);
                            }
                            continue;
                        }
                        // 动态已被删除：只能用快照渲染，并标记为删除态。
                        if (snapshot == null || snapshot.getMoment() == null) {
                            LogUtil.w(TAG, "loadFavorites: snapshot invalid, momentId = "
                                    + favorite.getMomentId());
                            continue;
                        }
                        DbMoment deleted = snapshot.getMoment();
                        normalizeSnapshotMoment(deleted);
                        List<DbMomentComment> snapshotComments = snapshot.getComments();
                        deleted.setComments(snapshotComments);
                        // 已删除的动态无法再「展开」翻页，把总数对齐到快照条数，避免出现点了没反应的入口。
                        deleted.setCommentsTotalCount(
                                snapshotComments == null ? 0 : snapshotComments.size());
                        deletedIds.add(favorite.getMomentId());
                        moments.add(MessageTransitionServe.dealMsgTypeTransition(deleted));
                        List<DbLikeMessage> snapshotLikes = snapshot.getLikes();
                        if (snapshotLikes != null && !snapshotLikes.isEmpty()) {
                            praiseRecords.put(favorite.getMomentId(), snapshotLikes);
                        }
                    }
                }
                LogUtil.i(TAG, "loadFavorites: size = " + moments.size() + ", deleted = "
                        + deletedIds.size());
                // 点赞头像要走网络缓存，放到主线程（列表渲染依赖它）。
                HandlerUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        loadLikeDrawable(praiseRecords);
                        if (!FavoriteMomentPresenter.this.isViewAttached()
                                || FavoriteMomentPresenter.this.getView() == null) {
                            return;
                        }
                        FavoriteMomentPresenter.this.getView().onFavoritesLoaded(moments, praiseRecords,
                                deletedIds);
                    }
                });
            }
        });
    }

    /** 读取本地仍存在的动态（含最近评论）；已被删除时返回 null。 */
    private DbMoment loadLiveMoment(String momentId) {
        List<DbMoment> moments = this.iMomentServe.getMomentById(momentId);
        if (moments == null || moments.isEmpty()) {
            return null;
        }
        DbMoment moment = moments.get(0);
        if (moment == null || moment.getType() == null || moment.getCreateTime() == null) {
            // 缺关键字段的动态渲染会崩，按已删除处理，退回快照。
            LogUtil.w(TAG, "loadLiveMoment: unusable moment, momentId = " + momentId);
            return null;
        }
        return moment;
    }

    /**
     * 快照数据的脏数据防御：列表渲染依赖 type / createTime / 评论列表非空，
     * 老版本写下的快照或异常数据在这里补齐默认值，避免绑定卡片时崩溃。
     */
    private void normalizeSnapshotMoment(DbMoment moment) {
        if (moment.getType() == null) {
            moment.setType(Integer.valueOf(0));
        }
        if (moment.getCreateTime() == null) {
            moment.setCreateTime(Long.valueOf(0L));
        }
        if (moment.getLikeTotal() == null) {
            moment.setLikeTotal(Integer.valueOf(0));
        }
    }

    /** 预加载「装扮点赞」的头像资源，供列表侧同步取用。 */
    private void loadLikeDrawable(Map<String, List<DbLikeMessage>> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        String selfWatchId = MomentApp.getWatchId();
        for (List<DbLikeMessage> likeMessages : records.values()) {
            for (DbLikeMessage likeMessage : likeMessages) {
                if (likeMessage.getEmotionId() == 0 || TextUtils.isEmpty(selfWatchId)) {
                    continue;
                }
                DbMomentPrerogativeLike prerogativeLike = MomentPrerogativeServeImpl
                        .getInstance(this.mContext).getPrerogativeLikeByEmotionId(likeMessage.getEmotionId());
                if (prerogativeLike == null) {
                    continue;
                }
                boolean isSelf = selfWatchId.equals(likeMessage.getWatchId());
                String likedPic = isSelf && IllegalMessageHandler.getInstance(this.mContext).isDisableSend()
                        ? prerogativeLike.getPraisedDisablePic()
                        : prerogativeLike.getPraisedPic();
                this.likeDrawableCache.loadImageFromNetToCache(likedPic);
                likeMessage.setLikedPic(likedPic);
            }
        }
    }

    /** 读取某条动态的最新本地数据（点赞回调后刷新卡片用）。 */
    public List<DbMoment> getMomentById(String momentId) {
        return this.iMomentServe.getMomentById(momentId);
    }

    /** 读取某条动态的本地点赞记录，并预加载点赞头像。 */
    public List<DbLikeMessage> getLikeMessageByMomentId(String momentId) {
        List<DbLikeMessage> likeMessages = this.iMomentServe.getLikeMessageByMomentId(momentId);
        if (likeMessages == null || likeMessages.isEmpty()) {
            return new ArrayList<>();
        }
        Map<String, List<DbLikeMessage>> records = new HashMap<>();
        records.put(momentId, likeMessages);
        loadLikeDrawable(records);
        return likeMessages;
    }

    @Override
    public String getLogTag() {
        return TAG;
    }
}
