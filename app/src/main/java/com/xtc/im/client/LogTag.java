package com.xtc.im.client;

/** im.client 模块日志 TAG。 */
public class LogTag {

    public static String tag() {
        return "IM-Client";
    }

    public static String tag(String suffix) {
        return "IM-Client-" + suffix;
    }
}