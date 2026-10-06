package com.xtc.dns.client.beh;

import android.content.Context;

import com.xtc.common.bigdata.BehaviorUtil;

import java.util.HashMap;

/**
 * DNS 客户端埋点工具。
 */
public class DnsBeh {

    /** 本地解析来源。 */
    public static final String SOURCE_LOCAL = "local";
    /** 腾讯解析来源。 */
    public static final String SOURCE_TENCENT = "tencent";

    private static final String EVENT_ID = "apm_http_dns_req";
    private static final String KEY_SUCCESS = "success";
    private static final String KEY_HOST = "host";
    private static final String KEY_IPS = "ips";
    private static final String KEY_TIME_CONSUMED = "time_consumed";
    private static final String KEY_SOURCE = "source";

    /** 上报解析成功。 */
    public static void reportSuccess(Context context, String host, String ips, long timeConsumed, String source) {
        HashMap<String, String> params = new HashMap<>();
        params.put(KEY_SUCCESS, String.valueOf(true));
        params.put(KEY_HOST, host);
        params.put(KEY_IPS, ips);
        params.put(KEY_TIME_CONSUMED, String.valueOf(timeConsumed));
        params.put(KEY_SOURCE, source);
        BehaviorUtil.customEvent(context, EVENT_ID, "", null, params);
    }

    /** 上报解析失败。 */
    public static void reportFail(Context context, String source, String host) {
        HashMap<String, String> params = new HashMap<>();
        params.put(KEY_SUCCESS, String.valueOf(false));
        params.put(KEY_HOST, host);
        params.put(KEY_SOURCE, source);
        BehaviorUtil.customEvent(context, EVENT_ID, "", null, params);
    }
}