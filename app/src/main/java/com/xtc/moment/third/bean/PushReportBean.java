package com.xtc.moment.third.bean;

/**
 * 举报推送数据。
 */
public class PushReportBean {

    private String momentId;
    private String watchId;
    private String content;

    public PushReportBean(String momentId, String watchId) {
        this.momentId = momentId;
        this.watchId = watchId;
    }

    public PushReportBean(String momentId, String watchId, String content) {
        this.momentId = momentId;
        this.watchId = watchId;
        this.content = content;
    }

    public String getMomentId() {
        return this.momentId;
    }

    public void setMomentId(String momentId) {
        this.momentId = momentId;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}