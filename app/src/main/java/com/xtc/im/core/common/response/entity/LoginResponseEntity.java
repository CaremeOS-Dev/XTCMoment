package com.xtc.im.core.common.response.entity;

import com.xtc.im.core.common.anotation.CommandValue;
import com.xtc.im.core.common.anotation.TagValue;

@CommandValue(4)
public class LoginResponseEntity extends ResponseEntity {

    @TagValue(1)
    private int RID;

    @TagValue(2)
    private int code;

    @TagValue(3)
    private String desc;

    @TagValue(10)
    private String encryptKey;

    @TagValue(11)
    private int encryptType;

    @TagValue(12)
    private long serverTime;

    @Override
    public int getRID() {
        return this.RID;
    }

    @Override
    public void setRID(int RID) {
        this.RID = RID;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }

    @Override
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getEncryptKey() {
        return this.encryptKey;
    }

    public void setEncryptKey(String encryptKey) {
        this.encryptKey = encryptKey;
    }

    public int getEncryptType() {
        return this.encryptType;
    }

    public void setEncryptType(int encryptType) {
        this.encryptType = encryptType;
    }

    public long getServerTime() {
        return this.serverTime;
    }

    public void setServerTime(long serverTime) {
        this.serverTime = serverTime;
    }

    @Override
    public String toString() {
        return "LoginResponseEntity{RID=" + this.RID + ", code=" + this.code + ", desc='" + this.desc + "'" + ", encryptKey='" + this.encryptKey + "'" + ", encryptType=" + this.encryptType + ", serverTime=" + this.serverTime + "}";
    }
}
