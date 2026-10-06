package com.xtc.moment.module.prerogative.bean;

/**
 * 特权礼包信息。
 */
public class PackagesEntity {

    private int prerogativeBagId;
    private String watchId;
    private int id;

    public void setPrerogativeBagId(int prerogativeBagId) {
        this.prerogativeBagId = prerogativeBagId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrerogativeBagId() {
        return this.prerogativeBagId;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return "PackagesEntity{prerogativeBagId=" + this.prerogativeBagId + ", watchId='" + this.watchId + "', id="
                + this.id + '}';
    }
}