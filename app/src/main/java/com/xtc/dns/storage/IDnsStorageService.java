package com.xtc.dns.storage;

import com.xtc.dns.bean.HttpDnsPack;
import com.xtc.dns.storage.db.DomainModel;

import java.util.List;

/**
 * DNS 存储服务接口。
 */
public interface IDnsStorageService {

    /** 查询域名的解析记录。 */
    List<DomainModel> queryDomain(String domain);

    /** 初始化。 */
    void init();

    /** 清理超时记录。 */
    void clearExpired(long expireTime);

    /** 保存解析结果。 */
    void save(HttpDnsPack httpDnsPack);

    /** 移除域名。 */
    void removeDomain(String domain);
}