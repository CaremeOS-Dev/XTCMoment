package com.xtc.web.client.api;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.xtc.log.LogUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.web.client.data.Constants;
import com.xtc.web.client.data.request.ReqJsBehavior;
import com.xtc.web.client.data.request.ReqJsHttp;
import com.xtc.web.client.data.request.ReqJsLog;
import com.xtc.web.client.data.request.ReqModule;
import com.xtc.web.client.data.request.ReqWakeLockTime;
import com.xtc.web.client.data.response.RespAppInstallState;
import com.xtc.web.client.data.response.RespFriendInfo;
import com.xtc.web.client.data.response.RespJsHttp;
import com.xtc.web.client.data.response.RespMotion;
import com.xtc.web.client.data.response.RespWakeLock;
import com.xtc.web.client.manager.AppInstallStateManager;
import com.xtc.web.client.manager.BaseInfoManager;
import com.xtc.web.client.manager.FriendManager;
import com.xtc.web.client.manager.JsBehaviorUtils;
import com.xtc.web.client.manager.JsEruptManager;
import com.xtc.web.client.manager.JsHttpManager;
import com.xtc.web.client.manager.JsLogUtils;
import com.xtc.web.client.manager.JsPushManager;
import com.xtc.web.client.manager.ModuleManager;
import com.xtc.web.client.manager.MotionManager;
import com.xtc.web.client.manager.UrlCacheManager;
import com.xtc.web.client.manager.WakeLockManager;
import com.xtc.web.core.callback.CompletionHandler;
import com.xtc.web.core.data.req.ReqBtnText;
import com.xtc.web.core.data.resp.RespAccelerometer;
import com.xtc.web.core.data.resp.RespPhoto;
import com.xtc.web.core.manager.JsJumpAppUtils;
import com.xtc.web.core.manager.SimpleShakeManager;
import com.xtc.web.core.manager.TakePhotoManager;

import java.util.HashMap;
import java.util.List;

/** 暴露给 H5 的客户端能力接口（namespace 为空，即 window.xxx）。 */
public class XTCWatchApi {

    private static final String TAG = Constants.TAG + XTCWatchApi.class.getSimpleName();

    private final Context mContext;

    public XTCWatchApi(Context context) {
        this.mContext = context;
    }

    @JavascriptInterface
    public void jsLog(Object arg) {
        JsLogUtils.log(JSONUtil.fromJSON(arg.toString(), ReqJsLog.class));
    }

    @JavascriptInterface
    public void massiveDataStatistic(Object arg) {
        LogUtil.d(TAG, "js call massiveDataStatistic args = " + arg);
        JsBehaviorUtils.behavior(this.mContext, JSONUtil.fromJSON(arg.toString(), ReqJsBehavior.class));
    }

    @JavascriptInterface
    public void getAppData(Object arg, CompletionHandler<HashMap<String, String>> completionHandler) {
        LogUtil.d(TAG, "js call native getAppData = " + arg);
        BaseInfoManager.getInstance(this.mContext)
                .getAppData(JSONUtil.fromJSON(arg.toString(), List.class, String.class), completionHandler);
    }

    @JavascriptInterface
    public void httpRequest(Object arg, CompletionHandler<RespJsHttp> completionHandler) {
        LogUtil.d(TAG, "js call httpRequest args = " + arg);
        new JsHttpManager().request(this.mContext, JSONUtil.fromJSON(arg.toString(), ReqJsHttp.class),
                completionHandler);
    }

    @JavascriptInterface
    public void getFriendList(Object arg, CompletionHandler<RespFriendInfo> completionHandler) {
        LogUtil.d(TAG, "js call getFriendList !");
        new FriendManager().request(this.mContext, completionHandler);
    }

    @JavascriptInterface
    public void getTodayStep(Object arg, CompletionHandler<RespMotion> completionHandler) {
        LogUtil.d(TAG, "js call getTodayStep");
        new MotionManager().requestTodayStep(this.mContext, completionHandler);
    }

    @JavascriptInterface
    public void getMotionStatus(Object arg, CompletionHandler<RespMotion> completionHandler) {
        LogUtil.d(TAG, "js call getMotionStatus");
        new MotionManager().requestMotionStatus(this.mContext, completionHandler);
    }

    @JavascriptInterface
    public void getModuleSwitch(Object arg, CompletionHandler<Integer> completionHandler) {
        LogUtil.d(TAG, "js call getModuleSwitch args = " + arg);
        new ModuleManager().getModuleSwitch(this.mContext, JSONUtil.fromJSON(arg.toString(), ReqModule.class),
                completionHandler);
    }

    @JavascriptInterface
    public void registerPush(Object arg) {
        LogUtil.d(TAG, "js call registerPush args = " + arg);
        JsPushManager.getInstance().registerType(JSONUtil.fromJSON(arg.toString(), List.class, Integer.class));
    }

    @JavascriptInterface
    public void cleanUrlCache(Object arg, CompletionHandler<Boolean> completionHandler) {
        LogUtil.d(TAG, "js call cleanUrlCache !");
        UrlCacheManager.getInstance(this.mContext).cleanCache(completionHandler);
    }

    @JavascriptInterface
    public void wakeLock(Object arg, CompletionHandler<RespWakeLock> completionHandler) {
        LogUtil.d(TAG, "js call wakeLock ! args = " + arg);
        WakeLockManager.getInstance(this.mContext).wakeLock(
                JSONUtil.fromJSON(arg.toString(), ReqWakeLockTime.class).getTime(), completionHandler);
    }

    @JavascriptInterface
    public void releaseWakeLock(Object arg, CompletionHandler<RespWakeLock> completionHandler) {
        LogUtil.d(TAG, "js call releaseWakeLock ! args = " + arg);
        WakeLockManager.getInstance(this.mContext).releaseWakeLock(completionHandler);
    }

    @JavascriptInterface
    public void registerErupt(Object arg) {
        LogUtil.d(TAG, "js call registerErupt  ! args = " + arg);
        JsEruptManager.getInstance(this.mContext).registerReceiver();
    }

    @JavascriptInterface
    public void onAccelerometer(Object arg) {
        LogUtil.d(TAG, "js call onAccelerometer ! args = " + arg);
        SimpleShakeManager.getInstance(this.mContext).onAccelerometer();
    }

    @JavascriptInterface
    public void offAccelerometer(Object arg, CompletionHandler<RespAccelerometer> completionHandler) {
        LogUtil.d(TAG, "js call offAccelerometer ! args = " + arg);
        SimpleShakeManager.getInstance(this.mContext).offAccelerometer();
    }

    @JavascriptInterface
    public void takePhotoReturnPath(Object arg, CompletionHandler<RespPhoto> completionHandler) {
        LogUtil.d(TAG, "js call takePhotoReturnPath ! 传递过来的参数是：" + arg.toString());
        TakePhotoManager.getInstance(this.mContext).takePhotoReturnPath(
                !TextUtils.isEmpty(arg.toString()) ? JSONUtil.fromJSON(arg.toString(), ReqBtnText.class) : null,
                completionHandler);
    }

    @JavascriptInterface
    public void takePhotoWithBtnTextReturnPath(Object arg, CompletionHandler<RespPhoto> completionHandler) {
        LogUtil.d(TAG, "js call takePhotoWithBtnTextReturnPath ! 参数是：" + arg.toString());
        TakePhotoManager.getInstance(this.mContext).takePhotoWithBtnTextReturnPath(
                !TextUtils.isEmpty(arg.toString()) ? JSONUtil.fromJSON(arg.toString(), ReqBtnText.class) : null,
                completionHandler);
    }

    @JavascriptInterface
    public void chooseImageReturnPath(Object arg, CompletionHandler<RespPhoto> completionHandler) {
        LogUtil.d(TAG, "js call chooseImageReturnPath !");
        TakePhotoManager.getInstance(this.mContext).chooseImageReturnPath(completionHandler);
    }

    @JavascriptInterface
    public void unRegisterCallback(Object arg, CompletionHandler<RespAccelerometer> completionHandler) {
        LogUtil.d(TAG, "js call unRegisterCallback ! args = " + arg);
        JsJumpAppUtils.unRegisterCallback();
    }

    @JavascriptInterface
    public void unRegisterTakePhotoCallback(Object arg, CompletionHandler<RespAccelerometer> completionHandler) {
        LogUtil.d(TAG, "js call unRegisterTakePhotoCallback ! args = " + arg);
        TakePhotoManager.unRegisterTakePhotoCallback();
    }

    @JavascriptInterface
    public void getAppInstallState(Object arg, CompletionHandler<RespAppInstallState> completionHandler) {
        LogUtil.d(TAG, "js call getAppInstallState args = " + arg);
        AppInstallStateManager.getAppInstallState(this.mContext, arg.toString(), completionHandler);
    }
}