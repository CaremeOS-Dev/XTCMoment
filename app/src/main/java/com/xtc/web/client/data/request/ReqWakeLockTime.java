package com.xtc.web.client.data.request;

/** 申请 WakeLock 的时长请求。 */
public class ReqWakeLockTime {

    private long time;

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "ReqWakeLockTime{time=" + this.time + '}';
    }
}