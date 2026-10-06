package com.xtc.moment;

/** Log tag factory of the moment app. */
public class LogTag {

    private LogTag() {
    }

    public static String tag() {
        return "Moment";
    }

    public static String tag(String name) {
        return "Moment-" + name;
    }
}