package com.xtc.im.core.common.response.entity;

import com.xtc.im.core.common.anotation.CommandValue;
import com.xtc.im.core.common.anotation.TagValue;

@CommandValue(41)
public class MessageMergeResponseEntity extends ResponseEntity {

    @TagValue(1)
    private int RID;

    @TagValue(2)
    private int respCode;

    @TagValue(3)
    private String respDesc;

    @Override
    public int getRID() {
        return this.RID;
    }

    @Override
    public void setRID(int RID) {
        this.RID = RID;
    }

    public int getRespCode() {
        return this.respCode;
    }

    public void setRespCode(int respCode) {
        this.respCode = respCode;
    }

    public String getRespDesc() {
        return this.respDesc;
    }

    public void setRespDesc(String respDesc) {
        this.respDesc = respDesc;
    }

    @Override
    public String toString() {
        return "MessageMergeResponseEntity{RID=" + this.RID + ", respCode=" + this.respCode + ", respDesc='" + this.respDesc + "'" + "}";
    }
}
