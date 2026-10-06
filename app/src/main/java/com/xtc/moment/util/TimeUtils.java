package com.xtc.moment.util;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;

import com.xtc.bigdata.collector.utils.DateFormatUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.module.Constants;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/** Date/time formatting helpers of the moment list. */
public class TimeUtils {

    private static final long DAY_TIME = 86400000;
    private static final long HOUR_TIME = 3600000;
    private static final long MINUTE_TIME = 60000;
    private static final String TAG = Constants.MOMENT_TAG + TimeUtils.class.getSimpleName();

    private TimeUtils() {
    }

    public static Date getDayBegin() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
        calendar.set(GregorianCalendar.MINUTE, 0);
        calendar.set(GregorianCalendar.SECOND, 0);
        calendar.set(GregorianCalendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getDayEnd() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(GregorianCalendar.HOUR_OF_DAY, 23);
        calendar.set(GregorianCalendar.MINUTE, 59);
        calendar.set(GregorianCalendar.SECOND, 59);
        return calendar.getTime();
    }

    public static Date getYesterdayBegin() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(getDayBegin());
        calendar.add(GregorianCalendar.DAY_OF_MONTH, -1);
        return calendar.getTime();
    }

    public static Date getYesterdayEnd() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(getDayEnd());
        calendar.add(GregorianCalendar.DAY_OF_MONTH, -1);
        return calendar.getTime();
    }

    public static Date getTomorrowBegin() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(getDayBegin());
        calendar.add(GregorianCalendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    public static Date getTomorrowEnd() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(getDayEnd());
        calendar.add(GregorianCalendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    public static long dateDiff(Date from, Date to) {
        return to.getTime() - from.getTime();
    }

    public static double getDiffDays(Date from, Date to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("getDiffDays param is null!");
        }
        return dateDiff(from, to) / 8.64E7d;
    }

    public static int filterDays(long timestamp) {
        return (int) (dateDiff(new Date(timestamp), getDayEnd()) / DAY_TIME);
    }

    public static String getHHmmTime(Context context, long timestamp) {
        return new SimpleDateFormat(context.getString(R.string.string_hmdateformat), Locale.getDefault())
                .format(new Date(timestamp));
    }

    public static String getMMddHHmmTime(Context context, long timestamp) {
        return new SimpleDateFormat(context.getString(R.string.string_twentyfourdateformat), Locale.getDefault())
                .format(new Date(timestamp));
    }

    public static String getMMddTime(Context context, long timestamp) {
        return new SimpleDateFormat(context.getString(R.string.string_mddateformat), Locale.getDefault())
                .format(new Date(timestamp));
    }

    public static String getYYMMTime(Context context, long timestamp) {
        return new SimpleDateFormat(context.getString(R.string.string_ymdateformat), Locale.getDefault())
                .format(new Date(timestamp));
    }

    public static String getPreviesTimeTitle(Context context, long elapsedTime) {
        if (elapsedTime > 2592000000L) {
            return String.format(context.getString(R.string.string_day_ago), 30);
        }
        if (elapsedTime > DAY_TIME) {
            return String.format(context.getString(R.string.string_day_ago), elapsedTime / DAY_TIME);
        }
        return elapsedTime > HOUR_TIME
                ? String.format(context.getString(R.string.string_hour_ago), elapsedTime / HOUR_TIME)
                : String.format(context.getString(R.string.string_minite_ago), (elapsedTime / MINUTE_TIME) + 1);
    }

    public static String getTime(Context context, long timestamp) {
        if (!isCurrentYear(timestamp)) {
            return getYYMMTime(context, timestamp);
        }
        int days = filterDays(timestamp);
        String hhmm = getHHmmTime(context, timestamp);
        if (days != 0) {
            return days != 1
                    ? getMMddTime(context, timestamp)
                    : String.format(context.getString(R.string.string_yesterday_time), hhmm);
        }
        return String.format(context.getString(R.string.string_today_time), hhmm);
    }

    private static boolean isCurrentYear(long timestamp) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(new Date(timestamp));
        return calendar.get(GregorianCalendar.YEAR) == new GregorianCalendar().get(GregorianCalendar.YEAR);
    }

    public static String getNewMsgTime(Context context, long timestamp) {
        if (!isCurrentYear(timestamp)) {
            return getYYMMTime(context, timestamp);
        }
        int days = filterDays(timestamp);
        String hhmm = getHHmmTime(context, timestamp);
        if (days != 0) {
            return days != 1
                    ? getMMddTime(context, timestamp)
                    : String.format(context.getString(R.string.string_yesterday_time), hhmm);
        }
        return getDiffTime(context, timestamp);
    }

    private static String getDiffTime(Context context, long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        return diff > HOUR_TIME
                ? String.format(context.getString(R.string.string_hour_ago), diff / HOUR_TIME)
                : String.format(context.getString(R.string.string_minite_ago), (diff / MINUTE_TIME) + 1);
    }

    public static Long getFileLastModifiedTime(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return 0L;
        }
        return file.lastModified();
    }

    public static Integer getDateDifferenceToMin(Date from, Date to) {
        return ((int) (from.getTime() - to.getTime())) / Constants.Share.TO_MINUTE;
    }

    public static Integer getDateDifferenceToMin(long from, long to) {
        return (int) ((from - to) / MINUTE_TIME);
    }

    public static boolean isSameDay(long timestamp) {
        return filterDays(timestamp) == 0;
    }

    public static long getTimeByFormat(String time) {
        if (TextUtils.isEmpty(time)) {
            return 0L;
        }
        Date date = null;
        try {
            date = new SimpleDateFormat(DateFormatUtil.FORMAT_1).parse(time);
        } catch (ParseException e) {
            LogUtil.e(TAG, "getTimeByFormat error", e);
        }
        if (date == null) {
            return 0L;
        }
        return date.getTime();
    }
}