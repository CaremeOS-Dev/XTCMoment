package com.xtc.utils.system;

import android.app.ActivityManager;
import android.app.Application;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/** Process-name and foreground-package helpers. */
public class ProcessUtils {

    private static UsageStats lastUsedStats;
    private static String lastForegroundPackage;

    private ProcessUtils() {
    }

    /** Current process name via {@code ActivityThread.currentProcessName()}. */
    public static String getCurrentProcessName() {
        try {
            Method method = Class.forName("android.app.ActivityThread", false, Application.class.getClassLoader())
                    .getDeclaredMethod("currentProcessName", new Class[0]);
            method.setAccessible(true);
            Object result = method.invoke(null, new Object[0]);
            if (result instanceof String) {
                return (String) result;
            }
            return null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    /** @return true when this app has a foreground process. */
    public static boolean isAppForeground(Context context) {
        List<ActivityManager.RunningAppProcessInfo> processes = ((ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses();
        if (processes != null && processes.size() != 0) {
            for (ActivityManager.RunningAppProcessInfo process : processes) {
                if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                        && Arrays.asList(process.pkgList).contains(context.getPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /** @return true when {@code packageName} is the current foreground package. */
    public static boolean isForegroundPackage(Context context, String packageName) {
        return TextUtils.equals(packageName, getForegroundPackage(context));
    }

    /** Current foreground package, using the API level appropriate strategy. */
    public static String getForegroundPackage(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return getForegroundPackageFromTasks(context);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            return getForegroundPackageFromProcesses(context);
        }
        return getForegroundPackageFromUsageStats(context);
    }

    /** API 19/20: read the package of the most recent task. */
    private static String getForegroundPackageFromTasks(Context context) {
        List<ActivityManager.RunningTaskInfo> tasks = ((ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1);
        if (tasks == null || tasks.size() == 0 || tasks.get(0) == null) {
            return null;
        }
        return tasks.get(0).topActivity.getPackageName();
    }

    /** API 21: read the package of the most important process. */
    private static String getForegroundPackageFromProcesses(Context context) {
        List<ActivityManager.RunningAppProcessInfo> processes = ((ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses();
        if (processes != null && processes.size() != 0) {
            for (ActivityManager.RunningAppProcessInfo process : processes) {
                if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return process.processName;
                }
            }
        }
        return null;
    }

    /** API 22+: read the package of the most recently used app. */
    public static String getForegroundPackageFromUsageStats(Context context) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                calendar.getTimeInMillis(), endTime);
        if (stats == null || stats.size() == 0) {
            return null;
        }
        for (UsageStats usageStats : stats) {
            if (lastUsedStats == null || usageStats.getLastTimeUsed() > lastUsedStats.getLastTimeUsed()) {
                lastUsedStats = usageStats;
            }
        }
        return lastUsedStats.getPackageName();
    }

    /** Returns the package of the most recent {@code MOVE_TO_FOREGROUND} event. */
    private static String getForegroundPackageFromEvents(Context context) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
        long now = System.currentTimeMillis();
        UsageEvents.Event event = new UsageEvents.Event();
        UsageEvents usageEvents = usageStatsManager.queryEvents(now - 10000, now);
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event);
            if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                lastForegroundPackage = event.getPackageName();
            }
        }
        return lastForegroundPackage;
    }
}