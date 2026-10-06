package com.xtc.ui.widget.slideitemlistview.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import com.xtc.ui.widget.R;
import java.util.Objects;

/** 可侧滑展开菜单的条目容器。 */
public class SlideMenuComponent extends ViewGroup {
    private static final String TAG = SlideMenuComponent.class.getSimpleName();
    private static final int VELOCITY_THRESHOLD = 600;
    private static boolean isTouching;
    private static SlideMenuComponent mViewCache;
    private boolean isEnableSlide;
    boolean isExpand;
    private boolean isLeftSlide;
    private boolean isRightSlide;
    private boolean isUnMoved;
    private boolean isUserSlided;
    private ValueAnimator mCloseAnim;
    private View mContentView;
    private ValueAnimator mExpandAnim;
    private PointF mFirstP;
    private PointF mLastP;
    private int mLimit;
    private int mMaxVelocity;
    private int mPointerId;
    private int mRightMenuWidths;
    private int mScaleTouchSlop;
    private int mScreenWidth;
    private VelocityTracker mVelocityTracker;

    public SlideMenuComponent(Context context) {
        this(context, null);
    }

    public SlideMenuComponent(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideMenuComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mLastP = new PointF();
        this.isUnMoved = true;
        this.mFirstP = new PointF();
        init(context, attrs, defStyleAttr);
    }

    public void setEnableSlide(boolean enableSlide) {
        this.isEnableSlide = enableSlide;
    }

    public boolean isEnableSlide() {
        return this.isEnableSlide;
    }

    public void setLeftSlide(boolean leftSlide) {
        this.isLeftSlide = leftSlide;
    }

    public boolean isLeftSlide() {
        return this.isLeftSlide;
    }

    public void setRightSlide(boolean rightSlide) {
        this.isRightSlide = rightSlide;
    }

    public boolean isRightSlide() {
        return this.isRightSlide;
    }

    public static SlideMenuComponent getViewCache() {
        return mViewCache;
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        this.mScreenWidth = ((WindowManager) Objects.requireNonNull(context.getSystemService("window"))).getDefaultDisplay().getWidth();
        this.mScaleTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.mMaxVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        this.isEnableSlide = true;
        this.isLeftSlide = true;
        this.isRightSlide = false;
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SlideMenuComponent, defStyleAttr, 0);
        int indexCount = attributes.getIndexCount();
        for (int index = 0; index < indexCount; index++) {
            int attributeIndex = attributes.getIndex(index);
            if (attributeIndex == R.styleable.SlideMenuComponent_enableSlide) {
                this.isEnableSlide = attributes.getBoolean(attributeIndex, true);
            } else if (attributeIndex == R.styleable.SlideMenuComponent_rightSlide) {
                this.isLeftSlide = attributes.getBoolean(attributeIndex, false);
            } else if (attributeIndex == R.styleable.SlideMenuComponent_leftSlide) {
                this.isLeftSlide = attributes.getBoolean(attributeIndex, true);
            }
        }
        attributes.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setClickable(true);
        this.mRightMenuWidths = 0;
        int childCount = getChildCount();
        boolean measureUniformHeight = View.MeasureSpec.getMode(heightMeasureSpec) != 1073741824;
        int contentWidth = 0;
        int maxHeight = 0;
        boolean hasMatchParentHeight = false;
        for (int index = 0; index < childCount; index++) {
            View child = getChildAt(index);
            child.setClickable(true);
            if (child.getVisibility() != 8) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
                maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
                if (measureUniformHeight && layoutParams.height == -1) {
                    hasMatchParentHeight = true;
                }
                if (index > 0) {
                    this.mRightMenuWidths += child.getMeasuredWidth();
                } else {
                    this.mContentView = child;
                    contentWidth = child.getMeasuredWidth();
                }
            }
        }
        setMeasuredDimension(getPaddingLeft() + getPaddingRight() + contentWidth, maxHeight + getPaddingTop() + getPaddingBottom());
        this.mLimit = this.mScreenWidth / 3;
        if (hasMatchParentHeight) {
            forceUniformHeight(childCount, widthMeasureSpec);
        }
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new ViewGroup.MarginLayoutParams(getContext(), attrs);
    }

    private void forceUniformHeight(int childCount, int widthMeasureSpec) {
        int heightSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824);
        for (int index = 0; index < childCount; index++) {
            View child = getChildAt(index);
            if (child.getVisibility() != 8) {
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
                if (layoutParams.height == -1) {
                    int oldWidth = layoutParams.width;
                    layoutParams.width = child.getMeasuredWidth();
                    measureChildWithMargins(child, widthMeasureSpec, 0, heightSpec, 0);
                    layoutParams.width = oldWidth;
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childCount = getChildCount();
        int leftOffset = getPaddingLeft();
        int rightOffset = getPaddingLeft();
        for (int index = 0; index < childCount; index++) {
            View child = getChildAt(index);
            if (child.getVisibility() != 8) {
                if (index == 0 || this.isLeftSlide) {
                    child.layout(leftOffset, getPaddingTop(), child.getMeasuredWidth() + leftOffset,
                            getPaddingTop() + child.getMeasuredHeight());
                    leftOffset += child.getMeasuredWidth();
                } else {
                    child.layout(rightOffset - child.getMeasuredWidth(), getPaddingTop(), rightOffset,
                            getPaddingTop() + child.getMeasuredHeight());
                    rightOffset -= child.getMeasuredWidth();
                }
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (this.isEnableSlide) {
            acquireVelocityTracker(event);
            VelocityTracker tracker = this.mVelocityTracker;
            int action = event.getAction();
            if (action == 0) {
                this.isUserSlided = false;
                this.isUnMoved = true;
                if (isTouching) {
                    return false;
                }
                isTouching = true;
                this.mLastP.set(event.getRawX(), event.getRawY());
                this.mFirstP.set(event.getRawX(), event.getRawY());
                SlideMenuComponent cachedView = mViewCache;
                if (cachedView != null) {
                    if (cachedView != this) {
                        cachedView.smoothClose();
                    }
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                this.mPointerId = event.getPointerId(0);
            } else if (action != 1) {
                if (action == 2) {
                    float diffX = this.mLastP.x - event.getRawX();
                    if (this.mFirstP.x - event.getRawX() < (-this.mLimit)) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                    } else {
                        if (diffX > 10.0f || getScrollX() > 10) {
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        if (Math.abs(diffX) > this.mScaleTouchSlop) {
                            this.isUnMoved = false;
                        }
                        if (this.isLeftSlide && diffX > 0.0f) {
                            if (getScrollX() < 0) {
                                scrollTo(0, 0);
                            } else {
                                scrollBy((int) diffX, 0);
                                this.mLastP.set(event.getRawX(), event.getRawY());
                                if (!this.isRightSlide) {
                                    return false;
                                }
                            }
                        } else {
                            this.mLastP.set(event.getRawX(), event.getRawY());
                            if (!this.isRightSlide && diffX < 0.0f) {
                                return false;
                            }
                        }
                    }
                } else if (action == 3) {
                    if (Math.abs(event.getRawX() - this.mFirstP.x) > this.mScaleTouchSlop) {
                        this.isUserSlided = true;
                    }
                    tracker.computeCurrentVelocity(1000, this.mMaxVelocity);
                    float xVelocity = tracker.getXVelocity(this.mPointerId);
                    float totalDiffX = this.mFirstP.x - event.getRawX();
                    if (Math.abs(xVelocity) > 600.0f) {
                        if (xVelocity < -600.0f) {
                            if (this.isLeftSlide && totalDiffX > 0.0f) {
                                smoothExpand();
                            }
                        } else if (this.isLeftSlide && totalDiffX > 0.0f) {
                            smoothClose();
                        }
                    } else if (Math.abs(getScrollX()) > this.mLimit) {
                        smoothExpand();
                    } else {
                        smoothClose();
                    }
                    releaseVelocityTracker();
                    isTouching = false;
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.isEnableSlide) {
            int action = event.getAction();
            if (action != 1) {
                if (action == 2 && Math.abs(event.getRawX() - this.mFirstP.x) > this.mScaleTouchSlop) {
                    return true;
                }
            } else {
                if (Math.abs(event.getRawX() - this.mFirstP.x) > this.mScaleTouchSlop) {
                    return true;
                }
                float diffX = this.mFirstP.x - event.getRawX();
                if (this.isLeftSlide && diffX > 0.0f && getScrollX() > this.mScaleTouchSlop
                        && event.getX() < getWidth() - getScrollX()) {
                    if (this.isUnMoved) {
                        smoothClose();
                    }
                    return true;
                }
                if (this.isRightSlide && diffX < 0.0f && (-getScrollX()) > this.mScaleTouchSlop
                        && event.getX() > (-getScrollX())) {
                    if (this.isUnMoved) {
                        smoothClose();
                    }
                    return true;
                }
                if (this.isUserSlided) {
                    return true;
                }
            }
        }
        return super.onInterceptTouchEvent(event);
    }

    public void smoothExpand() {
        mViewCache = this;
        View contentView = this.mContentView;
        if (contentView != null) {
            contentView.setLongClickable(false);
        }
        cancelAnim();
        int[] values = new int[2];
        values[0] = getScrollX();
        values[1] = this.isLeftSlide ? this.mRightMenuWidths : -this.mRightMenuWidths;
        this.mExpandAnim = ValueAnimator.ofInt(values);
        this.mExpandAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                SlideMenuComponent.this.scrollTo(((Integer) animation.getAnimatedValue()).intValue(), 0);
            }
        });
        this.mExpandAnim.setInterpolator(new OvershootInterpolator());
        this.mExpandAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                SlideMenuComponent.this.isExpand = true;
            }
        });
        this.mExpandAnim.setDuration(300L).start();
    }

    private void cancelAnim() {
        ValueAnimator closeAnim = this.mCloseAnim;
        if (closeAnim != null && closeAnim.isRunning()) {
            this.mCloseAnim.cancel();
        }
        ValueAnimator expandAnim = this.mExpandAnim;
        if (expandAnim == null || !expandAnim.isRunning()) {
            return;
        }
        this.mExpandAnim.cancel();
    }

    public void smoothClose() {
        mViewCache = null;
        View contentView = this.mContentView;
        if (contentView != null) {
            contentView.setLongClickable(true);
        }
        cancelAnim();
        this.mCloseAnim = ValueAnimator.ofInt(getScrollX(), 0);
        this.mCloseAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                SlideMenuComponent.this.scrollTo(((Integer) animation.getAnimatedValue()).intValue(), 0);
            }
        });
        this.mCloseAnim.setInterpolator(new AccelerateInterpolator());
        this.mCloseAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                SlideMenuComponent.this.isExpand = false;
            }
        });
        this.mCloseAnim.setDuration(300L).start();
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

    @Override
    protected void onDetachedFromWindow() {
        SlideMenuComponent cachedView = mViewCache;
        if (this == cachedView) {
            cachedView.smoothClose();
            mViewCache = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    public boolean performLongClick() {
        if (Math.abs(getScrollX()) > this.mScaleTouchSlop) {
            return false;
        }
        return super.performLongClick();
    }

    public void quickClose() {
        if (this == mViewCache) {
            cancelAnim();
            mViewCache.scrollTo(0, 0);
            mViewCache = null;
        }
    }
}