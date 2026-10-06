package com.xtc.moment.module.bean;

import java.util.Map;

/**
 * 图片上传 token 的返回体。
 */
public class PhotoTokenVo {

    private String type;
    private String wangSuUrl;
    private String zone;
    private String uploadToken;
    private long uploadTokenDeadLine;
    private CloudFileResource source;
    private SmallPicSouce smallPic;
    private Map<String, String> customParamMap;
    private String text;

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
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

    public CloudFileResource getSource() {
        return this.source;
    }

    public void setSource(CloudFileResource source) {
        this.source = source;
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

    @Override
    public String toString() {
        return "PhotoTokenVo{type='" + this.type + "', wangSuUrl=" + this.wangSuUrl + ", zone='" + this.zone
                + "', uploadToken='" + this.uploadToken + "', uploadTokenDeadLine=" + this.uploadTokenDeadLine
                + ", source=" + this.source + ", customParamMap=" + this.customParamMap + '}';
    }
}