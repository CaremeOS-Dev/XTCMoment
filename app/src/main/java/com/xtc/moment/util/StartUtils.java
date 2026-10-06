package com.xtc.moment.util;

import android.content.Context;
import android.content.Intent;

import com.xtc.log.LogUtil;

/**
 * 通过 launcher 插件机制启动其它应用页面。
 */
public class StartUtils {

    private static final String TAG = "Common_bean StartUtil";

    public static final String EXTRA_PACKAGE_NAME = "packageName";
    public static final String EXTRA_STRATEGY_TYPE = "strategyType";
    public static final String EXTRA_TARGET_ACTIVITY = "className";
    public static final String EXTRA_START_INTENT = "startIntent";
    public static final String EXTRA_UNINSTALL_TOAST = "uninstall_toast";

    public static final String PACKAGE_LAUNCHER = "com.xtc.i3launcher";
    public static final String START_APP_ACTION = "com.xtc.launcher.startapp";
    public static final String STRATEGY_TYPE_PLUGIN = "plugin";

    public static void startActivity(Context context, String packageName, String className, Intent intent) {
        Intent broadcastIntent = new Intent(START_APP_ACTION)
                .putExtra(EXTRA_PACKAGE_NAME, packageName)
                .putExtra(EXTRA_STRATEGY_TYPE, STRATEGY_TYPE_PLUGIN)
                .putExtra(EXTRA_TARGET_ACTIVITY, className)
                .setPackage(PACKAGE_LAUNCHER)
                .putExtra(EXTRA_START_INTENT, intent);
        intent.setClassName(packageName, className);
        context.sendBroadcast(broadcastIntent);
        LogUtil.d(TAG, "startActivity : packageName = [" + packageName + "], className = [" + className + "]");
    }
}