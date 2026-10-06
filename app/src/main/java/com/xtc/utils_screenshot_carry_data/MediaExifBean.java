package com.xtc.utils_screenshot_carry_data;

import java.io.Serializable;

/** Exif metadata carried alongside a screenshot's media file. */
public class MediaExifBean implements Serializable {

    private static final long serialVersionUID = -3571405948730949356L;

    private String watchId;
    private String md5;
    private String resource;
    private String externalInfo;
    private int sourceType;

    public String getWatchId() {
        return watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getExternalInfo() {
        return externalInfo;
    }

    public void setExternalInfo(String externalInfo) {
        this.externalInfo = externalInfo;
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    @Override
    public String toString() {
        return "MediaExifBean{watchId='" + watchId + "', md5='" + md5 + "', resource='" + resource
                + "', externalInfo='" + externalInfo + "', sourceType=" + sourceType + '}';
    }
}