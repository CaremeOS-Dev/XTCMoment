package com.xtc.shareapi.share.shareobject;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.xtc.shareapi.share.bean.ParcelableMap;
import com.xtc.shareapi.share.communication.BaseResponse;
import com.xtc.shareapi.share.communication.ShowMessageFromXTC;
import com.xtc.shareapi.share.constant.OpenApiConstant;
import com.xtc.shareapi.share.interfaces.IShareObject;

/**
 * 视频分享对象，包含本地路径、云端下载地址、缩略图与自定义参数等。
 */
public class XTCVideoObject implements Parcelable, IShareObject {

    private static final int LENGTH_LIMIT = 1024;
    private static final int THUMBNAIL_PATH_LENGTH_LIMIT = 512;
    private static final int VIDEO_PATH_LENGTH_LIMIT = 512;
    private static final String TAG = "Share_XTCVideoObject";

    private String type;
    private String wangSuUrl;
    private String zone;
    private ParcelableMap customParamMap;
    private String thumbnailPath;
    private String thumbnailDownloadUrl;
    private long thumbnailDeadline;
    private String thumbnailKey;
    private String videoPath;
    private String videoDownloadUrl;
    private long videoDeadline;
    private String videoKey;
    private String sourceDownloadUrl;
    private long sourceDeadline;
    private String sourceKey;
    private String extInfo;
    private String startActivity;
    private long duration;

    public XTCVideoObject() {
    }

    protected XTCVideoObject(Parcel parcel) {
        this.type = parcel.readString();
        this.wangSuUrl = parcel.readString();
        this.zone = parcel.readString();
        this.customParamMap = parcel.readParcelable(ParcelableMap.class.getClassLoader());
        this.thumbnailPath = parcel.readString();
        this.thumbnailDownloadUrl = parcel.readString();
        this.thumbnailDeadline = parcel.readLong();
        this.thumbnailKey = parcel.readString();
        this.videoPath = parcel.readString();
        this.videoDownloadUrl = parcel.readString();
        this.videoDeadline = parcel.readLong();
        this.videoKey = parcel.readString();
        this.sourceDownloadUrl = parcel.readString();
        this.sourceDeadline = parcel.readLong();
        this.sourceKey = parcel.readString();
        this.extInfo = parcel.readString();
        this.startActivity = parcel.readString();
        this.duration = parcel.readLong();
    }

    @Override
    public int type() {
        return TYPE_VIDEO;
    }

    @Override
    public void toBundle(Bundle bundle) {
        bundle.putString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_VIDEO_PATH, videoPath);
        bundle.putString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_THUMBNAIL_PATH, thumbnailPath);
        bundle.putString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_VIDEO_DOWNLOAD_URL, videoDownloadUrl);
        bundle.putLong(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_VIDEO_DEADLINE, videoDeadline);
        bundle.putString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_THUMBNAIL_DOWNLOAD_URL, thumbnailDownloadUrl);
        bundle.putLong(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_THUMBNAIL_DEADLINE, thumbnailDeadline);
        bundle.putString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_THUMBNAIL_KEY, thumbnailKey);
        bundle.putString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_VIDEO_KEY, videoKey);
        bundle.putString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_EXTINFO, extInfo);
        bundle.putString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_START_ACTIVITY, startActivity);
        bundle.putString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_SOURCE_DOWNLOAD_URL, sourceDownloadUrl);
        bundle.putLong(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_SOURCE_DEADLINE, sourceDeadline);
        bundle.putString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_SOURCE_KEY, sourceKey);
        bundle.putString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_TYPE, type);
        bundle.putString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_WANGSU_URL, wangSuUrl);
        bundle.putString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_ZONE, zone);
        bundle.putParcelable(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_CUSTOM_PARAM_MAP, customParamMap);
        bundle.putLong(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_DURATION, duration);
    }

    @Override
    public XTCVideoObject fromBundle(Bundle bundle) {
        XTCVideoObject videoObject = new XTCVideoObject();
        videoObject.setVideoPath(bundle.getString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_VIDEO_PATH));
        videoObject.setThumbnailPath(bundle.getString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_THUMBNAIL_PATH));
        videoObject.setExtInfo(bundle.getString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_EXTINFO));
        videoObject.setStartActivity(bundle.getString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_START_ACTIVITY));
        videoObject.setDuration(bundle.getLong(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_DURATION));
        videoObject.setVideoDownloadUrl(bundle.getString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_VIDEO_DOWNLOAD_URL));
        videoObject.setVideoDeadline(bundle.getLong(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_VIDEO_DEADLINE));
        videoObject.setVideoKey(bundle.getString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_VIDEO_KEY));
        videoObject.setThumbnailDownloadUrl(bundle.getString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_THUMBNAIL_DOWNLOAD_URL));
        videoObject.setThumbnailDeadline(bundle.getLong(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_THUMBNAIL_DEADLINE));
        videoObject.setThumbnailKey(bundle.getString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_THUMBNAIL_KEY));
        videoObject.setSourceDownloadUrl(bundle.getString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_SOURCE_DOWNLOAD_URL));
        videoObject.setSourceDeadline(bundle.getLong(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_SOURCE_DEADLINE));
        videoObject.setSourceKey(bundle.getString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_SOURCE_KEY));
        videoObject.setCustomParamMap(bundle.getParcelable(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_CUSTOM_PARAM_MAP));
        videoObject.setType(bundle.getString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_TYPE));
        videoObject.setWangSuUrl(bundle.getString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_WANGSU_URL));
        videoObject.setZone(bundle.getString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_ZONE));
        return videoObject;
    }

    @Override
    public BaseResponse checkArgs() {
        ShowMessageFromXTC.Response response = new ShowMessageFromXTC.Response();
        Log.d(TAG, "thumbnailDownloadUrl" + thumbnailDownloadUrl + "videoDownloadUrl" + videoDownloadUrl);
        if (videoPath != null && thumbnailPath != null) {
            if (thumbnailDownloadUrl == null && videoDownloadUrl == null) {
                Log.e(TAG, "checkArgs fail, video is sending");
                response.setCode(12);
                response.setErrorDesc("video_is_sending,video is sending");
                return response;
            }
            if (videoPath.length() > VIDEO_PATH_LENGTH_LIMIT) {
                Log.e(TAG, "checkArgs fail, video path is invalid");
                response.setCode(6);
                response.setErrorDesc("share_argument_error,path is invalid");
                return response;
            }
            if (thumbnailPath.length() > THUMBNAIL_PATH_LENGTH_LIMIT) {
                Log.e(TAG, "checkArgs fail, thumbnail path is invalid");
                response.setCode(6);
                response.setErrorDesc("share_argument_error, thumbnail path is invalid");
                return response;
            }
            String startActivity = this.startActivity;
            if (startActivity != null && startActivity.length() > LENGTH_LIMIT) {
                Log.e(TAG, "checkArgs fail, startActivity is too long");
                response.setCode(6);
                response.setErrorDesc("share_argument_error,startActivity is too long");
                return response;
            }
            String extInfo = this.extInfo;
            if (extInfo != null && extInfo.length() > LENGTH_LIMIT) {
                Log.e(TAG, "checkArgs fail, extInfo is too long");
                response.setCode(6);
                response.setErrorDesc("share_argument_error,extInfo is too long");
                return response;
            }
            response.setCode(1);
            return response;
        }
        response.setCode(1);
        return response;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getThumbnailDownloadUrl() {
        return thumbnailDownloadUrl;
    }

    public void setThumbnailDownloadUrl(String thumbnailDownloadUrl) {
        this.thumbnailDownloadUrl = thumbnailDownloadUrl;
    }

    public String getThumbnailKey() {
        return thumbnailKey;
    }

    public void setThumbnailKey(String thumbnailKey) {
        this.thumbnailKey = thumbnailKey;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getVideoDownloadUrl() {
        return videoDownloadUrl;
    }

    public void setVideoDownloadUrl(String videoDownloadUrl) {
        this.videoDownloadUrl = videoDownloadUrl;
    }

    public String getVideoKey() {
        return videoKey;
    }

    public void setVideoKey(String videoKey) {
        this.videoKey = videoKey;
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

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getThumbnailDeadline() {
        return thumbnailDeadline;
    }

    public void setThumbnailDeadline(long thumbnailDeadline) {
        this.thumbnailDeadline = thumbnailDeadline;
    }

    public long getVideoDeadline() {
        return videoDeadline;
    }

    public void setVideoDeadline(long videoDeadline) {
        this.videoDeadline = videoDeadline;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWangSuUrl() {
        return wangSuUrl;
    }

    public void setWangSuUrl(String wangSuUrl) {
        this.wangSuUrl = wangSuUrl;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public ParcelableMap getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(ParcelableMap customParamMap) {
        this.customParamMap = customParamMap;
    }

    public String getSourceDownloadUrl() {
        return sourceDownloadUrl;
    }

    public void setSourceDownloadUrl(String sourceDownloadUrl) {
        this.sourceDownloadUrl = sourceDownloadUrl;
    }

    public long getSourceDeadline() {
        return sourceDeadline;
    }

    public void setSourceDeadline(long sourceDeadline) {
        this.sourceDeadline = sourceDeadline;
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(type);
        parcel.writeString(wangSuUrl);
        parcel.writeString(zone);
        parcel.writeParcelable(customParamMap, flags);
        parcel.writeString(thumbnailPath);
        parcel.writeString(thumbnailDownloadUrl);
        parcel.writeLong(thumbnailDeadline);
        parcel.writeString(thumbnailKey);
        parcel.writeString(videoPath);
        parcel.writeString(videoDownloadUrl);
        parcel.writeLong(videoDeadline);
        parcel.writeString(videoKey);
        parcel.writeString(sourceDownloadUrl);
        parcel.writeLong(sourceDeadline);
        parcel.writeString(sourceKey);
        parcel.writeString(extInfo);
        parcel.writeString(startActivity);
        parcel.writeLong(duration);
    }

    public void readFromParcel(Parcel parcel) {
        this.type = parcel.readString();
        this.wangSuUrl = parcel.readString();
        this.zone = parcel.readString();
        this.customParamMap = parcel.readParcelable(ParcelableMap.class.getClassLoader());
        this.thumbnailPath = parcel.readString();
        this.thumbnailDownloadUrl = parcel.readString();
        this.thumbnailDeadline = parcel.readLong();
        this.thumbnailKey = parcel.readString();
        this.videoPath = parcel.readString();
        this.videoDownloadUrl = parcel.readString();
        this.videoDeadline = parcel.readLong();
        this.videoKey = parcel.readString();
        this.sourceDownloadUrl = parcel.readString();
        this.sourceDeadline = parcel.readLong();
        this.sourceKey = parcel.readString();
        this.extInfo = parcel.readString();
        this.startActivity = parcel.readString();
        this.duration = parcel.readLong();
    }

    @Override
    public String toString() {
        return "XTCVideoObject{type='" + type + "', wangSuUrl='" + wangSuUrl + "', zone='" + zone
                + "', customParamMap=" + customParamMap + ", thumbnailPath='" + thumbnailPath
                + "', thumbnailDownloadUrl='" + thumbnailDownloadUrl + "', thumbnailDeadline=" + thumbnailDeadline
                + ", thumbnailKey='" + thumbnailKey + "', videoPath='" + videoPath + "', videoDownloadUrl='"
                + videoDownloadUrl + "', videoDeadline=" + videoDeadline + ", videoKey='" + videoKey
                + "', sourceDownloadUrl='" + sourceDownloadUrl + "', sourceDeadline=" + sourceDeadline
                + ", sourceKey='" + sourceKey + "', extInfo='" + extInfo + "', startActivity='" + startActivity
                + "', duration=" + duration + '}';
    }

    public static final Parcelable.Creator<XTCVideoObject> CREATOR = new Parcelable.Creator<XTCVideoObject>() {
        @Override
        public XTCVideoObject createFromParcel(Parcel parcel) {
            return new XTCVideoObject(parcel);
        }

        @Override
        public XTCVideoObject[] newArray(int size) {
            return new XTCVideoObject[size];
        }
    };
}