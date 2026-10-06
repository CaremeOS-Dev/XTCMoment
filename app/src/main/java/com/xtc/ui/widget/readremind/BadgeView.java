package com.xtc.ui.widget.readremind;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TabWidget;
import android.widget.TextView;

/** 可挂载到任意 View 上的数字角标控件。 */
public class BadgeView extends TextView {
    private boolean mHideOnNull;

    public BadgeView(Context context) {
        this(context, null);
    }

    public BadgeView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public BadgeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mHideOnNull = true;
        init();
    }

    private void init() {
        if (!(getLayoutParams() instanceof FrameLayout.LayoutParams)) {
            setLayoutParams(new FrameLayout.LayoutParams(-2, -2, 53));
        }
        setTextColor(-1);
        setTypeface(Typeface.DEFAULT_BOLD);
        setTextSize(2, 11.0f);
        setPadding(dip2Px(5.0f), dip2Px(1.0f), dip2Px(5.0f), dip2Px(1.0f));
        setBackground(9, Color.parseColor("#d3321b"));
        setGravity(17);
        setHideOnNull(true);
        setBadgeCount(0);
    }

    public void setBackground(int dipRadius, int color) {
        float radius = dip2Px(dipRadius);
        ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(
                new float[]{radius, radius, radius, radius, radius, radius, radius, radius}, null, null));
        shapeDrawable.getPaint().setColor(color);
        setBackgroundDrawable(shapeDrawable);
    }

    public boolean isHideOnNull() {
        return this.mHideOnNull;
    }

    public void setHideOnNull(boolean hideOnNull) {
        this.mHideOnNull = hideOnNull;
        setText(getText());
    }

    @Override
    public void setText(CharSequence text, TextView.BufferType type) {
        if (isHideOnNull() && (text == null || text.toString().equalsIgnoreCase("0"))) {
            setVisibility(8);
        } else {
            setVisibility(0);
        }
        super.setText(text, type);
    }

    public void setBadgeCount(int count) {
        setText(String.valueOf(count));
    }

    public Integer getBadgeCount() {
        if (getText() == null) {
            return null;
        }
        try {
            return Integer.valueOf(Integer.parseInt(getText().toString()));
        } catch (NumberFormatException unused) {
            return null;
        }
    }

    public void setBadgeGravity(int gravity) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        layoutParams.gravity = gravity;
        setLayoutParams(layoutParams);
    }

    public int getBadgeGravity() {
        return ((FrameLayout.LayoutParams) getLayoutParams()).gravity;
    }

    public void setBadgeMargin(int margin) {
        setBadgeMargin(margin, margin, margin, margin);
    }

    public void setBadgeMargin(int left, int top, int right, int bottom) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        layoutParams.leftMargin = dip2Px(left);
        layoutParams.topMargin = dip2Px(top);
        layoutParams.rightMargin = dip2Px(right);
        layoutParams.bottomMargin = dip2Px(bottom);
        setLayoutParams(layoutParams);
    }

    public int[] getBadgeMargin() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        return new int[]{layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin};
    }

    public void incrementBadgeCount(int increment) {
        Integer badgeCount = getBadgeCount();
        if (badgeCount == null) {
            setBadgeCount(increment);
        } else {
            setBadgeCount(increment + badgeCount.intValue());
        }
    }

    public void decrementBadgeCount(int decrement) {
        incrementBadgeCount(-decrement);
    }

    public void setTargetView(TabWidget tabWidget, int index) {
        setTargetView(tabWidget.getChildTabViewAt(index));
    }

    public void setTargetView(View target) {
        if (getParent() != null) {
            ((ViewGroup) getParent()).removeView(this);
        }
        if (target == null) {
            return;
        }
        if (target.getParent() instanceof FrameLayout) {
            ((FrameLayout) target.getParent()).addView(this);
            return;
        }
        if (target.getParent() instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) target.getParent();
            int index = parent.indexOfChild(target);
            parent.removeView(target);
            FrameLayout wrapper = new FrameLayout(getContext());
            ViewGroup.LayoutParams layoutParams = target.getLayoutParams();
            wrapper.setLayoutParams(layoutParams);
            target.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            parent.addView(wrapper, index, layoutParams);
            wrapper.addView(target);
            wrapper.addView(this);
            return;
        }
        if (target.getParent() == null) {
            Log.e(getClass().getSimpleName(), "ParentView is needed");
        }
    }

    private int dip2Px(float dip) {
        return (int) ((dip * getContext().getResources().getDisplayMetrics().density) + 0.5f);
    }
}