package com.xtc.ui.widget.swipeback;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.Scroller;
import com.xtc.log.LogUtil;

/** 从左边缘滑动返回的容器布局。 */
public class SwipeBackLayout extends FrameLayout {
    private static final String TAG = "SwipeBackLayout";
    private float downX;
    private ISlideStateListener iSlideStateListener;
    private boolean isFinish;
    private boolean isSliding;
    private float lastX;
    private int leftSlideWidth;
    private Scroller scroller;
    private int touchSlop;
    private View view;
    private int viewWidth;

    public SwipeBackLayout(Context context) {
        this(context, null);
    }

    public SwipeBackLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeBackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.isSliding = false;
        this.isFinish = false;
        this.leftSlideWidth = 50;
        this.scroller = new Scroller(context);
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public void setSlideStateListener(ISlideStateListener listener) {
        this.iSlideStateListener = listener;
    }

    public void setLeftSlideWidth(int leftSlideWidth) {
        LogUtil.d(TAG, "setLeftSlideWidth leftSlideWidth = " + leftSlideWidth);
        this.leftSlideWidth = leftSlideWidth;
    }

    public void setContentView(View contentView) {
        this.view = (View) contentView.getParent();
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
        int action = event.getAction() & 255;
        LogUtil.d(TAG, "onInterceptTouchEvent action = " + action + "---isSliding = " + this.isSliding);
        if (action == 0) {
            this.downX = event.getRawX();
            float downX = this.downX;
            this.lastX = downX;
            this.isSliding = false;
            if (downX < this.leftSlideWidth) {
                this.isSliding = true;
            }
        }
        return this.isSliding;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & 255;
        LogUtil.d(TAG, "onTouchEvent action = " + action + "---isSliding = " + this.isSliding);
        if (action == 1) {
            if (this.isSliding) {
                dealUp();
            }
        } else if (action != 2) {
            if (action == 3) {
                if (this.isSliding) {
                    dealUp();
                }
            }
        } else if (this.isSliding) {
            dealMove(event);
        }
        return true;
    }

    private void dealMove(MotionEvent event) {
        float rawX = event.getRawX();
        float diffX = this.lastX - rawX;
        int scrollX = this.view.getScrollX();
        this.lastX = rawX;
        LogUtil.d(TAG, "dealMove scrollX = " + scrollX + "---diffX = " + diffX + "---isSliding = " + this.isSliding);
        if (scrollX > 0 || scrollX < (-this.viewWidth) || !this.isSliding) {
            return;
        }
        this.view.scrollBy(Math.round(diffX), 0);
    }

    private void dealUp() {
        if (this.view.getScrollX() <= (-this.viewWidth) / 2) {
            this.isFinish = true;
            scrollRight();
        } else {
            scrollOrigin();
            this.isFinish = false;
        }
    }

    private void scrollRight() {
        int scrollX = this.viewWidth + this.view.getScrollX();
        this.scroller.startScroll(this.view.getScrollX(), 0, -scrollX, 0, Math.abs(scrollX));
        postInvalidate();
    }

    private void scrollOrigin() {
        int scrollX = this.view.getScrollX();
        this.scroller.startScroll(this.view.getScrollX(), 0, -scrollX, 0, Math.abs(scrollX));
        postInvalidate();
    }

    @Override
    public void computeScroll() {
        if (this.scroller.computeScrollOffset()) {
            this.view.scrollTo(this.scroller.getCurrX(), this.scroller.getCurrY());
            postInvalidate();
            if (this.scroller.isFinished()) {
                if (this.isFinish) {
                    dealSlideExit();
                } else {
                    dealSlideBack();
                }
            }
        }
    }

    private void dealSlideExit() {
        ISlideStateListener listener = this.iSlideStateListener;
        if (listener != null) {
            listener.onSlideExit();
        }
    }

    private void dealSlideBack() {
        ISlideStateListener listener = this.iSlideStateListener;
        if (listener != null) {
            listener.onSlideBack();
        }
    }
}