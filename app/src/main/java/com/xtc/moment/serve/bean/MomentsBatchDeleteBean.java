package com.xtc.moment.serve.bean;

import java.util.List;

/**
 * 批量删除动态的事件载荷。
 */
public class MomentsBatchDeleteBean {

    List<String> watchIds;
    private int self;
    private String timePoint;

    public List<String> getWatchIds() {
        return this.watchIds;
    }

    public void setWatchIds(List<String> watchIds) {
        this.watchIds = watchIds;
    }

    public int getSelf() {
        return this.self;
    }

    public void setSelf(int self) {
        this.self = self;
    }

    public String getTimePoint() {
        return this.timePoint;
    }

    public void setTimePoint(String timePoint) {
        this.timePoint = timePoint;
    }

    @Override
    public String toString() {
        return "MomentsBatchDeleteBean{watchIds=" + this.watchIds + ", self=" + this.self + ", timePoint='"
                + this.timePoint + "'}";
    }
}