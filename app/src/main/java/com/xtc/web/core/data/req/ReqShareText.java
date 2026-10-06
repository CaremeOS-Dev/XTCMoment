package com.xtc.web.core.data.req;

/** H5 分享文本请求。 */
public class ReqShareText {

    private ReqShareAccount account;
    private String appName;
    private String content;
    private String icon;
    private String key;
    private ReqShareScene scene;

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
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
        return "ReqShareText{key='" + this.key + "', content='" + this.content + "', appName='" + this.appName
                + "', icon='" + this.icon + "', scene=" + this.scene + ", account=" + this.account + '}';
    }
}