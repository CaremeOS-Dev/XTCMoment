package com.xtc.virtualselfapi.bean.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/** One virtual-self costume. */
@DatabaseTable(tableName = "costume")
public class DbCostume {

    /** Column names. */
    public interface Key {
        String ID = "costumeId";
        String TYPE = "type";
    }

    @DatabaseField
    private int boxType;

    @DatabaseField(unique = true)
    private int costumeId;

    @DatabaseField
    private int costumeType;

    @DatabaseField
    private int cutBottom;

    @DatabaseField
    private int cutTop;

    @DatabaseField
    private int debris;

    @DatabaseField
    private int gender;

    @DatabaseField
    private int gold;

    @DatabaseField(id = true)
    private int id;

    @DatabaseField
    private int layer;

    @DatabaseField
    private String name;

    @DatabaseField
    private int paddingBottom;

    @DatabaseField
    private int paddingEnd;

    @DatabaseField
    private int paddingStart;

    @DatabaseField
    private int paddingTop;

    @DatabaseField
    private String preUrl;

    @DatabaseField
    private int sort;

    @DatabaseField
    private int subType;

    @DatabaseField
    private int type;

    @DatabaseField
    private String url;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCostumeId() {
        return this.costumeId;
    }

    public void setCostumeId(int costumeId) {
        this.costumeId = costumeId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGender() {
        return this.gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDebris() {
        return this.debris;
    }

    public void setDebris(int debris) {
        this.debris = debris;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSubType() {
        return this.subType;
    }

    public void setSubType(int subType) {
        this.subType = subType;
    }

    public int getLayer() {
        return this.layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public int getCutTop() {
        return this.cutTop;
    }

    public void setCutTop(int cutTop) {
        this.cutTop = cutTop;
    }

    public int getCutBottom() {
        return this.cutBottom;
    }

    public void setCutBottom(int cutBottom) {
        this.cutBottom = cutBottom;
    }

    public int getPaddingStart() {
        return this.paddingStart;
    }

    public void setPaddingStart(int paddingStart) {
        this.paddingStart = paddingStart;
    }

    public int getPaddingEnd() {
        return this.paddingEnd;
    }

    public void setPaddingEnd(int paddingEnd) {
        this.paddingEnd = paddingEnd;
    }

    public int getPaddingTop() {
        return this.paddingTop;
    }

    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
    }

    public int getPaddingBottom() {
        return this.paddingBottom;
    }

    public void setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public int getBoxType() {
        return this.boxType;
    }

    public void setBoxType(int boxType) {
        this.boxType = boxType;
    }

    public int getGold() {
        return this.gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public String getPreUrl() {
        return this.preUrl;
    }

    public void setPreUrl(String preUrl) {
        this.preUrl = preUrl;
    }

    public int getSort() {
        return this.sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getCostumeType() {
        return this.costumeType;
    }

    public void setCostumeType(int costumeType) {
        this.costumeType = costumeType;
    }

    @Override
    public String toString() {
        return "DbCostume{id=" + this.id + ", costumeId=" + this.costumeId + ", name='" + this.name + "', gender="
                + this.gender + ", url='" + this.url + "', preUrl='" + this.preUrl + "', sort=" + this.sort
                + ", debris=" + this.debris + ", type=" + this.type + ", subType=" + this.subType + ", layer="
                + this.layer + ", boxType=" + this.boxType + ", cutTop=" + this.cutTop + ", cutBottom="
                + this.cutBottom + ", paddingStart=" + this.paddingStart + ", paddingEnd=" + this.paddingEnd
                + ", paddingTop=" + this.paddingTop + ", paddingBottom=" + this.paddingBottom + ", gold=" + this.gold
                + ", costumeType=" + this.costumeType + '}';
    }
}