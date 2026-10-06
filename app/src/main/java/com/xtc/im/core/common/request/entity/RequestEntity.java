package com.xtc.im.core.common.request.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.xtc.im.core.common.anotation.CommandValue;
import com.xtc.im.core.common.request.Entity;

/** 请求实体基类，支持克隆与 Parcelable。 */
@CommandValue(0)
public abstract class RequestEntity extends Entity implements Parcelable, Cloneable {

    public static final Parcelable.Creator<RequestEntity> CREATOR = new Parcelable.Creator<RequestEntity>() {
        @Override
        public RequestEntity createFromParcel(Parcel source) {
            return null;
        }

        @Override
        public RequestEntity[] newArray(int size) {
            return null;
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    /** 请求 id，由子类覆盖。 */
    public int getRID() {
        return 0;
    }

    public void setRID(int rid) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public RequestEntity cloneRequestEntity() throws CloneNotSupportedException {
        return (RequestEntity) clone();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}