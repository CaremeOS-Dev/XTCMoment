package com.xtc.moment.module.prerogative.bean;

/**
 * 特权表情/资源项。
 */
public class EmotionsEntity {

    private int prerogativeEmotion;
    private Long expireTime;
    private String prerogatives;
    private int prerogativeBag;
    private String createTime;
    private String watchId;
    private int prerogativeId;
    private int id;
    private int status;
    private Long useTime;
    private Long expireMins;

    public void setPrerogativeEmotion(int prerogativeEmotion) {
        this.prerogativeEmotion = prerogativeEmotion;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public void setPrerogatives(String prerogatives) {
        this.prerogatives = prerogatives;
    }

    public void setPrerogativeBag(int prerogativeBag) {
        this.prerogativeBag = prerogativeBag;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public void setPrerogativeId(int prerogativeId) {
        this.prerogativeId = prerogativeId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPrerogativeEmotion() {
        return this.prerogativeEmotion;
    }

    public Long getExpireTime() {
        return this.expireTime;
    }

    public String getPrerogatives() {
        return this.prerogatives;
    }

    public int getPrerogativeBag() {
        return this.prerogativeBag;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public int getPrerogativeId() {
        return this.prerogativeId;
    }

    public int getId() {
        return this.id;
    }

    public int getStatus() {
        return this.status;
    }

    public Long getUseTime() {
        return this.useTime;
    }

    public void setUseTime(Long useTime) {
        this.useTime = useTime;
    }

    public Long getExpireMins() {
        return this.expireMins;
    }

    public void setExpireMins(Long expireMins) {
        this.expireMins = expireMins;
    }

    public boolean isMomentLikeEquity() {
        return getPrerogativeEmotion() == 2;
    }

    public boolean isMomentBgEquity() {
        return getPrerogativeEmotion() == 3;
    }

    @Override
    public String toString() {
        return "EmotionsEntity{prerogativeEmotion=" + this.prerogativeEmotion + ", expireTime='" + this.expireTime
                + "', prerogatives='" + this.prerogatives + "', prerogativeBag=" + this.prerogativeBag
                + ", createTime='" + this.createTime + "', watchId='" + this.watchId + "', prerogativeId="
                + this.prerogativeId + ", id=" + this.id + ", status=" + this.status + ", useTime=" + this.useTime
                + ", expireMins=" + this.expireMins + '}';
    }
}