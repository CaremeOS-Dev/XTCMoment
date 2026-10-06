package com.xtc.ui.widget.ptrrefresh.header;

import com.xtc.ui.widget.ptrrefresh.indicator.Indicator;
import com.xtc.ui.widget.ptrrefresh.layout.BaseFrameLayout;

/** 下拉刷新 UI 状态回调。 */
public interface UIRefreshHandler {
    void onUIPositionChange(BaseFrameLayout frameLayout, boolean isUnderTouch, byte status, Indicator indicator);

    void onUIRefreshBegin(BaseFrameLayout frameLayout);

    void onUIRefreshComplete(BaseFrameLayout frameLayout, boolean isSuccess);

    void onUIRefreshPrepare(BaseFrameLayout frameLayout);

    void onUIReset(BaseFrameLayout frameLayout);
}