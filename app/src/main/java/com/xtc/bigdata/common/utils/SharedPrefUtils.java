package com.xtc.bigdata.common.utils;

import com.xtc.utils.storage.SharedManager;

/** Preferences facade used by the big-data library. */
public class SharedPrefUtils {

    public static final String PREF_NAME = "BigData_Collector";

    private static SharedPrefUtils instance;

    protected SharedPrefUtils() {
    }

    public static SharedPrefUtils getInstance() {
        if (instance == null) {
            synchronized (SharedPrefUtils.class) {
                if (instance == null) {
                    instance = new SharedPrefUtils();
                }
            }
        }
        return instance;
    }

    public void saveKeyStringValue(String key, String value) {
        SharedManager.getInstance(ContextUtils.getContext()).putString(PREF_NAME, key, value);
    }

    public void saveKeyIntValue(String key, int value) {
        SharedManager.getInstance(ContextUtils.getContext()).putInt(PREF_NAME, key, value);
    }

    public void saveKeyLongValue(String key, long value) {
        SharedManager.getInstance(ContextUtils.getContext()).putLong(PREF_NAME, key, value);
    }

    public void saveKeyFloatValue(String key, float value) {
        SharedManager.getInstance(ContextUtils.getContext()).putFloat(PREF_NAME, key, value);
    }

    public void saveKeyBooleanValue(String key, boolean value) {
        SharedManager.getInstance(ContextUtils.getContext()).putBoolean(PREF_NAME, key, value);
    }

    public String getKeyStringValue(String key, String defaultValue) {
        return SharedManager.getInstance(ContextUtils.getContext()).getString(PREF_NAME, key, defaultValue);
    }

    public long getKeyLongValue(String key, long defaultValue) {
        return SharedManager.getInstance(ContextUtils.getContext()).getLong(PREF_NAME, key, defaultValue);
    }

    public int getKeyIntValue(String key, int defaultValue) {
        return SharedManager.getInstance(ContextUtils.getContext()).getInt(PREF_NAME, key, defaultValue);
    }

    public boolean getKeyBooleanValue(String key, boolean defaultValue) {
        return SharedManager.getInstance(ContextUtils.getContext()).getBoolean(PREF_NAME, key, defaultValue);
    }

    public float getKeyFloatValue(String key, float defaultValue) {
        return SharedManager.getInstance(ContextUtils.getContext()).getFloat(PREF_NAME, key, defaultValue);
    }

    public void clear() {
        SharedManager.getInstance(ContextUtils.getContext()).clear(PREF_NAME);
    }

    public void remove(String key) {
        SharedManager.getInstance(ContextUtils.getContext()).remove(PREF_NAME, key);
    }
}