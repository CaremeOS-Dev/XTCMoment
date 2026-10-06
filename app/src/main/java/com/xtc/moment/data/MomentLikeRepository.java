package com.xtc.moment.data;

import com.xtc.moment.common.base.Preconditions;
import com.xtc.moment.data.local.MomentLikeLocalDataSource;
import com.xtc.moment.data.remote.MomentLikeRemoteDataSource;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.net.bean.DefaultResponse;
import com.xtc.moment.tasks.domain.usecase.CancelPraiseMomentTask;
import com.xtc.moment.tasks.domain.usecase.PraiseMomentTask;

import rx.Observable;

/**
 * 点赞数据仓库：网络点赞走远端数据源，点赞记录与动态刷新走本地数据源。
 */
public class MomentLikeRepository implements com.xtc.moment.data.like.IMomentLikeDataSource {

    private static MomentLikeRepository INSTANCE;
    private final MomentLikeLocalDataSource mMomentsLocalDataSource;
    private final MomentLikeRemoteDataSource mMomentsRemoteDataSource;

    private MomentLikeRepository(MomentLikeRemoteDataSource remoteDataSource, MomentLikeLocalDataSource localDataSource) {
        this.mMomentsRemoteDataSource = Preconditions.checkNotNull(remoteDataSource);
        this.mMomentsLocalDataSource = Preconditions.checkNotNull(localDataSource);
    }

    public static MomentLikeRepository getInstance(MomentLikeRemoteDataSource remoteDataSource, MomentLikeLocalDataSource localDataSource) {
        if (INSTANCE == null) {
            synchronized (MomentsRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MomentLikeRepository(remoteDataSource, localDataSource);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public Observable<DefaultResponse> praiseMoment(PraiseMomentTask.RequestValues requestValues) {
        return this.mMomentsRemoteDataSource.praiseMoment(requestValues);
    }

    @Override
    public boolean addLikeMessage(DbLikeMessage likeMessage) {
        return this.mMomentsLocalDataSource.addLikeMessage(likeMessage);
    }

    @Override
    public void updateMomentByMomentId(String momentId) {
        this.mMomentsLocalDataSource.updateMomentByMomentId(momentId);
    }

    @Override
    public Observable<String> cancelPraiseMoment(CancelPraiseMomentTask.RequestValues requestValues) {
        return this.mMomentsRemoteDataSource.cancelPraiseMoment(requestValues);
    }

    @Override
    public DbMoment deleteLikeDaoByMomentId(String momentId, String watchId) {
        return this.mMomentsLocalDataSource.deleteLikeDaoByMomentId(momentId, watchId);
    }

    @Override
    public Observable<DefaultResponse> praiseAdvertise(PraiseMomentTask.RequestValues requestValues) {
        return this.mMomentsRemoteDataSource.praiseAdvertise(requestValues);
    }
}