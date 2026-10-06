package com.xtc.shareapi.share.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * 可在 Bundle/Intent 中传递的字符串映射（Map）包装类。
 */
public class ParcelableMap implements Parcelable {

    private Map map;

    public ParcelableMap() {
    }

    public ParcelableMap(Map<String, String> map) {
        this.map = map;
    }

    protected ParcelableMap(Parcel parcel) {
        this.map = parcel.readHashMap(HashMap.class.getClassLoader());
    }

    public Map<String, String> getMap() {
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

    public static final Parcelable.Creator<ParcelableMap> CREATOR = new Parcelable.Creator<ParcelableMap>() {
        @Override
        public ParcelableMap createFromParcel(Parcel parcel) {
            return new ParcelableMap(parcel);
        }

        @Override
        public ParcelableMap[] newArray(int size) {
            return new ParcelableMap[size];
        }
    };
}