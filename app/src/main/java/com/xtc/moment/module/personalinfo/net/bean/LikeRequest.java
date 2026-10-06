package com.xtc.moment.module.personalinfo.net.bean;

/**
 * 点赞上报请求。
 */
public class LikeRequest {

    private int count;
    private String watchId;
    private String likeWatchId;

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public String getLikeWatchId() {
        return this.likeWatchId;
    }

    public void setLikeWatchId(String likeWatchId) {
        this.likeWatchId = likeWatchId;
    }
}