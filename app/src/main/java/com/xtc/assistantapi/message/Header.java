package com.xtc.assistantapi.message;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 指令头，包含命名空间与指令名。
 */
public class Header implements Parcelable, Serializable {

    private static final long serialVersionUID = -1825288528856671561L;

    private String namespace;
    private String name;

    public Header() {
    }

    public Header(String namespace, String name) {
        setNamespace(namespace);
        setName(name);
    }

    protected Header(Parcel parcel) {
        this.namespace = parcel.readString();
        this.name = parcel.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.namespace);
        parcel.writeString(this.name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public final void setNamespace(String namespace) {
        if (namespace == null) {
            throw new IllegalArgumentException("Header namespace must not be null");
        }
        this.namespace = namespace;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final String getNamespace() {
        return namespace;
    }

    public final String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Header{namespace='" + namespace + "', name='" + name + "'}";
    }

    public static final Parcelable.Creator<Header> CREATOR = new Parcelable.Creator<Header>() {
        @Override
        public Header createFromParcel(Parcel parcel) {
            return new Header(parcel);
        }

        @Override
        public Header[] newArray(int size) {
            return new Header[size];
        }
    };
}