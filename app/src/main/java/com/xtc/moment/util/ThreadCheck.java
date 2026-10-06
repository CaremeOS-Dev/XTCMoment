package com.xtc.moment.util;

import android.os.Looper;

/** Small helpers for asking whether the current thread is the UI thread. */
public class ThreadCheck {

    private static final String TAG = "ThreadCheck";

    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    public static boolean isMainThread(String tag) {
        return Looper.getMainLooper() == Looper.myLooper();
    }
}
