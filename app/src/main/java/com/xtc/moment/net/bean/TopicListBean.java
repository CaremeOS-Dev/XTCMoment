package com.xtc.moment.net.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 一个「小括号」（话题）条目。
 *
 * <p>服务端 {@code /moment-service/topic/recommend/list} 返回的推荐话题，
 * 以及发布动态时随 {@code topicList} 一起上报。同时作为 Activity 间传递的
 * Parcelable 载荷，因此字段顺序需与写入顺序保持一致。</p>
 */
public class TopicListBean implements Parcelable {

    public static final Creator<TopicListBean> CREATOR = new Creator<TopicListBean>() {
        @Override
        public TopicListBean createFromParcel(Parcel source) {
            return new TopicListBean(source);
        }

        @Override
        public TopicListBean[] newArray(int size) {
            return new TopicListBean[size];
        }
    };

    /** 话题展示颜色，形如 {@code "#FFFFAA22"}。 */
    @SerializedName("color")
    private String color;

    /** 话题跳转链接（点击话题时打开）。 */
    @SerializedName("jumpUrl")
    private String jumpUrl;

    /** 排序权重，越大越靠前。 */
    @SerializedName("sortValue")
    private int sortValue;

    /** 话题名（不含括号，由展示层补上「（）」）。 */
    @SerializedName("topicName")
    private String topicName;

    /** 话题类型。 */
    @SerializedName("topicType")
    private int topicType;

    public TopicListBean() {
    }

    protected TopicListBean(Parcel source) {
        this.topicName = source.readString();
        this.color = source.readString();
        this.jumpUrl = source.readString();
        this.sortValue = source.readInt();
        this.topicType = source.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.topicName);
        dest.writeString(this.color);
        dest.writeString(this.jumpUrl);
        dest.writeInt(this.sortValue);
        dest.writeInt(this.topicType);
    }

    public String getTopicName() {
        return this.topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getJumpUrl() {
        return this.jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    public int getSortValue() {
        return this.sortValue;
    }

    public void setSortValue(int sortValue) {
        this.sortValue = sortValue;
    }

    public int getTopicType() {
        return this.topicType;
    }

    public void setTopicType(int topicType) {
        this.topicType = topicType;
    }

    @Override
    public String toString() {
        return "TopicListBean{topicName='" + this.topicName + "', color='" + this.color
                + "', jumpUrl='" + this.jumpUrl + "', sortValue=" + this.sortValue
                + ", topicType=" + this.topicType + '}';
    }
}
