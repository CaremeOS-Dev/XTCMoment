package com.xtc.moment.net.bean;

/** The body sent when posting a comment. */
public class MomentCommentBean {
    private String comment;
    private String momentId;
    private String momentWatchId;
    private String replyId;
    private String watchId;

    public String getMomentWatchId() {
        return this.momentWatchId;
    }

    public void setMomentWatchId(String momentWatchId) {
        this.momentWatchId = momentWatchId;
    }

    public String getMomentId() {
        return this.momentId;
    }

    public void setMomentId(String momentId) {
        this.momentId = momentId;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public String getReplyId() {
        return this.replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    @Override
    public String toString() {
        return "MomentCommentBean{momentWatchId='" + this.momentWatchId + "', momentId='" + this.momentId + "', comment='" + this.comment + "', watchId='" + this.watchId + "', replyId='" + this.replyId + "'}";
    }
}
