package com.xtc.moment.net.bean;

/**
 * 「小括号」（话题）功能的服务端配置。
 *
 * <p>随 {@link RespTopicConfig#getConfig()} 下发，用于约束可添加的话题数量、
 * 名称长度以及官方/用户话题的默认展示色。</p>
 */
public class TopicConfigBean {

    /** 一条动态最多可携带的话题数量。 */
    private int maxTopicCount;

    /** 官方话题默认文字颜色。 */
    private String officialColor;

    /** 话题名允许的最大长度。 */
    private int topicNameMaxLength;

    /** 用户自定义话题默认文字颜色。 */
    private String userColor;

    public int getMaxTopicCount() {
        return this.maxTopicCount;
    }

    public void setMaxTopicCount(int maxTopicCount) {
        this.maxTopicCount = maxTopicCount;
    }

    public int getTopicNameMaxLength() {
        return this.topicNameMaxLength;
    }

    public void setTopicNameMaxLength(int topicNameMaxLength) {
        this.topicNameMaxLength = topicNameMaxLength;
    }

    public String getOfficialColor() {
        return this.officialColor;
    }

    public void setOfficialColor(String officialColor) {
        this.officialColor = officialColor;
    }

    public String getUserColor() {
        return this.userColor;
    }

    public void setUserColor(String userColor) {
        this.userColor = userColor;
    }

    @Override
    public String toString() {
        return "TopicConfigBean{maxTopicCount=" + this.maxTopicCount + ", topicNameMaxLength="
                + this.topicNameMaxLength + ", officialColor='" + this.officialColor
                + "', userColor='" + this.userColor + "'}";
    }
}
