package com.xtc.data.common.database;

import java.util.List;

/** Null-safe dispatch helpers for the database callbacks. */
public class DbListenerUtil {

    private DbListenerUtil() {
    }

    public static void notifySuccess(OnDbListener listener) {
        if (listener == null) {
            return;
        }
        listener.onSuccess();
    }

    public static <T> void notifySuccess(OnGetDbListener<T> listener, T value) {
        if (listener == null) {
            return;
        }
        listener.onSuccess(value);
    }

    public static <T> void notifySuccess(OnGetDbsListener<T> listener, List<T> values) {
        if (listener == null) {
            return;
        }
        listener.onSuccess(values);
    }

    public static void notifyFail(DbFailListener listener, String message) {
        if (listener == null) {
            return;
        }
        listener.onFail(new Exception(message));
    }
}