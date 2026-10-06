package com.xtc.moment.module.illegal.config;

import java.util.ArrayList;
import java.util.List;

/**
 * 违规处罚相关的配置项：允许条数、延迟发送时间、各类截止时间等。
 */
public class IllegalSanctionConfig {

    private int allowCount;
    private long delaySendTime;
    private long delaySendDeadLine;
    private long illegalRecordDeadLine;
    private long disableSendDeadLine;
    private boolean isEnableReview;
    private int appealUrlRequestType;
    private int pullIllegalCount;
    private List<Integer> allowNotDelayMsgTypeList;

    public IllegalSanctionConfig(int allowCount, long delaySendTime, long delaySendDeadLine, long illegalRecordDeadLine,
            long disableSendDeadLine, boolean isEnableReview, int appealUrlRequestType, int pullIllegalCount,
            List<Integer> allowNotDelayMsgTypeList) {
        this.allowCount = allowCount;
        this.delaySendTime = delaySendTime;
        this.delaySendDeadLine = delaySendDeadLine;
        this.illegalRecordDeadLine = illegalRecordDeadLine;
        this.disableSendDeadLine = disableSendDeadLine;
        this.isEnableReview = isEnableReview;
        this.appealUrlRequestType = appealUrlRequestType;
        this.pullIllegalCount = pullIllegalCount;
        this.allowNotDelayMsgTypeList = allowNotDelayMsgTypeList;
    }

    public int getAllowCount() {
        return this.allowCount;
    }

    public void setAllowCount(int allowCount) {
        this.allowCount = allowCount;
    }

    public long getDelaySendTime() {
        return this.delaySendTime;
    }

    public void setDelaySendTime(long delaySendTime) {
        this.delaySendTime = delaySendTime;
    }

    public long getDelaySendDeadLine() {
        return this.delaySendDeadLine;
    }

    public void setDelaySendDeadLine(long delaySendDeadLine) {
        this.delaySendDeadLine = delaySendDeadLine;
    }

    public long getIllegalRecordDeadLine() {
        return this.illegalRecordDeadLine;
    }

    public void setIllegalRecordDeadLine(long illegalRecordDeadLine) {
        this.illegalRecordDeadLine = illegalRecordDeadLine;
    }

    public long getDisableSendDeadLine() {
        return this.disableSendDeadLine;
    }

    public void setDisableSendDeadLine(long disableSendDeadLine) {
        this.disableSendDeadLine = disableSendDeadLine;
    }

    public boolean isEnableReview() {
        return this.isEnableReview;
    }

    public void setEnableReview(boolean enableReview) {
        this.isEnableReview = enableReview;
    }

    public int getAppealUrlRequestType() {
        return this.appealUrlRequestType;
    }

    public void setAppealUrlRequestType(int appealUrlRequestType) {
        this.appealUrlRequestType = appealUrlRequestType;
    }

    public int getPullIllegalCount() {
        return this.pullIllegalCount;
    }

    public void setPullIllegalCount(int pullIllegalCount) {
        this.pullIllegalCount = pullIllegalCount;
    }

    public List<Integer> getAllowNotDelayMsgTypeList() {
        return this.allowNotDelayMsgTypeList;
    }

    @Override
    public String toString() {
        return "IllegalSanctionConfig{allowCount=" + this.allowCount + ", delaySendTime=" + this.delaySendTime
                + ", delaySendDeadLine=" + this.delaySendDeadLine + ", illegalRecordDeadLine="
                + this.illegalRecordDeadLine + ", disableSendDeadLine=" + this.disableSendDeadLine
                + ", isEnableReview=" + this.isEnableReview + ", appealUrlRequestType=" + this.appealUrlRequestType
                + ", pullIllegalCount=" + this.pullIllegalCount + '}';
    }

    public static class Builder {

        private int allowCount = 3;
        private long delaySendTime = 30;
        private long delaySendDeadLine = 1440;
        private long illegalRecordDeadLine = 1440;
        private long disableSendDeadLine = 1440;
        private boolean isEnableReview;
        private int appealUrlRequestType = 6;
        private int pullIllegalCount = 4;
        private List<Integer> allowNotDelayMsgTypeList = new ArrayList<>();

        public Builder setAllowCount(int allowCount) {
            this.allowCount = allowCount;
            return this;
        }

        public Builder setDelaySendTime(long delaySendTime) {
            this.delaySendTime = delaySendTime;
            return this;
        }

        public Builder setDelaySendDeadLine(long delaySendDeadLine) {
            this.delaySendDeadLine = delaySendDeadLine;
            return this;
        }

        public Builder setIllegalRecordDeadLine(long illegalRecordDeadLine) {
            this.illegalRecordDeadLine = illegalRecordDeadLine;
            return this;
        }

        public Builder setDisableSendDeadLine(long disableSendDeadLine) {
            this.disableSendDeadLine = disableSendDeadLine;
            return this;
        }

        public Builder setEnableReview(boolean enableReview) {
            this.isEnableReview = enableReview;
            return this;
        }

        public Builder setAppealUrlRequestType(int appealUrlRequestType) {
            this.appealUrlRequestType = appealUrlRequestType;
            return this;
        }

        public Builder setPullIllegalCount(int pullIllegalCount) {
            this.pullIllegalCount = pullIllegalCount;
            return this;
        }

        public Builder setAllowNotDelayMsgTypeList(List<Integer> allowNotDelayMsgTypeList) {
            this.allowNotDelayMsgTypeList = allowNotDelayMsgTypeList;
            return this;
        }

        public IllegalSanctionConfig build() {
            return new IllegalSanctionConfig(this.allowCount, this.delaySendTime, this.delaySendDeadLine,
                    this.illegalRecordDeadLine, this.disableSendDeadLine, this.isEnableReview,
                    this.appealUrlRequestType, this.pullIllegalCount, this.allowNotDelayMsgTypeList);
        }
    }
}