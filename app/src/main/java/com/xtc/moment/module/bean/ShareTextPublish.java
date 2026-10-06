package com.xtc.moment.module.bean;

/**
 * 纯文本分享的发布态数据。
 */
public class ShareTextPublish {

    private String content;
    private byte[] appIcon;
    private String appName;
    private String transaction;
    private String packageName;

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
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
        builder.append("ShareTextPublish{content='").append(this.content).append('\'');
        builder.append(", appIcon!=null").append(this.appIcon != null);
        builder.append(", appName='").append(this.appName).append('\'');
        builder.append(", transaction='").append(this.transaction).append('\'');
        builder.append(", packageName='").append(this.packageName).append('\'');
        builder.append('}');
        return builder.toString();
    }
}