package com.xtc.im.core.app.bean;

import android.os.Parcel;
import android.os.Parcelable;

/** 单条 IM 消息内容。 */
public class ImMessage implements Parcelable {

    public static final Parcelable.Creator<ImMessage> CREATOR = new Parcelable.Creator<ImMessage>() {
        @Override
        public ImMessage createFromParcel(Parcel source) {
            return new ImMessage(source);
        }

        @Override
        public ImMessage[] newArray(int size) {
            return new ImMessage[size];
        }
    };

    private String content;
    private Long timestamp;
    private Integer type;
    private String watchId;

    public ImMessage() {
    }

    protected ImMessage(Parcel source) {
        this.content = source.readString();
        this.type = Integer.valueOf(source.readInt());
        this.watchId = source.readString();
        this.timestamp = Long.valueOf(source.readLong());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
        dest.writeInt(this.type.intValue());
        dest.writeString(this.watchId);
        dest.writeLong(this.timestamp.longValue());
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ImMessage{content='" + this.content + "'" + ", type=" + this.type + ", watchId='" + this.watchId
                + "'" + ", timestamp=" + this.timestamp + "}";
    }
}