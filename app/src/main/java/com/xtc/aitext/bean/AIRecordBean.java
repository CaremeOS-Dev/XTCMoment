package com.xtc.aitext.bean;

import java.util.List;

/**
 * AI 文案创作记录。
 */
public class AIRecordBean {

    private List<AIRecordDetailBean> userAiText;
    private AIPageBean page;

    public void setPage(AIPageBean page) {
        this.page = page;
    }

    public AIPageBean getPage() {
        return page;
    }

    public void setUserAiText(List<AIRecordDetailBean> userAiText) {
        this.userAiText = userAiText;
    }

    public List<AIRecordDetailBean> getUserAiText() {
        return userAiText;
    }

    @Override
    public String toString() {
        return "AIRecordBean{userAiText=" + userAiText + ", page=" + page + '}';
    }
}