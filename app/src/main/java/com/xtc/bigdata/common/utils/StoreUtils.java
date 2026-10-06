package com.xtc.bigdata.common.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;

import com.xtc.log.LogUtil;
import com.xtc.moment.module.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

/**
 * 存储与内存信息工具。
 */
public class StoreUtils {

    private static final int GB = 1073741824;
    private static final int KB = 1024;
    private static final long MB = 1048576;
    private static final String TAG = "StoreUtils";

    private StoreUtils() {
    }

    public static long getExternalStoreTotalSize() {
        return StorageUtils.getExternalStorageTotalSize();
    }

    public static long getExternalStoreAvailableSize() {
        return StorageUtils.getDataAvailableSize();
    }

    public static long getInternalStoreTotalSize() {
        StatFs statFs = new StatFs(Environment.getRootDirectory().getPath());
        return statFs.getBlockCountLong() * statFs.getBlockSizeLong();
    }

    public static long getInternalStoreAvailableSize() {
        StatFs statFs = new StatFs(Environment.getRootDirectory().getPath());
        return statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong();
    }

    /**
     * 递归计算文件或目录大小。
     */
    public static long getFileSize(String path) {
        if (path == null) {
            return 0L;
        }
        File file = new File(path);
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children == null) {
                return 0L;
            }
            long total = 0;
            for (File child : children) {
                long size;
                if (child.isDirectory()) {
                    size = getFileSize(child.getAbsolutePath());
                } else {
                    size = child.length();
                }
                total += size;
            }
            return total;
        }
        return file.length();
    }

    public static boolean deleteFolder(String path) {
        if (path == null) {
            return false;
        }
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        if (file.isFile()) {
            return deleteFile(path);
        }
        return deleteDirectory(path);
    }

    public static boolean checkFileDirExisted(String path) {
        String parentDir = getParentDir(path);
        if (parentDir == null) {
            return false;
        }
        File file = new File(parentDir);
        try {
            if (file.exists() || file.mkdirs()) {
                return true;
            }
            LogUtil.d(TAG, "create folder " + parentDir + " failed");
            return true;
        } catch (SecurityException e) {
            LogUtil.e(TAG, e);
            return false;
        }
    }

    public static boolean compare2Sec(long first, long second, int seconds) {
        long diff = Math.abs(first - second);
        Log.v(TAG, (diff / 1000) + "");
        return diff / 1000 > ((long) seconds);
    }

    public static String convertSizeUnit(long size) {
        if (size >= GB) {
            return String.format(Locale.CHINA, "%.02f GB", Float.valueOf(size / 1.07374182E9f));
        }
        return (size < MB || size >= GB)
                ? String.format(Locale.CHINA, "%.02f KB", Float.valueOf(size / 1024.0f))
                : String.format(Locale.CHINA, "%.02f MB", Float.valueOf(size / 1048576.0f));
    }

    public static String availablepercent(long available, long total) {
        if (total <= 0) {
            return null;
        }
        return ((available * 100) / total) + "%";
    }

    /**
     * 读取 /proc/meminfo 中的 MemTotal。
     */
    public static long getMemoryTotalSize() {
        BufferedReader reader = null;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader("/proc/meminfo");
            reader = new BufferedReader(fileReader, 2048);
            String line = reader.readLine();
            if (TextUtils.isEmpty(line)) {
                return 0L;
            }
            return ((long) Integer.parseInt(line.substring(line.indexOf("MemTotal:")).replaceAll("\\D+", ""))) * PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            return 0L;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    LogUtil.e(TAG, e);
                }
            }
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (Exception e) {
                    LogUtil.e(TAG, e);
                }
            }
        }
    }

    public static long getMemoryAvailable(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        if (activityManager == null) {
            LogUtil.e(TAG, "error , ActivityManager = null !");
            return 0L;
        }
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }

    private static boolean externalStoreAvailable() {
        String state = Environment.getExternalStorageState();
        return state != null && state.equals(Environment.MEDIA_MOUNTED);
    }

    private static boolean deleteFile(String path) {
        return FileUtils.deleteFile(path);
    }

    private static boolean deleteDirectory(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        return FileUtils.deleteDir(new File(path));
    }

    private static String getParentDir(String path) {
        if (path == null) {
            return null;
        }
        try {
            int index = path.lastIndexOf(Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER);
            if (index <= -1) {
                return null;
            }
            return path.substring(0, index);
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }
}