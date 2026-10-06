package com.xtc.shareapi.share.manager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.xtc.shareapi.R;
import com.xtc.shareapi.share.bean.JumpToMomentRequest;
import com.xtc.shareapi.share.communication.BaseRequest;
import com.xtc.shareapi.share.communication.BaseResponse;
import com.xtc.shareapi.share.communication.SendMessageToXTC;
import com.xtc.shareapi.share.constant.OpenApiConstant;
import com.xtc.shareapi.share.utils.BitmapUtil;
import com.xtc.shareapi.share.utils.ShareUtil;
import com.xtc.shareapi.share.view.PopWindowManager;
import com.xtc.system.account.WatchAccountBase;
import com.xtc.utils.system.WatchModelUtil;

import java.util.UUID;

/**
 * 分享消息管理器，对外提供分享到微聊/好友圈、静默分享与跳转好友圈等入口。
 */
public class ShareMessageManager {

    private static final String TAG = OpenApiConstant.TAG + ShareMessageManager.class.getSimpleName();

    /** 宿主版本查询失败时返回的版本号。 */
    private static final int VERSION_QUERY_FAILED = 0;
    /** 宿主分享 SDK 版本 1。 */
    private static final int VERSION_ONE = 1;

    private Context context;
    private String appName;
    private byte[] appIcon;

    public ShareMessageManager(Context context) {
        this.context = context;
    }

    /** 发起分享请求（单请求）。 */
    public void sendRequestToXTC(BaseRequest baseRequest, String appKey) {
        Context context = this.context;
        if (context == null) {
            Log.e(TAG, "share to xtc the context is null!");
            return;
        }
        if (baseRequest == null) {
            Log.d(TAG, "baseRequest must not null!");
            return;
        }
        if (!(baseRequest instanceof SendMessageToXTC.Request)) {
            Log.d(TAG, "baseRequest must instanceof SendMessageToXTC.Request!");
            return;
        }
        if (!(context instanceof Activity) || TextUtils.isEmpty(context.getPackageName())
                || TextUtils.isEmpty(this.context.getClass().getName())) {
            Log.e(TAG, "you should use an activity context here!");
            return;
        }
        if (!ShareUtil.isConnected(this.context)) {
            Log.d(TAG, "network is not connected");
            ShareUtil.startTargetActivity(this.context, BaseResponse.Code.NETWORK_ERROR, BaseResponse.Desc.NETWORK_ERROR);
            return;
        }
        BaseResponse checkResult = baseRequest.checkArgs();
        if (checkResult == null || checkResult.getCode() != BaseResponse.Code.OK) {
            Log.d(TAG, "the argument request check fail");
            ShareUtil.startTargetActivity(this.context, BaseResponse.Code.ARGUMENT_ERROR, "the argument request check fail");
            return;
        }
        baseRequest.setTransaction(System.currentTimeMillis() + String.valueOf(UUID.randomUUID()));
        int hostSdkVersion = ShareUtil.getHostSdkVersion(this.context, OpenApiConstant.App.LAUNCHER);
        if (hostSdkVersion == VERSION_QUERY_FAILED) {
            Log.d(TAG, "check sdk version fail");
            ShareUtil.startTargetActivity(this.context, BaseResponse.Code.NON_SUPPORT, "current host not support share!");
            return;
        }
        SendMessageToXTC.Request request = (SendMessageToXTC.Request) baseRequest;
        request.getMessage().getShareObject();
        if (hostSdkVersion == VERSION_ONE) {
            new StrategyVersionOne(this.context, appName, appIcon).share(request, appKey);
        } else {
            new StrategyVersionTwo(this.context, appName, appIcon).share(request, appKey);
        }
    }

    /** 发起分享请求（双请求，用于选择场景）。 */
    public void sendRequestToXTC(BaseRequest chatRequest, BaseRequest momentRequest, String appKey) {
        Context context = this.context;
        if (context == null) {
            Log.e(TAG, "share to xtc the context is null!");
            return;
        }
        if (chatRequest == null || momentRequest == null) {
            Log.d(TAG, "baseRequest must not null!");
            return;
        }
        if (!(chatRequest instanceof SendMessageToXTC.Request) || !(momentRequest instanceof SendMessageToXTC.Request)) {
            Log.d(TAG, "baseRequest must instanceof SendMessageToXTC.Request!");
            return;
        }
        if (!(context instanceof Activity) || TextUtils.isEmpty(context.getPackageName())
                || TextUtils.isEmpty(this.context.getClass().getName())) {
            Log.e(TAG, "you should use an activity context here!");
            return;
        }
        if (!ShareUtil.isConnected(this.context)) {
            Log.d(TAG, "network is not connected");
            ShareUtil.startTargetActivity(this.context, BaseResponse.Code.NETWORK_ERROR, BaseResponse.Desc.NETWORK_ERROR);
            return;
        }
        BaseResponse chatCheck = chatRequest.checkArgs();
        BaseResponse momentCheck = momentRequest.checkArgs();
        if (chatCheck == null || chatCheck.getCode() != BaseResponse.Code.OK
                || momentCheck == null || momentCheck.getCode() != BaseResponse.Code.OK) {
            Log.d(TAG, "the argument request check fail");
            ShareUtil.startTargetActivity(this.context, BaseResponse.Code.ARGUMENT_ERROR, "the argument request check fail");
            return;
        }
        chatRequest.setTransaction(System.currentTimeMillis() + String.valueOf(UUID.randomUUID()));
        momentRequest.setTransaction(System.currentTimeMillis() + String.valueOf(UUID.randomUUID()));
        int hostSdkVersion = ShareUtil.getHostSdkVersion(this.context, OpenApiConstant.App.LAUNCHER);
        SendMessageToXTC.Request request = (SendMessageToXTC.Request) momentRequest;
        request.getMessage().getShareObject();
        if (hostSdkVersion == VERSION_QUERY_FAILED) {
            Log.d(TAG, "check sdk version fail");
            ShareUtil.startTargetActivity(this.context, BaseResponse.Code.NON_SUPPORT, "current host not support share!");
        } else if (hostSdkVersion == VERSION_ONE) {
            PopWindowManager.getInstance(this.context).showChooseSceneWindow(appKey, (SendMessageToXTC.Request) chatRequest,
                    request, new StrategyVersionOne(this.context, appName, appIcon));
        } else {
            PopWindowManager.getInstance(this.context).showChooseSceneWindow(appKey, (SendMessageToXTC.Request) chatRequest,
                    request, new StrategyVersionTwo(this.context, appName, appIcon));
        }
    }

    /** 静默分享到微聊。 */
    public void sendRequestToXTC(BaseRequest baseRequest, String appKey, int requestCode, String accountId,
                                 IShareCallback callback) throws RemoteException {
        if (this.context == null || baseRequest == null || TextUtils.isEmpty(appKey)) {
            callback.onResult(BaseResponse.Code.ARGUMENT_ERROR, "context and baseRequest and appKey add accountId must not null!");
            return;
        }
        if (!(baseRequest instanceof SendMessageToXTC.Request)) {
            callback.onResult(BaseResponse.Code.ARGUMENT_ERROR, "baseRequest argument must instanceof SendMessageToXTC.Request!");
            return;
        }
        if (!ShareUtil.isConnected(this.context)) {
            callback.onResult(BaseResponse.Code.NETWORK_ERROR, "network not connect!");
            return;
        }
        if (!ShareUtil.isAppInWhiteList(this.context, OpenApiConstant.App.CHAT_PACKAGE_NAME)) {
            callback.onResult(BaseResponse.Code.APP_NOT_PERMISSION, "app not permission!");
            return;
        }
        BaseResponse checkResult = baseRequest.checkArgs();
        if (checkResult == null || checkResult.getCode() != BaseResponse.Code.OK) {
            callback.onResult(BaseResponse.Code.ARGUMENT_ERROR, "argument check error!");
            return;
        }
        int hostSdkVersion = ShareUtil.getHostSdkVersion(this.context, OpenApiConstant.App.LAUNCHER);
        ShareUtil.getHostSdkVersion(this.context, OpenApiConstant.App.CHAT_PACKAGE_NAME);
        if (hostSdkVersion <= VERSION_ONE) {
            Log.d(TAG, "check sdk version fail");
            callback.onResult(BaseResponse.Code.NON_SUPPORT, "current host not support!");
            return;
        }
        SendMessageToXTC.Request request = (SendMessageToXTC.Request) baseRequest;
        request.getMessage().getShareObject();
        baseRequest.setTransaction(System.currentTimeMillis() + String.valueOf(UUID.randomUUID()));
        new StrategyVersionTwo(this.context, appName, appIcon)
                .silentlyShare(request, requestCode, accountId, callback, appKey);
    }

    /** 静默分享到时光记忆。 */
    public void sendRequestToXTC(BaseRequest baseRequest, String appKey, ISilentlyShareCallback callback)
            throws RemoteException {
        Context context = this.context;
        if (context == null || baseRequest == null) {
            callback.onResult(BaseResponse.Code.ARGUMENT_ERROR, "context or baseRequest must not null!");
            return;
        }
        if (!(baseRequest instanceof SendMessageToXTC.Request)) {
            callback.onResult(BaseResponse.Code.ARGUMENT_ERROR, "baseRequest argument must instanceof SendMessageToXTC.Request!");
            return;
        }
        if (!ShareUtil.isConnected(context)) {
            callback.onResult(BaseResponse.Code.NETWORK_ERROR, "network not connect!");
            return;
        }
        if (!ShareUtil.isAppInWhiteList(this.context, OpenApiConstant.App.PACKAGE_TIME_MEMORY)) {
            callback.onResult(BaseResponse.Code.APP_NOT_PERMISSION, "app not permission!");
            return;
        }
        BaseResponse checkResult = baseRequest.checkArgs();
        if (checkResult == null || checkResult.getCode() != BaseResponse.Code.OK) {
            callback.onResult(BaseResponse.Code.ARGUMENT_ERROR, "argument check error!");
            return;
        }
        if (ShareUtil.getHostSdkVersion(this.context, OpenApiConstant.App.LAUNCHER) <= VERSION_ONE) {
            Log.d(TAG, "check sdk version fail");
            callback.onResult(BaseResponse.Code.NON_SUPPORT, "current host not support!");
            return;
        }
        SendMessageToXTC.Request request = (SendMessageToXTC.Request) baseRequest;
        request.getMessage().getShareObject();
        baseRequest.setTransaction(System.currentTimeMillis() + String.valueOf(UUID.randomUUID()));
        new StrategyVersionTwo(this.context, appName, appIcon)
                .silentlyShareByTimeMemory(request, appKey, callback);
    }

    /** 跳转好友圈分享图片。 */
    public void shareToMomentPicture(JumpToMomentRequest jumpToMomentRequest) {
        Context context = this.context;
        if (context == null) {
            Log.d(TAG, "share to xtc the context is null!");
            return;
        }
        if (jumpToMomentRequest == null) {
            Log.d(TAG, "shareToPictureInfo must not null!");
            return;
        }
        if (!(context instanceof Activity) || TextUtils.isEmpty(context.getPackageName())
                || TextUtils.isEmpty(this.context.getClass().getName())) {
            Log.d(TAG, "you should use an activity context here!");
            return;
        }
        if (!ShareUtil.isConnected(this.context)) {
            Log.d(TAG, "network is not connected");
            Context shareContext = this.context;
            Toast.makeText(shareContext, shareContext.getString(R.string.net_work_not_connect), Toast.LENGTH_SHORT).show();
            return;
        }
        BaseResponse checkResult = jumpToMomentRequest.checkArgs();
        if (checkResult == null || checkResult.getCode() != BaseResponse.Code.OK) {
            Log.d(TAG, "the argument request check fail");
            return;
        }
        new JumpToMomentStrategy(this.context).share(jumpToMomentRequest);
    }

    /** 检查基础版本是否支持指定场景。 */
    public boolean checkBaseVersion(int sceneType) {
        int hostSdkVersion = ShareUtil.getHostSdkVersion(this.context, OpenApiConstant.App.LAUNCHER);
        if (sceneType == 2 && !WatchAccountBase.queryModuleSwitchByBoolean(this.context,
                OpenApiConstant.ModuleSwitch.MODULE_SWITCH_MOMENT, !WatchModelUtil.isOverseas())) {
            return false;
        }
        if (hostSdkVersion == VERSION_QUERY_FAILED) {
            Log.d(TAG, "check sdk version fail");
            return false;
        }
        if (hostSdkVersion == VERSION_ONE) {
            return new StrategyVersionOne(this.context, appName, appIcon).checkBaseVersion(sceneType);
        }
        return new StrategyVersionTwo(this.context, appName, appIcon).checkBaseVersion(sceneType);
    }

    public void setAppIcon(Bitmap bitmap) {
        this.appIcon = BitmapUtil.bitmapToByteArray(BitmapUtil.scaleIcon(this.context, bitmap));
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}