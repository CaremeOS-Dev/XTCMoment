package com.xtc.moment.serve;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.bigdata.common.utils.ContextUtils;
import com.xtc.log.LogUtil;
import com.xtc.qiniu.ICloudManager;
import com.xtc.qiniu.ICloudService;
import com.xtc.utils.encode.UUIDUtil;
import com.xtc.utils.system.NetworkUtils;

import java.io.File;

/**
 * 视频/封面文件上传管理器：封装七牛上传、取消与本地文件清理。
 */
public class VideoUploadManager {

    private static final String TAG = "UploadFile";
    private static int VIDEO_SPACE_TYPE = 1;
    private static String currentKey;

    public interface OnUploadCompleteListener {
        void onCompletion(String key);

        void onFailed(String message);
    }

    public interface UploadFileCallback {
        void onUploadFileFailure(String message);

        void onUploadFileSuccess(String key);
    }

    /**
     * 仅上传文件封面。
     */
    public static void uploadFileByCover(Context context, String filePath) {
        String key = UUIDUtil.randomUUID();
        File file = new File(filePath);
        if (!file.exists()) {
            LogUtil.e(TAG, "uploadFile but file not exist. file:" + file);
            return;
        }
        ICloudService.upLoadFileByCover(context, VIDEO_SPACE_TYPE, key, file, new ICloudManager.OnUpLoadListener() {
            @Override
            public void onFailure(String key2, int code, String message) {
            }

            @Override
            public void onProgress(String key2, double percent) {
            }

            @Override
            public void onSuccess(String key2) {
                LogUtil.i("VideoUploadManager", "congratulation :) contact head upload to qiNiu success.");
            }
        });
    }

    public static void uploadFile(Context context, final String fileName, String dirPath, final boolean deleteAfterSuccess, final UploadFileCallback callback) {
        LogUtil.d(TAG, "uploadFile filePath = " + dirPath + "---fileName = " + fileName);
        final File file = new File(dirPath + fileName);
        if (!file.exists()) {
            LogUtil.e(TAG, "uploadFile file not exists");
            if (callback != null) {
                callback.onUploadFileFailure("file not exists");
            }
            return;
        }
        if (!NetworkUtils.isNetworkAvailable(context)) {
            LogUtil.e(TAG, "uploadFile Network is not Connected");
            if (callback != null) {
                callback.onUploadFileFailure("Network is not Connected");
            }
            return;
        }
        currentKey = ICloudService.upLoadFile(context, VIDEO_SPACE_TYPE, fileName, file, new ICloudManager.OnUpLoadListener() {
            @Override
            public void onProgress(String key, double percent) {
                LogUtil.d(VideoUploadManager.TAG, "onProgress s = " + key + "---v = " + percent);
            }

            @Override
            public void onSuccess(String key) {
                LogUtil.d(VideoUploadManager.TAG, "onSuccess s = " + key);
                if (deleteAfterSuccess) {
                    VideoUploadManager.deleteFile(file.getAbsolutePath());
                }
                VideoUploadManager.currentKey = null;
                if (callback != null) {
                    callback.onUploadFileSuccess(fileName);
                }
            }

            @Override
            public void onFailure(String key, int code, String message) {
                LogUtil.d(VideoUploadManager.TAG, "onFailure s = " + key + "---i = " + code + "---s1 = " + message);
                VideoUploadManager.currentKey = null;
                if (callback != null) {
                    callback.onUploadFileFailure(key + "---" + code + "---" + message);
                }
            }
        });
        LogUtil.d("lee", "uploadFile currentKey = " + currentKey);
    }

    public static void cancelUpload(Context context) {
        if (TextUtils.isEmpty(currentKey)) {
            return;
        }
        ICloudService.cancle(context, currentKey);
    }

    public static void deleteFile(String path) {
        LogUtil.d(TAG, "deleteFile filePath = " + path);
        File file = new File(path);
        if (!file.exists()) {
            LogUtil.e(TAG, "deleteFile fileFolder not exists");
            return;
        }
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children == null || children.length == 0) {
                file.delete();
                return;
            }
            for (File child : children) {
                deleteFile(child.getAbsolutePath());
            }
            file.delete();
        }
    }

    public static String uploadVideoFile(String filePath, String uploadToken, String key, ICloudManager.OnUpLoadListener listener) {
        return ICloudService.uploadFile(ContextUtils.getContext(), filePath, key, uploadToken, listener);
    }
}