package com.xtc.im.transpond;

/** Holds the active {@link TranspondAdapter}. */
public class TranspondManager {

    private static TranspondAdapter transpondAdapter;

    private TranspondManager() {
    }

    public static void regist(TranspondAdapter adapter) {
        transpondAdapter = adapter;
    }

    /** @return true when the request was handed to the adapter. */
    public static boolean transpond(String url, int method, byte[] headers, byte[] body, ITranspondCallback callback) {
        TranspondAdapter adapter = transpondAdapter;
        if (adapter != null) {
            return adapter.transpondHttp(url, method, headers, body, callback);
        }
        return false;
    }
}