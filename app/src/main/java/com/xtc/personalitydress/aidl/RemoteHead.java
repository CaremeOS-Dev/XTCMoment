package com.xtc.personalitydress.aidl;

import android.os.Parcel;

/**
 * 个性化头像装扮数据。
 */
public class RemoteHead extends RemoteDress {

    private String headId;
    private String sourcePath;
    private int movementType;
    private int carouseNum;

    public RemoteHead() {
    }

    protected RemoteHead(Parcel source) {
        this.headId = source.readString();
        this.sourcePath = source.readString();
        this.movementType = source.readInt();
        this.carouseNum = source.readInt();
    }

    public String getHeadId() {
        return this.headId;
    }

    public void setHeadId(String headId) {
        this.headId = headId;
    }

    public String getSourcePath() {
        return this.sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public void setMovementType(int movementType) {
        this.movementType = movementType;
    }

    public int getMovementType() {
        return this.movementType;
    }

    public int getCarouseNum() {
        return this.carouseNum;
    }

    public void setCarouseNum(int carouseNum) {
        this.carouseNum = carouseNum;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void readFromParcel(Parcel source) {
        source.readString();
        this.headId = source.readString();
        this.sourcePath = source.readString();
        this.movementType = source.readInt();
        this.carouseNum = source.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getClass().getName());
        dest.writeString(this.headId);
        dest.writeString(this.sourcePath);
        dest.writeInt(this.movementType);
        dest.writeInt(this.carouseNum);
    }

    @Override
    public String toString() {
        return "RemoteHead{headId='" + this.headId + "', sourcePath='" + this.sourcePath
                + "', movementType=" + this.movementType + '}';
    }
}