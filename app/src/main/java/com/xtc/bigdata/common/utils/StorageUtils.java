package com.xtc.bigdata.common.utils;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import com.xtc.moment.module.Constants;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * 存储空间查询工具。
 */
public class StorageUtils {

    private static final Pattern DIR_SEPORATOR = Pattern.compile(Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER);
    private static final String TAG = "StorageUtils";

    private StorageUtils() {
    }

    public static boolean isExternalStorageRemoveable() {
        return !Environment.isExternalStorageEmulated() && Environment.isExternalStorageRemovable();
    }

    public static boolean isExternalStroageAvailable() {
        if (TextUtils.isEmpty(Environment.getExternalStorageState())) {
            return false;
        }
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static File getExternalStorageFile() {
        return Environment.getExternalStorageDirectory();
    }

    public static long getExternalStorageTotalSize() {
        if (!isExternalStroageAvailable()) {
            return -1L;
        }
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return statFs.getBlockCountLong() * statFs.getBlockSizeLong();
    }

    public static double getExternalStorageAvailableSize() {
        if (isExternalStroageAvailable()) {
            return FileUtils.getFileAvaiableSize(Environment.getExternalStorageDirectory());
        }
        return -1.0d;
    }

    public static File getDataFile() {
        return Environment.getDataDirectory();
    }

    public static long getDataAvailableSize() {
        return FileUtils.getFileAvaiableSize(Environment.getDataDirectory());
    }

    public static String getSdCardPath() {
        return System.getenv("EXTERNAL_STORAGE");
    }

    private static String[] getStorageDirectories() {
        HashSet<String> directories = new HashSet<>();
        String externalStorage = System.getenv("EXTERNAL_STORAGE");
        String secondaryStorage = System.getenv("SECONDARY_STORAGE");
        String emulatedTarget = System.getenv("EMULATED_STORAGE_TARGET");
        if (TextUtils.isEmpty(emulatedTarget)) {
            if (TextUtils.isEmpty(externalStorage)) {
                directories.add("/storage/sdcard0");
            } else {
                directories.add(externalStorage);
            }
        } else {
            String suffix = "";
            if (Build.VERSION.SDK_INT >= 17) {
                String[] parts = DIR_SEPORATOR.split(Environment.getExternalStorageDirectory().getAbsolutePath());
                String last = parts[parts.length - 1];
                boolean isNumeric = false;
                try {
                    Integer.valueOf(last);
                    isNumeric = true;
                } catch (NumberFormatException e) {
                    isNumeric = false;
                }
                if (isNumeric) {
                    suffix = last;
                }
            }
            if (TextUtils.isEmpty(suffix)) {
                directories.add(emulatedTarget);
            } else {
                directories.add(emulatedTarget + File.separator + suffix);
            }
        }
        if (!TextUtils.isEmpty(secondaryStorage)) {
            Collections.addAll(directories, secondaryStorage.split(File.pathSeparator));
        }
        return directories.toArray(new String[directories.size()]);
    }
}