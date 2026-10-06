package com.xtc.dns.client;

/**
 * DNS 客户端日志标签工具。
 */
public class LogTag {

    public static String tag() {
        return "HttpDnsClient";
    }

    public static String tag(String suffix) {
        return "HttpDnsClient-" + suffix;
    }
}