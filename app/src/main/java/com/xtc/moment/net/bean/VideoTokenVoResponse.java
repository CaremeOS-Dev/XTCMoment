package com.xtc.moment.net.bean;

import com.xtc.moment.module.bean.CloudFileResource;
import com.xtc.moment.module.bean.PoiBean;

import java.util.Map;

/** Upload/download tokens and resource descriptors for a video. */
public class VideoTokenVoResponse {
    private String content;
    private Map<String, String> customParamMap;
    private int dialogType;
    private CloudFileResource icon;
    private String iconUploadToken;
    private long iconUploadTokenDeadLine;
    private String localThumbnailPath;
    private String localVideoPath;
    private PoiBean poiBean;
    private CloudFileResource source;
    private CloudFileResource transfer;
    private String type;
    private String uploadToken;
    private long uploadTokenDeadLine;
    private String videoName;
    private String wangSuUrl;
    private String zone;

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

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

    public void setDialogType(int dialogType) {
        this.dialogType = dialogType;
    }

    public int getDialogType() {
        return this.dialogType;
    }

    public String getLocalVideoPath() {
        return this.localVideoPath;
    }

    public void setLocalVideoPath(String localVideoPath) {
        this.localVideoPath = localVideoPath;
    }

    public String getLocalThumbnailPath() {
        return this.localThumbnailPath;
    }

    public void setLocalThumbnailPath(String localThumbnailPath) {
        this.localThumbnailPath = localThumbnailPath;
    }

    public PoiBean getPoiBean() {
        return this.poiBean;
    }

    public void setPoiBean(PoiBean poiBean) {
        this.poiBean = poiBean;
    }

    public String getVideoName() {
        return this.videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    @Override
    public String toString() {
        return "VideoTokenVo{type='" + this.type + "', wangSuUrl='" + this.wangSuUrl + "', zone='" + this.zone + "', uploadToken='" + this.uploadToken + "', uploadTokenDeadLine=" + this.uploadTokenDeadLine + ", iconUploadToken='" + this.iconUploadToken + "', iconUploadTokenDeadLine=" + this.iconUploadTokenDeadLine + ", source=" + this.source + ", icon=" + this.icon + ", transfer=" + this.transfer + ", customParamMap=" + this.customParamMap + ", dialogType=" + this.dialogType + ", localVideoPath=" + this.localVideoPath + ", content=" + this.content + ", localThumbnailPath=" + this.localThumbnailPath + ", poiBean=" + this.poiBean + '}';
    }

    /** Copies the fields needed when persisting the publish content. */
    public VideoTokenVoResponse toPublishContent(VideoTokenVoResponse videoTokenVoResponse) {
        VideoTokenVoResponse result = new VideoTokenVoResponse();
        result.setIcon(videoTokenVoResponse.getIcon());
        result.setSource(videoTokenVoResponse.getSource());
        result.setTransfer(videoTokenVoResponse.getTransfer());
        result.setIconUploadTokenDeadLine(videoTokenVoResponse.getIconUploadTokenDeadLine());
        result.setDialogType(videoTokenVoResponse.getDialogType());
        result.setType(videoTokenVoResponse.getType());
        result.setUploadTokenDeadLine(videoTokenVoResponse.getUploadTokenDeadLine());
        result.setZone(videoTokenVoResponse.getZone());
        return result;
    }
}
