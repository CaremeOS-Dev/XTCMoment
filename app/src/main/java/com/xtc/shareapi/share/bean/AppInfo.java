package com.xtc.shareapi.share.bean;

/**
 * 已注册分享能力的外部应用信息，包含包名、授权状态与访问 token。
 */
public class AppInfo {

    private String packageName;
    private int allow;
    private String token;

    public AppInfo() {
    }

    public AppInfo(String packageName, int allow, String token) {
        this.packageName = packageName;
        this.allow = allow;
        this.token = token;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getAllow() {
        return allow;
    }

    public void setAllow(int allow) {
        this.allow = allow;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "AppInfo{packageName='" + packageName + "', allow=" + allow + ", token='" + token + "'}";
    }
}