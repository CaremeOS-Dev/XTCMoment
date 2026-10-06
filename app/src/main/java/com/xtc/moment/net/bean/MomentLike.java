package com.xtc.moment.net.bean;

import java.util.Date;

/** A like record as returned by the server. */
public class MomentLike {
    private Date createTime;
    private Integer emotionId;
    private Long id;
    private String momentId;
    private String momentWatchId;
    private String watchId;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMomentWatchId() {
        return this.momentWatchId;
    }

    public void setMomentWatchId(String momentWatchId) {
        this.momentWatchId = momentWatchId == null ? null : momentWatchId.trim();
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
        this.watchId = watchId == null ? null : watchId.trim();
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getEmotionId() {
        return this.emotionId.intValue();
    }

    public void setEmotionId(int emotionId) {
        this.emotionId = Integer.valueOf(emotionId);
    }

    @Override
    public String toString() {
        return "MomentLike{id=" + this.id + ", momentWatchId='" + this.momentWatchId + "', momentId='" + this.momentId + "', watchId='" + this.watchId + "', createTime=" + this.createTime + ", emotionId=" + this.emotionId + '}';
    }
}
