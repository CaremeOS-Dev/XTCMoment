package com.xtc.virtualselfapi.load;

import android.support.v4.app.FragmentActivity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xtc.game.engine.bean.SpineCodeLoadBean;
import com.xtc.game.engine.support.SpineFunctionDelegate;
import com.xtc.httplib.util.HandlerUtil;
import com.xtc.log.LogUtil;
import com.xtc.virtualselfapi.bean.DynamicsVirtualSelfBean;
import com.xtc.virtualselfapi.bean.SuitVirtualBean;
import com.xtc.virtualselfapi.bean.ViewInfo;
import com.xtc.virtualselfapi.constants.Constants;
import com.xtc.virtualselfapi.fragment.VirtualDynamicsSelfFragment;
import com.xtc.virtualselfapi.generate.DynamicsVisualStrategy;
import com.xtc.virtualselfapi.generate.bean.GenerateVirtualBean;
import com.xtc.virtualselfapi.interfaces.ViewLoadCallBack;
import com.xtc.virtualselfapi.view.BaseVirtualView;

import java.util.List;

/**
 * 虚拟形象视图加载器基类，负责默认形象回退与动画播放。
 */
public abstract class BaseViewLoader<T extends BaseVirtualView> {

    protected boolean isLoadDefault;
    protected FragmentActivity mActivity;
    protected int mDynamicsViewId;
    protected ImageView mIvForeground;
    protected ViewGroup mVgGenerateRender;
    protected ViewLoadCallBack mViewLoadCallBack;
    protected SpineCodeLoadBean spineCodeLoadBean;
    protected SpineFunctionDelegate spineFunctionDelegate;
    protected VirtualDynamicsSelfFragment virtualDynamicsSelfFragment;

    public abstract String getTag();

    public abstract void loadVirtualView(T virtualView);

    public BaseViewLoader(FragmentActivity activity) {
        this.mActivity = activity;
    }

    /** 动态形象加载失败时回退到默认形象。 */
    protected void loadDefaultVisual(GenerateVirtualBean generateVirtualBean, List<ViewInfo> viewInfoList) {
        if (this.isLoadDefault) {
            notifyLoadFail();
            return;
        }
        this.isLoadDefault = true;
        LogUtil.w(getTag(), "加载动态形象失败");
        if (generateVirtualBean == null) {
            notifyLoadFail();
            return;
        }
        generateVirtualBean.setLoadSuitVirtual(true);
        DynamicsVirtualSelfBean dynamicsBean = generateVirtualBean.getDynamicsVirtualSelfBean();
        if (dynamicsBean == null) {
            notifyLoadFail();
            return;
        }
        this.virtualDynamicsSelfFragment = VirtualDynamicsSelfFragment.getInstance();
        String defaultSkin = dynamicsBean.getGender() == 0
                ? Constants.DEFAULT_SKIN.DEFAULT_BOY_SKIN : Constants.DEFAULT_SKIN.DEFAULT_GIRL_SKIN;
        SuitVirtualBean suitVirtualBean = new SuitVirtualBean();
        suitVirtualBean.setAtlasResourceName(dynamicsBean.getAtlasResourceName());
        suitVirtualBean.setCustomType(dynamicsBean.getCustomType());
        suitVirtualBean.setSkeletonResourceName(dynamicsBean.getSkeletonResourceName());
        suitVirtualBean.setCustomSetName(defaultSkin);
        suitVirtualBean.setCustomSetResourceName(defaultSkin);
        generateVirtualBean.setSuitVirtualBean(suitVirtualBean);
        generateVirtualBean.setVgGenerateRender(this.mVgGenerateRender);
        generateVirtualBean.setViewInfoList(viewInfoList);
        generateVirtualBean.setDynamicsRender(this.virtualDynamicsSelfFragment);
        new DynamicsVisualStrategy(this.mActivity).generateVisual(generateVirtualBean, this.mDynamicsViewId);
    }

    public void sayHi() {
        sayHi(Constants.AnimConstant.DELAY_START_ANIM_TIME);
    }

    public void sayHi(long delayMillis) {
        if (this.spineFunctionDelegate == null || this.spineCodeLoadBean == null) {
            return;
        }
        executeVisualAnimation(Constants.AnimConstant.VISUAL_SAY_HI_ANIMATION, false, delayMillis);
    }

    public void executeVisualAnimation(final String animationName, final boolean loop, long delayMillis) {
        HandlerUtil.runOnUIThreadDelay(new Runnable() {
            @Override
            public void run() {
                if (BaseViewLoader.this.spineFunctionDelegate == null || BaseViewLoader.this.spineCodeLoadBean == null) {
                    return;
                }
                BaseViewLoader.this.spineFunctionDelegate.executeVisualAnimation(
                        BaseViewLoader.this.spineCodeLoadBean, animationName, loop);
            }
        }, delayMillis);
    }

    public BaseViewLoader setIvForeground(ImageView ivForeground) {
        this.mIvForeground = ivForeground;
        return this;
    }

    public BaseViewLoader setVgGenerateRender(RelativeLayout vgGenerateRender) {
        this.mVgGenerateRender = vgGenerateRender;
        return this;
    }

    public BaseViewLoader setDynamicsViewId(int dynamicsViewId) {
        this.mDynamicsViewId = dynamicsViewId;
        return this;
    }

    public BaseViewLoader setViewLoadCallBack(ViewLoadCallBack viewLoadCallBack) {
        this.mViewLoadCallBack = viewLoadCallBack;
        return this;
    }

    protected boolean isActivityFinish() {
        FragmentActivity activity = this.mActivity;
        return activity == null || activity.isDestroyed() || this.mActivity.isFinishing();
    }

    protected boolean checkParams() {
        if (isActivityFinish()) {
            LogUtil.d(getTag(), "isActivityFinish true");
            return false;
        }
        if (this.mIvForeground == null) {
            LogUtil.d(getTag(), "mIvForeground is null");
            return false;
        }
        if (this.mDynamicsViewId == 0) {
            LogUtil.d(getTag(), "mDynamicsViewId is 0");
            return false;
        }
        if (this.mVgGenerateRender != null) {
            return true;
        }
        LogUtil.d(getTag(), "mVgGenerateRender is null");
        return false;
    }

    protected void notifyLoadSuccess() {
        if (this.mViewLoadCallBack != null) {
            this.mViewLoadCallBack.onLoadSuccess();
        }
    }

    protected void notifyLoadFail() {
        if (this.mViewLoadCallBack != null) {
            this.mViewLoadCallBack.onLoadFail();
        }
    }
}