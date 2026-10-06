package com.xtc.bigdata.common.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.DisplayMetrics;

import com.xtc.log.LogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;

/** System information helpers. */
public class SystemInfoUtils {

    private static final String TAG = "SystemInfoUtils";

    private SystemInfoUtils() {
    }

    public static String getSystemModel() {
        return Build.MODEL;
    }

    public static String getAndroidVersion() {
        return String.valueOf(Build.VERSION.RELEASE);
    }

    public static String getRomVersion() {
        try {
            String modVersion = getSystemProperty("ro.modversion");
            String displayId = getSystemProperty("ro.build.display.id");
            if (modVersion == null) {
                modVersion = "";
            }
            if (displayId == null) {
                return modVersion;
            }
            return !displayId.equals("") ? displayId : modVersion;
        } catch (Throwable ignored) {
            return "";
        }
    }

    public static String getSystemProperty(String key) {
        try {
            return Class.forName("android.os.SystemProperties").getMethod("get", String.class).invoke(null, key).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** CPU hardware name from {@code /proc/cpuinfo}. */
    public static String getCpuName() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/cpuinfo"));
            String line;
            do {
                line = reader.readLine();
                if (line == null) {
                    return null;
                }
            } while (!line.contains("Hardware"));
            return line.split(":")[1];
        } catch (Exception e) {
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                    // ignored
                }
            }
        }
    }

    public static String getCpuInfo() {
        String[] info = {"", ""};
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/cpuinfo"), 8192);
            String[] firstLine = reader.readLine().split("\\s+");
            for (int i = 2; i < firstLine.length; i++) {
                info[0] = info[0] + firstLine[i] + " ";
            }
            info[1] = info[1] + reader.readLine().split("\\s+")[2];
            reader.close();
        } catch (IOException ignored) {
            // ignored
        }
        return info[0] + " " + info[1];
    }

    public static String getTotalRam(Context context) {
        String total = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/meminfo"), 8192);
            total = reader.readLine().split("\\s+")[1];
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (total != null ? (int) Math.ceil(new Float(Float.valueOf(total).floatValue() / 1048576.0f).doubleValue()) : 0) + "GB";
    }

    public static String getRomTotalSize(Context context) {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        return Formatter.formatFileSize(context, statFs.getBlockSizeLong() * statFs.getBlockCountLong());
    }

    public static String getAvailableRomSize(Context context) {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        return Formatter.formatFileSize(context, statFs.getBlockSizeLong() * statFs.getAvailableBlocksLong());
    }

    public static double getScreenInch(Context context) {
        try {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;
            float w = width;
            float h = height;
            return formatDouble(Math.sqrt((w / displayMetrics.xdpi) * (w / displayMetrics.xdpi)
                    + (h / displayMetrics.ydpi) * (h / displayMetrics.ydpi)));
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0d;
        }
    }

    private static double formatDouble(double value) {
        return new BigDecimal(value).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String getScreenResolutionRatio(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels + "*" + displayMetrics.heightPixels;
    }

    public static boolean isRoot() {
        try {
            return new File("/system/bin/su").exists() || new File("/system/xbin/su").exists();
        } catch (Exception e) {
            LogUtil.e(TAG, "isRoot error = " + e);
            e.printStackTrace();
            return false;
        }
    }
}