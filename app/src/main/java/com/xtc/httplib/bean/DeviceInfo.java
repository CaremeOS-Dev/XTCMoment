package com.xtc.httplib.bean;

/** Device information supplied by the host app. */
public interface DeviceInfo {
    String BUILD_TYPE_DEBUG = "userdebug";
    String BUILD_TYPE_USER = "user";

    String getAndroidVersion();

    String getBindNumber();

    String getBuildType();

    String getChipId();

    String getMacAddr();

    String getSystemProperty(String key);

    String getWatchVersion();
}