package com.xtc.moment.module.bean;

import android.graphics.drawable.BitmapDrawable;

import com.opensource.svgaplayer.SVGAVideoEntity;

/**
 * 装扮数据：记录手表 id、装扮 id/路径，以及解码后的位图或 SVGA 动画实体。
 */
public class DressBean {

    private String watchId;
    private String dressId;
    private String dressPath;
    private BitmapDrawable dressBitmap;
    private SVGAVideoEntity dressEntity;
    private boolean isSvg;
    private int loopCount;

    public DressBean(String watchId, String dressId, String dressPath) {
        this.watchId = watchId;
        this.dressId = dressId;
        this.dressPath = dressPath;
    }

    public DressBean(String watchId, String dressId, String dressPath, boolean isSvg, int loopCount) {
        this.watchId = watchId;
        this.dressId = dressId;
        this.dressPath = dressPath;
        this.isSvg = isSvg;
        this.loopCount = loopCount;
    }

    public String getDressId() {
        return this.dressId;
    }

    public void setDressId(String dressId) {
        this.dressId = dressId;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public String getDressPath() {
        return this.dressPath;
    }

    public void setDressPath(String dressPath) {
        this.dressPath = dressPath;
    }

    public BitmapDrawable getDressBitmap() {
        return this.dressBitmap;
    }

    public void setDressBitmap(BitmapDrawable dressBitmap) {
        this.dressBitmap = dressBitmap;
    }

    public boolean isSvga() {
        return this.isSvg;
    }

    public void setSvga(boolean svga) {
        this.isSvg = svga;
    }

    public SVGAVideoEntity getDressEntity() {
        return this.dressEntity;
    }

    public void setDressEntity(SVGAVideoEntity dressEntity) {
        this.dressEntity = dressEntity;
    }

    public boolean isSvg() {
        return this.isSvg;
    }

    public void setSvg(boolean svg) {
        this.isSvg = svg;
    }

    public int getLoopCount() {
        return this.loopCount;
    }

    public void setLoopCount(int loopCount) {
        this.loopCount = loopCount;
    }

    @Override
    public String toString() {
        return "DressBean{watchId='" + this.watchId + "', dressId='" + this.dressId + "', dressPath='" + this.dressPath
                + "', dressBitmap=" + this.dressBitmap + ", dressEntity=" + this.dressEntity + ", isSvg=" + this.isSvg
                + ", loopCount=" + this.loopCount + '}';
    }
}