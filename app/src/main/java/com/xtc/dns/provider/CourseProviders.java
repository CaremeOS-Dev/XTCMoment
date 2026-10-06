package com.xtc.dns.provider;

/**
 * HTTP DNS 内容提供者的路径与方法常量。
 */
public interface CourseProviders {

    /** 提供者 authority 模板。 */
    String AUTHORITY_FORMAT = "%s.com.xtc.httpdns.provider";
    /** 域名路径。 */
    String PATH_DOMAIN_MODEL = "DomainModel/*";
    /** 检查 IP 是否过期。 */
    String METHOD_CHECK_IP_EXPIRE = "checkIpExpire";
    /** 检查 IP 是否超时。 */
    String METHOD_CHECK_IP_OVER_TIME = "checkIpOverTime";
    /** 降低 IP 优先级。 */
    String METHOD_DECREASE_IP_PRIORITY = "decreaseIpPriority";
    /** 清空。 */
    String METHOD_CLEAR = "clear";

    /** 配置表字段。 */
    interface ConfigColumn {
        String IS_HTTP_DNS_ENABLE = "isHttpDnsEnable";
    }

    /** 域名表字段。 */
    interface DomainColumn {
        String ID = "id";
        String DOMAIN = "domain";
        String CREATE_TIME = "createTime";
        String DOMAIN_TTL = "domainTTL";
        String IP = "ip";
        String IP_TTL = "ipTTL";
        String PRIORITY = "priority";
        String SP = "sp";
    }
}