package com.xtc.moment.base;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import com.xtc.architecture.mvp.core.MvpPresenter;
import com.xtc.bigdata.collector.utils.CollectionUtil;
import com.xtc.dispatch.TaskDispatcher;
import com.xtc.dispatch.task.AbsTask;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.data.IMomentsDataSource;
import com.xtc.moment.data.MomentLikeRepository;
import com.xtc.moment.data.MomentsRepository;
import com.xtc.moment.data.like.IMomentLikeDataSource;
import com.xtc.moment.data.local.MomentLikeLocalDataSource;
import com.xtc.moment.data.local.MomentsLocalDataSource;
import com.xtc.moment.data.remote.MomentLikeRemoteDataSource;
import com.xtc.moment.data.remote.MomentsRemoteDataSource;
import com.xtc.moment.db.bean.DbAdvertCloseRecord;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.helper.ReminderHelper;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.scope.FriendsVisibleRangeActivity;
import com.xtc.moment.module.widget.NormalThreeIconDialog;
import com.xtc.moment.net.bean.DeleteResultBean;
import com.xtc.moment.net.bean.NormalResultBean;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.serve.CommentServeImpl;
import com.xtc.moment.serve.IMomentServe;
import com.xtc.moment.serve.bean.WatchAccountInfo;
import com.xtc.moment.serve.delete.MomentDeleteServe;
import com.xtc.moment.tasks.domain.usecase.CancelPraiseMomentTask;
import com.xtc.moment.tasks.domain.usecase.PraiseMomentTask;
import com.xtc.moment.tasks.domain.usecase.PublishCommentTask;
import com.xtc.moment.tasks.domain.usecase.PublishOfficialCommentTask;
import com.xtc.moment.third.MomentBehaviorUtil;
import com.xtc.moment.third.bean.PushCommentBean;
import com.xtc.moment.third.behavior.MomentBehavior;
import com.xtc.moment.util.BeanConverterUtil;
import com.xtc.moment.util.EventData;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.MomentTypeUtil;
import com.xtc.moment.util.NotifyUtils;
import com.xtc.moment.util.ProviderNotifyUtils;
import com.xtc.moment.util.ToastUtil;
import com.xtc.ui.widget.UiConstants;
import com.xtc.ui.widget.dialog.NormalIconDialog;
import com.xtc.ui.widget.dialog.bean.icon.DoubleIconBtnBean;
import com.xtc.ui.widget.dialog.bean.icon.NormalIconDialogBean;
import com.xtc.ui.widget.dialog.bean.icon.ThreeIconBtnBean;
import com.xtc.ui.widget.util.DialogUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.system.NetworkUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 互动功能基类 Presenter：评论、点赞、删除动态/评论等通用逻辑。
 */
public abstract class BaseInteractPresenter<M extends IBaseInteractView> extends MvpPresenter<M> {

    protected NormalIconDialog deleteDialog;
    protected IMomentLikeDataSource iMomentLikeDataSource;
    protected IMomentServe iMomentServe;
    protected Context mContext;
    private IMomentsDataSource momentsDataSource;
    private MomentsLocalDataSource momentsLocalDataSource;

    public abstract String getLogTag();

    public void commentMoment(final DbMomentComment comment) {
        LogUtil.d(getLogTag(), "评论动态：" + comment);
        PublishCommentTask task = new PublishCommentTask(getMomentsRepository());
        task.setRequestValues(new PublishCommentTask.RequestValues(comment));
        task.setTaskCallback(new AbsTask.TaskCallback<AbsTask.ResponseValue>() {
            @Override
            protected void onSuccess(AbsTask.ResponseValue responseValue) {
                if (BaseInteractPresenter.this.isViewAttached() && BaseInteractPresenter.this.getView() != null && (responseValue instanceof PublishCommentTask.ResponseValue)) {
                    final PublishCommentTask.ResponseValue result = (PublishCommentTask.ResponseValue) responseValue;
                    BaseInteractPresenter.this.getView().publishSuccess(result.getMomentComment(), result.getResult());
                    HandlerUtil.runOnBackground(new Runnable() {
                        @Override
                        public void run() {
                            BaseInteractPresenter.this.notifyCommentAdded(result.getMomentComment());
                        }
                    });
                }
            }

            @Override
            protected void onError(AbsTask.ResponseValue responseValue) {
                if (BaseInteractPresenter.this.isViewAttached() && BaseInteractPresenter.this.getView() != null && (responseValue instanceof PublishCommentTask.ErrorResponseValue)) {
                    String errorCode = ((PublishCommentTask.ErrorResponseValue) responseValue).getErrorCode();
                    if ("4".equals(errorCode)) {
                        BaseInteractPresenter.this.getView().publishLimited();
                    } else if ("2".equals(errorCode)) {
                        BaseInteractPresenter.this.getView().publishInvalidate();
                    } else if ("000005".equals(errorCode)) {
                        BaseInteractPresenter.this.handleAlreadyDeletedMoment(comment);
                    } else {
                        BaseInteractPresenter.this.getView().publishFail(errorCode);
                    }
                }
            }

            @Override
            protected void onError() {
                if (!BaseInteractPresenter.this.isViewAttached() || BaseInteractPresenter.this.getView() == null) {
                    return;
                }
                BaseInteractPresenter.this.getView().publishFail("");
            }
        });
        TaskDispatcher.dispatchImmediately(task);
    }
    /**
     * 评论已被服务端删除时，删除本地动态并提示。
     */
    private void handleAlreadyDeletedMoment(final DbMomentComment comment) {
        Observable.fromCallable(new Callable<DbMoment>() {
            @Override
            public DbMoment call() throws Exception {
                HashSet<String> ids = new HashSet<>();
                ids.add(comment.getMomentId());
                List<DbMoment> moments = BaseInteractPresenter.this.iMomentServe.queryByMomentIds(ids);
                if (moments == null || moments.size() == 0) {
                    return null;
                }
                DbMoment moment = moments.get(0);
                LogUtil.d(BaseInteractPresenter.this.getLogTag(), "需要移除的好友动态：" + moment);
                boolean deleted = BaseInteractPresenter.this.iMomentServe.deleteDBMomentByMomentId(moment);
                if (deleted) {
                    EventBus.getDefault().post(new EventData(1, moment));
                    return moment;
                }
                return null;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<DbMoment>() {
            @Override
            public void call(DbMoment moment) {
                if (moment == null || !BaseInteractPresenter.this.isViewAttached() || BaseInteractPresenter.this.getView() == null) {
                    return;
                }
                HandlerUtil.runOnUIThreadDelay(new Runnable() {
                    @Override
                    public void run() {
                        if (!BaseInteractPresenter.this.isViewAttached() || BaseInteractPresenter.this.getView() == null) {
                            return;
                        }
                        BaseInteractPresenter.this.getView().momentAlreadyDeleted();
                    }
                }, 500L);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtil.e(BaseInteractPresenter.this.getLogTag(), "动态移除失败:", throwable);
            }
        });
    }

    public void deleteComment(Context context, DbMomentComment comment, DbMoment moment) {
        showDeleteCommentDialog(context, comment, moment);
    }

    private void showDeleteCommentDialog(Context context, final DbMomentComment comment, final DbMoment moment) {
        this.deleteDialog = DialogUtil.makeDoubleIconBtnDialog(context, new DoubleIconBtnBean(context, true, UiConstants.Color.GRAY, 0, R.string.cancel, UiConstants.Color.RED, 2, R.string.delete, true, new DoubleIconBtnBean.OnClickListener() {
            @Override
            public void onBottomBtnClick(Dialog dialog, View view) {
            }

            @Override
            public void onLeftBtnClick(Dialog dialog, View view) {
                DialogUtil.dismissDialog(dialog);
            }

            @Override
            public void onRightBtnClick(Dialog dialog, View view) {
                BaseInteractPresenter.this.deleteCommentSync(comment, moment);
                DialogUtil.dismissDialog(dialog);
            }
        }));
        DialogUtil.showDialog(this.deleteDialog);
    }

    public void deleteCommentSync(final DbMomentComment comment, final DbMoment moment) {
        if (!NetworkUtils.isNetworkAvailable(this.mContext)) {
            if (getView() != null) {
                getView().removeFail();
            }
            return;
        }
        if (comment == null) {
            return;
        }
        final String watchId = AccountInfoServerImpl.getInstance(this.mContext).getWatchAccountInfo().getWatchId(this.mContext);
        comment.setWatchId(watchId);
        this.iMomentServe.deleteServerComment(comment).map(new Func1<NormalResultBean, Boolean>() {
            @Override
            public Boolean call(NormalResultBean result) {
                if (result != null && "000001".equals(result.getCode())) {
                    if (MomentTypeUtil.isOfficialType(moment.getType().intValue())) {
                        LogUtil.i(BaseInteractPresenter.this.getLogTag(), "iMomentServe 删除评论官方消息" + moment.getType());
                        MomentBehavior.advCommentDeleteClick(BaseInteractPresenter.this.mContext, new PushCommentBean(watchId, comment.getCommentId()));
                    } else {
                        MomentBehavior.commentDelete(BaseInteractPresenter.this.mContext, new PushCommentBean(watchId, comment.getCommentId()));
                    }
                    BaseInteractPresenter.this.iMomentServe.deleteDBCommentByMomentIdAndCommentId(comment.getMomentId(), comment.getCommentId());
                    ProviderNotifyUtils.notifyCommentDelete(comment, BaseInteractPresenter.this.mContext);
                    return true;
                }
                return false;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable throwable) {
                LogUtil.d(BaseInteractPresenter.this.getLogTag(), "deleteServerMomentByKey:e" + throwable);
                if (BaseInteractPresenter.this.getView() != null) {
                    BaseInteractPresenter.this.getView().removeCommentFail();
                }
            }

            @Override
            public void onNext(Boolean success) {
                if (!BaseInteractPresenter.this.isViewAttached() || BaseInteractPresenter.this.getView() == null) {
                    return;
                }
                LogUtil.d(BaseInteractPresenter.this.getLogTag(), "deleteCommentSync:dbMomentComment" + success + comment);
                if (success.booleanValue()) {
                    BaseInteractPresenter.this.getView().removeComment(comment);
                } else {
                    BaseInteractPresenter.this.getView().removeCommentFail();
                }
            }
        });
    }

    protected IMomentsDataSource getMomentsRepository() {
        if (this.momentsDataSource == null) {
            this.momentsDataSource = MomentsRepository.getInstance(MomentsRemoteDataSource.getInstance(this.mContext), getMomentsLocalDataSource());
        }
        return this.momentsDataSource;
    }

    protected MomentsLocalDataSource getMomentsLocalDataSource() {
        if (this.momentsLocalDataSource == null) {
            this.momentsLocalDataSource = MomentsLocalDataSource.getInstance(this.mContext);
        }
        return this.momentsLocalDataSource;
    }

    private void notifyCommentAdded(DbMomentComment comment) {
        comment.setWatchName(this.mContext.getResources().getString(R.string.me));
        String watchId = AccountInfoServerImpl.getInstance(this.mContext).getWatchAccountInfo().getWatchId(this.mContext);
        if (!TextUtils.isEmpty(comment.getReplyId()) && comment.getReplyId().equals(watchId)) {
            comment.setReplyName(this.mContext.getResources().getString(R.string.me));
        }
        Uri.Builder builder = Uri.parse("content://com.xtc.moment.commentProvider/comment").buildUpon();
        builder.appendQueryParameter("type", Constants.QueryParameter.NEW_COMMENT_OR_LIKE_MESSAGE);
        builder.appendQueryParameter("data", JSONUtil.toJSON(comment));
        this.mContext.getContentResolver().notifyChange(builder.build(), null);
        NotifyUtils.notifyCommentUnReadNum(this.mContext);
    }

    public void likeMoment(String momentId, String momentWatchId, boolean isOfficial) {
        WatchAccountInfo accountInfo = AccountInfoServerImpl.getInstance(this.mContext).getWatchAccountInfo();
        PraiseMomentTask.RequestValues requestValues = new PraiseMomentTask.RequestValues(momentId, momentWatchId, accountInfo.getName(this.mContext), accountInfo.getWatchId(this.mContext), isOfficial);
        PraiseMomentTask task = new PraiseMomentTask(getMomentLikeRepository());
        task.setRequestValues(requestValues);
        task.setTaskCallback(new AbsTask.TaskCallback<AbsTask.ResponseValue>() {
            @Override
            protected void onSuccess(AbsTask.ResponseValue responseValue) {
                if (BaseInteractPresenter.this.isViewAttached() && (responseValue instanceof PraiseMomentTask.ResponseValue)) {
                    BaseInteractPresenter.this.getView().likeSuccess(((PraiseMomentTask.ResponseValue) responseValue).getDbLikeMessage());
                }
            }

            @Override
            protected void onError(AbsTask.ResponseValue responseValue) {
                if (BaseInteractPresenter.this.isViewAttached() && (responseValue instanceof PraiseMomentTask.ErrorResponseValue)) {
                    BaseInteractPresenter.this.getView().likeError(((PraiseMomentTask.ErrorResponseValue) responseValue).getErrorMessage());
                }
            }

            @Override
            protected void onError() {
                BaseInteractPresenter.this.getView().likeError();
            }
        });
        TaskDispatcher.dispatchImmediately(task);
    }

    public void cancelLikeMoment(String momentId, String momentWatchId, DbMoment moment, boolean isOfficial) {
        CancelPraiseMomentTask task = new CancelPraiseMomentTask(getMomentLikeRepository());
        task.setRequestValues(new CancelPraiseMomentTask.RequestValues(momentId, momentWatchId, moment, isOfficial));
        task.setTaskCallback(new AbsTask.TaskCallback<AbsTask.ResponseValue>() {
            @Override
            protected void onSuccess(AbsTask.ResponseValue responseValue) {
                if ((responseValue instanceof CancelPraiseMomentTask.ResponseValue) && BaseInteractPresenter.this.isViewAttached()) {
                    BaseInteractPresenter.this.getView().cancelLikeSuccess(((CancelPraiseMomentTask.ResponseValue) responseValue).getDbMoment());
                }
            }

            @Override
            protected void onError(AbsTask.ResponseValue responseValue) {
                if ((responseValue instanceof CancelPraiseMomentTask.ResponseValue) && BaseInteractPresenter.this.isViewAttached()) {
                    BaseInteractPresenter.this.getView().likeError(((CancelPraiseMomentTask.ResponseValue) responseValue).getErrorMessage());
                }
            }

            @Override
            protected void onError() {
                BaseInteractPresenter.this.getView().likeError();
            }
        });
        TaskDispatcher.dispatchImmediately(task);
    }
    /**
     * 长按动态的处理入口：官方广告弹关闭框，自己的动态按类型弹删除/可见范围框。
     */
    public void deleteMoment(Context context, DbMoment moment, int type) {
        if (moment.getWatchId().length() < 40) {
            showCloseAdvertDialog(context, moment);
            LogUtil.d(getLogTag(), "deleteMoment 只是弹窗");
            MomentBehavior.advLongClick(context, moment.getMomentId());
        } else if (type == 1) {
            showDeleteDialog(context, moment);
        } else if (type == 3) {
            showChangeRangeDialog(context, moment);
        } else if (type == 13) {
            showDeleteAndChangeRangeDialog(context, moment);
        }
    }

    private void showDeleteAndChangeRangeDialog(final Context context, final DbMoment moment) {
        NormalIconDialogBean bean = new NormalIconDialogBean(2, false, context, null, UiConstants.Color.GREED, R.drawable.bg_mine_green, R.string.visible_range_btn, UiConstants.Color.RED, R.drawable.delete, R.string.delete, R.string.cancel, new ThreeIconBtnBean.OnClickListener() {
            @Override
            public void onLeftBtnClick(Dialog dialog, View view) {
                Intent intent = new Intent(context, FriendsVisibleRangeActivity.class);
                intent.putExtra("moment_id", moment.getMomentId());
                context.startActivity(intent);
                DialogUtil.dismissDialog(dialog);
            }

            @Override
            public void onRightBtnClick(Dialog dialog, View view) {
                BaseInteractPresenter.this.deleteDBMomentAsync(moment);
                DialogUtil.dismissDialog(dialog);
            }

            @Override
            public void onBottomBtnClick(Dialog dialog, View view) {
                DialogUtil.dismissDialog(dialog);
            }
        });
        NormalThreeIconDialog dialog = new NormalThreeIconDialog(context, true, true);
        dialog.initData(bean);
        DialogUtil.showDialog(dialog);
    }

    private void showChangeRangeDialog(Context context, DbMoment moment) {
        NormalIconDialogBean bean = new NormalIconDialogBean(0, true, context, null, UiConstants.Color.GRAY, R.drawable.cancel, R.string.cancel, UiConstants.Color.GREED, R.drawable.bg_mine_green, R.string.visible_range_btn, 0, new ThreeIconBtnBean.OnClickListener() {
            @Override
            public void onBottomBtnClick(Dialog dialog, View view) {
            }

            @Override
            public void onLeftBtnClick(Dialog dialog, View view) {
                DialogUtil.dismissDialog(dialog);
            }

            @Override
            public void onRightBtnClick(Dialog dialog, View view) {
                DialogUtil.dismissDialog(dialog);
            }
        });
        NormalThreeIconDialog dialog = new NormalThreeIconDialog(context, true, true);
        dialog.initData(bean);
        DialogUtil.showDialog(dialog);
    }

    private void showDeleteDialog(Context context, final DbMoment moment) {
        this.deleteDialog = DialogUtil.makeDoubleIconBtnDialog(context, new DoubleIconBtnBean(context, true, UiConstants.Color.GRAY, 0, R.string.cancel, UiConstants.Color.RED, 2, R.string.delete, true, new DoubleIconBtnBean.OnClickListener() {
            @Override
            public void onBottomBtnClick(Dialog dialog, View view) {
            }

            @Override
            public void onLeftBtnClick(Dialog dialog, View view) {
                DialogUtil.dismissDialog(dialog);
            }

            @Override
            public void onRightBtnClick(Dialog dialog, View view) {
                BaseInteractPresenter.this.deleteDBMomentAsync(moment);
                DialogUtil.dismissDialog(dialog);
            }
        }));
        DialogUtil.showDialog(this.deleteDialog);
    }

    private void showCloseAdvertDialog(Context context, final DbMoment moment) {
        com.xtc.moment.module.widget.DialogUtil.showDialog(com.xtc.moment.module.widget.DialogUtil.makeDoubleIconBtnDialog(context, new com.xtc.moment.module.widget.DoubleIconBtnBean(context, true, UiConstants.Color.GRAY, 0, R.string.cancel, UiConstants.Color.RED, R.drawable.ic_advertise_close, R.string.close_advert, true, new com.xtc.moment.module.widget.DoubleIconBtnBean.OnClickListener() {
            @Override
            public void onBottomBtnClick(Dialog dialog, View view) {
            }

            @Override
            public void onLeftBtnClick(Dialog dialog, View view) {
                DialogUtil.dismissDialog(dialog);
            }

            @Override
            public void onRightBtnClick(Dialog dialog, View view) {
                BaseInteractPresenter.this.deleteDBMomentAsync(moment);
                MomentBehavior.advCloseClick(BaseInteractPresenter.this.mContext, moment.getMomentId());
                DialogUtil.dismissDialog(dialog);
            }
        })));
    }

    protected void deleteDBMomentAsync(final DbMoment moment) {
        if (!NetworkUtils.isNetworkAvailable(this.mContext)) {
            if (getView() != null) {
                getView().removeFail();
            }
            return;
        }
        if (moment == null) {
            return;
        }
        if (MomentTypeUtil.isOfficialType(moment)) {
            deleteOfficialMoment(moment);
            return;
        }
        this.iMomentServe.deleteServerMomentByKey(AccountInfoServerImpl.getInstance(this.mContext).getWatchAccountInfo().getWatchId(this.mContext), moment)
                .map(new Func1<DeleteResultBean, Boolean>() {
                    @Override
                    public Boolean call(DeleteResultBean result) {
                        if (result != null && result.getResult() == 1) {
                            MomentBehavior.deleteContent(BaseInteractPresenter.this.mContext, MomentBehaviorUtil.createDeleteContentBean(moment));
                            MomentDeleteServe.getInstance(BaseInteractPresenter.this.mContext).delete(moment);
                            return true;
                        }
                        return false;
                    }
                })
                .map(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean success) {
                        ProviderNotifyUtils.notifyPersonalMomentDeleted(moment, BaseInteractPresenter.this.mContext);
                        return success;
                    }
                })
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtil.d(BaseInteractPresenter.this.getLogTag(), "deleteServerMomentByKey:e" + throwable);
                    }

                    @Override
                    public void onNext(Boolean success) {
                        if (!BaseInteractPresenter.this.isViewAttached() || BaseInteractPresenter.this.getView() == null) {
                            return;
                        }
                        if (success.booleanValue()) {
                            LogUtil.d(BaseInteractPresenter.this.getLogTag(), "deleteMoment:moment" + moment);
                            EventBus.getDefault().post(new EventData(1, moment));
                        } else {
                            BaseInteractPresenter.this.getView().removeFail();
                        }
                    }
                });
    }

    private void deleteOfficialMoment(final DbMoment moment) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                DbAdvertCloseRecord record = new DbAdvertCloseRecord();
                record.setAdvertId(moment.getMomentId());
                BaseInteractPresenter.this.getMomentsLocalDataSource().addAdvertCloseRecord(record);
                BaseInteractPresenter.this.deleteLocalOfficialData(moment.getMomentId());
                EventBus.getDefault().post(new EventData(1, moment));
                LogUtil.d(BaseInteractPresenter.this.getLogTag(), "关闭广告--> " + record);
            }
        });
    }

    private void deleteLocalOfficialData(String momentId) {
        LogUtil.d(getLogTag(), "deleteLocalOfficialData " + momentId);
        if (momentId == null || momentId.length() >= 40) {
            return;
        }
        boolean momentDeleted = getMomentsLocalDataSource().deleteMomentByMomentId(getLogTag(), momentId);
        boolean likeDeleted = getMomentsLocalDataSource().deleteLikeDaoByMomentId(momentId);
        boolean commentDeleted = getMomentsLocalDataSource().deleteCommentDaoByMomentId(momentId);
        LogUtil.d(getLogTag(), "deleteMomentByMomentId：" + momentDeleted + ", deleteLikeDaoByMomentId：" + likeDeleted + ", deleteCommentDaoByMomentId：" + commentDeleted);
    }

    public void publishAdvertComment(DbMomentComment comment) {
        LogUtil.d(getLogTag(), "publishAdvertComment#content:" + comment);
        comment.setWatchId(AccountInfoServerImpl.getInstance(this.mContext).getWatchAccountInfo().getWatchId(this.mContext));
        PublishOfficialCommentTask task = new PublishOfficialCommentTask(getMomentsRepository());
        task.setRequestValues(new PublishOfficialCommentTask.RequestValues(comment));
        task.setTaskCallback(new AbsTask.TaskCallback<AbsTask.ResponseValue>() {
            @Override
            protected void onSuccess(AbsTask.ResponseValue responseValue) {
                if (BaseInteractPresenter.this.isViewAttached() && BaseInteractPresenter.this.getView() != null && (responseValue instanceof PublishOfficialCommentTask.ResponseValue)) {
                    PublishOfficialCommentTask.ResponseValue result = (PublishOfficialCommentTask.ResponseValue) responseValue;
                    BaseInteractPresenter.this.getView().publishSuccess(result.getMomentComment(), result.getResult());
                }
            }

            @Override
            protected void onError(AbsTask.ResponseValue responseValue) {
                if (BaseInteractPresenter.this.isViewAttached() && BaseInteractPresenter.this.getView() != null && (responseValue instanceof PublishOfficialCommentTask.ErrorResponseValue)) {
                    String errorCode = ((PublishOfficialCommentTask.ErrorResponseValue) responseValue).getErrorCode();
                    if ("4".equals(errorCode)) {
                        BaseInteractPresenter.this.getView().publishLimited();
                    } else if ("2".equals(errorCode)) {
                        BaseInteractPresenter.this.getView().publishInvalidate();
                    } else {
                        BaseInteractPresenter.this.getView().publishFail(errorCode);
                    }
                }
            }

            @Override
            protected void onError() {
                if (!BaseInteractPresenter.this.isViewAttached() || BaseInteractPresenter.this.getView() == null) {
                    return;
                }
                BaseInteractPresenter.this.getView().publishFail("");
            }
        });
        TaskDispatcher.dispatchImmediately(task);
    }

    public void loadMoreComment(final DbMoment moment) {
        CommentServeImpl.getInstance(this.mContext.getApplicationContext()).searchCommentFromNet(1, 5, moment.getMomentId(), moment.getWatchId(), false)
                .map(new Func1<List<DbMomentComment>, DbMoment>() {
                    @Override
                    public DbMoment call(List<DbMomentComment> comments) {
                        BaseInteractPresenter.this.iMomentServe.addCommentMoment(comments);
                        if (comments != null) {
                            if (moment.getComments() == null) {
                                moment.setComments(new ArrayList<DbMomentComment>());
                            }
                            if (!CollectionUtil.isEmpty(comments)) {
                                moment.setComments(BeanConverterUtil.mapMomentToCommentList(moment, comments));
                                moment.setCommentsTotalCount(moment.getComments().size());
                            } else {
                                moment.setCommentsTotalCount(0);
                            }
                        }
                        return moment;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<DbMoment>() {
                    @Override
                    public void call(DbMoment result) {
                        LogUtil.d(BaseInteractPresenter.this.getLogTag(), "getMomentByMomentId success call: " + result);
                        if (result != null) {
                            BaseInteractPresenter.this.getView().loadCommentSuccess(result);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(BaseInteractPresenter.this.getLogTag(), "getMomentByMomentId throwable call: " + throwable);
                        if (NetworkUtils.isNetworkAvailable(BaseInteractPresenter.this.mContext)) {
                            return;
                        }
                        ToastUtil.showShortCover(BaseInteractPresenter.this.mContext, BaseInteractPresenter.this.mContext.getString(R.string.net_work_exception));
                    }
                });
    }

    private IMomentLikeDataSource getMomentLikeRepository() {
        if (this.iMomentLikeDataSource == null) {
            this.iMomentLikeDataSource = MomentLikeRepository.getInstance(MomentLikeRemoteDataSource.getInstance(this.mContext), MomentLikeLocalDataSource.getInstance(this.mContext));
        }
        return this.iMomentLikeDataSource;
    }

    public void getReminderConfig(Constants.RCType type, int value) {
        ReminderHelper.get(this.mContext).getReminderConfig(type, value);
    }

    public void dealUnExecutedImReminder(final boolean flag) {
        HandlerUtil.runOnBackgroundDelay(new Runnable() {
            @Override
            public void run() {
                ReminderHelper.get(BaseInteractPresenter.this.mContext).dealImReminder(flag);
            }
        }, 1000L);
    }
}