package com.xtc.moment.module.bean;

/** A live photo: a still image paired with a short video. */
public class LivePhotoMsg extends PhotoMsg {

    private VideoMsg videoMsg;

    public void setVideoMsg(VideoMsg videoMsg) {
        this.videoMsg = videoMsg;
    }

    public VideoMsg getVideoMsg() {
        return this.videoMsg;
    }

    @Override
    public String toString() {
        return "LivePhotoMsg{" + super.toString() + "videoMsg=" + this.videoMsg + '}';
    }
}