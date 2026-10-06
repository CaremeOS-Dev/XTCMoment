package com.xtc.shareapi.share.shareobject;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.xtc.shareapi.share.communication.BaseResponse;
import com.xtc.shareapi.share.communication.ShowMessageFromXTC;
import com.xtc.shareapi.share.constant.OpenApiConstant;
import com.xtc.shareapi.share.interfaces.IShareObject;

/**
 * 应用扩展分享对象，携带扩展信息与可启动的 Activity 名称。
 */
public class XTCAppExtendObject implements IShareObject {

    private static final int LENGTH_LIMIT = 1024;
    private static final String TAG = OpenApiConstant.TAG + XTCAppExtendObject.class.getSimpleName();

    private String extInfo;
    private String startActivity;

    @Override
    public int type() {
        return OpenApiConstant.XTCShareType.APP;
    }

    @Override
    public void toBundle(Bundle bundle) {
        bundle.putString(OpenApiConstant.XTCAppExtendConstant.BUNDLE_EXTEND_INFO, extInfo);
        bundle.putString(OpenApiConstant.XTCAppExtendConstant.BUNDLE_START_ACTIVITY, startActivity);
    }

    @Override
    public XTCAppExtendObject fromBundle(Bundle bundle) {
        XTCAppExtendObject appExtendObject = new XTCAppExtendObject();
        appExtendObject.extInfo = bundle.getString(OpenApiConstant.XTCAppExtendConstant.BUNDLE_EXTEND_INFO);
        appExtendObject.startActivity = bundle.getString(OpenApiConstant.XTCAppExtendConstant.BUNDLE_START_ACTIVITY);
        return appExtendObject;
    }

    @Override
    public BaseResponse checkArgs() {
        ShowMessageFromXTC.Response response = new ShowMessageFromXTC.Response();
        String extInfo = this.extInfo;
        if (extInfo != null && extInfo.length() > LENGTH_LIMIT) {
            Log.e(TAG, "checkArgs fail, the program extInfo is too long");
            response.setCode(6);
            response.setErrorDesc("share_argument_error,the program extInfo is too long");
            return response;
        }
        if (TextUtils.isEmpty(startActivity) || startActivity.length() > LENGTH_LIMIT) {
            Log.e(TAG, "checkArgs fail, the program startActivity name is empty or the program name is too long");
            response.setCode(6);
            response.setErrorDesc("share_argument_error,the program startActivity name is empty or the program name is too long");
            return response;
        }
        response.setCode(1);
        return response;
    }

    public String getExtInfo() {
        return extInfo;
    }

    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo;
    }

    public String getStartActivity() {
        return startActivity;
    }

    public void setStartActivity(String startActivity) {
        this.startActivity = startActivity;
    }

    @Override
    public String toString() {
        return "XTCAppExtendObject{extInfo='" + extInfo + "', startActivity='" + startActivity + "'}";
    }
}