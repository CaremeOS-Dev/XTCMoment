package com.xtc.qiniu;

import android.content.Context;

import com.qiniu.android.http.dns.Dns;
import com.qiniu.android.http.dns.DnsSource;
import com.qiniu.android.http.dns.IDnsNetworkAddress;
import com.xtc.bigdata.collector.utils.CollectionUtil;
import com.xtc.dns.api.DnsStrategy;
import com.xtc.dns.client.HttpDnsManager;
import com.xtc.log.LogUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Qiniu DNS implementation backed by the app HTTP-DNS provider. */
public class XtcDns implements Dns {

    private static final String TAG = "qiniu_upload_XtcDns";

    private final Context context;
    private final DnsStrategy dnsStrategy;
    private List<InetAddress> inetAddressList;

    public XtcDns(Context context) {
        this.context = context;
        this.dnsStrategy = new DnsStrategy(context);
    }

    @Override
    public List<IDnsNetworkAddress> lookup(String host) throws UnknownHostException {
        this.inetAddressList = this.dnsStrategy.lookup(host);
        LogUtil.d(TAG, "lookup() called with: mInetAddressList = [" + this.inetAddressList + "]");
        ArrayList<IDnsNetworkAddress> result = new ArrayList<>();
        Iterator<InetAddress> iterator = this.inetAddressList.iterator();
        while (iterator.hasNext()) {
            result.add(new XTCDnsNetworkAddress(host, iterator.next().getHostAddress(), 300L, DnsSource.Custom,
                    System.currentTimeMillis() / 1000));
        }
        return result;
    }

    /** Lowers the priority of every resolved address. */
    public void clear() {
        if (CollectionUtil.isEmpty(this.inetAddressList)) {
            return;
        }
        for (int i = 0; i < this.inetAddressList.size(); i++) {
            InetAddress inetAddress = this.inetAddressList.get(i);
            if (inetAddress != null) {
                LogUtil.d(TAG, "clear() called inetAddress = " + inetAddress);
                String hostAddress = inetAddress.getHostAddress();
                HttpDnsManager.getInstance(this.context).decreaseIpPriority(hostAddress);
                HttpDnsManager.getInstance(this.context).checkIpExpire(inetAddress.getHostName(), hostAddress);
            }
        }
    }
}