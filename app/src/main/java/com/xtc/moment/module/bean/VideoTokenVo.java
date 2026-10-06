package com.xtc.moment.module.bean;

import java.util.Map;

/**
 * 视频上传 token 的返回体。
 */
public class VideoTokenVo {

    private String type;
    private String wangSuUrl;
    private String zone;
    private String uploadToken;
    private long uploadTokenDeadLine;
    private String iconUploadToken;
    private long iconUploadTokenDeadLine;
    private CloudFileResource source;
    private CloudFileResource icon;
    private CloudFileResource transfer;
    private Map<String, String> customParamMap;

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWangSuUrl() {
        return this.wangSuUrl;
    }

    public void setWangSuUrl(String wangSuUrl) {
        this.wangSuUrl = wangSuUrl;
    }

    public String getZone() {
        return this.zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getUploadToken() {
        return this.uploadToken;
    }

    public void setUploadToken(String uploadToken) {
        this.uploadToken = uploadToken;
    }

    public long getUploadTokenDeadLine() {
        return this.uploadTokenDeadLine;
    }

    public void setUploadTokenDeadLine(long uploadTokenDeadLine) {
        this.uploadTokenDeadLine = uploadTokenDeadLine;
    }

    public String getIconUploadToken() {
        return this.iconUploadToken;
    }

    public void setIconUploadToken(String iconUploadToken) {
        this.iconUploadToken = iconUploadToken;
    }

    public long getIconUploadTokenDeadLine() {
        return this.iconUploadTokenDeadLine;
    }

    public void setIconUploadTokenDeadLine(long iconUploadTokenDeadLine) {
        this.iconUploadTokenDeadLine = iconUploadTokenDeadLine;
    }

    public CloudFileResource getSource() {
        return this.source;
    }

    public void setSource(CloudFileResource source) {
        this.source = source;
    }

    public CloudFileResource getIcon() {
        return this.icon;
    }

    public void setIcon(CloudFileResource icon) {
        this.icon = icon;
    }

    public CloudFileResource getTransfer() {
        return this.transfer;
    }

    public void setTransfer(CloudFileResource transfer) {
        this.transfer = transfer;
    }

    public Map<String, String> getCustomParamMap() {
        return this.customParamMap;
    }

    public void setCustomParamMap(Map<String, String> customParamMap) {
        this.customParamMap = customParamMap;
    }

    @Override
    public String toString() {
        return "VideoTokenVo{type='" + this.type + "', wangSuUrl='" + this.wangSuUrl + "', zone='" + this.zone
                + "', uploadToken='" + this.uploadToken + "', uploadTokenDeadLine=" + this.uploadTokenDeadLine
                + ", iconUploadToken='" + this.iconUploadToken + "', iconUploadTokenDeadLine="
                + this.iconUploadTokenDeadLine + ", source=" + this.source + ", icon=" + this.icon + ", transfer="
                + this.transfer + ", customParamMap=" + this.customParamMap + '}';
    }
}