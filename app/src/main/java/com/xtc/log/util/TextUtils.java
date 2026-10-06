package com.xtc.log.util;

/** Minimal string helpers used by the logging stack. */
public final class TextUtils {

    private TextUtils() {
        throw new AssertionError();
    }

    public static boolean isEmpty(CharSequence text) {
        return text == null || text.length() == 0;
    }
}
