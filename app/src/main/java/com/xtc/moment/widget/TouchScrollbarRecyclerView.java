package com.xtc.moment.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.xtc.ui.widget.scalablecontainer.AppRecyclerView;

/**
 * 带滚动回调的 RecyclerView。
 */
public class TouchScrollbarRecyclerView extends AppRecyclerView {

    private boolean isUp;
    private OnScrollCallback mScrollCallback;

    public interface OnScrollCallback {
        void onScrolling();

        void onScrollIdle(boolean isUp);
    }

    public TouchScrollbarRecyclerView(Context context) {
        super(context);
        this.isUp = false;
    }

    public TouchScrollbarRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.isUp = false;
    }

    public TouchScrollbarRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.isUp = false;
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        if (this.mScrollCallback != null && canScrollVertically(1) && canScrollVertically(-1)) {
            if (dy > 0) {
                this.isUp = false;
            } else {
                this.isUp = true;
            }
            this.mScrollCallback.onScrolling();
        }
    }

    @Override
    public void onScrollStateChanged(int newState) {
        super.onScrollStateChanged(newState);
        if (newState == SCROLL_STATE_IDLE && this.mScrollCallback != null) {
            this.mScrollCallback.onScrollIdle(this.isUp);
        }
    }

    public void setScrollCallback(OnScrollCallback callback) {
        this.mScrollCallback = callback;
    }
}