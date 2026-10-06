package com.xtc.im.core.common.request.entity;

import com.xtc.im.core.common.anotation.CommandValue;
import com.xtc.im.core.common.anotation.TagValue;

@CommandValue(20)
public class TranspondRequestEntity extends RequestEntity {

    @TagValue(1)
    private int RID;

    @TagValue(10)
    private String url;

    @TagValue(11)
    private int method;

    @TagValue(12)
    private byte[] header;

    @TagValue(13)
    private byte[] body;

    @Override
    public int getRID() {
        return this.RID;
    }

    @Override
    public void setRID(int RID) {
        this.RID = RID;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getMethod() {
        return this.method;
    }

    public void setMethod(int method) {
        this.method = method;
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

    @Override
    public String toString() {
        return "TranspondRequestEntity{RID=" + this.RID + ", url='" + this.url + "'" + ", method=" + this.method + ", header=" + this.header + ", body=" + this.body + "}";
    }
}
