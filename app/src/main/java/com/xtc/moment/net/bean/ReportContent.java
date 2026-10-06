package com.xtc.moment.net.bean;

/** A single report reason the user submitted. */
public class ReportContent {
    private String content;
    private int type;
    private String watchId;

    public ReportContent(String content, int type, String watchId) {
        this.content = content;
        this.type = type;
        this.watchId = watchId;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    @Override
    public String toString() {
        return "ReportContent{content='" + this.content + "', type=" + this.type + ", watchId='" + this.watchId + "'}";
    }
}
