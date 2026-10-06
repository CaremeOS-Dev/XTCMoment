package com.xtc.domain.config.http;

import com.google.gson.annotations.SerializedName;

/** One service-domain mapping returned by the route service. */
public class DomainResp {

    @SerializedName("serviceName")
    private final String serviceName;

    @SerializedName("serviceDomain")
    private final String serviceDomain;

    public DomainResp(String serviceName, String serviceDomain) {
        this.serviceName = serviceName;
        this.serviceDomain = serviceDomain;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public String getServiceDomain() {
        return this.serviceDomain;
    }
}