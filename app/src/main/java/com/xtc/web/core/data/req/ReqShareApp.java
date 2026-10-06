package com.xtc.web.core.data.req;

/** H5 分享到第三方应用的请求。 */
public class ReqShareApp {

    private ReqShareAccount account;
    private String appName;
    private String desc;
    private String extInfo;
    private String icon;
    private String image;
    private String key;
    private ReqShareScene scene;
    private String startActivity;

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getExtInfo() {
        return this.extInfo;
    }

    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo;
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

    public String getStartActivity() {
        return this.startActivity;
    }

    public void setStartActivity(String startActivity) {
        this.startActivity = startActivity;
    }

    public ReqShareAccount getAccount() {
        return this.account;
    }

    public void setAccount(ReqShareAccount account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "ReqShareApp{key='" + this.key + "', image='" + this.image + "', desc='" + this.desc + "', extInfo='"
                + this.extInfo + "', appName='" + this.appName + "', icon='" + this.icon + "', startActivity='"
                + this.startActivity + "', scene=" + this.scene + ", account=" + this.account + '}';
    }
}