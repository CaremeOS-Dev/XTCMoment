package com.xtc.im.core.common.request.entity;

import com.xtc.im.core.common.anotation.CommandValue;
import com.xtc.im.core.common.anotation.TagValue;
import java.util.Arrays;

@CommandValue(33)
public class EncryptSetRequestEntity extends RequestEntity {

    @TagValue(1)
    private int RID;

    @TagValue(10)
    private int encryptType;

    @TagValue(11)
    private byte[] encryptKey;

    @Override
    public int getRID() {
        return this.RID;
    }

    @Override
    public void setRID(int RID) {
        this.RID = RID;
    }

    public int getEncryptType() {
        return this.encryptType;
    }

    public void setEncryptType(int encryptType) {
        this.encryptType = encryptType;
    }

    public byte[] getEncryptKey() {
        return this.encryptKey;
    }

    public void setEncryptKey(byte[] encryptKey) {
        this.encryptKey = encryptKey;
    }

    @Override
    public String toString() {
        return "EncryptSetRequestEntity{RID=" + this.RID + ", encryptType=" + this.encryptType + ", encryptKey=" + this.encryptKey + "}";
    }
}
