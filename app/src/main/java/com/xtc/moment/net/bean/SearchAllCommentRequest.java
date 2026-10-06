package com.xtc.moment.net.bean;

/** Request body for fetching all comments of a moment. */
public class SearchAllCommentRequest {
    private String momentId;
    private String momentWatchId;
    private String watchId;

    public void setMomentWatchId(String momentWatchId) {
        this.momentWatchId = momentWatchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public void setMomentId(String momentId) {
        this.momentId = momentId;
    }

    public String getMomentWatchId() {
        return this.momentWatchId;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public String getMomentId() {
        return this.momentId;
    }
}
