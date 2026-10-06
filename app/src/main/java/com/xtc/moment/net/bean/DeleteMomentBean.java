package com.xtc.moment.net.bean;

/** Request body for deleting a moment. */
public class DeleteMomentBean {
    private String momentId;
    private String watchId;

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public String getMomentId() {
        return this.momentId;
    }

    public void setMomentId(String momentId) {
        this.momentId = momentId;
    }

    @Override
    public String toString() {
        return "DeleteMomentBean{watchId='" + this.watchId + "', momentId='" + this.momentId + "'}";
    }
}
