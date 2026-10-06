package com.xtc.bigdata.common.utils;

import android.app.Application;

import com.xtc.log.LogUtil;

/** Holds the application context used by the big-data library. */
public class ContextUtils {

    private static final String TAG = ContextUtils.class.getName();
    private static Application context;

    public static Application getContext() {
        return context;
    }

    public static void setContext(Application application) {
        if (application == null) {
            LogUtil.w(TAG, "setContext: application is null");
        } else {
            context = application;
        }
    }

    public static boolean isEmpty() {
        return getContext() == null;
    }

    public static void clean() {
        context = null;
    }

    private ContextUtils() {
    }
}