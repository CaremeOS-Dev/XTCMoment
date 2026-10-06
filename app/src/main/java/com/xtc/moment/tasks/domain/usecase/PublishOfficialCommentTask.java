package com.xtc.moment.tasks.domain.usecase;

import com.bumptech.glide.util.Preconditions;
import com.xtc.dispatch.task.AbsTask;
import com.xtc.log.LogUtil;
import com.xtc.moment.data.IMomentsDataSource;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.net.bean.CommentBean;
import com.xtc.moment.net.bean.CommentOfficialResultBean;
import com.xtc.moment.util.BeanConverterUtil;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * 发布官方动态评论任务：与普通评论流程一致，但走官方评论接口与返回体。
 */
public class PublishOfficialCommentTask extends AbsTask<PublishOfficialCommentTask.RequestValues, AbsTask.ResponseValue> {

    private final IMomentsDataSource mMomentsRepository;

    public PublishOfficialCommentTask(IMomentsDataSource momentsRepository) {
        this.mMomentsRepository = Preconditions.checkNotNull(momentsRepository, "tasksRepository cannot be null!");
    }

    @Override
    protected void executeTask(final RequestValues requestValues) {
        if (requestValues == null) {
            return;
        }
        this.mMomentsRepository.publishOfficialComment(requestValues).map(new Func1<CommentOfficialResultBean, CommentOfficialResultBean>() {
            @Override
            public CommentOfficialResultBean call(CommentOfficialResultBean resultBean) {
                if (resultBean == null) {
                    return null;
                }
                String result = resultBean.getResult();
                if ("1".equals(result) || "3".equals(result)) {
                    CommentBean comment = resultBean.getAdvertCommentVo();
                    LogUtil.i("AbsTask", "convertToDbMomentComment " + comment);
                    LogUtil.i("AbsTask", "convertToDbMomentComment " + resultBean);
                    DbMomentComment dbMomentComment = BeanConverterUtil.convertToDbMomentComment(comment, requestValues.getDbMomentComment());
                    LogUtil.i("AbsTask", "convertToDbMomentComment DbMomentComment " + dbMomentComment);
                    PublishOfficialCommentTask.this.mMomentsRepository.addCommentMoment(dbMomentComment);
                }
                return resultBean;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<CommentOfficialResultBean>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable throwable) {
                LogUtil.e("AbsTask", "publishMoment ErrorMessage = ", throwable);
                TaskCallback<AbsTask.ResponseValue> callback = PublishOfficialCommentTask.this.getTaskCallback();
                if (callback != null) {
                    callback.uiError();
                }
            }

            @Override
            public void onNext(CommentOfficialResultBean resultBean) {
                TaskCallback<AbsTask.ResponseValue> callback = PublishOfficialCommentTask.this.getTaskCallback();
                if (callback == null) {
                    return;
                }
                if (resultBean == null) {
                    callback.uiError();
                    return;
                }
                String result = resultBean.getResult();
                if ("1".equals(result) || "3".equals(result)) {
                    callback.uiSuccess(new ResponseValue(BeanConverterUtil.convertToDbMomentComment(resultBean.getAdvertCommentVo(), requestValues.getDbMomentComment()), result));
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