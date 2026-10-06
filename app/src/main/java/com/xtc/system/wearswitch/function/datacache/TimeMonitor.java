package com.xtc.system.wearswitch.function.datacache;

import com.xtc.log.Log;

import java.util.HashMap;

/**
 * 耗时统计工具，按标签记录开始时间并输出耗时。
 */
public class TimeMonitor {

    private static final HashMap<String, Long> START_TIME_MAP = new HashMap<>();

    /** 记录开始时间。 */
    public static void start(String tag) {
        START_TIME_MAP.put(tag, System.currentTimeMillis());
    }

    /** 输出耗时并移除记录。 */
    public static void end(String tag) {
        boolean isMainThread = ThreadCheck.isMainThread();
        Long startTime = START_TIME_MAP.get(tag);
        if (startTime == null) {
            return;
        }
        Log.i("TimeMonitor", tag + " 时间 : " + (System.currentTimeMillis() - startTime) + " ms ,isMainThread:" + isMainThread);
        START_TIME_MAP.remove(tag);
    }

    /** 输出耗时与附加信息并移除记录。 */
    public static void end(String tag, String extra) {
        boolean isMainThread = ThreadCheck.isMainThread();
        Long startTime = START_TIME_MAP.get(tag);
        if (startTime == null) {
            return;
        }
        Log.i("TimeMonitor", tag + " 时间 : " + (System.currentTimeMillis() - startTime) + " ms ,isMainThread:" + isMainThread + " extra: " + extra);
        START_TIME_MAP.remove(tag);
    }
}