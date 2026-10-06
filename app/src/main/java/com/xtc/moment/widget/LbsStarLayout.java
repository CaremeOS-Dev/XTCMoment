package com.xtc.moment.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xtc.moment.R;

/**
 * 地点星级评分控件。
 */
public class LbsStarLayout extends LinearLayout implements View.OnClickListener {

    private int level;
    private OnLevelChangeListener levelchangelistener;

    public interface OnLevelChangeListener {
        void onLevelChange(int level);
    }

    public LbsStarLayout(Context context) {
        this(context, null);
    }

    public LbsStarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LbsStarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        LayoutInflater.from(getContext()).inflate(R.layout.widget_lbs_start, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int index = 0; index < getChildCount(); index++) {
            getChildAt(index).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        int clickedIndex = indexOfChild(view);
        this.level = clickedIndex + 1;
        OnLevelChangeListener listener = this.levelchangelistener;
        if (listener != null) {
            listener.onLevelChange(this.level);
        }
        for (int index = 0; index < getChildCount(); index++) {
            if (index <= clickedIndex) {
                ((ImageView) getChildAt(index)).setImageResource(R.drawable.ic_lbs_star_press);
            } else {
                ((ImageView) getChildAt(index)).setImageResource(R.drawable.ic_lbs_star);
            }
        }
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevelchangelistener(OnLevelChangeListener listener) {
        this.levelchangelistener = listener;
    }
}