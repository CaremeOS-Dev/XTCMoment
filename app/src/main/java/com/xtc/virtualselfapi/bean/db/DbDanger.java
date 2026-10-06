package com.xtc.virtualselfapi.bean.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/** One danger animation of the virtual self. */
@DatabaseTable(tableName = "danger")
public class DbDanger {

    /** Column names. */
    public interface Key {
        String ID = "id";
    }

    @DatabaseField
    private String animInfo;

    @DatabaseField
    private String desc;

    @DatabaseField(id = true)
    private int id;

    @DatabaseField
    private String info;

    @DatabaseField
    private String name;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getInfo() {
        return this.info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getAnimInfo() {
        return this.animInfo;
    }

    public void setAnimInfo(String animInfo) {
        this.animInfo = animInfo;
    }

    @Override
    public String toString() {
        return "DbDanger{id=" + this.id + ", name='" + this.name + "', desc='" + this.desc + "', info='" + this.info
                + "', animInfo='" + this.animInfo + "'}";
    }
}