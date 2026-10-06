package com.xtc.moment.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.TextView;

import com.xtc.moment.R;
import com.xtc.ui.widget.scalablecontainer.AppLinearLayout;

/**
 * 地点信息展示布局。
 */
public class LbsLayout extends AppLinearLayout {

    private TextView tvLocation;

    public LbsLayout(Context context) {
        this(context, null);
    }

    public LbsLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LbsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_lbs, this);
        this.tvLocation = (TextView) findViewById(R.id.tv_location);
    }

    public void setLocation(String location) {
        this.tvLocation.setText(location);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (isClickable()) {
            return super.dispatchTouchEvent(event);
        }
        return false;
    }
}