package com.xtc.im.core.common.response.entity;

import com.xtc.im.core.common.anotation.CommandValue;
import com.xtc.im.core.common.anotation.TagValue;

@CommandValue(23)
public class PublicKeyResponseEntity extends ResponseEntity {

    @TagValue(1)
    private int RID;

    @TagValue(2)
    private int code;

    @TagValue(3)
    private String desc;

    @TagValue(10)
    private byte[] publicKey;

    @TagValue(11)
    private byte[] modulus;

    @TagValue(12)
    private byte[] exponent;

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

    public byte[] getPublicKey() {
        return this.publicKey;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public byte[] getModulus() {
        return this.modulus;
    }

    public void setModulus(byte[] modulus) {
        this.modulus = modulus;
    }

    public byte[] getExponent() {
        return this.exponent;
    }

    public void setExponent(byte[] exponent) {
        this.exponent = exponent;
    }

    @Override
    public String toString() {
        return "PublicKeyResponseEntity{RID=" + this.RID + ", code=" + this.code + ", desc='" + this.desc + "'" + ", publicKey=" + this.publicKey + ", modulus=" + this.modulus + ", exponent=" + this.exponent + "}";
    }
}
