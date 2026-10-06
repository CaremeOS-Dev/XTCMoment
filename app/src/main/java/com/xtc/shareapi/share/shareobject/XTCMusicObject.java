package com.xtc.shareapi.share.shareobject;

import android.os.Bundle;
import android.util.Log;

import com.xtc.shareapi.share.communication.BaseResponse;
import com.xtc.shareapi.share.communication.ShowMessageFromXTC;
import com.xtc.shareapi.share.constant.OpenApiConstant;
import com.xtc.shareapi.share.interfaces.IShareObject;

/**
 * 音乐分享对象，包含默认/高低码率地址、时长与作者等信息。
 */
public class XTCMusicObject implements IShareObject {

    private static final int LENGTH_LIMIT = 1024;
    private static final String TAG = OpenApiConstant.TAG + XTCMusicObject.class.getSimpleName();

    private String musicUrl;
    private String musicLowUrl;
    private String musicHighUrl;
    private String extInfo;
    private String startActivity;
    private String musicName;
    private String author;
    private long duration;

    public XTCMusicObject() {
    }

    public XTCMusicObject(String musicUrl, String musicLowUrl, String musicHighUrl, String extInfo,
                          String startActivity, String musicName, String author, long duration) {
        this.musicUrl = musicUrl;
        this.musicLowUrl = musicLowUrl;
        this.musicHighUrl = musicHighUrl;
        this.extInfo = extInfo;
        this.startActivity = startActivity;
        this.musicName = musicName;
        this.author = author;
        this.duration = duration;
    }

    @Override
    public int type() {
        return TYPE_MUSIC;
    }

    @Override
    public void toBundle(Bundle bundle) {
        bundle.putString(OpenApiConstant.XTCMusicConstant.BUNDLE_MUSIC_DEFAULT_URL, musicUrl);
        bundle.putString(OpenApiConstant.XTCMusicConstant.BUNDLE_MUSIC_HIGH_URL, musicHighUrl);
        bundle.putString(OpenApiConstant.XTCMusicConstant.BUNDLE_MUSIC_LOW_URL, musicLowUrl);
        bundle.putString(OpenApiConstant.XTCMusicConstant.BUNDLE_MUSIC_AUTHOR, author);
        bundle.putLong(OpenApiConstant.XTCMusicConstant.BUNDLE_MUSIC_DURATION, duration);
        bundle.putString(OpenApiConstant.XTCMusicConstant.BUNDLE_MUSIC_EXTEND, extInfo);
        bundle.putString(OpenApiConstant.XTCMusicConstant.BUNDLE_MUSIC_NAME, musicName);
        bundle.putString(OpenApiConstant.XTCMusicConstant.BUNDLE_MUSIC_START_ACTIVITY, startActivity);
    }

    @Override
    public XTCMusicObject fromBundle(Bundle bundle) {
        XTCMusicObject musicObject = new XTCMusicObject();
        musicObject.setMusicUrl(bundle.getString(OpenApiConstant.XTCMusicConstant.BUNDLE_MUSIC_DEFAULT_URL));
        musicObject.setMusicLowUrl(bundle.getString(OpenApiConstant.XTCMusicConstant.BUNDLE_MUSIC_LOW_URL));
        musicObject.setMusicHighUrl(bundle.getString(OpenApiConstant.XTCMusicConstant.BUNDLE_MUSIC_HIGH_URL));
        musicObject.setAuthor(bundle.getString(OpenApiConstant.XTCMusicConstant.BUNDLE_MUSIC_AUTHOR));
        musicObject.setExtInfo(bundle.getString(OpenApiConstant.XTCMusicConstant.BUNDLE_MUSIC_EXTEND));
        musicObject.setMusicName(bundle.getString(OpenApiConstant.XTCMusicConstant.BUNDLE_MUSIC_NAME));
        musicObject.setStartActivity(bundle.getString(OpenApiConstant.XTCMusicConstant.BUNDLE_MUSIC_START_ACTIVITY));
        musicObject.setDuration(bundle.getLong(OpenApiConstant.XTCMusicConstant.BUNDLE_MUSIC_DURATION));
        return musicObject;
    }

    @Override
    public BaseResponse checkArgs() {
        ShowMessageFromXTC.Response response = new ShowMessageFromXTC.Response();
        String musicUrl = this.musicUrl;
        if ((musicUrl != null && musicUrl.length() != 0)
                || ((this.musicLowUrl != null && this.musicHighUrl.length() != 0)
                || (this.musicHighUrl != null && this.musicHighUrl.length() != 0))) {
            String musicUrl2 = this.musicUrl;
            if (musicUrl2 != null && musicUrl2.length() > LENGTH_LIMIT) {
                Log.e(TAG, "checkArgs fail, musicUrl is too long");
                response.setCode(6);
                response.setErrorDesc("share_argument_error,musicUrl is too long");
                return response;
            }
            String musicLowUrl = this.musicLowUrl;
            if (musicLowUrl != null && musicLowUrl.length() > LENGTH_LIMIT) {
                Log.e(TAG, "checkArgs fail, musicLowBandUrl is too long");
                response.setCode(6);
                response.setErrorDesc("share_argument_error,musicLowBandUrl is too long");
                return response;
            }
            String musicHighUrl = this.musicHighUrl;
            if (musicHighUrl != null && musicHighUrl.length() > LENGTH_LIMIT) {
                Log.e(TAG, "checkArgs fail, musicHighBandUrl is too long");
                response.setCode(6);
                response.setErrorDesc("share_argument_error,musicHighBandUrl is too long");
                return response;
            }
            String musicName = this.musicName;
            if (musicName != null && musicName.length() > LENGTH_LIMIT) {
                Log.e(TAG, "checkArgs fail, musicName is too long");
                response.setCode(6);
                response.setErrorDesc("share_argument_error,musicName is too long");
                return response;
            }
            String author = this.author;
            if (author != null && author.length() > LENGTH_LIMIT) {
                Log.e(TAG, "checkArgs fail, author name is too long");
                response.setCode(6);
                response.setErrorDesc("share_argument_error,author name is too long");
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
        Log.e(TAG, "both arguments are null");
        response.setCode(6);
        response.setErrorDesc("share_argument_error,both arguments are null");
        return response;
    }

    public String getMusicUrl() {
        return musicUrl;
    }

    public void setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
    }

    public String getMusicLowUrl() {
        return musicLowUrl;
    }

    public void setMusicLowUrl(String musicLowUrl) {
        this.musicLowUrl = musicLowUrl;
    }

    public String getMusicHighUrl() {
        return musicHighUrl;
    }

    public void setMusicHighUrl(String musicHighUrl) {
        this.musicHighUrl = musicHighUrl;
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

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "XTCMusicObject{musicUrl='" + musicUrl + "', musicLowUrl='" + musicLowUrl + "', musicHighUrl='"
                + musicHighUrl + "', extInfo='" + extInfo + "', startActivity='" + startActivity + "', musicName='"
                + musicName + "', author='" + author + "', duration=" + duration + '}';
    }
}