package com.xtc.im.core.common.request.entity;

import com.xtc.im.core.common.anotation.CommandValue;
import com.xtc.im.core.common.anotation.TagValue;

@CommandValue(22)
public class PublicKeyRequestEntity extends RequestEntity {

    @TagValue(1)
    private int RID;

    @Override
    public int getRID() {
        return this.RID;
    }

    @Override
    public void setRID(int RID) {
        this.RID = RID;
    }

    @Override
    public String toString() {
        return "PublicKeyRequestEntity{RID=" + this.RID + "}";
    }
}
