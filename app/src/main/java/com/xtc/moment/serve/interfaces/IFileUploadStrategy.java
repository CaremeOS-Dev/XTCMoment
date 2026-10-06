package com.xtc.moment.serve.interfaces;

import com.xtc.moment.net.bean.VideoTokenVoResponse;

/**
 * 文件上传策略。
 */
public interface IFileUploadStrategy<T> {

    interface FileUploadListener {
        void onSuccess();

        void onFail(String message, int code, String extra);
    }

    void uploadFileToCloud(String localPath, VideoTokenVoResponse tokenVo, T extra, boolean needTransfer,
            FileUploadListener listener);
}