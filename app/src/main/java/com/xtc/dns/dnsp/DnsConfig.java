package com.xtc.dns.dnsp;

import android.text.TextUtils;

import com.xtc.dns.LogTag;
import com.xtc.dns.bean.HttpDnsInfo;
import com.xtc.dns.util.Tools;
import com.xtc.log.LogUtil;

import java.util.Properties;

/**
 * DNS 配置，支持从属性文件读取并写回。
 */
public class DnsConfig {

    /** 开启。 */
    public static final int ENABLE_OPEN = 1;
    /** 关闭。 */
    public static final int ENABLE_CLOSE = 0;
    /** 默认 TTL。 */
    public static final int DEFAULT_TTL = 1;

    private static final String TAG = LogTag.tag("DnsConfig");

    /** 腾讯 HttpDns 服务器 IP。 */
    public static String tencentDnsIp = "119.29.29.98";
    /** 腾讯 HttpDns 应用 ID。 */
    public static String tencentAppId = "196";
    /** 最大缓存时间（秒）。 */
    public static int maxCacheTime = 259200;
    /** 缓存 TTL（秒）。 */
    public static int cacheTtl = 480;
    /** 是否启用 HttpDns。 */
    public static boolean httpDnsEnabled = false;
    /** 腾讯 HttpDns 是否可用。 */
    public static boolean tencentAvailable = true;

    private static void setHttpDnsEnabled(boolean enabled) {
        LogUtil.d(TAG, "设置HttpDns开关：" + enabled);
        httpDnsEnabled = enabled;
    }

    private static void setTencentDnsIp(String ip) {
        LogUtil.d(TAG, "设置腾讯HttpDns ip:" + ip);
        tencentDnsIp = ip;
    }

    private static void setMaxCacheTime(int cacheTime) {
        LogUtil.d(TAG, "设置最大缓存时间：" + cacheTime);
        maxCacheTime = cacheTime;
    }

    private static void setCacheTtl(int ttl) {
        LogUtil.d(TAG, "设置缓存时间ttl:" + ttl);
        cacheTtl = ttl;
    }

    /** 异步读取配置。 */
    public static void loadAsync() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Properties properties = Tools.readProperties();
                if (properties == null) {
                    return;
                }
                try {
                    setHttpDnsEnabled(properties.getProperty("enableHttpDns", "0").equals("1"));
                    String dnsIp = properties.getProperty("httpDnsIp");
                    if (!TextUtils.isEmpty(dnsIp)) {
                        setTencentDnsIp(dnsIp);
                    }
                    String cacheTime = properties.getProperty("cacheTime");
                    if (!TextUtils.isEmpty(cacheTime)) {
                        setMaxCacheTime(Integer.parseInt(cacheTime));
                    }
                    String dnsTtl = properties.getProperty("dnsTtl");
                    if (TextUtils.isEmpty(dnsTtl)) {
                        return;
                    }
                    setCacheTtl(Integer.parseInt(dnsTtl));
                } catch (Throwable throwable) {
                    LogUtil.w(TAG, "init config failure", throwable);
                }
            }
        }).start();
    }

    /** 保存配置。 */
    public static void save(HttpDnsInfo httpDnsInfo) {
        if (httpDnsInfo == null) {
            return;
        }
        try {
            setHttpDnsEnabled(httpDnsInfo.getDnsSwitch() == 1);
            setTencentDnsIp(httpDnsInfo.getIp());
            setCacheTtl(httpDnsInfo.getTtl());
            setMaxCacheTime(httpDnsInfo.getCacheTime());
            final Properties properties = new Properties();
            properties.put("enableHttpDns", String.valueOf(httpDnsInfo.getDnsSwitch()));
            properties.put("httpDnsIp", httpDnsInfo.getIp());
            properties.put("cacheTime", String.valueOf(httpDnsInfo.getCacheTime()));
            properties.put("dnsTtl", String.valueOf(httpDnsInfo.getTtl()));
            new Thread() {
                @Override
                public void run() {
                    Tools.writeProperties(properties);
                }
            }.start();
        } catch (Throwable throwable) {
            LogUtil.w(TAG, "save config failure", throwable);
        }
    }
}