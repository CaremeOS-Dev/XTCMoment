package com.xtc.ui.widget.recycler;

import android.support.v7.widget.RecyclerView;

/** 简化的滚动监听：回调到顶/到底/上滑/下滑。 */
public class SimpleScrollListener extends RecyclerView.OnScrollListener {
    private OnScrollCallback callback;

    /** 滚动回调。 */
    public interface OnScrollCallback {
        void onScrollDown(RecyclerView recyclerView, int dy);

        void onScrollToBottom();

        void onScrollToTop();

        void onScrollUp(RecyclerView recyclerView, int dy);
    }

    public SimpleScrollListener(OnScrollCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState == 0) {
            if (!recyclerView.canScrollVertically(1)) {
                OnScrollCallback scrollCallback = this.callback;
                if (scrollCallback != null) {
                    scrollCallback.onScrollToBottom();
                }
            }
            if (!recyclerView.canScrollVertically(-1)) {
                OnScrollCallback scrollCallback = this.callback;
                if (scrollCallback != null) {
                    scrollCallback.onScrollToTop();
                }
            }
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (dy > 0) {
            OnScrollCallback scrollCallback = this.callback;
            if (scrollCallback != null) {
                scrollCallback.onScrollDown(recyclerView, dy);
            }
        } else {
            OnScrollCallback scrollCallback = this.callback;
            if (scrollCallback != null) {
                scrollCallback.onScrollUp(recyclerView, dy);
            }
        }
    }
}