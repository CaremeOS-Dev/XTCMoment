package com.xtc.im.core.common.response.entity;

import com.xtc.im.core.common.anotation.CommandValue;
import com.xtc.im.core.common.anotation.TagValue;

@CommandValue(21)
public class TranspondResponseEntity extends ResponseEntity {

    @TagValue(1)
    private int RID;

    @TagValue(2)
    private int code;

    @TagValue(3)
    private String desc;

    @TagValue(10)
    private byte[] header;

    @TagValue(11)
    private byte[] body;

    @TagValue(12)
    private int statusCode;

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

    public byte[] getHeader() {
        return this.header;
    }

    public void setHeader(byte[] header) {
        this.header = header;
    }

    public byte[] getBody() {
        return this.body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return "TranspondResponseEntity{RID=" + this.RID + ", code=" + this.code + ", desc='" + this.desc + "'" + ", header=" + this.header + ", body=" + this.body + ", statusCode=" + this.statusCode + "}";
    }
}
