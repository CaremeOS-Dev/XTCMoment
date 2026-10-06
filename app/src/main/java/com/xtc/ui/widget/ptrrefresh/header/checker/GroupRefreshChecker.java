package com.xtc.ui.widget.ptrrefresh.header.checker;

import android.view.View;

import com.xtc.ui.widget.ptrrefresh.layout.BaseFrameLayout;

/** 分组列表的刷新判定，默认总是允许下拉刷新。 */
public abstract class GroupRefreshChecker implements RefreshChecker {

    @Override
    public boolean checkCanDoRefresh(BaseFrameLayout frameLayout, View contentView, View headerView) {
        return true;
    }
}