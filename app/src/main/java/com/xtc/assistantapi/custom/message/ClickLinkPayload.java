package com.xtc.assistantapi.custom.message;

import android.os.Parcel;
import android.os.Parcelable;

import com.xtc.assistantapi.message.Payload;

/**
 * 点击链接指令载荷。
 */
public class ClickLinkPayload extends Payload {

    private String url;

    public ClickLinkPayload(String url) {
        this.url = url;
    }

    protected ClickLinkPayload(Parcel parcel) {
        super(parcel);
        this.url = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeString(this.url);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static final Parcelable.Creator<ClickLinkPayload> CREATOR = new Parcelable.Creator<ClickLinkPayload>() {
        @Override
        public ClickLinkPayload createFromParcel(Parcel parcel) {
            return new ClickLinkPayload(parcel);
        }

        @Override
        public ClickLinkPayload[] newArray(int size) {
            return new ClickLinkPayload[size];
        }
    };
}