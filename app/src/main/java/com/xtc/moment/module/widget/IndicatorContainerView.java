package com.xtc.moment.module.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.utils.ui.DimenUtil;

/**
 * 指示器容器，选中项会拉长。
 */
public class IndicatorContainerView extends LinearLayout {

    private static final String TAG = "IndicatorContainerView";

    private static final int CHILD_WIDTH_DP = 5;
    private static final int CHILD_WIDTH_DP_SELECT = 9;
    private static final int CHILD_HEIGHT_DP = 4;
    private static final int CHILD_MARGIN_LEFT_DP = 3;
    private static final int CHILD_MARGIN_TOP_DP = 4;

    private final Context mContext;
    private final int mChildViewWidth;
    private final int mChildViewSelectWidth;
    private final int mChildViewHeight;
    private final int mChildMarginLeft;
    private final int mChildMarginTop;

    public IndicatorContainerView(Context context) {
        super(context);
        this.mContext = context;
        this.mChildViewWidth = DimenUtil.dp2px(context, CHILD_WIDTH_DP);
        this.mChildViewSelectWidth = DimenUtil.dp2px(context, CHILD_WIDTH_DP_SELECT);
        this.mChildViewHeight = DimenUtil.dp2px(context, CHILD_HEIGHT_DP);
        this.mChildMarginLeft = DimenUtil.dp2px(context, CHILD_MARGIN_LEFT_DP);
        this.mChildMarginTop = DimenUtil.dp2px(context, CHILD_MARGIN_TOP_DP);
    }

    public IndicatorContainerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        this.mChildViewWidth = DimenUtil.dp2px(context, CHILD_WIDTH_DP);
        this.mChildViewSelectWidth = DimenUtil.dp2px(context, CHILD_WIDTH_DP_SELECT);
        this.mChildViewHeight = DimenUtil.dp2px(context, CHILD_HEIGHT_DP);
        this.mChildMarginLeft = DimenUtil.dp2px(context, CHILD_MARGIN_LEFT_DP);
        this.mChildMarginTop = DimenUtil.dp2px(context, CHILD_MARGIN_TOP_DP);
    }

    public void initIndicatorView(int length) {
        removeAllViews();
        LogUtil.d(TAG, "initIndicatorView：length = " + length);
        for (int index = 0; index < length; index++) {
            View child = new View(this.mContext);
            child.setLayoutParams(getDefaultLayoutParams());
            child.setBackgroundResource(R.drawable.selector_indicator);
            addView(child);
        }
        getChildAt(0).setSelected(true);
        getChildAt(0).setLayoutParams(getSelectLayoutParams());
    }

    private LinearLayout.LayoutParams getDefaultLayoutParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(this.mChildViewWidth, this.mChildViewHeight);
        params.setMargins(this.mChildMarginLeft, this.mChildMarginTop, this.mChildMarginLeft, 0);
        return params;
    }

    private LinearLayout.LayoutParams getSelectLayoutParams() {
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(this.mChildViewSelectWidth, this.mChildViewHeight);
        params.setMargins(this.mChildMarginLeft, this.mChildMarginTop, this.mChildMarginLeft, 0);
        return params;
    }

    public void selectIndicator(int position) {
        for (int index = 0; index < getChildCount(); index++) {
            View child = getChildAt(index);
            if (position == index) {
                child.setLayoutParams(getSelectLayoutParams());
                child.setSelected(true);
            } else {
                child.setLayoutParams(getDefaultLayoutParams());
                child.setSelected(false);
            }
        }
    }
}