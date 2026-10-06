package com.xtc.virtualselfapi.fragment;

import com.xtc.game.engine.bean.NeedRenderEntity;
import com.xtc.game.engine.container.BasicSpineRenderConfig;
import com.xtc.game.engine.container.SpineRenderFragment;
import com.xtc.game.engine.render.BaseSpineAdapter;
import com.xtc.virtualselfapi.bean.DynamicsVirtualSelfBean;
import com.xtc.virtualselfapi.bean.ResourceAttachmentBean;
import com.xtc.virtualselfapi.bean.SuitVirtualBean;
import com.xtc.virtualselfapi.constants.Constants;
import com.xtc.virtualselfapi.render.VirtualSelfRender;
import com.xtc.virtualselfapi.utils.CharacterDataConvertUtil;
import com.xtc.virtualselfapi.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态虚拟形象渲染 Fragment。
 */
public class VirtualDynamicsSelfFragment extends SpineRenderFragment implements BaseSpineAdapter.LoadDataListener {

    private DynamicsVirtualSelfBean dynamicsVirtualSelfBean;
    private boolean hasInitRender;
    private float scale;
    private int showX;
    private int showY;
    private SuitVirtualBean suitVirtualBean;

    public static VirtualDynamicsSelfFragment getInstance() {
        return new VirtualDynamicsSelfFragment();
    }

    public void init(SuitVirtualBean suitVirtualBean, int showX, int showY, float scale) {
        setSuitVirtualBean(suitVirtualBean);
        setShowX(showX);
        setShowY(showY);
        setScale(scale);
        this.hasInitRender = true;
    }

    public void init(DynamicsVirtualSelfBean dynamicsVirtualSelfBean, int showX, int showY, float scale) {
        setDynamicsVirtualSelfBean(dynamicsVirtualSelfBean);
        setShowX(showX);
        setShowY(showY);
        setScale(scale);
        this.hasInitRender = true;
    }

    public void setShowX(int showX) {
        this.showX = showX;
    }

    public void setShowY(int showY) {
        this.showY = showY;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setSuitVirtualBean(SuitVirtualBean suitVirtualBean) {
        this.suitVirtualBean = suitVirtualBean;
    }

    public void setDynamicsVirtualSelfBean(DynamicsVirtualSelfBean dynamicsVirtualSelfBean) {
        this.dynamicsVirtualSelfBean = dynamicsVirtualSelfBean;
    }

    @Override
    public BasicSpineRenderConfig buildRenderFragment(BasicSpineRenderConfig config) {
        List<NeedRenderEntity> entities;
        if (this.suitVirtualBean != null) {
            entities = buildSuitCharacterEntities();
        } else {
            entities = buildSelfCharacterEntities();
        }
        config.setSpineAdapter(new VirtualSelfRender(entities));
        config.setTransparentBackground(true);
        config.setSupportAreaChangeSkin(true);
        config.setAreaNames("head", Constants.SOURCE_THEIR_TYPE_PANTS,
                Constants.SOURCE_THEIR_TYPE_CLOTHES, Constants.SOURCE_THEIR_TYPE_SHOE);
        return config;
    }

    private List<NeedRenderEntity> buildSuitCharacterEntities() {
        if (this.suitVirtualBean == null) {
            return new ArrayList<>();
        }
        List<NeedRenderEntity> entities = new ArrayList<>();
        String resourcePath = FileUtils.getCharacterDynamicResourcePath(this.suitVirtualBean.getCustomType());
        NeedRenderEntity entity = new NeedRenderEntity(
                resourcePath + this.suitVirtualBean.getAtlasResourceName(),
                resourcePath + this.suitVirtualBean.getSkeletonResourceName(),
                this.showX, this.showY, this.scale);
        entity.setDefaultSkinName(this.suitVirtualBean.getCustomSetResourceName());
        entities.add(entity);
        return entities;
    }

    private List<NeedRenderEntity> buildSelfCharacterEntities() {
        if (this.dynamicsVirtualSelfBean == null) {
            return new ArrayList<>();
        }
        List<NeedRenderEntity> entities = new ArrayList<>();
        String resourcePath = FileUtils.getCharacterDynamicResourcePath(this.dynamicsVirtualSelfBean.getCustomType());
        NeedRenderEntity entity = new NeedRenderEntity(
                resourcePath + this.dynamicsVirtualSelfBean.getAtlasResourceName(),
                resourcePath + this.dynamicsVirtualSelfBean.getSkeletonResourceName(),
                this.showX, this.showY, this.scale);
        List<com.xtc.game.engine.bean.SlotAttachmentBean> attachments = new ArrayList<>();
        List<ResourceAttachmentBean> headDress = this.dynamicsVirtualSelfBean.getHeadDress();
        List<ResourceAttachmentBean> upperBodyDress = this.dynamicsVirtualSelfBean.getUpperBodyDress();
        List<ResourceAttachmentBean> lowerBodyDress = this.dynamicsVirtualSelfBean.getLowerBodyDress();
        List<ResourceAttachmentBean> shoesDress = this.dynamicsVirtualSelfBean.getShoesDress();
        attachments.addAll(CharacterDataConvertUtil.convertSlotAttachment(headDress));
        attachments.addAll(CharacterDataConvertUtil.convertSlotAttachment(upperBodyDress));
        attachments.addAll(CharacterDataConvertUtil.convertSlotAttachment(lowerBodyDress));
        attachments.addAll(CharacterDataConvertUtil.convertSlotAttachment(shoesDress));
        entity.setDefaultAttachments(attachments);
        entities.add(entity);
        return entities;
    }

    public boolean isHasInitRender() {
        return this.hasInitRender;
    }

    public void setHasInitRender(boolean hasInitRender) {
        this.hasInitRender = hasInitRender;
    }
}