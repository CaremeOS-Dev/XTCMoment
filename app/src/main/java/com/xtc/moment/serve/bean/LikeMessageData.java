package com.xtc.moment.serve.bean;

/**
 * 点赞消息数据。
 */
public class LikeMessageData {

    private String watchId;
    private String watchName;
    private String momentId;
    private Long createTime;
    private boolean checked;

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

    public Long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public boolean isChecked() {
        return this.checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return "LikeMessageData{watchId='" + this.watchId + "', watchName='" + this.watchName + "', momentId='"
                + this.momentId + "', createTime=" + this.createTime + ", checked=" + this.checked + '}';
    }
}