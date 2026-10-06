package com.xtc.assistantapi.message;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.xtc.assistantapi.LogTag;

import java.io.Serializable;

/**
 * 助手指令，由指令头与载荷组成，并保留原始报文。
 */
public class Directive implements Parcelable, Serializable {

    private static final long serialVersionUID = 8432822302032062559L;
    private static final String TAG = LogTag.of("Directive");

    public Header header;
    public Payload payload;

    @Expose(deserialize = false, serialize = false)
    private Gson gson;
    @Expose(deserialize = false, serialize = false)
    private String rawMessage;
    @Expose(deserialize = false, serialize = false)
    private String rawPayload;

    public Directive() {
    }

    public Directive(Header header, Payload payload) {
        this.header = header;
        this.payload = payload;
    }

    public Directive(Header header, JsonElement payloadElement, String rawMessage) {
        this.header = header;
        Class<?> payloadClass = PayloadConfig.getInstance().findPayloadClass(header.getNamespace(), header.getName());
        if (payloadClass != null) {
            this.gson = new Gson();
            this.payload = (Payload) this.gson.fromJson(payloadElement, payloadClass);
        } else {
            this.payload = new Payload();
        }
        this.rawMessage = rawMessage;
        if (payloadElement != null) {
            this.rawPayload = payloadElement.toString();
        }
    }

    protected Directive(Parcel parcel) {
        this.header = parcel.readParcelable(Header.class.getClassLoader());
        this.payload = parcel.readParcelable(Payload.class.getClassLoader());
    }

    public Header getHeader() {
        return header;
    }

    public Payload getPayload() {
        return payload;
    }

    public String getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(String rawMessage) {
        this.rawMessage = rawMessage;
    }

    public String getRawPayload() {
        return rawPayload;
    }

    public void setRawPayload(String rawPayload) {
        this.rawPayload = rawPayload;
    }

    /** 指令名。 */
    public String getName() {
        Header header = this.header;
        return header != null ? header.getName() : "";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(this.header, flags);
        parcel.writeParcelable(this.payload, flags);
    }

    @Override
    public String toString() {
        return "Directive{header=" + header + ", payload=" + payload + '}';
    }

    public static final Parcelable.Creator<Directive> CREATOR = new Parcelable.Creator<Directive>() {
        @Override
        public Directive createFromParcel(Parcel parcel) {
            return new Directive(parcel);
        }

        @Override
        public Directive[] newArray(int size) {
            return new Directive[size];
        }
    };
}