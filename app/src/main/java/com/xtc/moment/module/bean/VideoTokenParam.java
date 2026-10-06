package com.xtc.moment.module.bean;

/**
 * 申请视频上传 token 的入参。
 */
public class VideoTokenParam {

    public static String DEF_THUMNAIL_FORMAT = ".jpg";
    public static String MP4_FORMAT = ".mp4";
    public static String THUMNAIL_FORMAT = ".webp";
    public static int VIDEO_SEND_TYPE = 7;
    public static int VIDEO_TYPE = 5;
    public static final int VIDEO_TYPE_NORMAL_VIDEO = 0;
    public static final int VIDEO_TYPE_POINT_VIDEO = 1;

    private String content;
    private String format;
    private String iconFormat;
    private String md5;
    private int sendType;
    private String watchId;
    private int type;
    private int videoType;
    private boolean localPornCensorSwitch;

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isLocalPornCensorSwitch() {
        return this.localPornCensorSwitch;
    }

    public void setLocalPornCensorSwitch(boolean localPornCensorSwitch) {
        this.localPornCensorSwitch = localPornCensorSwitch;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getIconFormat() {
        return this.iconFormat;
    }

    public void setIconFormat(String iconFormat) {
        this.iconFormat = iconFormat;
    }

    public String getMd5() {
        return this.md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public int getSendType() {
        return this.sendType;
    }

    public void setSendType(int sendType) {
        this.sendType = sendType;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getVideoType() {
        return this.videoType;
    }

    public void setVideoType(int videoType) {
        this.videoType = videoType;
    }

    @Override
    public String toString() {
        return "VideoTokenParam{format='" + this.format + "', iconFormat='" + this.iconFormat + "', md5='" + this.md5
                + "', sendType='" + this.sendType + "', watchId='" + this.watchId + "', type=" + this.type
                + ", videoType=" + this.videoType + '}';
    }
}