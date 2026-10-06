package com.xtc.bigdata.collector.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/** Simple date formatting helpers used by the collectors and the http stack. */
public class DateFormatUtil {

    public static final String FORMAT_1 = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_2 = "yyyyMMdd";

    private DateFormatUtil() {
    }

    public static String format() {
        return format(FORMAT_1);
    }

    public static String format(String pattern, Date date) {
        return new SimpleDateFormat(pattern).format(date);
    }

    public static String format(String pattern, long timestamp) {
        return format(pattern, new Date(timestamp));
    }

    public static String format(String pattern) {
        return format(pattern, new Date());
    }
}