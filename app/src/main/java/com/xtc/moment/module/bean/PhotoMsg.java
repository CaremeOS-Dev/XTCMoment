package com.xtc.moment.module.bean;

import java.util.ArrayList;
import java.util.Map;

/** One photo of a moment, either local or cloud backed. */
public class PhotoMsg {

    private String content;
    private Map<String, String> customParamMap;
    private int dialogType;
    private String localPath;
    private boolean photoHasDownload;
    private boolean photoHasUpload;
    private ArrayList<String> photoLocalPath;
    private int pictureIndex;
    private PoiBean poiBean;
    private String resource;
    private SmallPicSouce smallPic;
    private CloudFileResource source;
    private String trackMd5Value;
    private String type;
    private ArrayList<Long> urlDeadlines;
    private String wangSuUrl;
    private String zone;

    public int getPictureIndex() {
        return this.pictureIndex;
    }

    public void setPictureIndex(int pictureIndex) {
        this.pictureIndex = pictureIndex;
    }

    public String getResource() {
        return this.resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public ArrayList<String> getPhotoPathLoad() {
        return this.photoLocalPath;
    }

    public void setPhotoLocalPath(ArrayList<String> photoLocalPath) {
        this.photoLocalPath = photoLocalPath;
    }

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

    public CloudFileResource getSource() {
        return this.source;
    }

    public void setSource(CloudFileResource source) {
        this.source = source;
    }

    public String getLocalPath() {
        return this.localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public boolean isPhotoHasUpload() {
        return this.photoHasUpload;
    }

    public void setPhotoHasUpload(boolean photoHasUpload) {
        this.photoHasUpload = photoHasUpload;
    }

    public boolean isPhotoHasDownload() {
        return this.photoHasDownload;
    }

    public void setPhotoHasDownload(boolean photoHasDownload) {
        this.photoHasDownload = photoHasDownload;
    }

    public PoiBean getPoiBean() {
        return this.poiBean;
    }

    public void setPoiBean(PoiBean poiBean) {
        this.poiBean = poiBean;
    }

    public SmallPicSouce getSmallPic() {
        return this.smallPic;
    }

    public void setSmallPic(SmallPicSouce smallPic) {
        this.smallPic = smallPic;
    }

    public Map<String, String> getCustomParamMap() {
        return this.customParamMap;
    }

    public void setCustomParamMap(Map<String, String> customParamMap) {
        this.customParamMap = customParamMap;
    }

    public int getDialogType() {
        return this.dialogType;
    }

    public void setDialogType(int dialogType) {
        this.dialogType = dialogType;
    }

    public String getTrackMd5Value() {
        return this.trackMd5Value;
    }

    public void setTrackMd5Value(String trackMd5Value) {
        this.trackMd5Value = trackMd5Value;
    }

    public ArrayList<Long> getUrlDeadlines() {
        return this.urlDeadlines;
    }

    public void setUrlDeadlines(ArrayList<Long> urlDeadlines) {
        this.urlDeadlines = urlDeadlines;
    }

    @Override
    public String toString() {
        return "PhotoMsg{type='" + this.type + "', wangSuUrl='" + this.wangSuUrl + "', zone='" + this.zone
                + "', source=" + this.source + ", smallPic=" + this.smallPic + ", localPath='" + this.localPath
                + "', customParamMap=" + this.customParamMap + ", dialogType=" + this.dialogType
                + ", photoHasUpload=" + this.photoHasUpload + ", photoHasDownload=" + this.photoHasDownload
                + ", urlDeadlines=" + this.urlDeadlines + ", content=" + this.content + ", trackMd5Value="
                + this.trackMd5Value + ", poiBean=" + this.poiBean + '}';
    }
}