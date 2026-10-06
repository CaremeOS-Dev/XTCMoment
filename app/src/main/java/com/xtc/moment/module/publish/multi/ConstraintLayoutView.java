package com.xtc.moment.module.publish.multi;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Constraint layout that reports a horizontal swipe from the left edge so the host activity can
 * finish itself.
 */
public class ConstraintLayoutView extends ConstraintLayout {

    /** Fraction of the width the finger must travel before the swipe is reported. */
    private static final float SWIPE_BACK_FACTOR = 0.3f;

    private int downX;
    private boolean isDealCallBack;

    /** Notified when the user swipes the view to the right. */
    public interface CallBack {
        void onSwipeBack();
    }

    private CallBack callBack;

    public ConstraintLayoutView(Context context) {
        super(context);
    }

    public ConstraintLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ConstraintLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            this.downX = (int) event.getRawX();
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE
                && ((int) event.getRawX()) - this.downX > getWidth() * SWIPE_BACK_FACTOR) {
            this.isDealCallBack = true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            this.downX = 0;
            if (this.isDealCallBack && this.callBack != null) {
                this.callBack.onSwipeBack();
            }
            this.isDealCallBack = false;
        }
        return super.dispatchTouchEvent(event);
    }
}