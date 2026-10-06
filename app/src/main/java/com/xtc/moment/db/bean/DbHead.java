package com.xtc.moment.db.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/** A head (avatar / dress) resource row. */
@DatabaseTable(tableName = "head")
public class DbHead implements Parcelable {

    public static final Parcelable.Creator<DbHead> CREATOR = new Parcelable.Creator<DbHead>() {
        @Override
        public DbHead createFromParcel(Parcel parcel) {
            return new DbHead(parcel);
        }

        @Override
        public DbHead[] newArray(int size) {
            return new DbHead[size];
        }
    };

    @DatabaseField
    private int carouseNum;

    @DatabaseField(unique = true)
    private String headId;

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField
    private int movementType;

    @DatabaseField
    private String sourcePath;

    public DbHead() {
    }

    protected DbHead(Parcel parcel) {
        if (parcel.readByte() == 0) {
            this.id = null;
        } else {
            this.id = Integer.valueOf(parcel.readInt());
        }
        this.headId = parcel.readString();
        this.sourcePath = parcel.readString();
        this.movementType = parcel.readInt();
        this.carouseNum = parcel.readInt();
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public int getMovementType() {
        return this.movementType;
    }

    public void setMovementType(int movementType) {
        this.movementType = movementType;
    }

    public int getCarouseNum() {
        return this.carouseNum;
    }

    public void setCarouseNum(int carouseNum) {
        this.carouseNum = carouseNum;
    }

    @Override
    public String toString() {
        return "DbHead{id=" + this.id + ", headId='" + this.headId + "', sourcePath='" + this.sourcePath + "', movementType=" + this.movementType + ", carouseNum=" + this.carouseNum + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DbHead other = (DbHead) obj;
        String id = this.headId;
        if (id == null ? other.headId != null : !id.equals(other.headId)) {
            return false;
        }
        if (this.carouseNum != other.carouseNum) {
            return false;
        }
        String path = this.sourcePath;
        String otherPath = other.sourcePath;
        if (path != null) {
            return path.equals(otherPath);
        }
        return otherPath == null;
    }

    @Override
    public int hashCode() {
        String id = this.headId;
        int result = (id != null ? id.hashCode() : 0) * 31;
        String path = this.sourcePath;
        return result + (path != null ? path.hashCode() : 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        if (this.id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(this.id.intValue());
        }
        parcel.writeString(this.headId);
        parcel.writeString(this.sourcePath);
        parcel.writeInt(this.movementType);
        parcel.writeInt(this.carouseNum);
    }
}
