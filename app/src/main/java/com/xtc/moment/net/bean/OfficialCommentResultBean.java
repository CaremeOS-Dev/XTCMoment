package com.xtc.moment.net.bean;

import java.util.List;

/** A page of official (advert) comments. */
public class OfficialCommentResultBean {
    private String advertId;
    private List<CommentBean> list;
    private int totalCount;

    public String getAdvertId() {
        return this.advertId;
    }

    public void setAdvertId(String advertId) {
        this.advertId = advertId;
    }

    public List<CommentBean> getList() {
        return this.list;
    }

    public void setList(List<CommentBean> list) {
        this.list = list;
    }

    public int getTotalCount() {
        return this.totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public String toString() {
        return "OfficialCommentResultBean{advertId='" + this.advertId + "', totalCount=" + this.totalCount + ", list=" + this.list + '}';
    }
}
