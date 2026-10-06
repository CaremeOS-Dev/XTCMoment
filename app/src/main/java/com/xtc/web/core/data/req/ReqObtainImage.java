package com.xtc.web.core.data.req;

/** 获取本地图片（按指定宽高压缩）的请求。 */
public class ReqObtainImage {

    private int height;
    private String path;
    private int width;

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "ReqObtainImage{path='" + this.path + "', width=" + this.width + ", height=" + this.height + '}';
    }
}