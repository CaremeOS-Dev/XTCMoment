package com.xtc.moment.module.bean;

/**
 * 动态消息（评论/点赞）的推送载体，泛型 T 承载原始业务数据。
 */
public class MomentNewMsgBean<T> {

    public static final int TYPE_PRAISE = 1;
    public static final int TYPE_COMMENT = 2;

    private String momentId;
    private int type;
    private int mediaType;
    private String commentWatchId;
    private String commentName;
    private String content;
    private String resource;
    private Integer resourceId;
    private Long createTime;
    private int emotionId;
    private T bean;

    public String getMomentId() {
        return this.momentId;
    }

    public void setMomentId(String momentId) {
        this.momentId = momentId;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMediaType() {
        return this.mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public String getCommentWatchId() {
        return this.commentWatchId;
    }

    public void setCommentWatchId(String commentWatchId) {
        this.commentWatchId = commentWatchId;
    }

    public String getCommentName() {
        return this.commentName;
    }

    public void setCommentName(String commentName) {
        this.commentName = commentName;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getResource() {
        return this.resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public Integer getResourceId() {
        return this.resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public Long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public T getBean() {
        return this.bean;
    }

    public void setBean(T bean) {
        this.bean = bean;
    }

    public int getEmotionId() {
        return this.emotionId;
    }

    public void setEmotionId(int emotionId) {
        this.emotionId = emotionId;
    }

    @Override
    public String toString() {
        return "MomentNewMsgBean{momentId='" + this.momentId + "', type=" + this.type + ", mediaType=" + this.mediaType
                + ", commentWatchId='" + this.commentWatchId + "', commentName='" + this.commentName + "', content='"
                + this.content + "', resource='" + this.resource + "', resourceId=" + this.resourceId + ", createTime="
                + this.createTime + ", emotionId=" + this.emotionId + ", bean=" + this.bean + '}';
    }
}