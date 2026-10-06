package com.xtc.moment.module.bean;

/** A small preview of a cloud file. */
public class SmallPicSouce {

    private String downloadUrl;
    private String key;
    private long urlDeadline;

    public SmallPicSouce(String key, String downloadUrl, long urlDeadline) {
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

    @Override
    public String toString() {
        return "SmallPicSouce{key='" + this.key + "', downloadUrl='" + this.downloadUrl + "', urlDeadline="
                + this.urlDeadline + '}';
    }
}