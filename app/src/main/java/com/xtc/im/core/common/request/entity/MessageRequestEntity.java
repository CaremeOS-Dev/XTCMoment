package com.xtc.im.core.common.request.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.xtc.im.core.common.anotation.CommandValue;
import com.xtc.im.core.common.anotation.TagValue;

/** 群消息发送请求。 */
@CommandValue(9)
public class MessageRequestEntity extends RequestEntity implements Parcelable {

    public static final Parcelable.Creator<MessageRequestEntity> CREATOR = new Parcelable.Creator<MessageRequestEntity>() {
        @Override
        public MessageRequestEntity createFromParcel(Parcel source) {
            return new MessageRequestEntity(source);
        }

        @Override
        public MessageRequestEntity[] newArray(int size) {
            return new MessageRequestEntity[size];
        }
    };

    @TagValue(1)
    private int RID;

    @TagValue(2)
    private int needResponse;

    @TagValue(10)
    private long dialogId;

    @TagValue(11)
    private long imAccountId;

    @TagValue(12)
    private long registId;

    @TagValue(13)
    private int msgType;

    @TagValue(14)
    private byte[] msg;

    @TagValue(15)
    private String msgId;

    @TagValue(18)
    private int contentType;

    @TagValue(20)
    private int noSensitivity;

    public MessageRequestEntity() {
    }

    protected MessageRequestEntity(Parcel source) {
        this.RID = source.readInt();
        this.needResponse = source.readInt();
        this.dialogId = source.readLong();
        this.imAccountId = source.readLong();
        this.registId = source.readLong();
        this.msgType = source.readInt();
        this.msg = source.createByteArray();
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
        dest.writeLong(this.dialogId);
        dest.writeLong(this.imAccountId);
        dest.writeLong(this.registId);
        dest.writeInt(this.msgType);
        dest.writeByteArray(this.msg);
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

    public long getDialogId() {
        return this.dialogId;
    }

    public void setDialogId(long dialogId) {
        this.dialogId = dialogId;
    }

    public long getImAccountId() {
        return this.imAccountId;
    }

    public void setImAccountId(long imAccountId) {
        this.imAccountId = imAccountId;
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

    public byte[] getMsg() {
        return this.msg;
    }

    public void setMsg(byte[] msg) {
        this.msg = msg;
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
        return "MessageRequestEntity{RID=" + this.RID + ", needResponse=" + this.needResponse + ", dialogId="
                + this.dialogId + ", imAccountId=" + this.imAccountId + ", registId=" + this.registId + ", msgType="
                + this.msgType + ", msgId='" + this.msgId + "'" + ", contentType=" + this.contentType
                + ", noSensitivity=" + this.noSensitivity + "}";
    }
}