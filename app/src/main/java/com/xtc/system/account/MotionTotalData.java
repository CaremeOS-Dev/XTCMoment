package com.xtc.system.account;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.system.account.bean.NetFriendRank;
import com.xtc.utils.encode.JSONUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 运动总数据与好友排行读取。
 */
public class MotionTotalData {

    public String dateToday;
    public int totalCalories;
    public int totalDistance;
    public int totalStep;

    public MotionTotalData() {
    }

    public MotionTotalData(String dateToday, int totalStep, int totalCalories, int totalDistance) {
        this.dateToday = dateToday;
        this.totalStep = totalStep;
        this.totalCalories = totalCalories;
        this.totalDistance = totalDistance;
    }

    public String getDateToday() {
        return this.dateToday;
    }

    public void setDateToday(String dateToday) {
        this.dateToday = dateToday;
    }

    public int getTotalStep() {
        return this.totalStep;
    }

    public void setTotalStep(int totalStep) {
        this.totalStep = totalStep;
    }

    public int getTotalCalories() {
        return this.totalCalories;
    }

    public void setTotalCalories(int totalCalories) {
        this.totalCalories = totalCalories;
    }

    public int getTotalDistance() {
        return this.totalDistance;
    }

    public void setTotalDistance(int totalDistance) {
        this.totalDistance = totalDistance;
    }

    @Override
    public String toString() {
        return "MotionTotalData{dateToday='" + this.dateToday + "', totalStep=" + this.totalStep + ", totalCalories=" + this.totalCalories + ", totalDistance=" + this.totalDistance + '}';
    }

    public static synchronized List<NetFriendRank> getFriendList(Context context) {
        String json;
        try {
            json = context.getContentResolver().getType(Uri.parse("content://com.xtc.motion/friend_list"));
        } catch (Exception e) {
            json = "";
        }
        LogUtil.i("life", "json:" + json);
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        return JSONUtil.fromJSON(json, List.class, NetFriendRank.class);
    }

    public static synchronized int getGoalSteps(Context context) {
        String value;
        try {
            value = context.getContentResolver().getType(Uri.parse("content://com.xtc.motion/goal_steps"));
        } catch (Exception e) {
            value = "8000";
        }
        LogUtil.i("life", "steps:" + value);
        return Integer.valueOf(value).intValue();
    }

    public static synchronized MotionTotalData getLastTotalData(Context context, boolean refresh) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = refresh ? Uri.parse("content://com.xtc.motion/refresh_data") : Uri.parse("content://com.xtc.motion/read_data");
        String json;
        try {
            json = contentResolver.getType(uri);
        } catch (Exception e) {
            json = "";
        }
        LogUtil.i("life", "json:" + json);
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        return JSONUtil.fromJSON(json, MotionTotalData.class);
    }

    public static String getDateToDay() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());
    }
}