package com.xtc.moment.module.widget;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * 列表项可见性监听。
 */
public abstract class OnItemVisibleListener extends RecyclerView.OnScrollListener {

    private static final String TAG = "XTC_MOMENT_OnItemVisibleListener";

    private final LinearLayoutManager mLayoutManager;

    private int mFirstCompleteVisible = Integer.MIN_VALUE;
    private int mLastCompleteVisible = Integer.MIN_VALUE;

    protected abstract void onItemVisible(int position);

    public OnItemVisibleListener(LinearLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int firstVisible = this.mLayoutManager.findFirstVisibleItemPosition();
        int lastVisible = this.mLayoutManager.findLastVisibleItemPosition();
        if (this.mFirstCompleteVisible == Integer.MIN_VALUE && this.mLastCompleteVisible == Integer.MIN_VALUE) {
            int count = (lastVisible - firstVisible) + 1;
            for (int index = firstVisible; index < count; index++) {
                onItemVisible(index);
            }
            this.mFirstCompleteVisible = firstVisible;
            this.mLastCompleteVisible = lastVisible;
            return;
        }
        if (firstVisible == -1 && lastVisible == -1) {
            return;
        }
        if (dy > 0) {
            if (lastVisible > this.mLastCompleteVisible) {
                onItemVisible(lastVisible);
            }
        } else if (firstVisible < this.mFirstCompleteVisible) {
            onItemVisible(firstVisible);
        }
        this.mFirstCompleteVisible = firstVisible;
        this.mLastCompleteVisible = lastVisible;
    }
}