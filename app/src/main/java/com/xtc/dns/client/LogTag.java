package com.xtc.dns.client;

/** Log tag factory for the HTTP-DNS client. */
public class LogTag {

    private LogTag() {
    }

    public static String tag() {
        return "HttpDnsClient";
    }

    public static String tag(String name) {
        return tag() + name;
    }
}