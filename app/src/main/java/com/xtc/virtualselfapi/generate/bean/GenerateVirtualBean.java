package com.xtc.virtualselfapi.generate.bean;

import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xtc.game.engine.render.BaseSpineAdapter;
import com.xtc.virtualselfapi.bean.DynamicsVirtualSelfBean;
import com.xtc.virtualselfapi.bean.SuitVirtualBean;
import com.xtc.virtualselfapi.bean.ViewInfo;
import com.xtc.virtualselfapi.fragment.VirtualDynamicsSelfFragment;

import java.util.List;

/**
 * 渲染虚拟形象所需的上下文数据集合。
 */
public class GenerateVirtualBean {

    private VirtualDynamicsSelfFragment dynamicsRender;
    private DynamicsVirtualSelfBean dynamicsVirtualSelfBean;
    private FragmentManager fragmentManager;
    private boolean haveDanger;
    private boolean isLoadSuitVirtual;
    private ImageView ivForeground;
    private BaseSpineAdapter.LoadDataListener loadDataListener;
    private int status;
    private SuitVirtualBean suitVirtualBean;
    private ViewGroup vgGenerateRender;
    private List<ViewInfo> viewInfoList;

    public List<ViewInfo> getViewInfoList() {
        return this.viewInfoList;
    }

    public void setViewInfoList(List<ViewInfo> viewInfoList) {
        this.viewInfoList = viewInfoList;
    }

    public DynamicsVirtualSelfBean getDynamicsVirtualSelfBean() {
        return this.dynamicsVirtualSelfBean;
    }

    public void setDynamicsVirtualSelfBean(DynamicsVirtualSelfBean dynamicsVirtualSelfBean) {
        this.dynamicsVirtualSelfBean = dynamicsVirtualSelfBean;
    }

    public ViewGroup getVgGenerateRender() {
        return this.vgGenerateRender;
    }

    public void setVgGenerateRender(ViewGroup vgGenerateRender) {
        this.vgGenerateRender = vgGenerateRender;
    }

    public VirtualDynamicsSelfFragment getDynamicsRender() {
        return this.dynamicsRender;
    }

    public void setDynamicsRender(VirtualDynamicsSelfFragment dynamicsRender) {
        this.dynamicsRender = dynamicsRender;
    }

    public FragmentManager getFragmentManager() {
        return this.fragmentManager;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public BaseSpineAdapter.LoadDataListener getLoadDataListener() {
        return this.loadDataListener;
    }

    public void setLoadDataListener(BaseSpineAdapter.LoadDataListener loadDataListener) {
        this.loadDataListener = loadDataListener;
    }

    public boolean isHaveDanger() {
        return this.haveDanger;
    }

    public void setHaveDanger(boolean haveDanger) {
        this.haveDanger = haveDanger;
    }

    public ImageView getIvForeground() {
        return this.ivForeground;
    }

    public void setIvForeground(ImageView ivForeground) {
        this.ivForeground = ivForeground;
    }

    public SuitVirtualBean getSuitVirtualBean() {
        return this.suitVirtualBean;
    }

    public void setSuitVirtualBean(SuitVirtualBean suitVirtualBean) {
        this.suitVirtualBean = suitVirtualBean;
    }

    public boolean isLoadSuitVirtual() {
        return this.isLoadSuitVirtual;
    }

    public void setLoadSuitVirtual(boolean loadSuitVirtual) {
        this.isLoadSuitVirtual = loadSuitVirtual;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}