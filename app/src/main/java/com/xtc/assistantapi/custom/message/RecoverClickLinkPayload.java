package com.xtc.assistantapi.custom.message;

import android.os.Parcel;
import android.os.Parcelable;

import com.xtc.assistantapi.message.Payload;

/**
 * 恢复点击链接指令载荷。
 */
public class RecoverClickLinkPayload extends Payload {

    private String action;

    public RecoverClickLinkPayload(String action) {
        this.action = action;
    }

    protected RecoverClickLinkPayload(Parcel parcel) {
        super(parcel);
        this.action = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeString(this.action);
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public static final Parcelable.Creator<RecoverClickLinkPayload> CREATOR = new Parcelable.Creator<RecoverClickLinkPayload>() {
        @Override
        public RecoverClickLinkPayload createFromParcel(Parcel parcel) {
            return new RecoverClickLinkPayload(parcel);
        }

        @Override
        public RecoverClickLinkPayload[] newArray(int size) {
            return new RecoverClickLinkPayload[size];
        }
    };
}