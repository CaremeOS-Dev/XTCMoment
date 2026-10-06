package com.xtc.aitext.bean;

/**
 * 创建 AI 文案的响应。
 */
public class CreatBackBean {

    private int remainTimes;
    private Integer id = 0;
    private int callResult;
    private long waitTime;

    public int getRemainTimes() {
        return remainTimes;
    }

    public void setRemainTimes(int remainTimes) {
        this.remainTimes = remainTimes;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getCallResult() {
        return callResult;
    }

    public void setCallResult(int callResult) {
        this.callResult = callResult;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }

    @Override
    public String toString() {
        return "CreatBackBean{remainTimes=" + remainTimes + ", id=" + id + ", callResult=" + callResult
                + ", waitTime=" + waitTime + '}';
    }
}