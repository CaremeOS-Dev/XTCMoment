package com.xtc.virtualselfapi.bean.net.resp;

/**
 * 危险状态响应。
 */
public class RespDanger {

    private int dangerId;
    private long duration;
    private long startTime;

    public int getDangerId() {
        return this.dangerId;
    }

    public void setDangerId(int dangerId) {
        this.dangerId = dangerId;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "RespDanger{dangerId=" + this.dangerId + ", startTime=" + this.startTime + ", duration=" + this.duration + '}';
    }
}