package com.xtc.moment.module.illegal.net.bean.request;

/**
 * 禁用发送记录上报请求体。
 */
public class ReportDisableBean {

    private String watchId;
    private long startTime;
    private long expireTime;
    private int type;
    private int times;

    public ReportDisableBean(String watchId, long startTime, long expireTime, int type, int times) {
        this.watchId = watchId;
        this.startTime = startTime;
        this.expireTime = expireTime;
        this.type = type;
        this.times = times;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getExpireTime() {
        return this.expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public int getStatus() {
        return this.type;
    }

    public void setStatus(int type) {
        this.type = type;
    }

    public int getTimes() {
        return this.times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    @Override
    public String toString() {
        return "ReportIllegalBean{watchId='" + this.watchId + "', startTime='" + this.startTime + "', expireTime='"
                + this.expireTime + "', status=" + this.type + ", times=" + this.times + '}';
    }
}