package com.xtc.moment.module.bean;

import java.util.List;

/**
 * 申请图片上传 token 的入参。
 */
public class PhotoTokenParam {

    public static String GIF_FORMAT = ".gif";
    public static String JPG_FORMAT = ".jpg";
    public static String PNG_FORMAT = ".png";
    public static String WEBP_FORMAT = ".webp";

    private String content;
    private String format;
    private String smallPicFormat;
    private String md5;
    private int dialogType;
    private List<Long> dialogIds;
    private String watchId;
    private boolean localPornCensorSwitch;

    public boolean isLocalPornCensorSwitch() {
        return this.localPornCensorSwitch;
    }

    public void setLocalPornCensorSwitch(boolean localPornCensorSwitch) {
        this.localPornCensorSwitch = localPornCensorSwitch;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getSmallPicFormat() {
        return this.smallPicFormat;
    }

    public void setSmallPicFormat(String smallPicFormat) {
        this.smallPicFormat = smallPicFormat;
    }

    public String getMd5() {
        return this.md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public int getDialogType() {
        return this.dialogType;
    }

    public void setDialogType(int dialogType) {
        this.dialogType = dialogType;
    }

    public List<Long> getDialogIds() {
        return this.dialogIds;
    }

    public void setDialogIds(List<Long> dialogIds) {
        this.dialogIds = dialogIds;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    @Override
    public String toString() {
        return "PhotoTokenParam{format='" + this.format + "', smallPicFormat='" + this.smallPicFormat + "', md5='"
                + this.md5 + "', dialogType=" + this.dialogType + ", dialogIds=" + this.dialogIds + ", watchId='"
                + this.watchId + "'}";
    }
}