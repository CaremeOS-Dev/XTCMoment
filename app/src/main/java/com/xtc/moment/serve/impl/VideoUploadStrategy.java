package com.xtc.moment.serve.impl;

import com.xtc.log.LogUtil;
import com.xtc.moment.module.bean.CloudFileResource;
import com.xtc.moment.module.bean.VideoMsg;
import com.xtc.moment.net.bean.VideoTokenVoResponse;
import com.xtc.moment.serve.VideoUploadManager;
import com.xtc.moment.serve.interfaces.IFileUploadStrategy;
import com.xtc.qiniu.ICloudManager;

/**
 * 视频文件上传策略：根据是否上传视频本体，选择对应的上传 token 与云端 key。
 */
public class VideoUploadStrategy implements IFileUploadStrategy<VideoMsg> {

    @Override
    public void uploadFileToCloud(String localPath, final VideoTokenVoResponse tokenVo, final VideoMsg videoMsg, final boolean needTransfer, final FileUploadListener listener) {
        String uploadToken;
        String key = null;
        if (needTransfer) {
            uploadToken = tokenVo.getUploadToken();
            CloudFileResource source = tokenVo.getSource();
            if (source != null) {
                key = source.getKey();
            }
        } else {
            uploadToken = tokenVo.getIconUploadToken();
            CloudFileResource icon = tokenVo.getIcon();
            if (icon != null) {
                key = icon.getKey();
            }
        }
        VideoUploadManager.uploadVideoFile(localPath, uploadToken, key, new ICloudManager.OnUpLoadListener() {
            @Override
            public void onSuccess(String key2) {
                LogUtil.i("MsgService", "uploadVideoFile onSuccess :" + key2);
                videoMsg.setCustomParamMap(tokenVo.getCustomParamMap());
                videoMsg.setIcon(tokenVo.getIcon());
                videoMsg.setSource(tokenVo.getSource());
                videoMsg.setTransfer(tokenVo.getTransfer());
                videoMsg.setType(tokenVo.getType());
                videoMsg.setWangSuUrl(tokenVo.getWangSuUrl());
                videoMsg.setZone(tokenVo.getZone());
                if (needTransfer) {
                    videoMsg.setVideoHasUpload(true);
                } else {
                    videoMsg.setThumnailHasUpload(true);
                }
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure(String key2, int code, String message) {
                if (listener != null) {
                    listener.onFail(key2, code, message);
                }
            }

            @Override
            public void onProgress(String key2, double percent) {
                LogUtil.e("MsgService", "key: " + key2 + " percent: " + percent);
            }
        });
    }
}