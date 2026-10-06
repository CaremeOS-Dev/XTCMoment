package com.xtc.moment.module.barrage;

import android.content.Context;

import com.xtc.architecture.mvp.core.MvpBasePresenter;
import com.xtc.dispatch.TaskDispatcher;
import com.xtc.dispatch.task.AbsTask;
import com.xtc.funlist.util.FuncUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.moment.data.IMomentsDataSource;
import com.xtc.moment.data.MomentsRepository;
import com.xtc.moment.data.local.MomentsLocalDataSource;
import com.xtc.moment.data.remote.MomentsRemoteDataSource;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.net.bean.SearchGiftRequest;
import com.xtc.moment.net.bean.SendGiftRequest;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.tasks.domain.usecase.SearchGiftTask;
import com.xtc.moment.tasks.domain.usecase.SendGiftTask;
import com.xtc.moment.util.FunPhotoUtils;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.MomentFileUtils;
import com.xtc.moment.util.SharedTool;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;

import java.io.File;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 弹幕页业务处理：拉取弹幕数据、发送礼物、检查拍同款开关。
 */
public class BarragePresenter extends MvpBasePresenter<IBarrageView> {

    private static final String TAG = "BarragePresenter";

    private Context mContext;
    private IMomentsDataSource momentsDataSource;
    private final MomentsLocalDataSource momentsLocalDataSource;
    private final MomentsRemoteDataSource momentsRemoteDataSource;
    private final String watchId;

    public BarragePresenter(Context context) {
        this.mContext = context;
        this.momentsLocalDataSource = MomentsLocalDataSource.getInstance(context);
        this.momentsRemoteDataSource = MomentsRemoteDataSource.getInstance(context);
        this.momentsDataSource = MomentsRepository.getInstance(this.momentsRemoteDataSource,
                this.momentsLocalDataSource);
        this.watchId = AccountInfoServerImpl.getInstance(context).getWatchAccountInfo().getWatchId(context);
        copyFile();
    }

    /** 首次进入时把弹幕动画资源从 assets 复制到本地。 */
    private void copyFile() {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                if (BarragePresenter.this.mContext == null
                        || SharedTool.isCopyAssets(BarragePresenter.this.mContext)) {
                    return;
                }
                boolean copied = MomentFileUtils.copyFileFromAssets(BarragePresenter.this.mContext, "video",
                        MomentFileUtils.getResourcePath(BarragePresenter.this.mContext) + File.separator);
                LogUtil.d(TAG, "copyFile: " + copied);
                if (copied) {
                    LogUtil.d(TAG, "copyFile: copy Success ; save state = "
                            + SharedTool.saveCopyAssetsState(BarragePresenter.this.mContext, true));
                }
            }
        });
    }

    public void getBarrageData(final DbMoment moment) {
        SearchGiftRequest request = new SearchGiftRequest();
        request.setCount(20);
        request.setFriendId(this.watchId);
        request.setMomentId(moment.getMomentId());
        request.setWatchId(moment.getWatchId());
        SearchGiftTask searchGiftTask = new SearchGiftTask(this.momentsDataSource, this.mContext);
        SearchGiftTask.RequestValues requestValues = new SearchGiftTask.RequestValues(request);
        requestValues.setMoment(moment);
        searchGiftTask.setRequestValues(requestValues);
        searchGiftTask.setTaskCallback(new AbsTask.TaskCallback<AbsTask.ResponseValue>() {
            @Override
            protected void onSuccess(AbsTask.ResponseValue responseValue) {
                if (BarragePresenter.this.getView() == null) {
                    return;
                }
                SearchGiftTask.ResponseValue searchResponse = (SearchGiftTask.ResponseValue) responseValue;
                LogUtil.e(TAG, "onSuccess: " + searchResponse.getMomentComments());
                LogUtil.e(TAG, "onSuccess: " + searchResponse.getSearchGiftResponse());
                BarragePresenter.this.getView().getBarrageDataSuccess(searchResponse.getMomentComments(),
                        searchResponse.getSearchGiftResponse());
            }

            @Override
            protected void onError(AbsTask.ResponseValue responseValue) {
                if (BarragePresenter.this.getView() == null) {
                    return;
                }
                BarragePresenter.this.getView().getBarrageDataFail(moment.getComments());
            }

            @Override
            protected void onError() {
                if (BarragePresenter.this.getView() == null) {
                    return;
                }
                BarragePresenter.this.getView().getBarrageDataFail(moment.getComments());
            }
        });
        TaskDispatcher.dispatchImmediately(searchGiftTask);
    }

    public void sendGift(DbMoment moment, final int giftType, final boolean isSend) {
        LogUtil.d(TAG, "sendGift: " + giftType + " isSend: " + isSend);
        SendGiftRequest request = new SendGiftRequest(moment.getMomentId(), moment.getWatchId(),
                this.watchId, giftType);
        SendGiftTask sendGiftTask = new SendGiftTask(this.momentsDataSource);
        sendGiftTask.setRequestValues(new SendGiftTask.RequestValues(request, isSend));
        sendGiftTask.setTaskCallback(new AbsTask.TaskCallback<AbsTask.ResponseValue>() {
            @Override
            protected void onSuccess(AbsTask.ResponseValue responseValue) {
                if (BarragePresenter.this.getView() == null) {
                    return;
                }
                BarragePresenter.this.getView().sendGiftSuccess(giftType, isSend);
            }

            @Override
            protected void onError(AbsTask.ResponseValue responseValue) {
                if (BarragePresenter.this.getView() == null) {
                    return;
                }
                BarragePresenter.this.getView().sendGiftFail();
            }

            @Override
            protected void onError() {
                if (BarragePresenter.this.getView() == null) {
                    return;
                }
                BarragePresenter.this.getView().sendGiftFail();
            }
        });
        TaskDispatcher.dispatchImmediately(sendGiftTask);
    }

    /** 检查拍同款入口是否展示。 */
    public void checkModuleSwitch() {
        Observable.just(false)
                .map(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean ignored) {
                        boolean videoSwitch = ModuleSwitchUtil.queryModuleSwitchByBoolean(
                                BarragePresenter.this.mContext,
                                ModuleSwitchConstant.MODULE_SWITCH_MOMENT_SEND_VIDEO_FUNCTION, false);
                        LogUtil.d(TAG, "call: checkModuleSwitch videoSwitch = " + videoSwitch);
                        boolean support = videoSwitch && FuncUtil.supportFunShortVideoTakeSame()
                                && FunPhotoUtils.isAvailable(BarragePresenter.this.mContext);
                        return support;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean support) {
                        if (!support || BarragePresenter.this.getView() == null) {
                            return;
                        }
                        BarragePresenter.this.getView().showBtn();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(TAG, "call: checkModuleSwitch ", throwable);
                    }
                });
    }
}