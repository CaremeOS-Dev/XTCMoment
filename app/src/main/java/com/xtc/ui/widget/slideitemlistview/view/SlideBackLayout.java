package com.xtc.ui.widget.slideitemlistview.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Scroller;
import com.xtc.log.LogUtil;

/** 侧滑返回布局：把 Activity 内容包一层，实现从左边缘滑动返回。 */
public class SlideBackLayout extends FrameLayout {
    private Activity activity;
    private boolean canSlide;
    private ViewGroup contentView;
    private float downX;
    private ISlideStateListener iSlideStateListener;
    private boolean isFinish;
    private boolean isSliding;
    private float lastX;
    private Scroller scroller;
    private int touchSlop;
    private int viewWidth;

    /** 侧滑状态回调。 */
    public interface ISlideStateListener {
        void onScrollLeft();

        void onScrollRight();

        void onSlideExit();
    }

    public SlideBackLayout(Context context) {
        this(context, null);
    }

    public SlideBackLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideBackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.canSlide = true;
        this.isSliding = false;
        this.isFinish = false;
        this.scroller = new Scroller(getContext());
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public void setSlideStateListener(ISlideStateListener listener) {
        this.iSlideStateListener = listener;
    }

    public void attachToActivity(Activity activity) {
        this.activity = activity;
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.getChildAt(0);
        rootView.setBackgroundColor(0);
        decorView.removeView(rootView);
        decorView.addView(this);
        addView(rootView);
        this.contentView = rootView;
    }

    public void setCanSlide(boolean canSlide) {
        LogUtil.d("setCanSlide canSlide = " + canSlide);
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
        int action = event.getAction() & 255;
        LogUtil.d("onInterceptTouchEvent action = " + action + "---isSliding = " + this.isSliding + "---canSlide = " + this.canSlide);
        if (action == 0) {
            this.downX = event.getRawX();
            this.lastX = this.downX;
            this.isSliding = false;
        } else if (action == 2) {
            dealIntercept(event);
        }
        return this.isSliding;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & 255;
        if (action == 1) {
            dealUp();
        } else if (action != 2) {
            if (action == 3) {
                dealUp();
            }
        } else if (this.isSliding) {
            dealMove(event);
        } else {
            dealIntercept(event);
        }
        return true;
    }

    private void dealIntercept(MotionEvent event) {
        if (event.getRawX() - this.downX <= this.touchSlop || !this.canSlide) {
            return;
        }
        this.isSliding = true;
    }

    private void convertToTranslucent() {
        try {
            Class.forName("android.app.Activity")
                    .getMethod("convertToTranslucent", Class.forName("android.app.Activity$TranslucentConversionListener"),
                            Class.forName("android.app.ActivityOptions"))
                    .invoke(this.activity, null, null);
        } catch (Exception e) {
            LogUtil.e("convertToTranslucent " + e.toString());
        }
    }

    private void convertFromTranslucent() {
        try {
            Class.forName("android.app.Activity").getMethod("convertFromTranslucent", new Class[0])
                    .invoke(this.activity, new Object[0]);
        } catch (Exception e) {
            LogUtil.e("convertFromTranslucent " + e.toString());
        }
    }

    private void dealMove(MotionEvent event) {
        float rawX = event.getRawX();
        float distance = rawX - this.downX;
        float diffX = this.lastX - rawX;
        this.lastX = rawX;
        LogUtil.d("dealMove diffX = " + diffX + "---isSliding = " + this.isSliding);
        if (distance < 0.0f || distance > this.viewWidth || !this.isSliding) {
            return;
        }
        this.contentView.scrollBy(Math.round(diffX), 0);
    }

    private void dealUp() {
        if (Math.abs(this.contentView.getScrollX()) > this.viewWidth / 3) {
            this.isFinish = true;
            scrollRight();
        } else {
            this.isFinish = false;
            scrollOrigin();
        }
        LogUtil.d("dealUp isFinish = " + this.isFinish);
    }

    private void scrollRight() {
        ISlideStateListener listener = this.iSlideStateListener;
        if (listener != null) {
            listener.onScrollRight();
        }
        int scrollX = this.viewWidth + this.contentView.getScrollX();
        this.scroller.startScroll(this.contentView.getScrollX(), 0, -scrollX, 0, Math.abs(scrollX));
        postInvalidate();
    }

    private void scrollOrigin() {
        ISlideStateListener listener = this.iSlideStateListener;
        if (listener != null) {
            listener.onScrollLeft();
        }
        int scrollX = -this.contentView.getScrollX();
        this.scroller.startScroll(this.contentView.getScrollX(), 0, scrollX, 0, Math.abs(scrollX));
        postInvalidate();
    }

    @Override
    public void computeScroll() {
        if (this.scroller.computeScrollOffset()) {
            LogUtil.d("computeScroll");
            this.contentView.scrollTo(this.scroller.getCurrX(), this.scroller.getCurrY());
            postInvalidate();
            if (this.scroller.isFinished() && this.isFinish) {
                dealSlideExit();
                this.activity.finish();
            }
        }
    }

    private void dealSlideExit() {
        ISlideStateListener listener = this.iSlideStateListener;
        if (listener != null) {
            listener.onSlideExit();
        }
    }
}