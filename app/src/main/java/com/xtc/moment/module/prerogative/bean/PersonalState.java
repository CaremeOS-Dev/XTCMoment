package com.xtc.moment.module.prerogative.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 个人状态数据。
 */
public class PersonalState implements Parcelable {

    public static final Parcelable.Creator<PersonalState> CREATOR = new Parcelable.Creator<PersonalState>() {
        @Override
        public PersonalState createFromParcel(Parcel source) {
            return new PersonalState(source);
        }

        @Override
        public PersonalState[] newArray(int size) {
            return new PersonalState[size];
        }
    };

    private int statusId;
    private String url;
    private String desc;
    private long expireTime;
    private int likes;
    private String watchID;
    private String socialId;
    private boolean isliked;

    public PersonalState() {
    }

    protected PersonalState(Parcel source) {
        this.statusId = source.readInt();
        this.url = source.readString();
        this.desc = source.readString();
        this.expireTime = source.readLong();
        this.likes = source.readInt();
        this.watchID = source.readString();
        this.socialId = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.statusId);
        dest.writeString(this.url);
        dest.writeString(this.desc);
        dest.writeLong(this.expireTime);
        dest.writeInt(this.likes);
        dest.writeString(this.watchID);
        dest.writeString(this.socialId);
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public int getLikes() {
        return this.likes;
    }

    public int getStatusId() {
        return this.statusId;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getWatchID() {
        return this.watchID;
    }

    public void setWatchID(String watchID) {
        this.watchID = watchID;
    }

    public String getSocialId() {
        return this.socialId;
    }

    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }

    public boolean isIsliked() {
        return this.isliked;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setIsliked(boolean isliked) {
        this.isliked = isliked;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public long getExpireTime() {
        return this.expireTime;
    }

    @Override
    public String toString() {
        return "PersonalState{statusId=" + this.statusId + ", url='" + this.url + "', desc='" + this.desc
                + "', expireTime='" + this.expireTime + "', likes=" + this.likes + ", isliked=" + this.isliked
                + ", socialId='" + this.socialId + "', watchID='" + this.watchID + "'}";
    }
}