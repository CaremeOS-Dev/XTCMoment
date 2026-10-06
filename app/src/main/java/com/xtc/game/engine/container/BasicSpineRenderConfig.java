package com.xtc.game.engine.container;

import com.xtc.game.engine.render.BaseSpineAdapter;

/**
 * 骨骼渲染容器的基本配置。
 */
public class BasicSpineRenderConfig {

    private BaseSpineAdapter spineAdapter;
    private boolean supportAreaChangeSkin = false;
    private boolean allowAutoStopRender = false;
    private boolean transparentBackground = true;
    private String[] areaNames;

    public BaseSpineAdapter getSpineAdapter() {
        return this.spineAdapter;
    }

    public void setSpineAdapter(BaseSpineAdapter spineAdapter) {
        this.spineAdapter = spineAdapter;
    }

    public boolean isSupportAreaChangeSkin() {
        return this.supportAreaChangeSkin;
    }

    public void setSupportAreaChangeSkin(boolean supportAreaChangeSkin) {
        this.supportAreaChangeSkin = supportAreaChangeSkin;
    }

    public boolean isAllowAutoStopRender() {
        return this.allowAutoStopRender;
    }

    public void setAllowAutoStopRender(boolean allowAutoStopRender) {
        this.allowAutoStopRender = allowAutoStopRender;
    }

    public String[] getAreaNames() {
        return this.areaNames;
    }

    public void setAreaNames(String... areaNames) {
        this.areaNames = areaNames;
    }

    public boolean isTransparentBackground() {
        return this.transparentBackground;
    }

    public void setTransparentBackground(boolean transparentBackground) {
        this.transparentBackground = transparentBackground;
    }
}