package com.xtc.bigdata.common.utils;

import android.content.Context;
import android.net.Uri;

import com.xtc.bigdata.common.constants.Constants;
import com.xtc.bigdata.common.db.constant.Columns;

import java.io.File;

/**
 * 行为采集 Provider 的 Uri 构造工具。
 */
public class UriUtils {

    public static String getAuthority() {
        return Constants.HOST_APP_ID + ".provider";
    }

    private static Uri getCombineUri(String authority, String suffix) {
        return Uri.parse(com.xtc.moment.module.Constants.ProviderConstants.PREFIX_CONTENT_PROVIDER + authority + File.separator + Columns.DIR_PATH + suffix);
    }

    public static Uri getContentUri(Context context) {
        return getCombineUri(getAuthority(), "");
    }

    public static Uri getRealTimeContentUri(Context context) {
        return getCombineUri(getAuthority(), Columns.REAL_TIME);
    }

    public static Uri getAppLaunchContentUri(Context context) {
        return getCombineUri(getAuthority(), Columns.APP_LAUNCH);
    }

    public static Uri getAppExitContentUri(Context context) {
        return getCombineUri(getAuthority(), Columns.APP_EXIT);
    }

    public static Uri getPressHomeKeyContentUri(Context context) {
        return getCombineUri(getAuthority(), Columns.PRESS_HOME_KEY);
    }

    public static Uri getQueryDataContentUri(String authority) {
        return getCombineUri(authority + ".provider", Columns.QUERY_DATA_KEY);
    }

    public static Uri getReportDataContentUri(String authority) {
        return getCombineUri(authority + ".provider", Columns.REPORT_DATA_KEY);
    }
}