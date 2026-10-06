package com.xtc.system.account;

/** Device information supplied by the account layer. */
public interface Device {
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