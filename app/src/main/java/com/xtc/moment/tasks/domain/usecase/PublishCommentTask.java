package com.xtc.moment.tasks.domain.usecase;

import com.bumptech.glide.util.Preconditions;
import com.xtc.dispatch.task.AbsTask;
import com.xtc.log.LogUtil;
import com.xtc.moment.data.IMomentsDataSource;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.net.bean.CommentResultBean;
import com.xtc.moment.util.BeanConverterUtil;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * 发布评论任务：提交评论，成功时把服务端返回的评论写入本地库。
 */
public class PublishCommentTask extends AbsTask<PublishCommentTask.RequestValues, AbsTask.ResponseValue> {

    private final IMomentsDataSource mMomentsRepository;

    public PublishCommentTask(IMomentsDataSource momentsRepository) {
        this.mMomentsRepository = Preconditions.checkNotNull(momentsRepository, "tasksRepository cannot be null!");
    }

    @Override
    protected void executeTask(final RequestValues requestValues) {
        if (requestValues == null) {
            return;
        }
        this.mMomentsRepository.publishComment(requestValues).map(new Func1<CommentResultBean, CommentResultBean>() {
            @Override
            public CommentResultBean call(CommentResultBean commentResultBean) {
                if (commentResultBean == null) {
                    return null;
                }
                String result = commentResultBean.getResult();
                if ("1".equals(result) || "3".equals(result)) {
                    PublishCommentTask.this.mMomentsRepository.addCommentMoment(BeanConverterUtil.convertToDbMomentComment(commentResultBean.getComment(), requestValues.getDbMomentComment()));
                }
                return commentResultBean;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<CommentResultBean>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable throwable) {
                LogUtil.e("AbsTask", "publishMoment ErrorMessage = ", throwable);
                TaskCallback<AbsTask.ResponseValue> callback = PublishCommentTask.this.getTaskCallback();
                if (callback == null) {
                    return;
                }
                // 异常 message 即服务端业务错误码（如 000007 账号异常），必须原样透传，
                // 否则 View 层只能拿到空串，一律提示「发布失败」。
                String errorCode = throwable == null ? null : throwable.getMessage();
                if (errorCode != null && errorCode.contains("000005")) {
                    callback.uiError(new ErrorResponseValue("000005"));
                } else {
                    callback.uiError(new ErrorResponseValue(errorCode));
                }
            }

            @Override
            public void onNext(CommentResultBean commentResultBean) {
                TaskCallback<AbsTask.ResponseValue> callback = PublishCommentTask.this.getTaskCallback();
                if (callback == null) {
                    return;
                }
                if (commentResultBean == null) {
                    callback.uiError();
                    return;
                }
                String result = commentResultBean.getResult();
                if ("1".equals(result) || "3".equals(result)) {
                    callback.uiSuccess(new ResponseValue(BeanConverterUtil.convertToDbMomentComment(commentResultBean.getComment(), requestValues.getDbMomentComment()), result));
                } else if ("2".equals(result)) {
                    callback.uiError(new ErrorResponseValue("2"));
                } else if ("4".equals(result)) {
                    callback.uiError(new ErrorResponseValue("4"));
                } else {
                    callback.uiError();
                }
            }
        });
    }

    public static class RequestValues implements AbsTask.RequestValues {

        private DbMomentComment dbMomentComment;

        public RequestValues(DbMomentComment dbMomentComment) {
            this.dbMomentComment = dbMomentComment;
        }

        public DbMomentComment getDbMomentComment() {
            return this.dbMomentComment;
        }

        public void setDbMomentComment(DbMomentComment dbMomentComment) {
            this.dbMomentComment = dbMomentComment;
        }

        @Override
        public String toString() {
            return "RequestValues{dbMomentComment=" + this.dbMomentComment + '}';
        }
    }

    public static class ResponseValue implements AbsTask.ResponseValue {

        private DbMomentComment momentComment;
        private String result;

        public ResponseValue(DbMomentComment momentComment, String result) {
            this.momentComment = momentComment;
            this.result = result;
        }

        public String getResult() {
            return this.result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public DbMomentComment getMomentComment() {
            return this.momentComment;
        }

        public void setMomentComment(DbMomentComment momentComment) {
            this.momentComment = momentComment;
        }

        @Override
        public String toString() {
            return "ResponseValue{result='" + this.result + "', momentComment=" + this.momentComment + '}';
        }
    }

    public static class ErrorResponseValue implements AbsTask.ResponseValue {

        private String errorCode;

        public ErrorResponseValue(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorCode() {
            return this.errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        @Override
        public String toString() {
            return "ErrorResponseValue{errorCode='" + this.errorCode + "'}";
        }
    }
}