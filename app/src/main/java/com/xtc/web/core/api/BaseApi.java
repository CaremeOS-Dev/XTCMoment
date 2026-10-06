package com.xtc.web.core.api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.xtc.log.LogUtil;
import com.xtc.shareapi.share.communication.BaseResponse;
import com.xtc.system.account.WatchAccountBase;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.web.core.CoreConstants;
import com.xtc.web.core.WebManager;
import com.xtc.web.core.callback.ASRCallback;
import com.xtc.web.core.callback.CompletionHandler;
import com.xtc.web.core.callback.RecordCallback;
import com.xtc.web.core.data.req.ReqAccount;
import com.xtc.web.core.data.req.ReqBtnText;
import com.xtc.web.core.data.req.ReqJsStartApp;
import com.xtc.web.core.data.req.ReqObtainImage;
import com.xtc.web.core.data.req.ReqSaveImage;
import com.xtc.web.core.data.req.ReqShare;
import com.xtc.web.core.data.req.ReqShareApp;
import com.xtc.web.core.data.req.ReqShareJumpMoment;
import com.xtc.web.core.data.req.ReqSp;
import com.xtc.web.core.data.req.ReqSystemProperty;
import com.xtc.web.core.data.req.ReqVibrator;
import com.xtc.web.core.data.resp.RespAccelerometer;
import com.xtc.web.core.data.resp.RespAccountInfo;
import com.xtc.web.core.data.resp.RespAppInfo;
import com.xtc.web.core.data.resp.RespBatteryInfo;
import com.xtc.web.core.data.resp.RespImage;
import com.xtc.web.core.data.resp.RespLocationInfo;
import com.xtc.web.core.data.resp.RespPhoto;
import com.xtc.web.core.data.resp.RespStartApp;
import com.xtc.web.core.data.resp.RespSystemProperty;
import com.xtc.web.core.data.resp.RespVoiceResult;
import com.xtc.web.core.manager.AccountManager;
import com.xtc.web.core.manager.AppManager;
import com.xtc.web.core.manager.BatteryInfoManager;
import com.xtc.web.core.manager.BitmapManager;
import com.xtc.web.core.manager.JsJumpAppUtils;
import com.xtc.web.core.manager.JsToastManager;
import com.xtc.web.core.manager.LocationManager;
import com.xtc.web.core.manager.ShareManager;
import com.xtc.web.core.manager.SimpleShakeManager;
import com.xtc.web.core.manager.SpManager;
import com.xtc.web.core.manager.SystemPropertyManager;
import com.xtc.web.core.manager.TakePhotoManager;
import com.xtc.web.core.manager.VibratorManager;

/** 暴露给所有 H5 页面通用的原生能力（namespace 为 xtc）。 */
public class BaseApi {

    private static final String TAG = CoreConstants.TAG + BaseApi.class.getSimpleName();

    private ASRCallback asrCallback;
    private Context mContext;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private RecordCallback recordCallback;

    public BaseApi(Context context) {
        this.mContext = context;
    }

    public void setAsrCallback(ASRCallback asrCallback) {
        this.asrCallback = asrCallback;
    }

    public void setRecordCallback(RecordCallback recordCallback) {
        this.recordCallback = recordCallback;
    }

    @JavascriptInterface
    public void delayRemoveLoading(final Object arg) {
        Log.d(TAG, "js call delayRemoveLoading = " + arg);
        this.mainHandler.post(new Runnable() {
            @Override
            public void run() {
                WebManager webManager = WebManager.hasInit();
                if (webManager != null) {
                    webManager.delayRemoveLoading(((Boolean) arg).booleanValue());
                }
            }
        });
    }

    @JavascriptInterface
    public void removeLoading(Object arg) {
        Log.d(TAG, "js call removeLoading = " + arg);
        this.mainHandler.post(new Runnable() {
            @Override
            public void run() {
                WebManager webManager = WebManager.hasInit();
                if (webManager != null) {
                    webManager.removeLoadingView(true);
                }
            }
        });
    }

    @JavascriptInterface
    public void showLoading(Object arg) {
        Log.d(TAG, "js call showLoading = " + arg);
        this.mainHandler.post(new Runnable() {
            @Override
            public void run() {
                WebManager webManager = WebManager.hasInit();
                if (webManager != null) {
                    webManager.addLoadingView();
                }
            }
        });
    }

    @JavascriptInterface
    public void toastMethod(Object arg) {
        Log.d(TAG, "js call native toastMethod = " + arg);
        JsToastManager.showToast(this.mContext, arg.toString());
    }

    @JavascriptInterface
    public void getLocalImage(Object arg, CompletionHandler<RespImage> completionHandler) {
        Log.d(TAG, "js call getImage args = " + arg);
        new BitmapManager().getLocalImage(this.mContext,
                JSONUtil.fromJSON(arg.toString(), ReqObtainImage.class), completionHandler);
    }

    @JavascriptInterface
    public void saveLocalImage(Object arg, CompletionHandler<Boolean> completionHandler) throws Throwable {
        Log.d(TAG, "js call saveLocalImage = " + arg);
        new BitmapManager().saveLocalImage(this.mContext,
                JSONUtil.fromJSON(arg.toString(), ReqSaveImage.class), completionHandler);
    }

    @JavascriptInterface
    public void getAppInfo(Object arg, CompletionHandler<RespAppInfo> completionHandler) {
        Log.d(TAG, "js call getAppInfo args = " + arg);
        new AppManager().request(this.mContext, (String) arg, completionHandler);
    }

    @JavascriptInterface
    public void openUrl(Object arg, CompletionHandler<RespStartApp> completionHandler) {
        Log.d(TAG, "js call openUrl args = " + arg);
        JsJumpAppUtils.startApp(this.mContext, JSONUtil.fromJSON(arg.toString(), ReqJsStartApp.class),
                completionHandler);
    }

    @JavascriptInterface
    public void saveSp(Object arg, CompletionHandler<Boolean> completionHandler) {
        Log.d(TAG, "js call saveSp args = " + arg);
        SpManager.getInstance(this.mContext).saveSp(JSONUtil.fromJSON(arg.toString(), ReqSp.class),
                completionHandler);
    }

    @JavascriptInterface
    public void getSp(Object arg, CompletionHandler<String> completionHandler) {
        Log.d(TAG, "js call getSp");
        SpManager.getInstance(this.mContext).getSp(arg.toString(), completionHandler);
    }

    @JavascriptInterface
    public void removeSp(Object arg, CompletionHandler<Boolean> completionHandler) {
        Log.d(TAG, "js call saveSp args = " + arg);
        SpManager.getInstance(this.mContext).removeSp(JSONUtil.fromJSON(arg.toString(), ReqSp.class),
                completionHandler);
    }

    @JavascriptInterface
    public void shareApp(Object arg, CompletionHandler<BaseResponse> completionHandler) {
        Log.d(TAG, "js call shareApp args = " + arg);
        new ShareManager().shareApp(this.mContext, JSONUtil.fromJSON(arg.toString(), ReqShareApp.class),
                completionHandler);
    }

    @JavascriptInterface
    public void share(Object arg, CompletionHandler<BaseResponse> completionHandler) {
        Log.d(TAG, "js call share args = " + arg);
        new ShareManager().share(this.mContext, JSONUtil.fromJSON(arg.toString(), ReqShare.class),
                completionHandler);
    }

    @JavascriptInterface
    public void shareToJumpMoment(Object arg, CompletionHandler<BaseResponse> completionHandler) {
        if (arg == null) {
            return;
        }
        LogUtil.d(TAG, "js call shareToJumpMoment args = " + arg);
        new ShareManager().shareJumpToMoment(this.mContext,
                JSONUtil.fromJSON(arg.toString(), ReqShareJumpMoment.class), completionHandler);
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
    public void getLocation(Object arg, CompletionHandler<RespLocationInfo> completionHandler) {
        LogUtil.d(TAG, "js call startLocation ! args = " + arg);
        LocationManager.getInstance(this.mContext).getLocation(completionHandler);
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
    public void getBatteryInfo(Object arg, CompletionHandler<RespBatteryInfo> completionHandler) {
        LogUtil.d(TAG, "js call getBatteryInfo ! args = " + arg);
        BatteryInfoManager.getInstance(this.mContext).getBatteryInfo(completionHandler);
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
    public void vibrator(Object arg) {
        LogUtil.d(TAG, "js call vibrator ! args = " + arg);
        VibratorManager.vibratorWatch(this.mContext, JSONUtil.fromJSON(arg.toString(), ReqVibrator.class));
    }

    @JavascriptInterface
    public void asrStart(Object arg, CompletionHandler<RespVoiceResult> completionHandler) {
        LogUtil.d(TAG, "js call asrStart = " + arg);
        ASRCallback callback = this.asrCallback;
        if (callback != null) {
            callback.startASR(completionHandler);
        }
    }

    @JavascriptInterface
    public void asrStop(Object arg, CompletionHandler<RespAccelerometer> completionHandler) {
        LogUtil.d(TAG, "js call asrStop " + arg);
        ASRCallback callback = this.asrCallback;
        if (callback != null) {
            callback.stopASR();
        }
    }

    @JavascriptInterface
    public void startRecord(Object arg, CompletionHandler<RespVoiceResult> completionHandler) {
        LogUtil.d(TAG, "js call startRecord = " + arg);
        RecordCallback callback = this.recordCallback;
        if (callback != null) {
            callback.startRecord(completionHandler);
        }
    }

    @JavascriptInterface
    public void stopRecord(Object arg, CompletionHandler<RespAccelerometer> completionHandler) {
        LogUtil.d(TAG, "js call stopRecord " + arg);
        RecordCallback callback = this.recordCallback;
        if (callback != null) {
            callback.stopRecord();
        }
    }

    @JavascriptInterface
    public void getOpenId(Object arg, CompletionHandler<RespAccountInfo> completionHandler) {
        Log.d(TAG, "BaseApi getOpenId = " + arg);
        ReqAccount reqAccount = JSONUtil.fromJSON(arg.toString(), ReqAccount.class);
        reqAccount.setWatchId(WatchAccountBase.getAccountWatchId(this.mContext));
        AccountManager.getInstance(this.mContext).getOpenId(reqAccount, completionHandler);
    }

    @JavascriptInterface
    public void getSystemProperty(Object arg, CompletionHandler<RespSystemProperty> completionHandler) {
        Log.d(TAG, "js call getSystemProperty args = " + arg);
        new SystemPropertyManager().request(JSONUtil.fromJSON(arg.toString(), ReqSystemProperty.class),
                completionHandler);
    }
}