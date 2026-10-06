package com.xtc.moment.module.bean;

/** MD5 metadata of an uploaded photo. */
public class PhotoMD5Value {

    public String content;
    private String md5;
    private String trackMd5Value;
    private String watchId;

    public PhotoMD5Value() {
    }

    public PhotoMD5Value(String trackMd5Value) {
        this.trackMd5Value = trackMd5Value;
    }

    public String getMd5() {
        return this.md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTrackMd5Value() {
        return this.trackMd5Value;
    }

    public void setTrackMd5Value(String trackMd5Value) {
        this.trackMd5Value = trackMd5Value;
    }

    @Override
    public String toString() {
        return "PhotoMD5Value{trackMd5Value=" + this.trackMd5Value + '}';
    }
}