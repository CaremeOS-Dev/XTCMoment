package com.xtc.shareapi.share.manager;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * 分享流程的线程调度工具，提供主线程与后台线程任务派发。
 */
class ShareHandlerUtil {

    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());
    private static final Handler BACKGROUND_HANDLER;

    static {
        HandlerThread handlerThread = new HandlerThread("background_thread", 10);
        handlerThread.start();
        BACKGROUND_HANDLER = new Handler(handlerThread.getLooper());
    }

    public static void runOnUIThread(Runnable runnable) {
        MAIN_HANDLER.post(runnable);
    }

    public static void runOnUIThreadDelay(Runnable runnable, long delayMillis) {
        MAIN_HANDLER.postDelayed(runnable, delayMillis);
    }

    public static void runOnBackground(Runnable runnable) {
        BACKGROUND_HANDLER.post(runnable);
    }

    public static void runOnBackgroundDelay(Runnable runnable, long delayMillis) {
        BACKGROUND_HANDLER.postDelayed(runnable, delayMillis);
    }
}