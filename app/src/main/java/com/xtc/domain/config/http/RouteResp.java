package com.xtc.domain.config.http;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Response of the route-info endpoint. */
public class RouteResp {

    @SerializedName("dateCenterCode")
    private final String dataCenterCode;

    @SerializedName("envs")
    private final List<EnvResp> envs;

    public RouteResp(String dataCenterCode, List<EnvResp> envs) {
        this.dataCenterCode = dataCenterCode;
        this.envs = envs;
    }

    public String getDataCenterCode() {
        return this.dataCenterCode;
    }

    public List<EnvResp> getEnvs() {
        return this.envs;
    }
}