package com.xtc.utils.encode;

import android.text.TextUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/** File helpers shared by the encoding utilities in this package. */
class PrivateUtils {

    static final int KB = 1024;
    static final int MB = 1048576;
    static final int GB = 1073741824;

    PrivateUtils() {
    }

    /** Creates the directory (and parents) if needed. */
    public static boolean makeDirs(File file) {
        if (file.isFile()) {
            return file.mkdirs();
        }
        if (file.exists()) {
            return true;
        }
        return file.mkdirs();
    }

    /** Wraps the path in a {@link File}, or null when the path is empty. */
    public static File toFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        return new File(path);
    }

    /** Closes every non-null stream, swallowing IO errors. */
    public static void close(Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        try {
            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    closeable.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Returns the file name portion of the path. */
    public static String getFileName(String path) {
        int lastSeparator;
        return (TextUtils.isEmpty(path) || (lastSeparator = path.lastIndexOf(File.separator)) == -1)
                ? path : path.substring(lastSeparator + 1);
    }

    /** Creates the directory (and parents) if needed; true when it exists afterwards. */
    public static boolean makeDirsIfNeeded(File file) {
        return file != null && (!file.exists() ? !file.mkdirs() : !file.isDirectory());
    }

    /** Creates the file (and parents) if needed; true when it exists afterwards. */
    public static boolean createFile(String path) {
        return createFile(toFile(path));
    }

    /** Creates the file (and parents) if needed; true when it exists afterwards. */
    public static boolean createFile(File file) {
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return file.isFile();
        }
        if (!makeDirsIfNeeded(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}