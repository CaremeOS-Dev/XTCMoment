package com.xtc.moment.module.publish.multi.bean;

import java.util.Arrays;

/** Parameters of a video published from a third party "fun" app. */
public class FunVideoParms {

    private String videoPath;
    private String videoParm;
    private String videoPackageName;
    private long videoLength;
    private byte[] videoIcon;
    private String videoAppName;
    private String videoExtraOutput;

    public String getVideoPath() {
        return this.videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getVideoParm() {
        return this.videoParm;
    }

    public void setVideoParm(String videoParm) {
        this.videoParm = videoParm;
    }

    public String getVideoPackageName() {
        return this.videoPackageName;
    }

    public void setVideoPackageName(String videoPackageName) {
        this.videoPackageName = videoPackageName;
    }

    public long getVideoLength() {
        return this.videoLength;
    }

    public void setVideoLength(long videoLength) {
        this.videoLength = videoLength;
    }

    public byte[] getVideoIcon() {
        return this.videoIcon;
    }

    public void setVideoIcon(byte[] videoIcon) {
        this.videoIcon = videoIcon;
    }

    public String getVideoAppName() {
        return this.videoAppName;
    }

    public void setVideoAppName(String videoAppName) {
        this.videoAppName = videoAppName;
    }

    public String getVideoExtraOutput() {
        return this.videoExtraOutput;
    }

    public void setVideoExtraOutput(String videoExtraOutput) {
        this.videoExtraOutput = videoExtraOutput;
    }

    @Override
    public String toString() {
        return "FunVideoParms{videoPath='" + this.videoPath + "', videoParm='" + this.videoParm
                + "', videoPackageName='" + this.videoPackageName + "', videoLength=" + this.videoLength
                + ", videoIcon=" + Arrays.toString(this.videoIcon) + ", videoAppName='" + this.videoAppName
                + "', videoExtraOutput='" + this.videoExtraOutput + "'}";
    }
}