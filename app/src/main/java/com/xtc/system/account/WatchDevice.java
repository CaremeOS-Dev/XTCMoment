package com.xtc.system.account;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.utils.system.SystemProperty;

/** Reads device information from the watch system properties and Wi-Fi. */
public class WatchDevice implements Device {

    private static final String TAG = "WatchDevice";
    public static final String UNKOWN = "unkown";

    private String androidVersion;
    private String bindNumber;
    private String chipId;
    private Context context;
    private String macAddr;
    private String watchVersion;

    public WatchDevice(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public String getMacAddr() {
        if (!TextUtils.isEmpty(this.macAddr)) {
            return this.macAddr;
        }
        this.macAddr = UNKOWN;
        WifiManager wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            return this.macAddr;
        }
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        if (connectionInfo == null) {
            return this.macAddr;
        }
        this.macAddr = connectionInfo.getMacAddress();
        if (TextUtils.isEmpty(this.macAddr)) {
            this.macAddr = UNKOWN;
        }
        return this.macAddr;
    }

    @Override
    public String getBindNumber() {
        if (!TextUtils.isEmpty(this.bindNumber)) {
            return this.bindNumber;
        }
        this.bindNumber = getSystemProperty(SystemProperty.BOOT_BIND_NUMBER);
        if (TextUtils.isEmpty(this.bindNumber)) {
            this.bindNumber = UNKOWN;
        }
        return this.bindNumber;
    }

    @Override
    public String getChipId() {
        if (!TextUtils.isEmpty(this.chipId)) {
            return this.chipId;
        }
        this.chipId = getSystemProperty("ro.boot.xtc.chipid");
        if (TextUtils.isEmpty(this.chipId)) {
            this.chipId = UNKOWN;
        }
        return this.chipId;
    }

    @Override
    public String getAndroidVersion() {
        if (!TextUtils.isEmpty(this.androidVersion)) {
            return this.androidVersion;
        }
        this.androidVersion = getSystemProperty("ro.build.version.release");
        if (TextUtils.isEmpty(this.androidVersion)) {
            this.androidVersion = UNKOWN;
        }
        return this.androidVersion;
    }

    @Override
    public String getWatchVersion() {
        if (!TextUtils.isEmpty(this.watchVersion)) {
            return this.watchVersion;
        }
        this.watchVersion = getSystemProperty(SystemProperty.CURRENT_SOFT_VERSION);
        if (TextUtils.isEmpty(this.watchVersion)) {
            this.watchVersion = UNKOWN;
        }
        return this.watchVersion;
    }

    @Override
    public String getSystemProperty(String key) {
        try {
            Class<?> properties = Class.forName("android.os.SystemProperties");
            return (String) properties.getMethod("get", String.class).invoke(properties, key);
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }

    @Override
    public String getBuildType() {
        return getSystemProperty(SystemProperty.BUILD_TYPE);
    }
}