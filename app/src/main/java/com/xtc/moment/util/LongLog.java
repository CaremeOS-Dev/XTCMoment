package com.xtc.moment.util;

import com.xtc.log.LogUtil;

/** Splits long log messages so they are not truncated by the log buffer. */
public class LongLog {

    public static final int LOGD = 5;
    public static final int LOGE = 21;
    public static final int LOGI = 16;

    private static final String TAG = "LongLog";
    private static final int MAX_LENGTH = 2048;

    private LongLog() {
    }

    private static void log(String tag, String message, int level) {
        if (message == null) {
            LogUtil.d(tag, null);
            return;
        }
        if ("".equals(message)) {
            LogUtil.d(tag, "");
            return;
        }
        int remaining = message.length();
        int offset = 0;
        while (remaining > 0) {
            int length = Math.min(remaining, MAX_LENGTH);
            int end = offset + length;
            String chunk = message.substring(offset, end);
            if (level == LOGD) {
                LogUtil.d(tag, chunk);
            } else if (level == LOGI) {
                LogUtil.i(tag, chunk);
            } else if (level == LOGE) {
                LogUtil.e(tag, chunk);
            } else {
                LogUtil.e(TAG, "log: wrong log level =" + level);
            }
            remaining -= length;
            offset = end;
        }
    }

    public static void d(String tag, String message) {
        log(tag, message, LOGD);
    }

    public static void i(String tag, String message) {
        log(tag, message, LOGI);
    }

    public static void e(String tag, String message) {
        log(tag, message, LOGE);
    }
}