package com.xtc.log;

/**
 * Static convenience facade over {@link Log}.
 *
 * <p>The single-argument overloads are deprecated: they log with an empty tag
 * and only survive because older call sites still use them. New code should
 * always pass a tag.
 */
public class LogUtil {

    @Deprecated
    public static void v(String message) {
        v("", message);
    }

    @Deprecated
    public static void d(String message) {
        d("", message);
    }

    @Deprecated
    public static void i(String message) {
        i("", message);
    }

    @Deprecated
    public static void w(String message) {
        w("", message);
    }

    @Deprecated
    public static void e(String message) {
        e("", message);
    }

    @Deprecated
    public static void wtf(String message) {
        wtf("", message);
    }

    @Deprecated
    public static void e(Throwable throwable) {
        e("", throwable);
    }

    public static void v(String tag, String message) {
        Log.v(tag, message);
    }

    public static void d(String tag, String message) {
        Log.d(tag, message);
    }

    public static void i(String tag, String message) {
        Log.i(tag, message);
    }

    public static void w(String tag, String message) {
        Log.w(tag, message);
    }

    public static void e(String tag, String message) {
        Log.e(tag, message);
    }

    public static void wtf(String tag, String message) {
        Log.wtf(tag, message);
    }

    public static void w(String tag, Throwable throwable) {
        Log.w(tag, throwable);
    }

    /** Error record carrying a throwable; logs with an empty message. */
    public static void e(String tag, Throwable throwable) {
        Log.e(tag, "", throwable);
    }

    public static void wtf(String tag, Throwable throwable) {
        Log.wtf(tag, throwable);
    }

    public static void v(String tag, String message, Throwable throwable) {
        Log.v(tag, message, throwable);
    }

    public static void d(String tag, String message, Throwable throwable) {
        Log.d(tag, message, throwable);
    }

    public static void i(String tag, String message, Throwable throwable) {
        Log.i(tag, message, throwable);
    }

    public static void w(String tag, String message, Throwable throwable) {
        Log.w(tag, message, throwable);
    }

    public static void e(String tag, String message, Throwable throwable) {
        Log.e(tag, message, throwable);
    }

    public static void wtf(String tag, String message, Throwable throwable) {
        Log.wtf(tag, message, throwable);
    }

    public static void flush() {
        Log.flush();
    }

    public static void close() {
        Log.close();
    }
}
