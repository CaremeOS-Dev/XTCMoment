package com.xtc.moment.module.illegal.net.bean.request;

/**
 * 高风险内容查询请求体。
 */
public class HighRiskRequestBean {

    private String watchId;
    private int pageSize;
    private int pageNum;

    public HighRiskRequestBean(String watchId) {
        this.watchId = watchId;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNum() {
        return this.pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    @Override
    public String toString() {
        return "HighRiskRequestBean{watchId='" + this.watchId + "', pageSize=" + this.pageSize + ", pageNum="
                + this.pageNum + '}';
    }
}