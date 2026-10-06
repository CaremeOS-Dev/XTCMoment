package com.xtc.log.crash;

/** Notified just before the process dies from an uncaught exception. */
public interface CrashListener {
    void onUncaughtException(Thread thread, Throwable throwable);
}
