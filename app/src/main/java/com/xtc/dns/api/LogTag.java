package com.xtc.dns.api;

/** Log tag factory for the HTTP-DNS API. */
public class LogTag {

    private LogTag() {
    }

    public static String tag() {
        return "HttpDnsApi";
    }

    public static String tag(String name) {
        return tag() + name;
    }
}