package com.xtc.moment.net.bean;

/** A report-punishment push payload. */
public class ReportPushData {
    private int duration;
    private long expireTime;
    private long handleTime;
    private int type;

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getHandleTime() {
        return this.handleTime;
    }

    public void setHandleTime(long handleTime) {
        this.handleTime = handleTime;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getExpireTime() {
        return this.expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    @Override
    public String toString() {
        return "ReportPushData{type=" + this.type + ", handleTime=" + this.handleTime + ", duration=" + this.duration + ", expireTime=" + this.expireTime + '}';
    }
}
