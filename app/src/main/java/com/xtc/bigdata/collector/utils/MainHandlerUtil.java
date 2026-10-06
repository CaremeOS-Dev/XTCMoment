package com.xtc.bigdata.collector.utils;

import android.os.Handler;
import android.os.Looper;

/** Main-thread posting helper. */
public class MainHandlerUtil {

    private static Handler mainHandler = new Handler(Looper.getMainLooper());

    private MainHandlerUtil() {
    }

    public static void post(Runnable runnable) {
        mainHandler.post(runnable);
    }

    public static void postDelay(Runnable runnable, long delayMillis) {
        mainHandler.postDelayed(runnable, delayMillis);
    }
}