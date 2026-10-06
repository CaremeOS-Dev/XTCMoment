package com.xtc.dns.bean;

/**
 * HTTP DNS 解析结果信息。
 */
public class HttpDnsInfo {

    private Integer cacheTime;
    private Integer dnsSwitch;
    private String ip;
    private Integer ttl;

    public Integer getCacheTime() {
        return cacheTime;
    }

    public void setCacheTime(Integer cacheTime) {
        this.cacheTime = cacheTime;
    }

    public Integer getDnsSwitch() {
        return dnsSwitch;
    }

    public void setDnsSwitch(Integer dnsSwitch) {
        this.dnsSwitch = dnsSwitch;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getTtl() {
        return ttl;
    }

    public void setTtl(Integer ttl) {
        this.ttl = ttl;
    }

    @Override
    public String toString() {
        return "HttpDnsInfo{cacheTime=" + cacheTime + ", dnsSwitch=" + dnsSwitch + ", ip='" + ip
                + "', ttl=" + ttl + '}';
    }
}