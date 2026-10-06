package com.xtc.ui.widget.ptrrefresh.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/** 屏幕尺寸缓存与 dp 换算（以 320dp 设计稿为基准）。 */
public class LocalDisplay {

    private static final int DESIGN_WIDTH_DP = 320;

    public static float SCREEN_DENSITY;
    public static int SCREEN_HEIGHT_DP;
    public static int SCREEN_HEIGHT_PIXELS;
    public static int SCREEN_WIDTH_DP;
    public static int SCREEN_WIDTH_PIXELS;

    public static void init(Context context) {
        if (context == null) {
            return;
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getMetrics(displayMetrics);
        SCREEN_WIDTH_PIXELS = displayMetrics.widthPixels;
        SCREEN_HEIGHT_PIXELS = displayMetrics.heightPixels;
        SCREEN_DENSITY = displayMetrics.density;
        SCREEN_WIDTH_DP = (int) (SCREEN_WIDTH_PIXELS / displayMetrics.density);
        SCREEN_HEIGHT_DP = (int) (SCREEN_HEIGHT_PIXELS / displayMetrics.density);
    }

    public static int dp2px(float dp) {
        return (int) ((dp * SCREEN_DENSITY) + 0.5f);
    }

    /** 按屏幕宽度对设计稿尺寸等比缩放后换算为 px。 */
    public static int designedDP2px(float designedDp) {
        if (SCREEN_WIDTH_DP != DESIGN_WIDTH_DP) {
            designedDp = (designedDp * SCREEN_WIDTH_DP) / DESIGN_WIDTH_DP;
        }
        return dp2px(designedDp);
    }

    public static void setPadding(View view, float left, float top, float right, float bottom) {
        view.setPadding(designedDP2px(left), dp2px(top), designedDP2px(right), dp2px(bottom));
    }
}