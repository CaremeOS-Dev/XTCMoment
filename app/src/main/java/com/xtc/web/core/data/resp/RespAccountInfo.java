package com.xtc.web.core.data.resp;

/** 开放平台账号信息。 */
public class RespAccountInfo {

    private Integer appId;
    private Integer authId;
    private String openId;

    public Integer getAppId() {
        return this.appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Integer getAuthId() {
        return this.authId;
    }

    public void setAuthId(Integer authId) {
        this.authId = authId;
    }

    public String getOpenId() {
        return this.openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    @Override
    public String toString() {
        return "GetOpenIdResponse{appId=" + this.appId + ", authId=" + this.authId + ", openId='" + this.openId + "'}";
    }
}