package com.xtc.bigdata.collector.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.xtc.log.LogUtil;

import java.nio.charset.Charset;

/** Application identity helpers. */
public class AppUtils {

    private AppUtils() {
    }

    /** Application label of the current package, or an empty string. */
    public static String getModuleName(Context context) {
        String label = "";
        try {
            label = context.getPackageManager().getPackageInfo(context.getPackageName(), 0)
                    .applicationInfo.loadLabel(context.getPackageManager()).toString();
            return TextUtils.isEmpty(label) ? "" : label;
        } catch (Exception e) {
            LogUtil.e("AppUtils", e);
            return label;
        }
    }

    /** 16-char md5 app id derived from the package name. */
    public static String getAppId(Context context) throws Exception {
        return new MD5Coder(16).encode(context.getPackageName().getBytes(Charset.forName("UTF-8")));
    }

    public static String getVersionName(Context context) {
        return getVersionName(context, context.getPackageName());
    }

    public static String getVersionName(Context context, String packageName) {
        try {
            PackageInfo packageInfo = context.getApplicationContext().getPackageManager().getPackageInfo(packageName, 0);
            if (packageInfo == null) {
                return null;
            }
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException ignored) {
            return null;
        }
    }
}