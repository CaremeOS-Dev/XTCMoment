package com.xtc.aitext.util;

import android.os.SystemClock;

import com.xtc.log.LogUtil;

/**
 * 点击防抖工具。
 */
public class AITextClickTimeUtil {

    private static final String TAG = "ai_text_AITextClickTimeUtil";
    private static final long CLICK_INTERVAL = 1000;

    /** 距离上次点击不足 1 秒时返回 true。 */
    public static boolean isFastClick(long lastClickTime) {
        if (SystemClock.elapsedRealtime() - lastClickTime >= CLICK_INTERVAL) {
            return false;
        }
        LogUtil.d(TAG, "click to fast");
        return true;
    }
}