package com.xtc.moment.tasks.domain.usecase;

import com.bumptech.glide.util.Preconditions;
import com.xtc.dispatch.task.AbsTask;
import com.xtc.log.LogUtil;
import com.xtc.moment.data.like.IMomentLikeDataSource;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.net.bean.DefaultResponse;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * 点赞任务：区分普通动态与官方动态，点赞成功后写入本地点赞记录并刷新动态。
 */
public class PraiseMomentTask extends AbsTask<PraiseMomentTask.RequestValues, AbsTask.ResponseValue> {

    private final IMomentLikeDataSource iMomentLikeDataSource;

    public PraiseMomentTask(IMomentLikeDataSource momentLikeDataSource) {
        this.iMomentLikeDataSource = Preconditions.checkNotNull(momentLikeDataSource, "tasksRepository cannot be null!");
    }

    @Override
    protected void executeTask(final RequestValues requestValues) {
        if (requestValues.isOfficial) {
            this.iMomentLikeDataSource.praiseAdvertise(requestValues).map(new Func1<DefaultResponse, DbLikeMessage>() {
                @Override
                public DbLikeMessage call(DefaultResponse response) {
                    return buildLikeMessage(requestValues, response);
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<DbLikeMessage>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable throwable) {
                    LogUtil.e("moment", throwable);
                    TaskCallback<AbsTask.ResponseValue> callback = PraiseMomentTask.this.getTaskCallback();
                    if (callback != null) {
                        callback.uiError(new ErrorResponseValue(throwable.getMessage()));
                    }
                }

                @Override
                public void onNext(DbLikeMessage likeMessage) {
                    TaskCallback<AbsTask.ResponseValue> callback = PraiseMomentTask.this.getTaskCallback();
                    if (likeMessage != null) {
                        callback.uiSuccess(new ResponseValue(likeMessage));
                    } else {
                        callback.uiError();
                    }
                }
            });
        } else {
            this.iMomentLikeDataSource.praiseMoment(requestValues).map(new Func1<DefaultResponse, DbLikeMessage>() {
                @Override
                public DbLikeMessage call(DefaultResponse response) {
                    LogUtil.i("AbsTask", "DefaultResponse" + response);
                    return buildLikeMessage(requestValues, response);
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<DbLikeMessage>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable throwable) {
                    LogUtil.e("moment", throwable);
                    TaskCallback<AbsTask.ResponseValue> callback = PraiseMomentTask.this.getTaskCallback();
                    if (callback != null) {
                        callback.uiError(new ErrorResponseValue(throwable.getMessage()));
                    }
                }

                @Override
                public void onNext(DbLikeMessage likeMessage) {
                    TaskCallback<AbsTask.ResponseValue> callback = PraiseMomentTask.this.getTaskCallback();
                    if (likeMessage != null) {
                        callback.uiSuccess(new ResponseValue(likeMessage));
                    } else {
                        callback.uiError();
                    }
                }
            });
        }
    }

    private DbLikeMessage buildLikeMessage(RequestValues requestValues, DefaultResponse response) {
        DbLikeMessage likeMessage = new DbLikeMessage();
        likeMessage.setChecked(true);
        likeMessage.setWatchName(requestValues.getWatchName());
        likeMessage.setMomentId(requestValues.getMomentId());
        likeMessage.setCreateTime(Long.valueOf(System.currentTimeMillis()));
        likeMessage.setWatchId(requestValues.getWatchId());
        likeMessage.setMomentWatchId(requestValues.getMomentWatchId());
        likeMessage.setEmotionId(response.getEmotionId());
        if (this.iMomentLikeDataSource.addLikeMessage(likeMessage)) {
            this.iMomentLikeDataSource.updateMomentByMomentId(requestValues.getMomentId());
        }
        return likeMessage;
    }

    public static class RequestValues implements AbsTask.RequestValues {

        private boolean isOfficial;
        private String momentId;
        private String momentWatchId;
        private String watchId;
        private String watchName;

        public RequestValues(String momentId, String momentWatchId, String watchName, String watchId, boolean isOfficial) {
            this.momentId = momentId;
            this.momentWatchId = momentWatchId;
            this.watchName = watchName;
            this.watchId = watchId;
            this.isOfficial = isOfficial;
        }

        public String getMomentId() {
            return this.momentId;
        }

        public String getMomentWatchId() {
            return this.momentWatchId;
        }

        public String getWatchId() {
            return this.watchId;
        }

        public String getWatchName() {
            return this.watchName;
        }
    }

    public static class ResponseValue implements AbsTask.ResponseValue {

        private DbLikeMessage dbLikeMessage;

        public ResponseValue(DbLikeMessage dbLikeMessage) {
            this.dbLikeMessage = dbLikeMessage;
        }

        public DbLikeMessage getDbLikeMessage() {
            return this.dbLikeMessage;
        }

        public void setDbLikeMessage(DbLikeMessage dbLikeMessage) {
            this.dbLikeMessage = dbLikeMessage;
        }
    }

    public static class ErrorResponseValue implements AbsTask.ResponseValue {

        private String errorMessage;

        public ErrorResponseValue(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return this.errorMessage;
        }
    }
}