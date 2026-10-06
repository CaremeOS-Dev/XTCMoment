package com.xtc.httplib;

/** Log tag factory used by the HTTP stack. */
public class LogTag {

    private LogTag() {
    }

    public static String tag() {
        return "HTTP";
    }

    public static String tag(String name) {
        return "HTTP-" + name;
    }
}