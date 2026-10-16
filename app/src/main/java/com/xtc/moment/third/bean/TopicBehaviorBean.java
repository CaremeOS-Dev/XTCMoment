package com.xtc.moment.third.bean;

/**
 * 「小括号」（话题）相关行为的埋点载荷。
 */
public class TopicBehaviorBean {

    private int topicLength;
    private String topicName;
    private int type;

    public TopicBehaviorBean(int type, String topicName, int topicLength) {
        this.type = type;
        this.topicName = topicName;
        this.topicLength = topicLength;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTopicName() {
        return this.topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public int getTopicLength() {
        return this.topicLength;
    }

    public void setTopicLength(int topicLength) {
        this.topicLength = topicLength;
    }

    @Override
    public String toString() {
        return "TopicBehaviorBean{type=" + this.type + ", topicName='" + this.topicName
                + "', topicLength=" + this.topicLength + '}';
    }
}
