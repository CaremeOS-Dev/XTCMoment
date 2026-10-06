package com.xtc.moment.util;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xtc.moment.module.Constants;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.storage.SharedManager;

import java.util.List;

/**
 * 动态模块的 SharedPreferences 读写封装。
 */
public class SharedTool {

    public static boolean isFirstUseMomentText(Context context) {
        return SharedManager.getInstance(context).getBoolean(Constant.MomentSupervise.FIRST_USE_MOMENT_TEXT, true);
    }

    public static void saveFirstUseMomentText(Context context, boolean value) {
        SharedManager.getInstance(context).putBoolean(Constant.MomentSupervise.FIRST_USE_MOMENT_TEXT, value);
    }

    public static boolean isFirstUseMomentPhoto(Context context) {
        return SharedManager.getInstance(context).getBoolean(Constant.MomentSupervise.FIRST_USE_MOMENT_PHOTO, true);
    }

    public static void saveFirstUseMomentPhoto(Context context, boolean value) {
        SharedManager.getInstance(context).putBoolean(Constant.MomentSupervise.FIRST_USE_MOMENT_PHOTO, value);
    }

    public static boolean isFirstUseMomentCamera(Context context) {
        return SharedManager.getInstance(context).getBoolean(Constant.MomentSupervise.FIRST_USE_MOMENT_CAMERA, true);
    }

    public static void saveFirstUseMomentCamera(Context context, boolean value) {
        SharedManager.getInstance(context).putBoolean(Constant.MomentSupervise.FIRST_USE_MOMENT_CAMERA, value);
    }

    public static boolean isFirstUseMomentVideo(Context context) {
        return SharedManager.getInstance(context).getBoolean(Constant.MomentSupervise.FIRST_USE_MOMENT_VIDEO, true);
    }

    public static void saveFirstUseMomentVideo(Context context, boolean value) {
        SharedManager.getInstance(context).putBoolean(Constant.MomentSupervise.FIRST_USE_MOMENT_VIDEO, value);
    }

    public static boolean isBanToPublish(Context context) {
        return SharedManager.getInstance(context).getBoolean(Constant.MomentSupervise.BAN_PUBLISH_STATUS, false);
    }

    public static void saveBanToPublish(Context context, boolean value) {
        SharedManager.getInstance(context).putBoolean(Constant.MomentSupervise.BAN_PUBLISH_STATUS, value);
    }

    public static String getReportInfoMessage(Context context) {
        return SharedManager.getInstance(context).getString(Constants.SpConstant.REPORT_INFORMATION_DATA, "");
    }

    public static long getCanRequestReportTime(Context context) {
        return SharedManager.getInstance(context).getLong(Constants.SpConstant.REPORT_IS_CAN_REQUEST, 0L);
    }

    public static boolean saveCanRequestReportTime(Context context, long time) {
        return SharedManager.getInstance(context).putLong(Constants.SpConstant.REPORT_IS_CAN_REQUEST, time);
    }

    public static void removeReportInfoMessage(Context context) {
        SharedManager.getInstance(context).remove(Constants.SpConstant.REPORT_INFORMATION_DATA);
    }

    public static boolean saveCopyAssetsState(Context context, boolean value) {
        return SharedManager.getInstance(context).putBoolean(Constant.MomentSupervise.IS_COPY_ASSETS_FILE, value);
    }

    public static boolean isCopyAssets(Context context) {
        return SharedManager.getInstance(context).getBoolean(Constant.MomentSupervise.IS_COPY_ASSETS_FILE, false);
    }

    public static boolean saveFlagFromOS(Context context, int flag) {
        return SharedManager.getInstance(context).putInt(Constants.SpConstant.SP_KEY_FROM_WEI_CHAT, flag);
    }

    public static int getFlagFromOS(Context context) {
        return SharedManager.getInstance(context).getInt(Constants.SpConstant.SP_KEY_FROM_WEI_CHAT, 0);
    }

    public static boolean getIsRunning(Context context) {
        return SharedManager.getInstance(context).getBoolean(Constants.SpConstant.SP_MOMENT_IS_RUNNING, false);
    }

    public static void saveIsRunning(Context context, boolean running) {
        SharedManager.getInstance(context).putBoolean(Constants.SpConstant.SP_MOMENT_IS_RUNNING, running);
    }

    public static boolean saveLikesRule(Context context, String rule) {
        return SharedManager.getInstance(context).putString(Constants.SpConstant.LIKE_RULES, rule);
    }

    public static String getLikesRule(Context context) {
        return SharedManager.getInstance(context).getString(Constants.SpConstant.LIKE_RULES, "");
    }

    public static boolean getIsHintPermission(Context context) {
        return SharedManager.getInstance(context).getBoolean(Constants.SpConstant.SP_MOMENT_HINT_PERMISSION, false);
    }

    public static void saveHintPermission(Context context, boolean hint) {
        SharedManager.getInstance(context).putBoolean(Constants.SpConstant.SP_MOMENT_HINT_PERMISSION, hint);
    }

    public static long getPrerogativeRefreshTime(Context context) {
        return SharedManager.getInstance(context).getLong(Constants.SpConstant.SP_MOMENT_REFRESH_PREROGATIVE_RESOURCE_TIME, 0L);
    }

    public static void savePrerogativeRefreshTime(Context context, long time) {
        SharedManager.getInstance(context).putLong(Constants.SpConstant.SP_MOMENT_REFRESH_PREROGATIVE_RESOURCE_TIME, time);
    }

    public static String getPrerogativeLikeData(Context context) {
        return SharedManager.getInstance(context).getString(Constants.SpConstant.SP_MOMENT_PREROGATIVE_RESOURCE_LIKE, "");
    }

    public static void savePrerogativeLikeData(Context context, String data) {
        SharedManager.getInstance(context).putString(Constants.SpConstant.SP_MOMENT_PREROGATIVE_RESOURCE_LIKE, data);
    }

    public static String getPrerogativeBackgroundData(Context context) {
        return SharedManager.getInstance(context).getString(Constants.SpConstant.SP_MOMENT_PREROGATIVE_RESOURCE_BACKGROUND, "");
    }

    public static void savePrerogativeBackgroundData(Context context, String data) {
        SharedManager.getInstance(context).putString(Constants.SpConstant.SP_MOMENT_PREROGATIVE_RESOURCE_BACKGROUND, data);
    }

    public static boolean getIsLocationPermission(Context context) {
        return SharedManager.getInstance(context).getBoolean(Constants.SpConstant.SP_MOMENT_LOCATION_PERMISSION, false);
    }

    public static void saveLocationPermission(Context context, boolean granted) {
        SharedManager.getInstance(context).putBoolean(Constants.SpConstant.SP_MOMENT_LOCATION_PERMISSION, granted);
    }

    public static List<String> getLbsAnimList(Context context) {
        return new Gson().fromJson(
                SharedManager.getInstance(context).getString(Constants.SpConstant.SP_KEY_LBS_ANIM_LIST, "[]"),
                new TypeToken<List<String>>() {
                }.getType());
    }

    public static void saveLbsAnimList(Context context, List<String> list) {
        SharedManager.getInstance(context).putString(Constants.SpConstant.SP_KEY_LBS_ANIM_LIST, JSONUtil.toJSON(list));
    }

    public static String getLastReportMomentId(Context context) {
        return SharedManager.getInstance(context).getString(Constants.SpConstant.SP_LAST_REPORT_MOMENT_ID, "");
    }

    public static void saveLastReportMomentId(Context context, String momentId) {
        SharedManager.getInstance(context).putString(Constants.SpConstant.SP_LAST_REPORT_MOMENT_ID, momentId);
    }

    public static void savePullReminderConfigTime(Context context, long time) {
        SharedManager.getInstance(context).putLong(Constants.SpConstant.SP_PULL_REMINDER_CONFIG_TIME, time);
    }

    public static long getLastPullReminderConfigTime(Context context) {
        return SharedManager.getInstance(context).getLong(Constants.SpConstant.SP_PULL_REMINDER_CONFIG_TIME, 0L);
    }

    public static int getReminderLabelCount(Context context, String key) {
        return SharedManager.getInstance(context).getInt(key, 0);
    }

    public static void saveReminderLabelCount(Context context, String key, int count) {
        SharedManager.getInstance(context).putInt(key, count);
    }
}