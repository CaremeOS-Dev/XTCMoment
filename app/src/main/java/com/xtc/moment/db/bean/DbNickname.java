package com.xtc.moment.db.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/** A nickname (dress) resource row. */
@DatabaseTable(tableName = "nickname")
public class DbNickname implements Parcelable {

    public static final Parcelable.Creator<DbNickname> CREATOR = new Parcelable.Creator<DbNickname>() {
        @Override
        public DbNickname createFromParcel(Parcel parcel) {
            return new DbNickname(parcel);
        }

        @Override
        public DbNickname[] newArray(int size) {
            return new DbNickname[size];
        }
    };

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField(unique = true)
    private String nicknameId;

    @DatabaseField
    private String sourcePath;

    public DbNickname() {
    }

    protected DbNickname(Parcel parcel) {
        if (parcel.readByte() == 0) {
            this.id = null;
        } else {
            this.id = Integer.valueOf(parcel.readInt());
        }
        this.nicknameId = parcel.readString();
        this.sourcePath = parcel.readString();
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
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
    public String toString() {
        return "DbNickname{id=" + this.id + ", nicknameId='" + this.nicknameId + "', sourcePath='" + this.sourcePath + "'}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DbNickname other = (DbNickname) obj;
        String id = this.nicknameId;
        if (id == null ? other.nicknameId != null : !id.equals(other.nicknameId)) {
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
        String id = this.nicknameId;
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
        parcel.writeString(this.nicknameId);
        parcel.writeString(this.sourcePath);
    }
}
