package com.xtc.moment.module.personalinfo.net.bean;

/** Base request carrying the caller's watch id. */
public class BaseRequestBean {
    private String exwatchId;
    private String watchId;

    public BaseRequestBean(String watchId) {
        this.watchId = watchId;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public void setExwatchId(String exwatchId) {
        this.exwatchId = exwatchId;
    }

    @Override
    public String toString() {
        return "BaseRequestBean{watchId='" + this.watchId + "'}";
    }
}
