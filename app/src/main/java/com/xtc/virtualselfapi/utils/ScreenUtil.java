package com.xtc.virtualselfapi.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.xtc.log.LogUtil;

/**
 * 屏幕尺寸工具。
 */
public class ScreenUtil {

    private static final String TAG = "Virtual_Self_Api_ScreenUtil";

    private static int screenHeight;
    private static int screenWidth;

    public static int getScreenWidth(Context context) {
        int width = screenWidth;
        if (width > 0) {
            return width;
        }
        if (context == null) {
            LogUtil.e(TAG, "getScreenWidth context is null");
            return 320;
        }
        screenWidth = displayMetrics(context).widthPixels;
        LogUtil.d(TAG, "get screen width = " + screenWidth);
        return screenWidth;
    }

    public static int getScreenHeight(Context context) {
        int height = screenHeight;
        if (height > 0) {
            return height;
        }
        if (context == null) {
            LogUtil.e(TAG, "getScreenHeight context is null");
            return 360;
        }
        screenHeight = displayMetrics(context).heightPixels;
        LogUtil.d(TAG, "get screen height = " + screenHeight);
        return screenHeight;
    }

    public static DisplayMetrics displayMetrics(Context context) {
        WindowManager windowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }
}