package com.xtc.utils.ui;

import android.text.TextUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/** File helpers shared by the UI utilities in this package. */
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Creates the file (and parents) if needed; true when it exists afterwards. */
    public static boolean createFile(File file) {
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return file.isFile();
        }
        if (!makeDirs(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Creates the directory (and parents) if needed; true when it exists afterwards. */
    public static boolean makeDirs(File file) {
        return file != null && (!file.exists() ? !file.mkdirs() : !file.isDirectory());
    }
}