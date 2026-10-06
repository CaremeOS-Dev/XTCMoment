package com.xtc.shareapi.share.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * 可在 Bundle/Intent 中传递的字符串映射（HashMap）包装类。
 */
public class SerializableMap implements Parcelable {

    private HashMap map;

    public SerializableMap() {
    }

    public SerializableMap(HashMap<String, String> map) {
        this.map = map;
    }

    protected SerializableMap(Parcel parcel) {
        this.map = parcel.readHashMap(HashMap.class.getClassLoader());
    }

    public HashMap getMap() {
        return map;
    }

    public void setMap(HashMap map) {
        this.map = map;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeMap(map);
    }

    @Override
    public String toString() {
        return "SerializableMap{map=" + map + '}';
    }

    public static final Parcelable.Creator<SerializableMap> CREATOR = new Parcelable.Creator<SerializableMap>() {
        @Override
        public SerializableMap createFromParcel(Parcel parcel) {
            return new SerializableMap(parcel);
        }

        @Override
        public SerializableMap[] newArray(int size) {
            return new SerializableMap[size];
        }
    };
}