package com.xtc.moment.module.prerogative.bean;

/**
 * 拉取个人特权资源的请求体。
 */
public class LoadPersonalReq {

    private String watchId;

    public LoadPersonalReq() {
    }

    public LoadPersonalReq(String watchId) {
        this.watchId = watchId;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }
}