package com.xtc.ui.widget.refreshview.interfaces;

import com.xtc.ui.widget.refreshview.PtrFrameLayout;
import com.xtc.ui.widget.refreshview.PtrIndicator;

/** 下拉刷新 UI 状态回调。 */
public interface PtrUIHandler {
    void onUIPositionChange(PtrFrameLayout frameLayout, boolean isUnderTouch, byte status, PtrIndicator indicator);

    void onUIRefreshBegin(PtrFrameLayout frameLayout);

    void onUIRefreshComplete(PtrFrameLayout frameLayout);

    void onUIRefreshPrepare(PtrFrameLayout frameLayout);

    void onUIReset(PtrFrameLayout frameLayout);
}