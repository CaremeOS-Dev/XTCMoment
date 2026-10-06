package com.xtc.moment.module.illegal.config;

/**
 * 违规处置配置。
 */
public class IllegalSanctionConfigBean {

    private int allowCount = 3;
    private int delaySendTime = 30;
    private int delaySendDeadLine = 72;
    private int illegalRecordDeadLine = 24;
    private int disableSendDeadLine = 72;
    private int pullIllegalCount = 4;

    public void setAllowCount(int allowCount) {
        this.allowCount = allowCount;
    }

    public void setDelaySendTime(int delaySendTime) {
        this.delaySendTime = delaySendTime;
    }

    public void setDelaySendDeadLine(int delaySendDeadLine) {
        this.delaySendDeadLine = delaySendDeadLine;
    }

    public void setIllegalRecordDeadLine(int illegalRecordDeadLine) {
        this.illegalRecordDeadLine = illegalRecordDeadLine;
    }

    public void setDisableSendDeadLine(int disableSendDeadLine) {
        this.disableSendDeadLine = disableSendDeadLine;
    }

    public void setPullIllegalCount(int pullIllegalCount) {
        this.pullIllegalCount = pullIllegalCount;
    }

    public int getAllowCount() {
        return this.allowCount;
    }

    public int getDelaySendTime() {
        return this.delaySendTime;
    }

    public int getDelaySendDeadLine() {
        return this.delaySendDeadLine * 60;
    }

    public int getIllegalRecordDeadLine() {
        return this.illegalRecordDeadLine * 60;
    }

    public int getDisableSendDeadLine() {
        return this.disableSendDeadLine * 60;
    }

    public int getPullIllegalCount() {
        return this.pullIllegalCount;
    }
}