package com.xtc.dns.storage.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * DNS 域名解析记录实体。
 */
@DatabaseTable(tableName = "domain_model")
public class DomainModel {

    @DatabaseField(columnName = "id", generatedId = true)
    private long id;

    @DatabaseField(columnName = "domain", uniqueIndexName = "_idx_domain_ip")
    private String domain = "";

    @DatabaseField(columnName = "ip", indexName = "_id_ip", uniqueIndexName = "_idx_domain_ip")
    private String ip = "";

    @DatabaseField(columnName = "ip_ttl")
    private long ipTtl = 0;

    @DatabaseField(columnName = "priority")
    private int priority = 0;

    @DatabaseField(columnName = "sp")
    private String sp = "";

    @DatabaseField(columnName = "domain_ttl")
    private long domainTtl;

    @DatabaseField(columnName = "create_time")
    private long createTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSp() {
        return sp;
    }

    public void setSp(String sp) {
        this.sp = sp;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getIpTtl() {
        return ipTtl;
    }

    public void setIpTtl(long ipTtl) {
        this.ipTtl = ipTtl;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public long getDomainTtl() {
        return domainTtl;
    }

    public void setDomainTtl(long domainTtl) {
        this.domainTtl = domainTtl;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "DomainModel{id=" + id + ", domain='" + domain + "', ip='" + ip + "', ipTTL='" + ipTtl
                + "', priority=" + priority + ", sp='" + sp + "', domainTTL=" + domainTtl
                + ", createTime=" + createTime + '}';
    }
}