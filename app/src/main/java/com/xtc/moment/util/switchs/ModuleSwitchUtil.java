package com.xtc.moment.util.switchs;

import android.content.Context;

import com.xtc.log.LogUtil;
import com.xtc.moment.util.ThreadCheck;
import com.xtc.system.account.WatchAccountBase;
import com.xtc.system.wearswitch.function.datacache.DataCacheContainer;

/**
 * 模块开关查询工具，可开启内存缓存并监听主线程查询。
 */
public class ModuleSwitchUtil {

    private static final String TAG = "ModuleSwitchUtil";
    private static final int CACHE_CAPACITY = 20;

    private static final DataCacheContainer<Integer, Integer> moduleSwitchCache =
            new DataCacheContainer<>(CACHE_CAPACITY);

    private static boolean isOpenCache = false;
    private static InitModuleSwitchFinishListener moduleSwitchListener;

    public static boolean isMonitor = false;

    public interface InitModuleSwitchFinishListener {
        void onChanged();
    }

    public interface ModuleSwitchConstant {
        int DISPLAY = 0;
        int HIDDEN = 1;
        int DISPLAY_HAS_TIP = 2;
    }

    public static void setIsOpenCache(boolean openCache) {
        isOpenCache = openCache;
    }

    public static boolean queryModuleSwitchByBoolean(Context context, int module, boolean defaultValue) {
        int switchValue = queryModuleSwitchByInt(context, module, defaultValue);
        return switchValue == 0 || (switchValue != 1 && switchValue == 2);
    }

    public static int queryModuleSwitchByInt(Context context, int module, boolean defaultValue) {
        int cachedValue = queryModuleSwitchByCache(module);
        if (cachedValue != -1) {
            return cachedValue;
        }
        int switchValue = WatchAccountBase.queryModuleSwitchByInt(context, module, defaultValue);
        if (isMonitor && ThreadCheck.isMainThread()) {
            LogUtil.e(TAG, "thread monitor queryModuleSwitchByInt error: ", new NullPointerException());
        }
        saveDataCache(module, Integer.valueOf(switchValue));
        return switchValue;
    }

    private static int queryModuleSwitchByCache(int module) {
        if (!isOpenCache) {
            return -1;
        }
        Integer cachedValue = moduleSwitchCache.get(Integer.valueOf(module));
        if (cachedValue == null) {
            return -1;
        }
        LogUtil.i(TAG, "queryModuleSwitchByCache module=" + module + ":" + cachedValue);
        return cachedValue.intValue();
    }

    private static void saveDataCache(int module, Integer switchValue) {
        if (isOpenCache) {
            LogUtil.i(TAG, "saveModuleSwitchByCache module: " + module + " switchValue:" + switchValue);
            moduleSwitchCache.put(Integer.valueOf(module), switchValue);
        }
    }

    public static void clearCache() {
        if (isOpenCache) {
            moduleSwitchCache.clear();
        }
    }

    public static void setInitModuleSwitchFinishListener(InitModuleSwitchFinishListener listener) {
        moduleSwitchListener = listener;
    }

    public static void notifyModuleInitFinish() {
        InitModuleSwitchFinishListener listener = moduleSwitchListener;
        if (listener == null) {
            LogUtil.d(TAG, "moduleSwitchListener == null");
        } else {
            listener.onChanged();
        }
    }
}