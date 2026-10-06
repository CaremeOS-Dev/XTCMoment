package com.xtc.moment.module.illegal.net.bean.request;

/**
 * 延迟发送次数上报请求体。
 */
public class ReportIllegalBean {

    private String watchId;
    private long expireTime;
    private int count;

    public ReportIllegalBean(String watchId, long expireTime, int count) {
        this.watchId = watchId;
        this.expireTime = expireTime;
        this.count = count;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public long getExpireTime() {
        return this.expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}