package com.xtc.web.core.data.req;

/** 震动请求：等待时间与震动时长。 */
public class ReqVibrator {

    private long runTime;
    private long waitTime;

    public long getWaitTime() {
        return this.waitTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }

    public long getRunTime() {
        return this.runTime;
    }

    public void setRunTime(long runTime) {
        this.runTime = runTime;
    }

    @Override
    public String toString() {
        return "ReqVibrator{waitTime=" + this.waitTime + ", runTime=" + this.runTime + '}';
    }
}