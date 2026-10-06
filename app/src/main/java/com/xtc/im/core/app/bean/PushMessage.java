package com.xtc.im.core.app.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/** 推送消息，落库到 PushMessage 表并可在进程间传递。 */
@DatabaseTable(tableName = "PushMessage")
public class PushMessage implements Parcelable {

    /** 敏感词检查标记。 */
    public @interface CheckType {
        int CHECK = 0;
        int NOT_CHECK = 1;
    }

    public static final Parcelable.Creator<PushMessage> CREATOR = new Parcelable.Creator<PushMessage>() {
        @Override
        public PushMessage createFromParcel(Parcel source) {
            return new PushMessage(source);
        }

        @Override
        public PushMessage[] newArray(int size) {
            return new PushMessage[size];
        }
    };

    @DatabaseField
    private String alias;

    @DatabaseField
    private int contentType;

    @DatabaseField
    private long createTime;

    @DatabaseField
    private long dialogId;

    @DatabaseField
    private long imAccountId;

    @DatabaseField(dataType = DataType.BYTE_ARRAY)
    private byte[] msg;

    @DatabaseField
    private String msgId;

    @DatabaseField
    private int msgType;

    @DatabaseField
    private int noSensitivity;

    @DatabaseField
    private boolean notify;

    @DatabaseField
    private String pkgName;

    @DatabaseField
    private long registId;

    @DatabaseField
    private long syncKey;

    @DatabaseField
    private boolean thirdSyncMsg;

    public PushMessage() {
    }

    protected PushMessage(Parcel source) {
        this.thirdSyncMsg = source.readByte() != 0;
        this.notify = source.readByte() != 0;
        this.dialogId = source.readLong();
        this.imAccountId = source.readLong();
        this.registId = source.readLong();
        this.msgType = source.readInt();
        this.msg = source.createByteArray();
        this.syncKey = source.readLong();
        this.msgId = source.readString();
        this.pkgName = source.readString();
        this.alias = source.readString();
        this.createTime = source.readLong();
        this.contentType = source.readInt();
        this.noSensitivity = source.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.thirdSyncMsg ? (byte) 1 : (byte) 0);
        dest.writeByte(this.notify ? (byte) 1 : (byte) 0);
        dest.writeLong(this.dialogId);
        dest.writeLong(this.imAccountId);
        dest.writeLong(this.registId);
        dest.writeInt(this.msgType);
        dest.writeByteArray(this.msg);
        dest.writeLong(this.syncKey);
        dest.writeString(this.msgId);
        dest.writeString(this.pkgName);
        dest.writeString(this.alias);
        dest.writeLong(this.createTime);
        dest.writeInt(this.contentType);
        dest.writeInt(this.noSensitivity);
    }

    public boolean isThirdSyncMsg() {
        return this.thirdSyncMsg;
    }

    public void setThirdSyncMsg(boolean thirdSyncMsg) {
        this.thirdSyncMsg = thirdSyncMsg;
    }

    public boolean isNotify() {
        return this.notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
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

    public long getSyncKey() {
        return this.syncKey;
    }

    public void setSyncKey(long syncKey) {
        this.syncKey = syncKey;
    }

    public String getMsgId() {
        return this.msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getPkgName() {
        return this.pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
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
        return "PushMessage{syncKey=" + this.syncKey + ", contentType=" + this.contentType + ", msgType=" + this.msgType
                + ", msgId='" + this.msgId + "'" + ", createTime=" + this.createTime + ", thirdSyncMsg="
                + this.thirdSyncMsg + ", notify=" + this.notify + ", dialogId=" + this.dialogId + ", imAccountId="
                + this.imAccountId + ", registId=" + this.registId + ", pkgName='" + this.pkgName + "'" + ", alias='"
                + this.alias + "'" + ", noSensitivity='" + this.noSensitivity + "'" + "}";
    }
}