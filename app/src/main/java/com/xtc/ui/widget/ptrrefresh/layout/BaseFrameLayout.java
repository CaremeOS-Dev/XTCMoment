package com.xtc.ui.widget.ptrrefresh.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.TextView;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.ui.widget.ptrrefresh.header.UIRefreshHandler;
import com.xtc.ui.widget.ptrrefresh.header.UIRefreshHandlerHolder;
import com.xtc.ui.widget.ptrrefresh.header.UIRefreshHandlerHook;
import com.xtc.ui.widget.ptrrefresh.header.checker.RefreshChecker;
import com.xtc.ui.widget.ptrrefresh.indicator.Indicator;

/** 下拉刷新容器基类：管理头部/内容布局、拖动位移与刷新状态机。 */
public class BaseFrameLayout extends ViewGroup {
    private static final byte FLAG_AUTO_REFRESH_AT_ONCE = 1;
    private static final byte FLAG_AUTO_REFRESH_BUT_LATER = 2;
    private static final byte FLAG_ENABLE_NEXT_PTR_AT_ONCE = 4;
    private static final byte FLAG_PIN_CONTENT = 8;
    private static final byte MASK_AUTO_REFRESH = 3;
    public static final byte PTR_STATUS_COMPLETE = 4;
    public static final byte PTR_STATUS_INIT = 1;
    public static final byte PTR_STATUS_LOADING = 3;
    public static final byte PTR_STATUS_PREPARE = 2;
    private static final String TAG = "BaseFrameLayout";
    private Runnable mCompleteRunnable;
    private int mContainerId;
    private View mContent;
    private boolean mDisableWhenHorizontalMove;
    private int mDurationToClose;
    private int mDurationToCloseHeader;
    private int mFlag;
    private boolean mHasSendCancelEvent;
    private View mHeader;
    private int mHeaderHeight;
    private int mHeaderId;
    private Indicator mIndicator;
    private boolean mIsSuccess;
    private boolean mKeepHeaderWhenRefresh;
    private MotionEvent mLastMoveEvent;
    private int mLoadingMinTime;
    private long mLoadingStartTime;
    private int mPagingTouchSlop;
    private boolean mPreventForHorizontal;
    private boolean mPullRefreshEnable;
    private boolean mPullToRefresh;
    private RefreshChecker mRefreshChecker;
    private UIRefreshHandlerHook mRefreshCompleteHook;
    private ScrollChecker mScrollChecker;
    private byte mStatus;
    private UIRefreshHandlerHolder mUIHandlerHolder;
    private Runnable mWaitRunnable;

    protected void onPositionChange(boolean isUnderTouch, byte status, Indicator indicator) {
    }

    public BaseFrameLayout(Context context) {
        this(context, null);
    }

    public BaseFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mHeaderId = 0;
        this.mContainerId = 0;
        this.mLoadingMinTime = 500;
        this.mLoadingStartTime = 0L;
        this.mDurationToClose = 200;
        this.mDurationToCloseHeader = 800;
        this.mPullToRefresh = false;
        this.mPullRefreshEnable = false;
        this.mKeepHeaderWhenRefresh = true;
        this.mStatus = (byte) 1;
        this.mFlag = 0;
        this.mHasSendCancelEvent = false;
        this.mPreventForHorizontal = false;
        this.mDisableWhenHorizontalMove = false;
        this.mCompleteRunnable = new Runnable() {
            @Override
            public void run() {
                BaseFrameLayout.this.performRefreshComplete();
            }
        };
        this.mIsSuccess = false;
        this.mWaitRunnable = new Runnable() {
            @Override
            public void run() {
                BaseFrameLayout.this.tryScrollBackToTopAfterComplete();
            }
        };
        this.mIndicator = new Indicator();
        this.mScrollChecker = new ScrollChecker();
        this.mUIHandlerHolder = UIRefreshHandlerHolder.create();
        this.mPagingTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop() * 2;
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.BaseFrameLayout, 0, 0);
        if (attributes != null) {
            this.mHeaderId = attributes.getResourceId(R.styleable.BaseFrameLayout_base_header, this.mHeaderId);
            this.mContainerId = attributes.getResourceId(R.styleable.BaseFrameLayout_base_content, this.mContainerId);
            this.mIndicator.setResistance(attributes.getFloat(R.styleable.BaseFrameLayout_base_resistance, this.mIndicator.getResistance()));
            this.mDurationToClose = attributes.getInt(R.styleable.BaseFrameLayout_base_duration_to_close, this.mDurationToClose);
            this.mDurationToCloseHeader = attributes.getInt(R.styleable.BaseFrameLayout_base_duration_to_close_header, this.mDurationToCloseHeader);
            this.mIndicator.setRatioOfHeaderHeightToRefresh(attributes.getFloat(R.styleable.BaseFrameLayout_base_ratio_of_header_height_to_refresh, this.mIndicator.getRatioOfHeaderToHeightRefresh()));
            this.mPullToRefresh = attributes.getBoolean(R.styleable.BaseFrameLayout_base_pull_to_fresh, this.mPullToRefresh);
            this.mKeepHeaderWhenRefresh = attributes.getBoolean(R.styleable.BaseFrameLayout_base_keep_header_when_refresh, this.mKeepHeaderWhenRefresh);
            attributes.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        int childCount = getChildCount();
        if (childCount > 2) {
            throw new IllegalStateException("BaseFrameLayout must host 2 elements");
        }
        if (childCount == 2) {
            int headerId = this.mHeaderId;
            if (headerId != 0 && this.mHeader == null) {
                this.mHeader = findViewById(headerId);
            }
            int containerId = this.mContainerId;
            if (containerId != 0 && this.mContent == null) {
                this.mContent = findViewById(containerId);
            }
            if (this.mContent == null || this.mHeader == null) {
                View firstChild = getChildAt(0);
                View secondChild = getChildAt(1);
                if (firstChild instanceof UIRefreshHandler) {
                    this.mHeader = firstChild;
                    this.mContent = secondChild;
                } else if (secondChild instanceof UIRefreshHandler) {
                    this.mHeader = secondChild;
                    this.mContent = firstChild;
                } else if (this.mContent == null && this.mHeader == null) {
                    this.mHeader = firstChild;
                    this.mContent = secondChild;
                } else {
                    View headerView = this.mHeader;
                    if (headerView == null) {
                        if (this.mContent == firstChild) {
                            firstChild = secondChild;
                        }
                        this.mHeader = firstChild;
                    } else {
                        if (headerView == firstChild) {
                            firstChild = secondChild;
                        }
                        this.mContent = firstChild;
                    }
                }
            }
        } else if (childCount == 1) {
            this.mContent = getChildAt(0);
        } else {
            TextView emptyView = new TextView(getContext());
            emptyView.setClickable(true);
            emptyView.setTextColor(-39424);
            emptyView.setGravity(17);
            emptyView.setTextSize(20.0f);
            emptyView.setText("The content view in BaseFrameLayout is empty.");
            this.mContent = emptyView;
            addView(this.mContent);
        }
        View headerView = this.mHeader;
        if (headerView != null) {
            headerView.bringToFront();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        View headerView = this.mHeader;
        if (headerView != null) {
            measureChildWithMargins(headerView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) this.mHeader.getLayoutParams();
            this.mHeaderHeight = this.mHeader.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
            this.mIndicator.setHeaderHeight(this.mHeaderHeight);
        }
        View content = this.mContent;
        if (content != null) {
            measureContentView(content, widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void measureContentView(View view, int widthMeasureSpec, int heightMeasureSpec) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        view.measure(getChildMeasureSpec(widthMeasureSpec, getPaddingLeft() + getPaddingRight() + layoutParams.leftMargin + layoutParams.rightMargin, layoutParams.width),
                getChildMeasureSpec(heightMeasureSpec, getPaddingTop() + getPaddingBottom() + layoutParams.topMargin, layoutParams.height));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        layoutChildren();
    }

    private void layoutChildren() {
        int currentPosY = this.mIndicator.getCurrentPosY();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        View headerView = this.mHeader;
        if (headerView != null) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) headerView.getLayoutParams();
            int childLeft = layoutParams.leftMargin + paddingLeft;
            int childTop = ((layoutParams.topMargin + paddingTop) + currentPosY) - this.mHeaderHeight;
            headerView.layout(childLeft, childTop, headerView.getMeasuredWidth() + childLeft, headerView.getMeasuredHeight() + childTop);
        }
        if (this.mContent != null) {
            if (isPinContent()) {
                currentPosY = 0;
            }
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) this.mContent.getLayoutParams();
            int childLeft = paddingLeft + layoutParams.leftMargin;
            int childTop = paddingTop + layoutParams.topMargin + currentPosY;
            this.mContent.layout(childLeft, childTop, this.mContent.getMeasuredWidth() + childLeft, this.mContent.getMeasuredHeight() + childTop);
        }
    }

    public boolean dispatchTouchEventSupper(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!isEnabled() || this.mContent == null || this.mHeader == null) {
            return dispatchTouchEventSupper(event);
        }
        int action = event.getAction();
        if (action == 0) {
            this.mHasSendCancelEvent = false;
            this.mPreventForHorizontal = false;
            this.mScrollChecker.abortIfWorking();
            this.mIndicator.onPressDown(event.getX(), event.getY());
            dispatchTouchEventSupper(event);
            return true;
        }
        if (action == 1 || action == 3) {
            this.mIndicator.onRelease();
            if (this.mIndicator.hasLeftStartPosition()) {
                onTouchRelease(false);
                if (this.mIndicator.hasMovedAfterPressedDown()) {
                    sendCancelEvent();
                    return true;
                }
                return dispatchTouchEventSupper(event);
            }
            return dispatchTouchEventSupper(event);
        }
        if (action != 2) {
            return dispatchTouchEventSupper(event);
        }
        this.mLastMoveEvent = event;
        this.mIndicator.onMove(event.getX(), event.getY());
        float offsetX = this.mIndicator.getOffsetX();
        float offsetY = this.mIndicator.getOffsetY();
        if (this.mDisableWhenHorizontalMove && !this.mPreventForHorizontal
                && Math.abs(offsetX) > this.mPagingTouchSlop
                && Math.abs(offsetX) > Math.abs(offsetY)
                && this.mIndicator.isInStartPosition()) {
            this.mPreventForHorizontal = true;
        }
        if (this.mPreventForHorizontal) {
            return dispatchTouchEventSupper(event);
        }
        boolean isDown = offsetY > 0.0f;
        boolean isUp = !isDown;
        boolean hasLeftStartPosition = this.mIndicator.hasLeftStartPosition();
        boolean canPullDown = this.mRefreshChecker != null
                && this.mRefreshChecker.checkCanDoRefresh(this, this.mContent, this.mHeader);
        if (!this.mPullRefreshEnable) {
            return dispatchTouchEventSupper(event);
        }
        if (isDown && canPullDown) {
            return dispatchTouchEventSupper(event);
        }
        if (isUp && hasLeftStartPosition) {
            return dispatchTouchEventSupper(event);
        }
        movePos(offsetY);
        return true;
    }    private void movePos(float deltaY) {
        if (deltaY >= 0.0f || !this.mIndicator.isInStartPosition()) {
            int currentPosY = this.mIndicator.getCurrentPosY() + ((int) deltaY);
            if (this.mIndicator.willOverTop(currentPosY)) {
                currentPosY = 0;
            }
            this.mIndicator.setCurrentPos(currentPosY);
            updatePos(currentPosY - this.mIndicator.getLastPosY());
        }
    }

    private void updatePos(int delta) {
        if (delta == 0) {
            return;
        }
        boolean isUnderTouch = this.mIndicator.isUnderTouch();
        if (isUnderTouch && !this.mHasSendCancelEvent && this.mIndicator.hasMovedAfterPressedDown()) {
            this.mHasSendCancelEvent = true;
            sendCancelEvent();
        }
        if ((this.mIndicator.hasJustLeftStartPosition() && this.mStatus == 1)
                || (this.mIndicator.goDownCrossFinishPosition() && this.mStatus == 4 && isEnabledNextPtrAtOnce())) {
            LogUtil.d(TAG, "mStatus = PTR_STATUS_PREPARE");
            this.mStatus = (byte) 2;
            this.mUIHandlerHolder.onUIRefreshPrepare(this);
        }
        if (this.mIndicator.hasJustBackToStartPosition()) {
            tryToNotifyReset();
            if (isUnderTouch) {
                sendDownEvent();
            }
        }
        if (this.mStatus == 2) {
            if (isUnderTouch && isAutoRefresh() && this.mPullToRefresh && this.mIndicator.crossRefreshLineFromTopToBottom()) {
                LogUtil.d(TAG, "reach fresh height and perform Refresh");
                tryToPerformRefresh();
            }
            if (performAutoRefreshButLater() && this.mIndicator.hasJustReachedHeaderHeightFromTopToBottom()) {
                LogUtil.d(TAG, "reach header height and perform Refresh");
                tryToPerformRefresh();
            }
        }
        this.mHeader.offsetTopAndBottom(delta);
        if (!isPinContent()) {
            this.mContent.offsetTopAndBottom(delta);
        }
        invalidate();
        onPositionChange(isUnderTouch, this.mStatus, this.mIndicator);
        if (this.mUIHandlerHolder.hasHandler()) {
            this.mUIHandlerHolder.onUIPositionChange(this, isUnderTouch, this.mStatus, this.mIndicator);
        }
    }

    public int getHeaderHeight() {
        return this.mHeaderHeight;
    }

    public void onTouchRelease(boolean stayForLoading) {
        LogUtil.d(TAG, "onTouchRelease stayForLoading = " + stayForLoading);
        tryToPerformRefresh();
        byte status = this.mStatus;
        if (status != 3) {
            if (status == 4) {
                tryScrollBackToTopAbortRefresh();
                return;
            }
            tryScrollBackToTopAbortRefresh();
            return;
        }
        if (this.mKeepHeaderWhenRefresh) {
            if (!this.mIndicator.isOverOffsetToKeepHeaderWhileLoading() || stayForLoading) {
                return;
            }
            this.mScrollChecker.tryToScrollTo(this.mIndicator.getOffsetToKeepHeaderWhileLoading(), this.mDurationToClose);
            return;
        }
        tryScrollBackToTopWhileLoading();
    }

    public void setRefreshCompleteHook(UIRefreshHandlerHook hook) {
        this.mRefreshCompleteHook = hook;
        hook.setResumeAction(new Runnable() {
            @Override
            public void run() {
                BaseFrameLayout.this.notifyUIRefreshComplete(true);
            }
        });
    }

    private void tryScrollBackToTop() {
        if (this.mIndicator.isUnderTouch()) {
            return;
        }
        this.mScrollChecker.tryToScrollTo(0, this.mDurationToCloseHeader);
    }

    private void tryScrollBackToTopWhileLoading() {
        tryScrollBackToTop();
    }

    private void tryScrollBackToTopAfterComplete() {
        tryScrollBackToTop();
    }

    private void tryScrollBackToTopAbortRefresh() {
        tryScrollBackToTop();
    }

    private void tryToPerformRefresh() {
        if (this.mStatus != 2) {
            return;
        }
        if ((this.mIndicator.isOverOffsetToKeepHeaderWhileLoading() && isAutoRefresh()) || this.mIndicator.isOverOffsetToRefresh()) {
            LogUtil.d(TAG, "mStatus = PTR_STATUS_LOADING");
            this.mStatus = (byte) 3;
            performRefresh();
        }
    }

    private void performRefresh() {
        this.mLoadingStartTime = System.currentTimeMillis();
        if (this.mUIHandlerHolder.hasHandler()) {
            this.mUIHandlerHolder.onUIRefreshBegin(this);
        }
        RefreshChecker refreshChecker = this.mRefreshChecker;
        if (refreshChecker != null) {
            refreshChecker.onRefreshBegin(this);
        }
    }

    private void tryToNotifyReset() {
        byte status = this.mStatus;
        if ((status == 4 || status == 2) && this.mIndicator.isInStartPosition()) {
            if (this.mUIHandlerHolder.hasHandler()) {
                this.mUIHandlerHolder.onUIReset(this);
            }
            LogUtil.d(TAG, "mStatus = PTR_STATUS_INIT");
            this.mStatus = (byte) 1;
            clearFlag();
        }
    }

    protected void onPtrScrollAbort() {
        if (this.mIndicator.hasLeftStartPosition() && isAutoRefresh()) {
            LogUtil.d(TAG, "onTouchRelease after scroll abort");
            onTouchRelease(true);
        }
    }

    protected void onPtrScrollFinish() {
        if (this.mIndicator.hasLeftStartPosition() && isAutoRefresh()) {
            LogUtil.d(TAG, "onTouchRelease after scroll finish");
            onTouchRelease(true);
        }
    }

    public boolean isRefreshing() {
        return this.mStatus == 3;
    }

    public final void refreshComplete() {
        UIRefreshHandlerHook hook = this.mRefreshCompleteHook;
        if (hook != null) {
            hook.reset();
        }
        long remainingTime = ((long) this.mLoadingMinTime) - (System.currentTimeMillis() - this.mLoadingStartTime);
        if (remainingTime <= 0) {
            performRefreshComplete();
        } else {
            postDelayed(this.mCompleteRunnable, remainingTime);
        }
    }

    public final void refreshComplete(boolean isSuccess) {
        this.mIsSuccess = isSuccess;
        UIRefreshHandlerHook hook = this.mRefreshCompleteHook;
        if (hook != null) {
            hook.reset();
        }
        performRefreshComplete(isSuccess);
    }

    private void performRefreshComplete() {
        this.mStatus = (byte) 4;
        LogUtil.d(TAG, "mStatus = PTR_STATUS_COMPLETE");
        if (this.mScrollChecker.mIsRunning && isAutoRefresh()) {
            return;
        }
        notifyUIRefreshComplete(false);
    }

    private void performRefreshComplete(boolean isSuccess) {
        this.mStatus = (byte) 4;
        LogUtil.d(TAG, "mStatus = PTR_STATUS_COMPLETE");
        if (this.mScrollChecker.mIsRunning && isAutoRefresh()) {
            return;
        }
        notifyUIRefreshComplete(false, isSuccess);
    }

    private void notifyUIRefreshComplete(boolean isScrollFinished) {
        UIRefreshHandlerHook hook;
        if (this.mIndicator.hasLeftStartPosition() && !isScrollFinished && (hook = this.mRefreshCompleteHook) != null) {
            hook.takeOver();
            return;
        }
        if (this.mUIHandlerHolder.hasHandler()) {
            this.mUIHandlerHolder.onUIRefreshComplete(this, false);
        }
        this.mIndicator.onUIRefreshComplete();
        tryScrollBackToTopAfterComplete();
        tryToNotifyReset();
    }

    private void notifyUIRefreshComplete(boolean isScrollFinished, boolean isSuccess) {
        UIRefreshHandlerHook hook;
        if (this.mIndicator.hasLeftStartPosition() && !isScrollFinished && (hook = this.mRefreshCompleteHook) != null) {
            hook.takeOver();
            return;
        }
        if (this.mUIHandlerHolder.hasHandler()) {
            this.mUIHandlerHolder.onUIRefreshComplete(this, isSuccess);
        }
        if (isSuccess) {
            postDelayed(this.mWaitRunnable, 700L);
        } else {
            tryScrollBackToTopAfterComplete();
        }
        this.mIndicator.onUIRefreshComplete();
        tryToNotifyReset();
    }

    public void autoRefresh() {
        autoRefresh(true, this.mDurationToCloseHeader);
    }

    public void autoRefresh(boolean atOnce) {
        autoRefresh(atOnce, this.mDurationToCloseHeader);
    }

    private void clearFlag() {
        this.mFlag &= -4;
    }

    public void autoRefresh(boolean atOnce, int duration) {
        if (this.mStatus != 1) {
            return;
        }
        this.mFlag |= atOnce ? 1 : 2;
        this.mStatus = (byte) 2;
        if (this.mUIHandlerHolder.hasHandler()) {
            this.mUIHandlerHolder.onUIRefreshPrepare(this);
        }
        this.mScrollChecker.tryToScrollTo(this.mIndicator.getOffsetToRefresh(), duration);
        if (atOnce) {
            this.mStatus = (byte) 3;
            performRefresh();
        }
    }

    public boolean isAutoRefresh() {
        return (this.mFlag & 3) > 0;
    }

    private boolean performAutoRefreshButLater() {
        return (this.mFlag & 3) == 2;
    }

    public void setEnabledNextPtrAtOnce(boolean enabled) {
        if (enabled) {
            this.mFlag |= 4;
        } else {
            this.mFlag &= -5;
        }
    }

    public boolean isEnabledNextPtrAtOnce() {
        return (this.mFlag & 4) > 0;
    }

    public void setPinContent(boolean pinContent) {
        if (pinContent) {
            this.mFlag |= 8;
        } else {
            this.mFlag &= -9;
        }
    }

    public boolean isPinContent() {
        return (this.mFlag & 8) > 0;
    }

    public void disableWhenHorizontalMove(boolean disable) {
        this.mDisableWhenHorizontalMove = disable;
    }

    public void setLoadingMinTime(int loadingMinTime) {
        this.mLoadingMinTime = loadingMinTime;
    }

    public View getContentView() {
        return this.mContent;
    }

    public void setCheckRefHelper(RefreshChecker refreshChecker) {
        this.mRefreshChecker = refreshChecker;
    }

    public void addUIRefreshHandler(UIRefreshHandler handler) {
        UIRefreshHandlerHolder.addHandler(this.mUIHandlerHolder, handler);
    }

    public void removePtrUIHandler(UIRefreshHandler handler) {
        this.mUIHandlerHolder = UIRefreshHandlerHolder.removeHandler(this.mUIHandlerHolder, handler);
    }

    public void setPtrIndicator(Indicator indicator) {
        Indicator currentIndicator = this.mIndicator;
        if (currentIndicator != null && currentIndicator != indicator) {
            indicator.convertFrom(currentIndicator);
        }
        this.mIndicator = indicator;
    }

    public float getResistance() {
        return this.mIndicator.getResistance();
    }

    public void setResistance(float resistance) {
        this.mIndicator.setResistance(resistance);
    }

    public float getDurationToClose() {
        return this.mDurationToClose;
    }

    public void setDurationToClose(int durationToClose) {
        this.mDurationToClose = durationToClose;
    }

    public long getDurationToCloseHeader() {
        return this.mDurationToCloseHeader;
    }

    public void setDurationToCloseHeader(int durationToCloseHeader) {
        this.mDurationToCloseHeader = durationToCloseHeader;
    }

    public void setRatioOfHeaderHeightToRefresh(float ratio) {
        this.mIndicator.setRatioOfHeaderHeightToRefresh(ratio);
    }

    public int getOffsetToRefresh() {
        return this.mIndicator.getOffsetToRefresh();
    }

    public void setOffsetToRefresh(int offsetToRefresh) {
        this.mIndicator.setOffsetToRefresh(offsetToRefresh);
    }

    public float getRatioOfHeaderToHeightRefresh() {
        return this.mIndicator.getRatioOfHeaderToHeightRefresh();
    }

    public void setOffsetToKeepHeaderWhileLoading(int offset) {
        this.mIndicator.setOffsetToKeepHeaderWhileLoading(offset);
    }

    public int getOffsetToKeepHeaderWhileLoading() {
        return this.mIndicator.getOffsetToKeepHeaderWhileLoading();
    }

    public boolean isKeepHeaderWhenRefresh() {
        return this.mKeepHeaderWhenRefresh;
    }

    public void setKeepHeaderWhenRefresh(boolean keepHeaderWhenRefresh) {
        this.mKeepHeaderWhenRefresh = keepHeaderWhenRefresh;
    }

    public boolean isPullToRefresh() {
        return this.mPullToRefresh;
    }

    public void setPullToRefreshEnable(boolean pullRefreshEnable) {
        this.mPullRefreshEnable = pullRefreshEnable;
    }

    public View getHeaderView() {
        return this.mHeader;
    }

    public void setHeaderView(View headerView) {
        View currentHeader = this.mHeader;
        if (currentHeader != null && headerView != null && currentHeader != headerView) {
            removeView(currentHeader);
        }
        if (headerView.getLayoutParams() == null) {
            headerView.setLayoutParams(new LayoutParams(-1, -2));
        }
        this.mHeader = headerView;
        addView(headerView);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams != null && (layoutParams instanceof LayoutParams);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-1, -1);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return new LayoutParams(layoutParams);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    private void sendCancelEvent() {
        MotionEvent lastMoveEvent = this.mLastMoveEvent;
        if (lastMoveEvent == null) {
            return;
        }
        dispatchTouchEventSupper(MotionEvent.obtain(lastMoveEvent.getDownTime(),
                lastMoveEvent.getEventTime() + ((long) ViewConfiguration.getLongPressTimeout()),
                3, lastMoveEvent.getX(), lastMoveEvent.getY(), lastMoveEvent.getMetaState()));
    }

    private void sendDownEvent() {
        MotionEvent lastMoveEvent = this.mLastMoveEvent;
        dispatchTouchEventSupper(MotionEvent.obtain(lastMoveEvent.getDownTime(), lastMoveEvent.getEventTime(),
                0, lastMoveEvent.getX(), lastMoveEvent.getY(), lastMoveEvent.getMetaState()));
    }

    public void abortedRefresh() {
        Runnable completeRunnable = this.mCompleteRunnable;
        if (completeRunnable != null) {
            removeCallbacks(completeRunnable);
        }
        Runnable waitRunnable = this.mWaitRunnable;
        if (waitRunnable != null) {
            removeCallbacks(waitRunnable);
        }
        ScrollChecker scrollChecker = this.mScrollChecker;
        if (scrollChecker != null) {
            scrollChecker.abortIfWorking();
            this.mScrollChecker.forceFinished();
        }
        UIRefreshHandlerHolder handlerHolder = this.mUIHandlerHolder;
        if (handlerHolder == null || !handlerHolder.hasHandler()) {
            return;
        }
        this.mUIHandlerHolder.onUIReset(this);
    }

    /** 自定义 LayoutParams。 */
    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    /** 平滑滚动执行器。 */
    class ScrollChecker implements Runnable {
        private boolean mIsRunning = false;
        private int mLastFlingY;
        private Scroller mScroller;
        private int mStart;

        public ScrollChecker() {
            this.mScroller = new Scroller(BaseFrameLayout.this.getContext());
        }

        @Override
        public void run() {
            boolean finished = !this.mScroller.computeScrollOffset() || this.mScroller.isFinished();
            int currentY = this.mScroller.getCurrY();
            int delta = currentY - this.mLastFlingY;
            if (!finished) {
                this.mLastFlingY = currentY;
                BaseFrameLayout.this.movePos(delta);
                BaseFrameLayout.this.post(this);
                return;
            }
            finish();
        }

        private void finish() {
            reset();
            BaseFrameLayout.this.onPtrScrollFinish();
        }

        private void reset() {
            this.mIsRunning = false;
            this.mLastFlingY = 0;
            BaseFrameLayout.this.removeCallbacks(this);
        }

        public void abortIfWorking() {
            if (this.mIsRunning) {
                forceFinished();
                BaseFrameLayout.this.onPtrScrollAbort();
                reset();
            }
        }

        public void tryToScrollTo(int target, int duration) {
            if (BaseFrameLayout.this.mIndicator.isAlreadyHere(target)) {
                return;
            }
            this.mStart = BaseFrameLayout.this.mIndicator.getCurrentPosY();
            int delta = target - this.mStart;
            BaseFrameLayout.this.removeCallbacks(this);
            forceFinished();
            this.mScroller.startScroll(0, 0, 0, delta, duration);
            this.mLastFlingY = 0;
            this.mIsRunning = true;
            BaseFrameLayout.this.post(this);
        }

        public void forceFinished() {
            Scroller scroller = this.mScroller;
            if (scroller == null || scroller.isFinished()) {
                return;
            }
            this.mScroller.forceFinished(true);
        }
    }
}