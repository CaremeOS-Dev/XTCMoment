package com.xtc.moment.module.bean;

/** A file stored in the cloud, with its key and a time-limited download URL. */
public class CloudFileResource {

    public static final int WIDTH = 320;
    public static final int HEIGHT = 360;

    private String downloadUrl;
    private String key;
    private long urlDeadline;
    private int width = WIDTH;
    private int height = HEIGHT;

    public CloudFileResource() {
    }

    public CloudFileResource(String key, String downloadUrl, long urlDeadline) {
        this.key = key;
        this.downloadUrl = downloadUrl;
        this.urlDeadline = urlDeadline;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDownloadUrl() {
        return this.downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public long getUrlDeadline() {
        return this.urlDeadline;
    }

    public void setUrlDeadline(long urlDeadline) {
        this.urlDeadline = urlDeadline;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "CloudFileResource{key='" + this.key + "', downloadUrl='" + this.downloadUrl + "', urlDeadline=" + this.urlDeadline + ", width=" + this.width + ", height=" + this.height + '}';
    }
}
