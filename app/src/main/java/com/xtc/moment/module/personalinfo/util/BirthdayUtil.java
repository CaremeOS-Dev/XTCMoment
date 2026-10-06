package com.xtc.moment.module.personalinfo.util;

import android.content.Context;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * 生日相关工具：星座推算与生日文案格式化。
 */
public class BirthdayUtil {

    private static final String TAG = "BirthdayUtil";
    private static final int[] DAY_ARR = {20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22};
    private static final int[] CONSTELLATION_ARR = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};

    public static int getConstellationByBirthday(long birthday) {
        if (birthday <= 0) {
            return -1;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(birthday);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int constellation = day < DAY_ARR[month] ? CONSTELLATION_ARR[month] : CONSTELLATION_ARR[(month + 1) % 12];
        LogUtil.d(TAG, "getConstellationByBirthday: month = " + month + ", day = " + day + ",constellation = " + constellation);
        return constellation;
    }

    public static String getBirthdayString(Context context, long birthday) {
        return new SimpleDateFormat(context.getResources().getString(R.string.string_mddateformat), Locale.getDefault()).format(Long.valueOf(birthday));
    }
}