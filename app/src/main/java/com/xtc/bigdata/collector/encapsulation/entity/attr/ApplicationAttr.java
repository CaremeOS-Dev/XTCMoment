package com.xtc.bigdata.collector.encapsulation.entity.attr;

import android.content.ContentValues;

import com.xtc.bigdata.collector.encapsulation.interfaces.IAttr;
import com.xtc.bigdata.common.db.constant.Columns;

/** Application attributes attached to every event. */
public class ApplicationAttr implements IAttr {
    private String appId = "";
    private String appVersion = "";
    private String packageName = "";
    private String moduleName = "";
    private String channleId = "";

    @Override
    public void insert(ContentValues contentValues) {
        contentValues.put(Columns.COLUMN_AA_APPID, this.appId);
        contentValues.put(Columns.COLUMN_AA_APPVER, this.appVersion);
        contentValues.put(Columns.COLUMN_AA_PACKAGENAME, this.packageName);
        contentValues.put(Columns.COLUMN_AA_MODULENAME, this.moduleName);
        contentValues.put(Columns.COLUMN_OA_CHANNELID, this.channleId);
    }

    @Override
    public IAttr clone() {
        ApplicationAttr applicationAttr = new ApplicationAttr();
        applicationAttr.appId = this.appId;
        applicationAttr.appVersion = this.appVersion;
        applicationAttr.packageName = this.packageName;
        applicationAttr.moduleName = this.moduleName;
        applicationAttr.channleId = this.channleId;
        return applicationAttr;
    }

    public ApplicationAttr setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public ApplicationAttr setAppVersion(String appVersion) {
        this.appVersion = appVersion;
        return this;
    }

    public ApplicationAttr setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public ApplicationAttr setModuleName(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    public ApplicationAttr setchannleId(String channleId) {
        this.channleId = channleId;
        return this;
    }

    public String getAppId() {
        return this.appId;
    }

    @Override
    public String toString() {
        return "ApplicationAttr{appId=\'" + this.appId + "\', appVersion=\'" + this.appVersion + "\', packageName=\'"
                + this.packageName + "\', moduleName=\'" + this.moduleName + "\', channleId=\'" + this.channleId + "\'}";
    }
}