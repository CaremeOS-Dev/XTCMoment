package com.xtc.bigdata.common.utils;

import java.io.Closeable;
import java.io.IOException;

/** Close helpers. */
public class CloseableUtils {

    private CloseableUtils() {
    }

    /** Closes every non-null stream, printing IO errors. */
    public static void closeAll(Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /** Closes every non-null stream, ignoring IO errors. */
    public static void closeAllQuietly(Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException ignored) {
                    // ignored
                }
            }
        }
    }
}