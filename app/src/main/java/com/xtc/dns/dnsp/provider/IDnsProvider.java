package com.xtc.dns.dnsp.provider;

import com.xtc.dns.bean.HttpDnsPack;

/**
 * DNS 解析提供者接口。
 */
public interface IDnsProvider {

    /** 解析域名。 */
    HttpDnsPack resolve(String domain);

    /** 是否可用。 */
    boolean isAvailable();

    /** 提供者名称。 */
    String getName();

    /** 提供者标识。 */
    String getTag();
}