package com.xtc.virtualselfapi.bean.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/** A costume decoration owned by the account. */
@DatabaseTable(tableName = "decorate")
public class DbDecorate {

    /** Column names. */
    public interface Key {
        String DECORATE_ID = "costumeId";
    }

    @DatabaseField(columnName = "costumeId", id = true)
    private String costumeId;

    @DatabaseField
    private String costumeName;

    @DatabaseField
    private String createTime;

    @DatabaseField
    private String id;

    public DbDecorate() {
    }

    public DbDecorate(String costumeId) {
        this.costumeId = costumeId;
    }

    public void setCostumeId(String costumeId) {
        this.costumeId = costumeId;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setCostumeName(String costumeName) {
        this.costumeName = costumeName;
    }

    public String getCostumeId() {
        return this.costumeId;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public String getCostumeName() {
        return this.costumeName;
    }

    @Override
    public String toString() {
        return "DbDecorate{, costumeId='" + this.costumeId + "', createTime='" + this.createTime + "', costumeName='"
                + this.costumeName + "'}";
    }
}