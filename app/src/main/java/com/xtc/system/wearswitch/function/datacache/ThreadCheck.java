package com.xtc.system.wearswitch.function.datacache;

import android.os.Looper;

import com.xtc.log.LogUtil;

/**
 * 线程检测工具。
 */
public class ThreadCheck {

    private static final String TAG = ThreadCheck.class.getSimpleName();

    /** 当前是否为主线程。 */
    public static boolean isMainThread() {
        boolean isMainThread = Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId();
        LogUtil.d(TAG, "isMainThread:" + isMainThread);
        return isMainThread;
    }
}