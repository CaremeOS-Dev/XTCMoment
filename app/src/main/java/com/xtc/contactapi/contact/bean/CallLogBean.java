package com.xtc.contactapi.contact.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 通话记录数据。
 */
public class CallLogBean implements Parcelable {

    private int id;
    private String headImage;
    private String name;
    private long time;
    private int state;
    private long stateTime;
    private int type;
    private int callType;
    private int readState;
    private String contactServerId;
    private String mobileNumber;
    private int contactType;
    private int role;

    protected CallLogBean(Parcel parcel) {
        this.id = parcel.readInt();
        this.headImage = parcel.readString();
        this.name = parcel.readString();
        this.time = parcel.readLong();
        this.state = parcel.readInt();
        this.stateTime = parcel.readLong();
        this.type = parcel.readInt();
        this.callType = parcel.readInt();
        this.readState = parcel.readInt();
        this.contactServerId = parcel.readString();
        this.mobileNumber = parcel.readString();
        this.contactType = parcel.readInt();
        this.role = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.id);
        parcel.writeString(this.headImage);
        parcel.writeString(this.name);
        parcel.writeLong(this.time);
        parcel.writeInt(this.state);
        parcel.writeLong(this.stateTime);
        parcel.writeInt(this.type);
        parcel.writeInt(this.callType);
        parcel.writeInt(this.readState);
        parcel.writeString(this.contactServerId);
        parcel.writeString(this.mobileNumber);
        parcel.writeInt(this.contactType);
        parcel.writeInt(this.role);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getStateTime() {
        return stateTime;
    }

    public void setStateTime(long stateTime) {
        this.stateTime = stateTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public int getCallType() {
        return callType;
    }

    public void setCallType(int callType) {
        this.callType = callType;
    }

    public int getReadState() {
        return readState;
    }

    public void setReadState(int readState) {
        this.readState = readState;
    }

    public String getContactServerId() {
        return contactServerId;
    }

    public void setContactServerId(String contactServerId) {
        this.contactServerId = contactServerId;
    }

    public int getContactType() {
        return contactType;
    }

    public void setContactType(int contactType) {
        this.contactType = contactType;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @Override
    public String toString() {
        return "CallLogBean{id=" + id + ", headImage='" + headImage + "', name='" + name + "', time=" + time
                + ", state=" + state + ", stateTime=" + stateTime + ", type=" + type + ", callType=" + callType
                + ", readState=" + readState + ", contactServerId='" + contactServerId + "', mobileNumber='"
                + mobileNumber + "', contactType=" + contactType + ", role=" + role + '}';
    }

    public static final Parcelable.Creator<CallLogBean> CREATOR = new Parcelable.Creator<CallLogBean>() {
        @Override
        public CallLogBean createFromParcel(Parcel parcel) {
            return new CallLogBean(parcel);
        }

        @Override
        public CallLogBean[] newArray(int size) {
            return new CallLogBean[size];
        }
    };
}