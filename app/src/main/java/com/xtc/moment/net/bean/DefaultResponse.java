package com.xtc.moment.net.bean;

/** Generic server acknowledgement for a like/comment action. */
public class DefaultResponse {
    private long createTime;
    private int emotionId;
    private String momentId;
    private String momentWatchId;
    private String watchId;
    private String watchName;

    public String getWatchName() {
        return this.watchName;
    }

    public void setWatchName(String watchName) {
        this.watchName = watchName;
    }

    public String getMomentWatchId() {
        return this.momentWatchId;
    }

    public void setMomentWatchId(String momentWatchId) {
        this.momentWatchId = momentWatchId;
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

    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getEmotionId() {
        return this.emotionId;
    }

    public void setEmotionId(int emotionId) {
        this.emotionId = emotionId;
    }

    @Override
    public String toString() {
        return "DefaultResponse{momentWatchId='" + this.momentWatchId + "', momentId='" + this.momentId + "', watchId='" + this.watchId + "', createTime=" + this.createTime + ", watchName='" + this.watchName + "', emotionId='" + this.emotionId + "'}";
    }
}
