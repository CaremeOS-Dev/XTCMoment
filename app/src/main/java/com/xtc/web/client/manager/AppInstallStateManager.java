package com.xtc.web.client.manager;

import android.content.Context;
import android.content.pm.PackageManager;

import com.xtc.log.LogUtil;
import com.xtc.web.client.data.response.RespAppInstallState;
import com.xtc.web.core.callback.CompletionHandler;
import com.xtc.web.core.data.resp.RespAppState;
import com.xtc.web.core.provider.AppStateHelper;

/** 应用安装状态查询：本地已安装直接返回，否则向应用市场查询。 */
public class AppInstallStateManager {

    private static final String TAG = "AppInstallStateManager";

    /** 查询指定包名的安装状态。 */
    public static void getAppInstallState(Context context, String packageName,
            final CompletionHandler<RespAppInstallState> completionHandler) {
        try {
            if (context.getPackageManager().getPackageInfo(packageName, 0) != null) {
                RespAppInstallState state = new RespAppInstallState();
                state.setState(RespAppInstallState.State.INSTALLED);
                completionHandler.complete(state);
                return;
            }
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.e(TAG, "getAppInstallState-> " + e);
        }
        queryAppStoreState(context, packageName, completionHandler);
    }

    /** 通过应用市场 ContentProvider 查询应用状态。 */
    private static void queryAppStoreState(Context context, String packageName,
            final CompletionHandler<RespAppInstallState> completionHandler) {
        AppStateHelper appStateHelper = AppStateHelper.getInstance(context);
        if (!appStateHelper.checkHasAppInfoProvider()) {
            LogUtil.d(TAG, "当前版本不支持该功能");
            return;
        }
        appStateHelper.registerObserver();
        appStateHelper.setAppStateListener(new AppStateHelper.AppStateListener() {
            @Override
            public void onResultState(RespAppState respAppState) {
                if (respAppState == null) {
                    LogUtil.d(TAG, "服务器请求异常");
                    return;
                }
                RespAppInstallState state = new RespAppInstallState();
                if (RespAppState.Code.CARRIAGE == respAppState.getCode()) {
                    state.setState(RespAppInstallState.State.ATSTORE);
                    completionHandler.complete(state);
                } else if (RespAppState.Code.UNDERCARRIAGE == respAppState.getCode()) {
                    state.setState(RespAppInstallState.State.NOEXIST);
                    completionHandler.complete(state);
                }
            }
        });
        appStateHelper.getAppState(packageName);
    }
}