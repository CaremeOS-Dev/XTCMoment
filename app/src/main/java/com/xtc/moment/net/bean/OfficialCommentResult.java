package com.xtc.moment.net.bean;

import java.util.List;

/** Envelope holding a list of advert comment pages. */
public class OfficialCommentResult {
    private List<OfficialCommentResultBean> advertVoList;

    public List<OfficialCommentResultBean> getAdvertVoList() {
        return this.advertVoList;
    }

    public void setAdvertVoList(List<OfficialCommentResultBean> advertVoList) {
        this.advertVoList = advertVoList;
    }

    @Override
    public String toString() {
        return "OfficialCommentResult{advertVoList=" + this.advertVoList + '}';
    }
}
