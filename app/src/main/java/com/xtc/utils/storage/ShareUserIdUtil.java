package com.xtc.utils.storage;

import android.content.Context;
import android.content.pm.PackageManager;

import com.xtc.log.LogUtil;

/** Helpers for accessing another app's shared-user-id storage. */
public class ShareUserIdUtil {

    private ShareUserIdUtil() {
    }

    /** Context of the shared-user-id package, or null on failure. */
    public static Context createSharedContext(Context context, String packageName) {
        try {
            return PrivateUtils.createPackageContext(context, packageName);
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.e(e);
            return null;
        }
    }

    /** Opens the database of another package by name. */
    public static ShareDBHelper openSharedDatabase(Context context, String dbName) {
        String path = context.getDatabasePath(dbName).getPath();
        LogUtil.i("ShareUserIdUtil", path);
        return ShareDBHelper.getInstance(path);
    }
}