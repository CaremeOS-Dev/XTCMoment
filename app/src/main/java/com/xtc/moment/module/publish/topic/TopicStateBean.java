package com.xtc.moment.module.publish.topic;

import com.xtc.moment.net.bean.TopicListBean;

/**
 * 话题列表项的展示状态：话题数据 + 是否已被用户选中。
 */
public class TopicStateBean {

    private TopicListBean topicListBean;
    private boolean selected;

    public TopicStateBean(TopicListBean topicListBean, boolean selected) {
        this.topicListBean = topicListBean;
        this.selected = selected;
    }

    public TopicListBean getTopicListBean() {
        return this.topicListBean;
    }

    public void setTopicListBean(TopicListBean topicListBean) {
        this.topicListBean = topicListBean;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "TopicStateBean{topicListBean=" + this.topicListBean + ", selected=" + this.selected + '}';
    }
}
