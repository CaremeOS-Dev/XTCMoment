package com.xtc.moment.util;

import android.os.Looper;

/** Helpers to check which thread the caller runs on. */
public class ThreadCheck {

    private static final String TAG = "ThreadCheck";

    private ThreadCheck() {
    }

    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    public static boolean isMainThread(String tag) {
        return Looper.getMainLooper() == Looper.myLooper();
    }
}