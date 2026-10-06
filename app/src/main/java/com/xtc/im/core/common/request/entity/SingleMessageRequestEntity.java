package com.xtc.im.core.common.request.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.xtc.im.core.common.anotation.CommandValue;
import com.xtc.im.core.common.anotation.TagValue;

/** 单聊消息发送请求。 */
@CommandValue(40)
public class SingleMessageRequestEntity extends RequestEntity implements Parcelable {

    public static final Parcelable.Creator<SingleMessageRequestEntity> CREATOR =
            new Parcelable.Creator<SingleMessageRequestEntity>() {
                @Override
                public SingleMessageRequestEntity createFromParcel(Parcel source) {
                    return new SingleMessageRequestEntity(source);
                }

                @Override
                public SingleMessageRequestEntity[] newArray(int size) {
                    return new SingleMessageRequestEntity[size];
                }
            };

    @TagValue(1)
    private int RID;

    @TagValue(2)
    private int needResponse;

    @TagValue(10)
    private long receiverId;

    @TagValue(11)
    private long accountId;

    @TagValue(12)
    private long registId;

    @TagValue(13)
    private int msgType;

    @TagValue(14)
    private byte[] message;

    @TagValue(15)
    private String msgId;

    @TagValue(16)
    private int contentType;

    @TagValue(18)
    private int noSensitivity;

    public SingleMessageRequestEntity() {
    }

    protected SingleMessageRequestEntity(Parcel source) {
        this.RID = source.readInt();
        this.needResponse = source.readInt();
        this.receiverId = source.readLong();
        this.accountId = source.readLong();
        this.registId = source.readLong();
        this.msgType = source.readInt();
        this.message = source.createByteArray();
        this.msgId = source.readString();
        this.contentType = source.readInt();
        this.noSensitivity = source.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.RID);
        dest.writeInt(this.needResponse);
        dest.writeLong(this.receiverId);
        dest.writeLong(this.accountId);
        dest.writeLong(this.registId);
        dest.writeInt(this.msgType);
        dest.writeByteArray(this.message);
        dest.writeString(this.msgId);
        dest.writeInt(this.contentType);
        dest.writeInt(this.noSensitivity);
    }

    @Override
    public int getRID() {
        return this.RID;
    }

    @Override
    public void setRID(int RID) {
        this.RID = RID;
    }

    public int getNeedResponse() {
        return this.needResponse;
    }

    public void setNeedResponse(int needResponse) {
        this.needResponse = needResponse;
    }

    public long getReceiverId() {
        return this.receiverId;
    }

    public void setReceiverId(long receiverId) {
        this.receiverId = receiverId;
    }

    public long getAccountId() {
        return this.accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public long getRegistId() {
        return this.registId;
    }

    public void setRegistId(long registId) {
        this.registId = registId;
    }

    public int getMsgType() {
        return this.msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public byte[] getMessage() {
        return this.message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }

    public String getMsgId() {
        return this.msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public int getContentType() {
        return this.contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public int getNoSensitivity() {
        return this.noSensitivity;
    }

    public void setNoSensitivity(int noSensitivity) {
        this.noSensitivity = noSensitivity;
    }

    @Override
    public String toString() {
        return "SingleMessageRequestEntity{RID=" + this.RID + ", needResponse=" + this.needResponse + ", receiverId="
                + this.receiverId + ", accountId=" + this.accountId + ", registId=" + this.registId + ", msgType="
                + this.msgType + ", msgId='" + this.msgId + "'" + ", contentType=" + this.contentType
                + ", noSensitivity=" + this.noSensitivity + "}";
    }
}