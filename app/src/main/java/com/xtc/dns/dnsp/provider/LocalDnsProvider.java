package com.xtc.dns.dnsp.provider;

import com.xtc.dns.LogTag;
import com.xtc.dns.beh.DnsBeh;
import com.xtc.dns.bean.HttpDnsPack;
import com.xtc.dns.dnsp.DnsConfig;
import com.xtc.dns.storage.db.DomainModel;
import com.xtc.log.LogUtil;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * 本地 DNS 解析提供者，使用系统 InetAddress 解析。
 */
public class LocalDnsProvider implements IDnsProvider {

    private static final String TAG = LogTag.tag("LocalDnsProvider");

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getTag() {
        return DnsBeh.SOURCE_LOCAL;
    }

    @Override
    public HttpDnsPack resolve(String domain) {
        try {
            LogUtil.i(TAG, "请求Dns开始，LocalDns，domain = " + domain);
            InetAddress[] addresses = InetAddress.getAllByName(domain);
            String[] ips = new String[addresses.length];
            for (int i = 0; i < addresses.length; i++) {
                ips[i] = addresses[i].getHostAddress();
            }
            if (ips.length <= 0) {
                return null;
            }
            HttpDnsPack pack = new HttpDnsPack();
            pack.setDomain(domain);
            pack.setResultCode(1);
            long now = System.currentTimeMillis();
            ArrayList<DomainModel> domainList = new ArrayList<>();
            String ipListText = "";
            for (int i = 0; i < ips.length; i++) {
                String ip = ips[i];
                ipListText = i == ips.length - 1 ? ipListText + ip : ipListText + ip + ",";
                DomainModel domainModel = new DomainModel();
                domainModel.setDomain(domain);
                domainModel.setIp(ip);
                domainModel.setPriority(0);
                domainModel.setIpTtl(60L);
                domainModel.setDomainTtl(DnsConfig.cacheTtl);
                domainModel.setCreateTime(now);
                domainList.add(domainModel);
            }
            pack.setDomainList(domainList);
            pack.setErrorMsg(ipListText);
            LogUtil.i(TAG, "请求Dns结束，LocalDns，result  = " + pack);
            return pack;
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }
}