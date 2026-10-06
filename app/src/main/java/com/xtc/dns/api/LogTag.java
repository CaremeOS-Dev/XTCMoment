package com.xtc.dns.api;

/**
 * DNS API 日志标签工具。
 */
public class LogTag {

    public static String tag() {
        return "HttpDnsApi";
    }

    public static String tag(String suffix) {
        return tag() + suffix;
    }
}