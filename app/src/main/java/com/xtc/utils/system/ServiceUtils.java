package com.xtc.utils.system;

import android.app.ActivityManager;
import android.content.Context;

import java.util.Iterator;

/** Service running-state helpers. */
public final class ServiceUtils {

    private ServiceUtils() {
    }

    /** @return true when the given service class is running. */
    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        Iterator<ActivityManager.RunningServiceInfo> iterator = ((ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE)).getRunningServices(Integer.MAX_VALUE).iterator();
        while (iterator.hasNext()) {
            if (serviceClass.getName().equals(iterator.next().service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}