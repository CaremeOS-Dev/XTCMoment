package com.xtc.aitext.bean;

import java.util.List;

/**
 * AI 文案主页数据。
 */
public class AIDataBean {

    private int aiTextLength;
    private List<AIStyleTextBean> aiStyleTextVos;
    private List<UserAccessBean> userAccessVoList;
    private int aiTextCount;

    public int getAiTextLength() {
        return aiTextLength;
    }

    public void setAiTextLength(int aiTextLength) {
        this.aiTextLength = aiTextLength;
    }

    public List<AIStyleTextBean> getAiStyleTextVos() {
        return aiStyleTextVos;
    }

    public void setAiStyleTextVos(List<AIStyleTextBean> aiStyleTextVos) {
        this.aiStyleTextVos = aiStyleTextVos;
    }

    public List<UserAccessBean> getUserAccessVoList() {
        return userAccessVoList;
    }

    public void setUserAccessVoList(List<UserAccessBean> userAccessVoList) {
        this.userAccessVoList = userAccessVoList;
    }

    public int getAiTextCount() {
        return aiTextCount;
    }

    public void setAiTextCount(int aiTextCount) {
        this.aiTextCount = aiTextCount;
    }

    @Override
    public String toString() {
        return "AIDataBean{aiTextLength=" + aiTextLength + ", aiStyleTextVos=" + aiStyleTextVos
                + ", userAccessVoList=" + userAccessVoList + ", aiTextCount=" + aiTextCount + '}';
    }
}