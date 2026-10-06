package com.xtc.moment.share.callback;

/**
 * 分享结果回调。
 */
public interface IShareCallback {
    void sendShare();

    void cancelShare();

    void sendSuccessResult();

    void sendOtherResponse(Throwable throwable);
}