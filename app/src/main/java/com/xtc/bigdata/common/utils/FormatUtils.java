package com.xtc.bigdata.common.utils;

import android.text.TextUtils;

import com.xtc.bigdata.common.constants.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Date / log formatting helpers. */
public class FormatUtils {

    private FormatUtils() {
    }

    public static String formatExceptionLog(String text) {
        return TextUtils.isEmpty(text) ? text : text.replace("\\n", "\n").replace("\\t", "\t");
    }

    public static String getDate() {
        return getDate(new Date().getTime());
    }

    public static String getDate(long timeMillis) {
        return new SimpleDateFormat(Constants.DATA_TRIGGER_DATE_FORMAT, Locale.getDefault()).format(Long.valueOf(timeMillis));
    }
}