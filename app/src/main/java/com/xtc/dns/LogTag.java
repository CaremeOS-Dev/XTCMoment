package com.xtc.dns;

/**
 * DNS 服务日志标签工具。
 */
public class LogTag {

    public static String tag() {
        return "HttpDnsServer";
    }

    public static String tag(String suffix) {
        return "HttpDnsServer-" + suffix;
    }
}