package com.xtc.game.engine.bean;

/**
 * 支持骨骼缩放的待渲染实体。
 */
public class ScaleNeedRenderEntity extends NeedRenderEntity {

    private float scaleX = 1.0f;
    private float scaleY = 1.0f;

    public ScaleNeedRenderEntity(String atlasPath, String skeletonPath, int x, int y, float scale) {
        super(atlasPath, skeletonPath, x, y, scale);
    }

    public ScaleNeedRenderEntity(String atlasPath, String skeletonPath, int x, int y, float scale,
                                 float scaleX, float scaleY) {
        super(atlasPath, skeletonPath, x, y, scale);
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public float getScaleX() {
        return this.scaleX;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public float getScaleY() {
        return this.scaleY;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }
}