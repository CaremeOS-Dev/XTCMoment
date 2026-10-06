package com.xtc.moment.net.bean;

import com.xtc.moment.module.personalinfo.net.bean.BaseRequestBean;

/** Request for one chapter of a community conversation. */
public class CommunityConversationReq extends BaseRequestBean {
    private int chapterIndex;

    public CommunityConversationReq(String watchId) {
        super(watchId);
    }

    public int getChapterIndex() {
        return this.chapterIndex;
    }

    public void setChapterIndex(int chapterIndex) {
        this.chapterIndex = chapterIndex;
    }
}
