package com.xtc.im.core.common.request.entity;

import com.xtc.im.core.common.anotation.CommandValue;
import com.xtc.im.core.common.anotation.TagValue;

@CommandValue(5)
public class AccountRequestEntity extends RequestEntity {
    public static final int OFFLINE = 1;
    public static final int UPLINE = 2;


    @TagValue(1)
    private int RID;

    @TagValue(10)
    private long registId;

    @TagValue(11)
    private int businessType;

    @TagValue(12)
    private String businessId;

    @TagValue(13)
    private String businessToken;

    @TagValue(14)
    private int status;

    @TagValue(15)
    private String appKey;

    @TagValue(16)
    private int lowPower;

    @Override
    public int getRID() {
        return this.RID;
    }

    @Override
    public void setRID(int RID) {
        this.RID = RID;
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

    public String getBusinessId() {
        return this.businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getBusinessToken() {
        return this.businessToken;
    }

    public void setBusinessToken(String businessToken) {
        this.businessToken = businessToken;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAppKey() {
        return this.appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public int getLowPower() {
        return this.lowPower;
    }

    public void setLowPower(int lowPower) {
        this.lowPower = lowPower;
    }

    @Override
    public String toString() {
        return "AccountRequestEntity{RID=" + this.RID + ", registId=" + this.registId + ", businessType=" + this.businessType + ", businessId='" + this.businessId + "'" + ", businessToken='" + this.businessToken + "'" + ", status=" + this.status + ", appKey='" + this.appKey + "'" + ", lowPower=" + this.lowPower + "}";
    }
}
