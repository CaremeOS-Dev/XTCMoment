package com.xtc.virtualselfapi.generate;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.xtc.log.LogUtil;
import com.xtc.virtualselfapi.bean.DynamicsVirtualSelfBean;
import com.xtc.virtualselfapi.bean.SuitVirtualBean;
import com.xtc.virtualselfapi.bean.ViewInfo;
import com.xtc.virtualselfapi.bean.db.DbDecorate;
import com.xtc.virtualselfapi.fragment.VirtualDynamicsSelfFragment;
import com.xtc.virtualselfapi.generate.bean.GenerateVirtualBean;
import com.xtc.virtualselfapi.generate.interfaces.IGenerateVisualStrategy;
import com.xtc.virtualselfapi.manager.DynamicsShowCache;
import com.xtc.virtualselfapi.manager.VirtualSelfDBServeImpl;
import com.xtc.virtualselfapi.utils.ScreenUtil;
import com.xtc.virtualselfapi.view.ModelView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 动态虚拟形象渲染策略：挂载渲染 Fragment 并处理危险状态下的前景/背景。
 */
public class DynamicsVisualStrategy implements IGenerateVisualStrategy {

    private static final String TAG = "Virtual_Self_Api_DynamicsVisualStrategy";

    private final Context context;

    public DynamicsVisualStrategy(Context context) {
        this.context = context;
    }

    @Override
    public void generateVisual(GenerateVirtualBean generateVirtualBean, int containerViewId) {
        VirtualDynamicsSelfFragment dynamicsRender = generateVirtualBean.getDynamicsRender();
        ViewGroup renderContainer = generateVirtualBean.getVgGenerateRender();
        DynamicsVirtualSelfBean dynamicsBean = generateVirtualBean.getDynamicsVirtualSelfBean();
        createDynamicsForAndBackground(generateVirtualBean,
                generateVirtualBean.isHaveDanger() || generateVirtualBean.getStatus() == 10);
        SuitVirtualBean suitVirtualBean = generateVirtualBean.getSuitVirtualBean();
        renderContainer.removeAllViews();
        if (!dynamicsRender.isHasInitRender()) {
            LogUtil.i(TAG, "virtualDynamicsSelfFragment not init Render");
            if (!generateVirtualBean.isLoadSuitVirtual()) {
                dynamicsRender.init(dynamicsBean, ScreenUtil.getScreenWidth(this.context) / 2, 25, DynamicsShowCache.getScale());
            } else if (suitVirtualBean != null) {
                dynamicsRender.init(suitVirtualBean, ScreenUtil.getScreenWidth(this.context) / 2, 25, DynamicsShowCache.getScale());
            }
            generateVirtualBean.setDynamicsRender(dynamicsRender);
            dynamicsRender.setLoadDataListener(generateVirtualBean.getLoadDataListener());
        }
        FragmentTransaction transaction = generateVirtualBean.getFragmentManager().beginTransaction();
        transaction.replace(containerViewId, dynamicsRender);
        transaction.commitAllowingStateLoss();
        createStaticDebris(generateVirtualBean.getViewInfoList(), renderContainer);
    }

    private void createDynamicsForAndBackground(GenerateVirtualBean generateVirtualBean, boolean showDanger) {
        final ViewGroup renderContainer = generateVirtualBean.getVgGenerateRender();
        if (renderContainer == null) {
            LogUtil.i(TAG, "vgGenerateRender is null");
            return;
        }
        if (!showDanger) {
            restoreDangerView(generateVirtualBean);
            return;
        }
        List<ViewInfo> viewInfoList = generateVirtualBean.getViewInfoList();
        LogUtil.i(TAG, "处于危险下，创建动态装扮的前景和背景");
        ViewInfo backgroundInfo = null;
        ViewInfo foregroundInfo = null;
        for (ViewInfo viewInfo : viewInfoList) {
            if (viewInfo.getType() == 5) {
                if (viewInfo.getLayer() == 0) {
                    backgroundInfo = viewInfo;
                } else {
                    foregroundInfo = viewInfo;
                }
            }
        }
        if (backgroundInfo != null) {
            Glide.with(this.context).load(backgroundInfo.getUrl()).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                    renderContainer.setBackground(drawable);
                }
            });
        }
        if (foregroundInfo != null) {
            Glide.with(this.context).load(foregroundInfo.getUrl()).into(generateVirtualBean.getIvForeground());
        }
    }

    private void createStaticDebris(List<ViewInfo> viewInfoList, final ViewGroup renderContainer) {
        Observable.just(viewInfoList)
                .map(new Func1<List<ViewInfo>, List<ViewInfo>>() {
                    @Override
                    public List<ViewInfo> call(List<ViewInfo> list) {
                        return filterStaticSuitAndDangerView(list);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<ViewInfo>>() {
                    @Override
                    public void call(List<ViewInfo> list) {
                        for (int index = 0; index < list.size(); index++) {
                            ModelView.loadView((RelativeLayout) renderContainer, list.get(index), Glide.get(context));
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(TAG, "createStaticDebris error:", throwable);
                    }
                });
    }

    private List<ViewInfo> filterStaticSuitAndDangerView(List<ViewInfo> viewInfoList) {
        List<ViewInfo> result = new ArrayList<>();
        List<DbDecorate> decorateList = VirtualSelfDBServeImpl.getInstance().queryDecorateList();
        if (decorateList == null) {
            decorateList = VirtualSelfDBServeImpl.getInstance().queryDecorateList();
        }
        LogUtil.i(TAG, "filterStaticSuitAndDangerView: " + decorateList);
        for (int index = 0; index < viewInfoList.size(); index++) {
            ViewInfo viewInfo = viewInfoList.get(index);
            if (viewInfo.getType() != 0 && viewInfo.getType() != 5) {
                if (decorateList == null || decorateList.size() <= 0) {
                    break;
                }
                boolean isDynamic = false;
                for (int decorateIndex = 0; decorateIndex < decorateList.size(); decorateIndex++) {
                    DbDecorate decorate = decorateList.get(decorateIndex);
                    if (decorate.getCostumeId() != null
                            && decorate.getCostumeId().equals(String.valueOf(viewInfo.getId()))) {
                        isDynamic = true;
                        break;
                    }
                }
                if (isDynamic) {
                    LogUtil.i(TAG, "filter Dynamic !!");
                } else {
                    result.add(viewInfo);
                }
            }
        }
        return result;
    }

    private void restoreDangerView(GenerateVirtualBean generateVirtualBean) {
        ImageView foreground = generateVirtualBean.getIvForeground();
        if (foreground == null) {
            return;
        }
        if (foreground.getDrawable() == null && foreground.getResources() == null) {
            return;
        }
        generateVirtualBean.getIvForeground().setImageDrawable(null);
    }
}