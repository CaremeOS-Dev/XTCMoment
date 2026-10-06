package com.xtc.moment.data;

import com.xtc.moment.common.base.Preconditions;
import com.xtc.moment.data.local.MomentsLocalDataSource;
import com.xtc.moment.data.remote.MomentsRemoteDataSource;
import com.xtc.moment.db.bean.DbGiftRecord;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.net.bean.CommentOfficialResultBean;
import com.xtc.moment.net.bean.CommentResultBean;
import com.xtc.moment.net.bean.LbsStarBean;
import com.xtc.moment.net.bean.Moment;
import com.xtc.moment.net.bean.NormalResultBean;
import com.xtc.moment.net.bean.ReminderConfig;
import com.xtc.moment.net.bean.ReportMomentReq;
import com.xtc.moment.net.bean.SearchGiftRequest;
import com.xtc.moment.net.bean.SearchGiftResponse;
import com.xtc.moment.net.bean.SendGiftRequest;
import com.xtc.moment.tasks.domain.usecase.PublishCommentTask;
import com.xtc.moment.tasks.domain.usecase.PublishMomentTask;
import com.xtc.moment.tasks.domain.usecase.PublishOfficialCommentTask;

import rx.Observable;

/**
 * 动态数据仓库：把读写请求分别路由到远端与本地数据源。
 */
public class MomentsRepository implements IMomentsDataSource {

    private static MomentsRepository INSTANCE;
    private final MomentsLocalDataSource mMomentsLocalDataSource;
    private final MomentsRemoteDataSource mMomentsRemoteDataSource;

    private MomentsRepository(MomentsRemoteDataSource remoteDataSource, MomentsLocalDataSource localDataSource) {
        this.mMomentsRemoteDataSource = Preconditions.checkNotNull(remoteDataSource);
        this.mMomentsLocalDataSource = Preconditions.checkNotNull(localDataSource);
    }

    public static MomentsRepository getInstance(MomentsRemoteDataSource remoteDataSource, MomentsLocalDataSource localDataSource) {
        if (INSTANCE == null) {
            synchronized (MomentsRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MomentsRepository(remoteDataSource, localDataSource);
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public Observable<Moment> publishMoment(PublishMomentTask.RequestValues requestValues) {
        return this.mMomentsRemoteDataSource.publishMoment(requestValues);
    }

    @Override
    public boolean isLiked(DbMoment moment) {
        return this.mMomentsLocalDataSource.isLiked(moment);
    }

    @Override
    public void insertMomentByMomentId(DbMoment moment) {
        this.mMomentsLocalDataSource.insertMomentByMomentId(moment);
    }

    @Override
    public Observable<CommentResultBean> publishComment(PublishCommentTask.RequestValues requestValues) {
        return this.mMomentsRemoteDataSource.publishComment(requestValues);
    }

    @Override
    public Observable<CommentOfficialResultBean> publishOfficialComment(PublishOfficialCommentTask.RequestValues requestValues) {
        return this.mMomentsRemoteDataSource.publishOfficialComment(requestValues);
    }

    @Override
    public void addCommentMoment(DbMomentComment comment) {
        this.mMomentsLocalDataSource.addComment(comment);
    }

    @Override
    public boolean addGiftRecordMessage(DbGiftRecord record) {
        return this.mMomentsLocalDataSource.addGiftRecordMessage(record);
    }

    @Override
    public DbGiftRecord queryGiftByMomentId(String momentId) {
        return this.mMomentsLocalDataSource.queryGiftByMomentId(momentId);
    }

    @Override
    public boolean updateGift(DbGiftRecord record) {
        return this.mMomentsLocalDataSource.updateGift(record);
    }

    @Override
    public Observable<NormalResultBean> sendGift(SendGiftRequest request) {
        return this.mMomentsRemoteDataSource.sendGift(request);
    }

    @Override
    public Observable<SearchGiftResponse> searchGift(SearchGiftRequest request) {
        return this.mMomentsRemoteDataSource.searchGift(request);
    }

    @Override
    public Observable<String> reportMoment(ReportMomentReq request) {
        return this.mMomentsRemoteDataSource.reportMoment(request);
    }

    @Override
    public Observable<String> doLbsStar(LbsStarBean body) {
        return this.mMomentsRemoteDataSource.doLbsStar(body);
    }

    @Override
    public Observable<ReminderConfig> getReminderConfig() {
        return this.mMomentsRemoteDataSource.getReminderConfig();
    }
}