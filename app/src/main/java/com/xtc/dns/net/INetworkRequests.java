package com.xtc.dns.net;

import java.util.HashMap;

/**
 * 网络请求接口。
 */
public interface INetworkRequests {

    /** GET 请求。 */
    String get(String url);

    /** 带 Referer 的 GET 请求。 */
    String get(String url, String referer);

    /** 带请求头的 GET 请求。 */
    String get(String url, HashMap<String, String> headers);
}