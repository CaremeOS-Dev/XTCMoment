package com.xtc.utils.storage;

import android.content.Context;
import android.content.pm.PackageManager;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/** Internal helpers shared by the storage utilities. */
class PrivateUtils {

    private static final long KB = 1024;
    private static final long MB = 1048576;
    private static final long GB = 1073741824;

    PrivateUtils() {
    }

    /** Closes every stream, printing any IO error. */
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

    /** Formats a byte count with three decimals (B/KB/MB/GB). */
    public static String formatSize(long size) {
        if (size < 0) {
            return "shouldn't be less than zero!";
        }
        if (size < KB) {
            return String.format(Locale.getDefault(), "%.3fB", Double.valueOf(size));
        }
        if (size < MB) {
            return String.format(Locale.getDefault(), "%.3fKB", Double.valueOf(size / 1024.0d));
        }
        if (size < GB) {
            return String.format(Locale.getDefault(), "%.3fMB", Double.valueOf(size / 1048576.0d));
        }
        return String.format(Locale.getDefault(), "%.3fGB", Double.valueOf(size / 1.073741824E9d));
    }

    /** Reads the whole stream into a byte array. */
    public static byte[] readStream(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }
        return readStreamToBuffer(inputStream).toByteArray();
    }

    /** Reads the whole stream into a byte-array output stream. */
    public static ByteArrayOutputStream readStreamToBuffer(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            while (true) {
                int read = inputStream.read(buffer, 0, 1024);
                if (read != -1) {
                    outputStream.write(buffer, 0, read);
                } else {
                    close(inputStream);
                    return outputStream;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            close(inputStream);
            return null;
        }
    }

    /** Creates a context for another installed package. */
    public static Context createPackageContext(Context context, String packageName) throws PackageManager.NameNotFoundException {
        return context.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);
    }
}