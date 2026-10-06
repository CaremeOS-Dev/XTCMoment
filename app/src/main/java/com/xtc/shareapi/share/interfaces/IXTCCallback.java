package com.xtc.shareapi.share.interfaces;

import android.content.Intent;

/**
 * 外部应用响应分享请求（Intent 方式）的回调。
 */
public interface IXTCCallback {

    /** 处理外部应用发来的分享 Intent。 */
    boolean handleIntent(Intent intent, IResponseCallback responseCallback);
}