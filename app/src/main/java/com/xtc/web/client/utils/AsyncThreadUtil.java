package com.xtc.web.client.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/** web.client 的主线程/后台线程切换工具。 */
public class AsyncThreadUtil {

    private static Handler backgroundHandler;
    private static Handler mainHandler = new Handler(Looper.getMainLooper());

    static {
        HandlerThread handlerThread = new HandlerThread("web-core-async-thread", 10);
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper());
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

    public static void removeCallbacksAndMessages() {
        mainHandler.removeCallbacksAndMessages(null);
        backgroundHandler.removeCallbacksAndMessages(null);
    }
}