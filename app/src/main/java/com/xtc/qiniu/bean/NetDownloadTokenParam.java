package com.xtc.qiniu.bean;

/** Request body of the download-token endpoint. */
public class NetDownloadTokenParam {

    private int fileType;
    private String format;
    private java.util.List<String> keys;
    private Integer longEdge;
    private Integer quality;
    private Integer shortEdge;

    public int getFileType() {
        return this.fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public java.util.List<String> getKeys() {
        return this.keys;
    }

    public void setKeys(java.util.List<String> keys) {
        this.keys = keys;
    }

    public Integer getLongEdge() {
        return this.longEdge;
    }

    public void setLongEdge(Integer longEdge) {
        this.longEdge = longEdge;
    }

    public Integer getShortEdge() {
        return this.shortEdge;
    }

    public void setShortEdge(Integer shortEdge) {
        this.shortEdge = shortEdge;
    }

    public Integer getQuality() {
        return this.quality;
    }

    public void setQuality(Integer quality) {
        this.quality = quality;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}