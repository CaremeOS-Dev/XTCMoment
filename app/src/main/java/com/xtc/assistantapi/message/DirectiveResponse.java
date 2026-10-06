package com.xtc.assistantapi.message;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 指令处理响应，携带结果码与描述。
 */
public class DirectiveResponse implements Parcelable, Serializable {

    private static final long serialVersionUID = -8562843416515624015L;

    private int code;
    private String message;

    public DirectiveResponse() {
    }

    public DirectiveResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    protected DirectiveResponse(Parcel parcel) {
        this.code = parcel.readInt();
        this.message = parcel.readString();
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.code);
        parcel.writeString(this.message);
    }

    @Override
    public String toString() {
        return "DirectiveResponse{code=" + code + ", message='" + message + "'}";
    }

    public static final Parcelable.Creator<DirectiveResponse> CREATOR = new Parcelable.Creator<DirectiveResponse>() {
        @Override
        public DirectiveResponse createFromParcel(Parcel parcel) {
            return new DirectiveResponse(parcel);
        }

        @Override
        public DirectiveResponse[] newArray(int size) {
            return new DirectiveResponse[size];
        }
    };
}