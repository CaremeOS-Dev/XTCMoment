package com.xtc.contactapi.contacthead.bean;

import android.graphics.drawable.BitmapDrawable;

/**
 * 联系人头像装扮数据。
 */
public class DressBean {

    private String watchId;
    private String dressId;
    private String dressPath;
    private BitmapDrawable dressBitmap;
    private boolean isGif;

    public DressBean() {
    }

    public DressBean(String watchId, String dressId, String dressPath) {
        this.watchId = watchId;
        this.dressId = dressId;
        this.dressPath = dressPath;
    }

    public String getDressId() {
        return dressId;
    }

    public void setDressId(String dressId) {
        this.dressId = dressId;
    }

    public String getWatchId() {
        return watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public String getDressPath() {
        return dressPath;
    }

    public void setDressPath(String dressPath) {
        this.dressPath = dressPath;
    }

    public BitmapDrawable getDressBitmap() {
        return dressBitmap;
    }

    public void setDressBitmap(BitmapDrawable dressBitmap) {
        this.dressBitmap = dressBitmap;
    }

    public boolean isGif() {
        return isGif;
    }

    public void setGif(boolean gif) {
        isGif = gif;
    }

    @Override
    public String toString() {
        return "DressBean{watchId='" + watchId + "', dressId='" + dressId + "', dressPath='" + dressPath
                + "', dressBitmap=" + dressBitmap + ", isGif=" + isGif + '}';
    }
}