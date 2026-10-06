package com.xtc.utils.system;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xtc.log.LogUtil;

/** Safe wrappers around activity starts and foreground checks. */
public class ActivityUtils {

    private static final String TAG = "ActivityUtils";

    private ActivityUtils() {
    }

    /** Starts an activity for result, swallowing any exception. */
    public static void startActivityForResult(Activity activity, Intent intent, int requestCode) {
        try {
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            LogUtil.e(TAG, e);
        }
    }

    /** Starts an activity, swallowing any exception. */
    public static void startActivity(Context context, Intent intent) {
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            LogUtil.e(TAG, e);
        }
    }

    /** @return true when the intent resolves to an installed activity. */
    public static boolean isIntentAvailable(Context context, Intent intent) {
        return intent != null && intent.resolveActivity(context.getPackageManager()) != null;
    }

    /** @return true when the given activity class is the top activity. */
    public static boolean isTopActivity(Context context, Class<?> clazz) {
        if (context == null || clazz == null) {
            return false;
        }
        return isTopActivity(context, clazz.getName());
    }

    /** @return true when the given class name is the top activity of this app. */
    public static boolean isTopActivity(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }
        return isTopActivity(context, context.getPackageName(), className);
    }

    /** @return true when the given class name is the top activity of {@code packageName}. */
    public static boolean isTopActivity(Context context, String packageName, String className) {
        if (context == null || TextUtils.isEmpty(className) || TextUtils.isEmpty(packageName)) {
            return false;
        }
        ComponentName topActivity = ((ActivityManager) context.getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1).get(0).topActivity;
        return packageName.equals(topActivity.getPackageName()) && topActivity.getClassName().equals(className);
    }

    /** Package name of the current top activity, or an empty string. */
    public static String getTopPackageName(Context context) {
        return context == null ? "" : ((ActivityManager) context.getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1).get(0).topActivity.getPackageName();
    }
}