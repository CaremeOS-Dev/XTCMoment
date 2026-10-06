package com.xtc.assistantapi.message;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 指令载荷基类，具体载荷类型由各指令定义。
 */
public class Payload implements Parcelable, Serializable {

    private static final long serialVersionUID = 9117206166920207324L;

    public Payload() {
    }

    protected Payload(Parcel parcel) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
    }

    public static final Parcelable.Creator<Payload> CREATOR = new Parcelable.Creator<Payload>() {
        @Override
        public Payload createFromParcel(Parcel parcel) {
            return new Payload(parcel);
        }

        @Override
        public Payload[] newArray(int size) {
            return new Payload[size];
        }
    };
}