package com.xtc.moment.module.report.bean;

/**
 * 举报内容里的视频资源。
 */
public class VideoResource {

    private String content;
    private int dialogType;
    private String picKey;
    private String videoKey;

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getDialogType() {
        return this.dialogType;
    }

    public void setDialogType(int dialogType) {
        this.dialogType = dialogType;
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

    @Override
    public String toString() {
        return "VideoResource{content='" + this.content + "', dialogType=" + this.dialogType + ", picKey='"
                + this.picKey + "', videoKey='" + this.videoKey + "'}";
    }
}