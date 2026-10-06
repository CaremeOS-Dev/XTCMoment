package com.xtc.ui.widget.indicator;

import android.content.Context;
import android.support.v4.widget.Space;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.xtc.log.LogUtil;
import com.xtc.ui.widget.R;

/** 基于布局的子页面指示器，支持动态增减指示点。 */
public class HeadViewIndicator extends FrameLayout {
    private static final int DEFAULT_RES_ID = R.drawable.ic_page_tag_dot;
    private static final String TAG = "HeadViewIndicator";
    private int mCount;
    private int mIndex;
    private LinearLayout rootView;

    private int getIndicateIndex(int index) {
        return index * 2;
    }

    public HeadViewIndicator(Context context, int index, int count, int selectResId) {
        this(context, null, index, count, selectResId);
    }

    public HeadViewIndicator(Context context, AttributeSet attrs, int index, int count, int selectResId) {
        super(context, attrs);
        this.mIndex = -1;
        int currentIndex = 0;
        this.mCount = 0;
        LogUtil.i(TAG, "HeadView: current view: index = " + index + "count = " + count);
        if (index > count) {
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index, count));
        }
        LayoutInflater.from(context).inflate(R.layout.view_head_indicator, this);
        setLayoutParams(new ViewGroup.LayoutParams(-1, (int) context.getResources().getDimension(R.dimen.head_view_height)));
        this.rootView = (LinearLayout) findViewById(R.id.ll_hv_root);
        while (currentIndex < count) {
            if (currentIndex != 0) {
                this.rootView.addView(createSpaceView(context));
            }
            this.rootView.addView(createIndicatorView(context, index == currentIndex ? selectResId : DEFAULT_RES_ID));
            currentIndex++;
        }
        this.mIndex = index;
        this.mCount = count;
    }

    public void updateIndicator(int index, int count, int selectResId) {
        if (index > count) {
            LogUtil.d(TAG, "updateIndicator: " + outOfBoundsMsg(index, count));
            return;
        }
        int currentCount = this.mCount;
        if (count == currentCount) {
            if (index == this.mIndex) {
                updateChildView(index, selectResId);
            } else {
                updateChildView(index, selectResId);
                updateChildView(this.mIndex, DEFAULT_RES_ID);
            }
        } else if (count > currentCount) {
            while (currentCount < count) {
                this.rootView.addView(createSpaceView(getContext()));
                this.rootView.addView(createIndicatorView(getContext(), index == currentCount ? selectResId : DEFAULT_RES_ID));
                currentCount++;
            }
            if (index == this.mIndex) {
                updateChildView(index, selectResId);
            } else {
                updateChildView(index, selectResId);
                updateChildView(this.mIndex, DEFAULT_RES_ID);
            }
        } else {
            int removedCount = (currentCount - count) * 2;
            this.rootView.removeViews(this.rootView.getChildCount() - removedCount, removedCount);
            if (index == this.mIndex) {
                updateChildView(index, selectResId);
            } else {
                updateChildView(index, selectResId);
                updateChildView(this.mIndex, DEFAULT_RES_ID);
            }
        }
        this.mCount = count;
        this.mIndex = index;
    }

    private void updateChildView(int index, int resId) {
        LinearLayout rootView = this.rootView;
        if (rootView == null) {
            return;
        }
        View child = rootView.getChildAt(getIndicateIndex(index));
        if (child instanceof ImageView) {
            ((ImageView) child).setImageResource(resId);
        }
    }

    private ImageView createIndicatorView(Context context, int resId) {
        ImageView imageView = new ImageView(context);
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        int width = context.getResources().getDimensionPixelSize(R.dimen.dimen_app_page_tag_width);
        int height = context.getResources().getDimensionPixelSize(R.dimen.dimen_app_page_tag_height);
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(width, height);
        } else {
            layoutParams.width = width;
            layoutParams.height = height;
        }
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setImageResource(resId);
        return imageView;
    }

    private Space createSpaceView(Context context) {
        Space space = new Space(context);
        ViewGroup.LayoutParams layoutParams = space.getLayoutParams();
        int width = context.getResources().getDimensionPixelSize(R.dimen.dimen_app_page_tag_space);
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(width, -2);
        } else {
            layoutParams.width = width;
            layoutParams.height = -2;
        }
        space.setLayoutParams(layoutParams);
        return space;
    }

    private String outOfBoundsMsg(int index, int count) {
        return "Index: " + index + ", Count: " + count;
    }
}