package com.xtc.domain.config.http;

import com.google.gson.annotations.SerializedName;

/** Request body of the route-info endpoint. */
public class RouteReq {

    @SerializedName("model")
    private final String model;

    @SerializedName("accountInfo")
    private final String accountInfo;

    @SerializedName("firemware")
    private final String firmware;

    @SerializedName("accountType")
    private final Integer accountType;

    public RouteReq(String model, String accountInfo, String firmware, Integer accountType) {
        this.model = model;
        this.accountInfo = accountInfo;
        this.firmware = firmware;
        this.accountType = accountType;
    }

    public String getModel() {
        return this.model;
    }

    public String getAccountInfo() {
        return this.accountInfo;
    }

    public String getFirmware() {
        return this.firmware;
    }

    public Integer getAccountType() {
        return this.accountType;
    }
}