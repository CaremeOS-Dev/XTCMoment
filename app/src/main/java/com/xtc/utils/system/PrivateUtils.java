package com.xtc.utils.system;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

/** File helpers shared by the system utilities in this package. */
class PrivateUtils {

    PrivateUtils() {
    }

    /** Wraps the path in a {@link File}, or null when the path is empty. */
    protected static File toFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        return new File(path);
    }

    /** Returns {@code true} when the file exists. */
    protected static boolean exists(File file) {
        return file != null && file.exists();
    }

    /** Returns the extension of the path, or an empty string when there is none. */
    protected static String getExtension(String path) {
        if (TextUtils.isEmpty(path)) {
            return path;
        }
        int dotIndex = path.lastIndexOf(46);
        return (dotIndex == -1 || path.lastIndexOf(File.separator) >= dotIndex) ? "" : path.substring(dotIndex + 1);
    }

    /** Deletes the directory tree, returning true when nothing is left behind. */
    protected static boolean deleteDirKeepingSelf(File file) {
        if (file == null) {
            return false;
        }
        if (!file.exists()) {
            return true;
        }
        if (!file.isDirectory()) {
            return false;
        }
        File[] children = file.listFiles();
        if (children != null && children.length != 0) {
            for (File child : children) {
                if (child.isFile()) {
                    if (!deleteFile(child)) {
                        return false;
                    }
                } else if (child.isDirectory() && !deleteDir(child)) {
                    return false;
                }
            }
        }
        return true;
    }

    /** Deletes the file, returning true when it no longer exists. */
    protected static boolean deleteFile(File file) {
        return file != null && (!file.exists() || (file.isFile() && file.delete()));
    }

    /** Deletes the directory tree at {@code path}. */
    protected static boolean deleteDir(String path) {
        return deleteDir(toFile(path));
    }

    /** Deletes the directory tree, returning true when the directory is gone. */
    protected static boolean deleteDir(File file) {
        if (file == null) {
            return false;
        }
        if (!file.exists()) {
            return true;
        }
        if (!file.isDirectory()) {
            return false;
        }
        File[] children = file.listFiles();
        if (children != null && children.length != 0) {
            for (File child : children) {
                if (child.isFile()) {
                    if (!deleteFile(child)) {
                        return false;
                    }
                } else if (child.isDirectory() && !deleteDir(child)) {
                    return false;
                }
            }
        }
        return file.delete();
    }

    /** Returns true when the directory tree at {@code path} can be fully deleted. */
    public static boolean canDeleteDir(String path) {
        return deleteDirKeepingSelf(toFile(path));
    }

    /** Returns {@code true} when external storage is mounted. */
    public static boolean isExternalStorageMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /** Returns {@code true} when the string is null or empty. */
    public static boolean isEmpty(String value) {
        return value == null || value.length() == 0;
    }
}