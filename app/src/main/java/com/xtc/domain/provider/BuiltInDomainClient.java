package com.xtc.domain.provider;

import com.xtc.domain.Domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** Built-in CN / South-Asia domain table. */
public class BuiltInDomainClient implements DomainProvider {

    /** Environment id of the built-in config. */
    public static final int ENV_ID = 1;

    private static final String DATA_CENTER_CN = "CN_BJ";
    private static final String DATA_CENTER_SA = "SG_SG";
    private static final String ENV_NAME_CN = "正式环境";
    private static final String ENV_NAME_SA = "东南亚正式环境";
    private static final boolean USE_SA = isSaRegion();
    private static final Map<String, Domain> DOMAINS;

    private static boolean isSaRegion() {
        String country = Locale.getDefault().getCountry();
        return "TW".equals(country) || "ID".equals(country) || "TH".equals(country) || "MY".equals(country);
    }

    static {
        HashMap<String, Domain> map = new HashMap<>(Domain.values().length);
        for (Domain domain : Domain.values()) {
            map.put(domain.getName(), domain);
        }
        DOMAINS = Collections.unmodifiableMap(map);
    }

    @Override
    public String getDataCenterCode() {
        return USE_SA ? DATA_CENTER_SA : DATA_CENTER_CN;
    }

    @Override
    public Integer getEnvId() {
        return ENV_ID;
    }

    @Override
    public String getEnvName() {
        return USE_SA ? ENV_NAME_SA : ENV_NAME_CN;
    }

    @Override
    public String getDomain(String name) {
        Domain domain = DOMAINS.get(name);
        if (domain == null) {
            return null;
        }
        if (USE_SA) {
            return domain.getSaDomain();
        }
        return domain.getCnDomain();
    }
}