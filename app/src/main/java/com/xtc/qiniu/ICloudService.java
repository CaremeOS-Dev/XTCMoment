package com.xtc.qiniu;

import android.content.Context;

import com.xtc.log.LogUtil;

import java.io.File;

/** Facade over the cloud-storage backend. */
public class ICloudService {

    public static final int Type_QiNiu = 1;
    public static final int Type_Wcs = 2;

    private static int iCloudType = Type_QiNiu;
    public static String qiNiuDomain;

    private ICloudService() {
    }

    public static void setICloudType(int type) {
        if (iCloudType != type) {
            LogUtil.d("切换云存储原类型=" + iCloudType + ", 新类型=" + type);
            iCloudType = type;
        }
    }

    public static void setMaxUploadLimit(int limit) {
        UploadLimitAgent.getInstance().setMaxUploadLimit(limit);
    }

    public static void setQiNiuDomain(String domain) {
        qiNiuDomain = domain;
    }

    private static ICloudManager getInstance(Context context) {
        return QiNiuManager.getInstance(context.getApplicationContext(), qiNiuDomain);
    }

    public static String upLoadFileByCover(Context context, int spaceType, String key, File file,
            ICloudManager.OnUpLoadListener listener) {
        return UploadLimitAgent.getInstance().upLoadFileByCover(context, spaceType, key, file, listener);
    }

    public static String upLoadFile(Context context, int spaceType, String key, File file,
            ICloudManager.OnUpLoadListener listener) {
        return UploadLimitAgent.getInstance().upLoadFile(context, spaceType, key, file, listener);
    }

    public static String upLoadData(Context context, int spaceType, String key, byte[] data,
            ICloudManager.OnUpLoadListener listener) {
        return getInstance(context).uploadData(spaceType, key, data, listener);
    }

    public static String uploadFile(Context context, String filePath, String key, String token,
            ICloudManager.OnUpLoadListener listener) {
        return UploadLimitAgent.getInstance().uploadFile(context, filePath, key, token, listener);
    }

    public static void downLoadForFile(Context context, String url, String directory, String name,
            ICloudManager.OnDownLoadListener listener) {
        getInstance(context).downloadForFile(url, directory, name, listener);
    }

    public static void downLoadForByte(Context context, String url, ICloudManager.OnDownLoadListener listener) {
        getInstance(context).downloadForBytes(url, listener);
    }

    public static void cancle(Context context, String tag) {
        getInstance(context).cancle(tag);
        UploadLimitAgent.getInstance().cancel(tag);
    }
}