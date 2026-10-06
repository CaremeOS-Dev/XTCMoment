package com.xtc.shareapi.share.shareobject;

import android.os.Bundle;
import android.util.Log;

import com.xtc.shareapi.share.communication.BaseResponse;
import com.xtc.shareapi.share.communication.ShowMessageFromXTC;
import com.xtc.shareapi.share.constant.OpenApiConstant;
import com.xtc.shareapi.share.interfaces.IBundleSerialize;

/**
 * 云端文件资源，包含下载地址、有效期与展示尺寸。
 */
public class ShareCloudFileResource implements IBundleSerialize {

    private static final int DOWNLOAD_URL_LENGTH_LIMIT = 2048;
    private static final int KEY_LENGTH_LIMIT = 1024;
    private static final String TAG = OpenApiConstant.TAG + ShareCloudFileResource.class.getSimpleName();

    public static final int WIDTH = 320;
    public static final int HEIGHT = 360;

    private String key;
    private String downloadUrl;
    private long urlDeadline;
    private int width = WIDTH;
    private int height = HEIGHT;

    public ShareCloudFileResource() {
    }

    public ShareCloudFileResource(String key, String downloadUrl, long urlDeadline) {
        this.key = key;
        this.downloadUrl = downloadUrl;
        this.urlDeadline = urlDeadline;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public long getUrlDeadline() {
        return urlDeadline;
    }

    public void setUrlDeadline(long urlDeadline) {
        this.urlDeadline = urlDeadline;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void toBundle(Bundle bundle) {
        bundle.putString(OpenApiConstant.ShareCloudFileConstant.BUNDLE_FILE_DOWNLOAD_URL, downloadUrl);
        bundle.putString(OpenApiConstant.ShareCloudFileConstant.BUNDLE_FILE_KEY, key);
        bundle.putLong(OpenApiConstant.ShareCloudFileConstant.BUNDLE_FILE_URL_DEADLINE, urlDeadline);
        bundle.putInt(OpenApiConstant.ShareCloudFileConstant.BUNDLE_FILE_WIDTH, width);
        bundle.putInt(OpenApiConstant.ShareCloudFileConstant.BUNDLE_FILE_HEIGHT, height);
    }

    @Override
    public IBundleSerialize fromBundle(Bundle bundle) {
        ShareCloudFileResource resource = new ShareCloudFileResource();
        resource.setDownloadUrl(bundle.getString(OpenApiConstant.ShareCloudFileConstant.BUNDLE_FILE_DOWNLOAD_URL));
        resource.setKey(bundle.getString(OpenApiConstant.ShareCloudFileConstant.BUNDLE_FILE_KEY));
        resource.setUrlDeadline(bundle.getLong(OpenApiConstant.ShareCloudFileConstant.BUNDLE_FILE_URL_DEADLINE));
        resource.setWidth(bundle.getInt(OpenApiConstant.ShareCloudFileConstant.BUNDLE_FILE_WIDTH));
        resource.setHeight(bundle.getInt(OpenApiConstant.ShareCloudFileConstant.BUNDLE_FILE_HEIGHT));
        return resource;
    }

    @Override
    public BaseResponse checkArgs() {
        ShowMessageFromXTC.Response response = new ShowMessageFromXTC.Response();
        String key = this.key;
        if (key != null && key.length() > KEY_LENGTH_LIMIT) {
            Log.e(TAG, "checkArgs fail, key is invalid");
            response.setCode(6);
            response.setErrorDesc("checkArgs fail, key is invalid");
            return response;
        }
        String downloadUrl = this.downloadUrl;
        if (downloadUrl != null && downloadUrl.length() > DOWNLOAD_URL_LENGTH_LIMIT) {
            Log.e(TAG, "checkArgs fail, downloadUrl is null");
            response.setCode(6);
            response.setErrorDesc("checkArgs fail, downloadUrl is null");
            return response;
        }
        // 原实现连续三次判断 urlDeadline != 0，此处保持一致。
        long urlDeadline = this.urlDeadline;
        if (urlDeadline != 0) {
            Log.e(TAG, "checkArgs fail, urlDeadline is 0");
            response.setCode(6);
            response.setErrorDesc("checkArgs fail, urlDeadline is 0");
            return response;
        }
        if (urlDeadline != 0) {
            Log.e(TAG, "checkArgs fail, urlDeadline is 0");
            response.setCode(6);
            response.setErrorDesc("checkArgs fail, urlDeadline is 0");
            return response;
        }
        if (urlDeadline != 0) {
            Log.e(TAG, "checkArgs fail, urlDeadline is 0");
            response.setCode(6);
            response.setErrorDesc("checkArgs fail, urlDeadline is 0");
            return response;
        }
        response.setCode(1);
        return response;
    }

    @Override
    public String toString() {
        return "CloudFileResource{key='" + key + "', downloadUrl='" + downloadUrl + "', urlDeadline="
                + urlDeadline + ", width=" + width + ", height=" + height + '}';
    }
}