package com.xtc.shareapi.share.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.xtc.shareapi.share.constant.OpenApiConstant;
import com.xtc.utils.system.SystemProperty;
import com.xtc.utils.system.SystemPropertyUtil;

/**
 * 分享相关的宿主应用查询工具，包括版本号、目标类名、安装状态与网络状态等。
 */
public class ShareUtil {

    private static final String TAG = OpenApiConstant.TAG + ShareUtil.class.getSimpleName();

    /** 读取宿主应用声明的分享 SDK 版本号。 */
    public static int getHostSdkVersion(Context context, String packageName) {
        try {
            int version = context.getPackageManager()
                    .getApplicationInfo(packageName, PackageManager.GET_META_DATA)
                    .metaData.getInt(OpenApiConstant.App.META_DATA_VERSION);
            Log.d(TAG, "get host sdk version = " + version);
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "get host sdk version error = " + e.getMessage());
            return -1;
        }
    }

    /** 读取宿主应用 meta-data 中配置的目标类名。 */
    public static String getTargetClassName(Context context, String packageName, String metaKey) {
        try {
            String className = context.getPackageManager()
                    .getApplicationInfo(packageName, PackageManager.GET_META_DATA)
                    .metaData.getString(metaKey);
            Log.d(TAG, "get meta info " + className);
            return className;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** 判断目标应用是否已安装。 */
    public static boolean isInstallScene(Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "not install " + packageName, e);
            return false;
        }
    }

    /** 回跳当前页面并携带结果码。 */
    public static void startTargetActivity(Context context, int resultCode, String errorDesc) {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(context.getPackageName(), context.getClass().getName());
        intent.putExtra(OpenApiConstant.ResponseConstant.BUNDLE_ERROR_CODE, resultCode);
        intent.putExtra(OpenApiConstant.ResponseConstant.BUNDLE_ERROR_DESC, errorDesc);
        intent.setComponent(componentName);
        context.startActivity(intent);
    }

    /** 获取当前应用名称。 */
    public static synchronized String getAppName(Context context) {
        try {
            return context.getResources().getString(context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).applicationInfo.labelRes);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "get app name error = " + e);
            return null;
        }
    }

    /** 获取当前应用图标并缩放为分享用缩略图。 */
    public static synchronized Bitmap getBitmap(Context context) {
        PackageManager packageManager;
        try {
            packageManager = context.getApplicationContext().getPackageManager();
        } catch (Exception e) {
            Log.e(TAG, "get app icon error = " + e);
            return null;
        }
        try {
            return BitmapUtil.scaleIcon(context, ((BitmapDrawable) packageManager.getApplicationIcon(
                    packageManager.getApplicationInfo(context.getPackageName(), 0))).getBitmap());
        } catch (Exception e) {
            Log.e(TAG, "get app icon error = " + e);
            return null;
        }
    }

    /** 当前网络是否已连接。 */
    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /** 应用是否在自启动白名单内。 */
    public static boolean isAppInWhiteList(Context context, String packageName) {
        if (!isSystemSupportCta()) {
            Log.d(TAG, "Not Support Cta");
            return true;
        }
        try {
            Bundle bundle = context.getContentResolver().call(Uri.parse(OpenApiConstant.SelfStart.LAUNCHER_SELF_START_URI),
                    OpenApiConstant.SelfStart.METHOD_GET_PACKAGE_SELF_START, packageName, null);
            if (bundle != null) {
                return bundle.getBoolean(OpenApiConstant.SelfStart.EXTRA_SELF_START, true);
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "读取应用自启权限失败, error = " + e);
            return true;
        }
    }

    /** 系统是否支持 CTA（合规）机制。 */
    public static boolean isSystemSupportCta() {
        return SystemPropertyUtil.getInt(SystemProperty.CTA_VERSION, 0) == 1;
    }
}
