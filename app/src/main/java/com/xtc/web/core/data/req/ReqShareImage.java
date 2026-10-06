package com.xtc.web.core.data.req;

/** H5 分享图片请求。 */
public class ReqShareImage {

    private ReqShareAccount account;
    private String appName;
    private String icon;
    private String image;
    private String key;
    private ReqShareScene scene;

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ReqShareScene getScene() {
        return this.scene;
    }

    public void setScene(ReqShareScene scene) {
        this.scene = scene;
    }

    public String getAppName() {
        return this.appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public ReqShareAccount getAccount() {
        return this.account;
    }

    public void setAccount(ReqShareAccount account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "ReqShareImage{key='" + this.key + "', image='" + this.image + "', appName='" + this.appName
                + "', icon='" + this.icon + "', scene=" + this.scene + ", account=" + this.account + '}';
    }
}