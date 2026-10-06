package com.xtc.moment.net.bean;

/** Star rating a user gave to a location. */
public class LbsStarBean {
    private String location;
    private String momentId;
    private int star;
    private String watchId;

    public LbsStarBean(String momentId, String watchId, String location, int star) {
        this.momentId = momentId;
        this.watchId = watchId;
        this.location = location;
        this.star = star;
    }

    public String getMomentId() {
        return this.momentId;
    }

    public void setMomentId(String momentId) {
        this.momentId = momentId;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getStar() {
        return this.star;
    }

    public void setStar(int star) {
        this.star = star;
    }
}
