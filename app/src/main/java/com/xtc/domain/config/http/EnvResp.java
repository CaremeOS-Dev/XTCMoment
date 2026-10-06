package com.xtc.domain.config.http;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** One environment of the route response. */
public class EnvResp {

    @SerializedName("envId")
    private final Integer envId;

    @SerializedName("envName")
    private final String envName;

    @SerializedName("domains")
    private final List<DomainResp> domains;

    public EnvResp(Integer envId, String envName, List<DomainResp> domains) {
        this.envId = envId;
        this.envName = envName;
        this.domains = domains;
    }

    public Integer getEnvId() {
        return this.envId;
    }

    public String getEnvName() {
        return this.envName;
    }

    public List<DomainResp> getDomains() {
        return this.domains;
    }
}