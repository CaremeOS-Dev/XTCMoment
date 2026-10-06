package com.xtc.utils.system;

import android.content.Context;

/** Camera feature helpers. */
public class CameraUtils {

    private CameraUtils() {
    }

    /** @return true when the device declares a camera. */
    public static boolean hasCamera(Context context) {
        return context.getPackageManager().hasSystemFeature("android.hardware.camera");
    }
}