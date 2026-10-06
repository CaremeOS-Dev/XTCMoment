package com.xtc.moment.third.bean;

/**
 * 视频推送数据。
 */
public class PushVideoBean {

    public static final int CLICK_SEND_VIDEO = 1;
    public static final int SEND_SUCCESS_VIDEO = 2;
    public static final int SEND_SUCCESS_SHARE_VIDEO = 3;

    private String momentId;
    private String watchId;
    private String time;
    private String momentName;
    private String lookName;
    private String type;

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

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMomentName() {
        return this.momentName;
    }

    public void setMomentName(String momentName) {
        this.momentName = momentName;
    }

    public String getLookName() {
        return this.lookName;
    }

    public void setLookName(String lookName) {
        this.lookName = lookName;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}