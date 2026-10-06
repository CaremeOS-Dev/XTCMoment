package com.xtc.shareapi.share.bean;

import android.util.Log;

import com.xtc.shareapi.share.communication.BaseResponse;
import com.xtc.shareapi.share.communication.ShowMessageFromXTC;
import com.xtc.shareapi.share.interfaces.IJumpToMomentObject;

/**
 * 跳转好友圈发动态的请求包装，负责校验内部跳转对象的参数。
 */
public class JumpToMomentRequest {

    private static final String TAG = "JumpToMomentRequest";

    private IJumpToMomentObject jumpToMomentObject;

    public void setJumpToMomentObject(IJumpToMomentObject jumpToMomentObject) {
        this.jumpToMomentObject = jumpToMomentObject;
    }

    public IJumpToMomentObject getJumpToMomentObject() {
        return jumpToMomentObject;
    }

    /** 校验跳转参数。 */
    public BaseResponse checkArgs() {
        IJumpToMomentObject object = jumpToMomentObject;
        if (object == null) {
            Log.d(TAG, "checkArgs fail ,shareMomentObject is null");
            ShowMessageFromXTC.Response response = new ShowMessageFromXTC.Response();
            response.setCode(6);
            response.setErrorDesc("shareMomentObject is null");
            return response;
        }
        return object.checkArgs();
    }

    @Override
    public String toString() {
        return "ShareToPictureInfo{jumpToMomentObject=" + jumpToMomentObject + '}';
    }
}