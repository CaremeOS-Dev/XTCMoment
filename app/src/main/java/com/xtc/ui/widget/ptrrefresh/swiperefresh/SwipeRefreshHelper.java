package com.xtc.ui.widget.ptrrefresh.swiperefresh;

import android.support.v4.widget.SwipeRefreshLayout;

/** 对 SwipeRefreshLayout 的简单封装。 */
public class SwipeRefreshHelper {

    /** 刷新回调。 */
    public interface OnSwipeRefreshListener {
        void onRefresh();
    }

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (mOnSwipeRefreshListener != null) {
                        mOnSwipeRefreshListener.onRefresh();
                    }
                }
            };

    private OnSwipeRefreshListener mOnSwipeRefreshListener;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public SwipeRefreshHelper(SwipeRefreshLayout swipeRefreshLayout) {
        this.mSwipeRefreshLayout = swipeRefreshLayout;
    }

    public void setOnSwipeRefreshListener(OnSwipeRefreshListener listener) {
        this.mOnSwipeRefreshListener = listener;
        this.mSwipeRefreshLayout.setOnRefreshListener(this.mOnRefreshListener);
    }

    public void autoRefresh() {
        if (this.mOnSwipeRefreshListener != null) {
            this.mSwipeRefreshLayout.setRefreshing(true);
            this.mOnSwipeRefreshListener.onRefresh();
        }
    }

    public void refreshComplete() {
        this.mSwipeRefreshLayout.setRefreshing(false);
    }
}