package com.xtc.utils.system;

import android.text.TextUtils;

import java.util.TimeZone;

/** Formats the current time-zone as an ASCII display name or GMT offset. */
public final class SystemTimeUtils {

    private static final int MILLIS_PER_MINUTE = 60000;
    private static final int MINUTES_PER_HOUR = 60;

    private SystemTimeUtils() {
        throw new UnsupportedOperationException("You can't instantiate SystemTimeUtils");
    }

    /** Time-zone display name, falling back to a {@code GMT+HH:MM} offset. */
    public static String getTimeZoneDisplayName() {
        String displayName = TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT);
        return isAscii(displayName) ? displayName : formatOffset(TimeZone.getDefault().getRawOffset());
    }

    /** @return true when the string only contains printable ASCII characters. */
    private static boolean isAscii(String value) {
        if (TextUtils.isEmpty(value)) {
            return false;
        }
        int length = value.length();
        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if ((c <= 31 && c != '\t') || c >= 127) {
                return false;
            }
        }
        return true;
    }

    /** Formats a raw UTC offset in milliseconds as {@code GMT+HH:MM}. */
    private static String formatOffset(int rawOffsetMillis) {
        char sign;
        int totalMinutes = rawOffsetMillis / MILLIS_PER_MINUTE;
        if (totalMinutes < 0) {
            sign = '-';
            totalMinutes = -totalMinutes;
        } else {
            sign = '+';
        }
        StringBuilder builder = new StringBuilder(9);
        builder.append("GMT");
        builder.append(sign);
        appendPadded(builder, 2, totalMinutes / MINUTES_PER_HOUR);
        builder.append(':');
        appendPadded(builder, 2, totalMinutes % MINUTES_PER_HOUR);
        return builder.toString();
    }

    /** Appends the number left-padded with zeros to {@code width}. */
    private static void appendPadded(StringBuilder builder, int width, int value) {
        String text = Integer.toString(value);
        for (int i = 0; i < width - text.length(); i++) {
            builder.append('0');
        }
        builder.append(text);
    }
}