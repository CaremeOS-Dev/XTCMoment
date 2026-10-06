package com.xtc.utils.storage;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import com.xtc.moment.module.Constants;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.regex.Pattern;

/** Storage volume inspection helpers. */
public class StorageUtils {

    private static final String TAG = "StorageUtils";
    private static final Pattern PATH_SPLITTER = Pattern.compile(Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER);

    private StorageUtils() {
    }

    /** @return true when external storage is removable rather than emulated. */
    public static boolean isExternalStorageRemovable() {
        return !Environment.isExternalStorageEmulated() && Environment.isExternalStorageRemovable();
    }

    /** @return true when external storage is mounted. */
    public static boolean isExternalStorageMounted() {
        if (TextUtils.isEmpty(Environment.getExternalStorageState())) {
            return false;
        }
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /** External storage root directory. */
    public static File getExternalStorageDirectory() {
        return Environment.getExternalStorageDirectory();
    }

    /** Total size of external storage in bytes, or -1 when unmounted. */
    public static long getExternalStorageTotalSize() {
        if (!isExternalStorageMounted()) {
            return -1L;
        }
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return ((long) statFs.getBlockCount()) * ((long) statFs.getBlockSize());
    }

    /** Free ratio of external storage, or -1 when unmounted. */
    public static double getExternalStorageFreeRatio() {
        if (isExternalStorageMounted()) {
            return FileUtils.getUsableSpace(Environment.getExternalStorageDirectory());
        }
        return -1.0d;
    }

    /** Internal data directory. */
    public static File getDataDirectory() {
        return Environment.getDataDirectory();
    }

    /** Usable space of the data directory. */
    public static long getDataDirectoryUsableSpace() {
        return FileUtils.getUsableSpace(Environment.getDataDirectory());
    }

    /** Value of the {@code EXTERNAL_STORAGE} environment variable. */
    public static String getExternalStorageEnvironment() {
        return System.getenv("EXTERNAL_STORAGE");
    }

    /** All candidate external storage root paths. */
    private static String[] getStorageDirectories() {
        HashSet<String> paths = new HashSet<>();
        String externalStorage = System.getenv("EXTERNAL_STORAGE");
        String secondaryStorage = System.getenv("SECONDARY_STORAGE");
        String emulatedTarget = System.getenv("EMULATED_STORAGE_TARGET");
        if (TextUtils.isEmpty(emulatedTarget)) {
            if (TextUtils.isEmpty(externalStorage)) {
                paths.add("/storage/sdcard0");
            } else {
                paths.add(externalStorage);
            }
        } else {
            String userId = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                String[] parts = PATH_SPLITTER.split(Environment.getExternalStorageDirectory().getAbsolutePath());
                String lastSegment = parts[parts.length - 1];
                boolean isNumeric = false;
                try {
                    Integer.valueOf(lastSegment);
                    isNumeric = true;
                } catch (NumberFormatException ignored) {
                    // not a numeric user id
                }
                if (isNumeric) {
                    userId = lastSegment;
                }
            }
            if (TextUtils.isEmpty(userId)) {
                paths.add(emulatedTarget);
            } else {
                paths.add(emulatedTarget + File.separator + userId);
            }
        }
        if (!TextUtils.isEmpty(secondaryStorage)) {
            Collections.addAll(paths, secondaryStorage.split(File.pathSeparator));
        }
        return paths.toArray(new String[paths.size()]);
    }
}