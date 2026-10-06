package com.xtc.aitext.util;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.MessageQueue;

/**
 * 线程调度工具，提供主线程与后台线程任务派发。
 */
public class AITextHandlerUtil {

    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());
    private static final Handler BACKGROUND_HANDLER;

    static {
        HandlerThread handlerThread = new HandlerThread("HandlerUtil-background_thread", 10);
        handlerThread.start();
        BACKGROUND_HANDLER = new Handler(handlerThread.getLooper());
    }

    public static void runOnMain(Runnable runnable) {
        MAIN_HANDLER.post(runnable);
    }

    public static void runOnMainDelay(Runnable runnable, long delayMillis) {
        MAIN_HANDLER.postDelayed(runnable, delayMillis);
    }

    public static void runOnBackground(Runnable runnable) {
        BACKGROUND_HANDLER.post(runnable);
    }

    public static void runOnBackgroundDelay(Runnable runnable, long delayMillis) {
        BACKGROUND_HANDLER.postDelayed(runnable, delayMillis);
    }

    public static void removeFromBackground(Runnable runnable) {
        BACKGROUND_HANDLER.removeCallbacks(runnable);
    }

    public static void removeFromMain(Runnable runnable) {
        MAIN_HANDLER.removeCallbacks(runnable);
    }

    /** 在主线程空闲时执行任务。 */
    public static void runOnIdle(final Runnable runnable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Looper.getMainLooper().getQueue().addIdleHandler(new MessageQueue.IdleHandler() {
                @Override
                public boolean queueIdle() {
                    runnable.run();
                    return false;
                }
            });
        } else {
            runnable.run();
        }
    }
}