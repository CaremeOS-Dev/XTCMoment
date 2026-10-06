package com.xtc.ui.widget.slideitemlistview.view;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import java.util.Objects;

/** 可左滑露出操作区的条目容器。 */
public class SlideLeftComponent extends RelativeLayout {
    private static final String TAG = SlideLeftComponent.class.getSimpleName();
    private static final int VELOCITY_THRESHOLD = 600;
    private static final int VIEW_GONE = 8;
    private static SlideLeftComponent mViewCache;
    private final int DURATION_TIME;
    private boolean isEnableSlide;
    private boolean isLeftSlide;
    private boolean isQuickStates;
    private boolean isRightSlide;
    private boolean isUserSlided;
    private View mContentView;
    private int mDown;
    private int mFirstMove;
    private Point mFirstP;
    private Handler mHandler;
    private Point mLastP;
    private int mLimit;
    private int mMask;
    private int mMaxVelocity;
    private boolean mMultipleScrollXLessThanZero;
    public OnMotionEventListener mOnMotionEventListener;
    private OnSlideListener mOnSlideListener;
    private int mPointerId;
    private int mScaleTouchSlop;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    /** 触摸事件回调。 */
    public interface OnMotionEventListener {
        void onCancelEvent(MotionEvent event);

        void onDownEvent(MotionEvent event);

        void onMoveEvent(MotionEvent event);

        void onUpEvent(MotionEvent event);
    }

    /** 滑动回调。 */
    public interface OnSlideListener {
        void onSlideEnd(int x, int y);

        void onSlideStart(int x, int y);

        void onSliding(float dx, float dy, float totalDx, float totalDy);
    }

    public SlideLeftComponent(Context context) {
        this(context, null);
    }

    public SlideLeftComponent(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideLeftComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mLastP = new Point();
        this.mFirstP = new Point();
        this.DURATION_TIME = 1000;
        this.mMask = 1;
        this.mDown = 1;
        this.mFirstMove = 2;
        this.mScroller = new Scroller(getContext());
        this.mHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                if (message.what == 8) {
                    SlideLeftComponent.this.mContentView.setVisibility(8);
                }
            }
        };
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        int width = ((WindowManager) Objects.requireNonNull(context.getSystemService("window"))).getDefaultDisplay().getWidth();
        this.mScaleTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.mMaxVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        this.mLimit = width / 3;
        this.isLeftSlide = true;
        this.isEnableSlide = true;
        this.isRightSlide = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        for (int index = 0; index < childCount; index++) {
            View child = getChildAt(index);
            if (index == childCount - 1) {
                this.mContentView = child;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (this.mScroller.computeScrollOffset()) {
            this.mContentView.scrollTo(this.mScroller.getCurrX(), this.mScroller.getCurrY());
            invalidate();
            OnSlideListener listener = this.mOnSlideListener;
            if (listener != null) {
                listener.onSliding(0.0f, 0.0f, this.mScroller.getCurrX(), this.mScroller.getCurrY());
            }
        }
        OnSlideListener listener = this.mOnSlideListener;
        if (this.mScroller.isFinished() && this.isQuickStates && listener != null) {
            listener.onSlideEnd(this.mScroller.getCurrX(), this.mScroller.getCurrY());
        }
    }

    public View getContentView() {
        return this.mContentView;
    }

    public void abortAnimation() {
        if (this.mScroller.isFinished()) {
            return;
        }
        this.mScroller.abortAnimation();
    }

    public void forceAnimation() {
        if (this.mScroller.isFinished()) {
            return;
        }
        this.mScroller.forceFinished(true);
    }

    public static SlideLeftComponent getViewCache() {
        return mViewCache;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (this.isEnableSlide) {
            acquireVelocityTracker(event);
            VelocityTracker tracker = this.mVelocityTracker;
            int action = event.getAction();
            if (action == 0) {
                this.mMultipleScrollXLessThanZero = false;
                OnMotionEventListener motionListener = this.mOnMotionEventListener;
                if (motionListener != null) {
                    motionListener.onDownEvent(event);
                }
                this.mMask = this.mDown;
                this.isUserSlided = false;
                this.mFirstP.set((int) event.getRawX(), (int) event.getRawY());
                this.mLastP.set((int) event.getRawX(), (int) event.getRawY());
                SlideLeftComponent cachedView = mViewCache;
                if (cachedView != null && cachedView.isQuickStates) {
                    if (cachedView != this) {
                        cachedView.restore();
                    }
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
            } else if (action == 1) {
                OnMotionEventListener motionListener = this.mOnMotionEventListener;
                if (motionListener != null) {
                    motionListener.onUpEvent(event);
                }
                this.mLastP.x = (int) event.getRawX();
                this.mLastP.y = (int) event.getRawY();
                if (Math.abs(event.getRawX() - this.mFirstP.x) > this.mScaleTouchSlop) {
                    this.isUserSlided = true;
                }
                tracker.computeCurrentVelocity(1000, this.mMaxVelocity);
                if (tracker.getXVelocity(this.mPointerId) < -600.0f) {
                    scrollLeft();
                } else {
                    scrollByDistanceX();
                }
                releaseVelocityTracker();
            } else if (action == 2) {
                OnMotionEventListener motionListener = this.mOnMotionEventListener;
                if (motionListener != null) {
                    motionListener.onMoveEvent(event);
                }
                float diffX = this.mLastP.x - event.getRawX();
                if (this.mFirstP.x - event.getRawX() < (-this.mLimit)) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    if (diffX > 10.0f || getScrollX() > 10) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    float diffY = this.mLastP.y - event.getRawY();
                    if (Math.abs(diffY) > 10.0f || Math.abs(diffY) > this.mScaleTouchSlop) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    if ((this.isLeftSlide || this.isRightSlide) && Math.abs(this.mFirstP.x - event.getRawX()) > this.mScaleTouchSlop) {
                        if (this.mContentView.getScrollX() < 0) {
                            if (!this.mMultipleScrollXLessThanZero) {
                                this.mContentView.scrollTo(0, 0);
                            }
                            this.mMultipleScrollXLessThanZero = true;
                        } else {
                            this.mContentView.scrollBy((int) diffX, 0);
                        }
                        int mask = this.mMask;
                        if (mask == this.mDown) {
                            OnSlideListener slideListener = this.mOnSlideListener;
                            if (slideListener != null) {
                                slideListener.onSlideStart(this.mLastP.x, this.mLastP.y);
                            }
                            this.mMask = this.mFirstMove;
                        } else if (mask == this.mFirstMove) {
                            OnSlideListener slideListener = this.mOnSlideListener;
                            if (slideListener != null) {
                                slideListener.onSliding(diffX, this.mLastP.y - event.getRawY(),
                                        this.mFirstP.x - event.getRawX(), this.mFirstP.y - event.getRawX());
                            }
                        }
                        mViewCache = this;
                    }
                    this.mLastP.set((int) event.getRawX(), (int) event.getRawY());
                    this.mPointerId = event.getPointerId(0);
                    if (diffX < 0.0f) {
                        return false;
                    }
                }
            } else if (action == 3) {
                OnMotionEventListener motionListener = this.mOnMotionEventListener;
                if (motionListener != null) {
                    motionListener.onCancelEvent(event);
                }
                this.mLastP.x = (int) event.getRawX();
                this.mLastP.y = (int) event.getRawY();
                if (Math.abs(event.getRawX() - this.mFirstP.x) > this.mScaleTouchSlop) {
                    this.isUserSlided = true;
                }
                if (tracker.getXVelocity(this.mPointerId) < -600.0f) {
                    scrollLeft();
                    mViewCache = this;
                } else {
                    scrollByDistanceX();
                }
                releaseVelocityTracker();
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.isEnableSlide) {
            int action = event.getAction();
            if (action != 0) {
                if (action != 1) {
                    if (action == 2 && Math.abs(event.getRawX() - this.mFirstP.x) > this.mScaleTouchSlop) {
                        return true;
                    }
                } else if (Math.abs(event.getRawX() - this.mFirstP.x) > this.mScaleTouchSlop || this.isUserSlided) {
                    return true;
                }
            }
        }
        return super.onInterceptTouchEvent(event);
    }

    private void scrollLeft() {
        this.isQuickStates = true;
        int distance = this.mFirstP.x - this.mLastP.x;
        this.mScroller.startScroll(distance, 0, this.mContentView.getWidth() - distance, 0, 1000);
        postInvalidate();
        this.mHandler.sendEmptyMessageDelayed(8, 1000L);
    }

    public void restore() {
        this.mHandler.removeMessages(8);
        if (this.mContentView.getVisibility() != 0) {
            this.mContentView.setVisibility(0);
        }
        mViewCache = null;
        this.isQuickStates = false;
        this.mScroller.startScroll(this.mContentView.getWidth(), 0,
                -((this.mContentView.getWidth() + this.mContentView.getWidth()) / 2), 0, 1000);
        postInvalidate();
    }

    public void setOnSlideListener(OnSlideListener listener) {
        this.mOnSlideListener = listener;
    }

    public OnSlideListener getOnSlideListener() {
        return this.mOnSlideListener;
    }

    public void setOnMotionEventListener(OnMotionEventListener listener) {
        this.mOnMotionEventListener = listener;
    }

    public OnMotionEventListener getOnMotionEventListener() {
        return this.mOnMotionEventListener;
    }

    private void scrollByDistanceX() {
        if (this.mContentView.getScrollX() >= this.mLimit) {
            scrollLeft();
        } else {
            resetView();
        }
    }

    public void resetView() {
        View contentView = this.mContentView;
        if (contentView != null) {
            contentView.setVisibility(0);
            this.mContentView.scrollTo(0, 0);
        }
    }

    private void acquireVelocityTracker(MotionEvent event) {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(event);
    }

    private void releaseVelocityTracker() {
        VelocityTracker tracker = this.mVelocityTracker;
        if (tracker != null) {
            tracker.clear();
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }
}