package com.xtc.bigdata.common.constants;

/**
 * 大数据采集全局常量与开关。
 */
public class Constants {

    public static final String AUTO_COLLECT = "自动采集点击事件";
    public static final String DATA_TRIGGER_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String FIXTIME_TRIGER_ACTION = "com.xtc.bigdata.action.fixtimetriger";
    public static String HOST_APP_ID = null;
    public static final int INIT = 0;
    public static final int MAX_UPLOAD_THREAD = 10;
    public static final long NOTIFY_INTERVAL = 30000;
    public static String PACKAGE_NAME = null;
    public static final String PHONE = "phone";
    public static final int UPLOADING = 1;
    public static final int UPLOAD_FAIL = 2;
    public static final int UPLOAD_MAX_RECORDS = 100;
    public static final int UPLOAD_SUCCESS = 3;
    public static final String VERSION_NAME = "";
    public static final String WATCH = "watch";
    public static String deviceType = "watch";
    public static boolean isDebug = true;

    public static void setPackageName(String packageName) {
        PACKAGE_NAME = packageName;
    }

    public static void setHostAppId(String hostAppId) {
        HOST_APP_ID = hostAppId;
    }

    public static void setDebug(boolean debug) {
        isDebug = debug;
    }
}