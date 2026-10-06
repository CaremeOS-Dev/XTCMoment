package com.xtc.moment.manager;

import com.xtc.log.Log;

import java.util.HashMap;

/**
 * 启动耗时统计。
 */
public class StartupManage {

    public static final String TAG = "Moment_StartupManage";

    public static boolean isEndStartUp;

    private static long applicationTime;
    private static long firstScreenTime;
    private static boolean firstStartup = false;

    private static final HashMap<String, Long> methodStartUpTime = new HashMap<>();

    public static boolean isFirstStartup() {
        return firstStartup;
    }

    public static void setFirstStartup(boolean value) {
        firstStartup = value;
    }

    public static void startApplicationTime() {
        applicationTime = System.currentTimeMillis();
    }

    public static void endApplicationTime() {
        if (isEndStartUp) {
            return;
        }
        applicationTime = System.currentTimeMillis() - applicationTime;
        Log.i(TAG, "应用基础初始化时间 : " + applicationTime + " ms");
    }

    public static void startFirstScreenTime() {
        if (firstScreenTime != 0) {
            return;
        }
        firstScreenTime = System.currentTimeMillis();
    }

    public static void startMethodStartupTime(String methodName) {
        if (methodStartUpTime.containsKey(methodName)) {
            return;
        }
        methodStartUpTime.put(methodName, Long.valueOf(System.currentTimeMillis()));
    }

    public static void endMethodStartupTime(String methodName) {
        Long startTime = methodStartUpTime.get(methodName);
        if (startTime == null) {
            return;
        }
        Log.i(TAG, methodName + " 时间 : " + (System.currentTimeMillis() - startTime.longValue()) + " ms");
        methodStartUpTime.remove(methodName);
    }

    public static void endFirstScreenTime() {
        if (isEndStartUp) {
            return;
        }
        firstScreenTime = System.currentTimeMillis() - firstScreenTime;
        Log.i(TAG, "首屏渲染初始化时间 : " + firstScreenTime + " ms");
    }

    public static void printCountTime() {
        if (isEndStartUp) {
            return;
        }
        Log.i(TAG, "应用总初始化时间 : " + (applicationTime + firstScreenTime) + " ms");
        applicationTime = 0L;
        firstScreenTime = 0L;
        isEndStartUp = true;
    }
}