package com.xtc.im.core.common.request.entity;

import com.xtc.im.core.common.anotation.CommandValue;
import com.xtc.im.core.common.anotation.TagValue;

@CommandValue(25)
public class SyncTriggerRequestEntity extends RequestEntity {

    @TagValue(1)
    private int RID;

    @TagValue(10)
    private long accountId;

    @TagValue(11)
    private String accountToken;

    @TagValue(12)
    private long registId;

    @TagValue(13)
    private String apnsType;

    @TagValue(14)
    private String apnsToken;

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

    public String getAccountToken() {
        return this.accountToken;
    }

    public void setAccountToken(String accountToken) {
        this.accountToken = accountToken;
    }

    public long getRegistId() {
        return this.registId;
    }

    public void setRegistId(long registId) {
        this.registId = registId;
    }

    public String getApnsType() {
        return this.apnsType;
    }

    public void setApnsType(String apnsType) {
        this.apnsType = apnsType;
    }

    public String getApnsToken() {
        return this.apnsToken;
    }

    public void setApnsToken(String apnsToken) {
        this.apnsToken = apnsToken;
    }

    @Override
    public String toString() {
        return "SyncTriggerRequestEntity{RID=" + this.RID + ", accountId=" + this.accountId + ", accountToken='" + this.accountToken + "'" + ", registId=" + this.registId + ", apnsType='" + this.apnsType + "'" + ", apnsToken='" + this.apnsToken + "'" + "}";
    }
}
