package com.xtc.im.core.common.request.entity;

import com.xtc.im.core.common.anotation.CommandValue;
import com.xtc.im.core.common.anotation.TagValue;

@CommandValue(44)
public class ModeRequestEntity extends RequestEntity {

    @TagValue(1)
    private int RID;

    @TagValue(2)
    private long accountId;

    @TagValue(3)
    private long registId;

    @TagValue(4)
    private int businessType;

    @TagValue(5)
    private String params;

    @Override
    public int getRID() {
        return this.RID;
    }

    @Override
    public void setRID(int RID) {
        this.RID = RID;
    }

    public long getAccountId() {
        return this.accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public long getRegistId() {
        return this.registId;
    }

    public void setRegistId(long registId) {
        this.registId = registId;
    }

    public int getBusinessType() {
        return this.businessType;
    }

    public void setBusinessType(int businessType) {
        this.businessType = businessType;
    }

    public String getParams() {
        return this.params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "ModeRequestEntity{RID=" + this.RID + ", accountId=" + this.accountId + ", registId=" + this.registId + ", businessType=" + this.businessType + ", params='" + this.params + "'" + "}";
    }
}
