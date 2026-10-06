package com.xtc.utils.system;

import android.content.Context;

import com.xtc.utils.storage.FileUtils;
import com.xtc.utils.storage.SDCardUtils;

import java.io.File;

/** Cache and data directory cleaning helpers. */
public class CleanUtils {

    private CleanUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /** Clears the app cache directory. */
    public static boolean cleanInternalCache(Context context) {
        return FileUtils.cleanDirectory(context.getCacheDir());
    }

    /** Clears the app files directory. */
    public static boolean cleanInternalFiles(Context context) {
        return FileUtils.cleanDirectory(context.getFilesDir());
    }

    /** Clears the app databases directory. */
    public static boolean cleanInternalDbs(Context context) {
        return FileUtils.deleteDirectory(context.getFilesDir().getParent() + File.separator + "databases");
    }

    /** Deletes a database by name. */
    public static boolean cleanInternalDbByName(Context context, String dbName) {
        return context.deleteDatabase(dbName);
    }

    /** Clears the app shared-preferences directory. */
    public static boolean cleanInternalSp(Context context) {
        return FileUtils.deleteDirectory(context.getFilesDir().getParent() + File.separator + "shared_prefs");
    }

    /** Clears the external cache directory when external storage is mounted. */
    public static boolean cleanExternalCache(Context context) {
        return SDCardUtils.isMounted() && FileUtils.cleanDirectory(context.getExternalCacheDir());
    }

    /** Deletes the directory tree at {@code path}. */
    public static boolean cleanCustomDir(String path) {
        return FileUtils.deleteDirectory(path);
    }

    /** Deletes the directory tree. */
    public static boolean cleanCustomDir(File file) {
        return FileUtils.cleanDirectory(file);
    }
}