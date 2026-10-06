package com.xtc.shareapi.share.manager;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xtc.shareapi.share.communication.BaseResponse;
import com.xtc.shareapi.share.communication.SendMessageToXTC;
import com.xtc.shareapi.share.communication.ShowMessageFromXTC;
import com.xtc.shareapi.share.constant.OpenApiConstant;
import com.xtc.shareapi.share.interfaces.IResponseCallback;
import com.xtc.shareapi.share.interfaces.IXTCCallback;

/**
 * XTC 分享回调默认实现，解析回跳 Intent 并分发请求/响应。
 */
public class XTCCallbackImpl implements IXTCCallback {

    private final String TAG = OpenApiConstant.TAG + XTCCallbackImpl.class.getSimpleName();

    private Context context;

    public XTCCallbackImpl() {
    }

    public XTCCallbackImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean handleIntent(Intent intent, IResponseCallback responseCallback) {
        if (intent == null || responseCallback == null) {
            Log.d(OpenApiConstant.TAG, "current intent or callback is null");
            return false;
        }
        int resultCode = intent.getIntExtra(OpenApiConstant.ResponseConstant.BUNDLE_ERROR_CODE, -1);
        if (resultCode == -1) {
            Log.d(TAG, "current result is  no share event");
            return false;
        }
        if (resultCode == BaseResponse.Code.SKIP_CODE) {
            responseCallback.onReq(new ShowMessageFromXTC.Request().fromBundle(intent.getExtras()));
            return true;
        }
        responseCallback.onResp(resultCode == BaseResponse.Code.OK, new SendMessageToXTC.Response().fromBundle(intent.getExtras()));
        return true;
    }
}