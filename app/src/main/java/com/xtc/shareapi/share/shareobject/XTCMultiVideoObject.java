package com.xtc.shareapi.share.shareobject;

import android.os.Bundle;
import android.util.Log;

import com.xtc.shareapi.share.communication.BaseResponse;
import com.xtc.shareapi.share.communication.ShowMessageFromXTC;
import com.xtc.shareapi.share.constant.OpenApiConstant;
import com.xtc.shareapi.share.interfaces.IShareObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 多个视频分享对象。
 */
public class XTCMultiVideoObject implements IShareObject {

    private static final String TAG = "Share_XTCMultiLivePhotoObject";

    private ArrayList<XTCVideoObject> videoObjectList;

    public XTCMultiVideoObject() {
    }

    public XTCMultiVideoObject(ArrayList<XTCVideoObject> videoObjectList) {
        this.videoObjectList = videoObjectList;
    }

    @Override
    public int type() {
        return OpenApiConstant.XTCShareType.MULTI_VIDEO;
    }

    @Override
    public void toBundle(Bundle bundle) {
        bundle.putParcelableArrayList(OpenApiConstant.XTCMultiVideoConstant.BUNDLE_VIDEO_LIST, videoObjectList);
    }

    @Override
    public XTCMultiVideoObject fromBundle(Bundle bundle) {
        XTCMultiVideoObject multiVideoObject = new XTCMultiVideoObject();
        multiVideoObject.videoObjectList = bundle.getParcelableArrayList(OpenApiConstant.XTCMultiVideoConstant.BUNDLE_VIDEO_LIST);
        return multiVideoObject;
    }

    @Override
    public BaseResponse checkArgs() {
        ShowMessageFromXTC.Response response = new ShowMessageFromXTC.Response();
        ArrayList<XTCVideoObject> list = this.videoObjectList;
        if (list == null || list.size() == 0) {
            Log.e(TAG, "checkArgs fail, videoObjectList are empty");
            response.setCode(6);
            response.setErrorDesc("share_argument_errorvideoObjectList are empty");
            return response;
        }
        if (this.videoObjectList.size() > 9) {
            Log.e(TAG, "checkArgs fail, videoObjectList size big than 9");
            response.setCode(15);
            response.setErrorDesc("share_count_too_many_error, videoObjectList size big than 9");
            return response;
        }
        Iterator<XTCVideoObject> iterator = this.videoObjectList.iterator();
        while (iterator.hasNext()) {
            XTCVideoObject videoObject = iterator.next();
            if (videoObject == null) {
                iterator.remove();
            } else {
                BaseResponse result = videoObject.checkArgs();
                if (result.getCode() != 1) {
                    return result;
                }
            }
        }
        response.setCode(1);
        return response;
    }

    public ArrayList<XTCVideoObject> getVideoObjectList() {
        return videoObjectList;
    }

    public void setVideoObjectList(ArrayList<XTCVideoObject> videoObjectList) {
        this.videoObjectList = videoObjectList;
    }

    @Override
    public String toString() {
        return "XTCMultiVideoObject{xtcVideoObject=" + videoObjectList + '}';
    }
}