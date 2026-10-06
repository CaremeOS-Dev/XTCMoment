package com.xtc.log.crash;

import com.xtc.log.LogUtil;

/**
 * Last-chance handler for uncaught exceptions.
 *
 * <p>Installed by {@link com.xtc.log.LogConfig} during application startup. It
 * notifies the optional {@link CrashListener} (used to report the crash), then
 * delegates to the platform handler, and finally flushes the log so the fatal
 * record reaches disk.
 */
public enum CrashHandler implements Thread.UncaughtExceptionHandler {
    Instance;

    private static final String LogTag = "Fatal";

    private static CrashListener mCrashListener;
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    public static CrashHandler getInstance() {
        return Instance;
    }

    /** Captures the current handler and installs this one as the default. */
    public void init() {
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public static void setCrashListener(CrashListener crashListener) {
        mCrashListener = crashListener;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        try {
            if (mCrashListener != null) {
                mCrashListener.onUncaughtException(thread, throwable);
            }
            this.mDefaultHandler.uncaughtException(thread, throwable);
        } finally {
            saveAndPrintLog(throwable);
        }
    }

    private boolean saveAndPrintLog(Throwable throwable) {
        if (throwable == null) {
            return false;
        }
        LogUtil.e(LogTag, throwable);
        LogUtil.close();
        return true;
    }
}
