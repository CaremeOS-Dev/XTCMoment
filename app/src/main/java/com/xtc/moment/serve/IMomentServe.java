package com.xtc.moment.serve;

import com.xtc.moment.db.bean.DbIMReminder;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.db.bean.DbReminder;
import com.xtc.moment.db.bean.DbSexyPhotoDistinguish;
import com.xtc.moment.db.bean.DbVisible;
import com.xtc.moment.module.bean.FriendsVisibleBean;
import com.xtc.moment.module.bean.PoiBean;
import com.xtc.moment.module.bean.VideoTokenParam;
import com.xtc.moment.module.report.bean.ReportDataBean;
import com.xtc.moment.module.report.bean.ReportInformParam;
import com.xtc.moment.module.report.bean.StartReportRequest;
import com.xtc.moment.net.bean.BanStateBean;
import com.xtc.moment.net.bean.CommentOfficialResultBean;
import com.xtc.moment.net.bean.CommentResultBean;
import com.xtc.moment.net.bean.DefaultResponse;
import com.xtc.moment.net.bean.DeleteResultBean;
import com.xtc.moment.net.bean.Moment;
import com.xtc.moment.net.bean.NormalResultBean;
import com.xtc.moment.net.bean.OfficialCommentResultBean;
import com.xtc.moment.net.bean.PraiseResponse;
import com.xtc.moment.net.bean.VideoTokenVoResponse;

import java.util.List;
import java.util.Set;

import rx.Observable;

/**
 * 动态业务服务接口：聚合动态、评论、点赞、提醒、可见范围等所有本地与网络操作。
 */
public interface IMomentServe {

    void addCommentMoment(DbMomentComment comment);

    void addCommentMoment(List<DbMomentComment> comments);

    void addLikeMessage(List<DbLikeMessage> messages);

    boolean addLikeMessage(DbLikeMessage likeMessage);

    void addMoments(List<DbMoment> moments);

    boolean addSexyPhotoRecord(List<DbSexyPhotoDistinguish> records);

    void batchUpdateIMReminderStatus(List<DbMoment> moments, String status);

    Observable<String> cancelPraiseMoment(String momentId, String momentWatchId);

    void changeReportInfoData(String data);

    void clearLikeDbData();

    Observable<CommentOfficialResultBean> commentAdvertMoment(DbMomentComment comment);

    Observable<CommentResultBean> commentMoment(DbMomentComment comment);

    boolean deleteAllFriendsVisbleRecord(String momentId);

    void deleteDBCommentByMomentIdAndCommentId(String momentId, String commentId);

    boolean deleteDBMomentByMomentId(DbMoment moment);

    boolean deleteDBMomentByMomentIdAndWatchId(String momentId, String watchId);

    void deleteDbCommentForBatch(List<DbMomentComment> comments);

    boolean deleteDbMomentForBatch(List<DbMoment> moments);

    int deleteDbMomentsTimeAgo(long time);

    void deleteIMReminders(int type);

    DbMoment deleteLikeDaoByMomentId(String momentId, String momentWatchId);

    boolean deleteLikeMessageByMomentId(String momentId);

    boolean deleteMomentByWatchId(String watchId);

    Observable<NormalResultBean> deleteServerComment(DbMomentComment comment);

    Observable<DeleteResultBean> deleteServerMomentByKey(String key, DbMoment moment);

    long getAllMoment();

    List<DbLikeMessage> getLikeMessageByMomentId(String momentId);

    List<DbLikeMessage> getLikeMessageByMomentIdAndWatchId(String momentId, String watchId);

    List<DbMoment> getMomentById(String momentId);

    DbMoment getMomentByMomentIdWithoutComment(String momentId);

    List<DbMoment> getMomentByWatchId(String watchId);

    long getMomentCountInDb();

    Observable<List<DbMoment>> getMomentsFromDb(long offset, long limit, int type, String watchId);

    List<DbMoment> getMomentsFromDbSync(long offset, long limit, int type, String watchId);

    Observable<List<DbMoment>> getMomentsFromNet(long maxTime, int pageSize, String watchId, long minTime);

    Observable<List<OfficialCommentResultBean>> getOfficialCommentFromNet(String advertId, List<String> commentIds);

    List<DbMoment> getOthersUncheckedMoment(String watchIds);

    long getOthersUncheckedMomentCount(String watchIds);

    Observable<PraiseResponse> getPraiseRecord(List<String> momentIds, String watchId);

    long getUncheckedCommentCountByWatchId(String watchId);

    long getUncheckedLikeMessageCountByWatchId(String watchId);

    Observable<VideoTokenVoResponse> getUploadVideoToken(VideoTokenParam param);

    List<DbVisible> getVisibleFriendsFromDb(String momentId);

    boolean increaseLikeTotal(String momentId);

    boolean insertIMReminder(DbIMReminder reminder);

    void insertMomentByMomentId(DbMoment moment);

    boolean insertReminders(List<DbReminder> reminders);

    boolean insertVisibleFriendsFromDb(List<DbVisible> visibles);

    boolean isLiked(DbMoment moment);

    Observable<String> launchedReport(StartReportRequest request);

    List<DbMomentComment> loadAllUncheckedCommentByWatchId(String watchId);

    List<DbLikeMessage> loadAllUncheckedLikeMessageByWatchId(String watchId);

    Observable<DefaultResponse> praiseAdvertise(String momentId, String momentWatchId);

    Observable<DefaultResponse> praiseMoment(String momentId, String momentWatchId);

    Observable<Moment> publishMoment(int type, String content, int resourceId, String resource, FriendsVisibleBean friendsVisibleBean);

    Observable<Moment> publishMoment(int type, String content, int resourceId, String resource, PoiBean poiBean, FriendsVisibleBean friendsVisibleBean);

    Observable<Moment> publishMoment(int type, String content, int resourceId, String resource, String packageName, PoiBean poiBean, FriendsVisibleBean friendsVisibleBean);

    Observable<Moment> publishMoment(int type, String content, int resourceId, String resource, String packageName, List<Integer> visibleTypes, PoiBean poiBean);

    Observable<Moment> publishMoment(int type, String content, int resourceId, String resource, List<Integer> visibleTypes);

    List<DbReminder> queryAllReminders();

    List<DbMoment> queryByLimitTime(String watchId, long time);

    List<DbMoment> queryByMomentIds(Set<String> momentIds);

    List<DbMomentComment> queryCommentByCommentId(String commentId);

    List<DbMomentComment> queryCommentByMomentIdAndCommentId(String momentId, String commentId);

    List<DbMoment> queryDbMoments(long startTime, long endTime);

    List<DbMoment> queryDbMoments(String watchId, long startTime, long endTime);

    List<DbIMReminder> queryIMRemindersByStatus(String status);

    List<DbMoment> queryMessageForPages(long offset, long limit, boolean ascending);

    DbReminder queryReminderByLabel(String label);

    Observable<ReportDataBean> queryReportInform(ReportInformParam param);

    void saveVisibleRecord(FriendsVisibleBean friendsVisibleBean);

    Observable<BanStateBean> updateBanState();

    void updateIMReminderStatus(String momentId, String status);

    void updateLikeMessage(List<DbLikeMessage> messages);

    void updateLikeMessageName(String watchId, String watchName);

    boolean updateMoment(DbMoment moment);

    void updateMomentByMomentId(String momentId);

    void updateMomentComment(DbMomentComment comment);

    void updateMomentComment(List<DbMomentComment> comments);

    void updateMomentsByMomentId(List<DbMoment> moments);
}