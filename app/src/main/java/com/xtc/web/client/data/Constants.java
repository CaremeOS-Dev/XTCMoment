package com.xtc.web.client.data;

/** web.client 模块常量。 */
public interface Constants {

    String LANGUAGE = "language";
    String PACKAGE_NAME = "packageName";
    String TAG = "WebClient_";
    String TYPE = "type";
    String WEB_APP_ACTION = "com.xtc.web.app.time.action";

    /** H5 侧监听的系统事件类型。 */
    interface ReceiverType {
        int ALARM_CLOCK = 1;
        int CLASS_MODE = 10;
        int LIFECYCLE_ONSTOP = 13;
        int PHONE_CALL = 5;
        int POWER_CONNECTED = 8;
        int POWER_KEY = 11;
        int POWER_LOW = 4;
        int SCREEN_OFF = 7;
        int SCREEN_ON = 12;
        int TEMPERATURE = 3;
        int VIDEO_CALL = 6;
        int WATCH_LOSS = 9;
        int WORKPLAN_START = 2;
    }
}