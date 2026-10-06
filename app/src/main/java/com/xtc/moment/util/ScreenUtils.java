package com.xtc.moment.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.xtc.log.LogUtil;

/** Cached screen size helpers. */
public class ScreenUtils {

    private static final String TAG = "ScreenUtils";

    private static int screenHeight;
    private static int screenWidth;

    private ScreenUtils() {
    }

    public static int screenWidth(Context context) {
        if (screenWidth > 0) {
            return screenWidth;
        }
        screenWidth = displayMetrics(context).widthPixels;
        LogUtil.d(TAG, "getScreenWidth:screenWidth=" + screenWidth);
        return screenWidth;
    }

    public static int screenHeight(Context context) {
        if (screenHeight > 0) {
            return screenHeight;
        }
        screenHeight = displayMetrics(context).heightPixels;
        LogUtil.d(TAG, "getScreenHeight:screenHeight=" + screenHeight);
        return screenHeight;
    }

    private static DisplayMetrics displayMetrics(Context context) {
        WindowManager windowManager =
                (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }
}