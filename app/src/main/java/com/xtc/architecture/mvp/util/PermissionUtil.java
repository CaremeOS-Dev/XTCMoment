package com.xtc.architecture.mvp.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.xtc.architecture.mvp.PermissionListActivity;
import com.xtc.architecture.mvp.PermissionListener;
import com.xtc.architecture.mvp.core.IPermissionInfo;

import java.util.ArrayList;

/**
 * Runtime-permission helpers plus the CTA reminder hand-off.
 *
 * <p>The four extras below are the contract between the caller and
 * {@link PermissionListActivity}.
 */
public class PermissionUtil {

    public static final String EXTRA_TIP = "EXTRA_TIP";
    public static final String EXTRA_INTENT = "EXTRA_INTENT";
    public static final String EXTRA_CTA_TITLE = "EXTRA_CTA_TITLE";
    public static final String EXTRA_CTA_PERMISSION_ALLOWED = "EXTRA_CTA_PERMISSION_ALLOWED";

    /** Request code used for the runtime-permission prompt. */
    private static final int REQUEST_CODE = 1;

    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults, PermissionListener listener) {
        if (requestCode != REQUEST_CODE || grantResults.length <= 0) {
            return;
        }
        ArrayList<String> granted = new ArrayList<String>();
        ArrayList<String> denied = new ArrayList<String>();
        for (int i = 0; i < grantResults.length; i++) {
            int result = grantResults[i];
            String permission = permissions[i];
            if (result != 0) {
                denied.add(permission);
            } else {
                granted.add(permission);
            }
        }
        if (denied.isEmpty()) {
            listener.onGranted();
        } else {
            listener.onPartPermissionDenied(granted, denied);
        }
    }

    public static void requestPermissions(String[] permissions, PermissionListener listener, Activity activity) {
        ArrayList<String> denied = new ArrayList<String>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != 0) {
                denied.add(permission);
            }
        }
        if (!denied.isEmpty()) {
            ActivityCompat.requestPermissions(activity, denied.toArray(new String[denied.size()]), REQUEST_CODE);
        } else {
            listener.onGranted();
        }
    }

    public static void showCtaPermission(Context context, Intent intent, String ctaTitle, String tip) {
        Intent ctaIntent = new Intent(context, PermissionListActivity.class);
        ctaIntent.putExtra(EXTRA_INTENT, intent);
        ctaIntent.putExtra(EXTRA_CTA_TITLE, ctaTitle);
        ctaIntent.putExtra(EXTRA_TIP, tip);
        context.startActivity(ctaIntent);
    }

    /** Checks whether the CTA reminder is needed and, if so, hands off to it. */
    public static void checkPermissionReminder(Activity activity, Intent intent, IPermissionInfo permissionInfo) {
        if (intent != null && intent.getBooleanExtra(EXTRA_CTA_PERMISSION_ALLOWED, false)) {
            permissionInfo.setCheckPermissionReminder();
        }
        if (permissionInfo.needCheckPermissionReminder()) {
            showCtaPermission(activity, intent, permissionInfo.getCtaTitle(), permissionInfo.getTip());
            activity.finish();
        }
    }
}
