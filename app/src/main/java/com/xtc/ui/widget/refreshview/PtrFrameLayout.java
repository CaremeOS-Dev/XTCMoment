package com.xtc.ui.widget.refreshview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.Scroller;
import android.widget.TextView;
import com.xtc.log.LogUtil;
import com.xtc.ui.widget.R;
import com.xtc.ui.widget.refreshview.difviewhandler.GridViewHandler;
import com.xtc.ui.widget.refreshview.difviewhandler.ListViewHandler;
import com.xtc.ui.widget.refreshview.difviewhandler.RecyclerViewHandler;
import com.xtc.ui.widget.refreshview.interfaces.LoadMoreHandler;
import com.xtc.ui.widget.refreshview.interfaces.OnScrollBottomListener;
import com.xtc.ui.widget.refreshview.interfaces.OnScrollCallBackListener;
import com.xtc.ui.widget.refreshview.interfaces.PtlmHandler;
import com.xtc.ui.widget.refreshview.interfaces.PtlmUIHandler;
import com.xtc.ui.widget.refreshview.interfaces.PtrHandler;
import com.xtc.ui.widget.refreshview.interfaces.PtrUIHandler;
import com.xtc.ui.widget.refreshview.loadmorefooter.PtlmClassicDefaultFooter;

/** 下拉刷新 + 上拉加载更多容器，兼容 ListView/GridView/RecyclerView。 */
public class PtrFrameLayout extends ViewGroup {
    private static final boolean DEBUG = false;
    private static final byte FLAG_AUTO_REFRESH_AT_ONCE = 1;
    private static final byte FLAG_AUTO_REFRESH_BUT_LATER = 2;
    private static final byte FLAG_ENABLE_NEXT_PTR_AT_ONCE = 4;
    private static final byte FLAG_PIN_CONTENT = 8;
    private static final byte MASK_AUTO_REFRESH = 3;
    private static final byte PTR_STATUS_COMPLETE = 4;
    private static final byte PTR_STATUS_INIT = 1;
    private static final byte PTR_STATUS_LOADING = 3;
    public static final byte PTR_STATUS_PREPARE = 2;
    private static final String TAG = "PtrFrameLayout";
    private boolean hasInitLoadMoreView;
    private boolean isAutoLoadMoreEnable;
    private boolean isLoadMoreEnable;
    private boolean isLoadingMore;
    private PtlmUIHandler loadMoreViewFactory;
    private int mContainerId;
    protected View mContent;
    private View mContentView;
    private boolean mDisableWhenHorizontalMove;
    private int mDurationToClose;
    private int mDurationToCloseHeader;
    private int mFlag;
    private View mFooterView;
    private boolean mHasSendCancelEvent;
    private int mHeaderHeight;
    private int mHeaderId;
    private View mHeaderView;
    private boolean mKeepHeaderWhenRefresh;
    private MotionEvent mLastMoveEvent;
    private LoadMoreHandler mLoadMoreHandler;
    private PtlmUIHandler.ILoadMoreView mLoadMoreView;
    private int mLoadingMinTime;
    private long mLoadingStartTime;
    private int mPagingTouchSlop;
    private boolean mPreventForHorizontal;
    PtlmHandler mPtlmHandler;
    private PtrHandler mPtrHandler;
    private PtrIndicator mPtrIndicator;
    private PtrUIHandlerHolder mPtrUIHandlerHolder;
    private boolean mPullToRefresh;
    private PtrUIHandlerHook mRefreshCompleteHook;
    private ScrollChecker mScrollChecker;
    private byte mStatus;
    private View.OnClickListener onClickLoadMoreListener;
    private OnScrollBottomListener onScrollBottomListener;
    OnScrollCallBackListener onScrollCallBackListener;
    private boolean showFooterView;

    protected void onPositionChange(boolean isUnderTouch, byte status, PtrIndicator indicator) {
    }

    @Deprecated
    public void setInterceptEventWhileWorking(boolean intercept) {
    }

    public PtrFrameLayout(Context context) {
        this(context, null);
    }

    public PtrFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PtrFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mHeaderId = 0;
        this.mContainerId = 0;
        this.mDurationToClose = 200;
        this.mDurationToCloseHeader = 1000;
        this.mPullToRefresh = false;
        this.mKeepHeaderWhenRefresh = true;
        this.mPtrUIHandlerHolder = PtrUIHandlerHolder.create();
        this.mStatus = (byte) 1;
        this.mDisableWhenHorizontalMove = false;
        this.mFlag = 0;
        this.mPreventForHorizontal = false;
        this.mLoadingMinTime = 500;
        this.mLoadingStartTime = 0L;
        this.mHasSendCancelEvent = false;
        this.isLoadingMore = false;
        this.isAutoLoadMoreEnable = true;
        this.isLoadMoreEnable = false;
        this.hasInitLoadMoreView = false;
        this.onScrollBottomListener = new OnScrollBottomListener() {
            @Override
            public void onScorllBootom() {
                if (PtrFrameLayout.this.isAutoLoadMoreEnable && PtrFrameLayout.this.isLoadMoreEnable
                        && !PtrFrameLayout.this.isLoadingMore()) {
                    PtrFrameLayout.this.loadMore();
                } else {
                    LogUtil.d("test", " not loadMore--->>");
                }
            }
        };
        this.onClickLoadMoreListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PtrFrameLayout.this.isLoadMoreEnable || PtrFrameLayout.this.isLoadingMore()) {
                    return;
                }
                PtrFrameLayout.this.loadMore();
            }
        };
        this.mPtrIndicator = new PtrIndicator();
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.PtrFrameLayout, 0, 0);
        if (attributes != null) {
            this.mHeaderId = attributes.getResourceId(R.styleable.PtrFrameLayout_ptr_header, this.mHeaderId);
            this.mContainerId = attributes.getResourceId(R.styleable.PtrFrameLayout_ptr_content, this.mContainerId);
            this.mPtrIndicator.setResistance(attributes.getFloat(R.styleable.PtrFrameLayout_ptr_resistance, this.mPtrIndicator.getResistance()));
            this.mDurationToClose = attributes.getInt(R.styleable.PtrFrameLayout_ptr_duration_to_close, this.mDurationToClose);
            this.mDurationToCloseHeader = attributes.getInt(R.styleable.PtrFrameLayout_ptr_duration_to_close_header, this.mDurationToCloseHeader);
            this.mPtrIndicator.setRatioOfHeaderHeightToRefresh(attributes.getFloat(R.styleable.PtrFrameLayout_ptr_ratio_of_header_height_to_refresh, this.mPtrIndicator.getRatioOfHeaderToHeightRefresh()));
            this.mKeepHeaderWhenRefresh = attributes.getBoolean(R.styleable.PtrFrameLayout_ptr_keep_header_when_refresh, this.mKeepHeaderWhenRefresh);
            this.mPullToRefresh = attributes.getBoolean(R.styleable.PtrFrameLayout_ptr_pull_to_fresh, this.mPullToRefresh);
            attributes.recycle();
        }
        this.mScrollChecker = new ScrollChecker();
        this.mPagingTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop() * 2;
    }

    @Override
    protected void onFinishInflate() {
        int childCount = getChildCount();
        if (childCount > 2) {
            throw new IllegalStateException("PtrFrameLayout only can host 2 elements");
        }
        if (childCount == 2) {
            int headerId = this.mHeaderId;
            if (headerId != 0 && this.mHeaderView == null) {
                this.mHeaderView = findViewById(headerId);
            }
            int containerId = this.mContainerId;
            if (containerId != 0 && this.mContent == null) {
                this.mContent = findViewById(containerId);
            }
            if (this.mContent == null || this.mHeaderView == null) {
                View firstChild = getChildAt(0);
                View secondChild = getChildAt(1);
                if (firstChild instanceof PtrUIHandler) {
                    this.mHeaderView = firstChild;
                    this.mContent = secondChild;
                } else if (secondChild instanceof PtrUIHandler) {
                    this.mHeaderView = secondChild;
                    this.mContent = firstChild;
                } else if (this.mContent == null && this.mHeaderView == null) {
                    this.mHeaderView = firstChild;
                    this.mContent = secondChild;
                } else {
                    View headerView = this.mHeaderView;
                    if (headerView == null) {
                        if (this.mContent == firstChild) {
                            firstChild = secondChild;
                        }
                        this.mHeaderView = firstChild;
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
            emptyView.setText("The content view in PtrFrameLayout is empty. Do you forget to specify its id in xml layout file?");
            this.mContent = emptyView;
            addView(this.mContent);
        }
        View headerView = this.mHeaderView;
        if (headerView != null) {
            headerView.bringToFront();
        }
        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        View headerView = this.mHeaderView;
        if (headerView != null) {
            measureChildWithMargins(headerView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) this.mHeaderView.getLayoutParams();
            this.mHeaderHeight = this.mHeaderView.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
            this.mPtrIndicator.setHeaderHeight(this.mHeaderHeight);
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
        int currentPosY = this.mPtrIndicator.getCurrentPosY();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        View headerView = this.mHeaderView;
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
        if (!isEnabled() || this.mContent == null || this.mHeaderView == null) {
            return dispatchTouchEventSupper(event);
        }
        int action = event.getAction();
        if (action == 0) {
            if (this.mLoadMoreHandler != null) {
                this.mLoadMoreHandler.refreshDirection(0);
            }
            this.mHasSendCancelEvent = false;
            this.mPtrIndicator.onPressDown(event.getX(), event.getY());
            this.mScrollChecker.abortIfWorking();
            this.mPreventForHorizontal = false;
            dispatchTouchEventSupper(event);
            return true;
        }
        if (action == 1 || action == 3) {
            this.mPtrIndicator.onRelease();
            if (this.mPtrIndicator.hasLeftStartPosition()) {
                onRelease(false);
                if (this.mPtrIndicator.hasMovedAfterPressedDown()) {
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
        this.mPtrIndicator.onMove(event.getX(), event.getY());
        LogUtil.d("test", "e.getX()---->>" + event.getX() + "," + event.getY());
        float offsetX = this.mPtrIndicator.getOffsetX();
        float offsetY = this.mPtrIndicator.getOffsetY();
        if (this.mDisableWhenHorizontalMove && !this.mPreventForHorizontal
                && Math.abs(offsetX) > this.mPagingTouchSlop
                && Math.abs(offsetX) > Math.abs(offsetY)
                && this.mPtrIndicator.isInStartPosition()) {
            this.mPreventForHorizontal = true;
        }
        if (this.mPreventForHorizontal) {
            return dispatchTouchEventSupper(event);
        }
        boolean isDown = offsetY > 0.0f;
        boolean isUp = !isDown;
        if (this.mLoadMoreHandler != null) {
            this.mLoadMoreHandler.refreshDirection(isDown ? 1 : 2);
        }
        boolean hasLeftStartPosition = this.mPtrIndicator.hasLeftStartPosition();
        int maskedAction = MotionEventCompat.getActionMasked(event);
        boolean canRecyclerViewScrollUp = canRecyclerViewScrollUp(maskedAction);
        if (isDown && this.mPullToRefresh) {
            return dispatchTouchEventSupper(event);
        }
        if (isDown && this.mPtrHandler != null && canRecyclerViewScrollUp) {
            return dispatchTouchEventSupper(event);
        }
        if (isUp && hasLeftStartPosition) {
            return dispatchTouchEventSupper(event);
        }
        movePos(offsetY);
        return true;
    }

    private boolean canRecyclerViewScrollUp(int actionMasked) {
        View content = this.mContent;
        if (content instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) content;
            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager.findFirstVisibleItemPosition() == 0) {
                    int firstCompletelyVisible = layoutManager.findFirstCompletelyVisibleItemPosition();
                    View firstChild = recyclerView.getChildAt(0);
                    if (firstChild == null) {
                        return false;
                    }
                    if (firstCompletelyVisible == 0) {
                        if (firstChild.getHeight() == 0) {
                            return false;
                        }
                    } else {
                        int decoratedTop = layoutManager.getDecoratedTop(firstChild);
                        if (actionMasked == 2 && decoratedTop == 0) {
                            return false;
                        }
                    }
                }
            }
            return ViewCompat.canScrollVertically(recyclerView, -1);
        }
        return ViewCompat.canScrollVertically(content, -1);
    }    private void movePos(float deltaY) {
        if (deltaY < 0.0f && this.mPtrIndicator.isInStartPosition()) {
            LogUtil.e(TAG, "has reached the top");
            return;
        }
        int currentPosY = this.mPtrIndicator.getCurrentPosY() + ((int) deltaY);
        int maxPos = this.mHeaderHeight;
        if (currentPosY <= maxPos || !this.mPullToRefresh) {
            maxPos = currentPosY;
        }
        this.mPtrIndicator.setCurrentPos(maxPos);
        updatePos(maxPos - this.mPtrIndicator.getLastPosY());
    }

    private void updatePos(int delta) {
        if (delta == 0) {
            return;
        }
        boolean isUnderTouch = this.mPtrIndicator.isUnderTouch();
        if (isUnderTouch && !this.mHasSendCancelEvent && this.mPtrIndicator.hasMovedAfterPressedDown()) {
            this.mHasSendCancelEvent = true;
            sendCancelEvent();
        }
        if ((this.mPtrIndicator.hasJustLeftStartPosition() && this.mStatus == 1)
                || (this.mPtrIndicator.goDownCrossFinishPosition() && this.mStatus == 4 && isEnabledNextPtrAtOnce())) {
            this.mStatus = (byte) 2;
            this.mPtrUIHandlerHolder.onUIRefreshPrepare(this);
            LogUtil.d(TAG, "PtrUIHandler: onUIRefreshPrepare, mFlag %s" + this.mFlag);
        }
        if (this.mPtrIndicator.hasJustBackToStartPosition()) {
            tryToNotifyReset();
            if (isUnderTouch) {
                sendDownEvent();
            }
        }
        if (this.mStatus == 2) {
            if (isUnderTouch && !isAutoRefresh() && this.mPullToRefresh && this.mPtrIndicator.crossRefreshLineFromTopToBottom()) {
                tryToPerformRefresh();
            }
            if (performAutoRefreshButLater() && this.mPtrIndicator.hasJustReachedHeaderHeightFromTopToBottom()) {
                tryToPerformRefresh();
            }
        }
        this.mHeaderView.offsetTopAndBottom(delta);
        if (!isPinContent()) {
            this.mContent.offsetTopAndBottom(delta);
        }
        invalidate();
        if (this.mPtrUIHandlerHolder.hasHandler()) {
            LogUtil.d("test", "mPtrUIHandlerHolder.onUIPositionChange----->>");
            this.mPtrUIHandlerHolder.onUIPositionChange(this, isUnderTouch, this.mStatus, this.mPtrIndicator);
        }
        onPositionChange(isUnderTouch, this.mStatus, this.mPtrIndicator);
    }

    public int getHeaderHeight() {
        return this.mHeaderHeight;
    }

    private void onRelease(boolean stayForLoading) {
        tryToPerformRefresh();
        byte status = this.mStatus;
        if (status != 3) {
            if (status == 4) {
                notifyUIRefreshComplete(false);
                return;
            }
            tryScrollBackToTop();
            return;
        }
        if (this.mKeepHeaderWhenRefresh) {
            if (!this.mPtrIndicator.isOverOffsetToKeepHeaderWhileLoading() || stayForLoading) {
                return;
            }
            this.mScrollChecker.tryToScrollTo(this.mPtrIndicator.getOffsetToKeepHeaderWhileLoading(), this.mDurationToClose);
            return;
        }
        tryScrollBackToTop();
    }

    public void setRefreshCompleteHook(PtrUIHandlerHook hook) {
        this.mRefreshCompleteHook = hook;
        hook.setResumeAction(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(PtrFrameLayout.TAG, "mRefreshCompleteHook resume.");
                PtrFrameLayout.this.notifyUIRefreshComplete(true);
            }
        });
    }

    private void tryScrollBackToTop() {
        if (this.mPtrIndicator.isUnderTouch()) {
            return;
        }
        this.mScrollChecker.tryToScrollTo(0, this.mDurationToCloseHeader);
    }

    private boolean tryToPerformRefresh() {
        if (this.mStatus != 2) {
            return false;
        }
        if ((this.mPtrIndicator.isOverOffsetToKeepHeaderWhileLoading() && isAutoRefresh())
                || this.mPtrIndicator.isOverOffsetToRefresh()) {
            this.mStatus = (byte) 3;
            performRefresh();
        }
        return false;
    }

    private void performRefresh() {
        this.mLoadingStartTime = System.currentTimeMillis();
        if (this.mPtrUIHandlerHolder.hasHandler()) {
            this.mPtrUIHandlerHolder.onUIRefreshBegin(this);
        }
        PtrHandler ptrHandler = this.mPtrHandler;
        if (ptrHandler != null) {
            ptrHandler.onBeginRefreshing(this);
        }
    }

    private boolean tryToNotifyReset() {
        byte status = this.mStatus;
        if ((status != 4 && status != 2) || !this.mPtrIndicator.isInStartPosition()) {
            return false;
        }
        if (this.mPtrUIHandlerHolder.hasHandler()) {
            this.mPtrUIHandlerHolder.onUIReset(this);
        }
        this.mStatus = (byte) 1;
        clearFlag();
        return true;
    }

    protected void onPtrScrollAbort() {
        if (this.mPtrIndicator.hasLeftStartPosition() && isAutoRefresh()) {
            onRelease(true);
        }
    }

    protected void onPtrScrollFinish() {
        if (this.mPtrIndicator.hasLeftStartPosition() && isAutoRefresh()) {
            onRelease(true);
        }
    }

    public boolean isRefreshing() {
        return this.mStatus == 3;
    }

    public final void endRefreshing() {
        PtrUIHandlerHook hook = this.mRefreshCompleteHook;
        if (hook != null) {
            hook.reset();
        }
        int remainingTime = (int) (((long) this.mLoadingMinTime) - (System.currentTimeMillis() - this.mLoadingStartTime));
        if (remainingTime <= 0) {
            performRefreshComplete();
        } else {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    PtrFrameLayout.this.performRefreshComplete();
                }
            }, remainingTime);
        }
    }

    private void performRefreshComplete() {
        this.mStatus = (byte) 4;
        if (this.mScrollChecker.mIsRunning && isAutoRefresh()) {
            return;
        }
        notifyUIRefreshComplete(false);
    }

    private void notifyUIRefreshComplete(boolean isScrollFinished) {
        PtrUIHandlerHook hook;
        if (this.mPtrIndicator.hasLeftStartPosition() && !isScrollFinished && (hook = this.mRefreshCompleteHook) != null) {
            hook.takeOver();
            return;
        }
        if (this.mPtrUIHandlerHolder.hasHandler()) {
            this.mPtrUIHandlerHolder.onUIRefreshComplete(this);
        }
        this.mPtrIndicator.onUIRefreshComplete();
        tryScrollBackToTop();
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
        if (this.mPtrUIHandlerHolder.hasHandler()) {
            this.mPtrUIHandlerHolder.onUIRefreshPrepare(this);
        }
        this.mScrollChecker.tryToScrollTo(this.mPtrIndicator.getOffsetToRefresh(), duration);
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

    public void setPullToRefreshHandler(PtrHandler ptrHandler) {
        this.mPtrHandler = ptrHandler;
    }

    public void addPtrUIHandler(PtrUIHandler ptrUIHandler) {
        PtrUIHandlerHolder.addHandler(this.mPtrUIHandlerHolder, ptrUIHandler);
    }

    public void removePtrUIHandler(PtrUIHandler ptrUIHandler) {
        this.mPtrUIHandlerHolder = PtrUIHandlerHolder.removeHandler(this.mPtrUIHandlerHolder, ptrUIHandler);
    }

    public void setPtrIndicator(PtrIndicator indicator) {
        PtrIndicator currentIndicator = this.mPtrIndicator;
        if (currentIndicator != null && currentIndicator != indicator) {
            indicator.convertFrom(currentIndicator);
        }
        this.mPtrIndicator = indicator;
    }

    public float getResistance() {
        return this.mPtrIndicator.getResistance();
    }

    public void setResistance(float resistance) {
        this.mPtrIndicator.setResistance(resistance);
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
        this.mPtrIndicator.setRatioOfHeaderHeightToRefresh(ratio);
    }

    public int getOffsetToRefresh() {
        return this.mPtrIndicator.getOffsetToRefresh();
    }

    public void setOffsetToRefresh(int offsetToRefresh) {
        this.mPtrIndicator.setOffsetToRefresh(offsetToRefresh);
    }

    public float getRatioOfHeaderToHeightRefresh() {
        return this.mPtrIndicator.getRatioOfHeaderToHeightRefresh();
    }

    public void setOffsetToKeepHeaderWhileLoading(int offset) {
        this.mPtrIndicator.setOffsetToKeepHeaderWhileLoading(offset);
    }

    public int getOffsetToKeepHeaderWhileLoading() {
        return this.mPtrIndicator.getOffsetToKeepHeaderWhileLoading();
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

    public void setPullToRefresh(boolean pullToRefresh) {
        this.mPullToRefresh = pullToRefresh;
    }

    public View getHeaderView() {
        return this.mHeaderView;
    }

    public void setHeaderView(View headerView) {
        View currentHeader = this.mHeaderView;
        if (currentHeader != null && headerView != null && currentHeader != headerView) {
            removeView(currentHeader);
        }
        if (headerView.getLayoutParams() == null) {
            headerView.setLayoutParams(new LayoutParams(-1, -2));
        }
        this.mHeaderView = headerView;
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

    /** 平滑滚动到指定位置的执行器。 */
    class ScrollChecker implements Runnable {
        private boolean mIsRunning = false;
        private int mLastFlingY;
        private Scroller mScroller;
        private int mStart;
        private int mTo;

        public ScrollChecker() {
            this.mScroller = new Scroller(PtrFrameLayout.this.getContext());
        }

        @Override
        public void run() {
            boolean finished = !this.mScroller.computeScrollOffset() || this.mScroller.isFinished();
            int currentY = this.mScroller.getCurrY();
            int delta = currentY - this.mLastFlingY;
            if (!finished) {
                this.mLastFlingY = currentY;
                PtrFrameLayout.this.movePos(delta);
                PtrFrameLayout.this.post(this);
                return;
            }
            finish();
        }

        private void finish() {
            reset();
            PtrFrameLayout.this.onPtrScrollFinish();
        }

        private void reset() {
            this.mIsRunning = false;
            this.mLastFlingY = 0;
            PtrFrameLayout.this.removeCallbacks(this);
        }

        public void abortIfWorking() {
            if (this.mIsRunning) {
                if (!this.mScroller.isFinished()) {
                    this.mScroller.forceFinished(true);
                }
                PtrFrameLayout.this.onPtrScrollAbort();
                reset();
            }
        }

        public void tryToScrollTo(int target, int duration) {
            if (PtrFrameLayout.this.mPtrIndicator.isAlreadyHere(target)) {
                return;
            }
            this.mStart = PtrFrameLayout.this.mPtrIndicator.getCurrentPosY();
            this.mTo = target;
            int delta = target - this.mStart;
            PtrFrameLayout.this.removeCallbacks(this);
            this.mLastFlingY = 0;
            if (!this.mScroller.isFinished()) {
                this.mScroller.forceFinished(true);
            }
            this.mScroller.startScroll(0, 0, 0, delta, duration);
            PtrFrameLayout.this.post(this);
            this.mIsRunning = true;
        }
    }

    public void setAutoLoadMoreEnable(boolean autoLoadMoreEnable) {
        this.isAutoLoadMoreEnable = autoLoadMoreEnable;
    }

    public void setFooterView(PtlmUIHandler loadMoreViewFactory) {
        if (loadMoreViewFactory != null) {
            PtlmUIHandler currentFactory = this.loadMoreViewFactory;
            if (currentFactory == null || currentFactory != loadMoreViewFactory) {
                this.loadMoreViewFactory = loadMoreViewFactory;
                if (this.hasInitLoadMoreView) {
                    this.mLoadMoreHandler.removeFooter();
                    this.mLoadMoreView = this.loadMoreViewFactory.madeLoadMoreView();
                    this.hasInitLoadMoreView = this.mLoadMoreHandler.handleSetAdapter(this.mContentView, this.mLoadMoreView, this.onClickLoadMoreListener);
                    if (this.isLoadMoreEnable) {
                        return;
                    }
                    this.mLoadMoreHandler.removeFooter();
                }
            }
        }
    }

    public void setPullToLoadMoreHandler(PtlmHandler ptlmHandler) {
        this.mPtlmHandler = ptlmHandler;
    }

    public void setLoadMoreEnable(boolean loadMoreEnable) {
        if (this.isLoadMoreEnable == loadMoreEnable) {
            return;
        }
        this.isLoadMoreEnable = loadMoreEnable;
        if (!this.hasInitLoadMoreView && this.isLoadMoreEnable) {
            this.mContentView = getContentView();
            if (this.loadMoreViewFactory == null) {
                this.loadMoreViewFactory = new PtlmClassicDefaultFooter();
            }
            this.mLoadMoreView = this.loadMoreViewFactory.madeLoadMoreView();
            boolean needScrollCallback = true;
            if (this.mLoadMoreHandler == null) {
                View contentView = this.mContentView;
                if (contentView instanceof GridView) {
                    this.mLoadMoreHandler = new GridViewHandler();
                } else if (contentView instanceof AbsListView) {
                    this.mLoadMoreHandler = new ListViewHandler();
                } else {
                    if (contentView instanceof RecyclerView) {
                        this.mLoadMoreHandler = new RecyclerViewHandler();
                    }
                    needScrollCallback = false;
                }
            } else {
                needScrollCallback = false;
            }
            LoadMoreHandler loadMoreHandler = this.mLoadMoreHandler;
            if (loadMoreHandler == null) {
                throw new IllegalStateException("unSupported contentView !");
            }
            this.hasInitLoadMoreView = loadMoreHandler.handleSetAdapter(this.mContentView, this.mLoadMoreView, this.onClickLoadMoreListener);
            if (!needScrollCallback) {
                this.mLoadMoreHandler.setOnScrollBottomListener(this.mContentView, this.onScrollBottomListener);
                return;
            } else {
                this.mLoadMoreHandler.setOnScrollBottomCallBackListener(this.mContentView, this.onScrollBottomListener, this.onScrollCallBackListener);
                return;
            }
        }
        if (this.hasInitLoadMoreView) {
            if (this.isLoadMoreEnable) {
                this.mLoadMoreHandler.addFooter();
            } else {
                this.mLoadMoreHandler.removeFooter();
            }
        }
    }

    public void setOnScrollCallBackListener(OnScrollCallBackListener listener) {
        this.onScrollCallBackListener = listener;
    }

    public void setNoMoreData() {
        this.mLoadMoreView.showNoMore();
    }

    public void loadMore() {
        this.isLoadingMore = true;
        this.mLoadMoreView.showLoading();
        this.mPtlmHandler.onLoadMore();
    }

    public void loadFailed(Exception exception) {
        this.mLoadMoreView.showFail(exception);
    }

    public void loadMoreHide() {
        this.mLoadMoreView.hideView();
    }

    public void loadMoreComplete(boolean loadMoreEnable) {
        this.isLoadingMore = false;
        this.isLoadMoreEnable = loadMoreEnable;
        if (loadMoreEnable) {
            this.mLoadMoreView.showNormal();
        } else {
            setNoMoreData();
        }
    }

    public boolean isLoadingMore() {
        return this.isLoadingMore;
    }

    public boolean isLoadMoreEnable() {
        return this.isLoadMoreEnable;
    }
}