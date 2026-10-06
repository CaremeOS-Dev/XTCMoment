package com.xtc.shareapi.share.bean;

/**
 * 数据库中保存的 apk 鉴权信息，记录包名、版本、证书及证书列表。
 */
public class DbApkInfo {

    private int id;
    private int versionCode;
    private String packageName;
    private String certificate;
    private Long createTime;
    private String certificateList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getCertificateList() {
        return certificateList;
    }

    public void setCertificateList(String certificateList) {
        this.certificateList = certificateList;
    }

    @Override
    public String toString() {
        return "DbApkInfo{id=" + id + ", versionCode=" + versionCode + ", packageName='" + packageName
                + "', certificate='" + certificate + "', createTime=" + createTime
                + ", certificateList=" + certificateList + '}';
    }
}