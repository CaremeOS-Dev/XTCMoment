package com.xtc.web.client.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/** Cached H5 url of one web entry. */
@DatabaseTable(tableName = "web_url")
public class DbWebUrl {

    /** Column names. */
    public interface Key {
        String TYPE = "type";
    }

    @DatabaseField
    private String appPackageName;

    @DatabaseField
    private String h5Name;

    @DatabaseField
    private int id;

    @DatabaseField
    private String tag;

    @DatabaseField(id = true)
    private int type;

    @DatabaseField
    private String url;

    @DatabaseField
    private long version;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getH5Name() {
        return this.h5Name;
    }

    public void setH5Name(String h5Name) {
        this.h5Name = h5Name;
    }

    public String getAppPackageName() {
        return this.appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getVersion() {
        return this.version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "DbWebUrl{id=" + this.id + ", type=" + this.type + ", h5Name='" + this.h5Name + "', appPackageName='"
                + this.appPackageName + "', url='" + this.url + "', tag='" + this.tag + "', version=" + this.version
                + '}';
    }
}