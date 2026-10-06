package com.xtc.moment.module.personalinfo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.xtc.architecture.mvp.core.MvpBasePresenter;
import com.xtc.bigdata.common.utils.NetUtils;
import com.xtc.database.ormlite.CollectionUtil;
import com.xtc.httplib.okhttp.WatchHttpResultException;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.personalinfo.bigdata.PersonalCenterBehavior;
import com.xtc.moment.module.personalinfo.constant.PersonalInfoConstant;
import com.xtc.moment.module.personalinfo.manager.DressHelper;
import com.xtc.moment.module.personalinfo.manager.DressManage;
import com.xtc.moment.module.personalinfo.net.PersonInfoHttpProxy;
import com.xtc.moment.module.personalinfo.net.bean.GetBadgeResponse;
import com.xtc.moment.module.personalinfo.net.bean.LikeRule;
import com.xtc.moment.module.personalinfo.net.bean.PersonalInfoAndLikeRuleResponse;
import com.xtc.moment.module.personalinfo.net.bean.PersonalInfoResponse;
import com.xtc.moment.module.personalinfo.net.bean.RespPersonalInfoUnite;
import com.xtc.moment.module.prerogative.bean.EmotionsEntity;
import com.xtc.moment.module.prerogative.bean.PersonalResponse;
import com.xtc.moment.module.prerogative.bean.PersonalState;
import com.xtc.moment.module.prerogative.net.PrerogativeHttpProxy;
import com.xtc.moment.util.EventData;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.LongLog;
import com.xtc.moment.util.SharedTool;
import com.xtc.moment.util.StartUtils;
import com.xtc.moment.util.ToastUtil;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.system.AppUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 个人中心（好友主页）的业务 Presenter。
 *
 * <p>负责拉取好友资料、点赞/勋章/状态数据、删除好友，以及把结果通过 {@link IAccountInfoView}
 * 回传给界面。同时订阅联系人删除事件，在好友被删除时通知界面关闭。
 */
public class AccountPresenter extends MvpBasePresenter<IAccountInfoView> {

    private static final String DEAFLUT_ID = "0";
    private static final String SINGLE_CHAT = "3";
    private static final String TAG = "AccountPresenter";

    private final Context context;
    private final String myWatchId;
    private final PersonInfoHttpProxy personInfoHttpProxy;
    private final PrerogativeHttpProxy prerogativeHttpProxy;

    private DressManage dressManage;
    private DressHelper mDressHelper;
    private String mFriendWatchId;
    private boolean mIsSupportBadge;

    public AccountPresenter(Context context, String myWatchId) {
        this.context = context;
        this.personInfoHttpProxy = new PersonInfoHttpProxy(context);
        this.prerogativeHttpProxy = new PrerogativeHttpProxy(context);
        this.myWatchId = myWatchId;
        EventBus.getDefault().register(this);
        this.dressManage = DressManage.getInstance(context);
        this.mIsSupportBadge = ModuleSwitchUtil.queryModuleSwitchByBoolean(context,
                ModuleSwitchConstant.MODULE_PERSONAL_BADGE_SUPPORT, false);
        this.mDressHelper = new DressHelper(context, this.dressManage);
    }

    /** 加载好友主页数据，并上报进入好友主页埋点。 */
    public void initData(String friendWatchId) {
        getPersonalInfoUnite(friendWatchId);
        PersonalCenterBehavior.viewFriendPage(2);
    }

    /**
     * 查询好友正在使用的特权背景图路径。
     *
     * <p>先在特权表情里找到生效中的背景（{@code prerogativeEmotion == 4 && status == 1}），
     * 再用其 id 去个人中心的内容提供者查询背景图地址。
     */
    public void dealPrerogativeBg(String friendWatchId) {
        this.prerogativeHttpProxy.getPersonalData(friendWatchId)
                .map(new Func1<PersonalResponse, String>() {
                    @Override
                    public String call(PersonalResponse personalResponse) {
                        boolean hasPrerogativeBg = false;
                        int prerogativeBgId = 0;
                        if (personalResponse != null && !CollectionUtil.isEmpty(personalResponse.getEmotions())) {
                            List<EmotionsEntity> emotions = personalResponse.getEmotions();
                            for (int index = 0; index < emotions.size(); index++) {
                                EmotionsEntity emotionsEntity = emotions.get(index);
                                if (emotionsEntity.getPrerogativeEmotion() == 4 && emotionsEntity.getStatus() == 1) {
                                    prerogativeBgId = emotionsEntity.getPrerogativeId();
                                    LogUtil.d(TAG, "dealPrerogativeBg onNext: personal Bg = " + emotionsEntity);
                                    hasPrerogativeBg = true;
                                    break;
                                }
                            }
                        }
                        String bgPath = "";
                        if (hasPrerogativeBg) {
                            Cursor cursor = null;
                            try {
                                cursor = AccountPresenter.this.context.getContentResolver().query(
                                        Uri.parse(PersonalInfoConstant.URI_QUERY_PERSONAL_BG_PATH), null, null,
                                        new String[]{String.valueOf(prerogativeBgId)}, null);
                                if (cursor == null) {
                                    LogUtil.w(TAG, "dealPrerogativeBg: cursor is null");
                                    return "";
                                }
                                bgPath = cursor.moveToNext()
                                        ? JSONUtil.fromJSON(cursor.getString(0), String.class) : "";
                            } catch (Exception e) {
                                LogUtil.e(TAG, "dealPrerogativeBg#error: ", e);
                            } finally {
                                if (cursor != null) {
                                    cursor.close();
                                }
                            }
                        }
                        return bgPath;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtil.e(TAG, "onError: ", throwable);
                        if (AccountPresenter.this.getView() != null) {
                            AccountPresenter.this.getView().refreshMainBg("");
                        }
                    }

                    @Override
                    public void onNext(String bgPath) {
                        if (AccountPresenter.this.getView() != null) {
                            AccountPresenter.this.getView().refreshMainBg(bgPath);
                        }
                    }
                });
    }

    /** 绑定装扮服务，用于加载好友头像装扮。 */
    public void bindDressService(DressManage.DressServiceListener dressServiceListener, String friendWatchId) {
        if (dressServiceListener == null || TextUtils.isEmpty(friendWatchId)) {
            return;
        }
        this.mFriendWatchId = friendWatchId;
        this.dressManage.bindDressService(dressServiceListener);
    }

    /** 释放装扮服务连接。 */
    public void releaseDressConnect() {
        DressManage dressManage = this.dressManage;
        if (dressManage == null) {
            return;
        }
        dressManage.releaseDressConnect();
    }

    /** 从装扮服务加载好友头像与昵称。 */
    public void loadDress(DressHelper.OnLoadDataListener onLoadDataListener) {
        if (TextUtils.isEmpty(this.mFriendWatchId)) {
            LogUtil.d(TAG, "mFriendWatchId is empty");
        } else {
            this.mDressHelper.loadHeadAndNickNameFromDress(
                    new ArrayList<>(Arrays.asList(this.mFriendWatchId)), onLoadDataListener);
        }
    }

    /** 跳转到微聊聊天页；若微聊未安装则提示。 */
    public void gotoChat(String friendWatchId) {
        if (!AppUtils.isInstallApp(this.context, "com.xtc.weichat")) {
            LogUtil.i(TAG, "weicaht not install ");
            Context context = this.context;
            ToastUtil.showLong(context, context.getString(R.string.please_install_weichat));
            return;
        }
        Observable.just(friendWatchId)
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String watchId) {
                        String[] queryArgs = {watchId, SINGLE_CHAT};
                        String conversation = null;
                        Cursor cursor = null;
                        try {
                            cursor = AccountPresenter.this.context.getContentResolver().query(
                                    Uri.parse(PersonalInfoConstant.URI_QUERY_SINGLE_CONVERSATION), null, null,
                                    queryArgs, null);
                            if (cursor == null) {
                                LogUtil.w(TAG, "gotoChat: cursor is null");
                                return null;
                            }
                            conversation = cursor.moveToNext() ? cursor.getString(0) : null;
                        } catch (Exception e) {
                            LogUtil.e(TAG, "gotoChat#error: ", e);
                        } finally {
                            if (cursor != null) {
                                cursor.close();
                            }
                        }
                        return conversation;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String conversation) {
                        IAccountInfoView view = AccountPresenter.this.getView();
                        if (view == null || !AccountPresenter.this.isViewAttached()) {
                            return;
                        }
                        view.startToChatActivity(conversation);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(TAG, "gotoChat#error: ", throwable);
                        IAccountInfoView view = AccountPresenter.this.getView();
                        if (view == null || !AccountPresenter.this.isViewAttached()) {
                            return;
                        }
                        view.startToChatActivity(null);
                    }
                });
    }

    /** 拉取好友个人中心聚合数据（资料、签名点赞、勋章、状态）。 */
    public void getPersonalInfoUnite(final String friendWatchId) {
        this.personInfoHttpProxy.getPersonalInfoUnite(friendWatchId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RespPersonalInfoUnite>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.d(TAG, "loadPersonalInfo Completed ");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (AccountPresenter.this.context != null && throwable != null
                                && !TextUtils.isEmpty(throwable.getMessage())
                                && throwable.getMessage().contains("1003")) {
                            ToastUtil.showShortCover(AccountPresenter.this.context,
                                    AccountPresenter.this.context.getString(R.string.frequent_request));
                        }
                        LogUtil.e(TAG, "getPersonalInfoUnite onError:", throwable);
                    }

                    @Override
                    public void onNext(RespPersonalInfoUnite respPersonalInfoUnite) {
                        LongLog.d(TAG, "getPersonalInfoUnite: respPersonalInfoUnite = " + respPersonalInfoUnite);
                        if (respPersonalInfoUnite == null) {
                            return;
                        }
                        AccountPresenter.this.loadPersonalInfo(respPersonalInfoUnite.getGeniusAccount());
                        AccountPresenter.this.updateSignatureAndLike(respPersonalInfoUnite.getPersonalInfo());
                        AccountPresenter.this.initBadge(respPersonalInfoUnite.getSimpleMedal());
                        AccountPresenter.this.initState(respPersonalInfoUnite.getSocializeUser(), friendWatchId);
                    }
                });
    }

    /** 回传好友状态信息。 */
    public void initState(PersonalState personalState, String friendWatchId) {
        LogUtil.d(TAG, "loadPersonalState");
        IAccountInfoView view = getView();
        if (view == null || !isViewAttached() || personalState == null) {
            return;
        }
        personalState.setWatchID(friendWatchId);
        view.updateState(personalState);
    }

    /** 回传好友基础资料。 */
    public void loadPersonalInfo(PersonalInfoResponse personalInfoResponse) {
        LogUtil.d(TAG, "loadPersonalInfo");
        IAccountInfoView view = getView();
        if (view == null || !isViewAttached() || personalInfoResponse == null) {
            return;
        }
        view.updatePersonalInfo(personalInfoResponse);
    }

    /** 回传签名与点赞规则，并缓存点赞规则到本地。 */
    private void updateSignatureAndLike(PersonalInfoAndLikeRuleResponse personalInfoAndLikeRuleResponse) {
        LogUtil.d(TAG, "updateSignatureAndLike");
        if (personalInfoAndLikeRuleResponse != null) {
            final List<LikeRule> likeRule = personalInfoAndLikeRuleResponse.getLikeRule();
            HandlerUtil.runOnBackground(new Runnable() {
                @Override
                public void run() {
                    SharedTool.saveLikesRule(AccountPresenter.this.context, JSONUtil.toJSON(likeRule));
                }
            });
        }
        IAccountInfoView view = getView();
        if (view == null || !isViewAttached()) {
            return;
        }
        view.updateSignatureAndLike(personalInfoAndLikeRuleResponse);
    }

    /** 回传好友勋章列表。 */
    private void initBadge(GetBadgeResponse getBadgeResponse) {
        LogUtil.d(TAG, "initBadge");
        IAccountInfoView view = getView();
        if (view == null || !isViewAttached() || getBadgeResponse == null || !isSupportBadge()) {
            return;
        }
        view.updateBadge(getBadgeResponse.getMedals());
    }

    /** 删除好友，成功后通知界面关闭。 */
    public void deleteFriend(String friendWatchId) {
        if (!NetUtils.isConnected(this.context)) {
            Context context = this.context;
            ToastUtil.showShort(context, context.getString(R.string.net_work_exception));
            return;
        }
        if (getView() != null) {
            getView().setHelperStr(this.context.getString(R.string.loading_delete),
                    this.context.getString(R.string.friend_delete_failed),
                    this.context.getString(R.string.friend_delete_success));
            getView().showLoading();
        }
        this.personInfoHttpProxy.deleteFriend(this.myWatchId, friendWatchId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String result) {
                        LogUtil.i(TAG, "deleteFriend result: " + result);
                        PersonalCenterBehavior.deleteFriend(2);
                        if (AccountPresenter.this.getView() != null) {
                            AccountPresenter.this.getView().deleteFriendSuccess();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(TAG, "deleteFriend error: " + throwable);
                        if ((throwable instanceof WatchHttpResultException)
                                && "000005".equals(((WatchHttpResultException) throwable).serverCode())) {
                            if (AccountPresenter.this.getView() != null) {
                                AccountPresenter.this.getView().deleteFriendSuccess();
                            }
                        } else if (AccountPresenter.this.getView() != null) {
                            if (throwable == null) {
                                AccountPresenter.this.getView().loadFailed("");
                            } else {
                                AccountPresenter.this.getView().loadFailed(throwable.getMessage());
                            }
                        }
                    }
                });
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        EventBus.getDefault().unregister(this);
    }

    /** 联系人被删除时通知界面。 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onContactRemove(EventData eventData) {
        if (eventData.getType() != 7 || !(eventData.getData() instanceof String)
                || TextUtils.isEmpty((String) eventData.getData())) {
            return;
        }
        IAccountInfoView view = getView();
        if (view != null && isViewAttached()) {
            view.onContactRemove((String) eventData.getData());
        }
    }

    public boolean isSupportBadge() {
        LogUtil.d(TAG, "isSupportBadge: supportBadge = " + this.mIsSupportBadge);
        return this.mIsSupportBadge;
    }

    /** 刷新好友状态。 */
    public void refreshPersonalState(String friendWatchId) {
        getPersonalInfoUnite(friendWatchId);
    }

    /** 跳转到个人中心查看状态详情。 */
    public void gotoState(String packageName, PersonalState personalState) {
        Intent intent = new Intent();
        intent.setAction("com.xtc.state.home.StateLookActivity");
        intent.putExtra(Constants.State.STATE_NAME_STR, JSONUtil.toJSON(personalState));
        StartUtils.startActivity(this.context, packageName, "com.xtc.state.home.StateLookActivity", intent);
    }

    /** 跳转到应用市场下载个人中心。 */
    public void gotoDownloadAPP(String packageName) {
        if (packageName == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setData(Uri.parse(Constants.State.APP_UPDATE_DATA + packageName));
        try {
            this.context.startActivity(intent);
        } catch (Exception e) {
            LogUtil.e("go to download APP error: ", e);
            Context context = this.context;
            ToastUtil.showShort(context, context.getResources().getString(R.string.app_update_version_low));
        }
    }
}