package com.xtc.moment.module.assistant.message;

import android.os.Parcel;
import android.os.Parcelable;

import com.xtc.assistantapi.message.Payload;

/**
 * 语音助手下发“发布状态”指令时携带的数据。
 */
public class PostStatusPayload extends Payload {

    private String messageType;
    private String content;

    public PostStatusPayload() {
    }

    public PostStatusPayload(String messageType, String content) {
        this.messageType = messageType;
        this.content = content;
    }

    protected PostStatusPayload(Parcel parcel) {
        super(parcel);
        this.messageType = parcel.readString();
        this.content = parcel.readString();
    }

    public String getMessageType() {
        return this.messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeString(this.messageType);
        parcel.writeString(this.content);
    }

    @Override
    public String toString() {
        return "PostStatusPayload{messageType='" + this.messageType + "', content='" + this.content + "'}";
    }

    public static final Parcelable.Creator<PostStatusPayload> CREATOR = new Parcelable.Creator<PostStatusPayload>() {
        @Override
        public PostStatusPayload createFromParcel(Parcel parcel) {
            return new PostStatusPayload(parcel);
        }

        @Override
        public PostStatusPayload[] newArray(int size) {
            return new PostStatusPayload[size];
        }
    };
}