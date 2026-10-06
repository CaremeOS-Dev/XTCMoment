package com.xtc.moment.module.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;

/**
 * Vertical drawer container that can be dragged between a closed, an opened and (optionally) an
 * exit state.
 *
 * <p>The layout is used by the friend list page: dragging up opens the panel, dragging further
 * exits the page. The nested list is associated through {@link #setAssociatedListView} or
 * {@link #setAssociatedRecyclerView} so dragging is only enabled while the list is at its top.
 */
public class ScrollLayout extends FrameLayout {

    private static final String TAG = "ScrollLayout";

    /** Multiplier applied to the finger movement while dragging. */
    private static final float DRAG_SPEED_MULTIPLIER = 1.2f;
    /** Maximum distance in pixels a single drag step may move the layout. */
    private static final int DRAG_SPEED_SLOP = 30;
    /** Vertical fling velocity above which the layout snaps to the next state. */
    private static final int FLING_VELOCITY_SLOP = 80;
    /** Minimum vertical movement before a drag is recognized. */
    private static final int MOTION_DISTANCE_SLOP = 10;
    /** Base scroll duration in milliseconds. */
    private static final int MIN_SCROLL_DURATION = 100;
    /** Fraction of the travel used to decide between opened and closed on release. */
    private static final float SCROLL_TO_CLOSE_OFFSET_FACTOR = 0.5f;
    /** Fraction of the travel used to decide between opened and exit on release. */
    private static final float SCROLL_TO_EXIT_OFFSET_FACTOR = 0.8f;
    /** Scroll duration added per pixel of travel, in milliseconds. */
    private static final int SCROLL_DURATION_PER_PIXEL = 300;

    /** Initial state requested from xml. */
    private static final int MODE_OPEN = 0;
    private static final int MODE_CLOSE = 1;
    private static final int MODE_EXIT = 2;

    /** Internal states, finer grained than {@link Status}. */
    private enum InnerStatus {
        EXIT,
        OPENED,
        CLOSED,
        MOVING,
        SCROLLING
    }

    /** Public state of the drawer. */
    public enum Status {
        EXIT,
        OPENED,
        CLOSED
    }

    /** Receives the drawer scroll progress and the final state. */
    public interface OnScrollChangedListener {
        void onChildScroll(int dy);

        void onScrollFinished(Status status);

        void onScrollProgressChanged(float progress);
    }

    private final GestureDetector gestureDetector;
    private final GestureDetector.OnGestureListener gestureListener;
    private final AbsListView.OnScrollListener associatedListViewListener;
    private final RecyclerView.OnScrollListener associatedRecyclerViewListener;
    private final ContentScrollView.OnScrollChangedListener scrollViewListener;

    private final Scroller scroller;

    private InnerStatus currentInnerStatus = InnerStatus.OPENED;
    private Status lastFlingStatus = Status.CLOSED;

    private int maxOffset;
    public int minOffset;
    private int exitOffset;

    private boolean isEnable = true;
    private boolean isSupportExit;
    private boolean isAllowHorizontalScroll = true;
    private boolean isDraggable = true;
    private boolean isAllowPointerIntercepted = true;
    private boolean isCurrentPointerIntercepted;

    private float lastDownX;
    private float lastDownY;
    private float lastX;
    private float lastY;

    private ContentScrollView mScrollView;
    private OnScrollChangedListener onScrollChangedListener;

    public ScrollLayout(Context context) {
        this(context, null);
    }

    public ScrollLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.gestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (velocityY > FLING_VELOCITY_SLOP) {
                    if (Status.OPENED.equals(lastFlingStatus) && (-getScrollY()) > maxOffset) {
                        lastFlingStatus = Status.EXIT;
                        scrollToExit();
                    } else {
                        scrollToOpen();
                        lastFlingStatus = Status.OPENED;
                    }
                    return true;
                }
                if (velocityY >= FLING_VELOCITY_SLOP || getScrollY() > (-maxOffset)) {
                    if (velocityY >= FLING_VELOCITY_SLOP || getScrollY() <= (-maxOffset)) {
                        return false;
                    }
                    scrollToClose();
                    lastFlingStatus = Status.CLOSED;
                    return true;
                }
                scrollToOpen();
                lastFlingStatus = Status.OPENED;
                return true;
            }
        };

        this.associatedListViewListener = new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                updateListViewScrollState(view);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                updateListViewScrollState(view);
            }
        };

        this.associatedRecyclerViewListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                updateRecyclerViewScrollState(recyclerView);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                updateRecyclerViewScrollState(recyclerView);
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            this.scroller = new Scroller(getContext(), null, true);
        } else {
            this.scroller = new Scroller(getContext());
        }
        this.gestureDetector = new GestureDetector(getContext(), this.gestureListener);
        this.scrollViewListener = new ContentScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (mScrollView == null) {
                    return;
                }
                if (onScrollChangedListener != null) {
                    onScrollChangedListener.onChildScroll(oldScrollY);
                }
                setDraggable(mScrollView.getScrollY() == 0);
            }
        };
        initFromAttributes(context, attrs);
    }

    private void initFromAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScrollLayout);
        if (typedArray.hasValue(R.styleable.ScrollLayout_maxOffset)) {
            int offset = typedArray.getDimensionPixelOffset(R.styleable.ScrollLayout_maxOffset, this.maxOffset);
            if (offset != getScreenHeight()) {
                this.maxOffset = getScreenHeight() - offset;
            }
        }
        if (typedArray.hasValue(R.styleable.ScrollLayout_minOffset)) {
            this.minOffset = typedArray.getDimensionPixelOffset(R.styleable.ScrollLayout_minOffset, this.minOffset);
        }
        if (typedArray.hasValue(R.styleable.ScrollLayout_exitOffset)) {
            int offset = typedArray.getDimensionPixelOffset(R.styleable.ScrollLayout_exitOffset, getScreenHeight());
            if (offset != getScreenHeight()) {
                this.exitOffset = getScreenHeight() - offset;
            }
        }
        if (typedArray.hasValue(R.styleable.ScrollLayout_allowHorizontalScroll)) {
            this.isAllowHorizontalScroll = typedArray.getBoolean(R.styleable.ScrollLayout_allowHorizontalScroll, true);
        }
        if (typedArray.hasValue(R.styleable.ScrollLayout_isSupportExit)) {
            this.isSupportExit = typedArray.getBoolean(R.styleable.ScrollLayout_isSupportExit, true);
        }
        if (typedArray.hasValue(R.styleable.ScrollLayout_mode)) {
            int mode = typedArray.getInteger(R.styleable.ScrollLayout_mode, MODE_OPEN);
            if (mode == MODE_OPEN) {
                setToOpen();
            } else if (mode == MODE_EXIT) {
                setToExit();
            } else {
                setToClosed();
            }
        }
        typedArray.recycle();
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
        int max = this.maxOffset;
        int min = this.minOffset;
        if (max == min) {
            return;
        }
        int offset = -y;
        if (offset <= max) {
            onScrollProgressChanged((offset - min) / (float) (max - min));
        } else {
            onScrollProgressChanged((offset - max) / (float) (max - this.exitOffset));
        }
        if (y == (-this.minOffset)) {
            if (this.currentInnerStatus != InnerStatus.CLOSED) {
                this.currentInnerStatus = InnerStatus.CLOSED;
                onScrollFinished(Status.CLOSED);
            }
            return;
        }
        if (y == (-this.maxOffset)) {
            if (this.currentInnerStatus != InnerStatus.OPENED) {
                this.currentInnerStatus = InnerStatus.OPENED;
                onScrollFinished(Status.OPENED);
            }
            return;
        }
        if (this.isSupportExit && y == (-this.exitOffset) && this.currentInnerStatus != InnerStatus.EXIT) {
            this.currentInnerStatus = InnerStatus.EXIT;
            onScrollFinished(Status.EXIT);
        }
    }

    private void onScrollFinished(Status status) {
        OnScrollChangedListener listener = this.onScrollChangedListener;
        if (listener != null) {
            listener.onScrollFinished(status);
        }
    }

    private void onScrollProgressChanged(float progress) {
        OnScrollChangedListener listener = this.onScrollChangedListener;
        if (listener != null) {
            listener.onScrollProgressChanged(progress);
        }
    }

    @Override
    public void computeScroll() {
        if (this.scroller.isFinished() || !this.scroller.computeScrollOffset()) {
            return;
        }
        int currY = this.scroller.getCurrY();
        scrollTo(0, currY);
        if (currY == (-this.minOffset) || currY == (-this.maxOffset)
                || (this.isSupportExit && currY == (-this.exitOffset))) {
            LogUtil.d(TAG, "computeScroll: currY = " + currY + ", minOffset = " + this.minOffset
                    + ", maxOffset = " + this.maxOffset + ", isSupportExit = " + this.isSupportExit
                    + ", exitOffset = " + this.exitOffset);
            this.scroller.abortAnimation();
            return;
        }
        invalidate();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!this.isEnable) {
            return false;
        }
        if (!this.isDraggable && this.currentInnerStatus == InnerStatus.CLOSED) {
            return false;
        }
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            this.lastX = event.getX();
            this.lastY = event.getY();
            this.lastDownX = this.lastX;
            this.lastDownY = this.lastY;
            this.isAllowPointerIntercepted = true;
            this.isCurrentPointerIntercepted = false;
            if (!this.scroller.isFinished()) {
                this.scroller.forceFinished(true);
                this.currentInnerStatus = InnerStatus.MOVING;
                this.isCurrentPointerIntercepted = true;
                return true;
            }
            return false;
        }
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            this.isAllowPointerIntercepted = true;
            this.isCurrentPointerIntercepted = false;
            if (this.currentInnerStatus == InnerStatus.MOVING) {
                return true;
            }
            return false;
        }
        if (action != MotionEvent.ACTION_MOVE) {
            return false;
        }
        if (!this.isAllowPointerIntercepted) {
            return false;
        }
        if (this.isCurrentPointerIntercepted) {
            return true;
        }
        int deltaY = (int) (event.getY() - this.lastDownY);
        int deltaX = (int) (event.getX() - this.lastDownX);
        if (Math.abs(deltaY) < MOTION_DISTANCE_SLOP) {
            return false;
        }
        if (Math.abs(deltaY) < Math.abs(deltaX) && this.isAllowHorizontalScroll) {
            this.isAllowPointerIntercepted = false;
            this.isCurrentPointerIntercepted = false;
            return false;
        }
        if (this.currentInnerStatus == InnerStatus.CLOSED) {
            if (deltaY < 0) {
                return false;
            }
        } else if (this.currentInnerStatus == InnerStatus.OPENED && !this.isSupportExit && deltaY > 0) {
            return false;
        }
        this.isCurrentPointerIntercepted = true;
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!this.isCurrentPointerIntercepted) {
            return false;
        }
        this.gestureDetector.onTouchEvent(event);
        return move(event);
    }

    /** Handles a single drag step of the drawer. */
    public boolean move(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            this.lastY = event.getY();
            return true;
        }
        if (action == MotionEvent.ACTION_MOVE) {
            int deltaY = (int) ((event.getY() - this.lastY) * DRAG_SPEED_MULTIPLIER);
            int step = ((int) Math.signum(deltaY)) * Math.min(Math.abs(deltaY), DRAG_SPEED_SLOP);
            if (disposeEdgeValue(step)) {
                return true;
            }
            this.currentInnerStatus = InnerStatus.MOVING;
            int scrollY = getScrollY() - step;
            if (scrollY >= (-this.minOffset)) {
                scrollTo(0, -this.minOffset);
            } else if (scrollY <= (-this.maxOffset) && !this.isSupportExit) {
                scrollTo(0, -this.maxOffset);
            } else {
                scrollTo(0, scrollY);
            }
            this.lastY = event.getY();
            return true;
        }
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            if (this.currentInnerStatus != InnerStatus.MOVING) {
                return false;
            }
            completeMove();
            return true;
        }
        return false;
    }

    /** Returns true when the drag is already at the edge and should be ignored. */
    private boolean disposeEdgeValue(int step) {
        if (this.isSupportExit) {
            if (step > 0 || getScrollY() < (-this.minOffset)) {
                return step >= 0 && getScrollY() <= (-this.exitOffset);
            }
            return true;
        }
        if (step > 0 || getScrollY() < (-this.minOffset)) {
            return step >= 0 && getScrollY() <= (-this.maxOffset);
        }
        return true;
    }

    /** Snaps to the closest of the three states after a drag. */
    private void completeMove() {
        float openCloseThreshold = -((this.maxOffset - this.minOffset) * SCROLL_TO_CLOSE_OFFSET_FACTOR);
        if (getScrollY() > openCloseThreshold) {
            scrollToClose();
            return;
        }
        if (this.isSupportExit) {
            float exitThreshold = -(((this.exitOffset - this.maxOffset) * SCROLL_TO_EXIT_OFFSET_FACTOR) + this.maxOffset);
            if (getScrollY() <= openCloseThreshold && getScrollY() > exitThreshold) {
                scrollToOpen();
            } else {
                scrollToExit();
            }
            return;
        }
        scrollToOpen();
    }

    public void showOrHide() {
        if (this.currentInnerStatus == InnerStatus.OPENED) {
            scrollToClose();
        } else if (this.currentInnerStatus == InnerStatus.CLOSED) {
            scrollToOpen();
        }
    }

    public void scrollToOpen() {
        if (this.currentInnerStatus == InnerStatus.OPENED || this.maxOffset == this.minOffset) {
            return;
        }
        int dy = (-getScrollY()) - this.maxOffset;
        if (dy == 0) {
            return;
        }
        this.currentInnerStatus = InnerStatus.SCROLLING;
        this.scroller.startScroll(0, getScrollY(), 0, dy,
                Math.abs((dy * SCROLL_DURATION_PER_PIXEL) / (this.maxOffset - this.minOffset)) + MIN_SCROLL_DURATION);
        invalidate();
    }

    public int scrollToOpenSlowly() {
        if (this.currentInnerStatus == InnerStatus.OPENED) {
            LogUtil.d(TAG, "currentInnerStatus = OPENED");
            return 0;
        }
        if (this.maxOffset == this.minOffset) {
            LogUtil.d(TAG, "maxOffset == minOffset");
            return 0;
        }
        int dy = (-getScrollY()) - this.maxOffset;
        if (dy == 0) {
            LogUtil.d(TAG, "dy == 0");
            return 0;
        }
        this.currentInnerStatus = InnerStatus.SCROLLING;
        int duration = (Math.abs((dy * SCROLL_DURATION_PER_PIXEL) / (this.maxOffset - this.minOffset))
                + MIN_SCROLL_DURATION) * 3;
        int startY = getScrollY();
        if (startY == 0) {
            startY--;
        }
        LogUtil.d(TAG, "startScroll: startY = " + startY);
        this.scroller.startScroll(0, startY, 0, dy, duration);
        invalidate();
        return duration;
    }

    public void scrollToClose() {
        if (this.currentInnerStatus == InnerStatus.CLOSED || this.maxOffset == this.minOffset) {
            return;
        }
        int dy = (-getScrollY()) - this.minOffset;
        if (dy == 0) {
            return;
        }
        this.currentInnerStatus = InnerStatus.SCROLLING;
        this.scroller.startScroll(0, getScrollY(), 0, dy,
                Math.abs((dy * SCROLL_DURATION_PER_PIXEL) / (this.maxOffset - this.minOffset)) + MIN_SCROLL_DURATION);
        invalidate();
    }

    public void scrollToCloseSlowly() {
        if (this.currentInnerStatus == InnerStatus.CLOSED) {
            LogUtil.d(TAG, "currentInnerStatus = OPENED");
            return;
        }
        if (this.maxOffset == this.minOffset) {
            LogUtil.d(TAG, "maxOffset == minOffset");
            return;
        }
        int dy = (-getScrollY()) - this.minOffset;
        if (dy == 0) {
            LogUtil.d(TAG, "dy == 0");
            return;
        }
        this.currentInnerStatus = InnerStatus.SCROLLING;
        int duration = (Math.abs((dy * SCROLL_DURATION_PER_PIXEL) / (this.maxOffset - this.minOffset))
                + MIN_SCROLL_DURATION) * 3;
        int startY = getScrollY();
        if (startY == -340) {
            startY += 10;
        }
        LogUtil.d(TAG, "scrollToCloseSlowly: startY = " + startY);
        this.scroller.startScroll(0, startY, 0, dy, duration);
        invalidate();
    }

    public void scrollToExit() {
        if (!this.isSupportExit || this.currentInnerStatus == InnerStatus.EXIT
                || this.exitOffset == this.maxOffset) {
            return;
        }
        int dy = (-getScrollY()) - this.exitOffset;
        if (dy == 0) {
            return;
        }
        this.currentInnerStatus = InnerStatus.SCROLLING;
        this.scroller.startScroll(0, getScrollY(), 0, dy,
                Math.abs((dy * SCROLL_DURATION_PER_PIXEL) / (this.exitOffset - this.maxOffset)) + MIN_SCROLL_DURATION);
        invalidate();
    }

    public void setToOpen() {
        scrollTo(0, -this.maxOffset);
        this.currentInnerStatus = InnerStatus.OPENED;
        this.lastFlingStatus = Status.OPENED;
    }

    public void setToClosed() {
        scrollTo(0, -this.minOffset);
        this.currentInnerStatus = InnerStatus.CLOSED;
        this.lastFlingStatus = Status.CLOSED;
    }

    public void setToExit() {
        if (this.isSupportExit) {
            scrollTo(0, -this.exitOffset);
            this.currentInnerStatus = InnerStatus.EXIT;
        }
    }

    public void setMinOffset(int minOffset) {
        this.minOffset = minOffset;
    }

    public void setMaxOffset(int maxOffset) {
        this.maxOffset = getScreenHeight() - maxOffset;
    }

    public void setExitOffset(int exitOffset) {
        this.exitOffset = getScreenHeight() - exitOffset;
    }

    public void setEnable(boolean enable) {
        this.isEnable = enable;
    }

    public boolean isEnable() {
        return this.isEnable;
    }

    public void setIsSupportExit(boolean supportExit) {
        this.isSupportExit = supportExit;
    }

    public boolean isSupportExit() {
        return this.isSupportExit;
    }

    public boolean isAllowHorizontalScroll() {
        return this.isAllowHorizontalScroll;
    }

    public void setAllowHorizontalScroll(boolean allowHorizontalScroll) {
        this.isAllowHorizontalScroll = allowHorizontalScroll;
    }

    public boolean isDraggable() {
        return this.isDraggable;
    }

    public void setDraggable(boolean draggable) {
        this.isDraggable = draggable;
    }

    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        this.onScrollChangedListener = listener;
    }

    /** Current public state of the drawer. */
    public Status getCurrentStatus() {
        if (this.currentInnerStatus == InnerStatus.CLOSED) {
            return Status.CLOSED;
        }
        if (this.currentInnerStatus == InnerStatus.EXIT) {
            return Status.EXIT;
        }
        return Status.OPENED;
    }

    public void setAssociatedListView(AbsListView listView) {
        listView.setOnScrollListener(this.associatedListViewListener);
        updateListViewScrollState(listView);
    }

    public void setAssociatedRecyclerView(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(this.associatedRecyclerViewListener);
        updateRecyclerViewScrollState(recyclerView);
    }

    private void updateListViewScrollState(AbsListView listView) {
        if (listView.getChildCount() == 0) {
            setDraggable(true);
        } else if (listView.getFirstVisiblePosition() == 0
                && listView.getChildAt(0).getTop() == listView.getPaddingTop()) {
            setDraggable(true);
        } else {
            setDraggable(false);
        }
    }

    private void updateRecyclerViewScrollState(RecyclerView recyclerView) {
        if (recyclerView.getChildCount() == 0) {
            setDraggable(true);
            return;
        }
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        int[] firstVisiblePositions = new int[1];
        if (layoutManager instanceof LinearLayoutManager || layoutManager instanceof GridLayoutManager) {
            firstVisiblePositions[0] = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            firstVisiblePositions = ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(null);
        }
        if (firstVisiblePositions[0] == 0
                && recyclerView.getChildAt(0).getTop() == recyclerView.getPaddingTop()) {
            setDraggable(true);
        } else {
            setDraggable(false);
        }
    }

    public void setAssociatedScrollView(ContentScrollView scrollView) {
        this.mScrollView = scrollView;
        this.mScrollView.setScrollbarFadingEnabled(false);
        this.mScrollView.setOnScrollChangeListener(this.scrollViewListener);
    }

    public int getScreenHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public void startScroll(int startX, int startY, int dx, int dy) {
        this.scroller.startScroll(startX, startY, dx, dy);
        invalidate();
    }

    public Scroller getScroller() {
        return this.scroller;
    }
}