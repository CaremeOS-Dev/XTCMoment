package com.xtc.utils.system;

import java.io.Closeable;
import java.io.IOException;

/** Stream closing helpers. */
public class CloseUtils {

    private CloseUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /** Closes every stream, printing any IO error. */
    public static void closeWithLog(Closeable... closeables) {
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

    /** Closes every stream, silently ignoring IO errors. */
    public static void closeQuietly(Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        try {
            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    closeable.close();
                }
            }
        } catch (IOException ignored) {
            // ignored on purpose
        }
    }
}