package com.xtc.qiniu;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/** Background/main handler pair used by the upload queue. */
class HandlerUtil {

    private static Handler backgroundHandler;
    private static Handler mainHandler = new Handler(Looper.getMainLooper());

    static {
        HandlerThread handlerThread = new HandlerThread("background_thread", 10);
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper());
    }

    private HandlerUtil() {
    }

    public static void runOnUIThread(Runnable runnable) {
        mainHandler.post(runnable);
    }

    public static void runOnUIThreadDelay(Runnable runnable, long delayMillis) {
        mainHandler.postDelayed(runnable, delayMillis);
    }

    public static void runOnBackground(Runnable runnable) {
        backgroundHandler.post(runnable);
    }

    public static void runOnBackgroundDelay(Runnable runnable, long delayMillis) {
        backgroundHandler.postDelayed(runnable, delayMillis);
    }
}