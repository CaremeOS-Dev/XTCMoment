package com.xtc.moment.push.bean;

/**
 * IM 推送消息体。
 */
public class ImMessage {

    private String content;
    private Integer type;
    private String watchId;
    private Long timestamp;

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ImMessage{content='" + this.content + "', type=" + this.type + ", watchId='" + this.watchId
                + "', timestamp=" + this.timestamp + '}';
    }
}