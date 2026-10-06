package com.xtc.game.engine.bean;

import java.util.List;

/**
 * 待渲染实体：描述一套骨骼资源的图集、骨骼文件与默认皮肤信息。
 */
public class NeedRenderEntity {

    private String atlasPath;
    private String skeletonPath;
    private String defaultSkinName;
    private List<SlotAttachmentBean> defaultAttachments;
    private int x;
    private int y;
    private float scale;

    public NeedRenderEntity(String atlasPath, String skeletonPath, int x, int y, float scale) {
        this.atlasPath = atlasPath;
        this.skeletonPath = skeletonPath;
        this.x = x;
        this.y = y;
        this.scale = scale;
    }

    public String getAtlasPath() {
        return this.atlasPath;
    }

    public void setAtlasPath(String atlasPath) {
        this.atlasPath = atlasPath;
    }

    public String getSkeletonPath() {
        return this.skeletonPath;
    }

    public void setSkeletonPath(String skeletonPath) {
        this.skeletonPath = skeletonPath;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getDefaultSkinName() {
        return this.defaultSkinName;
    }

    public void setDefaultSkinName(String defaultSkinName) {
        this.defaultSkinName = defaultSkinName;
        List<SlotAttachmentBean> attachments = this.defaultAttachments;
        if (attachments == null || attachments.size() <= 0) {
            return;
        }
        this.defaultAttachments.clear();
    }

    public List<SlotAttachmentBean> getDefaultAttachments() {
        return this.defaultAttachments;
    }

    public void setDefaultAttachments(List<SlotAttachmentBean> defaultAttachments) {
        this.defaultAttachments = defaultAttachments;
    }

    public float getScale() {
        return this.scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    @Override
    public String toString() {
        return "NeedRenderEntity{atlasPath='" + this.atlasPath + "', skeletenPath='" + this.skeletonPath
                + "', defaultSkinName='" + this.defaultSkinName + "', defaultAttachments=" + this.defaultAttachments
                + ", x=" + this.x + ", y=" + this.y + ", scale=" + this.scale + '}';
    }
}