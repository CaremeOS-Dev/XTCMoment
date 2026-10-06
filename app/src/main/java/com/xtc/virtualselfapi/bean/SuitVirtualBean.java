package com.xtc.virtualselfapi.bean;

import java.io.Serializable;

/**
 * 套装虚拟形象资源信息。
 */
public class SuitVirtualBean implements Serializable {

    private String atlasResourceName;
    private String customSetName;
    private String customSetResourceName;
    private String customSetThumbnailUrl;
    private String customType;
    private int gender;
    private int newest;
    private String skeletonResourceName;

    public int getGender() {
        return this.gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getCustomSetName() {
        return this.customSetName;
    }

    public void setCustomSetName(String customSetName) {
        this.customSetName = customSetName;
    }

    public String getCustomSetThumbnailUrl() {
        return this.customSetThumbnailUrl;
    }

    public void setCustomSetThumbnailUrl(String customSetThumbnailUrl) {
        this.customSetThumbnailUrl = customSetThumbnailUrl;
    }

    public String getCustomSetResourceName() {
        return this.customSetResourceName;
    }

    public void setCustomSetResourceName(String customSetResourceName) {
        this.customSetResourceName = customSetResourceName;
    }

    public int getNewest() {
        return this.newest;
    }

    public void setNewest(int newest) {
        this.newest = newest;
    }

    public String getAtlasResourceName() {
        return this.atlasResourceName;
    }

    public void setAtlasResourceName(String atlasResourceName) {
        this.atlasResourceName = atlasResourceName;
    }

    public String getSkeletonResourceName() {
        return this.skeletonResourceName;
    }

    public void setSkeletonResourceName(String skeletonResourceName) {
        this.skeletonResourceName = skeletonResourceName;
    }

    public String getCustomType() {
        return this.customType;
    }

    public void setCustomType(String customType) {
        this.customType = customType;
    }

    @Override
    public String toString() {
        return "SuitVirtualBean{gender='" + this.gender + "', customSetName='" + this.customSetName
                + "', customSetThumbnailUrl='" + this.customSetThumbnailUrl + "', customSetResourceName='"
                + this.customSetResourceName + "', newest=" + this.newest + ", atlasResourceName='"
                + this.atlasResourceName + "', skeletonResourceName='" + this.skeletonResourceName
                + "', customType='" + this.customType + "'}";
    }
}