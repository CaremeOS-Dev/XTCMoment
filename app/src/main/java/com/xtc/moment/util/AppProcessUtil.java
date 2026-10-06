package com.xtc.moment.util;

import android.content.Context;

import com.xtc.moment.LogTag;
import com.xtc.utils.system.ProcessUtils;

/** Checks whether the current process is the app main process. */
public class AppProcessUtil {

    private static final String TAG = LogTag.tag("AppProcessUtil");

    private AppProcessUtil() {
    }

    public static boolean isAppProcess(Context context) {
        String processName = ProcessUtils.getCurrentProcessName();
        return processName != null && processName.equalsIgnoreCase(context.getPackageName());
    }
}