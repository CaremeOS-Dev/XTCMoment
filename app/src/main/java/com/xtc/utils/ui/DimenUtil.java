package com.xtc.utils.ui;

import android.content.Context;

/** Thin delegate over {@link DensityUtil} kept for historical call sites. */
public class DimenUtil {

    public static float getDensity(Context context) {
        return DensityUtil.getDensity(context);
    }

    public static int dp2px(Context context, float dpValue) {
        return DensityUtil.dp2px(context, dpValue);
    }

    public static float dp2pxFloat(Context context, float dpValue) {
        return DensityUtil.dp2pxFloat(context, dpValue);
    }

    public static int px2dp(Context context, float pxValue) {
        return DensityUtil.px2dp(context, pxValue);
    }

    public static int sp2px(Context context, float spValue) {
        return DensityUtil.sp2px(context, spValue);
    }

    public static int px2sp(Context context, float pxValue) {
        return DensityUtil.px2sp(context, pxValue);
    }
}