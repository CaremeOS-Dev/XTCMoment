package com.xtc.utils.storage;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/** Physical memory helpers. */
public class MemoryUtils {

    private static final long KB = 1024;

    private MemoryUtils() {
    }

    /** Total physical memory reported by {@code /proc/meminfo}. */
    public static long getTotalMemory() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/meminfo"), 2048);
            String line = reader.readLine();
            if (TextUtils.isEmpty(line)) {
                reader.close();
                return 0L;
            }
            long total = ((long) Integer.parseInt(line.substring(line.indexOf("MemTotal:")).replaceAll("\\D+", ""))) * KB;
            reader.close();
            return total;
        } catch (IOException e) {
            e.printStackTrace();
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                    ignored.printStackTrace();
                }
            }
            return 0L;
        }
    }

    /** Currently available memory reported by the ActivityManager. */
    public static long getAvailableMemory(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }
}