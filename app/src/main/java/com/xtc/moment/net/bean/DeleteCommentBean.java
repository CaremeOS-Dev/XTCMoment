package com.xtc.moment.net.bean;

/** Request body for deleting a comment. */
public class DeleteCommentBean {
    private String commentId;
    private String momentId;
    private String momentWatchId;
    private String watchId;

    public String getMomentId() {
        return this.momentId;
    }

    public void setMomentId(String momentId) {
        this.momentId = momentId;
    }

    public String getMomentWatchId() {
        return this.momentWatchId;
    }

    public void setMomentWatchId(String momentWatchId) {
        this.momentWatchId = momentWatchId;
    }

    public String getCommentId() {
        return this.commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    @Override
    public String toString() {
        return "DeleteCommentBean{momentId='" + this.momentId + "', momentWatchId='" + this.momentWatchId + "', commentId='" + this.commentId + "', watchId='" + this.watchId + "'}";
    }
}
