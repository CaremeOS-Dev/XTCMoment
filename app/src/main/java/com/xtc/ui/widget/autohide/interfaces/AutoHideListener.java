package com.xtc.ui.widget.autohide.interfaces;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/** 列表滚动时自动隐藏/显示顶部控件的滚动监听。 */
public abstract class AutoHideListener extends RecyclerView.OnScrollListener {

    private static final int HIDE_THRESHOLD = 10;

    private int mScrolledDistance = 0;
    private boolean mControlsVisible = true;

    public abstract void onHide();

    public abstract void onShow();

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager())
                .findFirstVisibleItemPosition();
        if (firstVisibleItem == 0) {
            if (!this.mControlsVisible) {
                onShow();
                this.mControlsVisible = true;
            }
        } else if (this.mScrolledDistance > HIDE_THRESHOLD && this.mControlsVisible) {
            onHide();
            this.mControlsVisible = false;
            this.mScrolledDistance = 0;
        } else if (this.mScrolledDistance < -HIDE_THRESHOLD && !this.mControlsVisible) {
            onShow();
            this.mControlsVisible = true;
            this.mScrolledDistance = 0;
        }
        if ((!this.mControlsVisible || dy <= 0) && (this.mControlsVisible || dy >= 0)) {
            return;
        }
        this.mScrolledDistance += dy;
    }
}