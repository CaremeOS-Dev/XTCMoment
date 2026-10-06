package com.xtc.moment.module.bean;

import java.util.Arrays;

/**
 * 网页分享的发布态数据。
 */
public class ShareWebPublish {

    private String desc;
    private byte[] appIcon;
    private String appName;
    private String transaction;
    private String packageName;
    private String webLink;
    private CloudFileResource videoSource;
    private CloudFileResource videoThumbSource;

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

    public String getWebLink() {
        return this.webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    public CloudFileResource getVideoSource() {
        return this.videoSource;
    }

    public void setVideoSource(CloudFileResource videoSource) {
        this.videoSource = videoSource;
    }

    public CloudFileResource getVideoThumbSource() {
        return this.videoThumbSource;
    }

    public void setVideoThumbSource(CloudFileResource videoThumbSource) {
        this.videoThumbSource = videoThumbSource;
    }

    @Override
    public String toString() {
        return "ShareWebPublish{desc='" + this.desc + "', appIcon=" + Arrays.toString(this.appIcon) + ", appName='"
                + this.appName + "', transaction='" + this.transaction + "', packageName='" + this.packageName
                + "', webLink='" + this.webLink + "', videoSource=" + this.videoSource + ", videoThumbSource="
                + this.videoThumbSource + '}';
    }
}