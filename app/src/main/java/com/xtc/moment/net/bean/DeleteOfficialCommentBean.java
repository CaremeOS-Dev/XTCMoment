package com.xtc.moment.net.bean;

/** Request body for deleting an advert comment. */
public class DeleteOfficialCommentBean {
    private String advertId;
    private String commentId;
    private String parentWatchId;
    private String watchId;

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public void setParentWatchId(String parentWatchId) {
        this.parentWatchId = parentWatchId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public void setAdvertId(String advertId) {
        this.advertId = advertId;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public String getParentWatchId() {
        return this.parentWatchId;
    }

    public String getCommentId() {
        return this.commentId;
    }

    public String getAdvertId() {
        return this.advertId;
    }

    @Override
    public String toString() {
        return "DeleteOfficialCommentBean{watchId='" + this.watchId + "', parentWatchId='" + this.parentWatchId + "', commentId='" + this.commentId + "', advertId='" + this.advertId + "'}";
    }
}
