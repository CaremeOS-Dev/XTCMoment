package com.xtc.moment.net.bean;

/** Body sent when commenting on an advert moment. */
public class MomentCommentOfficialBean {
    private String advertId;
    private String comment;
    private String parentWatchId;
    private String replyCommentId;
    private String replyWatchId;
    private String watchId;

    public String getParentWatchId() {
        return this.parentWatchId;
    }

    public void setParentWatchId(String parentWatchId) {
        this.parentWatchId = parentWatchId;
    }

    public String getAdvertId() {
        return this.advertId;
    }

    public void setAdvertId(String advertId) {
        this.advertId = advertId;
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

    public String getReplyCommentId() {
        return this.replyCommentId;
    }

    public void setReplyCommentId(String replyCommentId) {
        this.replyCommentId = replyCommentId;
    }

    public String getReplyWatchId() {
        return this.replyWatchId;
    }

    public void setReplyWatchId(String replyWatchId) {
        this.replyWatchId = replyWatchId;
    }

    @Override
    public String toString() {
        return "MomentCommentOfficialBean{parentWatchId='" + this.parentWatchId + "', advertId='" + this.advertId + "', comment='" + this.comment + "', watchId='" + this.watchId + "', replyCommentId='" + this.replyCommentId + "', replyWatchId='" + this.replyWatchId + "'}";
    }
}
