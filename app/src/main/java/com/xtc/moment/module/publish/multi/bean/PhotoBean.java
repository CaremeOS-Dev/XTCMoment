package com.xtc.moment.module.publish.multi.bean;

/** Paths of the photo/video pair picked for a multi picture publish. */
public class PhotoBean {

    private String photoPath;
    private int position;
    private String shareVideoPath;
    private String videoPath;

    public String getShareVideoPath() {
        return this.shareVideoPath;
    }

    public void setShareVideoPath(String shareVideoPath) {
        this.shareVideoPath = shareVideoPath;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getPhotoPath() {
        return this.photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getVideoPath() {
        return this.videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }
}