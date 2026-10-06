package com.xtc.moment.module.bean;

/**
 * 应用分享的发布态数据，字段与 {@link ShareAppMoment} 一致。
 */
public class ShareAppPublish {

    private String desc;
    private byte[] appIcon;
    private String appName;
    private String targetPackage;
    private String targetClass;
    private String extInfo;
    private String action;
    private String transaction;
    private String packageName;

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public byte[] getAppIcon() {
        return this.appIcon;
    }

    public void setAppIcon(byte[] appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppName() {
        return this.appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getTargetPackage() {
        return this.targetPackage;
    }

    public void setTargetPackage(String targetPackage) {
        this.targetPackage = targetPackage;
    }

    public String getTargetClass() {
        return this.targetClass;
    }

    public void setTargetClass(String targetClass) {
        this.targetClass = targetClass;
    }

    public String getExtInfo() {
        return this.extInfo;
    }

    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTransaction() {
        return this.transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ShareAppPublish{desc='").append(this.desc).append('\'');
        builder.append(", appIcon!=null").append(this.appIcon != null);
        builder.append(", appName='").append(this.appName).append('\'');
        builder.append(", targetPackage='").append(this.targetPackage).append('\'');
        builder.append(", targetClass='").append(this.targetClass).append('\'');
        builder.append(", extInfo='").append(this.extInfo).append('\'');
        builder.append(", action='").append(this.action).append('\'');
        builder.append(", transaction='").append(this.transaction).append('\'');
        builder.append(", packageName='").append(this.packageName).append('\'');
        builder.append('}');
        return builder.toString();
    }
}