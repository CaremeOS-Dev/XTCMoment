package com.xtc.utils.ui;

import android.content.Context;

/** Conversions between dp/sp and raw pixels. */
public class DensityUtil {

    /** Returns the display density (dp to px factor). */
    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /** Converts dp to px, rounding to the nearest pixel. */
    public static int dp2px(Context context, float dpValue) {
        return (int) ((getDensity(context) * dpValue) + 0.5f);
    }

    /** Converts dp to px without rounding. */
    public static float dp2pxFloat(Context context, float dpValue) {
        return getDensity(context) * dpValue;
    }

    /** Converts px to dp, truncating towards zero. */
    public static int px2dp(Context context, float pxValue) {
        return (int) (pxValue / getDensity(context));
    }

    /** Converts sp to px, rounding to the nearest pixel. */
    public static int sp2px(Context context, float spValue) {
        return (int) ((spValue * context.getResources().getDisplayMetrics().scaledDensity) + 0.5f);
    }

    /** Converts px to sp, rounding to the nearest pixel. */
    public static int px2sp(Context context, float pxValue) {
        return (int) ((pxValue / context.getResources().getDisplayMetrics().scaledDensity) + 0.5f);
    }
}