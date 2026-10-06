package com.xtc.moment.widget;

import android.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;

import com.xtc.log.LogUtil;
import com.xtc.ui.widget.util.UiCommonUtil;
import com.xtc.utils.ui.MotionEventUtils;

public class SwipeRefreshLayout extends ViewGroup {

    private static final String LOG_TAG = "XTC_MOMENT_SwipeRefreshLayout";
    private static final int[] LAYOUT_ATTRS = {R.attr.enabled};

    private static final float MAX_SWIPE_DISTANCE_FACTOR = 0.6f;
    private static final int REFRESH_TRIGGER_DISTANCE = 120;
    private static final long RETURN_TO_ORIGINAL_POSITION_TIMEOUT = 300;
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2.0f;
    private static final int INVALID_POINTER = -1;

    public interface OnRefreshListener {
        void onLoose();

        void onNormal();

        void onRefresh();
    }

    private enum STATUS {
        NORMAL,
        LOOSEN,
        REFRESHING
    }

    private View mTarget;
    private View mHeaderView;
    private OnRefreshListener mListener;
    private boolean mRefreshing;
    private int mTouchSlop;
    private float mDistanceToTriggerSync = -1.0f;
    private float mInitialMotionY;
    private float mLastMotionY;
    private int mActivePointerId = INVALID_POINTER;
    private boolean mIsBeingDragged;
    private boolean mReturningToStart;
    private STATUS mStatus = STATUS.NORMAL;
    private boolean mDisable;
    private int mHeaderHeight;
    private int mOriginalOffsetTop;
    private int mCurrentTargetOffsetTop;
    private int mFrom;
    private float moveY;
    private int mMediumAnimationDuration;
    private final DecelerateInterpolator mDecelerateInterpolator;

    private final Animation mAnimateToStartPosition;
    private final Animation mAnimateToHeaderPosition;
    private final Animation.AnimationListener mReturnToStartPositionListener;
    private final Animation.AnimationListener mReturnToHeaderPositionListener;
    private final Runnable mReturnToStartPosition;
    private final Runnable mReturnToHeaderPosition;
    private final Runnable mCancel;

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    public SwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public SwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRefreshing = false;
        mDistanceToTriggerSync = -1.0f;
        mActivePointerId = INVALID_POINTER;
        mStatus = STATUS.NORMAL;

        mAnimateToStartPosition = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation transformation) {
                int targetTop;
                if (mFrom != mOriginalOffsetTop) {
                    targetTop = ((int) ((mOriginalOffsetTop - mFrom) * interpolatedTime)) + mFrom;
                } else {
                    targetTop = 0;
                }
                int offset = targetTop - mTarget.getTop();
                int currentTop = mTarget.getTop();
                if (offset + currentTop < 0) {
                    offset = 0 - currentTop;
                }
                setTargetOffsetTopAndBottom(offset);
            }
        };
        mAnimateToHeaderPosition = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation transformation) {
                int targetTop;
                if (mFrom != mHeaderHeight) {
                    targetTop = ((int) ((mHeaderHeight - mFrom) * interpolatedTime)) + mFrom;
                } else {
                    targetTop = 0;
                }
                int offset = targetTop - mTarget.getTop();
                int currentTop = mTarget.getTop();
                if (offset + currentTop < 0) {
                    offset = 0 - currentTop;
                }
                setTargetOffsetTopAndBottom(offset);
            }
        };
        mReturnToStartPositionListener = new BaseAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mCurrentTargetOffsetTop = 0;
                mStatus = STATUS.NORMAL;
                mDisable = false;
            }
        };
        mReturnToHeaderPositionListener = new BaseAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mCurrentTargetOffsetTop = mHeaderHeight;
                mStatus = STATUS.REFRESHING;
            }
        };
        mReturnToStartPosition = new Runnable() {
            @Override
            public void run() {
                synchronized (SwipeRefreshLayout.class) {
                    mReturningToStart = true;
                    animateOffsetToStartPosition(mCurrentTargetOffsetTop + getPaddingTop(), mReturnToStartPositionListener);
                }
            }
        };
        mReturnToHeaderPosition = new Runnable() {
            @Override
            public void run() {
                synchronized (SwipeRefreshLayout.class) {
                    mReturningToStart = true;
                    animateOffsetToHeaderPosition(mCurrentTargetOffsetTop + getPaddingTop(), mReturnToHeaderPositionListener);
                }
            }
        };
        mCancel = new Runnable() {
            @Override
            public void run() {
                synchronized (SwipeRefreshLayout.class) {
                    LogUtil.i(LOG_TAG, "start mCancel");
                    mReturningToStart = true;
                    animateOffsetToStartPosition(mCurrentTargetOffsetTop + getPaddingTop(), mReturnToStartPositionListener);
                }
            }
        };
        moveY = 0.0f;
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMediumAnimationDuration = getResources().getInteger(R.integer.config_mediumAnimTime);
        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        setEnabled(typedArray.getBoolean(0, true));
        typedArray.recycle();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        removeCallbacks(mCancel);
        removeCallbacks(mReturnToStartPosition);
        removeCallbacks(mReturnToHeaderPosition);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mReturnToStartPosition);
        removeCallbacks(mCancel);
        removeCallbacks(mReturnToHeaderPosition);
    }

    private void animateOffsetToStartPosition(int from, Animation.AnimationListener listener) {
        if (mTarget == null) {
            return;
        }
        mFrom = from;
        mAnimateToStartPosition.reset();
        mAnimateToStartPosition.setDuration(mMediumAnimationDuration);
        mAnimateToStartPosition.setAnimationListener(listener);
        mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
        mTarget.startAnimation(mAnimateToStartPosition);
    }

    private void animateOffsetToHeaderPosition(int from, Animation.AnimationListener listener) {
        if (mTarget == null) {
            return;
        }
        mFrom = from;
        mAnimateToHeaderPosition.reset();
        mAnimateToHeaderPosition.setDuration(mMediumAnimationDuration);
        mAnimateToHeaderPosition.setAnimationListener(listener);
        mAnimateToHeaderPosition.setInterpolator(mDecelerateInterpolator);
        mTarget.startAnimation(mAnimateToHeaderPosition);
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    public void setRefreshing(boolean refreshing) {
        if (mRefreshing != refreshing) {
            ensureTarget();
            mRefreshing = refreshing;
        }
    }

    public boolean isRefreshing() {
        return mRefreshing;
    }

    private void ensureTarget() {
        if (mTarget == null) {
            if (getChildCount() > 2 && !isInEditMode()) {
                throw new IllegalStateException("SwipeRefreshLayout can only host two children");
            }
            mTarget = getChildAt(1);
            mTarget.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return mDisable;
                }
            });
            mOriginalOffsetTop = mTarget.getTop() + getPaddingTop();
        }
        if (mDistanceToTriggerSync != -1.0f || getParent() == null || ((View) getParent()).getHeight() <= 0) {
            return;
        }
        mDistanceToTriggerSync = (int) Math.min(((View) getParent()).getHeight() * MAX_SWIPE_DISTANCE_FACTOR,
                getResources().getDisplayMetrics().density * REFRESH_TRIGGER_DISTANCE);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (getChildCount() == 0 || getChildCount() == 1) {
            return;
        }
        View child = getChildAt(1);
        int paddingLeft = getPaddingLeft();
        int childTop = mCurrentTargetOffsetTop + getPaddingTop();
        int childRight = ((width - getPaddingLeft()) - getPaddingRight()) + paddingLeft;
        child.layout(paddingLeft, childTop, childRight, ((height - getPaddingTop()) - getPaddingBottom()) + childTop);
        mHeaderView.layout(paddingLeft, childTop - mHeaderHeight, childRight, childTop);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() <= 1) {
            throw new IllegalStateException("SwipeRefreshLayout must have the headerview and contentview");
        }
        if (getChildCount() > 2 && !isInEditMode()) {
            throw new IllegalStateException("SwipeRefreshLayout can only host two children");
        }
        if (mHeaderView == null) {
            mHeaderView = getChildAt(0);
            measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
            mHeaderHeight = mHeaderView.getMeasuredHeight();
            mDistanceToTriggerSync = mHeaderHeight;
        }
        getChildAt(1).measure(
                View.MeasureSpec.makeMeasureSpec((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight(), 1073741824),
                View.MeasureSpec.makeMeasureSpec((getMeasuredHeight() - getPaddingTop()) - getPaddingBottom(), 1073741824));
    }

    public boolean canChildScrollUp() {
        if (Build.VERSION.SDK_INT < 14) {
            View view = mTarget;
            if (!(view instanceof AbsListView)) {
                return view.getScrollY() > 0;
            }
            AbsListView absListView = (AbsListView) view;
            return absListView.getChildCount() > 0 && (absListView.getFirstVisiblePosition() > 0
                    || absListView.getChildAt(0).getTop() < absListView.getPaddingTop());
        }
        return ViewCompat.canScrollVertically(mTarget, -1);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        ensureTarget();
        int action = MotionEventCompat.getActionMasked(event);
        if (mReturningToStart && action == 0) {
            mReturningToStart = false;
        }
        if (!isEnabled() || mReturningToStart || canChildScrollUp() || mStatus == STATUS.REFRESHING) {
            return false;
        }
        if (action == 0) {
            float y = MotionEventUtils.getY(event);
            mInitialMotionY = y;
            mLastMotionY = y;
            mActivePointerId = MotionEventCompat.getPointerId(event, 0);
            mIsBeingDragged = false;
        } else if (action == 1) {
            mIsBeingDragged = false;
            mActivePointerId = INVALID_POINTER;
        } else if (action == 2) {
            int pointerId = mActivePointerId;
            if (pointerId == INVALID_POINTER) {
                LogUtil.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
                return false;
            }
            int pointerIndex = MotionEventCompat.findPointerIndex(event, pointerId);
            if (pointerIndex < 0) {
                LogUtil.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                return false;
            }
            float y = MotionEventUtils.getY(event, pointerIndex);
            if (y - mInitialMotionY > mTouchSlop) {
                mLastMotionY = y;
                mIsBeingDragged = true;
            }
        } else if (action == 3) {
            mIsBeingDragged = false;
            mActivePointerId = INVALID_POINTER;
        } else if (action == 6) {
            onSecondaryPointerUp(event);
        }
        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        if (mReturningToStart && action == 0) {
            mReturningToStart = false;
        }
        if (!isEnabled() || mReturningToStart || canChildScrollUp() || mStatus == STATUS.REFRESHING) {
            return false;
        }
        if (action == 0) {
            float y = MotionEventUtils.getY(event);
            mInitialMotionY = y;
            mLastMotionY = y;
            mActivePointerId = MotionEventCompat.getPointerId(event, 0);
            mIsBeingDragged = false;
        } else if (action == 1) {
            if (mStatus == STATUS.LOOSEN && !mRefreshing && moveY >= UiCommonUtil.dp2Px(getContext(), 36.0f)) {
                startRefresh();
            } else {
                updatePositionTimeout();
            }
            mIsBeingDragged = false;
            mActivePointerId = INVALID_POINTER;
            return false;
        } else if (action == 2) {
            int pointerIndex = MotionEventCompat.findPointerIndex(event, mActivePointerId);
            if (pointerIndex < 0) {
                LogUtil.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                return false;
            }
            float y = MotionEventUtils.getY(event, pointerIndex);
            float delta = y - mInitialMotionY;
            moveY = delta;
            if (!mIsBeingDragged && delta > mTouchSlop) {
                mIsBeingDragged = true;
            }
            if (mIsBeingDragged) {
                if (delta > mDistanceToTriggerSync) {
                    if (mStatus == STATUS.NORMAL) {
                        mStatus = STATUS.LOOSEN;
                        if (mListener != null) {
                            mListener.onLoose();
                        }
                    }
                    updateContentOffsetTop((int) delta);
                } else {
                    if (mStatus == STATUS.LOOSEN) {
                        mStatus = STATUS.NORMAL;
                        if (mListener != null) {
                            mListener.onNormal();
                        }
                    }
                    updateContentOffsetTop((int) delta);
                    if (mLastMotionY > y && mTarget.getTop() == getPaddingTop()) {
                        removeCallbacks(mCancel);
                    }
                }
                mLastMotionY = y;
            }
        } else if (action == 3) {
            updatePositionTimeout();
            mIsBeingDragged = false;
            mActivePointerId = INVALID_POINTER;
            return false;
        } else if (action == 5) {
            int actionIndex = MotionEventCompat.getActionIndex(event);
            mLastMotionY = MotionEventUtils.getY(event, actionIndex);
            mActivePointerId = MotionEventCompat.getPointerId(event, actionIndex);
        } else if (action == 6) {
            onSecondaryPointerUp(event);
        }
        return true;
    }

    private void startRefresh() {
        removeCallbacks(mCancel);
        mReturnToHeaderPosition.run();
        setRefreshing(true);
        mDisable = true;
        if (mListener != null) {
            mListener.onRefresh();
        }
    }

    public void stopRefresh() {
        mReturnToStartPosition.run();
    }

    private void updateContentOffsetTop(int targetTop) {
        int currentTop = mTarget.getTop();
        float target = targetTop;
        float trigger = mDistanceToTriggerSync;
        if (target > trigger) {
            targetTop = ((int) trigger) + (((int) (target - trigger)) / 2);
        } else if (targetTop < 0) {
            targetTop = 0;
        }
        setTargetOffsetTopAndBottom(targetTop - currentTop);
    }

    private void setTargetOffsetTopAndBottom(int offset) {
        mTarget.offsetTopAndBottom(offset);
        mHeaderView.offsetTopAndBottom(offset);
        mCurrentTargetOffsetTop = mTarget.getTop();
        invalidate();
    }

    private void updatePositionTimeout() {
        removeCallbacks(mCancel);
        postDelayed(mCancel, RETURN_TO_ORIGINAL_POSITION_TIMEOUT);
    }

    private void onSecondaryPointerUp(MotionEvent event) {
        int actionIndex = MotionEventCompat.getActionIndex(event);
        if (MotionEventCompat.getPointerId(event, actionIndex) == mActivePointerId) {
            int newPointerIndex = actionIndex == 0 ? 1 : 0;
            mLastMotionY = MotionEventUtils.getY(event, newPointerIndex);
            mActivePointerId = MotionEventCompat.getPointerId(event, newPointerIndex);
        }
    }

    private class BaseAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }
    }

    public void resetState() {
        mStatus = STATUS.NORMAL;
    }
}