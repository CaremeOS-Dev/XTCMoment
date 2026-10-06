package com.xtc.personalitydress.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 个性化装扮的跨进程传输基类，按类名多态还原具体装扮（气泡/头像/昵称）。
 */
public class RemoteDress implements Parcelable {

    public static final Parcelable.Creator<RemoteDress> CREATOR = new Parcelable.Creator<RemoteDress>() {
        @Override
        public RemoteDress createFromParcel(Parcel source) {
            String className = source.readString();
            if (className.equals(RemoteNickname.class.getName())) {
                return new RemoteNickname(source);
            }
            if (className.equals(RemoteHead.class.getName())) {
                return new RemoteHead(source);
            }
            return new RemoteDress(source);
        }

        @Override
        public RemoteDress[] newArray(int size) {
            return new RemoteDress[size];
        }
    };

    private String dressId;
    private String sourcePath;

    public RemoteDress() {
    }

    protected RemoteDress(Parcel source) {
        this.dressId = source.readString();
        this.sourcePath = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void readFromParcel(Parcel source) {
        this.dressId = source.readString();
        this.sourcePath = source.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.dressId);
        dest.writeString(this.sourcePath);
    }
}