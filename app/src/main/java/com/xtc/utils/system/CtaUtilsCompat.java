package com.xtc.utils.system;

import android.os.Build;

/** CTA property names that changed with Android 8.0. */
public class CtaUtilsCompat {

    private CtaUtilsCompat() {
    }

    /** Property reporting whether the system was upgraded from an older version. */
    public static String getFromDownVersionProperty() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? "persist.sys.system.from.downversion" : "persist.sys.from.downversion";
    }

    /** Property controlling the app-store permission. */
    public static String getAppUpdatePermissionProperty() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? "persist.sys.appupdate.permission" : "persist.sys.appstore.permission";
    }

    /** Property disabling the personal centre. */
    public static String getForbidPersonalProperty() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? "persist.sys.forbid.personal.center" : "persist.sys.forbid.personal";
    }
}