package com.xtc.ui.widget.permission;

import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import java.util.ArrayList;

/** 权限申请兼容工具：只为尚未授权的权限附带说明文案后发起申请。 */
public class PermissionCompat {

    public static void requestPermissions(Activity activity, String[] permissions, String[] rationales, int requestCode) {
        if (activity == null) {
            return;
        }
        if (permissions == null || permissions.length == 0 || rationales == null || rationales.length == 0
                || permissions.length != rationales.length) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
            return;
        }
        ArrayList<String> pendingPermissions = new ArrayList<>();
        for (int index = 0; index < permissions.length; index++) {
            if (ContextCompat.checkSelfPermission(activity, permissions[index]) != 0) {
                pendingPermissions.add(permissions[index]);
                pendingPermissions.add(rationales[index]);
            }
        }
        if (!pendingPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(activity,
                    pendingPermissions.toArray(new String[pendingPermissions.size()]), requestCode);
        } else {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }
    }

    public static void requestPermissions(Activity activity, String[] permissions, int[] rationaleResIds, int requestCode) {
        if (activity == null) {
            return;
        }
        if (permissions == null || permissions.length == 0 || rationaleResIds == null || rationaleResIds.length == 0
                || permissions.length != rationaleResIds.length) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
            return;
        }
        ArrayList<String> pendingPermissions = new ArrayList<>();
        for (int index = 0; index < permissions.length; index++) {
            if (ContextCompat.checkSelfPermission(activity, permissions[index]) != 0) {
                pendingPermissions.add(permissions[index]);
                pendingPermissions.add(activity.getString(rationaleResIds[index]));
            }
        }
        if (!pendingPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(activity,
                    pendingPermissions.toArray(new String[pendingPermissions.size()]), requestCode);
        } else {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }
    }
}