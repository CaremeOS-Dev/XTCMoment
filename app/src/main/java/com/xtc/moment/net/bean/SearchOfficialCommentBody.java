package com.xtc.moment.net.bean;

/** Paged request body for advert comments. */
public class SearchOfficialCommentBody {
    private String advertId;
    private String lookUpId;
    private int pageNum;
    private int pageSize;

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setLookUpId(String lookUpId) {
        this.lookUpId = lookUpId;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public void setAdvertId(String advertId) {
        this.advertId = advertId;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public String getLookUpId() {
        return this.lookUpId;
    }

    public int getPageNum() {
        return this.pageNum;
    }

    public String getAdvertId() {
        return this.advertId;
    }
}
