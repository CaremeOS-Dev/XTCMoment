package com.xtc.ui.widget.scalablecontainer;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.xtc.log.LogUtil;

/** 列表边缘缩放布局管理器：靠近底部的子项按比例缩小。 */
public class ScaleEdgeLayoutManager extends LinearLayoutManager {
    public static final float RESET_SCALE = 1.0f;
    public static final float START_SCALE = 0.8f;
    private static final String TAG = "ScaleEdgeLayoutManager";
    private boolean isAvailable;

    public ScaleEdgeLayoutManager(Context context) {
        super(context, 1, false);
        this.isAvailable = Build.VERSION.SDK_INT >= 27;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int scrolled = super.scrollVerticallyBy(dy, recycler, state);
        if (this.isAvailable) {
            scaleVerticalChildView();
        }
        return scrolled;
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            LogUtil.e(TAG, e);
        }
        if (!this.isAvailable || getItemCount() < 0 || state.isPreLayout()) {
            return;
        }
        scaleVerticalChildView();
    }

    private void scaleVerticalChildView() {
        int containerHeight = getHeight();
        int childCount = getChildCount();
        for (int index = 0; index < childCount; index++) {
            View child = getChildAt(index);
            child.getTop();
            int childHeight = child.getHeight();
            int childBottom = child.getBottom();
            float scale = 1.0f;
            if (childHeight < containerHeight && childBottom > containerHeight) {
                scale = 0.8f + (((childHeight - (childBottom - containerHeight)) * 0.19999999f) / childHeight);
            }
            child.setPivotX(child.getWidth() / 2.0f);
            child.setPivotY(0.0f);
            child.setScaleX(scale);
            child.setScaleY(scale);
        }
    }
}