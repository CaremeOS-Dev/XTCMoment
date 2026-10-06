package com.xtc.system.wearswitch.function;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.xtc.log.LogUtil;
import com.xtc.system.wearswitch.function.datacache.DataCacheContainer;
import com.xtc.system.wearswitch.function.datacache.TimeMonitor;

/**
 * 功能开关查询工具，支持内存缓存与内容提供者查询。
 */
public class FunSwitchUtil {

    private static final int CACHE_SIZE = 20;
    private static final String TAG = "FunSwitchUtil";
    private static final Uri FUN_SWITCH_URI = Uri.parse("content://com.xtc.fundata/FunSwitch/");
    private static final String COLUMN_SWITCH_STATUS = "switchStatus";
    private static final String SELECTION_APP_PACKAGE = "appPackage=?";

    private static final DataCacheContainer<String, Integer> CACHE = new DataCacheContainer<>(CACHE_SIZE);
    private static boolean cacheEnabled = false;

    /** 开关状态。 */
    public interface SwitchStatus {
        int OPEN = 0;
        int CLOSE = 1;
    }

    private FunSwitchUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /** 是否启用缓存。 */
    public static void setCacheEnabled(boolean enabled) {
        cacheEnabled = enabled;
    }

    /** 查询功能开关（布尔值）。 */
    public static boolean queryFunSwitchByBoolean(Context context, String appPackage, boolean defaultValue) {
        TimeMonitor.start("queryFunSwitchByBoolean appPackage:" + appPackage);
        int switchInt = queryFunSwitchByInt(context, appPackage, defaultValue);
        TimeMonitor.end("queryFunSwitchByBoolean appPackage:" + appPackage, "switchInt:" + switchInt);
        if (switchInt == SwitchStatus.OPEN) {
            return true;
        }
        if (switchInt == SwitchStatus.CLOSE) {
            return false;
        }
        LogUtil.e(TAG, "queryFunSwitchByBoolean: switchInt is error. switchInt: " + switchInt);
        return defaultValue;
    }

    /** 查询功能开关（布尔值），可选择先清理缓存。 */
    public static boolean queryFunSwitchByBoolean(Context context, String appPackage, boolean defaultValue,
                                                  boolean clearCache) {
        if (clearCache) {
            CACHE.remove(appPackage);
            LogUtil.i(TAG, "queryFunSwitchByBoolean: appPackage:" + appPackage + " switchLose");
        }
        return queryFunSwitchByBoolean(context, appPackage, defaultValue);
    }

    /** 查询功能开关（整型）。 */
    public static int queryFunSwitchByInt(Context context, String appPackage, boolean defaultValue) {
        int cached = getFromCache(appPackage);
        if (cached != -1) {
            return cached;
        }
        Cursor cursor = context.getContentResolver().query(FUN_SWITCH_URI, null, SELECTION_APP_PACKAGE,
                new String[]{appPackage}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int switchStatus = cursor.getInt(cursor.getColumnIndex(COLUMN_SWITCH_STATUS));
            cursor.close();
            saveToCache(appPackage, switchStatus);
            LogUtil.i(TAG, "queryFunSwitchByInt: appPackage: " + appPackage + ", switch: " + switchStatus);
            TimeMonitor.end("queryFunSwitchByInt appPackage:" + appPackage);
            return switchStatus;
        }
        if (cursor != null) {
            cursor.close();
        }
        int defaultSwitch = !defaultValue ? SwitchStatus.CLOSE : SwitchStatus.OPEN;
        saveToCache(appPackage, defaultSwitch);
        LogUtil.e(TAG, "queryFunSwitchByInt: cursor is null.");
        return defaultSwitch;
    }

    private static int getFromCache(String appPackage) {
        if (!cacheEnabled) {
            return -1;
        }
        Integer cached = CACHE.get(appPackage);
        if (cached == null) {
            return -1;
        }
        LogUtil.i(TAG, "queryFunSwitchByCache appPackage: " + appPackage + " switchInt:" + cached);
        return cached;
    }

    private static void saveToCache(String appPackage, Integer switchValue) {
        if (cacheEnabled) {
            LogUtil.i(TAG, "saveFunSwitchByCache appPackage: " + appPackage + " switchValue:" + switchValue);
            CACHE.put(appPackage, switchValue);
        }
    }

    /** 清空全部缓存。 */
    public static void clearCache() {
        LogUtil.i(TAG, "funSwitch clearAllCache ");
        CACHE.clear();
    }
}