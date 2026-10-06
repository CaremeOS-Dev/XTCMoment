package com.xtc.personalitydress.aidl;

import android.os.Parcel;

/**
 * 个性化昵称装扮数据。
 */
public class RemoteNickname extends RemoteDress {

    private String nicknameId;
    private String sourcePath;

    public RemoteNickname() {
    }

    protected RemoteNickname(Parcel source) {
        this.nicknameId = source.readString();
        this.sourcePath = source.readString();
    }

    public String getNicknameId() {
        return this.nicknameId;
    }

    public void setNicknameId(String nicknameId) {
        this.nicknameId = nicknameId;
    }

    public String getSourcePath() {
        return this.sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void readFromParcel(Parcel source) {
        source.readString();
        this.nicknameId = source.readString();
        this.sourcePath = source.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getClass().getName());
        dest.writeString(this.nicknameId);
        dest.writeString(this.sourcePath);
    }

    @Override
    public String toString() {
        return "RemoteNickname{nicknameId='" + this.nicknameId + "', sourcePath='" + this.sourcePath + "'}";
    }
}