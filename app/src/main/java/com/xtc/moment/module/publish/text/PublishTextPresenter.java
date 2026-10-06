package com.xtc.moment.module.publish.text;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;

import com.xtc.architecture.mvp.core.MvpPresenter;
import com.xtc.dispatch.TaskDispatcher;
import com.xtc.dispatch.task.AbsTask;
import com.xtc.log.LogUtil;
import com.xtc.moment.behavior.DigitalBigDateSender;
import com.xtc.moment.behavior.DigitalManager;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.moment.data.IMomentsDataSource;
import com.xtc.moment.data.MomentsRepository;
import com.xtc.moment.data.local.MomentsLocalDataSource;
import com.xtc.moment.data.remote.MomentsRemoteDataSource;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.prerogative.DbMomentPrerogativeBackground;
import com.xtc.moment.module.bean.FriendsVisibleBean;
import com.xtc.moment.module.bean.PoiBean;
import com.xtc.moment.net.bean.Moment;
import com.xtc.moment.prerogative.MomentPrerogativeServeImpl;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.serve.IMomentServe;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.moment.tasks.domain.usecase.PublishMomentTask;
import com.xtc.moment.third.behavior.MomentBehavior;
import com.xtc.moment.util.BeanConverterUtil;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 文字动态发布 presenter：处理心情/状态、纯位置、纯文字三类发布请求，
 * 并在发布成功后回写本地库、上报埋点。
 */
public class PublishTextPresenter extends MvpPresenter<IPublishTextView> {

    private static final String TAG = PublishTextPresenter.class.getSimpleName();

    /** 发布被限制时的服务端错误码。 */
    private static final String ERROR_CODE_PUBLISH_LIMITED = "000060";
    /** 发布内容不合法时的服务端错误码。 */
    private static final String ERROR_CODE_PUBLISH_INVALID = "000061";
    /** 发布失败埋点使用的错误码。 */
    private static final String PUBLIC_RESULT_ERROR_CODE = "9005";

    private final Context context;
    private final IMomentServe momentServe;
    private final IMomentsDataSource momentsDataSource;
    private final MomentsLocalDataSource momentsLocalDataSource;
    private final MomentsRemoteDataSource momentsRemoteDataSource;

    public PublishTextPresenter(Context context) {
        this.context = context;
        this.momentsRemoteDataSource = MomentsRemoteDataSource.getInstance(context);
        this.momentsLocalDataSource = MomentsLocalDataSource.getInstance(context);
        this.momentsDataSource = MomentsRepository.getInstance(this.momentsRemoteDataSource, this.momentsLocalDataSource);
        this.momentServe = MomentServeImpl.getInstance(context);
    }

    /**
     * 发布心情/状态类动态：直接调用 IMomentServe 的发布接口。
     */
    public void publishMoodOrStateMoment(int type, String resource, int resourceId, String content,
                                         PoiBean poiBean, FriendsVisibleBean visibleBean) {
        DigitalManager.getInstance().getDigitalEntity().momentType = String.valueOf(type);
        this.momentServe.publishMoment(type, resource, resourceId, content, poiBean, visibleBean)
                .subscribeOn(Schedulers.io())
                .subscribe(new PublishResultSubscriber(SystemClock.elapsedRealtime(), visibleBean));
    }

    /**
     * 发布纯位置动态：先查询 LBS 开关，再决定是否携带位置信息发布。
     */
    public void publishMoment(final PoiBean poiBean, final FriendsVisibleBean visibleBean) {
        final long startPublishTime = SystemClock.elapsedRealtime();
        DigitalManager.getInstance().getDigitalEntity().momentType = String.valueOf(2);
        Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ModuleSwitchUtil.queryModuleSwitchByBoolean(
                        PublishTextPresenter.this.context, ModuleSwitchConstant.MODULE_SWITCH_LBS_PUBLISH, false);
            }
        })
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<Boolean, Observable<Moment>>() {
                    @Override
                    public Observable<Moment> call(Boolean lbsSwitchEnabled) {
                        return PublishTextPresenter.this.momentServe.publishMoment(2, null, 0,
                                poiBean.getAddressDesc(), lbsSwitchEnabled.booleanValue() ? poiBean : null, visibleBean);
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new PublishResultSubscriber(startPublishTime, visibleBean));
    }

    /**
     * 发布纯文字动态（带资源类型与资源 id）。
     */
    public void publishMoment(String content, int resourceId, int type, PoiBean poiBean, FriendsVisibleBean visibleBean) {
        publishMomentText(content, resourceId, type, poiBean, visibleBean);
    }

    /**
     * 通过 {@link PublishMomentTask} 发布文字动态，携带位置与可见范围参数。
     */
    public void publishMomentText(String content, int resourceId, int type, PoiBean poiBean,
                                  final FriendsVisibleBean visibleBean) {
        PublishMomentTask publishMomentTask = new PublishMomentTask(this.momentsDataSource, this.context);
        PublishMomentTask.RequestValues requestValues = new PublishMomentTask.RequestValues(
                AccountInfoServerImpl.getInstance(this.context).getWatchAccountInfo().getWatchId(this.context),
                0, null, content, resourceId, null,
                MomentPrerogativeServeImpl.getInstance(this.context).getCurrentUseBackgroundEmotionId());
        if (poiBean != null) {
            requestValues.setLocation(poiBean.getCity() + poiBean.getAddressDesc());
            requestValues.setLocationType(poiBean.getLocationType());
            requestValues.setLatitude(Double.parseDouble(poiBean.getLocation().getLatitude()));
            requestValues.setLongitude(Double.parseDouble(poiBean.getLocation().getLongitude()));
        }
        if (visibleBean != null) {
            requestValues.setPermissionType(visibleBean.getType());
            requestValues.setLookupIds(visibleBean.getFriends());
        }
        publishMomentTask.setRequestValues(requestValues);
        publishMomentTask.setTaskCallback(new AbsTask.TaskCallback<AbsTask.ResponseValue>() {
            @Override
            protected void onSuccess(AbsTask.ResponseValue responseValue) {
                if (!PublishTextPresenter.this.isViewAttached() || PublishTextPresenter.this.getView() == null
                        || !(responseValue instanceof PublishMomentTask.ResponseValue)) {
                    return;
                }
                DbMoment dbMoment = ((PublishMomentTask.ResponseValue) responseValue).getDbMoment();
                if (visibleBean != null) {
                    dbMoment.setPermissionType(visibleBean.getType());
                }
                PublishTextPresenter.this.getView().publishSuccess(dbMoment);
            }

            @Override
            protected void onError(AbsTask.ResponseValue responseValue) {
                if (!PublishTextPresenter.this.isViewAttached() || PublishTextPresenter.this.getView() == null
                        || !(responseValue instanceof PublishMomentTask.ErrorResponseValue)) {
                    return;
                }
                String errorCode = ((PublishMomentTask.ErrorResponseValue) responseValue).getErrorCode();
                if (ERROR_CODE_PUBLISH_LIMITED.equals(errorCode)) {
                    PublishTextPresenter.this.getView().publishLimited();
                } else if (ERROR_CODE_PUBLISH_INVALID.equals(errorCode)) {
                    PublishTextPresenter.this.getView().publishInvalidate();
                } else {
                    PublishTextPresenter.this.getView().publishFail(errorCode);
                }
            }

            @Override
            protected void onError() {
                if (!PublishTextPresenter.this.isViewAttached() || PublishTextPresenter.this.getView() == null) {
                    return;
                }
                PublishTextPresenter.this.getView().publishFail("");
            }
        });
        TaskDispatcher.dispatchImmediately(publishMomentTask);
    }

    /**
     * 发布结果订阅者：记录埋点耗时，落库并回调视图。
     */
    private final class PublishResultSubscriber extends Subscriber<Moment> {

        private final long startPublishTime;
        private final FriendsVisibleBean visibleBean;

        PublishResultSubscriber(long startPublishTime, FriendsVisibleBean visibleBean) {
            this.startPublishTime = startPublishTime;
            this.visibleBean = visibleBean;
        }

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(final Throwable throwable) {
            LogUtil.e(TAG, "publishMoment ErrorMessage =", throwable);
            DigitalBigDateSender.onPublicResult(context, DigitalManager.getInstance().getDigitalEntity(),
                    false, PUBLIC_RESULT_ERROR_CODE, throwable);
            HandlerUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    handlePublishError(throwable);
                }
            });
        }

        @Override
        public void onNext(Moment moment) {
            long transformStartTime = SystemClock.elapsedRealtime();
            DigitalManager.getInstance().getDigitalEntity().publicTime =
                    String.valueOf(transformStartTime - this.startPublishTime);
            final DbMoment dbMoment = BeanConverterUtil.convertToDbMoment(moment);
            if (this.visibleBean != null) {
                dbMoment.setPermissionType(this.visibleBean.getType());
                MomentBehavior.upLoadVisibleRange(context, 1, this.visibleBean.getType());
            }
            if (dbMoment.getEmotionId() != 0 && TextUtils.isEmpty(dbMoment.getMomentBgPath())) {
                DbMomentPrerogativeBackground background = MomentPrerogativeServeImpl.getInstance(context)
                        .getPrerogativeBackgroundByEmotionId(dbMoment.getEmotionId());
                if (background != null && !TextUtils.isEmpty(background.getLocalEmotionPath())) {
                    dbMoment.setMomentBgPath(background.getLocalEmotionPath());
                }
                LogUtil.i(TAG, "updateLocalMoment" + background.getLocalEmotionPath());
            }
            HandlerUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    if (PublishTextPresenter.this.isViewAttached() && PublishTextPresenter.this.getView() != null) {
                        PublishTextPresenter.this.getView().publishSuccess(dbMoment);
                    }
                }
            });
            momentServe.insertMomentByMomentId(dbMoment);
            DigitalManager.getInstance().getDigitalEntity().transformTime =
                    String.valueOf(SystemClock.elapsedRealtime() - transformStartTime);
            DigitalBigDateSender.onPublicResult(context.getApplicationContext(),
                    DigitalManager.getInstance().getDigitalEntity(), true, "", null);
        }

        private void handlePublishError(Throwable throwable) {
            if (!PublishTextPresenter.this.isViewAttached() || PublishTextPresenter.this.getView() == null) {
                return;
            }
            String message = throwable.getMessage();
            if (ERROR_CODE_PUBLISH_LIMITED.equals(message)) {
                PublishTextPresenter.this.getView().publishLimited();
            } else if (ERROR_CODE_PUBLISH_INVALID.equals(message)) {
                PublishTextPresenter.this.getView().publishInvalidate();
            } else {
                PublishTextPresenter.this.getView().publishFail(message);
            }
        }
    }
}