package com.xtc.virtualselfapi.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 动态虚拟形象资源信息，包含各部位装扮附件。
 */
public class DynamicsVirtualSelfBean implements Serializable {

    private String atlasResourceName;
    private String customSetName;
    private String customSetThumbnailUrl;
    private String customType;
    private int disPlayX;
    private int disPlayY;
    private int gender;
    private List<ResourceAttachmentBean> headDress;
    private List<ResourceAttachmentBean> lowerBodyDress;
    private List<ResourceAttachmentBean> shoesDress;
    private String skeletonResourceName;
    private List<ResourceAttachmentBean> upperBodyDress;
    private float zoomScale;

    public int getGender() {
        return this.gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getCustomSetThumbnailUrl() {
        return this.customSetThumbnailUrl;
    }

    public void setCustomSetThumbnailUrl(String customSetThumbnailUrl) {
        this.customSetThumbnailUrl = customSetThumbnailUrl;
    }

    public String getCustomSetName() {
        return this.customSetName;
    }

    public void setCustomSetName(String customSetName) {
        this.customSetName = customSetName;
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

    public int getDisPlayX() {
        return this.disPlayX;
    }

    public void setDisPlayX(int disPlayX) {
        this.disPlayX = disPlayX;
    }

    public int getDisPlayY() {
        return this.disPlayY;
    }

    public void setDisPlayY(int disPlayY) {
        this.disPlayY = disPlayY;
    }

    public float getZoomScale() {
        return this.zoomScale;
    }

    public void setZoomScale(float zoomScale) {
        this.zoomScale = zoomScale;
    }

    public String getCustomType() {
        return this.customType;
    }

    public void setCustomType(String customType) {
        this.customType = customType;
    }

    public List<ResourceAttachmentBean> getHeadDress() {
        return this.headDress;
    }

    public void setHeadDress(List<ResourceAttachmentBean> headDress) {
        this.headDress = headDress;
    }

    public List<ResourceAttachmentBean> getUpperBodyDress() {
        return this.upperBodyDress;
    }

    public void setUpperBodyDress(List<ResourceAttachmentBean> upperBodyDress) {
        this.upperBodyDress = upperBodyDress;
    }

    public List<ResourceAttachmentBean> getLowerBodyDress() {
        return this.lowerBodyDress;
    }

    public void setLowerBodyDress(List<ResourceAttachmentBean> lowerBodyDress) {
        this.lowerBodyDress = lowerBodyDress;
    }

    public List<ResourceAttachmentBean> getShoesDress() {
        return this.shoesDress;
    }

    public void setShoesDress(List<ResourceAttachmentBean> shoesDress) {
        this.shoesDress = shoesDress;
    }

    @Override
    public String toString() {
        return "DynamicsVirtualSelfBean{gender=" + this.gender + ", atlasResourceName='" + this.atlasResourceName
                + "', skeletonResourceName='" + this.skeletonResourceName + "', disPlayX=" + this.disPlayX
                + ", disPlayY=" + this.disPlayY + ", zoomScale=" + this.zoomScale + ", customType='"
                + this.customType + "', headDress=" + this.headDress + ", upperBodyDress=" + this.upperBodyDress
                + ", lowerBodyDress=" + this.lowerBodyDress + ", shoesDress=" + this.shoesDress
                + ", customSetThumbnailUrl='" + this.customSetThumbnailUrl + "', customSetName='"
                + this.customSetName + "'}";
    }
}