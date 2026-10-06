package com.xtc.log;

/**
 * Masks sensitive values (phone numbers, watch ids, MAC addresses, IMEIs)
 * before they reach a log record.
 *
 * <p>Each helper keeps a short prefix and suffix visible and replaces the
 * middle with asterisks, matching what the stock code printed.
 */
public class LogMask {

    public static String toMaskBindNumber(String value) {
        return isEmpty(value) ? value : toMaskImpl(value, 4, Math.max(1, value.length() - 5));
    }

    public static String toMaskWatchId(String value) {
        return isEmpty(value) ? value : toMaskImpl(value, 4, Math.max(1, value.length() - 5));
    }

    public static String toMaskMacAddress(String value) {
        if (isEmpty(value)) {
            return value;
        }
        if (value.contains(":")) {
            return toMaskImpl(value, 3, Math.max(1, value.length() - 4));
        }
        return toMaskImpl(value, 2, Math.max(1, value.length() - 3));
    }

    public static String toMaskImei(String value) {
        return isEmpty(value) ? value : toMaskImpl(value, 4, Math.max(1, value.length() - 5));
    }

    public static String toMaskPhoneNumber(String value) {
        return isEmpty(value) ? value : toMaskImpl(value, 2, Math.max(1, value.length() - 3));
    }

    public static String toMask(String value) {
        return isEmpty(value) ? value : toMaskImpl(value, 1, Math.max(1, value.length() - 2));
    }

    /** Keeps {@code [0,start)} and {@code (end,length)} verbatim, stars the rest. */
    private static String toMaskImpl(String value, int start, int end) {
        if (isEmpty(value) || start >= value.length() || start > end) {
            return value;
        }
        int from = Math.max(start, 0);
        int to = Math.min(end, value.length() - 1);
        StringBuilder builder = new StringBuilder();
        if (from > 0) {
            builder.append(value, 0, from);
        }
        if (from <= to) {
            while (from <= to) {
                builder.append("*");
                from++;
            }
        }
        if (to < value.length() - 1) {
            builder.append(value, to + 1, value.length());
        }
        return builder.toString();
    }

    private static boolean isEmpty(String value) {
        return value == null || value.length() == 0;
    }
}
