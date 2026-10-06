package com.xtc.ui.widget.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import com.xtc.log.LogUtil;
import com.xtc.ui.widget.R;

/** 横向预览画廊：滚动停止后自动把子项吸附到中心。 */
public class PreviewGallery extends RecyclerView {
    private static final String TAG = PreviewGallery.class.getSimpleName();
    private boolean isDown;
    private int itemHorizontalGap;
    private int itemWidth;
    private RecyclerView.OnScrollListener scrollListener;

    public PreviewGallery(Context context) {
        this(context, null);
    }

    public PreviewGallery(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreviewGallery(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 0) {
                    PreviewGallery.this.dealSelected();
                } else if (1 == newState) {
                    PreviewGallery.this.isDown = true;
                }
            }
        };
        initView(context);
    }

    private void initView(Context context) {
        this.itemWidth = context.getResources().getDimensionPixelOffset(R.dimen.dp_110);
        this.itemHorizontalGap = context.getResources().getDimensionPixelOffset(R.dimen.dp_25);
        addOnScrollListener(this.scrollListener);
        setWillNotDraw(false);
    }

    public void setInitPosition(int targetPosition, int currentPosition) {
        if (getChildAt(0) == null) {
            LogUtil.e("view is null");
            return;
        }
        LogUtil.d(TAG, "setInitPosition position = " + targetPosition + "---itemWidth = " + this.itemWidth + "---currentPos = " + currentPosition);
        scrollBy((targetPosition - currentPosition) * this.itemWidth, 0);
    }

    private void dealSelected() {
        if (this.isDown) {
            this.isDown = false;
            View firstChild = getChildAt(0);
            if (firstChild == null) {
                LogUtil.e(TAG, "view is null");
                return;
            }
            int[] location = new int[2];
            firstChild.getLocationOnScreen(location);
            int childLeft = location[0];
            if (firstChild.getWidth() > this.itemWidth) {
                int absLeft = Math.abs(childLeft);
                int offset = absLeft - this.itemHorizontalGap;
                int itemWidth = this.itemWidth;
                if (offset > itemWidth / 2) {
                    smoothScrollBy((itemWidth - this.itemHorizontalGap) - offset, 0);
                    return;
                } else {
                    smoothScrollBy(-Math.abs(childLeft), 0);
                    return;
                }
            }
            int absLeft = Math.abs(childLeft);
            int itemWidth = this.itemWidth;
            if (absLeft > itemWidth / 2) {
                smoothScrollBy((itemWidth - this.itemHorizontalGap) - absLeft, 0);
            } else {
                smoothScrollBy((-this.itemHorizontalGap) - Math.abs(childLeft), 0);
            }
        }
    }
}