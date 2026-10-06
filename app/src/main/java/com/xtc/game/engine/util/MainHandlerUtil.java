package com.xtc.game.engine.util;

import android.os.Handler;
import android.os.Looper;

/**
 * 主线程 Handler 工具。
 */
public class MainHandlerUtil {

    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    public static void post(Runnable runnable) {
        MAIN_HANDLER.post(runnable);
    }

    public static void postDelayed(Runnable runnable, long delayMillis) {
        MAIN_HANDLER.postDelayed(runnable, delayMillis);
    }
}