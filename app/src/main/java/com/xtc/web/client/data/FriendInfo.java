package com.xtc.web.client.data;

/** 好友信息，供 H5 分享选择好友时使用。 */
public class FriendInfo {

    private String icon;
    private String name;
    private String openId;

    public String getOpenId() {
        return this.openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "FriendInfo{openId='" + this.openId + "', name='" + this.name + "', icon='" + this.icon + "'}";
    }
}