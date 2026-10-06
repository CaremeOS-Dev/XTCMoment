package com.xtc.httplib.bean;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.httplib.LogTag;
import com.xtc.log.LogUtil;
import com.xtc.utils.system.SystemProperty;

import java.lang.reflect.InvocationTargetException;

/** Reads device information from the watch system properties. */
public class WatchInfo implements DeviceInfo {

    private static final String TAG = LogTag.tag("WatchInfo");
    public static final String UNKOWN = "unkown";

    private String androidVersion;
    private String bindNumber;
    private String chipId;
    private Context context;
    private String watchVersion;

    @Override
    public String getMacAddr() {
        return UNKOWN;
    }

    public WatchInfo(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public String getBindNumber() {
        if (!TextUtils.isEmpty(this.bindNumber) && !UNKOWN.equals(this.bindNumber)) {
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
        } catch (ClassNotFoundException e) {
            LogUtil.e(TAG, e);
        } catch (IllegalAccessException e) {
            LogUtil.e(TAG, e);
        } catch (NoSuchMethodException e) {
            LogUtil.e(TAG, e);
        } catch (InvocationTargetException e) {
            LogUtil.e(TAG, e);
        }
        return null;
    }

    @Override
    public String getBuildType() {
        return getSystemProperty(SystemProperty.BUILD_TYPE);
    }
}