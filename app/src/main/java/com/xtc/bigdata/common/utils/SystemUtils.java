package com.xtc.bigdata.common.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.xtc.log.LogUtil;
import com.xtc.utils_screenshot_carry_data.ScreenshotUtils;

import java.util.UUID;

/**
 * 系统信息工具：设备唯一标识、路由 MAC 与渠道号。
 */
public class SystemUtils {

    private static final String TAG = "SystemUtils";

    public static String getUUID() {
        return UUID.randomUUID().toString().replace(ScreenshotUtils.SEPARATOR, "");
    }

    public static String getRouterMac(Context context) {
        return DeviceUtils.getMac(context);
    }

    public static String getChannleId(Context context) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return applicationInfo.metaData != null ? applicationInfo.metaData.getString("ChannleId") : "";
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.e(TAG, e);
            return "";
        }
    }

    private SystemUtils() {
    }
}