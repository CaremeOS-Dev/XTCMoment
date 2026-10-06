package com.xtc.contactapi.contacthead.util;

import android.content.Context;

/**
 * 尺寸单位换算工具。
 */
public class ResolutionTranslateUtil {

    /** dp 转 px。 */
    public static int dpToPx(Context context, float dp) {
        return (int) ((dp * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    /** sp 转 px。 */
    public static int spToPx(Context context, float sp) {
        return (int) ((sp * context.getResources().getDisplayMetrics().scaledDensity) + 0.5f);
    }

    /** px 转 dp。 */
    public static int pxToDp(Context context, float px) {
        return (int) ((px / context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    /** px 转 sp。 */
    public static int pxToSp(Context context, float px) {
        return (int) ((px / context.getResources().getDisplayMetrics().scaledDensity) + 0.5f);
    }
}