package com.xtc.moment.data.local;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.moment.LogTag;
import com.xtc.moment.db.bean.DbAdvertCloseRecord;
import com.xtc.moment.db.bean.DbGiftRecord;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.db.dao.CloseAdvertDao;
import com.xtc.moment.db.dao.GiftDao;
import com.xtc.moment.db.dao.LikeMessageDao;
import com.xtc.moment.db.dao.MomentCommentDao;
import com.xtc.moment.db.dao.MomentDao;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.serve.IAccountInfoServe;
import com.xtc.moment.serve.ServerCache;
import com.xtc.utils.common.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态本地数据源：统一封装各张表的 DAO，供仓库层调用。
 */
public class MomentsLocalDataSource {

    private static volatile MomentsLocalDataSource INSTANCE;
    private static final String TAG = LogTag.tag("MomentsLocalDataSource");

    private CloseAdvertDao closeAdvertDao;
    private GiftDao giftDao;
    private IAccountInfoServe iAccountInfoServe;
    private LikeMessageDao likeMessageDao;
    private Context mContext;
    private MomentCommentDao momentCommentDao;
    private MomentDao momentDao;

    private MomentsLocalDataSource(Context context) {
        this.mContext = context;
        this.momentDao = ServerCache.getDao(context, MomentDao.class);
        this.likeMessageDao = ServerCache.getDao(context, LikeMessageDao.class);
        this.momentCommentDao = ServerCache.getDao(context, MomentCommentDao.class);
        this.giftDao = ServerCache.getDao(context, GiftDao.class);
        this.closeAdvertDao = ServerCache.getDao(context, CloseAdvertDao.class);
        this.iAccountInfoServe = AccountInfoServerImpl.getInstance(context);
    }

    public static MomentsLocalDataSource getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (MomentsLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MomentsLocalDataSource(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public boolean isLiked(DbMoment moment) {
        List<DbLikeMessage> likeMessages = getLikeMessageByMomentIdAndWatchId(moment.getMomentId(), this.iAccountInfoServe.getWatchAccountInfo().getWatchId(this.mContext));
        return !CollectionUtil.isEmpty(likeMessages) && likeMessages.size() > 0;
    }

    public List<DbLikeMessage> getLikeMessageByMomentIdAndWatchId(String momentId, String watchId) {
        if (TextUtils.isEmpty(momentId) || TextUtils.isEmpty(watchId)) {
            return new ArrayList<>(0);
        }
        return this.likeMessageDao.getByMomentIdAndWatchId(momentId, watchId);
    }

    public void insertMomentByMomentId(DbMoment moment) {
        List<DbMoment> localMoments = getMomentById(moment.getMomentId());
        if (localMoments == null || localMoments.isEmpty()) {
            this.momentDao.insert(moment);
            LogUtil.i("MomentServeImpl", "insertMomentByMomentId insert moment:" + moment);
            return;
        }
        this.momentDao.updateMomentByMomentId(moment);
        LogUtil.i("MomentServeImpl", "insertMomentByMomentId update moment:" + moment);
    }

    public List<DbMoment> getMomentById(String momentId) {
        return this.momentDao.queryByMomentId(momentId);
    }

    /**
     * 给动态补上本地评论，并过滤掉已删除的评论。
     */
    private List<DbMoment> addCommentForMoment(List<DbMoment> moments) {
        if (CollectionUtil.isEmpty(moments)) {
            LogUtil.d(TAG, "moments is empty");
            return null;
        }
        ArrayList<DbMoment> result = new ArrayList<>(moments);
        for (DbMoment moment : result) {
            List<DbMomentComment> comments = this.momentCommentDao.queryCommentByMomentId(moment.getMomentId());
            if (comments != null && comments.size() > 0) {
                ArrayList<DbMomentComment> deleted = new ArrayList<>();
                for (int i = 0; i < comments.size(); i++) {
                    if (comments.get(i).isDeleted()) {
                        deleted.add(comments.get(i));
                    }
                }
                comments.removeAll(deleted);
                moment.setComments(comments);
            }
        }
        return result;
    }

    public void addComment(DbMomentComment comment) {
        this.momentCommentDao.addComment(comment);
    }

    public boolean deleteMomentByMomentId(String watchId, String momentId) {
        return this.momentDao.deleteMomentByMomentId(watchId, momentId);
    }

    public boolean deleteLikeDaoByMomentId(String momentId) {
        return this.likeMessageDao.deleteByMomentId(momentId);
    }

    public boolean deleteCommentDaoByMomentId(String momentId) {
        return this.momentCommentDao.deleteCommentByMomentId(momentId);
    }

    public List<DbMoment> getLocalAdvertiseList(Context context) {
        return this.momentDao.getLocalAdvertiseList(context);
    }

    public void addAdvertCloseRecord(DbAdvertCloseRecord record) {
        this.closeAdvertDao.addCloseAdvertRecord(record);
    }

    public DbAdvertCloseRecord getByAdvertId(String advertId) {
        return this.closeAdvertDao.getByAdvertId(advertId);
    }

    public int deleteAll() {
        return this.closeAdvertDao.deleteAll();
    }

    public boolean addGiftRecordMessage(DbGiftRecord record) {
        return this.giftDao.addGiftRecordMessage(record);
    }

    public DbGiftRecord queryGiftByMomentId(String momentId) {
        List<DbGiftRecord> records = this.giftDao.getByMomentId(momentId);
        if (records == null || records.size() <= 0) {
            return null;
        }
        return records.get(0);
    }

    public boolean updateGift(DbGiftRecord record) {
        return this.giftDao.updateGiftMessage(record);
    }
}