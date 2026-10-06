package com.xtc.web.core.data;

/** H5 查询到的已安装应用信息。 */
public class AppInfo {

    private String appName;
    private String packageName;
    private int versionCode;
    private String versionName;

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return this.appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getVersionCode() {
        return this.versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return this.versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    @Override
    public String toString() {
        return "AppInfo{packageName='" + this.packageName + "', appName='" + this.appName + "', versionCode="
                + this.versionCode + ", versionName='" + this.versionName + "'}";
    }
}