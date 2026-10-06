package com.xtc.dns.api;

import android.content.Context;

import com.xtc.dns.client.DomainInfo;
import com.xtc.dns.client.HttpDnsManager;
import com.xtc.log.LogUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Dns;
import okhttp3.EventListener;
import okhttp3.Protocol;

/** OkHttp DNS implementation backed by the HTTP-DNS provider. */
public class DnsStrategy implements Dns, EventListener.Factory {

    /** Result of a DNS lookup, reported to {@link DnsCallback}. */
    public interface DnsCallback {
        void onDnsResult(String host, boolean success, int provider, long costMillis);
    }

    private static final String TAG = LogTag.tag("DnsStrategy");
    private static final String PROVIDER_PACKAGE = "com.xtc.i3launcher";

    /** Provider identifier for the system resolver. */
    public static final int PROVIDER_SYSTEM = 1;
    /** Provider identifier for the HTTP-DNS resolver. */
    public static final int PROVIDER_HTTP_DNS = 2;

    private final Context context;
    private HttpDnsManager httpDnsManager = null;
    private DnsCallback dnsCallback = null;

    private final EventListener eventListener = new EventListener() {
        @Override
        public void connectFailed(Call call, InetSocketAddress inetSocketAddress, Proxy proxy, Protocol protocol,
                                  IOException e) {
            DnsStrategy.this.ensureManager();
            String host = call.request().url().host();
            String address = inetSocketAddress.getAddress().getHostAddress();
            DnsStrategy.this.httpDnsManager.decreaseIpPriority(address);
            DnsStrategy.this.httpDnsManager.checkIpExpire(host, address);
        }
    };

    public DnsStrategy(Context context) {
        this.context = context;
    }

    public void setDnsCallback(DnsCallback dnsCallback) {
        this.dnsCallback = dnsCallback;
    }

    private synchronized void ensureManager() {
        if (this.httpDnsManager != null) {
            return;
        }
        this.httpDnsManager = HttpDnsManager.getInstance(this.context);
        this.httpDnsManager.useAuthority(PROVIDER_PACKAGE);
    }

    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        ensureManager();
        long start = System.currentTimeMillis();
        List<DomainInfo> domainInfos = HttpDnsManager.getInstance(this.context).query(hostname);
        LogUtil.i(TAG, "hostname = " + hostname + "---domainInfo = " + domainInfos);
        long cost = System.currentTimeMillis() - start;
        if (domainInfos == null || domainInfos.isEmpty()) {
            if (this.dnsCallback != null) {
                this.dnsCallback.onDnsResult(hostname, false, PROVIDER_HTTP_DNS, cost);
            }
            try {
                start = System.currentTimeMillis();
                List<InetAddress> addresses = Dns.SYSTEM.lookup(hostname);
                if (this.dnsCallback != null) {
                    this.dnsCallback.onDnsResult(hostname, true, PROVIDER_SYSTEM,
                            System.currentTimeMillis() - start);
                }
                return addresses;
            } catch (UnknownHostException e) {
                if (this.dnsCallback != null) {
                    this.dnsCallback.onDnsResult(hostname, false, PROVIDER_SYSTEM,
                            System.currentTimeMillis() - start);
                }
                throw e;
            }
        }
        if (this.dnsCallback != null) {
            this.dnsCallback.onDnsResult(hostname, true, PROVIDER_HTTP_DNS, cost);
        }
        ArrayList<InetAddress> addresses = new ArrayList<>(domainInfos.size());
        Iterator<DomainInfo> iterator = domainInfos.iterator();
        while (iterator.hasNext()) {
            addresses.add(InetAddress.getByName(iterator.next().getIp()));
        }
        return addresses;
    }

    @Override
    public EventListener create(Call call) {
        return this.eventListener;
    }
}