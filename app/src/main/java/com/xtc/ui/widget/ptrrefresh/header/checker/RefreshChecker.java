package com.xtc.ui.widget.ptrrefresh.header.checker;

import android.view.View;

import com.xtc.ui.widget.ptrrefresh.layout.BaseFrameLayout;

/** 是否允许下拉刷新的判定与触发。 */
public interface RefreshChecker {
    boolean checkCanDoRefresh(BaseFrameLayout frameLayout, View contentView, View headerView);

    void onRefreshBegin(BaseFrameLayout frameLayout);
}