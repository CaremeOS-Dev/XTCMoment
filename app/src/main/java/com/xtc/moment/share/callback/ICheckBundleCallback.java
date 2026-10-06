package com.xtc.moment.share.callback;

import android.content.Intent;

/**
 * 分享参数校验回调。
 */
public interface ICheckBundleCallback {
    void checkSuccess();

    void sendResponse(Intent intent);
}