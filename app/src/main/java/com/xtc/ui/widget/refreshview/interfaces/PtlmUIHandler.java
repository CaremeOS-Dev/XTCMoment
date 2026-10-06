package com.xtc.ui.widget.refreshview.interfaces;

import android.view.View;

/** 加载更多 UI 处理接口。 */
public interface PtlmUIHandler {

    /** 向列表尾部追加视图的能力。 */
    interface FootViewAdder {
        View addFootView(int layoutResId);

        View addFootView(View view);
    }

    /** 加载更多视图。 */
    interface ILoadMoreView {
        void hideView();

        void init(FootViewAdder footViewAdder, View.OnClickListener onClickListener);

        void setFooterVisibility(boolean visible);

        void showFail(Exception exception);

        void showLoading();

        void showNoMore();

        void showNormal();

        void showView();
    }

    ILoadMoreView madeLoadMoreView();
}