package com.xtc.ui.widget.ptrrefresh.footer;

import android.view.View;

/** 加载更多视图的四种状态。 */
public interface LoadMoreView {
    void init(FootViewAdder footViewAdder, View.OnClickListener onClickListener);

    void showFail(Exception exception);

    void showLoading();

    void showNoMore();

    void showNormal();
}