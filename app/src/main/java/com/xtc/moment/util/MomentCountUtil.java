package com.xtc.moment.util;

import android.os.SystemClock;

import com.xtc.log.LogUtil;

/**
 * 动态流程耗时统计工具，用于日志里打印阶段耗时。
 */
public class MomentCountUtil {

    public static final String TAG = "Integral ---> ";

    private static long startTime;
    private static long lastTime;

    public static void resetTime(String tag) {
        startTime = SystemClock.elapsedRealtime();
        lastTime = SystemClock.elapsedRealtime();
        LogUtil.i(TAG + tag + " , 事件开始");
    }

    public static void printMsg(String tag) {
        long now = SystemClock.elapsedRealtime();
        LogUtil.d(TAG + tag + (now - lastTime) + " ms");
        lastTime = now;
    }

    public static void printTotal(String tag) {
        LogUtil.i(TAG + tag + (SystemClock.elapsedRealtime() - startTime) + " ms");
    }
}