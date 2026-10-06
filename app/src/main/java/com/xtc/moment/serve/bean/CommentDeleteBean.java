package com.xtc.moment.serve.bean;

/**
 * 评论删除事件载荷。
 */
public class CommentDeleteBean {

    private String watchId;
    private String watchName;
    private String momentId;
    private String parentId;
    private String commentId;
    private String advertId;

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

    public String getMomentId() {
        return this.momentId;
    }

    public void setMomentId(String momentId) {
        this.momentId = momentId;
    }

    public String getParentId() {
        return this.parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getCommentId() {
        return this.commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getAdvertId() {
        return this.advertId;
    }

    public void setAdvertId(String advertId) {
        this.advertId = advertId;
    }

    @Override
    public String toString() {
        return "CommentDeleteBean{watchId='" + this.watchId + "', watchName='" + this.watchName + "', momentId='"
                + this.momentId + "', parentId='" + this.parentId + "', commentId='" + this.commentId
                + "', advertId='" + this.advertId + "'}";
    }
}