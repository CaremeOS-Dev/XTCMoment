package com.xtc.dns.dnsp.provider;

import android.net.Uri;
import android.text.TextUtils;

import com.xtc.dns.LogTag;
import com.xtc.dns.beh.DnsBeh;
import com.xtc.dns.bean.HttpDnsPack;
import com.xtc.dns.dnsp.DnsConfig;
import com.xtc.dns.net.HttpURLConnectionNetworkRequests;
import com.xtc.dns.net.INetworkRequests;
import com.xtc.dns.storage.db.DomainModel;
import com.xtc.dns.util.DesEncryptUtil;
import com.xtc.log.LogUtil;

import java.util.ArrayList;

import master.flame.danmaku.danmaku.parser.IDataSource;

/**
 * 腾讯 HttpDns 解析提供者。
 */
public class TencentHttpDnsProvider implements IDnsProvider {

    private static final String TAG = LogTag.tag("TencentHttpDnsProvider");

    private final INetworkRequests networkRequests = new HttpURLConnectionNetworkRequests();

    @Override
    public String getTag() {
        return DnsBeh.SOURCE_TENCENT;
    }

    @Override
    public HttpDnsPack resolve(String domain) {
        String url = new Uri.Builder().scheme(IDataSource.SCHEME_HTTP_TAG)
                .authority(getName())
                .path("d")
                .appendQueryParameter("dn", encryptDomain(domain))
                .appendQueryParameter("id", getAppId())
                .appendQueryParameter("ttl", "1")
                .build().toString();
        LogUtil.i(TAG, "请求Dns开始，访问TencentHttpDns,domain = " + domain + ",url = " + url);
        String response = this.networkRequests.get(url);
        if (TextUtils.isEmpty(response)) {
            return null;
        }
        String decrypted = DesEncryptUtil.decrypt(response);
        LogUtil.i(TAG, "请求Dns结束，访问TencentHttpDns,domain = " + domain + ",result = " + decrypted);
        if (TextUtils.isEmpty(decrypted)) {
            return null;
        }
        HttpDnsPack pack = new HttpDnsPack();
        try {
            String[] parts = decrypted.split(",");
            String[] ipArray = parts[0].split(";");
            String ttl = parts[1];
            pack.setErrorMsg(decrypted);
            pack.setDomain(domain);
            pack.setResultCode(0);
            long now = System.currentTimeMillis();
            ArrayList<DomainModel> domainList = new ArrayList<>();
            for (String ip : ipArray) {
                DomainModel domainModel = new DomainModel();
                domainModel.setDomain(domain);
                domainModel.setIp(ip);
                domainModel.setPriority(0);
                domainModel.setIpTtl(Long.parseLong(ttl));
                domainModel.setDomainTtl(DnsConfig.cacheTtl);
                domainModel.setCreateTime(now);
                domainList.add(domainModel);
            }
            pack.setDomainList(domainList);
            return pack;
        } catch (Exception e) {
            return null;
        }
    }

    private String getAppId() {
        return DnsConfig.tencentAppId;
    }

    private String encryptDomain(String domain) {
        return DesEncryptUtil.encrypt(domain);
    }

    @Override
    public boolean isAvailable() {
        return DnsConfig.tencentAvailable;
    }

    @Override
    public String getName() {
        return DnsConfig.tencentDnsIp;
    }
}