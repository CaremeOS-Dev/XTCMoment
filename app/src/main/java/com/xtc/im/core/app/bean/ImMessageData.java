package com.xtc.im.core.app.bean;

import android.os.Parcel;
import android.os.Parcelable;

/** IM 消息的完整数据，含会话、账号与同步信息。 */
public class ImMessageData implements Parcelable {

    public static final Parcelable.Creator<ImMessageData> CREATOR = new Parcelable.Creator<ImMessageData>() {
        @Override
        public ImMessageData createFromParcel(Parcel source) {
            return new ImMessageData(source);
        }

        @Override
        public ImMessageData[] newArray(int size) {
            return new ImMessageData[size];
        }
    };

    private Long accountId;
    private Integer contentType;
    private Long createTime;
    private Long dialogId;
    private ImMessage message;
    private String msgId;
    private Integer msgType;
    private Long registId;
    private Long syncKey;

    public ImMessageData() {
    }

    protected ImMessageData(Parcel source) {
        this.dialogId = Long.valueOf(source.readLong());
        this.accountId = Long.valueOf(source.readLong());
        this.registId = Long.valueOf(source.readLong());
        this.msgType = Integer.valueOf(source.readInt());
        this.message = (ImMessage) source.readParcelable(ImMessage.class.getClassLoader());
        this.msgId = source.readString();
        this.syncKey = Long.valueOf(source.readLong());
        this.createTime = Long.valueOf(source.readLong());
        this.contentType = Integer.valueOf(source.readInt());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.dialogId.longValue());
        dest.writeLong(this.accountId.longValue());
        dest.writeLong(this.registId.longValue());
        dest.writeInt(this.msgType.intValue());
        dest.writeParcelable(this.message, flags);
        dest.writeString(this.msgId);
        dest.writeLong(this.syncKey.longValue());
        dest.writeLong(this.createTime.longValue());
        dest.writeInt(this.contentType.intValue());
    }

    public Long getDialogId() {
        return this.dialogId;
    }

    public void setDialogId(Long dialogId) {
        this.dialogId = dialogId;
    }

    public Long getAccountId() {
        return this.accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getRegistId() {
        return this.registId;
    }

    public void setRegistId(Long registId) {
        this.registId = registId;
    }

    public Integer getMsgType() {
        return this.msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

    public ImMessage getMessage() {
        return this.message;
    }

    public void setMessage(ImMessage message) {
        this.message = message;
    }

    public String getMsgId() {
        return this.msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public Long getSyncKey() {
        return this.syncKey;
    }

    public void setSyncKey(Long syncKey) {
        this.syncKey = syncKey;
    }

    public long getCreateTime() {
        return this.createTime.longValue();
    }

    public void setCreateTime(long createTime) {
        this.createTime = Long.valueOf(createTime);
    }

    public int getContentType() {
        return this.contentType.intValue();
    }

    public void setContentType(int contentType) {
        this.contentType = Integer.valueOf(contentType);
    }

    @Override
    public String toString() {
        return "ImMessageData{dialogId=" + this.dialogId + ", accountId=" + this.accountId + ", registId="
                + this.registId + ", msgType=" + this.msgType + ", message=" + this.message + ", msgId='" + this.msgId
                + "'" + ", syncKey=" + this.syncKey + ", createTime=" + this.createTime + ", contentType="
                + this.contentType + "}";
    }
}