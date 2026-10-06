package com.xtc.moment.share.model.bean;

/**
 * 应用分享配置返回体。
 */
public class NetAppShare {

    private String packageName;
    private long deadline;
    private int maxTimes;
    private String appKey;
    private int allow;

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getDeadline() {
        return this.deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public int getMaxTimes() {
        return this.maxTimes;
    }

    public void setMaxTimes(int maxTimes) {
        this.maxTimes = maxTimes;
    }

    public String getAppKey() {
        return this.appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public int getAllow() {
        return this.allow;
    }

    public void setAllow(int allow) {
        this.allow = allow;
    }

    @Override
    public String toString() {
        return "NetAppShare{packageName='" + this.packageName + "', deadline=" + this.deadline + ", maxTimes="
                + this.maxTimes + ", appKey='" + this.appKey + "', allow=" + this.allow + '}';
    }
}