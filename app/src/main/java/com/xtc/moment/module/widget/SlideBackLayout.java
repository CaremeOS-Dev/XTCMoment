package com.xtc.moment.module.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.xtc.log.LogUtil;

/**
 * Wraps the activity content view and turns a horizontal swipe from the left edge into a
 * "swipe back to finish" gesture.
 *
 * <p>While the finger moves the content view is scrolled to the right; on release the layout either
 * snaps back to the original position or finishes the activity when more than a third of the width
 * has been dragged.
 */
public class SlideBackLayout extends FrameLayout {

    private static final String TAG = "SlideBackLayout";

    /** Fraction of the width that must be dragged before the activity is finished. */
    private static final int FINISH_THRESHOLD_DIVISOR = 3;

    /** Receives the different stages of the swipe gesture. */
    public interface ISlideStateListener {
        void onScrollLeft();

        void onScrollRight();

        void onSlideExit();
    }

    private Activity activity;
    private ViewGroup contentView;
    private Scroller scroller;
    private ISlideStateListener slideStateListener;

    private boolean canSlide = true;
    private boolean isSliding;
    private boolean isFinish;
    private float downX;
    private float lastX;
    private int touchSlop;
    private int viewWidth;

    public SlideBackLayout(Context context) {
        this(context, null);
    }

    public SlideBackLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideBackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.scroller = new Scroller(getContext());
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public void setSlideStateListener(ISlideStateListener listener) {
        this.slideStateListener = listener;
    }

    /** Inserts this layout between the decor view and the activity content view. */
    public void attachToActivity(Activity activity) {
        this.activity = activity;
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        ViewGroup content = (ViewGroup) decorView.getChildAt(0);
        content.setBackgroundColor(0);
        decorView.removeView(content);
        decorView.addView(this);
        addView(content);
        this.contentView = content;
    }

    public void setCanSlide(boolean canSlide) {
        LogUtil.d(TAG, "setCanSlide canSlide = " + canSlide);
        this.canSlide = canSlide;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            this.viewWidth = getWidth();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int action = event.getAction() & 0xFF;
        LogUtil.d(TAG, "onInterceptTouchEvent action = " + action + "---isSliding = " + this.isSliding
                + "---canSlide = " + this.canSlide);
        if (action == MotionEvent.ACTION_DOWN) {
            this.downX = event.getRawX();
            this.lastX = this.downX;
            this.isSliding = false;
        } else if (action == MotionEvent.ACTION_MOVE) {
            dealIntercept(event);
        }
        return this.isSliding;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & 0xFF;
        if (action == MotionEvent.ACTION_UP) {
            dealUp();
        } else if (action == MotionEvent.ACTION_CANCEL) {
            dealUp();
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (this.isSliding) {
                dealMove(event);
            } else {
                dealIntercept(event);
            }
        }
        return true;
    }

    /** Starts the slide once the finger has moved further right than the touch slop. */
    private void dealIntercept(MotionEvent event) {
        if (event.getRawX() - this.downX <= this.touchSlop || !this.canSlide) {
            return;
        }
        this.isSliding = true;
    }

    private void convertToTranslucent() {
        try {
            Class.forName("android.app.Activity")
                    .getMethod("convertToTranslucent",
                            Class.forName("android.app.Activity$TranslucentConversionListener"),
                            Class.forName("android.app.ActivityOptions"))
                    .invoke(this.activity, null, null);
        } catch (Exception e) {
            LogUtil.e(TAG, "convertToTranslucent " + e.toString());
        }
    }

    private void convertFromTranslucent() {
        try {
            Class.forName("android.app.Activity").getMethod("convertFromTranslucent", new Class[0])
                    .invoke(this.activity, new Object[0]);
        } catch (Exception e) {
            LogUtil.e(TAG, "convertFromTranslucent " + e.toString());
        }
    }

    private void dealMove(MotionEvent event) {
        float rawX = event.getRawX();
        float offset = rawX - this.downX;
        float delta = this.lastX - rawX;
        this.lastX = rawX;
        LogUtil.d(TAG, "dealMove diffX = " + delta + "---isSliding = " + this.isSliding);
        if (offset < 0.0f || offset > this.viewWidth || !this.isSliding) {
            return;
        }
        this.contentView.scrollBy(Math.round(delta), 0);
    }

    private void dealUp() {
        if (Math.abs(this.contentView.getScrollX()) > this.viewWidth / FINISH_THRESHOLD_DIVISOR) {
            this.isFinish = true;
            scrollRight();
        } else {
            this.isFinish = false;
            scrollOrigin();
        }
        LogUtil.d(TAG, "dealUp isFinish = " + this.isFinish);
    }

    /** Animates the content off screen to the right. */
    private void scrollRight() {
        ISlideStateListener listener = this.slideStateListener;
        if (listener != null) {
            listener.onScrollRight();
        }
        int distance = this.viewWidth + this.contentView.getScrollX();
        this.scroller.startScroll(this.contentView.getScrollX(), 0, -distance, 0, Math.abs(distance));
        postInvalidate();
    }

    /** Animates the content back to its original position. */
    private void scrollOrigin() {
        ISlideStateListener listener = this.slideStateListener;
        if (listener != null) {
            listener.onScrollLeft();
        }
        int distance = -this.contentView.getScrollX();
        this.scroller.startScroll(this.contentView.getScrollX(), 0, distance, 0, Math.abs(distance));
        postInvalidate();
    }

    @Override
    public void computeScroll() {
        if (this.scroller.computeScrollOffset()) {
            LogUtil.d(TAG, "computeScroll");
            this.contentView.scrollTo(this.scroller.getCurrX(), this.scroller.getCurrY());
            postInvalidate();
            if (this.scroller.isFinished() && this.isFinish) {
                dealSlideExit();
                this.activity.finish();
            }
        }
    }

    private void dealSlideExit() {
        ISlideStateListener listener = this.slideStateListener;
        if (listener != null) {
            listener.onSlideExit();
        }
    }
}