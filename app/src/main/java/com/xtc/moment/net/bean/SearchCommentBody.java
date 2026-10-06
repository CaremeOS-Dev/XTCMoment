package com.xtc.moment.net.bean;

/** Paged request body for a moment's comments. */
public class SearchCommentBody {
    private String momentId;
    private String momentWatchId;
    private int pageNum;
    private int pageSize;
    private String watchId;

    public void setMomentWatchId(String momentWatchId) {
        this.momentWatchId = momentWatchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setMomentId(String momentId) {
        this.momentId = momentId;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public String getMomentWatchId() {
        return this.momentWatchId;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public String getMomentId() {
        return this.momentId;
    }

    public int getPageNum() {
        return this.pageNum;
    }
}
