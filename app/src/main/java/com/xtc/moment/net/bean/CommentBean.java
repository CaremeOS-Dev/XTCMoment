package com.xtc.moment.net.bean;

/** A comment payload as returned by the server. */
public class CommentBean {
    private String advertId;
    private String comment;
    private String commentId;
    private long createTime;
    private String momentId;
    private String parentWatchId;
    private String replyCommentId;
    private String replyId;
    private String replyName;
    private String replyWatchId;
    private String replyWatchName;
    private String watchId;
    private String watchName;

    public String getCommentId() {
        return this.commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
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

    public String getWatchName() {
        return this.watchName;
    }

    public void setWatchName(String watchName) {
        this.watchName = watchName;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReplyId() {
        return this.replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public String getReplyName() {
        return this.replyName;
    }

    public void setReplyName(String replyName) {
        this.replyName = replyName;
    }

    public String getReplyWatchId() {
        return this.replyWatchId;
    }

    public void setReplyWatchId(String replyWatchId) {
        this.replyWatchId = replyWatchId;
    }

    public String getReplyWatchName() {
        return this.replyWatchName;
    }

    public void setReplyWatchName(String replyWatchName) {
        this.replyWatchName = replyWatchName;
    }

    public String getReplyCommentId() {
        return this.replyCommentId;
    }

    public void setReplyCommentId(String replyCommentId) {
        this.replyCommentId = replyCommentId;
    }

    public String getAdvertId() {
        return this.advertId;
    }

    public void setAdvertId(String advertId) {
        this.advertId = advertId;
    }

    public String getParentWatchId() {
        return this.parentWatchId;
    }

    public void setParentWatchId(String parentWatchId) {
        this.parentWatchId = parentWatchId;
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "CommentBean{commentId='" + this.commentId + "', momentId='" + this.momentId + "', watchId='" + this.watchId + "', watchName='" + this.watchName + "', comment='" + this.comment + "', replyId='" + this.replyId + "', parentWatchId='" + this.parentWatchId + "', replyName='" + this.replyName + "', replyWatchId='" + this.replyWatchId + "', replyWatchName='" + this.replyWatchName + "', replyCommentId='" + this.replyCommentId + "', advertId='" + this.advertId + "', createTime=" + this.createTime + '}';
    }
}
