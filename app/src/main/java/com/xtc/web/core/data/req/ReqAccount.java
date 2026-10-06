package com.xtc.web.core.data.req;

/** 换取 openId 的请求体。 */
public class ReqAccount {

    private Integer appId;
    private String watchId;

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public Integer getAppId() {
        return this.appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    @Override
    public String toString() {
        return "GetOpenIdRequest{watchId='" + this.watchId + "', appId=" + this.appId + '}';
    }
}