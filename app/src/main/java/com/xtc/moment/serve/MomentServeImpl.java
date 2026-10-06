package com.xtc.moment.serve;

import android.content.Context;
import android.text.TextUtils;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.xtc.architecture.mvp.BaseServe;
import com.xtc.httplib.net.HttpRxJavaCallback;
import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.db.bean.DbIMReminder;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.db.bean.DbReminder;
import com.xtc.moment.db.bean.DbSexyPhotoDistinguish;
import com.xtc.moment.db.bean.DbVisible;
import com.xtc.moment.db.dao.FriendVisibleDao;
import com.xtc.moment.db.dao.LikeMessageDao;
import com.xtc.moment.db.dao.MomentCommentDao;
import com.xtc.moment.db.dao.MomentDao;
import com.xtc.moment.db.dao.MomentIMReminderDao;
import com.xtc.moment.db.dao.MomentReminderDao;
import com.xtc.moment.db.dao.NetInvalidMomentDao;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.bean.FriendsVisibleBean;
import com.xtc.moment.module.bean.PoiBean;
import com.xtc.moment.module.bean.ShareVideoMoment;
import com.xtc.moment.module.bean.VideoMsg;
import com.xtc.moment.module.bean.VideoTokenParam;
import com.xtc.moment.module.report.bean.ReportDataBean;
import com.xtc.moment.module.report.bean.ReportInformParam;
import com.xtc.moment.module.report.bean.StartReportRequest;
import com.xtc.moment.net.MomentHttpServiceProxy;
import com.xtc.moment.net.bean.BanStateBean;
import com.xtc.moment.net.bean.CommentOfficialResultBean;
import com.xtc.moment.net.bean.CommentResultBean;
import com.xtc.moment.net.bean.DefaultResponse;
import com.xtc.moment.net.bean.DeleteResultBean;
import com.xtc.moment.net.bean.Moment;
import com.xtc.moment.net.bean.Moments;
import com.xtc.moment.net.bean.NormalResultBean;
import com.xtc.moment.net.bean.OfficialCommentResultBean;
import com.xtc.moment.net.bean.PraiseResponse;
import com.xtc.moment.net.bean.VideoTokenVoResponse;
import com.xtc.moment.util.EventData;
import com.xtc.moment.util.LongLog;
import com.xtc.moment.util.Utils;
import com.xtc.system.account.Device;
import com.xtc.system.account.WatchDevice;
import com.xtc.utils.common.CollectionUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.storage.FileUtils;

import org.greenrobot.eventbus.EventBus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 好友圈业务服务实现：动态、评论、点赞、提醒与可见范围的全部读写入口。
 */
public class MomentServeImpl extends BaseServe implements IMomentServe {

    private static final int CREATE_WATCH_TIME = 0;
    private static final int NORMAL_OFFSET = 0;
    private static final String TAG = Constants.MOMENT_TAG + MomentServeImpl.class.getSimpleName();

    private Context context;
    private FriendVisibleDao friendVisibleDao;
    private IAccountInfoServe iAccountInfoServe;
    private LikeMessageDao likeMessageDao;
    private Device mDevice;
    private int mTopMomentCount;
    private MomentCommentDao momentCommentDao;
    private MomentDao momentDao;
    private MomentHttpServiceProxy momentHttpServiceProxy;
    private MomentIMReminderDao momentIMReminderDao;
    private MomentReminderDao momentReminderDao;
    private NetInvalidMomentDao netInvalidMomentDao;

    @Override
    public void changeReportInfoData(String data) {
    }

    private MomentServeImpl(Context context) {
        super(context);
        this.mTopMomentCount = 0;
        this.mDevice = new WatchDevice(context);
        this.context = context;
        this.momentDao = ServerCache.getDao(context, MomentDao.class);
        this.likeMessageDao = ServerCache.getDao(context, LikeMessageDao.class);
        this.momentCommentDao = ServerCache.getDao(context, MomentCommentDao.class);
        this.momentReminderDao = ServerCache.getDao(context, MomentReminderDao.class);
        this.momentIMReminderDao = ServerCache.getDao(context, MomentIMReminderDao.class);
        this.friendVisibleDao = ServerCache.getDao(context, FriendVisibleDao.class);
        this.momentHttpServiceProxy = ServerCache.getHttpService(context, MomentHttpServiceProxy.class);
        this.netInvalidMomentDao = ServerCache.getDao(context, NetInvalidMomentDao.class);
        this.iAccountInfoServe = AccountInfoServerImpl.getInstance(context);
        ServerCache.putBusinessServer(this);
    }

    public static IMomentServe getInstance(Context context) {
        return ServerCache.getBusinessServer(context, MomentServeImpl.class);
    }

    @Override
    public Observable<List<DbMoment>> getMomentsFromNet(long maxTime, int pageSize, String watchId, long minTime) {
        return this.momentHttpServiceProxy.searchMoment(0L, minTime - 1, pageSize, 0L, maxTime, System.currentTimeMillis(), watchId, AccountInfoServerImpl.getInstance(this.mContext).getWatchAccountInfo().getWatchId(this.mContext))
                .throttleFirst(1000L, TimeUnit.MILLISECONDS)
                .map(new Func1<Moments, List<DbMoment>>() {
                    @Override
                    public List<DbMoment> call(Moments moments) {
                        LongLog.i(MomentServeImpl.TAG, "getMomentsFromNet:" + moments);
                        List<DbMoment> watchMoments = moments.getWatchMoments();
                        if (watchMoments == null) {
                            return null;
                        }
                        for (DbMoment moment : watchMoments) {
                            moment.setMomentLbs(moment.getMomentLbs());
                        }
                        return watchMoments;
                    }
                });
    }

    @Override
    public Observable<List<OfficialCommentResultBean>> getOfficialCommentFromNet(String lookUpId, List<String> advertIdList) {
        return this.momentHttpServiceProxy.searchOfficialComment(lookUpId, advertIdList);
    }

    @Override
    public Observable<List<DbMoment>> getMomentsFromDb(final long offset, final long limit, final int type, final String watchId) {
        return Observable.create(new Observable.OnSubscribe<List<DbMoment>>() {
            @Override
            public void call(Subscriber<? super List<DbMoment>> subscriber) {
                subscriber.onNext(MomentServeImpl.this.getMomentsFromDbSync(offset, limit, type, watchId));
                subscriber.onCompleted();
            }
        }).throttleFirst(1000L, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io());
    }

    @Override
    public List<DbMoment> getMomentsFromDbSync(long offset, long limit, int type, String watchId) {
        List<DbMoment> moments;
        int topCount = this.mTopMomentCount;
        long realOffset = offset - ((long) topCount) > 0 ? offset - ((long) topCount) : offset;
        if (type > 0) {
            moments = this.momentDao.queryMoments(Long.valueOf(realOffset), Long.valueOf(limit));
        } else {
            moments = this.momentDao.queryPersonalMoments(realOffset, limit, watchId);
        }
        if (type > 0 && realOffset == 0) {
            List<DbMoment> topMoments = this.momentDao.queryTopMoments();
            this.mTopMomentCount = topMoments != null ? topMoments.size() : 0;
            if (this.mTopMomentCount > 0) {
                moments.addAll(0, topMoments);
            }
        }
        LogUtil.i(TAG, "getMomentsFromDbSync offset:" + realOffset + ",size:" + (moments != null ? moments.size() : 0));
        return addCommentForMoment(moments);
    }

    /**
     * 给动态补上最近 5 条本地评论，并过滤掉已删除的评论。
     */
    private List<DbMoment> addCommentForMoment(List<DbMoment> moments) {
        if (moments == null || moments.size() <= 0) {
            LogUtil.d(TAG, "moments == null || moments.size() <= 0");
            return null;
        }
        ArrayList<DbMoment> result = new ArrayList<>(moments);
        if (result.size() > 0) {
            for (DbMoment moment : result) {
                List<DbMomentComment> comments = this.momentCommentDao.queryCommentPageByMomentId(0L, 5L, moment.getMomentId());
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
        }
        return result;
    }

    @Override
    public long getMomentCountInDb() {
        return this.momentDao.getCount();
    }

    @Override
    public Observable<Moment> publishMoment(int type, String content, int resourceId, String resource, FriendsVisibleBean friendsVisibleBean) {
        return this.momentHttpServiceProxy.publishMoment(this.iAccountInfoServe.getWatchAccountInfo().getWatchId(this.mContext), resource, resourceId, content, type, null, friendsVisibleBean).throttleFirst(1000L, TimeUnit.MILLISECONDS);
    }

    @Override
    public Observable<Moment> publishMoment(int type, String content, int resourceId, String resource, PoiBean poiBean, FriendsVisibleBean friendsVisibleBean) {
        return this.momentHttpServiceProxy.publishMoment(this.iAccountInfoServe.getWatchAccountInfo().getWatchId(this.mContext), resource, resourceId, content, type, null, poiBean, friendsVisibleBean).throttleFirst(1000L, TimeUnit.MILLISECONDS);
    }

    @Override
    public Observable<Moment> publishMoment(int type, String content, int resourceId, String resource, List<Integer> visibleTypes) {
        return this.momentHttpServiceProxy.publishMoment(this.iAccountInfoServe.getWatchAccountInfo().getWatchId(this.mContext), resource, resourceId, content, type, null, visibleTypes).throttleFirst(1000L, TimeUnit.MILLISECONDS);
    }

    @Override
    public Observable<Moment> publishMoment(int type, String content, int resourceId, String resource, String packageName, PoiBean poiBean, FriendsVisibleBean friendsVisibleBean) {
        return this.momentHttpServiceProxy.publishMoment(this.iAccountInfoServe.getWatchAccountInfo().getWatchId(this.mContext), resource, resourceId, content, type, packageName, poiBean, friendsVisibleBean).throttleFirst(1000L, TimeUnit.MILLISECONDS);
    }

    @Override
    public Observable<Moment> publishMoment(int type, String content, int resourceId, String resource, String packageName, List<Integer> visibleTypes, PoiBean poiBean) {
        return this.momentHttpServiceProxy.publishMoment(this.iAccountInfoServe.getWatchAccountInfo().getWatchId(this.mContext), resource, resourceId, content, type, packageName, visibleTypes, poiBean).throttleFirst(1000L, TimeUnit.MILLISECONDS);
    }
    @Override
    public Observable<DefaultResponse> praiseMoment(String momentId, String momentWatchId) {
        return this.momentHttpServiceProxy.praiseMoment(momentId, momentWatchId, this.iAccountInfoServe.getWatchAccountInfo().getWatchId(this.mContext)).throttleFirst(1000L, TimeUnit.MILLISECONDS);
    }

    @Override
    public Observable<String> cancelPraiseMoment(String momentId, String momentWatchId) {
        return this.momentHttpServiceProxy.cancelPraiseMoment(momentId, momentWatchId, this.iAccountInfoServe.getWatchAccountInfo().getWatchId(this.mContext)).throttleFirst(1000L, TimeUnit.MICROSECONDS);
    }

    @Override
    public Observable<DefaultResponse> praiseAdvertise(String momentId, String momentWatchId) {
        return this.momentHttpServiceProxy.praiseAdvertise(momentId, momentWatchId, this.iAccountInfoServe.getWatchAccountInfo().getWatchId(this.mContext)).throttleFirst(1000L, TimeUnit.MILLISECONDS);
    }

    @Override
    public Observable<CommentResultBean> commentMoment(DbMomentComment comment) {
        comment.setWatchId(this.iAccountInfoServe.getWatchAccountInfo().getWatchId(this.mContext));
        return this.momentHttpServiceProxy.commentMoment(comment).throttleFirst(1000L, TimeUnit.MILLISECONDS);
    }

    @Override
    public Observable<CommentOfficialResultBean> commentAdvertMoment(DbMomentComment comment) {
        comment.setWatchId(this.iAccountInfoServe.getWatchAccountInfo().getWatchId(this.mContext));
        return this.momentHttpServiceProxy.commentAdvertMoment(comment).throttleFirst(1000L, TimeUnit.MILLISECONDS);
    }

    @Override
    public Observable<PraiseResponse> getPraiseRecord(List<String> momentIds, String watchId) {
        return this.momentHttpServiceProxy.getPraiseRecord(momentIds, watchId).throttleFirst(1000L, TimeUnit.MILLISECONDS);
    }

    @Override
    public void insertMomentByMomentId(DbMoment moment) {
        String momentId = moment.getMomentId();
        List<DbMoment> localMoments = getMomentByIdNoNeedComment(moment.getMomentId());
        if (localMoments == null || localMoments.isEmpty()) {
            this.momentDao.insert(moment);
            LogUtil.i(TAG, "insertMomentByMomentId insert momentId:" + momentId);
            return;
        }
        this.momentDao.updateMomentByMomentId(moment);
        LogUtil.i(TAG, "insertMomentByMomentId update momentId:" + momentId);
    }

    @Override
    public void updateMomentByMomentId(String momentId) {
        List<DbMoment> moments = getMomentById(momentId);
        if (moments == null || moments.isEmpty()) {
            LogUtil.w(TAG, "updateMomentByMomentId failed:no find moment that id is " + momentId);
            return;
        }
        if (moments.size() > 1) {
            LogUtil.w(TAG, "updateMomentByMomentId:there are more than one dbmoment record in db,count:" + moments.size());
        }
        DbMoment moment = moments.get(0);
        if (moment != null) {
            moment.setLikeTotal(Integer.valueOf(moment.getLikeTotal().intValue() + 1));
            moment.setEnableLike(!moment.isEnableLike());
            boolean updated = this.momentDao.update(moment);
            LogUtil.i(TAG, "like official " + updated);
            this.momentDao.updateBy(moment, "momentId", moment.getMomentId());
        }
    }

    @Override
    public DbMoment deleteLikeDaoByMomentId(String momentId, String momentWatchId) {
        List<DbMoment> moments = getMomentById(momentId);
        if (moments == null || moments.isEmpty()) {
            LogUtil.w(TAG, "updateMomentByMomentId failed:no find moment that id is " + momentId);
            return null;
        }
        if (moments.size() > 1) {
            LogUtil.w(TAG, "updateMomentByMomentId:there are more than one dbmoment record in db,count:" + moments.size());
        }
        DbMoment moment = moments.get(0);
        if (moment != null) {
            moment.setLikeTotal(Integer.valueOf(moment.getLikeTotal().intValue() - 1));
            moment.setEnableLike(true);
            updateMoment(moment);
        }
        boolean deleted = this.likeMessageDao.deleteByColumnName("momentWatchId", momentWatchId);
        LogUtil.i(TAG, "delete like in dao " + deleted);
        return moment;
    }

    @Override
    public boolean updateMoment(DbMoment moment) {
        if (moment == null) {
            LogUtil.w(TAG, "updateMoment failed: moment is null");
            return false;
        }
        return this.momentDao.updateMomentByMomentId(moment);
    }

    @Override
    public void updateMomentsByMomentId(List<DbMoment> moments) {
        if (moments == null || moments.isEmpty()) {
            return;
        }
        Utils.logSize(TAG, "updateMomentsByMomentId,list ", moments);
        this.momentDao.updateMomentsByMomentId(moments);
    }

    @Override
    public void addMoments(List<DbMoment> moments) {
        if (moments == null || moments.isEmpty()) {
            return;
        }
        this.momentDao.addMoments(moments);
    }

    @Override
    public boolean deleteDBMomentByMomentId(DbMoment moment) {
        if (moment == null) {
            return false;
        }
        String momentId = moment.getMomentId();
        boolean commentDeleted = this.momentCommentDao.deleteCommentByMomentId(momentId);
        boolean likeDeleted = this.likeMessageDao.deleteByMomentId(momentId);
        LogUtil.w(TAG, "deleteDBMomentByMomentId:deleteCommentByMomentId = " + commentDeleted + "; deleteByMomentId = " + likeDeleted);
        boolean deleted = this.momentDao.deleteByColumnName("momentId", momentId);
        if (deleted) {
            EventBus.getDefault().post(new EventData(15, moment));
        }
        deleteVideoThumbnailPath(moment);
        return deleted;
    }

    /**
     * 删除自己发布的视频动态时，一并删除本地缩略图。
     */
    private void deleteVideoThumbnailPath(DbMoment moment) {
        if (moment.getWatchId().equals(this.iAccountInfoServe.getWatchAccountInfo().getWatchId(this.mContext))) {
            String content = moment.getContent();
            int type = moment.getType().intValue();
            if (type == 6) {
                VideoMsg videoMsg = JSONUtil.fromJSON(content, VideoMsg.class);
                if (videoMsg == null || videoMsg.getLocalThumbnailPath() == null) {
                    return;
                }
                boolean deleted = FileUtils.deleteFile(videoMsg.getLocalThumbnailPath());
                LogUtil.d(TAG, "deleteVideoThumbnailPath: " + deleted);
                return;
            }
            if (type == 24) {
                ShareVideoMoment shareVideoMoment = JSONUtil.fromJSON(content, ShareVideoMoment.class);
                if (shareVideoMoment == null || shareVideoMoment.getLocalThumbnailPath() == null) {
                    return;
                }
                boolean deleted = FileUtils.deleteFile(shareVideoMoment.getLocalThumbnailPath());
                LogUtil.d(TAG, "deleteVideoThumbnailPath: " + deleted);
            }
        }
    }

    @Override
    public boolean deleteDBMomentByMomentIdAndWatchId(String momentId, String watchId) {
        HashMap<String, Object> conditions = new HashMap<>(2);
        conditions.put("momentId", momentId);
        conditions.put("watchId", watchId);
        return this.likeMessageDao.deleteByColumnName(conditions);
    }

    @Override
    public Observable<DeleteResultBean> deleteServerMomentByKey(String key, DbMoment moment) {
        return this.momentHttpServiceProxy.deleteMoment(key, moment);
    }

    @Override
    public Observable<NormalResultBean> deleteServerComment(DbMomentComment comment) {
        return this.momentHttpServiceProxy.deleteServerComment(comment);
    }

    @Override
    public boolean increaseLikeTotal(String momentId) {
        List<DbMoment> moments = getMomentById(momentId);
        if (moments == null || moments.isEmpty()) {
            LogUtil.w(TAG, "increaseLikeTotal failed:no find moment that id is " + momentId);
            return false;
        }
        if (moments.size() > 1) {
            LogUtil.w(TAG, "increaseLikeTotal:there are more than one dbmoment record in db,count:" + moments.size());
        }
        DbMoment moment = moments.get(0);
        if (moment == null) {
            return false;
        }
        moment.setLikeTotal(Integer.valueOf(moment.getLikeTotal().intValue() + 1));
        return this.momentDao.updateBy(moment, "momentId", moment.getMomentId());
    }

    @Override
    public boolean isLiked(DbMoment moment) {
        List<DbLikeMessage> likeMessages = getInstance(this.mContext).getLikeMessageByMomentIdAndWatchId(moment.getMomentId(), AccountInfoServerImpl.getInstance(this.mContext).getWatchAccountInfo().getWatchId(this.mContext));
        return likeMessages != null && likeMessages.size() > 0;
    }

    private List<DbMoment> getMomentByIdNoNeedComment(String momentId) {
        return this.momentDao.queryByMomentId(momentId);
    }

    @Override
    public List<DbMoment> getMomentById(String momentId) {
        return addCommentForMoment(this.momentDao.queryByMomentId(momentId));
    }

    @Override
    public DbMoment getMomentByMomentIdWithoutComment(String momentId) {
        if (TextUtils.isEmpty(momentId)) {
            return null;
        }
        List<DbMoment> moments = this.momentDao.queryByMomentId(momentId);
        if (moments == null || moments.size() == 0) {
            return null;
        }
        return moments.get(0);
    }

    @Override
    public List<DbMoment> getMomentByWatchId(String watchId) {
        if (TextUtils.isEmpty(watchId)) {
            LogUtil.w("getMomentByWatchId watchId is null");
            return null;
        }
        return this.momentDao.queryByWatchId(watchId);
    }

    @Override
    public List<DbMoment> queryByMomentIds(Set<String> momentIds) {
        if (momentIds == null || momentIds.size() <= 0) {
            return null;
        }
        return this.momentDao.queryByMomentIds(momentIds);
    }

    @Override
    public List<DbMoment> queryByLimitTime(String watchId, long time) {
        if (TextUtils.isEmpty(watchId)) {
            LogUtil.i(TAG, "queryByMomentCreatTime, watchId is null");
            return null;
        }
        return this.momentDao.queryByLimitTime(watchId, time);
    }

    @Override
    public boolean deleteMomentByWatchId(String watchId) {
        if (TextUtils.isEmpty(watchId)) {
            LogUtil.w(TAG, "deleteMomentByWatchId watchId is null");
            return false;
        }
        boolean momentDeleted = this.momentDao.deleteMomentByWatchId(watchId);
        List<DbMomentComment> comments = this.momentCommentDao.queryCommentByWatchId(watchId);
        boolean commentDeleted = false;
        if (comments != null && comments.size() > 0) {
            commentDeleted = this.momentCommentDao.deleteCommentByWatchId(comments.get(0).getWatchId());
            LogUtil.d(TAG, "deleteCommentByWatchId result = " + commentDeleted + watchId);
        }
        boolean replyDeleted = this.momentCommentDao.deleteCommentByReplyId(watchId);
        LogUtil.d(TAG, "deleteCommentByReplyId result = " + replyDeleted + " " + watchId);
        LogUtil.d(TAG, "deleteMomentByWatchId result = " + momentDeleted + comments);
        return momentDeleted || commentDeleted || replyDeleted;
    }
    @Override
    public List<DbLikeMessage> loadAllUncheckedLikeMessageByWatchId(String watchId) {
        if (TextUtils.isEmpty(watchId)) {
            return null;
        }
        return this.likeMessageDao.loadAllUncheckedLikeMessageByWatchId(watchId);
    }

    @Override
    public long getUncheckedLikeMessageCountByWatchId(String watchId) {
        return this.likeMessageDao.getUncheckedLikeMessageCountByWatchId(watchId);
    }

    @Override
    public List<DbMomentComment> loadAllUncheckedCommentByWatchId(String watchId) {
        return this.momentCommentDao.loadAllUncheckedCommentByWatchId(watchId);
    }

    @Override
    public long getUncheckedCommentCountByWatchId(String watchId) {
        return this.momentCommentDao.getUncheckedCommentCountByWatchId(watchId);
    }

    @Override
    public List<DbLikeMessage> getLikeMessageByMomentId(String momentId) {
        if (TextUtils.isEmpty(momentId)) {
            return null;
        }
        return this.likeMessageDao.getByMomentId(momentId);
    }

    @Override
    public List<DbLikeMessage> getLikeMessageByMomentIdAndWatchId(String momentId, String watchId) {
        if (TextUtils.isEmpty(momentId) || TextUtils.isEmpty(watchId)) {
            return null;
        }
        return this.likeMessageDao.getByMomentIdAndWatchId(momentId, watchId);
    }

    @Override
    public boolean addLikeMessage(DbLikeMessage likeMessage) {
        if (likeMessage == null) {
            return false;
        }
        LogUtil.i(TAG, "addLikeMessage:" + likeMessage);
        List<DbLikeMessage> localMessages = getInstance(this.mContext).getLikeMessageByMomentIdAndWatchId(likeMessage.getMomentId(), likeMessage.getWatchId());
        if (localMessages != null && localMessages.size() > 0) {
            LogUtil.d(TAG, "数据库已经有点赞数据，不再插入数据库");
            return false;
        }
        return this.likeMessageDao.insert(likeMessage);
    }

    @Override
    public void addLikeMessage(List<DbLikeMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }
        this.likeMessageDao.create(messages);
    }

    @Override
    public void updateLikeMessage(List<DbLikeMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }
        this.likeMessageDao.update(messages);
    }

    @Override
    public boolean deleteLikeMessageByMomentId(String momentId) {
        if (TextUtils.isEmpty(momentId)) {
            LogUtil.w(TAG, "deleteLikeMessageByMomentId: momentId is empty");
            return false;
        }
        return this.likeMessageDao.deleteByMomentId(momentId);
    }

    @Override
    public void updateMomentComment(List<DbMomentComment> comments) {
        if (comments == null || comments.isEmpty()) {
            return;
        }
        this.momentCommentDao.update(comments);
    }

    @Override
    public void updateMomentComment(DbMomentComment comment) {
        if (comment == null) {
            return;
        }
        if (this.momentCommentDao.update(comment)) {
            LogUtil.i(TAG, "update dbMomentComment successfully");
        } else {
            LogUtil.i(TAG, "update dbMomentComment failed");
        }
    }

    @Override
    public void addCommentMoment(DbMomentComment comment) {
        if (comment == null) {
            return;
        }
        this.momentCommentDao.addComment(comment);
    }

    @Override
    public void addCommentMoment(List<DbMomentComment> comments) {
        if (comments == null || comments.size() <= 0) {
            return;
        }
        LogUtil.i(TAG, "addCommentMoment " + (this.momentCommentDao == null));
        this.momentCommentDao.addComments(comments);
    }

    @Override
    public List<DbMoment> getOthersUncheckedMoment(String watchIds) {
        if (TextUtils.isEmpty(watchIds)) {
            return null;
        }
        return this.momentDao.getOthersUncheckedMoment(watchIds);
    }

    @Override
    public long getOthersUncheckedMomentCount(String watchIds) {
        return this.momentDao.getOthersUncheckedMomentCount(watchIds);
    }

    @Override
    public void updateLikeMessageName(String watchId, String watchName) {
        if (TextUtils.isEmpty(watchId) || TextUtils.isEmpty(watchName)) {
            return;
        }
        this.likeMessageDao.updateName(watchId, watchName);
    }

    @Override
    public long getAllMoment() {
        return this.momentDao.getCount();
    }

    @Override
    public List<DbMoment> queryMessageForPages(long offset, long limit, boolean ascending) {
        return this.momentDao.queryForPagesByOrder("id", ascending, Long.valueOf(offset), Long.valueOf(limit));
    }

    @Override
    public boolean addSexyPhotoRecord(List<DbSexyPhotoDistinguish> records) {
        if (records == null || records.isEmpty()) {
            return false;
        }
        return this.netInvalidMomentDao.addMomentBatch(records);
    }

    @Override
    public List<DbMomentComment> queryCommentByMomentIdAndCommentId(String momentId, String commentId) {
        if (TextUtils.isEmpty(momentId) || TextUtils.isEmpty(commentId)) {
            return new ArrayList<>(0);
        }
        return this.momentCommentDao.queryCommentByMomentIdAndCommentId(momentId, commentId);
    }

    @Override
    public List<DbMomentComment> queryCommentByCommentId(String commentId) {
        if (TextUtils.isEmpty(commentId)) {
            return new ArrayList<>(0);
        }
        return this.momentCommentDao.queryCommentByCommentId(commentId);
    }

    @Override
    public void deleteDBCommentByMomentIdAndCommentId(String momentId, String commentId) {
        if (TextUtils.isEmpty(momentId) || TextUtils.isEmpty(commentId)) {
            return;
        }
        this.momentCommentDao.deleteDBCommentByMomentIdAndCommentId(momentId, commentId);
    }

    @Override
    public Observable<BanStateBean> updateBanState() {
        return this.momentHttpServiceProxy.updateBanState(this.mDevice.getBindNumber()).throttleFirst(1000L, TimeUnit.MILLISECONDS);
    }

    @Override
    public void deleteDbCommentForBatch(List<DbMomentComment> comments) {
        if (comments == null || comments.isEmpty()) {
            return;
        }
        this.momentCommentDao.deleteDbCommentForBatch(comments);
    }

    @Override
    public boolean deleteDbMomentForBatch(List<DbMoment> moments) {
        if (CollectionUtil.isEmpty(moments)) {
            return false;
        }
        return this.momentDao.deleteForBatch(moments);
    }

    @Override
    public Observable<String> launchedReport(StartReportRequest request) {
        return this.momentHttpServiceProxy.launchedReport(request).map(new HttpRxJavaCallback<String>());
    }

    @Override
    public Observable<VideoTokenVoResponse> getUploadVideoToken(VideoTokenParam param) {
        param.setWatchId(MomentApp.getWatchId());
        return this.momentHttpServiceProxy.getUploadVideoToken(param);
    }

    @Override
    public void clearLikeDbData() {
        boolean deleted = this.likeMessageDao.deleteChecked();
        LogUtil.i(TAG, "clear like dao " + deleted);
    }

    @Override
    public Observable<ReportDataBean> queryReportInform(ReportInformParam param) {
        return this.momentHttpServiceProxy.queryReportInform(param).map(new HttpRxJavaCallback<ReportDataBean>());
    }

    @Override
    public List<DbVisible> getVisibleFriendsFromDb(String momentId) {
        HashMap<String, Object> conditions = new HashMap<>();
        conditions.put("momentId", momentId);
        return this.friendVisibleDao.queryByColumnName(conditions);
    }

    @Override
    public boolean insertVisibleFriendsFromDb(List<DbVisible> visibles) {
        return this.friendVisibleDao.insertForBatch(visibles);
    }

    @Override
    public boolean deleteAllFriendsVisbleRecord(String momentId) {
        HashMap<String, Object> conditions = new HashMap<>();
        conditions.put("momentId", momentId);
        return this.friendVisibleDao.deleteByColumnName(conditions);
    }

    @Override
    public void saveVisibleRecord(FriendsVisibleBean friendsVisibleBean) {
        ArrayList<DbVisible> visibles = new ArrayList<>();
        int type = friendsVisibleBean.getType();
        List<String> friends = friendsVisibleBean.getFriends();
        String momentId = friendsVisibleBean.getMomentId();
        if (TextUtils.isEmpty(momentId)) {
            return;
        }
        if (CollectionUtil.isEmpty(friends)) {
            DbVisible visible = new DbVisible();
            visible.setPermissionType(Integer.valueOf(type));
            visible.setMomentId(friendsVisibleBean.getMomentId());
            visibles.add(visible);
        } else {
            for (String watchId : friends) {
                DbVisible visible = new DbVisible();
                visible.setMomentId(friendsVisibleBean.getMomentId());
                visible.setWatchId(watchId);
                visible.setPermissionType(Integer.valueOf(type));
                visibles.add(visible);
            }
        }
        deleteAllFriendsVisbleRecord(momentId);
        if (CollectionUtil.isEmpty(visibles)) {
            return;
        }
        insertVisibleFriendsFromDb(visibles);
    }

    @Override
    public List<DbMoment> queryDbMoments(long startTime, long endTime) {
        try {
            QueryBuilder<DbMoment, Integer> builder = this.momentDao.getDao().queryBuilder();
            builder.where().between("createTime", Long.valueOf(startTime), Long.valueOf(endTime));
            return builder.query();
        } catch (SQLException e) {
            LogUtil.e(TAG, "queryDbMoments error:", e);
            return null;
        }
    }

    @Override
    public List<DbMoment> queryDbMoments(String watchId, long startTime, long endTime) {
        try {
            QueryBuilder<DbMoment, Integer> builder = this.momentDao.getDao().queryBuilder();
            builder.where().between("createTime", Long.valueOf(startTime), Long.valueOf(endTime)).and().eq("watchId", watchId);
            return builder.query();
        } catch (SQLException e) {
            LogUtil.e(TAG, "queryDbMoments error:", e);
            return null;
        }
    }

    @Override
    public int deleteDbMomentsTimeAgo(long time) {
        try {
            DeleteBuilder<DbMoment, Integer> builder = this.momentDao.getDao().deleteBuilder();
            builder.where().gt("createTime", Long.valueOf(time));
            return builder.delete();
        } catch (SQLException e) {
            LogUtil.e(TAG, "queryDbMoments error:", e);
            return -1;
        }
    }

    @Override
    public boolean insertReminders(List<DbReminder> reminders) {
        return this.momentReminderDao.insertReminders(reminders);
    }

    @Override
    public DbReminder queryReminderByLabel(String label) {
        return this.momentReminderDao.queryReminderByLabel(label);
    }

    @Override
    public List<DbReminder> queryAllReminders() {
        if (this.momentReminderDao.queryAllReminders() == null) {
            return new ArrayList<>();
        }
        return this.momentReminderDao.queryAllReminders();
    }

    @Override
    public boolean insertIMReminder(DbIMReminder reminder) {
        return this.momentIMReminderDao.insertIMReminder(reminder);
    }

    @Override
    public void updateIMReminderStatus(String momentId, String status) {
        this.momentIMReminderDao.updateIMReminderStatus(momentId, status);
    }

    @Override
    public void batchUpdateIMReminderStatus(List<DbMoment> moments, String status) {
        this.momentIMReminderDao.batchUpdateIMReminderStatus(moments, status);
    }

    @Override
    public List<DbIMReminder> queryIMRemindersByStatus(String status) {
        return this.momentIMReminderDao.queryIMRemindersByStatus(status);
    }

    @Override
    public void deleteIMReminders(int type) {
        this.momentIMReminderDao.deleteIMReminders(type);
    }
}