package com.xtc.dns.dnsp;

import com.xtc.dns.storage.db.DomainModel;

import java.util.List;

/**
 * DNS 服务接口。
 */
public interface IDnsService {

    /** 解析域名。 */
    List<DomainModel> resolveDomain(String domain);

    /** 初始化。 */
    void init();

    /** 保存域名解析结果。 */
    void saveDomain(String domain, String ip);

    /** 释放资源。 */
    void release();

    /** 移除域名。 */
    void removeDomain(String domain);
}