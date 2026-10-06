package com.xtc.aitext.bean;

/**
 * AI 文案风格。
 */
public class AIStyleTextBean {

    private String defaultTextTips;
    private String aiStyleUrl;
    private String aiStyleName;
    private Integer id;
    private int sort;

    public String getDefaultTextTips() {
        return defaultTextTips;
    }

    public void setDefaultTextTips(String defaultTextTips) {
        this.defaultTextTips = defaultTextTips;
    }

    public String getAiStyleUrl() {
        return aiStyleUrl;
    }

    public void setAiStyleUrl(String aiStyleUrl) {
        this.aiStyleUrl = aiStyleUrl;
    }

    public String getAiStyleName() {
        return aiStyleName;
    }

    public void setAiStyleName(String aiStyleName) {
        this.aiStyleName = aiStyleName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "AIStyleTextBean{defaultTextTips='" + defaultTextTips + "', aiStyleUrl='" + aiStyleUrl
                + "', aiStyleName='" + aiStyleName + "', id=" + id + ", sort=" + sort + '}';
    }
}