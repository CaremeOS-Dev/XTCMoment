package com.xtc.web.core.data.req;

/** 保存 base64 图片到本地路径的请求。 */
public class ReqSaveImage {

    private String image;
    private String name;
    private String path;

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ReqSaveImage{path='" + this.path + "', image='" + this.image + "', name='" + this.name + "'}";
    }
}