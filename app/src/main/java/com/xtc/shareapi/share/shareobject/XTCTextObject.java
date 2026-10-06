package com.xtc.shareapi.share.shareobject;

import android.os.Bundle;
import android.util.Log;

import com.xtc.shareapi.share.communication.BaseResponse;
import com.xtc.shareapi.share.communication.ShowMessageFromXTC;
import com.xtc.shareapi.share.constant.OpenApiConstant;
import com.xtc.shareapi.share.interfaces.IShareObject;

/**
 * 纯文本分享对象。
 */
public class XTCTextObject implements IShareObject {

    private static final int LENGTH_LIMIT = 1024;
    private static final String TAG = OpenApiConstant.TAG + XTCTextObject.class.getSimpleName();

    private String text;

    public XTCTextObject() {
    }

    public XTCTextObject(String text) {
        this.text = text;
    }

    @Override
    public int type() {
        return TYPE_TEXT;
    }

    @Override
    public void toBundle(Bundle bundle) {
        bundle.putString(OpenApiConstant.XTCTextConstant.BUNDLE_TEXT, text);
    }

    @Override
    public XTCTextObject fromBundle(Bundle bundle) {
        XTCTextObject textObject = new XTCTextObject();
        textObject.text = bundle.getString(OpenApiConstant.XTCTextConstant.BUNDLE_TEXT);
        return textObject;
    }

    @Override
    public BaseResponse checkArgs() {
        ShowMessageFromXTC.Response response = new ShowMessageFromXTC.Response();
        String text = this.text;
        if (text != null && text.length() != 0 && text.length() <= LENGTH_LIMIT) {
            response.setCode(1);
            return response;
        }
        Log.e(TAG, "checkArgs fail, text is invalid");
        response.setCode(6);
        response.setErrorDesc("share_argument_error, text is invalid");
        return response;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "XTCTextObject{text='" + text + "'}";
    }
}