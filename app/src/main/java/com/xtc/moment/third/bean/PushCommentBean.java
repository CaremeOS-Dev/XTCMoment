package com.xtc.moment.third.bean;

/**
 * 评论推送数据。
 */
public class PushCommentBean {

    public static final int STATUS_CANCEL = 0;
    public static final int STATUS_SURE = 1;

    private String momentId;
    private String commentId;
    private String watchId;
    private String replyId;
    private int status;
    private String content;

    public PushCommentBean() {
    }

    public PushCommentBean(String watchId, String commentId) {
        this.watchId = watchId;
        this.commentId = commentId;
    }

    public PushCommentBean(String watchId, String momentId, String replyId, int status) {
        this.watchId = watchId;
        this.momentId = momentId;
        this.replyId = replyId;
        this.status = status;
    }

    public PushCommentBean(String watchId, String momentId, String replyId, int status, String content) {
        this.momentId = momentId;
        this.watchId = watchId;
        this.replyId = replyId;
        this.status = status;
        this.content = content;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
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

    public String getReplyId() {
        return this.replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "PushCommentBean{momentId='" + this.momentId + "', commentId='" + this.commentId + "', watchId='"
                + this.watchId + "', replyId='" + this.replyId + "', status=" + this.status + ", content='" + this.content
                + "'}";
    }
}