package com.xtc.moment.module.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.xtc.log.LogUtil;

/**
 * 弹幕抽屉容器，下滑时回到最底部。
 */
public class BarrageDrawerView extends LinearLayout implements GestureDetector.OnGestureListener {

    protected static final float FLIP_DISTANCE = 50.0f;
    private static final String TAG = "BarrageBottomView";

    private final GestureDetector mDetector;
    private OnScrollListener onScrollListener;

    public interface OnScrollListener {
        void scrollToBottom();
    }

    public BarrageDrawerView(Context context) {
        super(context);
        this.mDetector = new GestureDetector(context, this);
        init();
    }

    public BarrageDrawerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mDetector = new GestureDetector(context, this);
        init();
    }

    public BarrageDrawerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mDetector = new GestureDetector(context, this);
        init();
    }

    private void init() {
        setFocusable(true);
        setClickable(true);
        setLongClickable(true);
        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                return BarrageDrawerView.this.mDetector.onTouchEvent(event);
            }
        });
    }

    @Override
    public boolean onDown(MotionEvent event) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent event) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent downEvent, MotionEvent currentEvent, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent event) {
    }

    @Override
    public boolean onFling(MotionEvent downEvent, MotionEvent currentEvent, float velocityX, float velocityY) {
        if (downEvent.getX() - currentEvent.getX() > FLIP_DISTANCE) {
            LogUtil.d(TAG, "向左滑...");
            return true;
        }
        if (currentEvent.getX() - downEvent.getX() > FLIP_DISTANCE) {
            LogUtil.d(TAG, "向右滑...");
            return true;
        }
        if (downEvent.getY() - currentEvent.getY() > FLIP_DISTANCE) {
            LogUtil.d(TAG, "向上滑...");
            return true;
        }
        if (currentEvent.getY() - downEvent.getY() > FLIP_DISTANCE) {
            LogUtil.d(TAG, "向下滑...");
            OnScrollListener listener = this.onScrollListener;
            if (listener != null) {
                listener.scrollToBottom();
            }
            return true;
        }
        LogUtil.d(TAG, currentEvent.getX() + " " + currentEvent.getY());
        return false;
    }

    public void setOnScrollListener(OnScrollListener listener) {
        this.onScrollListener = listener;
    }
}