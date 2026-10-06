package com.xtc.moment.util;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.MessageQueue;

import com.xtc.log.LogUtil;

/** Main/background handler helpers of the moment app. */
public class HandlerUtil {

    private static final String TAG = "HandlerUtil";

    private static Handler backgroundHandler;
    private static boolean hasInit;
    private static Handler mainHandler = new Handler(Looper.getMainLooper());

    private HandlerUtil() {
    }

    /** Initializes the main and background handlers. */
    public static void init() {
        synchronized (HandlerUtil.class) {
            if (hasInit) {
                return;
            }
            mainHandler = new Handler(Looper.getMainLooper());
            HandlerThread handlerThread = new HandlerThread("background_thread", 19);
            handlerThread.start();
            backgroundHandler = new Handler(handlerThread.getLooper());
            hasInit = true;
        }
    }

    public boolean isHasInit() {
        return hasInit;
    }

    /** Runs [runnable] on the main thread, directly when already there. */
    public static void runOnUIThread(Runnable runnable) {
        if (ThreadCheck.isMainThread()) {
            runnable.run();
        } else {
            checkHasInit();
            mainHandler.post(runnable);
        }
    }

    /** Always posts [runnable] to the main thread. */
    public static void runOnUIThreadNoCheck(Runnable runnable) {
        checkHasInit();
        mainHandler.post(runnable);
    }

    public static void runOnUIThreadDelay(Runnable runnable, long delayMillis) {
        checkHasInit();
        mainHandler.postDelayed(runnable, delayMillis);
    }

    public static void runOnBackground(Runnable runnable) {
        runOnBackground(runnable, false);
    }

    /**
     * Runs [runnable] on the background thread.
     *
     * @param forcePost when true the runnable is always posted instead of run inline
     */
    public static void runOnBackground(Runnable runnable, boolean forcePost) {
        if (runnable == null) {
            LogUtil.d(TAG, "r is null");
            return;
        }
        if (!forcePost && !ThreadCheck.isMainThread()) {
            runnable.run();
            return;
        }
        checkHasInit();
        if (backgroundHandler.getLooper() == Looper.myLooper()) {
            runnable.run();
        } else {
            backgroundHandler.post(runnable);
        }
    }

    public static void runOnBackgroundDelay(Runnable runnable, long delayMillis) {
        checkHasInit();
        backgroundHandler.postDelayed(runnable, delayMillis);
    }

    public static void removeBackgroundTask(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        checkHasInit();
        backgroundHandler.removeCallbacks(runnable);
    }

    private static synchronized void checkHasInit() {
        if (hasInit) {
            return;
        }
        LogUtil.i(TAG, "HandlerUtil is not init,start init");
        init();
    }

    /** Runs [runnable] when the main thread becomes idle. */
    public static void executeWhenMainThreadIdle(final Runnable runnable) {
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