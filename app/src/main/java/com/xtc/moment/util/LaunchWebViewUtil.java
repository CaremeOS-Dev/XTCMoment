package com.xtc.moment.util;

import android.os.Build;

import com.xtc.log.LogUtil;
import com.xtc.utils.system.SystemPropertyUtil;

/**
 * H5 页面启动方式判断：决定是否使用多进程 WebView。
 */
public class LaunchWebViewUtil {

    public static final String PROPERTY_SUPPORT_NEW_WEBVIEW = "ro.product.support.webview";
    private static final String TAG = "LaunchWebViewUtil";

    public static boolean isLaunchMultiProcess() {
        boolean supportNewWebView = SystemUtil.isLowMachine() && SystemPropertyUtil.getBoolean(PROPERTY_SUPPORT_NEW_WEBVIEW, false);
        LogUtil.i(TAG, "isSupportNewWebView :" + supportNewWebView);
        if (supportNewWebView) {
            return false;
        }
        LogUtil.i(TAG, "need launchMultiProcess");
        return true;
    }

    public static boolean isAutoKillWebProcess() {
        return Build.VERSION.SDK_INT < 30;
    }
}