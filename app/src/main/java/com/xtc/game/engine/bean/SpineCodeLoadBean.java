package com.xtc.game.engine.bean;

import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.Skin;
import com.esotericsoftware.spine.attachments.Attachment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 单套骨骼资源加载结果，聚合图集、骨骼、皮肤与动画等运行时对象。
 */
public class SpineCodeLoadBean {

    private PolygonSpriteBatch batch = new PolygonSpriteBatch();
    private SkeletonRenderer renderer = new SkeletonRenderer();
    private TextureAtlas atlas;
    private Skeleton skeleton;
    private AnimationState state;
    private SkeletonData skeletonData;
    private Array<Skin> skins;
    private HashMap<SkinKey, List<Attachment>> skinPartMap;
    private List<SkinKey> skinKeys;
    private Array<Animation> animations;
    private boolean animRunning;
    private int index;
    private NeedRenderEntity needRenderEntity;

    public SpineCodeLoadBean() {
        this.renderer.setPremultipliedAlpha(true);
        this.skinPartMap = new HashMap<>();
        this.skinKeys = new ArrayList<>();
    }

    public PolygonSpriteBatch getBatch() {
        return this.batch;
    }

    public void setBatch(PolygonSpriteBatch batch) {
        this.batch = batch;
    }

    public SkeletonRenderer getRenderer() {
        return this.renderer;
    }

    public void setRenderer(SkeletonRenderer renderer) {
        this.renderer = renderer;
    }

    public TextureAtlas getAtlas() {
        return this.atlas;
    }

    public void setAtlas(TextureAtlas atlas) {
        this.atlas = atlas;
    }

    public Skeleton getSkeleton() {
        return this.skeleton;
    }

    public void setSkeleton(Skeleton skeleton) {
        this.skeleton = skeleton;
    }

    public AnimationState getState() {
        return this.state;
    }

    public void setState(AnimationState state) {
        this.state = state;
    }

    public SkeletonData getSkeletonData() {
        return this.skeletonData;
    }

    public void setSkeletonData(SkeletonData skeletonData) {
        this.skeletonData = skeletonData;
    }

    public Array<Skin> getSkins() {
        return this.skins;
    }

    public void setSkins(Array<Skin> skins) {
        this.skins = skins;
    }

    public HashMap<SkinKey, List<Attachment>> getSkinPartMap() {
        return this.skinPartMap;
    }

    public void setSkinPartMap(HashMap<SkinKey, List<Attachment>> skinPartMap) {
        this.skinPartMap = skinPartMap;
    }

    public List<SkinKey> getSkinKeys() {
        return this.skinKeys;
    }

    public void setSkinKeys(List<SkinKey> skinKeys) {
        this.skinKeys = skinKeys;
    }

    public Array<Animation> getAnimations() {
        return this.animations;
    }

    public void setAnimations(Array<Animation> animations) {
        this.animations = animations;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isAnimRunning() {
        return this.animRunning;
    }

    public void setAnimRunning(boolean animRunning) {
        this.animRunning = animRunning;
    }

    public NeedRenderEntity getNeedRenderEntity() {
        return this.needRenderEntity;
    }

    public void setNeedRenderEntity(NeedRenderEntity needRenderEntity) {
        this.needRenderEntity = needRenderEntity;
    }

    @Override
    public String toString() {
        return "SpineCodeLoadBean{batch=" + this.batch + ", renderer=" + this.renderer + ", atlas=" + this.atlas
                + ", skeleton=" + this.skeleton + ", state=" + this.state + ", skeletonData=" + this.skeletonData
                + ", skins=" + this.skins + ", skinPartMap=" + this.skinPartMap + ", skinKeys=" + this.skinKeys
                + ", animations=" + this.animations + ", animRuning=" + this.animRunning + ", index=" + this.index
                + ", needRenderEntity=" + this.needRenderEntity + '}';
    }
}