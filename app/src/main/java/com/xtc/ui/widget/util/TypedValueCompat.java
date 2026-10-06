package com.xtc.ui.widget.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

/** 尺寸单位换算工具（使用前需先 init）。 */
public class TypedValueCompat {

    private static final String TAG = "TypedValueCompat";
    private static final int UNIT_DIP = TypedValue.COMPLEX_UNIT_DIP;
    private static final int UNIT_SP = TypedValue.COMPLEX_UNIT_SP;

    private static DisplayMetrics metrics;

    public static void init(Context context) {
        if (context != null && metrics == null) {
            metrics = context.getApplicationContext().getResources().getDisplayMetrics();
        }
    }

    public static float applyDimensionDip(float dip) {
        requireInit();
        return TypedValue.applyDimension(UNIT_DIP, dip, metrics);
    }

    public static float applyDimensionSp(float sp) {
        requireInit();
        return TypedValue.applyDimension(UNIT_SP, sp, metrics);
    }

    public static int obtainScreenWidthPixels() {
        requireInit();
        return metrics.widthPixels;
    }

    public static int obtainScreenHeightPixels() {
        requireInit();
        return metrics.heightPixels;
    }

    private static void requireInit() {
        if (metrics == null) {
            Log.d(TAG, "call TypedValueCompat.init() first !!!");
        }
    }
}