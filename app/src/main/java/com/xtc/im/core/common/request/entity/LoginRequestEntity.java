package com.xtc.im.core.common.request.entity;

import com.xtc.im.core.common.anotation.CommandValue;
import com.xtc.im.core.common.anotation.TagValue;

@CommandValue(3)
public class LoginRequestEntity extends RequestEntity {

    @TagValue(1)
    private int RID;

    @TagValue(10)
    private long registId;

    @TagValue(11)
    private String registToken;

    @TagValue(12)
    private long sdkVersion;

    @TagValue(13)
    private long apnsType;

    @TagValue(14)
    private String apnsToken;

    @TagValue(15)
    private String deviceToken;

    @TagValue(16)
    private String imSdkVersionName;

    @TagValue(17)
    private int pushType;

    @TagValue(18)
    private int platform;

    @TagValue(21)
    private long timestamp;

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

    public String getRegistToken() {
        return this.registToken;
    }

    public void setRegistToken(String registToken) {
        this.registToken = registToken;
    }

    public long getSdkVersion() {
        return this.sdkVersion;
    }

    public void setSdkVersion(long sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public long getApnsType() {
        return this.apnsType;
    }

    public void setApnsType(long apnsType) {
        this.apnsType = apnsType;
    }

    public String getApnsToken() {
        return this.apnsToken;
    }

    public void setApnsToken(String apnsToken) {
        this.apnsToken = apnsToken;
    }

    public String getDeviceToken() {
        return this.deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getImSdkVersionName() {
        return this.imSdkVersionName;
    }

    public void setImSdkVersionName(String imSdkVersionName) {
        this.imSdkVersionName = imSdkVersionName;
    }

    public int getPushType() {
        return this.pushType;
    }

    public void setPushType(int pushType) {
        this.pushType = pushType;
    }

    public int getPlatform() {
        return this.platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "LoginRequestEntity{RID=" + this.RID + ", registId=" + this.registId + ", registToken='" + this.registToken + "'" + ", sdkVersion=" + this.sdkVersion + ", apnsType=" + this.apnsType + ", apnsToken='" + this.apnsToken + "'" + ", deviceToken='" + this.deviceToken + "'" + ", imSdkVersionName='" + this.imSdkVersionName + "'" + ", pushType=" + this.pushType + ", platform=" + this.platform + ", timestamp=" + this.timestamp + "}";
    }
}
