package com.xtc.bigdata.common.constants;

/**
 * 事件类型定义与名称映射。
 */
public class EType {

    public static final String FUNCTION_MONITOR_URL = "URL监控";
    public static final String NAME_ACTIVITY_IN = "界面进入事件类型";
    public static final String NAME_ACTIVITY_OUT = "界面退出事件类型";
    public static final String NAME_APP_EXCEPTION = "应用异常";
    public static final String NAME_APP_LAUNCH = "APP调出前台事件";
    public static final String NAME_APP_OUT = "APP退出事件";
    public static final String NAME_CLICK = "计次事件";
    public static final String NAME_COUNT = "计数事件";
    public static final String NAME_CUSTOM = "自定义事件";
    public static final String NAME_DURATION = "使用时长事件";
    public static final String NAME_EXCEPTION = "异常信息";
    public static final String NAME_FUNC_END = "功能点结束事件";
    public static final String NAME_MONITOR_URL = "URL监控事件";
    public static final String NAME_PAGE = "页面切出事件";
    public static final String NAME_SEARCH = "搜索事件";
    public static final String NAME_SYSTEM_INFO = "系统信息事件";
    public static final String NAME_SYS_EXCEPTION = "系统异常";

    public static final int TYPE_ACTIVITY_IN = 1;
    public static final int TYPE_ACTIVITY_OUT = 2;
    public static final int TYPE_APP_IN = 3;
    public static final int TYPE_APP_OUT = 4;
    public static final int TYPE_CLICK = 5;
    public static final int TYPE_COUNT = 8;
    public static final int TYPE_CUSTOM = 7;
    public static final int TYPE_DROPBOX_Exception = 11;
    public static final int TYPE_EXCEPTION = 9;
    public static final int TYPE_MONITOR_URL = 14;
    public static final int TYPE_SEARCH = 6;
    public static final int TYPE_SYSTEM_INFO = 10;

    public static String getEventNameByEventType(int eventType) {
        if (eventType == TYPE_MONITOR_URL) {
            return NAME_MONITOR_URL;
        }
        switch (eventType) {
            case TYPE_ACTIVITY_IN:
                return NAME_ACTIVITY_IN;
            case TYPE_ACTIVITY_OUT:
                return NAME_ACTIVITY_OUT;
            case TYPE_APP_IN:
                return NAME_APP_LAUNCH;
            case TYPE_APP_OUT:
                return NAME_APP_OUT;
            case TYPE_CLICK:
                return NAME_CLICK;
            case TYPE_SEARCH:
                return NAME_SEARCH;
            case TYPE_CUSTOM:
                return NAME_CUSTOM;
            case TYPE_COUNT:
                return NAME_COUNT;
            case TYPE_EXCEPTION:
                return NAME_EXCEPTION;
            case TYPE_SYSTEM_INFO:
                return NAME_SYSTEM_INFO;
            default:
                return "";
        }
    }
}