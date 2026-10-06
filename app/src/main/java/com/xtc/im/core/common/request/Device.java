package com.xtc.im.core.common.request;

/** 设备信息提供者。 */
public interface Device {

    String getAndroidOsVersion();

    String getAndroidSysName();

    String getAppKeyFromMetaData();

    String getBasebandVersion();

    String getBuildNumber();

    String getDeviceId();

    String getImei();

    String getImsi();

    String getMacAddress();

    String getModelNumber();

    String getScreenResolution();

    int getSysSDKVersion();
}