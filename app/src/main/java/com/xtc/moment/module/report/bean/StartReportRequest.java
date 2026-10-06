package com.xtc.moment.module.report.bean;

import java.util.ArrayList;

/**
 * 提交举报请求体。
 */
public class StartReportRequest {

    private String watchId;
    private String friendWatchId;
    private int informType;
    private long informTime;
    private String momentId;
    private String commentId;
    private int reportMomentType;
    private int informSource;
    private String momentWatchId;
    private ArrayList<Contents> contents;

    public ArrayList<Contents> getContents() {
        return this.contents;
    }

    public void setContents(ArrayList<Contents> contents) {
        this.contents = contents;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public String getFriendWatchId() {
        return this.friendWatchId;
    }

    public void setFriendWatchId(String friendWatchId) {
        this.friendWatchId = friendWatchId;
    }

    public int getInformType() {
        return this.informType;
    }

    public void setInformType(int informType) {
        this.informType = informType;
    }

    public long getInformTime() {
        return this.informTime;
    }

    public void setInformTime(long informTime) {
        this.informTime = informTime;
    }

    public String getMomentId() {
        return this.momentId;
    }

    public void setMomentId(String momentId) {
        this.momentId = momentId;
    }

    public String getCommentId() {
        return this.commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public int getReportMomentType() {
        return this.reportMomentType;
    }

    public void setReportMomentType(int reportMomentType) {
        this.reportMomentType = reportMomentType;
    }

    public int getInformSource() {
        return this.informSource;
    }

    public void setInformSource(int informSource) {
        this.informSource = informSource;
    }

    public String getMomentWatchId() {
        return this.momentWatchId;
    }

    public void setMomentWatchId(String momentWatchId) {
        this.momentWatchId = momentWatchId;
    }

    @Override
    public String toString() {
        return "StartReportRequest{watchId='" + this.watchId + "', friendWatchId='" + this.friendWatchId
                + "', informType=" + this.informType + ", informTime=" + this.informTime + ", momentId='" + this.momentId
                + "', commentId='" + this.commentId + "', reportMomentType=" + this.reportMomentType
                + ", informSource=" + this.informSource + ", momentWatchId" + this.momentWatchId + ", contents="
                + this.contents + '}';
    }

    /** 举报内容项。 */
    public static class Contents {

        private String content;
        private int type;
        private long messageDate;

        public long getMessageDate() {
            return this.messageDate;
        }

        public void setMessageDate(long messageDate) {
            this.messageDate = messageDate;
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

        @Override
        public String toString() {
            return "Contents{content='" + this.content + "', type=" + this.type + ", messageDate=" + this.messageDate
                    + '}';
        }
    }
}