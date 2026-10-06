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
 * 多张图片分享对象。
 */
public class XTCMultiImageObject implements IShareObject {

    private static final String TAG = "Share_XTCMultiImageObject";

    private ArrayList<XTCImageObject> imagePathList;

    public XTCMultiImageObject() {
    }

    public XTCMultiImageObject(ArrayList<String> imagePathList) {
        // 与原实现保持一致：该构造分支不改变成员。
        this.imagePathList = this.imagePathList;
    }

    @Override
    public int type() {
        return OpenApiConstant.XTCShareType.MULTI_IMAGE;
    }

    @Override
    public void toBundle(Bundle bundle) {
        bundle.putParcelableArrayList(OpenApiConstant.XTCMultiImageConstant.BUNDLE_IMAGE_PATH_LIST, imagePathList);
    }

    @Override
    public XTCMultiImageObject fromBundle(Bundle bundle) {
        XTCMultiImageObject multiImageObject = new XTCMultiImageObject();
        multiImageObject.imagePathList = bundle.getParcelableArrayList(OpenApiConstant.XTCMultiImageConstant.BUNDLE_IMAGE_PATH_LIST);
        return multiImageObject;
    }

    @Override
    public BaseResponse checkArgs() {
        ShowMessageFromXTC.Response response = new ShowMessageFromXTC.Response();
        ArrayList<XTCImageObject> list = this.imagePathList;
        if (list == null || list.size() == 0) {
            Log.e(TAG, "checkArgs fail, imagePathList are empty");
            response.setCode(6);
            response.setErrorDesc("share_argument_errorimagePathList are empty");
            return response;
        }
        if (this.imagePathList.size() > 9) {
            Log.e(TAG, "checkArgs fail, imagePathList size big than 9");
            response.setCode(6);
            response.setErrorDesc("share_argument_errorimagePathList size big than 9");
            return response;
        }
        Iterator<XTCImageObject> iterator = this.imagePathList.iterator();
        while (iterator.hasNext()) {
            XTCImageObject imageObject = iterator.next();
            if (imageObject == null) {
                iterator.remove();
            } else {
                BaseResponse result = imageObject.checkArgs();
                if (result.getCode() != 1) {
                    return result;
                }
            }
        }
        response.setCode(1);
        return response;
    }

    public ArrayList<XTCImageObject> getImagePathList() {
        return imagePathList;
    }

    public void setImagePathList(ArrayList<XTCImageObject> imagePathList) {
        this.imagePathList = imagePathList;
    }

    @Override
    public String toString() {
        return "XTCMultiImageObject{imagePathList=" + imagePathList + '}';
    }
}