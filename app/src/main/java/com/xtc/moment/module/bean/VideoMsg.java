package com.xtc.moment.module.bean;

import java.util.Map;

/** One video of a moment together with its thumbnail. */
public class VideoMsg {

    private String content;
    private Map<String, String> customParamMap;
    private CloudFileResource icon;
    private boolean isFromAlbum;
    private String localThumbnailPath;
    private String localVideoPath;
    private PoiBean poiBean;
    private CloudFileResource source;
    private boolean thumnailHasDownload;
    private boolean thumnailHasUpload;
    private CloudFileResource transfer;
    private String type;
    private boolean videoHasDownload;
    private boolean videoHasUpload;
    private float videoLength;
    private String wangSuUrl;
    private String zone;

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public PoiBean getPoiBean() {
        return this.poiBean;
    }

    public void setPoiBean(PoiBean poiBean) {
        this.poiBean = poiBean;
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

    public boolean getVideoHasDownload() {
        return this.videoHasDownload;
    }

    public void setVideoHasDownload(boolean videoHasDownload) {
        this.videoHasDownload = videoHasDownload;
    }

    public boolean getThumnailHasDownload() {
        return this.thumnailHasDownload;
    }

    public void setThumnailHasDownload(boolean thumnailHasDownload) {
        this.thumnailHasDownload = thumnailHasDownload;
    }

    public boolean getVideoHasUpload() {
        return this.videoHasUpload;
    }

    public void setVideoHasUpload(boolean videoHasUpload) {
        this.videoHasUpload = videoHasUpload;
    }

    public boolean getThumnailHasUpload() {
        return this.thumnailHasUpload;
    }

    public void setThumnailHasUpload(boolean thumnailHasUpload) {
        this.thumnailHasUpload = thumnailHasUpload;
    }

    public float getVideoLength() {
        return this.videoLength;
    }

    public void setVideoLength(float videoLength) {
        this.videoLength = videoLength;
    }

    public boolean isFromAlbum() {
        return this.isFromAlbum;
    }

    public void setFromAlbum(boolean fromAlbum) {
        this.isFromAlbum = fromAlbum;
    }

    @Override
    public String toString() {
        return "VideoMsg{type='" + this.type + "', wangSuUrl='" + this.wangSuUrl + "', zone='" + this.zone
                + "', source=" + this.source + ", icon=" + this.icon + ", transfer=" + this.transfer
                + ", customParamMap=" + this.customParamMap + ", localVideoPath='" + this.localVideoPath
                + "', localThumbnailPath='" + this.localThumbnailPath + "', videoHasDownload=" + this.videoHasDownload
                + ", thumnailHasDownload=" + this.thumnailHasDownload + ", videoHasUpload=" + this.videoHasUpload
                + ", thumnailHasUpload=" + this.thumnailHasUpload + ", videoLength=" + this.videoLength
                + ", isFromAlbum=" + this.isFromAlbum + ", poiBean=" + this.poiBean + '}';
    }
}