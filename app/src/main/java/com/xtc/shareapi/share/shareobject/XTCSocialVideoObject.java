package com.xtc.shareapi.share.shareobject;

import android.os.Bundle;
import android.util.Log;

import com.xtc.shareapi.share.bean.ParcelableMap;
import com.xtc.shareapi.share.communication.BaseResponse;
import com.xtc.shareapi.share.communication.ShowMessageFromXTC;
import com.xtc.shareapi.share.constant.OpenApiConstant;
import com.xtc.shareapi.share.interfaces.IShareObject;

/**
 * 社交平台视频分享对象，在原视频基础上额外携带原始视频路径。
 */
public class XTCSocialVideoObject implements IShareObject {

    private static final int LENGTH_LIMIT = 1024;
    private static final int THUMBNAIL_PATH_LENGTH_LIMIT = 512;
    private static final int VIDEO_PATH_LENGTH_LIMIT = 512;
    private static final String TAG = OpenApiConstant.TAG + XTCSocialVideoObject.class.getSimpleName();

    private String type;
    private String wangSuUrl;
    private String zone;
    private ParcelableMap customParamMap;
    private String thumbnailPath;
    private String thumbnailDownloadUrl;
    private long thumbnailDeadline;
    private String thumbnailKey;
    private String videoPath;
    private String originVideoPath;
    private String videoDownloadUrl;
    private long videoDeadline;
    private String videoKey;
    private String sourceDownloadUrl;
    private long sourceDeadline;
    private String sourceKey;
    private String extInfo;
    private String startActivity;
    private long duration;

    @Override
    public int type() {
        return TYPE_SOCIAL_VIDEO;
    }

    @Override
    public void toBundle(Bundle bundle) {
        bundle.putString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_VIDEO_PATH, videoPath);
        bundle.putString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_ORIGIN_VIDEO_PATH, originVideoPath);
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
    public XTCSocialVideoObject fromBundle(Bundle bundle) {
        XTCSocialVideoObject socialVideoObject = new XTCSocialVideoObject();
        socialVideoObject.setVideoPath(bundle.getString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_VIDEO_PATH));
        socialVideoObject.setOriginVideoPath(bundle.getString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_ORIGIN_VIDEO_PATH));
        socialVideoObject.setThumbnailPath(bundle.getString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_THUMBNAIL_PATH));
        socialVideoObject.setExtInfo(bundle.getString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_EXTINFO));
        socialVideoObject.setStartActivity(bundle.getString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_START_ACTIVITY));
        socialVideoObject.setDuration(bundle.getLong(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_DURATION));
        socialVideoObject.setVideoDownloadUrl(bundle.getString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_VIDEO_DOWNLOAD_URL));
        socialVideoObject.setVideoDeadline(bundle.getLong(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_VIDEO_DEADLINE));
        socialVideoObject.setVideoKey(bundle.getString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_VIDEO_KEY));
        socialVideoObject.setThumbnailDownloadUrl(bundle.getString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_THUMBNAIL_DOWNLOAD_URL));
        socialVideoObject.setThumbnailDeadline(bundle.getLong(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_THUMBNAIL_DEADLINE));
        socialVideoObject.setThumbnailKey(bundle.getString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_THUMBNAIL_KEY));
        socialVideoObject.setSourceDownloadUrl(bundle.getString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_SOURCE_DOWNLOAD_URL));
        socialVideoObject.setSourceDeadline(bundle.getLong(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_SOURCE_DEADLINE));
        socialVideoObject.setSourceKey(bundle.getString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_SOURCE_KEY));
        socialVideoObject.setCustomParamMap(bundle.getParcelable(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_CUSTOM_PARAM_MAP));
        socialVideoObject.setType(bundle.getString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_TYPE));
        socialVideoObject.setWangSuUrl(bundle.getString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_WANGSU_URL));
        socialVideoObject.setZone(bundle.getString(OpenApiConstant.XTCVideoConstant.BUNDLE_VIDEO_ZONE));
        return socialVideoObject;
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
            if (originVideoPath.length() > VIDEO_PATH_LENGTH_LIMIT) {
                Log.e(TAG, "checkArgs fail, originVideo path is invalid");
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

    public String getOriginVideoPath() {
        return originVideoPath;
    }

    public void setOriginVideoPath(String originVideoPath) {
        this.originVideoPath = originVideoPath;
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
    public String toString() {
        return "XTCVideoObject{type='" + type + "', wangSuUrl='" + wangSuUrl + "', zone='" + zone
                + "', customParamMap=" + customParamMap + ", thumbnailPath='" + thumbnailPath
                + "', thumbnailDownloadUrl='" + thumbnailDownloadUrl + "', thumbnailDeadline=" + thumbnailDeadline
                + ", thumbnailKey='" + thumbnailKey + "', videoPath='" + videoPath + "', originVideoPath='"
                + originVideoPath + "', videoDownloadUrl='" + videoDownloadUrl + "', videoDeadline=" + videoDeadline
                + ", videoKey='" + videoKey + "', sourceDownloadUrl='" + sourceDownloadUrl + "', sourceDeadline="
                + sourceDeadline + ", sourceKey='" + sourceKey + "', extInfo='" + extInfo + "', startActivity='"
                + startActivity + "', duration=" + duration + '}';
    }
}