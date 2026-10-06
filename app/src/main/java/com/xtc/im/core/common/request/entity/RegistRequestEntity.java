package com.xtc.im.core.common.request.entity;

import com.xtc.im.core.common.anotation.CommandValue;
import com.xtc.im.core.common.anotation.TagValue;

@CommandValue(1)
public class RegistRequestEntity extends RequestEntity {

    @TagValue(1)
    private int RID;

    @TagValue(10)
    private int platform;

    @TagValue(11)
    private String appKey;

    @TagValue(12)
    private String pkgName;

    @TagValue(13)
    private String deviceId;

    @TagValue(14)
    private int sdkVerison;

    @TagValue(15)
    private String sysName;

    @TagValue(16)
    private String sysVersion;

    @TagValue(17)
    private String imei;

    @TagValue(18)
    private String imsi;

    @TagValue(19)
    private String mac;

    @TagValue(20)
    private String modelNumber;

    @TagValue(21)
    private String basebandVersion;

    @TagValue(22)
    private String buildNumber;

    @TagValue(23)
    private String resolution;

    @Override
    public int getRID() {
        return this.RID;
    }

    @Override
    public void setRID(int RID) {
        this.RID = RID;
    }

    public int getPlatform() {
        return this.platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public String getAppKey() {
        return this.appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getPkgName() {
        return this.pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getSdkVerison() {
        return this.sdkVerison;
    }

    public void setSdkVerison(int sdkVerison) {
        this.sdkVerison = sdkVerison;
    }

    public String getSysName() {
        return this.sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getSysVersion() {
        return this.sysVersion;
    }

    public void setSysVersion(String sysVersion) {
        this.sysVersion = sysVersion;
    }

    public String getImei() {
        return this.imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getImsi() {
        return this.imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getMac() {
        return this.mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getModelNumber() {
        return this.modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public String getBasebandVersion() {
        return this.basebandVersion;
    }

    public void setBasebandVersion(String basebandVersion) {
        this.basebandVersion = basebandVersion;
    }

    public String getBuildNumber() {
        return this.buildNumber;
    }

    public void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }

    public String getResolution() {
        return this.resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    @Override
    public String toString() {
        return "RegistRequestEntity{RID=" + this.RID + ", platform=" + this.platform + ", appKey='" + this.appKey + "'" + ", pkgName='" + this.pkgName + "'" + ", deviceId='" + this.deviceId + "'" + ", sdkVerison=" + this.sdkVerison + ", sysName='" + this.sysName + "'" + ", sysVersion='" + this.sysVersion + "'" + ", imei='" + this.imei + "'" + ", imsi='" + this.imsi + "'" + ", mac='" + this.mac + "'" + ", modelNumber='" + this.modelNumber + "'" + ", basebandVersion='" + this.basebandVersion + "'" + ", buildNumber='" + this.buildNumber + "'" + ", resolution='" + this.resolution + "'" + "}";
    }
}
