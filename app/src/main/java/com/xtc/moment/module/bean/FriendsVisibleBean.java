package com.xtc.moment.module.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 动态的可见范围设置：权限类型 + 可见好友 lookupId 列表 + 动态 id。
 */
public class FriendsVisibleBean implements Parcelable {

    public static final Parcelable.Creator<FriendsVisibleBean> CREATOR = new Parcelable.Creator<FriendsVisibleBean>() {
        @Override
        public FriendsVisibleBean createFromParcel(Parcel source) {
            return new FriendsVisibleBean(source);
        }

        @Override
        public FriendsVisibleBean[] newArray(int size) {
            return new FriendsVisibleBean[size];
        }
    };

    private int permissionType;
    private List<String> lookupIds;
    private String momentId;

    public FriendsVisibleBean() {
    }

    protected FriendsVisibleBean(Parcel source) {
        this.permissionType = source.readInt();
        this.lookupIds = source.createStringArrayList();
        this.momentId = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.permissionType);
        dest.writeStringList(this.lookupIds);
        dest.writeString(this.momentId);
    }

    public int getType() {
        return this.permissionType;
    }

    public void setType(int permissionType) {
        this.permissionType = permissionType;
    }

    public List<String> getFriends() {
        return this.lookupIds;
    }

    public void setFriends(List<String> lookupIds) {
        this.lookupIds = lookupIds;
    }

    public String getMomentId() {
        return this.momentId;
    }

    public void setMomentId(String momentId) {
        this.momentId = momentId;
    }

    @Override
    public String toString() {
        return "FriendsVisibleBean{type=" + this.permissionType + ", lookupIds=" + this.lookupIds + ", momentId='"
                + this.momentId + "'}";
    }
}