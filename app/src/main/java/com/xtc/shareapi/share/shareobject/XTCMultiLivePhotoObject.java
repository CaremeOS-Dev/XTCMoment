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
 * 多张实况照片分享对象。
 */
public class XTCMultiLivePhotoObject implements IShareObject {

    private static final String TAG = "Share_XTCMultiLivePhotoObject";

    private ArrayList<XTCLivePhotoObject> photoObjectList;

    public XTCMultiLivePhotoObject() {
    }

    public XTCMultiLivePhotoObject(ArrayList<XTCLivePhotoObject> photoObjectList) {
        this.photoObjectList = photoObjectList;
    }

    @Override
    public int type() {
        return OpenApiConstant.XTCShareType.MULTI_LIVE_PHOTO;
    }

    @Override
    public void toBundle(Bundle bundle) {
        bundle.putParcelableArrayList(OpenApiConstant.XTCMultiLivePhotoConstant.BUNDLE_LIVE_PHOTO_LIST, photoObjectList);
    }

    @Override
    public XTCMultiLivePhotoObject fromBundle(Bundle bundle) {
        XTCMultiLivePhotoObject multiLivePhotoObject = new XTCMultiLivePhotoObject();
        multiLivePhotoObject.photoObjectList = bundle.getParcelableArrayList(OpenApiConstant.XTCMultiLivePhotoConstant.BUNDLE_LIVE_PHOTO_LIST);
        return multiLivePhotoObject;
    }

    @Override
    public BaseResponse checkArgs() {
        ShowMessageFromXTC.Response response = new ShowMessageFromXTC.Response();
        ArrayList<XTCLivePhotoObject> list = this.photoObjectList;
        if (list == null || list.size() == 0) {
            Log.e(TAG, "checkArgs fail, photoObjectList are empty");
            response.setCode(6);
            response.setErrorDesc("share_argument_errorphotoObjectList are empty");
            return response;
        }
        if (this.photoObjectList.size() > 9) {
            Log.e(TAG, "checkArgs fail, photoObjectList size big than 9");
            response.setCode(15);
            response.setErrorDesc("share_count_too_many_error, photoObjectList size big than 9");
            return response;
        }
        Iterator<XTCLivePhotoObject> iterator = this.photoObjectList.iterator();
        while (iterator.hasNext()) {
            XTCLivePhotoObject livePhotoObject = iterator.next();
            if (livePhotoObject == null) {
                iterator.remove();
            } else {
                BaseResponse result = livePhotoObject.checkArgs();
                if (result.getCode() != 1) {
                    return result;
                }
            }
        }
        response.setCode(1);
        return response;
    }

    public ArrayList<XTCLivePhotoObject> getPhotoObjectList() {
        return photoObjectList;
    }

    public void setPhotoObjectList(ArrayList<XTCLivePhotoObject> photoObjectList) {
        this.photoObjectList = photoObjectList;
    }

    @Override
    public String toString() {
        return "XTCMultiLivePhotoObject{photoObjectList=" + photoObjectList + '}';
    }
}