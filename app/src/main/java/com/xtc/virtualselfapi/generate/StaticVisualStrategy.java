package com.xtc.virtualselfapi.generate;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.xtc.log.LogUtil;
import com.xtc.virtualselfapi.bean.ViewInfo;
import com.xtc.virtualselfapi.fragment.VirtualDynamicsSelfFragment;
import com.xtc.virtualselfapi.generate.bean.GenerateVirtualBean;
import com.xtc.virtualselfapi.generate.interfaces.IGenerateVisualStrategy;
import com.xtc.virtualselfapi.view.ModelView;

import java.util.List;

/**
 * 静态虚拟形象渲染策略：直接使用展示元素列表加载静态图片。
 */
public class StaticVisualStrategy implements IGenerateVisualStrategy {

    private static final String TAG = "Virtual_Self_Api_StaticVisualStrategy";

    private final Context context;

    public StaticVisualStrategy(Context context) {
        this.context = context;
    }

    @Override
    public void generateVisual(GenerateVirtualBean generateVirtualBean, int containerViewId) {
        VirtualDynamicsSelfFragment dynamicsRender = generateVirtualBean.getDynamicsRender();
        ViewGroup renderContainer = generateVirtualBean.getVgGenerateRender();
        List<ViewInfo> viewInfoList = generateVirtualBean.getViewInfoList();
        if (dynamicsRender != null) {
            LogUtil.i(TAG, "virtualDynamicsSelfFragment start remove");
            FragmentTransaction transaction = generateVirtualBean.getFragmentManager().beginTransaction();
            transaction.remove(dynamicsRender);
            transaction.commitAllowingStateLoss();
            dynamicsRender.clearLoadDataListener();
        } else {
            renderContainer.removeAllViews();
        }
        if (viewInfoList == null) {
            return;
        }
        for (int index = 0; index < viewInfoList.size(); index++) {
            ModelView.loadView((RelativeLayout) renderContainer, viewInfoList.get(index), Glide.get(this.context));
        }
    }
}