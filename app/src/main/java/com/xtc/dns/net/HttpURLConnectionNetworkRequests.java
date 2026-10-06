package com.xtc.dns.net;

import android.text.TextUtils;

import com.xtc.log.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于 HttpURLConnection 的网络请求实现，仅提供 GET 能力。
 */
public class HttpURLConnectionNetworkRequests implements INetworkRequests {

    private static final String TAG = "HXQ-TAG";
    private static final int CONNECT_TIMEOUT_MS = 5000;
    private static final int READ_TIMEOUT_MS = 5000;

    @Override
    public String get(String url) {
        return get(url, "");
    }

    @Override
    public String get(String url, String host) {
        HashMap<String, String> headers;
        if (TextUtils.isEmpty(host)) {
            headers = null;
        } else {
            headers = new HashMap<>();
            headers.put("Host", host);
        }
        return get(url, headers);
    }

    @Override
    public String get(String url, HashMap<String, String> headers) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            connection.setReadTimeout(READ_TIMEOUT_MS);
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    LogUtil.i(TAG, entry.getKey() + "  -  " + entry.getValue());
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}