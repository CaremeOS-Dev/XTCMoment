package com.xtc.moment.serve.bean;

/**
 * 单文件下载地址请求参数。
 */
public class FileUrlParam {

    public static final String TYPE_QN = "1";
    public static final String TYPE_WS = "2";
    public static final int MOMENT_DOWNLOAD_TYPE = 1;

    private String key;
    private String type;
    private int downloadType = MOMENT_DOWNLOAD_TYPE;

    public FileUrlParam(String key, String type) {
        this.key = key;
        this.type = type;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDownloadType() {
        return this.downloadType;
    }

    public void setDownloadType(int downloadType) {
        this.downloadType = downloadType;
    }

    @Override
    public String toString() {
        return "FileUrlParam{key='" + this.key + "', type='" + this.type + "', downloadType=" + this.downloadType + '}';
    }
}