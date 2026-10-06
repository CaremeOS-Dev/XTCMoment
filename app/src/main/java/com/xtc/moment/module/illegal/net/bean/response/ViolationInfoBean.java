package com.xtc.moment.module.illegal.net.bean.response;

/**
 * 违规状态返回体。
 */
public class ViolationInfoBean {

    private String startTime;
    private String expireTime;
    private int delayCount;
    private int status;
    private int type;
    private int disableSendDeadLine;

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getExpireTime() {
        return this.expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public int getDelayCount() {
        return this.delayCount;
    }

    public void setDelayCount(int delayCount) {
        this.delayCount = delayCount;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDisableSendDeadLine() {
        return this.disableSendDeadLine;
    }

    public void setDisableSendDeadLine(int disableSendDeadLine) {
        this.disableSendDeadLine = disableSendDeadLine;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ViolationInfoBean{startTime='" + this.startTime + "', expireTime='" + this.expireTime
                + "', delayCount=" + this.delayCount + ", status=" + this.status + ", type=" + this.type
                + ", disableSendDeadLine=" + this.disableSendDeadLine + '}';
    }
}