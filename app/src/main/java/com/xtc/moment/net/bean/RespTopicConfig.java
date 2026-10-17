package com.xtc.moment.net.bean;

import java.util.List;

/**
 * 「小括号」（话题）推荐列表接口的响应体。
 *
 * <p>对应 {@code GET /moment-service/topic/recommend/list}。</p>
 */
public class RespTopicConfig {

    /** 话题功能配置（数量上限、名称长度、默认颜色等）。 */
    private TopicConfigBean config;

    /** 推荐话题列表。 */
    private List<TopicListBean> topicList;

    public TopicConfigBean getConfig() {
        return this.config;
    }

    public void setConfig(TopicConfigBean config) {
        this.config = config;
    }

    public List<TopicListBean> getTopicList() {
        return this.topicList;
    }

    public void setTopicList(List<TopicListBean> topicList) {
        this.topicList = topicList;
    }

    @Override
    public String toString() {
        return "RespTopicConfig{config=" + this.config + ", topicList=" + this.topicList + '}';
    }
}
