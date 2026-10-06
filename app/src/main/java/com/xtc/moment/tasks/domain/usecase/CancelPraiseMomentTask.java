package com.xtc.moment.tasks.domain.usecase;

import com.bumptech.glide.util.Preconditions;
import com.xtc.dispatch.task.AbsTask;
import com.xtc.log.LogUtil;
import com.xtc.moment.data.like.IMomentLikeDataSource;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.util.HandlerUtil;

import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * 取消点赞任务：普通动态走网络取消，官方动态直接删除本地点赞记录。
 */
public class CancelPraiseMomentTask extends AbsTask<CancelPraiseMomentTask.RequestValues, AbsTask.ResponseValue> {

    private static final String TAG = "CancelPraiseMomentTask";

    private final IMomentLikeDataSource iMomentLikeDataSource;

    public CancelPraiseMomentTask(IMomentLikeDataSource momentLikeDataSource) {
        this.iMomentLikeDataSource = Preconditions.checkNotNull(momentLikeDataSource, "tasksRepository cannot be null!");
    }

    @Override
    protected void executeTask(final RequestValues requestValues) {
        if (requestValues.isOfficial) {
            final TaskCallback<AbsTask.ResponseValue> callback = getTaskCallback();
            if (requestValues.getDbMoment() != null) {
                HandlerUtil.runOnBackground(new Runnable() {
                    @Override
                    public void run() {
                        callback.uiSuccess(new ResponseValue(CancelPraiseMomentTask.this.iMomentLikeDataSource.deleteLikeDaoByMomentId(requestValues.getDbMoment().getMomentId(), requestValues.getDbMoment().getWatchId()), ""));
                    }
                });
            }
            return;
        }
        this.iMomentLikeDataSource.cancelPraiseMoment(requestValues).subscribeOn(Schedulers.io()).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable throwable) {
                LogUtil.e(CancelPraiseMomentTask.TAG, "moment", throwable);
                CancelPraiseMomentTask.this.getTaskCallback().uiError(new ResponseValue(null, throwable.getMessage()));
            }

            @Override
            public void onNext(String response) {
                DbMoment dbMoment = CancelPraiseMomentTask.this.iMomentLikeDataSource.deleteLikeDaoByMomentId(requestValues.getMomentId(), requestValues.getWatchId());
                LogUtil.i(CancelPraiseMomentTask.TAG, "cancel like moment " + dbMoment);
                TaskCallback<AbsTask.ResponseValue> callback = CancelPraiseMomentTask.this.getTaskCallback();
                if (dbMoment != null) {
                    callback.uiSuccess(new ResponseValue(dbMoment, ""));
                    return;
                }
                requestValues.getDbMoment().setEnableLike(!requestValues.getDbMoment().isEnableLike());
                requestValues.getDbMoment().setLikeTotal(Integer.valueOf(requestValues.getDbMoment().getLikeTotal().intValue() - 1));
                callback.uiSuccess(new ResponseValue(requestValues.getDbMoment(), ""));
            }
        });
    }

    public static class RequestValues implements AbsTask.RequestValues {

        private DbMoment dbMoment;
        private boolean isOfficial;
        private String momentId;
        private String watchId;

        public RequestValues(String momentId, String watchId, DbMoment dbMoment, boolean isOfficial) {
            this.momentId = momentId;
            this.watchId = watchId;
            this.dbMoment = dbMoment;
            this.isOfficial = isOfficial;
        }

        public String getMomentId() {
            return this.momentId;
        }

        public String getWatchId() {
            return this.watchId;
        }

        public DbMoment getDbMoment() {
            return this.dbMoment;
        }
    }

    public static class ResponseValue implements AbsTask.ResponseValue {

        private DbMoment dbMoment;
        private String errorMessage;

        public ResponseValue(DbMoment dbMoment, String errorMessage) {
            this.dbMoment = dbMoment;
            this.errorMessage = errorMessage;
        }

        public DbMoment getDbMoment() {
            return this.dbMoment;
        }

        public String getErrorMessage() {
            return this.errorMessage;
        }
    }
}