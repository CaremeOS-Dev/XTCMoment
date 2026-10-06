package com.xtc.aitext.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.aitext.R;
import com.xtc.aitext.activity.view.IAIEditedView;
import com.xtc.aitext.bean.AICreatStatusBean;
import com.xtc.aitext.bean.AIDataBean;
import com.xtc.aitext.bean.ButtonDescriptionBean;
import com.xtc.aitext.bean.CreatBackBean;
import com.xtc.aitext.bean.CreatTextBody;
import com.xtc.aitext.bean.MainDataBody;
import com.xtc.aitext.bean.ObtainTimeBean;
import com.xtc.aitext.bean.SuccessDescription;
import com.xtc.aitext.bean.UserAccessBean;
import com.xtc.aitext.behavior.AIBehaviorUtil;
import com.xtc.aitext.manager.AITextManager;
import com.xtc.aitext.net.AINetErrorAction;
import com.xtc.aitext.net.http.AITextHttpProxy;
import com.xtc.aitext.util.AIModuleUtil;
import com.xtc.aitext.util.AITextRxUtils;
import com.xtc.architecture.mvp.core.MvpPresenter;
import com.xtc.database.ormlite.CollectionUtil;
import com.xtc.log.LogUtil;
import com.xtc.ui.widget.toast.view.ToastUtil;
import com.xtc.utils.system.NetworkUtils;

import java.util.List;
import java.util.Objects;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * AI 编辑页 Presenter，负责主页数据查询、文案创建与权益领取。
 */
public class AIEditedPresenter extends MvpPresenter<IAIEditedView> {

    private static final String TAG = "ai_text_AIEditedPresenter";

    private final Context context;

    public AIEditedPresenter(Context context) {
        this.context = context;
    }

    /** 查询主页数据。 */
    public void queryData() {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            return;
        }
        MainDataBody body = new MainDataBody();
        body.setClientType(AITextManager.getInstance(context).getConfig().getClientType());
        AITextHttpProxy.getInstance(context).queryHomeData(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<AIDataBean>() {
                    @Override
                    public void call(AIDataBean dataBean) {
                        LogUtil.d(TAG, "queryDataFromNet: " + dataBean);
                        if (dataBean == null) {
                            if (getView() != null) {
                                getView().showEmpty();
                            }
                        } else {
                            if (getView() != null) {
                                getView().showHomeData(dataBean);
                            }
                            autoObtainDefaultAccess(dataBean.getUserAccessVoList());
                        }
                    }
                }, new AINetErrorAction(getClass(), new AINetErrorAction.ErrorCallback() {
                    @Override
                    public void onError(Throwable throwable) {
                        if (getView() != null) {
                            getView().showEmpty();
                        }
                    }
                }, "queryDataFromNet"));
    }

    /** 创建 AI 文案。 */
    public void createText(String aiText, Integer styleId, int remainTimes) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            ToastUtil.showShortCover(context, context.getString(R.string.string_toast_net_error));
            return;
        }
        if (remainTimes <= 0) {
            ToastUtil.showShortCover(context, context.getString(R.string.string_toast_no_available_number));
            return;
        }
        AIModuleUtil.notifyCreateStatus(context, new AICreatStatusBean(4, 0L));
        CreatTextBody body = new CreatTextBody();
        body.setAiText(aiText);
        body.setAiStyleId(styleId);
        body.setClientType(AITextManager.getInstance(context).getConfig().getClientType());
        AITextHttpProxy.getInstance(context).createText(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CreatBackBean>() {
                    @Override
                    public void call(CreatBackBean creatBackBean) {
                        LogUtil.d(TAG, "startCreateText: creatBackBean = " + creatBackBean);
                        if (getView() == null || creatBackBean == null) {
                            return;
                        }
                        if (Objects.equals(creatBackBean.getCallResult(), 0)) {
                            AIModuleUtil.notifyCreateStatus(context, new AICreatStatusBean(0, 0L, creatBackBean.getId()));
                        } else if (Objects.equals(creatBackBean.getCallResult(), 1)) {
                            getView().showRemainTimes(creatBackBean.getRemainTimes());
                            AIModuleUtil.notifyCreateStatus(context, new AICreatStatusBean(1, creatBackBean.getWaitTime(), creatBackBean.getId()));
                        }
                    }
                }, new AINetErrorAction(getClass(), new AINetErrorAction.ErrorCallback() {
                    @Override
                    public void onError(Throwable throwable) {
                        AIModuleUtil.notifyCreateStatus(context, new AICreatStatusBean(5, 0L));
                    }
                }, "startCreateText"));
    }

    /** 判断是否需要弹出领取弹窗。 */
    public boolean needShowAwardDialog(UserAccessBean userAccessBean) {
        if (userAccessBean == null || userAccessBean.getButtonDescription() == null) {
            LogUtil.i(TAG, "isCanShowAwardDialog illegality null");
            return false;
        }
        if (userAccessBean.getButtonDescription().getObtainStatus() == 0) {
            return false;
        }
        if (userAccessBean.getButtonDescription().getObtainStatus() == 2) {
            ToastUtil.showShortCover(context, userAccessBean.getButtonDescription().getObtainedTips());
            return false;
        }
        if (userAccessBean.getButtonDescription().getWatchBoxContent() == null) {
            obtainForTimes(userAccessBean, true);
            return true;
        }
        getView().showUserAccessWatchBox(userAccessBean);
        return false;
    }

    private void autoObtainDefaultAccess(List<UserAccessBean> accessList) {
        if (CollectionUtil.isEmpty(accessList)) {
            return;
        }
        for (UserAccessBean userAccessBean : accessList) {
            if (userAccessBean.getButtonDescription() != null
                    && userAccessBean.getButtonDescription().getObtainStatus() == 1
                    && userAccessBean.getButtonDescription().getDefaultCall() == 1) {
                obtainForTimes(userAccessBean, false);
            }
        }
    }

    /** 领取次数。 */
    public void obtainForTimes(final UserAccessBean userAccessBean, final boolean showDialogOnFail) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            ToastUtil.showShortCover(context, context.getString(R.string.string_toast_net_error));
            return;
        }
        AITextHttpProxy.getInstance(context).obtainTime(userAccessBean.getAccessType(), new MainDataBody())
                .map(new Func1<ObtainTimeBean, ObtainTimeBean>() {
                    @Override
                    public ObtainTimeBean call(ObtainTimeBean obtainTimeBean) {
                        return mergeObtainResult(obtainTimeBean, userAccessBean);
                    }
                })
                .subscribeOn(AITextRxUtils.getSingleScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ObtainTimeBean>() {
                    @Override
                    public void call(ObtainTimeBean obtainTimeBean) {
                        if (getView() == null || obtainTimeBean == null) {
                            return;
                        }
                        LogUtil.d(TAG, "obtainForTimes:obtainTimeBean = " + obtainTimeBean);
                        int obtainResult = obtainTimeBean.getObtainResult();
                        SuccessDescription successDescription = obtainTimeBean.getSuccessDescription();
                        ButtonDescriptionBean buttonDescription = obtainTimeBean.getButtonDescription();
                        if (Objects.equals(obtainResult, 0)) {
                            ToastUtil.showShortCover(context, obtainTimeBean.getFailedMsg());
                            if (showDialogOnFail) {
                                getView().showUserAccess(userAccessBean);
                            }
                            return;
                        }
                        if (Objects.equals(obtainResult, 1)) {
                            AIBehaviorUtil.reportObtainMethod(userAccessBean.getAccessName());
                            if (successDescription != null) {
                                if (successDescription.getAction() == 2) {
                                    ToastUtil.showShortCover(context, successDescription.getSuccessMsg());
                                } else if (successDescription.getAction() == 1) {
                                    getView().showToast(successDescription.getSuccessMsg());
                                }
                            }
                            getView().showRemainTimes(obtainTimeBean.getSuccessTime() + getView().getTimesSuffix());
                            getView().showUserAccess(userAccessBean);
                            return;
                        }
                        if (Objects.equals(obtainResult, 2)) {
                            if (buttonDescription != null && buttonDescription.getWatchBoxContent() != null) {
                                getView().showWatchBoxContent(buttonDescription.getWatchBoxContent(), userAccessBean);
                            }
                            getView().showUserAccess(userAccessBean);
                        }
                    }
                }, new AINetErrorAction(getClass(), new AINetErrorAction.ErrorCallback() {
                    @Override
                    public void onError(Throwable throwable) {
                        ToastUtil.showShortCover(context, context.getString(R.string.string_network_anomaly));
                        if (getView() != null) {
                            userAccessBean.getButtonDescription().setObtainStatus(1);
                            getView().showUserAccess(userAccessBean);
                        }
                    }
                }, "obtainForTimes"));
    }

    private ObtainTimeBean mergeObtainResult(ObtainTimeBean obtainTimeBean, UserAccessBean userAccessBean) {
        if (obtainTimeBean != null && userAccessBean != null) {
            if (!TextUtils.isEmpty(obtainTimeBean.getAccessDescription())) {
                userAccessBean.setAccessDescription(obtainTimeBean.getAccessDescription());
            }
            ButtonDescriptionBean newButton = obtainTimeBean.getButtonDescription();
            ButtonDescriptionBean oldButton = userAccessBean.getButtonDescription();
            if (newButton != null && oldButton != null) {
                if (!TextUtils.isEmpty(newButton.getObtainDescription())) {
                    oldButton.setObtainDescription(newButton.getObtainDescription());
                }
                oldButton.setObtainStatus(newButton.getObtainStatus());
                if (newButton.getWatchBoxContent() != null) {
                    oldButton.setWatchBoxContent(newButton.getWatchBoxContent());
                }
            }
        }
        return obtainTimeBean;
    }
}