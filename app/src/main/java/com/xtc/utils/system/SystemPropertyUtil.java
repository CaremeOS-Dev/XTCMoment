package com.xtc.utils.system;

import com.xtc.log.LogUtil;

/** Reads and writes {@code android.os.SystemProperties} through reflection. */
public class SystemPropertyUtil {

    private static final String PROPERTIES_CLASS = "android.os.SystemProperties";
    private static final String TAG = "SystemPropertyUtil";

    private SystemPropertyUtil() {
    }

    /** @deprecated the reflection based setter below should be used instead. */
    @Deprecated
    public static void setBooleanLegacy(String key, boolean value) {
        try {
            Class<?> properties = Class.forName(PROPERTIES_CLASS);
            properties.getMethod("set", String.class, String.class).invoke(properties, key, String.valueOf(value));
            LogUtil.i(key + "：" + value);
        } catch (Exception e) {
            LogUtil.e(e);
        }
    }

    /** @deprecated the reflection based setter below should be used instead. */
    @Deprecated
    public static void setLongLegacy(String key, long value) {
        try {
            Class<?> properties = Class.forName(PROPERTIES_CLASS);
            properties.getMethod("set", String.class, String.class).invoke(properties, key, String.valueOf(value));
            LogUtil.i(key + "：" + value);
        } catch (Exception e) {
            LogUtil.e(e);
        }
    }

    /** @deprecated the reflection based getter below should be used instead. */
    @Deprecated
    public static boolean getBooleanLegacy(String key, boolean defaultValue) {
        boolean result = defaultValue;
        try {
            Class<?> properties = Class.forName(PROPERTIES_CLASS);
            result = (Boolean) properties.getMethod("getBoolean", String.class, Boolean.TYPE)
                    .invoke(properties, key, Boolean.valueOf(defaultValue));
        } catch (Exception e) {
            LogUtil.e(e);
        }
        LogUtil.i(key + "：" + result);
        return result;
    }

    /** @deprecated the reflection based getter below should be used instead. */
    @Deprecated
    public static long getLongLegacy(String key, long defaultValue) {
        long result = defaultValue;
        try {
            Class<?> properties = Class.forName(PROPERTIES_CLASS);
            result = (Long) properties.getMethod("getLong", String.class, Long.TYPE)
                    .invoke(properties, key, Long.valueOf(defaultValue));
        } catch (Exception e) {
            LogUtil.e(e);
        }
        LogUtil.i(key + "：" + result);
        return result;
    }

    /** Sets a string system property. */
    public static void set(String key, String value) {
        try {
            Class.forName(PROPERTIES_CLASS).getMethod("set", String.class, String.class).invoke(null, key, value);
        } catch (Exception e) {
            LogUtil.e(TAG, "Exception while setting system property: " + e);
            e.printStackTrace();
        }
    }

    /** Sets a boolean system property. */
    public static void setBoolean(String key, boolean value) {
        set(key, String.valueOf(value));
    }

    /** Sets an int system property. */
    public static void setInt(String key, int value) {
        set(key, String.valueOf(value));
    }

    /** Sets a long system property. */
    public static void setLong(String key, long value) {
        set(key, String.valueOf(value));
    }

    /** Reads a string system property, falling back to {@code defaultValue}. */
    public static String get(String key, String defaultValue) {
        try {
            return (String) Class.forName(PROPERTIES_CLASS).getMethod("get", String.class, String.class)
                    .invoke(null, key, defaultValue);
        } catch (Exception e) {
            LogUtil.e(TAG, "Exception while getting system property: " + e);
            e.printStackTrace();
            return defaultValue;
        }
    }

    /** Reads a boolean system property, falling back to {@code defaultValue}. */
    public static boolean getBoolean(String key, boolean defaultValue) {
        try {
            Boolean value = (Boolean) Class.forName(PROPERTIES_CLASS).getMethod("getBoolean", String.class, Boolean.TYPE)
                    .invoke(null, key, Boolean.valueOf(defaultValue));
            return value == null ? defaultValue : value.booleanValue();
        } catch (Exception e) {
            LogUtil.e(TAG, "Exception while getting system property: " + e);
            e.printStackTrace();
            return defaultValue;
        }
    }

    /** Reads an int system property, falling back to {@code defaultValue}. */
    public static int getInt(String key, int defaultValue) {
        try {
            Integer value = (Integer) Class.forName(PROPERTIES_CLASS).getMethod("getInt", String.class, Integer.TYPE)
                    .invoke(null, key, Integer.valueOf(defaultValue));
            return value == null ? defaultValue : value.intValue();
        } catch (Exception e) {
            LogUtil.e(TAG, "Exception while getting system property: " + e);
            e.printStackTrace();
            return defaultValue;
        }
    }

    /** Reads a long system property, falling back to {@code defaultValue}. */
    public static long getLong(String key, long defaultValue) {
        try {
            Long value = (Long) Class.forName(PROPERTIES_CLASS).getMethod("getLong", String.class, Long.TYPE)
                    .invoke(null, key, Long.valueOf(defaultValue));
            return value == null ? defaultValue : value.longValue();
        } catch (Exception e) {
            LogUtil.e(TAG, "Exception while getting system property: " + e);
            e.printStackTrace();
            return defaultValue;
        }
    }
}