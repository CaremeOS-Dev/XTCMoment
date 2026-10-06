package com.xtc.moment.module.illegal.util;

import android.content.Context;
import android.content.res.Resources;

import com.xtc.bigdata.collector.utils.DateFormatUtil;
import com.xtc.moment.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 违规处置时间格式化工具。
 */
public class ConfigTimeFormatUtil {

    public static long formatMinuteConvertMillisecond(long minutes) {
        return minutes * 60 * 1000;
    }

    public static int formatDelayTime(long seconds) {
        return (int) (seconds / 60);
    }

    public static String formatToDate(String millis) {
        return formatToDate(millis, DateFormatUtil.FORMAT_1);
    }

    public static String formatToDate(String millis, String pattern) {
        return new SimpleDateFormat(pattern).format(new Date(new Long(millis).longValue()));
    }

    public static String formatDelaySendMessageExpireTime(Context context, long minutes) {
        long expireTime = System.currentTimeMillis() + formatMinuteConvertMillisecond(minutes);
        Resources resources = context.getResources();
        return formatToDate(String.valueOf(expireTime),
                "yyyy" + resources.getString(R.string.text_year)
                        + "MM" + resources.getString(R.string.text_month)
                        + "dd" + resources.getString(R.string.text_day)
                        + "HH" + resources.getString(R.string.text_hour));
    }

    public static String formatDisableSendExpireTime(Context context, long expireTime) {
        Resources resources = context.getResources();
        return formatToDate(String.valueOf(expireTime),
                "MM" + resources.getString(R.string.text_month)
                        + "dd" + resources.getString(R.string.text_day)
                        + " HH" + resources.getString(R.string.text_hour)
                        + "mm" + resources.getString(R.string.text_minutes));
    }

    public static String formatDisableSendDurationTime(Context context, long seconds) {
        int delayTime = formatDelayTime(seconds);
        String dayText = context.getResources().getString(R.string.text_day);
        if (delayTime % 24 != 0) {
            return delayTime + context.getResources().getString(R.string.text_hour_2);
        }
        return (delayTime / 24) + dayText;
    }
}