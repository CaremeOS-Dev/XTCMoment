package com.xtc.utils.system;

import android.content.Context;
import android.text.TextUtils;

/** Extra system-feature checks not covered by the framework helpers. */
public class SystemFeatureEx {

    public static final String FEATURE_PPG_SENSOR = "android.hardware.sensor.ppg";
    public static final String FEATURE_NFC = "android.hardware.nfc";

    private SystemFeatureEx() {
    }

    /** @return true when the device declares the given feature. */
    public static boolean hasFeature(Context context, String feature) {
        if (context == null || TextUtils.isEmpty(feature)) {
            return false;
        }
        return context.getPackageManager().hasSystemFeature(feature);
    }

    /** @return true when the device declares the given feature at {@code version}. */
    public static boolean hasFeature(Context context, String feature, int version) {
        if (context == null || TextUtils.isEmpty(feature)) {
            return false;
        }
        return context.getPackageManager().hasSystemFeature(feature, version);
    }

    /** @return true when the watch has a heart-rate (PPG) sensor. */
    public static boolean hasPpgSensor(Context context) {
        if (context == null) {
            return false;
        }
        if (WatchModelUtil.isModel(SystemProperty.Model.Inner.I26)
                || WatchModelUtil.isModel(SystemProperty.Model.Inner.I32)) {
            return true;
        }
        return context.getPackageManager().hasSystemFeature(FEATURE_PPG_SENSOR);
    }

    /** @return true when the watch has NFC. */
    public static boolean hasNfc(Context context) {
        if (context == null) {
            return false;
        }
        return context.getPackageManager().hasSystemFeature(FEATURE_NFC);
    }
}