package com.xtc.virtualselfapi.view;

import com.xtc.virtualselfapi.bean.DynamicsVirtualSelfBean;
import com.xtc.virtualselfapi.bean.ViewInfo;

import java.util.List;

/**
 * 虚拟形象视图基类，持有状态、展示元素与动态装扮。
 */
public abstract class BaseVirtualView {

    protected int status;
    protected DynamicsVirtualSelfBean talentCustomDress;
    protected List<ViewInfo> viewInfoList;

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<ViewInfo> getViewInfoList() {
        return this.viewInfoList;
    }

    public void setViewInfoList(List<ViewInfo> viewInfoList) {
        this.viewInfoList = viewInfoList;
    }

    public DynamicsVirtualSelfBean getTalentCustomDress() {
        return this.talentCustomDress;
    }

    public void setTalentCustomDress(DynamicsVirtualSelfBean talentCustomDress) {
        this.talentCustomDress = talentCustomDress;
    }
}