package com.xtc.httplib.util;

import com.xtc.httplib.LogTag;
import com.xtc.log.LogUtil;

import java.lang.reflect.InvocationTargetException;

/** Reflection access to {@code android.os.SystemProperties}. */
public class SystemProperties {

    private static final String TAG = LogTag.tag("SystemProperties");
    private static final String CLASS_NAME = "android.os.SystemProperties";

    private SystemProperties() {
    }

    private static boolean getBooleanSystemProperties(String key, boolean defaultValue) {
        try {
            Class<?> clazz = Class.forName(CLASS_NAME);
            return (Boolean) clazz.getMethod("getBoolean", String.class, Boolean.TYPE)
                    .invoke(clazz, key, defaultValue);
        } catch (ClassNotFoundException e) {
            LogUtil.e(TAG, e);
            return false;
        } catch (IllegalAccessException e) {
            LogUtil.e(TAG, e);
            return false;
        } catch (NoSuchMethodException e) {
            LogUtil.e(TAG, e);
            return false;
        } catch (InvocationTargetException e) {
            LogUtil.e(TAG, e);
            return false;
        }
    }

    private static int getIntegerSystemProperties(String key, int defaultValue) {
        try {
            Class<?> clazz = Class.forName(CLASS_NAME);
            return (Integer) clazz.getMethod("getInt", String.class, Integer.TYPE)
                    .invoke(clazz, key, defaultValue);
        } catch (ClassNotFoundException e) {
            LogUtil.e(TAG, e);
            return defaultValue;
        } catch (IllegalAccessException e) {
            LogUtil.e(TAG, e);
            return defaultValue;
        } catch (NoSuchMethodException e) {
            LogUtil.e(TAG, e);
            return defaultValue;
        } catch (InvocationTargetException e) {
            LogUtil.e(TAG, e);
            return defaultValue;
        }
    }

    private static String getStringSystemProperties(String key) {
        try {
            Class<?> clazz = Class.forName(CLASS_NAME);
            return (String) clazz.getMethod("get", String.class).invoke(clazz, key);
        } catch (ClassNotFoundException e) {
            LogUtil.e(TAG, e);
            return "";
        } catch (IllegalAccessException e) {
            LogUtil.e(TAG, e);
            return "";
        } catch (NoSuchMethodException e) {
            LogUtil.e(TAG, e);
            return "";
        } catch (InvocationTargetException e) {
            LogUtil.e(TAG, e);
            return "";
        }
    }

    private static void setSystemProperties(String key, boolean value) {
        try {
            Class<?> clazz = Class.forName(CLASS_NAME);
            clazz.getMethod("set", String.class, String.class).invoke(clazz, key, String.valueOf(value));
        } catch (ClassNotFoundException e) {
            LogUtil.e(TAG, e);
        } catch (IllegalAccessException e) {
            LogUtil.e(TAG, e);
        } catch (NoSuchMethodException e) {
            LogUtil.e(TAG, e);
        } catch (InvocationTargetException e) {
            LogUtil.e(TAG, e);
        }
    }

    private static void setSystemProperties(String key, String value) {
        LogUtil.d(TAG, "setSystemProperties:{" + key + "," + value + "}");
        try {
            Class<?> clazz = Class.forName(CLASS_NAME);
            clazz.getMethod("set", String.class, String.class).invoke(clazz, key, value);
        } catch (ClassNotFoundException e) {
            LogUtil.e(TAG, e);
        } catch (IllegalAccessException e) {
            LogUtil.e(TAG, e);
        } catch (NoSuchMethodException e) {
            LogUtil.e(TAG, e);
        } catch (InvocationTargetException e) {
            LogUtil.e(TAG, e);
        }
    }

    private static void setSystemProperties(String key, int value) {
        try {
            Class<?> clazz = Class.forName(CLASS_NAME);
            clazz.getMethod("set", String.class, String.class).invoke(clazz, key, String.valueOf(value));
        } catch (ClassNotFoundException e) {
            LogUtil.e(TAG, e);
        } catch (IllegalAccessException e) {
            LogUtil.e(TAG, e);
        } catch (NoSuchMethodException e) {
            LogUtil.e(TAG, e);
        } catch (InvocationTargetException e) {
            LogUtil.e(TAG, e);
        }
    }
}