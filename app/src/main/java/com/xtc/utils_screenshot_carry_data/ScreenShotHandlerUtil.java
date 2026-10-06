package com.xtc.utils_screenshot_carry_data;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.MessageQueue;

import com.xtc.log.LogUtil;

/** Shared main-thread and background-thread handlers for the screenshot pipeline. */
class ScreenShotHandlerUtil {

    private static final String TAG = "HandlerUtil";

    private static Handler mainHandler = new Handler(Looper.getMainLooper());
    private static Handler backgroundHandler;
    private static boolean initialized;

    ScreenShotHandlerUtil() {
    }

    /** Initializes the background thread and handler once. */
    public static void init() {
        synchronized (ScreenShotHandlerUtil.class) {
            if (initialized) {
                return;
            }
            mainHandler = new Handler(Looper.getMainLooper());
            HandlerThread handlerThread = new HandlerThread("background_thread", 19);
            handlerThread.start();
            backgroundHandler = new Handler(handlerThread.getLooper());
            initialized = true;
        }
    }

    /** @return true when the handlers have been initialized. */
    public boolean isInitialized() {
        return initialized;
    }

    /** Posts {@code runnable} to the main thread. */
    public static void post(Runnable runnable) {
        ensureInitialized();
        mainHandler.post(runnable);
    }

    /** Posts {@code runnable} to the main thread after {@code delayMillis}. */
    public static void postDelayed(Runnable runnable, long delayMillis) {
        ensureInitialized();
        mainHandler.postDelayed(runnable, delayMillis);
    }

    /** Runs {@code runnable} on the background thread, or inline when already on the main thread. */
    public static void runOnBackground(Runnable runnable) {
        if (runnable == null) {
            LogUtil.d(TAG, "r is null");
        } else if (!isMainThread()) {
            runnable.run();
        } else {
            ensureInitialized();
            backgroundHandler.post(runnable);
        }
    }

    /** @return true when the current thread is the main thread. */
    private static boolean isMainThread() {
        boolean isMain = Looper.getMainLooper() == Looper.myLooper();
        LogUtil.d(TAG, "isMainThread:" + isMain);
        return isMain;
    }

    /** Posts {@code runnable} to the background thread after {@code delayMillis}. */
    public static void postBackgroundDelayed(Runnable runnable, long delayMillis) {
        ensureInitialized();
        backgroundHandler.postDelayed(runnable, delayMillis);
    }

    /** Initializes the handlers when they have not been set up yet. */
    private static synchronized void ensureInitialized() {
        if (initialized) {
            return;
        }
        LogUtil.i(TAG, "HandlerUtil is not init,start init");
        init();
    }

    /** Runs {@code runnable} on the next idle of the main thread when supported. */
    public static void runOnIdle(final Runnable runnable) {
        if (Build.VERSION.SDK_INT >= 23) {
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