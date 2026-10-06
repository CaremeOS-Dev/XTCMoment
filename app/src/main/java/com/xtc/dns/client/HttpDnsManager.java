package com.xtc.dns.client;

import android.content.Context;
import android.content.pm.PackageManager;

import com.xtc.dns.client.resolver.DnsResolver;
import com.xtc.log.LogUtil;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Caches the per-host HTTP-DNS lookups. */
public class HttpDnsManager {

    private static final String TAG = LogTag.tag("HttpDnsManager");
    private static final Object LOCK = new Object();
    private static HttpDnsManager instance = null;

    private final Context context;
    private final Map<String, List<DomainInfo>> cache = new ConcurrentHashMap<>();
    private volatile DnsResolver resolver;

    private HttpDnsManager(Context context) {
        if (context == null) {
            throw new RuntimeException("HttpDnsManager init; context can not be null!!!");
        }
        this.context = context.getApplicationContext();
        useAuthority(this.context.getPackageName());
        init();
    }

    public static HttpDnsManager getInstance(Context context) {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new HttpDnsManager(context);
                }
            }
        }
        return instance;
    }

    /** Points the resolver at the provider authority of the given package. */
    public void useAuthority(String packageName) {
        PackageManager packageManager = this.context.getPackageManager();
        String authority = String.format(Locale.ROOT, "%s.com.xtc.httpdns.provider", packageName);
        if (packageManager.resolveContentProvider(authority, 0) != null) {
            LogUtil.i(TAG, "Set DnsResolver: " + packageName);
            this.resolver = new DnsResolver(this.context, authority);
            return;
        }
        LogUtil.w(TAG, "Can not resolve provider in application: " + packageName);
        this.resolver = new DnsResolver(this.context,
                String.format(Locale.ROOT, "%s.com.xtc.httpdns.provider", this.context.getPackageName()));
    }

    /** Cached IPs for the host, querying the provider on a miss. */
    public List<DomainInfo> query(String host) {
        List<DomainInfo> cached = this.cache.get(host);
        if (cached != null && !cached.isEmpty()) {
            LogUtil.i(TAG, "cache hint: " + cached.size());
            return cached;
        }
        List<DomainInfo> result = this.resolver.queryIp(host);
        if (result != null) {
            this.cache.put(host, result);
        }
        return result;
    }

    public void checkIpExpire(String host, String ip) {
        this.cache.remove(host);
        this.resolver.checkIpExpire(host, ip);
    }

    public void decreaseIpPriority(String ip) {
        this.cache.clear();
        this.resolver.decreaseIpPriority(ip);
    }

    public void init() {
        this.resolver.checkIpOverTime();
    }

    public void clear() {
        this.resolver.clear();
    }
}