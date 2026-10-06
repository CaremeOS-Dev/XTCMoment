package com.xtc.utils.system;

import android.content.Context;

/** Reads string resources and replaces the brand placeholder. */
public class StringResourceUtils {

    private static final String BRAND_PLACEHOLDER = "\\$\\{XTC\\}";
    private static final String BRAND_XTC = "小天才";
    private static final String BRAND_IMOO = "imoo";

    private StringResourceUtils() {
    }

    /** Reads a string resource with the brand placeholder replaced. */
    public static String getString(Context context, int resId) {
        return replaceBrand(context.getString(resId));
    }

    /** Reads a string-array resource with the brand placeholder replaced. */
    public static String[] getStringArray(Context context, int resId) {
        String[] strings = context.getResources().getStringArray(resId);
        for (int i = 0; i < strings.length; i++) {
            strings[i] = replaceBrand(strings[i]);
        }
        return strings;
    }

    /** Replaces {@code ${XTC}} with the brand matching the current region. */
    public static String replaceBrand(String text) {
        if (WatchModelUtil.isRegion("CN") || WatchModelUtil.isRegion("HK") || WatchModelUtil.isRegion("TW")) {
            return text.replaceAll(BRAND_PLACEHOLDER, BRAND_XTC);
        }
        return text.replaceAll(BRAND_PLACEHOLDER, BRAND_IMOO);
    }
}