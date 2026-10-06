package com.xtc.shareapi.share.shareobject;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.xtc.shareapi.share.communication.BaseResponse;
import com.xtc.shareapi.share.communication.ShowMessageFromXTC;
import com.xtc.shareapi.share.constant.OpenApiConstant;
import com.xtc.shareapi.share.interfaces.IShareObject;

/**
 * 实况照片分享对象，包含本地图片与视频路径。
 */
public class XTCLivePhotoObject implements Parcelable, IShareObject {

    private static final String TAG = OpenApiConstant.TAG + XTCLivePhotoObject.class.getSimpleName();

    public String localPhotoPath;
    public String localVideoPath;

    public XTCLivePhotoObject() {
    }

    public XTCLivePhotoObject(String localPhotoPath, String localVideoPath) {
        this.localPhotoPath = localPhotoPath;
        this.localVideoPath = localVideoPath;
    }

    protected XTCLivePhotoObject(Parcel parcel) {
        this.localPhotoPath = parcel.readString();
        this.localVideoPath = parcel.readString();
    }

    @Override
    public int type() {
        return OpenApiConstant.XTCShareType.LIVE_PHOTO;
    }

    @Override
    public void toBundle(Bundle bundle) {
        bundle.putString(OpenApiConstant.XTCLivePhotoConstant.BUNDLE_PHOTO_PATH, localPhotoPath);
        bundle.putString(OpenApiConstant.XTCLivePhotoConstant.BUNDLE_VIDEO_PATH, localVideoPath);
    }

    @Override
    public XTCLivePhotoObject fromBundle(Bundle bundle) {
        XTCLivePhotoObject livePhotoObject = new XTCLivePhotoObject();
        livePhotoObject.localPhotoPath = bundle.getString(OpenApiConstant.XTCLivePhotoConstant.BUNDLE_PHOTO_PATH);
        livePhotoObject.localVideoPath = bundle.getString(OpenApiConstant.XTCLivePhotoConstant.BUNDLE_VIDEO_PATH);
        return livePhotoObject;
    }

    @Override
    public BaseResponse checkArgs() {
        ShowMessageFromXTC.Response response = new ShowMessageFromXTC.Response();
        if (TextUtils.isEmpty(localPhotoPath) || TextUtils.isEmpty(localVideoPath)) {
            Log.e(TAG, "checkArgs fail,localPhotoPath or localVideoPath is empty");
            response.setCode(6);
            response.setErrorDesc("share_argument_error,localPhotoPath or localVideoPath is empty");
            return response;
        }
        response.setCode(1);
        return response;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(localPhotoPath);
        parcel.writeString(localVideoPath);
    }

    public void readFromParcel(Parcel parcel) {
        this.localPhotoPath = parcel.readString();
        this.localVideoPath = parcel.readString();
    }

    @Override
    public String toString() {
        return "XTCLivePhotoObject{localPhotoPath='" + localPhotoPath + "', localVideoPath='" + localVideoPath + "'}";
    }

    public static final Parcelable.Creator<XTCLivePhotoObject> CREATOR = new Parcelable.Creator<XTCLivePhotoObject>() {
        @Override
        public XTCLivePhotoObject createFromParcel(Parcel parcel) {
            return new XTCLivePhotoObject(parcel);
        }

        @Override
        public XTCLivePhotoObject[] newArray(int size) {
            return new XTCLivePhotoObject[size];
        }
    };
}