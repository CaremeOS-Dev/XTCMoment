package com.xtc.utils.system;

import com.xtc.log.LogUtil;

/** Global debug flag for the system utility package. */
public class UtilsSystemConfig {

    static boolean debug = false;
    private static final String TAG = "SystemUtilsConfig";

    public static void setDebug(boolean value) {
        LogUtil.i(TAG, "setDebug debug=" + value);
        debug = value;
    }
}