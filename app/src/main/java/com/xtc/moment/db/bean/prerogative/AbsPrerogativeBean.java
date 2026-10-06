package com.xtc.moment.db.bean.prerogative;

import com.j256.ormlite.field.DatabaseField;

/** Common columns of the prerogative (paid decoration) tables. */
public class AbsPrerogativeBean {

    public static final String DB_PREROGATIVE_ID = "prerogativeId";

    @DatabaseField
    protected String emotionCode;

    @DatabaseField
    protected String emotionName;

    @DatabaseField
    protected int emotionType;

    @DatabaseField
    protected String emotionTypeDesc;

    @DatabaseField
    protected Long expireMins;

    @DatabaseField
    protected Long expireTime;

    @DatabaseField(generatedId = true)
    protected Integer id;

    @DatabaseField
    protected String localEmotionPath;

    @DatabaseField
    protected String netDynamicName;

    @DatabaseField
    protected String netDynamicUrl;

    @DatabaseField
    protected String netDynamicVersion;

    @DatabaseField(columnName = DB_PREROGATIVE_ID)
    protected int prerogativeId;

    @DatabaseField
    protected int useStatus;

    @DatabaseField
    protected Long useTime;

    public void setEmotionCode(String emotionCode) {
        this.emotionCode = emotionCode;
    }

    public void setPrerogativeId(int prerogativeId) {
        this.prerogativeId = prerogativeId;
    }

    public void setEmotionType(int emotionType) {
        this.emotionType = emotionType;
    }

    public void setLocalEmotionPath(String localEmotionPath) {
        this.localEmotionPath = localEmotionPath;
    }

    public void setEmotionTypeDesc(String emotionTypeDesc) {
        this.emotionTypeDesc = emotionTypeDesc;
    }

    public void setEmotionName(String emotionName) {
        this.emotionName = emotionName;
    }

    public String getEmotionCode() {
        return this.emotionCode;
    }

    public int getPrerogativeId() {
        return this.prerogativeId;
    }

    public int getEmotionType() {
        return this.emotionType;
    }

    public String getLocalEmotionPath() {
        return this.localEmotionPath;
    }

    public String getEmotionTypeDesc() {
        return this.emotionTypeDesc;
    }

    public String getEmotionName() {
        return this.emotionName;
    }

    public String getNetDynamicName() {
        return this.netDynamicName;
    }

    public void setNetDynamicName(String netDynamicName) {
        this.netDynamicName = netDynamicName;
    }

    public String getNetDynamicUrl() {
        return this.netDynamicUrl;
    }

    public void setNetDynamicUrl(String netDynamicUrl) {
        this.netDynamicUrl = netDynamicUrl;
    }

    public String getNetDynamicVersion() {
        return this.netDynamicVersion;
    }

    public void setNetDynamicVersion(String netDynamicVersion) {
        this.netDynamicVersion = netDynamicVersion;
    }

    public int getUseStatus() {
        return this.useStatus;
    }

    public void setUseStatus(int useStatus) {
        this.useStatus = useStatus;
    }

    public long getUseTime() {
        return this.useTime == null ? 0L : this.useTime;
    }

    public void setUseTime(Long useTime) {
        this.useTime = useTime;
    }

    public long getExpireMins() {
        return this.expireMins == null ? 0L : this.expireMins;
    }

    public void setExpireMins(Long expireMins) {
        this.expireMins = expireMins;
    }

    public long getExpireTime() {
        return this.expireTime == null ? 0L : this.expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    /** @return true when the prerogative is currently in use. */
    public boolean isUsing() {
        return getUseStatus() == 1;
    }

    /** @return true when the prerogative was never owned. */
    public boolean isNotHave() {
        return getUseStatus() == 0;
    }

    /** @return true when the prerogative is not in use. */
    public boolean isNoUsing() {
        return getUseStatus() == 2;
    }

    /** @return true when the prerogative expired. */
    public boolean isOverdue() {
        return getExpireTime() > 0 && getExpireTime() < System.currentTimeMillis();
    }
}