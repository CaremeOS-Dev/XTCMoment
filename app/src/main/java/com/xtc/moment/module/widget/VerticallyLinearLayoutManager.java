package com.xtc.moment.module.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

/**
 * 支持动态开关滚动的竖直 LinearLayoutManager。
 */
public class VerticallyLinearLayoutManager extends LinearLayoutManager {

    private boolean isScrollEnabled = true;

    public VerticallyLinearLayoutManager(Context context) {
        super(context);
    }

    public VerticallyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public VerticallyLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setScrollEnabled(boolean scrollEnabled) {
        this.isScrollEnabled = scrollEnabled;
    }

    @Override
    public boolean canScrollVertically() {
        return this.isScrollEnabled && super.canScrollVertically();
    }
}