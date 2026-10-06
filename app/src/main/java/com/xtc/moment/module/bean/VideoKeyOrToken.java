package com.xtc.moment.module.bean;

/**
 * 视频上传后拿到的 key/token 组合。
 */
public class VideoKeyOrToken {

    private String picKey;
    private String videoKey;
    private String tansferKey;
    private int transferCode;
    private String content;
    private int dialogType;
    private String loadPath;

    public void setLoadPath(String loadPath) {
        this.loadPath = loadPath;
    }

    public String getLoadPath() {
        return this.loadPath;
    }

    public int getDialogType() {
        return this.dialogType;
    }

    public void setDialogType(int dialogType) {
        this.dialogType = dialogType;
    }

    public int getTransferCode() {
        return this.transferCode;
    }

    public void setTransferCode(int transferCode) {
        this.transferCode = transferCode;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public VideoKeyOrToken(String picKey, String videoKey, String tansferKey) {
        this.picKey = picKey;
        this.videoKey = videoKey;
        this.tansferKey = tansferKey;
    }

    public VideoKeyOrToken() {
    }

    public String getPicKey() {
        return this.picKey;
    }

    public void setPicKey(String picKey) {
        this.picKey = picKey;
    }

    public String getVideoKey() {
        return this.videoKey;
    }

    public void setVideoKey(String videoKey) {
        this.videoKey = videoKey;
    }

    public String getTansferKey() {
        return this.tansferKey;
    }

    public void setTansferKey(String tansferKey) {
        this.tansferKey = tansferKey;
    }

    @Override
    public String toString() {
        return "VideoKeyOrToken{picKey='" + this.picKey + "', videoKey='" + this.videoKey + "', tansferKey='"
                + this.tansferKey + "', transferCode='" + this.transferCode + "'}";
    }
}