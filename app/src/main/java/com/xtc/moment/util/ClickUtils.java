package com.xtc.moment.util;

import com.xtc.virtualselfapi.constants.Constants;

/** Guards against rapid repeated clicks. */
public class ClickUtils {

    private static final int CLICK_LONG_DURATION = 2000;
    private static final int MIN_CLICK_DELAY_TIME = 1500;

    private static long lastClickTime;
    private static long lastLongClickTime;

    private ClickUtils() {
    }

    /** @return true when the previous click happened long enough ago. */
    public static boolean isFastClick() {
        long now = System.currentTimeMillis();
        boolean allowed = now - lastClickTime >= MIN_CLICK_DELAY_TIME;
        lastClickTime = now;
        return allowed;
    }

    /** @return true when the previous long click happened recently. */
    public static boolean isFastLongClick() {
        long now = System.currentTimeMillis();
        boolean fast = now - lastLongClickTime < Constants.DEFAULT_INIT_DELAY_TIME;
        lastLongClickTime = now;
        return fast;
    }
}