package com.xtc.moment.net.bean;

/** Request body for reporting a moment. */
public class ReportMomentReq {
    private String momentId;
    private String watchId;

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public void setMomentId(String momentId) {
        this.momentId = momentId;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public String getMomentId() {
        return this.momentId;
    }
}
