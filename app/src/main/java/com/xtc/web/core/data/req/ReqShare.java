package com.xtc.web.core.data.req;

/** H5 通用分享请求。 */
public class ReqShare {

    private ReqShareAccount account;
    private String appName;
    private String content;
    private String desc;
    private String extInfo;
    private String icon;
    private String image;
    private String key;
    private String link;
    private int rtosSupport;
    private ReqShareScene scene;
    private int type;

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

    public ReqShareScene getScene() {
        return this.scene;
    }

    public void setScene(ReqShareScene scene) {
        this.scene = scene;
    }

    public ReqShareAccount getAccount() {
        return this.account;
    }

    public void setAccount(ReqShareAccount account) {
        this.account = account;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
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

    public int getRtosSupport() {
        return this.rtosSupport;
    }

    public void setRtosSupport(int rtosSupport) {
        this.rtosSupport = rtosSupport;
    }

    @Override
    public String toString() {
        return "ReqShare{key='" + this.key + "', content='" + this.content + "', appName='" + this.appName
                + "', icon='" + this.icon + "', scene=" + this.scene + ", account=" + this.account + ", image='"
                + this.image + "', link='" + this.link + "', desc='" + this.desc + "', type=" + this.type
                + ", extInfo='" + this.extInfo + "', rtosSupport='" + this.rtosSupport + "'}";
    }
}