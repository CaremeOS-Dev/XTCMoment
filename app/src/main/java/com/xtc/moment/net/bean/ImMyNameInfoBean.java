package com.xtc.moment.net.bean;

/** Name/number info for a watch account. */
public class ImMyNameInfoBean {
    private long changedTime;
    private String mobileId;
    private String name;
    private String number;
    private String watchId;

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMobileId() {
        return this.mobileId;
    }

    public void setMobileId(String mobileId) {
        this.mobileId = mobileId;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getChangedTime() {
        return this.changedTime;
    }

    public void setChangedTime(long changedTime) {
        this.changedTime = changedTime;
    }

    @Override
    public String toString() {
        return "ImMyNameInfoBean{number='" + this.number + "', mobileId='" + this.mobileId + "', watchId='" + this.watchId + "', name='" + this.name + "', changedTime=" + this.changedTime + '}';
    }
}
