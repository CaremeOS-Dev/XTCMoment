package com.xtc.moment.net.bean;

import java.util.List;

/**
 * 官方评论查询请求体。
 */
public class OfficialCommentRequestBean {

    private List<String> advertIdList;
    private String lookUpId;

    public String getLookUpId() {
        return this.lookUpId;
    }

    public void setLookUpId(String lookUpId) {
        this.lookUpId = lookUpId;
    }

    public List<String> getAdvertIdList() {
        return this.advertIdList;
    }

    public void setAdvertIdList(List<String> advertIdList) {
        this.advertIdList = advertIdList;
    }

    @Override
    public String toString() {
        return "OfficialCommentRequestBean{lookUpId='" + this.lookUpId + "', advertIdList=" + this.advertIdList + '}';
    }
}