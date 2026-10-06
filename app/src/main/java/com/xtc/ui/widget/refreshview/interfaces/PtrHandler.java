package com.xtc.ui.widget.refreshview.interfaces;

import android.view.View;

import com.xtc.ui.widget.refreshview.PtrFrameLayout;

/** 是否允许下拉刷新的判定与触发。 */
public interface PtrHandler {
    boolean checkCanDoRefresh(PtrFrameLayout frameLayout, View contentView, View headerView);

    void onBeginRefreshing(PtrFrameLayout frameLayout);
}