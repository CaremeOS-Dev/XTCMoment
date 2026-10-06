package com.xtc.moment.share.model.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/** One app allowed to share into the moment. */
@DatabaseTable(tableName = "app_share")
public class DbAppShare {

    /** Values of the {@code allow} column. */
    public interface AllowType {
        int ALLOW = 1;
        int NOT_ALLOW = -1;
        int NOT_EXIST = 0;
    }

    /** Column names. */
    public interface Name {
        String APP_KEY = "appKey";
        String PACKAGE_NAME = "packageName";
    }

    @DatabaseField
    private int allow;

    @DatabaseField
    private String appKey;

    @DatabaseField
    private long deadline;

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private int maxTimes;

    @DatabaseField(unique = true)
    private String packageName;

    @DatabaseField
    private int times;

    @DatabaseField
    private String token;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getDeadline() {
        return this.deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public int getTimes() {
        return this.times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public int getMaxTimes() {
        return this.maxTimes;
    }

    public void setMaxTimes(int maxTimes) {
        this.maxTimes = maxTimes;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAppKey() {
        return this.appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public int getAllow() {
        return this.allow;
    }

    public void setAllow(int allow) {
        this.allow = allow;
    }

    @Override
    public String toString() {
        return "DbAppShare{id=" + this.id + ", packageName='" + this.packageName + "', deadline=" + this.deadline
                + ", times=" + this.times + ", maxTimes=" + this.maxTimes + ", token='" + this.token + "', appKey='"
                + this.appKey + "', allow=" + this.allow + '}';
    }
}