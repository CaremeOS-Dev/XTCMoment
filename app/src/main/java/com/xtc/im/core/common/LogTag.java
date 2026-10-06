package com.xtc.im.core.common;

/** im.core 模块日志 TAG。 */
public class LogTag {

    public static String tag() {
        return "IM-Core";
    }

    public static String tag(String suffix) {
        return "IM-Core-" + suffix;
    }
}