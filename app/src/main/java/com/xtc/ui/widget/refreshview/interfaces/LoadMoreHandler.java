package com.xtc.ui.widget.refreshview.interfaces;

import android.view.View;

/** 不同列表类型的加载更多适配接口。 */
public interface LoadMoreHandler {
    void addFooter();

    boolean handleSetAdapter(View view, PtlmUIHandler.ILoadMoreView loadMoreView,
            View.OnClickListener onClickListener);

    void refreshDirection(int direction);

    void removeFooter();

    void setOnScrollBottomCallBackListener(View view, OnScrollBottomListener bottomListener,
            OnScrollCallBackListener scrollCallBackListener);

    void setOnScrollBottomListener(View view, OnScrollBottomListener bottomListener);
}