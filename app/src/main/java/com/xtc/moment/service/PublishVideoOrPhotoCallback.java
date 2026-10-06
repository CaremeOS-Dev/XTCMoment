package com.xtc.moment.service;

import com.xtc.moment.db.bean.DbMoment;

/**
 * 视频/图片发布进度回调。
 */
public interface PublishVideoOrPhotoCallback {
    void onProgress(int progress);

    void onPublishSuccess(DbMoment moment);

    void onFail(Throwable throwable);
}