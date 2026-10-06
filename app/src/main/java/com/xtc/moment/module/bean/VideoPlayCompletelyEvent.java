package com.xtc.moment.module.bean;

/** Fired when a video finishes playing. */
public class VideoPlayCompletelyEvent {

    boolean videoFinish;

    public VideoPlayCompletelyEvent(boolean videoFinish) {
        this.videoFinish = videoFinish;
    }

    public boolean isVideoFinish() {
        return this.videoFinish;
    }

    public void setVideoFinish(boolean videoFinish) {
        this.videoFinish = videoFinish;
    }

    @Override
    public String toString() {
        return "VideoPlayCompletelyEvent{videoFinish=" + this.videoFinish + '}';
    }
}