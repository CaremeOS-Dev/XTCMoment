package com.xtc.im.core.common.response.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.xtc.im.core.common.anotation.CommandValue;
import com.xtc.im.core.common.request.Entity;

/** 响应实体基类，子类覆盖 code / desc / rid。 */
@CommandValue(0)
public class ResponseEntity extends Entity implements Parcelable {

    public static final Parcelable.Creator<ResponseEntity> CREATOR = new Parcelable.Creator<ResponseEntity>() {
        @Override
        public ResponseEntity createFromParcel(Parcel source) {
            return new ResponseEntity(source);
        }

        @Override
        public ResponseEntity[] newArray(int size) {
            return new ResponseEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public int getCode() {
        return 0;
    }

    public String getDesc() {
        return null;
    }

    public int getRID() {
        return 0;
    }

    public void setCode(int code) {
    }

    public void setDesc(String desc) {
    }

    public void setRID(int rid) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public ResponseEntity() {
    }

    protected ResponseEntity(Parcel source) {
    }
}