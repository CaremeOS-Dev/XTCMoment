package com.xtc.bigdata.common.db.constant;

import android.provider.BaseColumns;

/** Column names of the behaviour database. */
public class Columns implements BaseColumns {
    public static final String APP_EXIT = "/app_exit";
    public static final String APP_LAUNCH = "/app_launch";
    public static final String COLUMN_AA_APPID = "appId";
    public static final String COLUMN_AA_APPVER = "appVer";
    public static final String COLUMN_AA_MODULENAME = "moduleName";
    public static final String COLUMN_AA_PACKAGENAME = "packageName";
    public static final String COLUMN_ANONYMOUS_USERID = "anonymous_user_id";
    public static final String COLUMN_COLLECTION_LEVEL = "dataCollectLevel";
    public static final String COLUMN_EA_EVENTTYPE = "eventType";
    public static final String COLUMN_EA_FUNCTIONNAME = "functionName";
    public static final String COLUMN_EA_MODULEDETAIL = "moduleDetail";
    public static final String COLUMN_EA_PAGE = "page";
    public static final String COLUMN_EA_SESSIONID = "sessionId";
    public static final String COLUMN_EA_TTIME = "trigTime";
    public static final String COLUMN_EA_TVALUE = "trigValue";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_INNER_MODEL = "innerModel";
    public static final String COLUMN_MA_BRAND = "brand";
    public static final String COLUMN_MA_DEVICE = "devName";
    public static final String COLUMN_MA_MID = "mId";
    public static final String COLUMN_MA_OSVERSION = "osVer";
    public static final String COLUMN_OA_CHANNELID = "channelId";
    public static final String COLUMN_OA_DAVER = "daVer";
    public static final String COLUMN_OA_EXTEND = "extend";
    public static final String COLUMN_OA_EXTENDJUDGMENT = "extendJudgment";
    public static final String COLUMN_OA_STATUS = "status";
    public static final String COLUMN_ROOTED = "rooted";
    public static final String COLUMN_SECURITY_LEVEL = "dataSecurityLevel";
    public static final String COLUMN_UA_USEREXTEND = "userExtend";
    public static final String COLUMN_UA_USERID = "userId";
    public static final String COLUMN_UA_USERNAME = "userName";
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/hb.android.userbehaviors";
    public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/hb.android.userbehavior";
    public static final int DIR = 1;
    public static final String DIR_PATH = "userbehavior";
    public static final int ITEM = 2;
    public static final String ITEM_PATH = "userbehavior/#";
    public static final String PRESS_HOME_KEY = "/press_home_key";
    public static final String QUERY_DATA_KEY = "/query_data";
    public static final String REAL_TIME = "/real_time";
    public static final String REPORT_DATA_KEY = "/report_data";

    private Columns() {
    }
}