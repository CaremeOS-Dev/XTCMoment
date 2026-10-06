package com.xtc.im.core.common.voice.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.xtc.im.core.common.anotation.CommandValue;
import com.xtc.im.core.common.anotation.TagValue;
import com.xtc.im.core.common.request.Entity;

/** 语音消息描述：时长、存储地址、分组与三种坐标系下的位置信息。 */
@CommandValue(0)
public class VoiceDescEntity extends Entity implements Parcelable {

    public static final Parcelable.Creator<VoiceDescEntity> CREATOR = new Parcelable.Creator<VoiceDescEntity>() {
        @Override
        public VoiceDescEntity createFromParcel(Parcel source) {
            return new VoiceDescEntity(source);
        }

        @Override
        public VoiceDescEntity[] newArray(int size) {
            return new VoiceDescEntity[size];
        }
    };

    @TagValue(10)
    private int vocTime;

    @TagValue(11)
    private String storeAddr;

    @TagValue(12)
    private String groupId;

    @TagValue(13)
    private int lastIndex;

    @TagValue(14)
    private String gdLatitude;

    @TagValue(15)
    private String gdLongitude;

    @TagValue(16)
    private int gdRadius;

    @TagValue(17)
    private String bdLatitude;

    @TagValue(18)
    private String bdLongitude;

    @TagValue(19)
    private int bdRadius;

    @TagValue(20)
    private String ggLatitude;

    @TagValue(21)
    private String ggLongitude;

    @TagValue(22)
    private int ggRadius;

    @TagValue(23)
    private String resoureKey;

    @TagValue(24)
    private long deadline;

    @TagValue(25)
    private String extra;

    public VoiceDescEntity() {
    }

    protected VoiceDescEntity(Parcel source) {
        this.vocTime = source.readInt();
        this.storeAddr = source.readString();
        this.groupId = source.readString();
        this.lastIndex = source.readInt();
        this.gdLatitude = source.readString();
        this.gdLongitude = source.readString();
        this.gdRadius = source.readInt();
        this.bdLatitude = source.readString();
        this.bdLongitude = source.readString();
        this.bdRadius = source.readInt();
        this.ggLatitude = source.readString();
        this.ggLongitude = source.readString();
        this.ggRadius = source.readInt();
        this.resoureKey = source.readString();
        this.deadline = source.readLong();
        this.extra = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.vocTime);
        dest.writeString(this.storeAddr);
        dest.writeString(this.groupId);
        dest.writeInt(this.lastIndex);
        dest.writeString(this.gdLatitude);
        dest.writeString(this.gdLongitude);
        dest.writeInt(this.gdRadius);
        dest.writeString(this.bdLatitude);
        dest.writeString(this.bdLongitude);
        dest.writeInt(this.bdRadius);
        dest.writeString(this.ggLatitude);
        dest.writeString(this.ggLongitude);
        dest.writeInt(this.ggRadius);
        dest.writeString(this.resoureKey);
        dest.writeLong(this.deadline);
        dest.writeString(this.extra);
    }

    public int getVocTime() {
        return this.vocTime;
    }

    public void setVocTime(int vocTime) {
        this.vocTime = vocTime;
    }

    public String getStoreAddr() {
        return this.storeAddr;
    }

    public void setStoreAddr(String storeAddr) {
        this.storeAddr = storeAddr;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public int getLastIndex() {
        return this.lastIndex;
    }

    public void setLastIndex(int lastIndex) {
        this.lastIndex = lastIndex;
    }

    public String getGdLatitude() {
        return this.gdLatitude;
    }

    public void setGdLatitude(String gdLatitude) {
        this.gdLatitude = gdLatitude;
    }

    public String getGdLongitude() {
        return this.gdLongitude;
    }

    public void setGdLongitude(String gdLongitude) {
        this.gdLongitude = gdLongitude;
    }

    public int getGdRadius() {
        return this.gdRadius;
    }

    public void setGdRadius(int gdRadius) {
        this.gdRadius = gdRadius;
    }

    public String getBdLatitude() {
        return this.bdLatitude;
    }

    public void setBdLatitude(String bdLatitude) {
        this.bdLatitude = bdLatitude;
    }

    public String getBdLongitude() {
        return this.bdLongitude;
    }

    public void setBdLongitude(String bdLongitude) {
        this.bdLongitude = bdLongitude;
    }

    public int getBdRadius() {
        return this.bdRadius;
    }

    public void setBdRadius(int bdRadius) {
        this.bdRadius = bdRadius;
    }

    public String getGgLatitude() {
        return this.ggLatitude;
    }

    public void setGgLatitude(String ggLatitude) {
        this.ggLatitude = ggLatitude;
    }

    public String getGgLongitude() {
        return this.ggLongitude;
    }

    public void setGgLongitude(String ggLongitude) {
        this.ggLongitude = ggLongitude;
    }

    public int getGgRadius() {
        return this.ggRadius;
    }

    public void setGgRadius(int ggRadius) {
        this.ggRadius = ggRadius;
    }

    public String getResoureKey() {
        return this.resoureKey;
    }

    public void setResoureKey(String resoureKey) {
        this.resoureKey = resoureKey;
    }

    public long getDeadline() {
        return this.deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public String getExtra() {
        return this.extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "VoiceDescEntity{vocTime=" + this.vocTime + ", storeAddr='" + this.storeAddr + "'" + ", groupId='"
                + this.groupId + "'" + ", lastIndex=" + this.lastIndex + ", gdLatitude='" + this.gdLatitude + "'"
                + ", gdLongitude='" + this.gdLongitude + "'" + ", gdRadius=" + this.gdRadius + ", bdLatitude='"
                + this.bdLatitude + "'" + ", bdLongitude='" + this.bdLongitude + "'" + ", bdRadius=" + this.bdRadius
                + ", ggLatitude='" + this.ggLatitude + "'" + ", ggLongitude='" + this.ggLongitude + "'" + ", ggRadius="
                + this.ggRadius + ", resoureKey='" + this.resoureKey + "'" + ", deadline=" + this.deadline
                + ", extra='" + this.extra + "'" + "}";
    }
}