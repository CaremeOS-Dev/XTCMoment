package com.xtc.im.core.common.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.xtc.im.core.common.request.Command;
import com.xtc.im.core.common.response.entity.ResponseEntity;

/** 一次推送请求的响应包装，含解析后的实体与原始 TLV 数据。 */
public class PushResponse implements Parcelable {

    /** 响应码。 */
    public interface Code {
        int AIDL_REMOTE_SERVICE_ERROR = 5;
        int CANCELED_REQUEST = 2;
        int LOGOUT = 4;
        int LOW_POWER = 8;
        int OFFLINE = 3;
        int PUBLICKEY_EXPIRE = 5200;
        int REGIST_TOKEN_INVALID = 4200;
        int REQUEST_THREAD_POOL_SHUTDOWN = 6;
        int SEND_ERROR = 7;
        int SUCCESS = 200;
        int TIME_OUT = 1;
        int UNKNOW_REGISTID = 3200;
    }

    public static final Parcelable.Creator<PushResponse> CREATOR = new Parcelable.Creator<PushResponse>() {
        @Override
        public PushResponse createFromParcel(Parcel source) {
            return new PushResponse(source);
        }

        @Override
        public PushResponse[] newArray(int size) {
            return new PushResponse[size];
        }
    };

    private ResponseEntity responseEntity;
    private byte[] tlvData;

    public PushResponse(ResponseEntity responseEntity, byte[] tlvData) {
        this.responseEntity = responseEntity;
        this.tlvData = tlvData;
    }

    protected PushResponse(Parcel source) {
        this.responseEntity = (ResponseEntity) source.readValue(getClass().getClassLoader());
        this.tlvData = source.createByteArray();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.responseEntity);
        dest.writeByteArray(this.tlvData);
    }

    public void readFromParcel(Parcel source) {
        this.responseEntity = (ResponseEntity) source.readValue(getClass().getClassLoader());
        this.tlvData = source.createByteArray();
    }

    public byte[] getTlvData() {
        return this.tlvData;
    }

    public void setTlvData(byte[] tlvData) {
        this.tlvData = tlvData;
    }

    public ResponseEntity getResponseEntity() {
        return this.responseEntity;
    }

    public void setResponseEntity(ResponseEntity responseEntity) {
        this.responseEntity = responseEntity;
    }

    public int getCommand() {
        return this.responseEntity.getCommand();
    }

    public int getCode() {
        return this.responseEntity.getCode();
    }

    public String getDesc() {
        return this.responseEntity.getDesc();
    }

    public int getRID() {
        return this.responseEntity.getRID();
    }

    /** 服务端主动推送类响应或业务成功码视为成功。 */
    public boolean isSuccess() {
        int command = this.responseEntity.getCommand();
        return command == Command.SYNC_INFORM || command == Command.VOICE_SLICE
                || command == Command.THIRD_SYNC_INFORM || command == Command.PUSH_RESPONSE
                || command == Command.HEART_BEAT_RESPONSE || this.responseEntity.getCode() == Code.SUCCESS;
    }

    public boolean isRegistTokenInvalid() {
        return this.responseEntity.getCommand() == Command.LOGIN_RESPONSE
                && this.responseEntity.getCode() == Code.REGIST_TOKEN_INVALID;
    }

    public boolean isUnknowRegistId() {
        return this.responseEntity.getCommand() == Command.LOGIN_RESPONSE
                && this.responseEntity.getCode() == Code.UNKNOW_REGISTID;
    }

    public boolean isPublicKeyExpire() {
        return this.responseEntity.getCommand() == Command.ENCRYPT_SET_RESPONSE
                && this.responseEntity.getCode() == Code.PUBLICKEY_EXPIRE;
    }

    public boolean isTimeout() {
        int command = this.responseEntity.getCommand();
        if (command == Command.SYNC_INFORM || command == Command.VOICE_SLICE
                || command == Command.THIRD_SYNC_INFORM || command == Command.PUSH_RESPONSE
                || command == Command.HEART_BEAT_RESPONSE) {
            return false;
        }
        return this.responseEntity.getCode() == Code.TIME_OUT;
    }

    @Override
    public String toString() {
        return "PushResponse{responseEntity=" + this.responseEntity + '}';
    }
}