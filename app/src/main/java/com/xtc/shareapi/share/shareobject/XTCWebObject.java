package com.xtc.shareapi.share.shareobject;

import android.os.Bundle;
import android.util.Log;

import com.xtc.shareapi.share.bean.SerializableMap;
import com.xtc.shareapi.share.communication.BaseResponse;
import com.xtc.shareapi.share.communication.ShowMessageFromXTC;
import com.xtc.shareapi.share.constant.OpenApiConstant;
import com.xtc.shareapi.share.interfaces.IShareObject;

/**
 * 网页分享对象，携带 URL、扩展信息与 RTOS 支持标记。
 */
public class XTCWebObject implements IShareObject {

    private static final int LENGTH_LIMIT = 1024;
    private static final String TAG = OpenApiConstant.TAG + XTCWebObject.class.getSimpleName();

    private String url;
    private String extInfo;
    private SerializableMap extMap;
    private int rtosSupport;

    @Override
    public int type() {
        return TYPE_WEB;
    }

    @Override
    public void toBundle(Bundle bundle) {
        bundle.putString(OpenApiConstant.XTCWebConstant.BUNDLE_WEB_EXTEND_INFO, extInfo);
        bundle.putString(OpenApiConstant.XTCWebConstant.BUNDLE_WEB_URL, url);
        bundle.putParcelable(OpenApiConstant.XTCWebConstant.BUILDER_WEB_MAP, getExtMap());
        bundle.putInt(OpenApiConstant.XTCWebConstant.BUILDER_WEB_RTOS_SUPPORT, getRtosSupport());
    }

    @Override
    public XTCWebObject fromBundle(Bundle bundle) {
        XTCWebObject webObject = new XTCWebObject();
        webObject.extInfo = bundle.getString(OpenApiConstant.XTCWebConstant.BUNDLE_WEB_EXTEND_INFO);
        webObject.url = bundle.getString(OpenApiConstant.XTCWebConstant.BUNDLE_WEB_URL);
        bundle.getString(OpenApiConstant.BuilderConstant.BUILDER_WEB_BUNDLE_MAP);
        webObject.extMap = bundle.getParcelable(OpenApiConstant.XTCWebConstant.BUILDER_WEB_MAP);
        webObject.rtosSupport = bundle.getInt(OpenApiConstant.XTCWebConstant.BUILDER_WEB_RTOS_SUPPORT);
        return webObject;
    }

    @Override
    public BaseResponse checkArgs() {
        ShowMessageFromXTC.Response response = new ShowMessageFromXTC.Response();
        Log.d(TAG, "start checkArgs");
        String extInfo = this.extInfo;
        if (extInfo != null && extInfo.length() > LENGTH_LIMIT) {
            Log.e(TAG, "checkArgs fail, the program extInfo is too null or too long");
            response.setCode(6);
            response.setErrorDesc("share_argument_error,the program extInfo is null or too long");
            return response;
        }
        String url = this.url;
        if (url == null || url.length() > LENGTH_LIMIT) {
            Log.e(TAG, "checkArgs fail, the  url is empty or the url is too long");
            response.setCode(6);
            response.setErrorDesc("share_argument_error, the  url is empty or the url is too long");
            return response;
        }
        response.setCode(1);
        return response;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getExtInfo() {
        return extInfo;
    }

    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo;
    }

    public SerializableMap getExtMap() {
        return extMap;
    }

    public void setExtMap(SerializableMap extMap) {
        this.extMap = extMap;
    }

    public int getRtosSupport() {
        return rtosSupport;
    }

    public void setRtosSupport(int rtosSupport) {
        this.rtosSupport = rtosSupport;
    }

    @Override
    public String toString() {
        return "XTCWebObject{url='" + url + "', extInfo='" + extInfo + "', extMap=" + extMap
                + ", rtosSupport=" + rtosSupport + '}';
    }
}