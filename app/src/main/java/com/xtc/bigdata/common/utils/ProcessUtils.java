package com.xtc.bigdata.common.utils;

import android.app.Application;
import android.content.Context;

import java.lang.reflect.Method;

/**
 * 进程工具。
 */
public class ProcessUtils {

    private static final String TAG = "ProcessUtils";

    /**
     * 通过反射调用 ActivityThread.currentProcessName() 获取当前进程名。
     */
    public static String getCurProcessName(Context context) {
        try {
            Method method = Class.forName("android.app.ActivityThread", false, Application.class.getClassLoader())
                    .getDeclaredMethod("currentProcessName", new Class[0]);
            method.setAccessible(true);
            Object result = method.invoke(null, new Object[0]);
            if (result instanceof String) {
                return (String) result;
            }
            return null;
        } catch (Throwable throwable) {
            return null;
        }
    }
}