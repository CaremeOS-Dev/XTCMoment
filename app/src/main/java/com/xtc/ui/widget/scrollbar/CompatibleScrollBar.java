package com.xtc.ui.widget.scrollbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.xtc.moment.R;

/** 兼容滚动条：根据滚动距离按比例移动滑块。 */
public class CompatibleScrollBar extends RelativeLayout {
    private float mItemHeight;
    private RelativeLayout mScrollBarContainer;
    private int mScrollBarContainerHeight;
    private float mScrollRatio;
    private View mScrollThumb;
    private int mScrollThumbMaxHeight;
    private int mScrollThumbMinHeight;
    private int mScrollY;
    private int mVisibleCount;

    public CompatibleScrollBar(Context context) {
        super(context, null);
    }

    public CompatibleScrollBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompatibleScrollBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.compatible_scroll_bar, (ViewGroup) this, true);
        this.mScrollBarContainer = (RelativeLayout) findViewById(R.id.rl_scroll_bar);
        this.mScrollThumb = findViewById(R.id.scroll_thumb);
        initDefaultValue();
    }

    private void initDefaultValue() {
        this.mItemHeight = getResources().getDimensionPixelSize(R.dimen.dp_150);
        this.mScrollBarContainerHeight = getResources().getDimensionPixelSize(R.dimen.dp_42);
        this.mScrollThumbMinHeight = getResources().getDimensionPixelSize(R.dimen.dp_4);
        this.mScrollThumbMaxHeight = getResources().getDimensionPixelSize(R.dimen.dp_40);
        this.mScrollRatio = 1.0f;
        this.mScrollY = 0;
        this.mVisibleCount = 1;
    }

    public void setScrollThumbHeight(float startOffset, float endOffset, int visibleCount) {
        if (visibleCount <= 0) {
            visibleCount = 1;
        }
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) this.mScrollThumb.getLayoutParams();
        int thumbHeight = this.mScrollBarContainerHeight / visibleCount;
        int minHeight = this.mScrollThumbMinHeight;
        if (thumbHeight >= minHeight) {
            minHeight = thumbHeight;
        }
        int maxHeight = this.mScrollThumbMaxHeight;
        if (minHeight > maxHeight) {
            minHeight = maxHeight;
        }
        layoutParams.height = minHeight;
        this.mScrollThumb.setLayoutParams(layoutParams);
        this.mScrollRatio = (this.mScrollBarContainerHeight - minHeight) / (endOffset - startOffset);
    }

    public void setScrollThumbHeight(int itemCount, float itemHeight) {
        setScrollThumbHeight(itemCount, itemHeight, 1);
    }

    public void setScrollThumbHeight(int itemCount, float itemHeight, int visibleCount) {
        this.mItemHeight = itemHeight;
        this.mVisibleCount = visibleCount;
        if (itemCount <= 0) {
            itemCount = 1;
        }
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) this.mScrollThumb.getLayoutParams();
        int thumbHeight = this.mScrollBarContainerHeight / itemCount;
        int minHeight = this.mScrollThumbMinHeight;
        if (thumbHeight < minHeight) {
            thumbHeight = minHeight;
        }
        int maxHeight = this.mScrollThumbMaxHeight;
        if (thumbHeight > maxHeight) {
            thumbHeight = maxHeight;
        }
        layoutParams.height = thumbHeight;
        this.mScrollThumb.setLayoutParams(layoutParams);
        this.mScrollRatio = (this.mScrollBarContainerHeight - thumbHeight) / ((itemCount * itemHeight) - (itemHeight * visibleCount));
    }

    public void onScrolled(int dx, int dy) {
        this.mScrollY += dy;
        System.out.println("scrollY:" + this.mScrollY + ",dy:" + dy);
        this.mScrollBarContainer.scrollTo(0, (int) (((float) (-dy)) * this.mScrollRatio));
    }
}