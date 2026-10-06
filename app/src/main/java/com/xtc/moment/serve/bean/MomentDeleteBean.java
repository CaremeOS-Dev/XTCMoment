package com.xtc.moment.serve.bean;

/**
 * 动态删除事件载荷。
 */
public class MomentDeleteBean {

    public static final int TYPE_FRIEND = 0;
    public static final int TYPE_SELF = 1;

    private String watchId;
    private String watchName;
    private String momentId;
    private String parentId;
    private int self;

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

    public String getWatchName() {
        return this.watchName;
    }

    public void setWatchName(String watchName) {
        this.watchName = watchName;
    }

    public String getParentId() {
        return this.parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public int getSelf() {
        return this.self;
    }

    public void setSelf(int self) {
        this.self = self;
    }

    @Override
    public String toString() {
        return "MomentDeleteBean{watchId='" + this.watchId + "', watchName='" + this.watchName + "', momentId='"
                + this.momentId + "', parentId='" + this.parentId + "', self=" + this.self + '}';
    }
}