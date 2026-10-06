package com.xtc.moment.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.xtc.database.ormlite.CollectionUtil;
import com.xtc.log.LogUtil;
import com.xtc.virtualselfapi.constants.Constants;

import java.util.List;

/**
 * 系统级工具：页面栈判断、连点检测、安装检测与机器等级判断。
 */
public class SystemUtil {

    private static final String TAG = "SystemUtil";

    private static final int CLICK_SHORTER_DURATION = 200;
    private static final int CLICK_SHORT_DURATION = 1000;
    private static final int CLICK_LONG_DURATION = 2000;
    private static final int ENTER_FUNC_TIMEOUT = 5000;

    private static final int SDK_INT = Build.VERSION.SDK_INT;

    private static long lastFastClickTime;
    private static long lastClickTime;
    private static long lastLongClickTime;
    private static long lastEnterFuncTime;
    private static boolean hasEnterFunc = false;
    private static int sVersionCode;

    public static boolean isActivityOnTop(Context context, Class clazz) {
        String className = clazz.getName();
        List<ActivityManager.RunningTaskInfo> runningTasks = ((ActivityManager) context.getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1);
        if (CollectionUtil.isEmpty(runningTasks)) {
            return false;
        }
        ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
        if (runningTaskInfo == null) {
            return false;
        }
        ComponentName topActivity = runningTaskInfo.topActivity;
        return topActivity != null
                && context.getPackageName().equals(topActivity.getPackageName())
                && topActivity.getClassName().equals(className);
    }

    public static boolean isFasterDoubleClick() {
        long currentTime = System.currentTimeMillis();
        long interval = currentTime - lastFastClickTime;
        if (0 < interval && interval < CLICK_SHORTER_DURATION) {
            return true;
        }
        lastFastClickTime = currentTime;
        return false;
    }

    public static boolean isFastDoubleClick() {
        long currentTime = System.currentTimeMillis();
        long interval = currentTime - lastClickTime;
        if (0 < interval && interval < CLICK_SHORT_DURATION) {
            return true;
        }
        lastClickTime = currentTime;
        return false;
    }

    public static boolean isFastLongDoubleClick() {
        long currentTime = System.currentTimeMillis();
        long interval = currentTime - lastLongClickTime;
        if (0 < interval && interval < Constants.DEFAULT_INIT_DELAY_TIME) {
            return true;
        }
        lastLongClickTime = currentTime;
        return false;
    }

    public static boolean hasEnterFunTimeOut() {
        long currentTime = System.currentTimeMillis();
        long interval = currentTime - lastEnterFuncTime;
        if (0 < interval && interval < Constants.DEFAULT_INIT_DELAY_TIME) {
            return true;
        }
        if (hasEnterFunc && interval < com.xtc.moment.module.Constants.DIFFER_TIME) {
            return true;
        }
        resetEnterFuncFlag();
        lastEnterFuncTime = currentTime;
        return false;
    }

    public static void setEnterFuncFlag() {
        hasEnterFunc = true;
    }

    public static void resetEnterFuncFlag() {
        hasEnterFunc = false;
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        PackageInfo packageInfo;
        if (packageName == null || packageName.isEmpty()) {
            return false;
        }
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.e("go to download APP error: ", e);
            packageInfo = null;
        }
        if (packageInfo != null) {
            return true;
        }
        LogUtil.d(TAG, "app uninstall: " + packageName);
        return false;
    }

    public static String getVersionCode(Context context) {
        if (sVersionCode == 0) {
            try {
                sVersionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            } catch (Exception e) {
                LogUtil.e(TAG, "getVersionCode error : ", e);
            }
        }
        return String.valueOf(sVersionCode);
    }

    public static boolean isHighMachine() {
        return SDK_INT > 25;
    }

    public static boolean isLowMachine() {
        return !isHighMachine();
    }
}