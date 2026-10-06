package com.xtc.virtualselfapi.render;

import com.xtc.game.engine.bean.NeedRenderEntity;
import com.xtc.game.engine.render.BaseSpineAdapter;

import java.util.List;

/**
 * 虚拟形象骨骼渲染适配器。
 */
public class VirtualSelfRender extends BaseSpineAdapter {

    private final List<NeedRenderEntity> needRenderEntityList;

    public VirtualSelfRender(List<NeedRenderEntity> needRenderEntityList) {
        this.needRenderEntityList = needRenderEntityList;
    }

    @Override
    public List<NeedRenderEntity> initRender() {
        return this.needRenderEntityList;
    }
}