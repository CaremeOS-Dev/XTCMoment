package com.xtc.utils.system;

import android.text.TextUtils;

/** Screen-shape helpers. */
@Deprecated
public class XtcScreenUtils {

    private static final String PROPERTY_ROUNDED_CORNER = "persist.sys.roundedcorner";
    private static String roundedCornerValue;

    private XtcScreenUtils() {
    }

    /** @return true when the screen has rounded corners. */
    public static boolean hasRoundedCorner() {
        if (TextUtils.isEmpty(roundedCornerValue)) {
            roundedCornerValue = SystemPropertyUtil.get(PROPERTY_ROUNDED_CORNER, "false");
        }
        return "true".equals(roundedCornerValue);
    }
}