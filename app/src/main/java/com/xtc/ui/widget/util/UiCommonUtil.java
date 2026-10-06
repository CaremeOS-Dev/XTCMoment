package com.xtc.ui.widget.util;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;

/** Small helpers shared by the widget library (colour, dp/px, visibility). */
public class UiCommonUtil {

    private static final String TAG = "UiCommonUtil";

    public static int getColor(Context context, int colorRes) {
        if (context == null || colorRes == 0) {
            return -1;
        }
        return context.getResources().getColor(colorRes);
    }

    public static int[] getColorArray(Context context, int[] colorResArray) {
        int[] result = colorResArray.clone();
        if (context != null) {
            for (int i = 0; i < result.length; i++) {
                result[i] = getColor(context, result[i]);
            }
        }
        return result;
    }

    public static int dp2Px(Context context, float dp) {
        return (int) ((dp * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static float[] dp2Px(Context context, float[] dpArray) {
        float[] result = dpArray.clone();
        if (context != null) {
            for (int i = 0; i < result.length; i++) {
                result[i] = dp2Px(context, result[i]);
            }
        }
        return result;
    }

    public static int px2Dp(Context context, float px) {
        return (int) ((px / context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static float[] px2Dp(Context context, float[] pxArray) {
        float[] result = pxArray.clone();
        if (context != null) {
            for (int i = 0; i < result.length; i++) {
                result[i] = px2Dp(context, result[i]);
            }
        }
        return result;
    }

    public static int getFontHeight(String text) {
        if (TextUtils.isEmpty(text)) {
            return -1;
        }
        Rect rect = new Rect();
        new Paint().getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }

    public static int getFontWidth(String text) {
        if (TextUtils.isEmpty(text)) {
            return -1;
        }
        Rect rect = new Rect();
        new Paint().getTextBounds(text, 0, text.length(), rect);
        return rect.width();
    }

    public static void showView(View view) {
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void hideView(View view) {
        if (view.getVisibility() != View.GONE) {
            view.setVisibility(View.GONE);
        }
    }
}
