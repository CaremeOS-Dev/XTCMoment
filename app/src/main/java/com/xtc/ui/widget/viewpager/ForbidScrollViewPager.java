package com.xtc.ui.widget.viewpager;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.KeyEventCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 从 support-v4 ViewPager 抽取并改造的页面容器。
 * 在原版基础上增加了 {@link #setForbidScroll(boolean)} 用于整体禁止手势滑动，
 * 以及 {@link #setClassMode(boolean)} 用于课程模式下的边界处理（右边界固定在 0）。
 * 页面切换回调相比原版多携带选中页的下标、目标下标与滚动时长信息。
 */
public class ForbidScrollViewPager extends ViewGroup {
    private static final int CLOSE_ENOUGH = 2;
    private static final boolean DEBUG = false;
    private static final int DEFAULT_GUTTER_SIZE = 16;
    private static final int DEFAULT_OFFSCREEN_PAGES = 1;
    private static final int DRAW_ORDER_DEFAULT = 0;
    private static final int DRAW_ORDER_FORWARD = 1;
    private static final int DRAW_ORDER_REVERSE = 2;
    private static final int INVALID_POINTER = -1;
    private static final int MAX_SETTLE_DURATION = 600;
    private static final int MIN_DISTANCE_FOR_FLING = 25;
    private static final int MIN_FLING_VELOCITY = 400;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_SETTLING = 2;
    private static final boolean USE_CACHE = false;
    private boolean isClassMode;
    private boolean isForbidScroll;
    private int mActivePointerId;
    private PagerAdapter mAdapter;
    private OnAdapterChangeListener mAdapterChangeListener;
    private int mBottomPageBounds;
    private boolean mCalledSuper;
    private int mChildHeightMeasureSpec;
    private int mChildWidthMeasureSpec;
    private int mCloseEnough;
    private int mCurItem;
    private int mDecorChildCount;
    private int mDefaultGutterSize;
    private int mDrawingOrder;
    private ArrayList<View> mDrawingOrderedChildren;
    private final Runnable mEndScrollRunnable;
    private int mExpectedAdapterCount;
    private long mFakeDragBeginTime;
    private boolean mFakeDragging;
    private boolean mFirstLayout;
    private float mFirstOffset;
    private int mFlingDistance;
    private int mGutterSize;
    private boolean mInLayout;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private OnPageChangeListener mInternalPageChangeListener;
    private boolean mIsBeingDragged;
    private boolean mIsScrollStarted;
    private boolean mIsUnableToDrag;
    private final ArrayList<ItemInfo> mItems;
    private float mLastMotionX;
    private float mLastMotionY;
    private float mLastOffset;
    private EdgeEffectCompat mLeftEdge;
    private Drawable mMarginDrawable;
    private int mMaximumVelocity;
    private int mMinimumVelocity;
    private boolean mNeedCalculatePageOffsets;
    private PagerObserver mObserver;
    private int mOffscreenPageLimit;
    private OnPageChangeListener mOnPageChangeListener;
    private List<OnPageChangeListener> mOnPageChangeListeners;
    private int mPageMargin;
    private PageTransformer mPageTransformer;
    private boolean mPopulatePending;
    private Parcelable mRestoredAdapterState;
    private ClassLoader mRestoredClassLoader;
    private int mRestoredCurItem;
    private EdgeEffectCompat mRightEdge;
    private int mScrollState;
    private Scroller mScroller;
    private boolean mScrollingCacheEnabled;
    private Method mSetChildrenDrawingOrderEnabled;
    private final ItemInfo mTempItem;
    private final Rect mTempRect;
    private int mTopPageBounds;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;
    private static final String TAG = ForbidScrollViewPager.class.getSimpleName();
    private static final int[] LAYOUT_ATTRS = {android.R.attr.layout_gravity};
    private static final Comparator<ItemInfo> COMPARATOR = new Comparator<ItemInfo>() {
        @Override
        public int compare(ItemInfo first, ItemInfo second) {
            return first.position - second.position;
        }
    };
    private static final Interpolator sInterpolator = new Interpolator() {
        @Override
        public float getInterpolation(float input) {
            float t = input - 1.0f;
            return 1.0f - (((t * t) * t) * t);
        }
    };
    private static final ViewPositionComparator sPositionComparator = new ViewPositionComparator();

    interface Decor {
    }

    interface OnAdapterChangeListener {
        void onAdapterChanged(PagerAdapter oldAdapter, PagerAdapter newAdapter);
    }

    /** 页面切换监听器，onPageSelected 额外携带旧下标、新下标与滚动耗时。 */
    public interface OnPageChangeListener {
        void onPageScrollStateChanged(int state);

        void onPageScrolled(int position, float offset, int offsetPixels);

        void onPageSelected(int oldPosition, int newPosition, int duration, long scrollDuration);
    }

    public interface PageTransformer {
        void transformPage(View page, float position);
    }

    public static class SimpleOnPageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageScrolled(int position, float offset, int offsetPixels) {
        }

        @Override
        public void onPageSelected(int oldPosition, int newPosition, int duration, long scrollDuration) {
        }
    }

    static class ItemInfo {
        Object object;
        float offset;
        int position;
        boolean scrolling;
        float widthFactor;

        ItemInfo() {
        }
    }

    public ForbidScrollViewPager(Context context) {
        super(context);
        this.mItems = new ArrayList<>();
        this.mTempItem = new ItemInfo();
        this.mTempRect = new Rect();
        this.mRestoredCurItem = -1;
        this.mRestoredAdapterState = null;
        this.mRestoredClassLoader = null;
        this.mFirstOffset = -3.4028235E38f;
        this.mLastOffset = Float.MAX_VALUE;
        this.mOffscreenPageLimit = 1;
        this.mActivePointerId = -1;
        this.mFirstLayout = true;
        this.mNeedCalculatePageOffsets = false;
        this.mEndScrollRunnable = new Runnable() {
            @Override
            public void run() {
                ForbidScrollViewPager.this.setScrollState(0);
                ForbidScrollViewPager.this.populate();
            }
        };
        this.mScrollState = 0;
        this.isClassMode = false;
        this.isForbidScroll = false;
        initViewPager();
    }

    public ForbidScrollViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mItems = new ArrayList<>();
        this.mTempItem = new ItemInfo();
        this.mTempRect = new Rect();
        this.mRestoredCurItem = -1;
        this.mRestoredAdapterState = null;
        this.mRestoredClassLoader = null;
        this.mFirstOffset = -3.4028235E38f;
        this.mLastOffset = Float.MAX_VALUE;
        this.mOffscreenPageLimit = 1;
        this.mActivePointerId = -1;
        this.mFirstLayout = true;
        this.mNeedCalculatePageOffsets = false;
        this.mEndScrollRunnable = new Runnable() {
            @Override
            public void run() {
                ForbidScrollViewPager.this.setScrollState(0);
                ForbidScrollViewPager.this.populate();
            }
        };
        this.mScrollState = 0;
        this.isClassMode = false;
        this.isForbidScroll = false;
        initViewPager();
    }

    void initViewPager() {
        setWillNotDraw(false);
        setDescendantFocusability(262144);
        setFocusable(true);
        Context context = getContext();
        this.mScroller = new Scroller(context, new DecelerateInterpolator());
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        float density = context.getResources().getDisplayMetrics().density;
        this.mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(viewConfiguration);
        this.mMinimumVelocity = (int) (400.0f * density);
        this.mMaximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        this.mLeftEdge = new EdgeEffectCompat(context);
        this.mRightEdge = new EdgeEffectCompat(context);
        this.mFlingDistance = (int) (25.0f * density);
        this.mCloseEnough = (int) (2.0f * density);
        this.mDefaultGutterSize = (int) (density * 16.0f);
        ViewCompat.setAccessibilityDelegate(this, new MyAccessibilityDelegate());
        if (ViewCompat.getImportantForAccessibility(this) == 0) {
            ViewCompat.setImportantForAccessibility(this, 1);
        }
        ViewCompat.setOnApplyWindowInsetsListener(this, new android.support.v4.view.OnApplyWindowInsetsListener() {
            private final Rect mTempRect = new Rect();

            @Override
            public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
                WindowInsetsCompat appliedInsets = ViewCompat.onApplyWindowInsets(view, windowInsetsCompat);
                if (appliedInsets.isConsumed()) {
                    return appliedInsets;
                }
                Rect rect = this.mTempRect;
                rect.left = appliedInsets.getSystemWindowInsetLeft();
                rect.top = appliedInsets.getSystemWindowInsetTop();
                rect.right = appliedInsets.getSystemWindowInsetRight();
                rect.bottom = appliedInsets.getSystemWindowInsetBottom();
                int childCount = ForbidScrollViewPager.this.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    WindowInsetsCompat childInsets = ViewCompat.dispatchApplyWindowInsets(ForbidScrollViewPager.this.getChildAt(i), appliedInsets);
                    rect.left = Math.min(childInsets.getSystemWindowInsetLeft(), rect.left);
                    rect.top = Math.min(childInsets.getSystemWindowInsetTop(), rect.top);
                    rect.right = Math.min(childInsets.getSystemWindowInsetRight(), rect.right);
                    rect.bottom = Math.min(childInsets.getSystemWindowInsetBottom(), rect.bottom);
                }
                return appliedInsets.replaceSystemWindowInsets(rect.left, rect.top, rect.right, rect.bottom);
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        removeCallbacks(this.mEndScrollRunnable);
        Scroller scroller = this.mScroller;
        if (scroller != null && !scroller.isFinished()) {
            this.mScroller.abortAnimation();
        }
        super.onDetachedFromWindow();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setScrollState(int newState) {
        if (this.mScrollState == newState) {
            return;
        }
        this.mScrollState = newState;
        if (this.mPageTransformer != null) {
            enableLayers(newState != 0);
        }
        dispatchOnScrollStateChanged(newState);
    }
    public void setAdapter(PagerAdapter pagerAdapter) {
        PagerAdapter oldAdapter = this.mAdapter;
        if (oldAdapter != null) {
            oldAdapter.setViewPagerObserver(null);
            this.mAdapter.startUpdate((ViewGroup) this);
            for (int i = 0; i < this.mItems.size(); i++) {
                ItemInfo itemInfo = this.mItems.get(i);
                this.mAdapter.destroyItem((ViewGroup) this, itemInfo.position, itemInfo.object);
            }
            this.mAdapter.finishUpdate((ViewGroup) this);
            this.mItems.clear();
            removeNonDecorViews();
            this.mCurItem = 0;
            scrollTo(0, 0);
        }
        PagerAdapter previousAdapter = this.mAdapter;
        this.mAdapter = pagerAdapter;
        this.mExpectedAdapterCount = 0;
        if (this.mAdapter != null) {
            if (this.mObserver == null) {
                this.mObserver = new PagerObserver();
            }
            this.mAdapter.setViewPagerObserver(this.mObserver);
            this.mPopulatePending = false;
            boolean wasFirstLayout = this.mFirstLayout;
            this.mFirstLayout = true;
            this.mExpectedAdapterCount = this.mAdapter.getCount();
            if (this.mRestoredCurItem >= 0) {
                this.mAdapter.restoreState(this.mRestoredAdapterState, this.mRestoredClassLoader);
                setCurrentItemInternal(this.mRestoredCurItem, false, true);
                this.mRestoredCurItem = -1;
                this.mRestoredAdapterState = null;
                this.mRestoredClassLoader = null;
            } else if (!wasFirstLayout) {
                populate();
            } else {
                requestLayout();
            }
        }
        OnAdapterChangeListener listener = this.mAdapterChangeListener;
        if (listener == null || previousAdapter == pagerAdapter) {
            return;
        }
        listener.onAdapterChanged(previousAdapter, pagerAdapter);
    }

    private void removeNonDecorViews() {
        int i = 0;
        while (i < getChildCount()) {
            if (!((LayoutParams) getChildAt(i).getLayoutParams()).isDecor) {
                removeViewAt(i);
                i--;
            }
            i++;
        }
    }

    public PagerAdapter getAdapter() {
        return this.mAdapter;
    }

    void setOnAdapterChangeListener(OnAdapterChangeListener onAdapterChangeListener) {
        this.mAdapterChangeListener = onAdapterChangeListener;
    }

    private int getClientWidth() {
        return (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
    }

    public void setCurrentItem(int item) {
        this.mPopulatePending = false;
        setCurrentItemInternal(item, !this.mFirstLayout, false);
    }

    public void setCurrentItem(int item, boolean smoothScroll) {
        this.mPopulatePending = false;
        setCurrentItemInternal(item, smoothScroll, false);
    }

    public int getCurrentItem() {
        return this.mCurItem;
    }

    void setCurrentItemInternal(int item, boolean smoothScroll, boolean always) {
        setCurrentItemInternal(item, smoothScroll, always, 0);
    }

    void setCurrentItemInternal(int item, boolean smoothScroll, boolean always, int velocity) {
        PagerAdapter pagerAdapter = this.mAdapter;
        if (pagerAdapter == null || pagerAdapter.getCount() <= 0) {
            setScrollingCacheEnabled(false);
            return;
        }
        if (!always && this.mCurItem == item && this.mItems.size() != 0) {
            setScrollingCacheEnabled(false);
            return;
        }
        int newCurrentItem;
        if (item < 0) {
            newCurrentItem = 0;
        } else {
            if (item >= this.mAdapter.getCount()) {
                item = this.mAdapter.getCount() - 1;
            }
            newCurrentItem = item;
        }
        int offscreenPageLimit = this.mOffscreenPageLimit;
        int previousCurrentItem = this.mCurItem;
        if (newCurrentItem > previousCurrentItem + offscreenPageLimit || newCurrentItem < previousCurrentItem - offscreenPageLimit) {
            for (int i = 0; i < this.mItems.size(); i++) {
                this.mItems.get(i).scrolling = true;
            }
        }
        boolean changed = this.mCurItem != newCurrentItem;
        if (this.mFirstLayout) {
            int oldCurrentItem = this.mCurItem;
            this.mCurItem = newCurrentItem;
            if (changed) {
                dispatchOnPageSelected(oldCurrentItem, newCurrentItem, velocity, 0L);
            }
            requestLayout();
            return;
        }
        int oldCurrentItem2 = this.mCurItem;
        populate(newCurrentItem);
        scrollToItem(oldCurrentItem2, newCurrentItem, smoothScroll, velocity, changed);
    }

    private void scrollToItem(int oldPosition, int newPosition, boolean smoothScroll, int velocity, boolean dispatchSelected) {
        ItemInfo itemInfo = infoForPosition(newPosition);
        int destination = itemInfo != null ? (int) (getClientWidth() * Math.max(this.mFirstOffset, Math.min(itemInfo.offset, this.mLastOffset))) : 0;
        if (smoothScroll) {
            long scrollDuration = smoothScrollTo(destination, 0, velocity);
            if (dispatchSelected) {
                dispatchOnPageSelected(oldPosition, newPosition, velocity, scrollDuration);
                return;
            }
            return;
        }
        if (dispatchSelected) {
            dispatchOnPageSelected(oldPosition, newPosition, velocity, 0L);
        }
        completeScroll(false);
        scrollTo(destination, 0);
        pageScrolled(destination);
    }

    @Deprecated
    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.mOnPageChangeListener = onPageChangeListener;
    }

    public void addOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        if (this.mOnPageChangeListeners == null) {
            this.mOnPageChangeListeners = new ArrayList();
        }
        this.mOnPageChangeListeners.add(onPageChangeListener);
    }

    public void removeOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        List<OnPageChangeListener> listeners = this.mOnPageChangeListeners;
        if (listeners != null) {
            listeners.remove(onPageChangeListener);
        }
    }

    public void clearOnPageChangeListeners() {
        List<OnPageChangeListener> listeners = this.mOnPageChangeListeners;
        if (listeners != null) {
            listeners.clear();
        }
    }

    public void setPageTransformer(boolean reverseDrawingOrder, PageTransformer pageTransformer) {
        if (Build.VERSION.SDK_INT >= 11) {
            boolean hasTransformer = pageTransformer != null;
            boolean needsPopulate = hasTransformer != (this.mPageTransformer != null);
            this.mPageTransformer = pageTransformer;
            setChildrenDrawingOrderEnabledCompat(hasTransformer);
            if (hasTransformer) {
                this.mDrawingOrder = reverseDrawingOrder ? 2 : 1;
            } else {
                this.mDrawingOrder = 0;
            }
            if (needsPopulate) {
                populate();
            }
        }
    }

    void setChildrenDrawingOrderEnabledCompat(boolean enable) {
        if (Build.VERSION.SDK_INT >= 7) {
            if (this.mSetChildrenDrawingOrderEnabled == null) {
                try {
                    this.mSetChildrenDrawingOrderEnabled = ViewGroup.class.getDeclaredMethod("setChildrenDrawingOrderEnabled", Boolean.TYPE);
                } catch (NoSuchMethodException unused) {
                }
            }
            try {
                this.mSetChildrenDrawingOrderEnabled.invoke(this, Boolean.valueOf(enable));
            } catch (Exception unused2) {
            }
        }
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int index) {
        if (this.mDrawingOrder == 2) {
            index = (childCount - 1) - index;
        }
        return ((LayoutParams) this.mDrawingOrderedChildren.get(index).getLayoutParams()).childIndex;
    }

    OnPageChangeListener setInternalPageChangeListener(OnPageChangeListener onPageChangeListener) {
        OnPageChangeListener previousListener = this.mInternalPageChangeListener;
        this.mInternalPageChangeListener = onPageChangeListener;
        return previousListener;
    }

    public int getOffscreenPageLimit() {
        return this.mOffscreenPageLimit;
    }

    public void setOffscreenPageLimit(int limit) {
        if (limit < 1) {
            limit = 1;
        }
        if (limit != this.mOffscreenPageLimit) {
            this.mOffscreenPageLimit = limit;
            populate();
        }
    }

    public void setPageMargin(int marginPixels) {
        int oldMargin = this.mPageMargin;
        this.mPageMargin = marginPixels;
        int width = getWidth();
        recomputeScrollPosition(width, width, marginPixels, oldMargin);
        requestLayout();
    }

    public int getPageMargin() {
        return this.mPageMargin;
    }

    public void setPageMarginDrawable(Drawable drawable) {
        this.mMarginDrawable = drawable;
        if (drawable != null) {
            refreshDrawableState();
        }
        setWillNotDraw(drawable == null);
        invalidate();
    }

    public void setPageMarginDrawable(int resId) {
        setPageMarginDrawable(getContext().getResources().getDrawable(resId));
    }

    @Override
    protected boolean verifyDrawable(Drawable drawable) {
        return super.verifyDrawable(drawable) || drawable == this.mMarginDrawable;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable drawable = this.mMarginDrawable;
        if (drawable == null || !drawable.isStateful()) {
            return;
        }
        drawable.setState(getDrawableState());
    }

    float distanceInfluenceForSnapDuration(float fraction) {
        double x = fraction - 0.5f;
        Double.isNaN(x);
        return (float) Math.sin((float) (x * 0.4712389167638204d));
    }

    void smoothScrollTo(int x, int y) {
        smoothScrollTo(x, y, 0);
    }

    long smoothScrollTo(int x, int y, int velocity) {
        int startX;
        int duration;
        if (getChildCount() == 0) {
            setScrollingCacheEnabled(false);
            return 0L;
        }
        Scroller scroller = this.mScroller;
        if ((scroller == null || scroller.isFinished()) ? false : true) {
            startX = this.mIsScrollStarted ? this.mScroller.getCurrX() : this.mScroller.getStartX();
            this.mScroller.abortAnimation();
            setScrollingCacheEnabled(false);
        } else {
            startX = getScrollX();
        }
        int startY = getScrollY();
        int dx = x - startX;
        int dy = y - startY;
        if (dx == 0 && dy == 0) {
            completeScroll(false);
            populate();
            setScrollState(0);
            return 0L;
        }
        setScrollingCacheEnabled(true);
        setScrollState(2);
        int clientWidth = getClientWidth();
        int halfWidth = clientWidth / 2;
        float width = clientWidth;
        float half = halfWidth;
        float distance = half + (distanceInfluenceForSnapDuration(Math.min(1.0f, (Math.abs(dx) * 1.0f) / width)) * half);
        int absVelocity = Math.abs(velocity);
        if (absVelocity > 0) {
            duration = Math.round(Math.abs(distance / absVelocity) * 1000.0f);
        } else {
            duration = (int) (((Math.abs(dx) / ((width * this.mAdapter.getPageWidth(this.mCurItem)) + this.mPageMargin)) + 1.0f) * 100.0f);
        }
        int duration2 = Math.max(Math.min(duration, 250), 140);
        this.mIsScrollStarted = false;
        this.mScroller.startScroll(startX, startY, dx, dy, duration2);
        ViewCompat.postInvalidateOnAnimation(this);
        return duration2;
    }

    ItemInfo addNewItem(int position, int index) {
        ItemInfo itemInfo = new ItemInfo();
        itemInfo.position = position;
        itemInfo.object = this.mAdapter.instantiateItem((ViewGroup) this, position);
        itemInfo.widthFactor = this.mAdapter.getPageWidth(position);
        if (index < 0 || index >= this.mItems.size()) {
            this.mItems.add(itemInfo);
        } else {
            this.mItems.add(index, itemInfo);
        }
        return itemInfo;
    }

    void dataSetChanged() {
        int adapterCount = this.mAdapter.getCount();
        this.mExpectedAdapterCount = adapterCount;
        boolean needPopulate = this.mItems.size() < (this.mOffscreenPageLimit * 2) + 1 && this.mItems.size() < adapterCount;
        int newCurrItem = this.mCurItem;
        int i = 0;
        boolean isUpdating = false;
        while (i < this.mItems.size()) {
            ItemInfo itemInfo = this.mItems.get(i);
            int newPos = this.mAdapter.getItemPosition(itemInfo.object);
            if (newPos != -1) {
                if (newPos == -2) {
                    this.mItems.remove(i);
                    i--;
                    if (!isUpdating) {
                        this.mAdapter.startUpdate((ViewGroup) this);
                        isUpdating = true;
                    }
                    this.mAdapter.destroyItem((ViewGroup) this, itemInfo.position, itemInfo.object);
                    if (this.mCurItem == itemInfo.position) {
                        newCurrItem = Math.max(0, Math.min(this.mCurItem, adapterCount - 1));
                    }
                } else if (itemInfo.position != newPos) {
                    if (itemInfo.position == this.mCurItem) {
                        newCurrItem = newPos;
                    }
                    itemInfo.position = newPos;
                }
                needPopulate = true;
            }
            i++;
        }
        if (isUpdating) {
            this.mAdapter.finishUpdate((ViewGroup) this);
        }
        Collections.sort(this.mItems, COMPARATOR);
        if (needPopulate) {
            int childCount = getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                LayoutParams layoutParams = (LayoutParams) getChildAt(i2).getLayoutParams();
                if (!layoutParams.isDecor) {
                    layoutParams.widthFactor = 0.0f;
                }
            }
            setCurrentItemInternal(newCurrItem, false, true);
            requestLayout();
        }
    }
    void populate() {
        populate(this.mCurItem);
    }

    void populate(int newCurrentItem) {
        ItemInfo oldItem;
        String resourceName;
        ItemInfo currentItem;
        ItemInfo itemInfo;
        int previousCurrItem = this.mCurItem;
        if (previousCurrItem != newCurrentItem) {
            oldItem = infoForPosition(previousCurrItem);
            this.mCurItem = newCurrentItem;
        } else {
            oldItem = null;
        }
        if (this.mAdapter == null) {
            sortChildDrawingOrder();
            return;
        }
        if (this.mPopulatePending) {
            sortChildDrawingOrder();
            return;
        }
        if (getWindowToken() == null) {
            return;
        }
        this.mAdapter.startUpdate((ViewGroup) this);
        int offscreenPageLimit = this.mOffscreenPageLimit;
        int minPos = Math.max(0, this.mCurItem - offscreenPageLimit);
        int adapterCount = this.mAdapter.getCount();
        int maxPos = Math.min(adapterCount - 1, this.mCurItem + offscreenPageLimit);
        if (adapterCount != this.mExpectedAdapterCount) {
            try {
                resourceName = getResources().getResourceName(getId());
            } catch (Resources.NotFoundException unused) {
                resourceName = Integer.toHexString(getId());
            }
            throw new IllegalStateException("The application's PagerAdapter changed the adapter's contents without calling PagerAdapter#notifyDataSetChanged! Expected adapter item count: " + this.mExpectedAdapterCount + ", found: " + adapterCount + " Pager id: " + resourceName + " Pager class: " + getClass() + " Problematic adapter: " + this.mAdapter.getClass());
        }
        int curIndex = 0;
        while (true) {
            if (curIndex < this.mItems.size()) {
                currentItem = this.mItems.get(curIndex);
                if (currentItem.position >= this.mCurItem) {
                    break;
                }
                curIndex++;
            } else {
                currentItem = null;
                break;
            }
        }
        if (currentItem == null && adapterCount > 0) {
            currentItem = addNewItem(this.mCurItem, curIndex);
        }
        if (currentItem != null) {
            int previousIndex = curIndex - 1;
            ItemInfo previousItem = previousIndex >= 0 ? this.mItems.get(previousIndex) : null;
            int clientWidth = getClientWidth();
            float extraWidthLeft = clientWidth <= 0 ? 0.0f : (2.0f - currentItem.widthFactor) + (getPaddingLeft() / clientWidth);
            int itemIndex = previousIndex;
            int currentIndex = curIndex;
            float leftWidthNeeded = 0.0f;
            for (int pos = this.mCurItem - 1; pos >= 0; pos--) {
                if (leftWidthNeeded >= extraWidthLeft && pos < minPos) {
                    if (previousItem == null) {
                        break;
                    }
                    if (pos == previousItem.position && !previousItem.scrolling) {
                        this.mItems.remove(itemIndex);
                        this.mAdapter.destroyItem((ViewGroup) this, pos, previousItem.object);
                        itemIndex--;
                        currentIndex--;
                        previousItem = itemIndex >= 0 ? this.mItems.get(itemIndex) : null;
                    }
                } else {
                    if (previousItem != null && pos == previousItem.position) {
                        leftWidthNeeded += previousItem.widthFactor;
                        itemIndex--;
                        previousItem = itemIndex >= 0 ? this.mItems.get(itemIndex) : null;
                    } else {
                        leftWidthNeeded += addNewItem(pos, itemIndex + 1).widthFactor;
                        currentIndex++;
                        previousItem = itemIndex >= 0 ? this.mItems.get(itemIndex) : null;
                    }
                }
            }
            float rightWidthNeeded = currentItem.widthFactor;
            int nextIndex = currentIndex + 1;
            if (rightWidthNeeded < 2.0f) {
                ItemInfo nextItem = nextIndex < this.mItems.size() ? this.mItems.get(nextIndex) : null;
                float extraWidthRight = clientWidth <= 0 ? 0.0f : (getPaddingRight() / clientWidth) + 2.0f;
                for (int pos = this.mCurItem + 1; pos < adapterCount; pos++) {
                    if (rightWidthNeeded >= extraWidthRight && pos > maxPos) {
                        if (nextItem == null) {
                            break;
                        }
                        if (pos == nextItem.position && !nextItem.scrolling) {
                            this.mItems.remove(nextIndex);
                            this.mAdapter.destroyItem((ViewGroup) this, pos, nextItem.object);
                            if (nextIndex < this.mItems.size()) {
                                nextItem = this.mItems.get(nextIndex);
                            }
                        }
                    } else if (nextItem != null && pos == nextItem.position) {
                        rightWidthNeeded += nextItem.widthFactor;
                        nextIndex++;
                        if (nextIndex < this.mItems.size()) {
                            nextItem = this.mItems.get(nextIndex);
                        }
                    } else {
                        ItemInfo addedItem = addNewItem(pos, nextIndex);
                        nextIndex++;
                        rightWidthNeeded += addedItem.widthFactor;
                        nextItem = nextIndex < this.mItems.size() ? this.mItems.get(nextIndex) : null;
                    }
                }
            }
            calculatePageOffsets(currentItem, currentIndex, oldItem);
        }
        this.mAdapter.setPrimaryItem((ViewGroup) this, this.mCurItem, currentItem != null ? currentItem.object : null);
        this.mAdapter.finishUpdate((ViewGroup) this);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            layoutParams.childIndex = i;
            if (!layoutParams.isDecor && layoutParams.widthFactor == 0.0f) {
                ItemInfo info = infoForChild(child);
                if (info != null) {
                    layoutParams.widthFactor = info.widthFactor;
                    layoutParams.position = info.position;
                }
            }
        }
        sortChildDrawingOrder();
        if (hasFocus()) {
            View focused = findFocus();
            ItemInfo focusedInfo = focused != null ? infoForAnyChild(focused) : null;
            if (focusedInfo == null || focusedInfo.position != this.mCurItem) {
                for (int i2 = 0; i2 < getChildCount(); i2++) {
                    View child = getChildAt(i2);
                    ItemInfo info = infoForChild(child);
                    if (info != null && info.position == this.mCurItem && child.requestFocus(2)) {
                        return;
                    }
                }
            }
        }
    }

    private void sortChildDrawingOrder() {
        if (this.mDrawingOrder != 0) {
            ArrayList<View> children = this.mDrawingOrderedChildren;
            if (children == null) {
                this.mDrawingOrderedChildren = new ArrayList<>();
            } else {
                children.clear();
            }
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                this.mDrawingOrderedChildren.add(getChildAt(i));
            }
            Collections.sort(this.mDrawingOrderedChildren, sPositionComparator);
        }
    }

    private void calculatePageOffsets(ItemInfo currentItem, int currentIndex, ItemInfo oldItem) {
        ItemInfo itemInfo3;
        ItemInfo itemInfo4;
        int adapterCount = this.mAdapter.getCount();
        int clientWidth = getClientWidth();
        float marginOffset = clientWidth > 0 ? this.mPageMargin / clientWidth : 0.0f;
        if (oldItem != null) {
            int oldPosition = oldItem.position;
            if (oldPosition < currentItem.position) {
                float offset = oldItem.offset + oldItem.widthFactor + marginOffset;
                int pos = oldPosition + 1;
                int index = 0;
                while (pos <= currentItem.position && index < this.mItems.size()) {
                    ItemInfo item = this.mItems.get(index);
                    while (true) {
                        itemInfo4 = item;
                        if (pos <= itemInfo4.position || index >= this.mItems.size() - 1) {
                            break;
                        }
                        index++;
                        item = this.mItems.get(index);
                    }
                    while (pos < itemInfo4.position) {
                        offset += this.mAdapter.getPageWidth(pos) + marginOffset;
                        pos++;
                    }
                    itemInfo4.offset = offset;
                    offset += itemInfo4.widthFactor + marginOffset;
                    pos++;
                }
            } else if (oldPosition > currentItem.position) {
                int index = this.mItems.size() - 1;
                float offset = oldItem.offset;
                while (true) {
                    oldPosition--;
                    if (oldPosition < currentItem.position || index < 0) {
                        break;
                    }
                    ItemInfo item = this.mItems.get(index);
                    while (true) {
                        itemInfo3 = item;
                        if (oldPosition >= itemInfo3.position || index <= 0) {
                            break;
                        }
                        index--;
                        item = this.mItems.get(index);
                    }
                    while (oldPosition > itemInfo3.position) {
                        offset -= this.mAdapter.getPageWidth(oldPosition) + marginOffset;
                        oldPosition--;
                    }
                    offset -= itemInfo3.widthFactor + marginOffset;
                    itemInfo3.offset = offset;
                }
            }
        }
        int itemCount = this.mItems.size();
        float offset = currentItem.offset;
        int pos = currentItem.position - 1;
        this.mFirstOffset = currentItem.position == 0 ? currentItem.offset : -3.4028235E38f;
        int lastPosition = adapterCount - 1;
        this.mLastOffset = currentItem.position == lastPosition ? (currentItem.offset + currentItem.widthFactor) - 1.0f : Float.MAX_VALUE;
        int index = currentIndex - 1;
        while (index >= 0) {
            ItemInfo item = this.mItems.get(index);
            while (pos > item.position) {
                offset -= this.mAdapter.getPageWidth(pos) + marginOffset;
                pos--;
            }
            offset -= item.widthFactor + marginOffset;
            item.offset = offset;
            if (item.position == 0) {
                this.mFirstOffset = offset;
            }
            index--;
            pos--;
        }
        float offset2 = currentItem.offset + currentItem.widthFactor + marginOffset;
        int pos2 = currentItem.position + 1;
        int index2 = currentIndex + 1;
        while (index2 < itemCount) {
            ItemInfo item = this.mItems.get(index2);
            while (pos2 < item.position) {
                offset2 += this.mAdapter.getPageWidth(pos2) + marginOffset;
                pos2++;
            }
            if (item.position == lastPosition) {
                this.mLastOffset = (item.widthFactor + offset2) - 1.0f;
            }
            item.offset = offset2;
            offset2 += item.widthFactor + marginOffset;
            index2++;
            pos2++;
        }
        this.mNeedCalculatePageOffsets = false;
    }

    public static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel parcel, ClassLoader loader) {
                return new SavedState(parcel, loader);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        });
        Parcelable adapterState;
        ClassLoader loader;
        int position;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.position);
            out.writeParcelable(this.adapterState, flags);
        }

        public String toString() {
            return "FragmentPager.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " position=" + this.position + "}";
        }

        SavedState(Parcel in, ClassLoader loader) {
            super(in);
            loader = loader == null ? getClass().getClassLoader() : loader;
            this.position = in.readInt();
            this.adapterState = in.readParcelable(loader);
            this.loader = loader;
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.position = this.mCurItem;
        PagerAdapter pagerAdapter = this.mAdapter;
        if (pagerAdapter != null) {
            savedState.adapterState = pagerAdapter.saveState();
        }
        return savedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        PagerAdapter pagerAdapter = this.mAdapter;
        if (pagerAdapter != null) {
            pagerAdapter.restoreState(savedState.adapterState, savedState.loader);
            setCurrentItemInternal(savedState.position, false, true);
        } else {
            this.mRestoredCurItem = savedState.position;
            this.mRestoredAdapterState = savedState.adapterState;
            this.mRestoredClassLoader = savedState.loader;
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (!checkLayoutParams(params)) {
            params = generateLayoutParams(params);
        }
        LayoutParams layoutParams = (LayoutParams) params;
        layoutParams.isDecor |= child instanceof Decor;
        if (this.mInLayout) {
            if (layoutParams != null && layoutParams.isDecor) {
                throw new IllegalStateException("Cannot add pager decor view during layout");
            }
            layoutParams.needsMeasure = true;
            addViewInLayout(child, index, layoutParams);
            return;
        }
        super.addView(child, index, layoutParams);
    }

    @Override
    public void removeView(View view) {
        if (this.mInLayout) {
            removeViewInLayout(view);
        } else {
            super.removeView(view);
        }
    }

    ItemInfo infoForChild(View child) {
        for (int i = 0; i < this.mItems.size(); i++) {
            ItemInfo itemInfo = this.mItems.get(i);
            if (this.mAdapter.isViewFromObject(child, itemInfo.object)) {
                return itemInfo;
            }
        }
        return null;
    }

    ItemInfo infoForAnyChild(View child) {
        while (true) {
            Object parent = child.getParent();
            if (parent != this) {
                if (parent == null || !(parent instanceof View)) {
                    return null;
                }
                child = (View) parent;
            } else {
                return infoForChild(child);
            }
        }
    }

    ItemInfo infoForPosition(int position) {
        for (int i = 0; i < this.mItems.size(); i++) {
            ItemInfo itemInfo = this.mItems.get(i);
            if (itemInfo.position == position) {
                return itemInfo;
            }
        }
        return null;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mFirstLayout = true;
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        LayoutParams layoutParams;
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        int measuredWidth = getMeasuredWidth();
        this.mGutterSize = Math.min(measuredWidth / 10, this.mDefaultGutterSize);
        int childWidthSize = (measuredWidth - getPaddingLeft()) - getPaddingRight();
        int childHeightSize = (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                LayoutParams childParams = (LayoutParams) child.getLayoutParams();
                if (childParams != null && childParams.isDecor) {
                    int horizontalGravity = childParams.gravity & 7;
                    int verticalGravity = childParams.gravity & 112;
                    int widthMode = Integer.MIN_VALUE;
                    int heightMode = Integer.MIN_VALUE;
                    boolean consumeVertical = verticalGravity == 48 || verticalGravity == 80;
                    boolean consumeHorizontal = horizontalGravity == 3 || horizontalGravity == 5;
                    if (consumeVertical) {
                        widthMode = 1073741824;
                    } else if (consumeHorizontal) {
                        heightMode = 1073741824;
                    }
                    int widthSize = childWidthSize;
                    int heightSize = childHeightSize;
                    if (childParams.width != -2) {
                        widthMode = 1073741824;
                        if (childParams.width != -1) {
                            widthSize = childParams.width;
                        }
                    }
                    if (childParams.height != -2) {
                        heightMode = 1073741824;
                        if (childParams.height != -1) {
                            heightSize = childParams.height;
                        }
                    }
                    child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, widthMode), View.MeasureSpec.makeMeasureSpec(heightSize, heightMode));
                    if (consumeVertical) {
                        childHeightSize -= child.getMeasuredHeight();
                    } else if (consumeHorizontal) {
                        childWidthSize -= child.getMeasuredWidth();
                    }
                }
            }
        }
        this.mChildWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(childWidthSize, 1073741824);
        this.mChildHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(childHeightSize, 1073741824);
        this.mInLayout = true;
        populate();
        this.mInLayout = false;
        int childCount2 = getChildCount();
        for (int i2 = 0; i2 < childCount2; i2++) {
            View child = getChildAt(i2);
            if (child.getVisibility() != 8 && ((layoutParams = (LayoutParams) child.getLayoutParams()) == null || !layoutParams.isDecor)) {
                child.measure(View.MeasureSpec.makeMeasureSpec((int) (childWidthSize * layoutParams.widthFactor), 1073741824), this.mChildHeightMeasureSpec);
            }
        }
    }
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        if (width != oldWidth) {
            int pageMargin = this.mPageMargin;
            recomputeScrollPosition(width, oldWidth, pageMargin, pageMargin);
        }
    }

    private void recomputeScrollPosition(int width, int oldWidth, int margin, int oldMargin) {
        if (oldWidth > 0 && !this.mItems.isEmpty()) {
            if (!this.mScroller.isFinished()) {
                this.mScroller.setFinalX(getCurrentItem() * getClientWidth());
                return;
            }
            scrollTo((int) ((getScrollX() / (((oldWidth - getPaddingLeft()) - getPaddingRight()) + oldMargin)) * (((width - getPaddingLeft()) - getPaddingRight()) + margin)), getScrollY());
            return;
        }
        ItemInfo itemInfo = infoForPosition(this.mCurItem);
        int newScrollX = (int) ((itemInfo != null ? Math.min(itemInfo.offset, this.mLastOffset) : 0.0f) * ((width - getPaddingLeft()) - getPaddingRight()));
        if (newScrollX != getScrollX()) {
            completeScroll(false);
            scrollTo(newScrollX, getScrollY());
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        ItemInfo itemInfo;
        int childLeft;
        int childTop;
        int childCount = getChildCount();
        int width = right - left;
        int height = bottom - top;
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        int scrollX = getScrollX();
        int remainingBottom = paddingBottom;
        int decorCount = 0;
        int remainingTop = paddingTop;
        int remainingLeft = paddingLeft;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
                if (layoutParams.isDecor) {
                    int horizontalGravity = layoutParams.gravity & 7;
                    int verticalGravity = layoutParams.gravity & 112;
                    if (horizontalGravity == 1) {
                        childLeft = Math.max((width - child.getMeasuredWidth()) / 2, remainingLeft);
                    } else if (horizontalGravity == 3) {
                        childLeft = remainingLeft;
                        remainingLeft = child.getMeasuredWidth() + remainingLeft;
                    } else if (horizontalGravity != 5) {
                        childLeft = remainingLeft;
                    } else {
                        childLeft = (width - paddingRight) - child.getMeasuredWidth();
                        paddingRight += child.getMeasuredWidth();
                    }
                    if (verticalGravity == 16) {
                        childTop = Math.max((height - child.getMeasuredHeight()) / 2, remainingTop);
                    } else if (verticalGravity == 48) {
                        childTop = remainingTop;
                        remainingTop = child.getMeasuredHeight() + remainingTop;
                    } else if (verticalGravity != 80) {
                        childTop = remainingTop;
                    } else {
                        childTop = (height - remainingBottom) - child.getMeasuredHeight();
                        remainingBottom += child.getMeasuredHeight();
                    }
                    int childLeft2 = childLeft + scrollX;
                    child.layout(childLeft2, childTop, child.getMeasuredWidth() + childLeft2, childTop + child.getMeasuredHeight());
                    decorCount++;
                }
            }
        }
        int contentWidth = (width - remainingLeft) - paddingRight;
        for (int i2 = 0; i2 < childCount; i2++) {
            View child = getChildAt(i2);
            if (child.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
                if (!layoutParams.isDecor && (itemInfo = infoForChild(child)) != null) {
                    float widthFactor = contentWidth;
                    int childLeft3 = ((int) (itemInfo.offset * widthFactor)) + remainingLeft;
                    if (layoutParams.needsMeasure) {
                        layoutParams.needsMeasure = false;
                        child.measure(View.MeasureSpec.makeMeasureSpec((int) (widthFactor * layoutParams.widthFactor), 1073741824), View.MeasureSpec.makeMeasureSpec((height - remainingTop) - remainingBottom, 1073741824));
                    }
                    child.layout(childLeft3, remainingTop, child.getMeasuredWidth() + childLeft3, child.getMeasuredHeight() + remainingTop);
                }
            }
        }
        this.mTopPageBounds = remainingTop;
        this.mBottomPageBounds = height - remainingBottom;
        this.mDecorChildCount = decorCount;
        if (this.mFirstLayout) {
            int currentItem = this.mCurItem;
            scrollToItem(currentItem, currentItem, false, 0, false);
        }
        this.mFirstLayout = false;
    }

    @Override
    public void computeScroll() {
        this.mIsScrollStarted = true;
        if (!this.mScroller.isFinished() && this.mScroller.computeScrollOffset()) {
            int scrollX = getScrollX();
            int scrollY = getScrollY();
            int currX = this.mScroller.getCurrX();
            int currY = this.mScroller.getCurrY();
            if (scrollX != currX || scrollY != currY) {
                scrollTo(currX, currY);
                if (!pageScrolled(currX)) {
                    this.mScroller.abortAnimation();
                    scrollTo(0, currY);
                }
            }
            ViewCompat.postInvalidateOnAnimation(this);
            return;
        }
        completeScroll(true);
    }

    private boolean pageScrolled(int xpos) {
        if (this.mItems.size() == 0) {
            if (this.mFirstLayout) {
                return false;
            }
            this.mCalledSuper = false;
            onPageScrolled(0, 0.0f, 0);
            if (this.mCalledSuper) {
                return false;
            }
            throw new IllegalStateException("onPageScrolled did not call superclass implementation");
        }
        ItemInfo itemInfo = infoForCurrentScrollPosition();
        int clientWidth = getClientWidth();
        int pageMargin = this.mPageMargin;
        int widthWithMargin = clientWidth + pageMargin;
        float width = clientWidth;
        int currentPage = itemInfo.position;
        float pageOffset = ((xpos / width) - itemInfo.offset) / (itemInfo.widthFactor + (pageMargin / width));
        this.mCalledSuper = false;
        onPageScrolled(currentPage, pageOffset, (int) (widthWithMargin * pageOffset));
        if (this.mCalledSuper) {
            return true;
        }
        throw new IllegalStateException("onPageScrolled did not call superclass implementation");
    }

    protected void onPageScrolled(int position, float offset, int offsetPixels) {
        int childLeft;
        if (this.mDecorChildCount > 0) {
            int scrollX = getScrollX();
            int paddingLeft = getPaddingLeft();
            int paddingRight = getPaddingRight();
            int width = getWidth();
            int childCount = getChildCount();
            int decorRight = paddingRight;
            int decorLeft = paddingLeft;
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
                if (layoutParams.isDecor) {
                    int horizontalGravity = layoutParams.gravity & 7;
                    if (horizontalGravity == 1) {
                        childLeft = Math.max((width - child.getMeasuredWidth()) / 2, decorLeft);
                    } else if (horizontalGravity == 3) {
                        childLeft = decorLeft;
                        decorLeft = child.getWidth() + decorLeft;
                    } else if (horizontalGravity != 5) {
                        childLeft = decorLeft;
                    } else {
                        childLeft = (width - decorRight) - child.getMeasuredWidth();
                        decorRight += child.getMeasuredWidth();
                    }
                    int offsetLeft = (childLeft + scrollX) - child.getLeft();
                    if (offsetLeft != 0) {
                        child.offsetLeftAndRight(offsetLeft);
                    }
                }
            }
        }
        dispatchOnPageScrolled(position, offset, offsetPixels);
        if (this.mPageTransformer != null) {
            int scrollX2 = getScrollX();
            int childCount2 = getChildCount();
            for (int i2 = 0; i2 < childCount2; i2++) {
                View child = getChildAt(i2);
                if (!((LayoutParams) child.getLayoutParams()).isDecor) {
                    this.mPageTransformer.transformPage(child, (child.getLeft() - scrollX2) / getClientWidth());
                }
            }
        }
        this.mCalledSuper = true;
    }
    private void dispatchOnPageScrolled(int position, float offset, int offsetPixels) {
        OnPageChangeListener listener = this.mOnPageChangeListener;
        if (listener != null) {
            listener.onPageScrolled(position, offset, offsetPixels);
        }
        List<OnPageChangeListener> listeners = this.mOnPageChangeListeners;
        if (listeners != null) {
            int size = listeners.size();
            for (int i = 0; i < size; i++) {
                OnPageChangeListener itemListener = this.mOnPageChangeListeners.get(i);
                if (itemListener != null) {
                    itemListener.onPageScrolled(position, offset, offsetPixels);
                }
            }
        }
        OnPageChangeListener internalListener = this.mInternalPageChangeListener;
        if (internalListener != null) {
            internalListener.onPageScrolled(position, offset, offsetPixels);
        }
    }

    private void dispatchOnPageSelected(int oldPosition, int newPosition, int duration, long scrollDuration) {
        OnPageChangeListener listener = this.mOnPageChangeListener;
        if (listener != null) {
            listener.onPageSelected(oldPosition, newPosition, duration, scrollDuration);
        }
        List<OnPageChangeListener> listeners = this.mOnPageChangeListeners;
        if (listeners != null) {
            int size = listeners.size();
            for (int i = 0; i < size; i++) {
                OnPageChangeListener itemListener = this.mOnPageChangeListeners.get(i);
                if (itemListener != null) {
                    itemListener.onPageSelected(oldPosition, newPosition, duration, scrollDuration);
                }
            }
        }
        OnPageChangeListener internalListener = this.mInternalPageChangeListener;
        if (internalListener != null) {
            internalListener.onPageSelected(oldPosition, newPosition, duration, scrollDuration);
        }
    }

    private void dispatchOnScrollStateChanged(int state) {
        OnPageChangeListener listener = this.mOnPageChangeListener;
        if (listener != null) {
            listener.onPageScrollStateChanged(state);
        }
        List<OnPageChangeListener> listeners = this.mOnPageChangeListeners;
        if (listeners != null) {
            int size = listeners.size();
            for (int i = 0; i < size; i++) {
                OnPageChangeListener itemListener = this.mOnPageChangeListeners.get(i);
                if (itemListener != null) {
                    itemListener.onPageScrollStateChanged(state);
                }
            }
        }
        OnPageChangeListener internalListener = this.mInternalPageChangeListener;
        if (internalListener != null) {
            internalListener.onPageScrollStateChanged(state);
        }
    }

    private void completeScroll(boolean postEvents) {
        boolean needPopulate = this.mScrollState == 2;
        if (needPopulate) {
            setScrollingCacheEnabled(false);
            if (!this.mScroller.isFinished()) {
                this.mScroller.abortAnimation();
                int oldX = getScrollX();
                int oldY = getScrollY();
                int x = this.mScroller.getCurrX();
                int y = this.mScroller.getCurrY();
                if (oldX != x || oldY != y) {
                    scrollTo(x, y);
                    if (x != oldX) {
                        pageScrolled(x);
                    }
                }
            } else {
                int oldX2 = getScrollX();
                int oldY2 = getScrollY();
                int x2 = this.mScroller.getCurrX();
                int y2 = this.mScroller.getCurrY();
                if (oldX2 != x2 || oldY2 != y2) {
                    scrollTo(x2, y2);
                    if (x2 != oldX2) {
                        pageScrolled(x2);
                    }
                }
            }
        }
        this.mPopulatePending = false;
        boolean needPopulate2 = needPopulate;
        for (int i = 0; i < this.mItems.size(); i++) {
            ItemInfo itemInfo = this.mItems.get(i);
            if (itemInfo.scrolling) {
                itemInfo.scrolling = false;
                needPopulate2 = true;
            }
        }
        if (needPopulate2) {
            if (postEvents) {
                ViewCompat.postOnAnimation(this, this.mEndScrollRunnable);
            } else {
                this.mEndScrollRunnable.run();
            }
        }
    }

    private boolean isGutterDrag(float x, float dx) {
        return (x < ((float) this.mGutterSize) && dx > 0.0f) || (x > ((float) (getWidth() - this.mGutterSize)) && dx < 0.0f);
    }

    private void enableLayers(boolean enable) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            ViewCompat.setLayerType(getChildAt(i), enable ? 2 : 0, null);
        }
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.isForbidScroll) {
            return false;
        }
        int action = ev.getAction() & 255;
        if (action == 3 || action == 1) {
            resetTouch();
            return false;
        }
        if (action != 0) {
            if (this.mIsBeingDragged) {
                return true;
            }
            if (this.mIsUnableToDrag) {
                return false;
            }
        }
        if (action == 0) {
            float x = ev.getX();
            this.mInitialMotionX = x;
            this.mLastMotionX = x;
            float y = ev.getY();
            this.mInitialMotionY = y;
            this.mLastMotionY = y;
            this.mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
            this.mIsUnableToDrag = false;
            this.mIsScrollStarted = true;
            this.mScroller.computeScrollOffset();
            if (this.mScrollState == 2 && Math.abs(this.mScroller.getFinalX() - this.mScroller.getCurrX()) > this.mCloseEnough) {
                this.mScroller.abortAnimation();
                this.mPopulatePending = false;
                populate();
                this.mIsBeingDragged = true;
                requestParentDisallowInterceptTouchEvent(true);
                setScrollState(1);
            } else {
                completeScroll(false);
                this.mIsBeingDragged = false;
            }
        } else if (action == 2) {
            int activePointerId = this.mActivePointerId;
            if (activePointerId != -1) {
                int pointerIndex = MotionEventCompat.findPointerIndex(ev, activePointerId);
                float x2 = MotionEventCompat.getX(ev, pointerIndex);
                float dx = x2 - this.mLastMotionX;
                float xDiff = Math.abs(dx);
                float y2 = MotionEventCompat.getY(ev, pointerIndex);
                float yDiff = Math.abs(y2 - this.mInitialMotionY);
                if (dx != 0.0f && !isGutterDrag(this.mLastMotionX, dx) && canScroll(this, false, (int) dx, (int) x2, (int) y2)) {
                    this.mLastMotionX = x2;
                    this.mLastMotionY = y2;
                    this.mIsUnableToDrag = true;
                    return false;
                }
                if (xDiff > this.mTouchSlop && xDiff * 0.5f > yDiff) {
                    this.mIsBeingDragged = true;
                    requestParentDisallowInterceptTouchEvent(true);
                    setScrollState(1);
                    this.mLastMotionX = dx > 0.0f ? this.mInitialMotionX + this.mTouchSlop : this.mInitialMotionX - this.mTouchSlop;
                    this.mLastMotionY = y2;
                    setScrollingCacheEnabled(true);
                } else if (yDiff > this.mTouchSlop) {
                    this.mIsUnableToDrag = true;
                }
                if (this.mIsBeingDragged && performDrag(x2)) {
                    ViewCompat.postInvalidateOnAnimation(this);
                }
            }
        } else if (action == 6) {
            onSecondaryPointerUp(ev);
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(ev);
        return this.mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        PagerAdapter pagerAdapter;
        boolean needsInvalidate = false;
        if (this.isForbidScroll) {
            return false;
        }
        if (this.mFakeDragging) {
            return true;
        }
        if ((ev.getAction() == 0 && ev.getEdgeFlags() != 0) || (pagerAdapter = this.mAdapter) == null || pagerAdapter.getCount() == 0) {
            return false;
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(ev);
        int action = ev.getAction() & 255;
        if (action == 0) {
            this.mScroller.abortAnimation();
            this.mPopulatePending = false;
            populate();
            float x = ev.getX();
            this.mInitialMotionX = x;
            this.mLastMotionX = x;
            float y = ev.getY();
            this.mInitialMotionY = y;
            this.mLastMotionY = y;
            this.mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
        } else if (action != 1) {
            if (action != 2) {
                if (action != 3) {
                    if (action == 5) {
                        int index = MotionEventCompat.getActionIndex(ev);
                        this.mLastMotionX = MotionEventCompat.getX(ev, index);
                        this.mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                    } else if (action == 6) {
                        onSecondaryPointerUp(ev);
                        this.mLastMotionX = MotionEventCompat.getX(ev, MotionEventCompat.findPointerIndex(ev, this.mActivePointerId));
                    }
                } else if (this.mIsBeingDragged) {
                    int currentItem = this.mCurItem;
                    scrollToItem(currentItem, currentItem, true, 0, false);
                    needsInvalidate = resetTouch();
                }
            } else if (!this.mIsBeingDragged) {
                int pointerIndex = MotionEventCompat.findPointerIndex(ev, this.mActivePointerId);
                if (pointerIndex == -1) {
                    needsInvalidate = resetTouch();
                } else {
                    float x2 = MotionEventCompat.getX(ev, pointerIndex);
                    float xDiff = Math.abs(x2 - this.mLastMotionX);
                    float y2 = MotionEventCompat.getY(ev, pointerIndex);
                    float yDiff = Math.abs(y2 - this.mLastMotionY);
                    if (xDiff > this.mTouchSlop && xDiff > yDiff) {
                        this.mIsBeingDragged = true;
                        requestParentDisallowInterceptTouchEvent(true);
                        float initialX = this.mInitialMotionX;
                        this.mLastMotionX = x2 - initialX > 0.0f ? initialX + this.mTouchSlop : initialX - this.mTouchSlop;
                        this.mLastMotionY = y2;
                        setScrollState(1);
                        setScrollingCacheEnabled(true);
                        ViewParent parent = getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(true);
                        }
                    }
                    if (this.mIsBeingDragged) {
                        needsInvalidate = false | performDrag(MotionEventCompat.getX(ev, MotionEventCompat.findPointerIndex(ev, this.mActivePointerId)));
                    }
                }
            } else if (this.mIsBeingDragged) {
                needsInvalidate = false | performDrag(MotionEventCompat.getX(ev, MotionEventCompat.findPointerIndex(ev, this.mActivePointerId)));
            }
        } else if (this.mIsBeingDragged) {
            VelocityTracker velocityTracker = this.mVelocityTracker;
            velocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
            int initialVelocity = (int) VelocityTrackerCompat.getXVelocity(velocityTracker, this.mActivePointerId);
            this.mPopulatePending = true;
            int width = getClientWidth();
            int scrollX = getScrollX();
            ItemInfo itemInfo = infoForCurrentScrollPosition();
            float width2 = width;
            setCurrentItemInternal(determineTargetPage(itemInfo.position, ((scrollX / width2) - itemInfo.offset) / (itemInfo.widthFactor + (this.mPageMargin / width2)), initialVelocity, (int) (MotionEventCompat.getX(ev, MotionEventCompat.findPointerIndex(ev, this.mActivePointerId)) - this.mInitialMotionX)), true, true, initialVelocity);
            needsInvalidate = resetTouch();
        }
        if (needsInvalidate) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
        return true;
    }

    private boolean resetTouch() {
        this.mActivePointerId = -1;
        endDrag();
        return this.mLeftEdge.onRelease() | this.mRightEdge.onRelease();
    }

    private void requestParentDisallowInterceptTouchEvent(boolean disallowIntercept) {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }

    public void setClassMode(boolean classMode) {
        this.isClassMode = classMode;
    }

    private boolean performDrag(float x) {
        boolean needsInvalidate = false;
        float deltaX = this.mLastMotionX - x;
        this.mLastMotionX = x;
        float scrollX = getScrollX() + deltaX;
        float clientWidth = getClientWidth();
        float leftBound = this.mFirstOffset * clientWidth;
        float rightBound = this.mLastOffset * clientWidth;
        boolean leftAbsolute = false;
        boolean rightAbsolute = false;
        ItemInfo firstItem = this.mItems.get(0);
        ArrayList<ItemInfo> items = this.mItems;
        ItemInfo lastItem = items.get(items.size() - 1);
        if (firstItem.position != 0) {
            leftBound = firstItem.offset * clientWidth;
        } else {
            leftAbsolute = true;
        }
        if (lastItem.position != this.mAdapter.getCount() - 1) {
            rightBound = lastItem.offset * clientWidth;
        } else {
            rightAbsolute = true;
        }
        if (this.isClassMode) {
            rightBound = 0.0f;
        }
        if (scrollX < leftBound) {
            if (leftAbsolute) {
                needsInvalidate = this.mLeftEdge.onPull(Math.abs(leftBound - scrollX) / clientWidth);
            }
            scrollX = leftBound;
        } else if (scrollX > rightBound) {
            if (rightAbsolute) {
                needsInvalidate = this.mRightEdge.onPull(Math.abs(scrollX - rightBound) / clientWidth);
            }
            scrollX = rightBound;
        }
        int scrollXInt = (int) scrollX;
        this.mLastMotionX += scrollX - scrollXInt;
        scrollTo(scrollXInt, getScrollY());
        pageScrolled(scrollXInt);
        return needsInvalidate;
    }

    private ItemInfo infoForCurrentScrollPosition() {
        int nextPosition;
        int clientWidth = getClientWidth();
        float scrollX = clientWidth > 0 ? getScrollX() / clientWidth : 0.0f;
        float marginOffset = clientWidth > 0 ? this.mPageMargin / clientWidth : 0.0f;
        ItemInfo lastItem = null;
        int i = 0;
        boolean first = true;
        int lastPos = -1;
        float lastOffset = 0.0f;
        float lastWidth = 0.0f;
        while (i < this.mItems.size()) {
            ItemInfo itemInfo = this.mItems.get(i);
            if (!first && itemInfo.position != (nextPosition = lastPos + 1)) {
                itemInfo = this.mTempItem;
                itemInfo.offset = lastOffset + lastWidth + marginOffset;
                itemInfo.position = nextPosition;
                itemInfo.widthFactor = this.mAdapter.getPageWidth(itemInfo.position);
                i--;
            }
            lastOffset = itemInfo.offset;
            float nextOffset = itemInfo.widthFactor + lastOffset + marginOffset;
            if (!first && scrollX < lastOffset) {
                return lastItem;
            }
            if (scrollX < nextOffset || i == this.mItems.size() - 1) {
                return itemInfo;
            }
            lastPos = itemInfo.position;
            lastWidth = itemInfo.widthFactor;
            i++;
            lastItem = itemInfo;
            first = false;
        }
        return lastItem;
    }

    private int determineTargetPage(int currentPage, float pageOffset, int velocity, int deltaX) {
        int targetPage;
        if (Math.abs(deltaX) <= this.mTouchSlop) {
            targetPage = (int) (currentPage + pageOffset + (currentPage >= this.mCurItem ? 0.4f : 0.6f));
        } else {
            targetPage = currentPage + (deltaX <= 0 ? 1 : 0);
        }
        if (this.mItems.size() <= 0) {
            return targetPage;
        }
        ItemInfo firstItem = this.mItems.get(0);
        ArrayList<ItemInfo> items = this.mItems;
        return Math.max(firstItem.position, Math.min(targetPage, items.get(items.size() - 1).position));
    }
    @Override
    public void draw(Canvas canvas) {
        PagerAdapter pagerAdapter;
        super.draw(canvas);
        int overScrollMode = ViewCompat.getOverScrollMode(this);
        boolean needsInvalidate = false;
        if (overScrollMode == 0 || (overScrollMode == 1 && (pagerAdapter = this.mAdapter) != null && pagerAdapter.getCount() > 1)) {
            if (!this.mLeftEdge.isFinished()) {
                int restoreCount = canvas.save();
                int height = (getHeight() - getPaddingTop()) - getPaddingBottom();
                int width = getWidth();
                canvas.rotate(270.0f);
                canvas.translate((-height) + getPaddingTop(), this.mFirstOffset * width);
                this.mLeftEdge.setSize(height, width);
                needsInvalidate = false | this.mLeftEdge.draw(canvas);
                canvas.restoreToCount(restoreCount);
            }
            if (!this.mRightEdge.isFinished()) {
                int restoreCount2 = canvas.save();
                int width2 = getWidth();
                int height2 = (getHeight() - getPaddingTop()) - getPaddingBottom();
                canvas.rotate(90.0f);
                canvas.translate(-getPaddingTop(), (-(this.mLastOffset + 1.0f)) * width2);
                this.mRightEdge.setSize(height2, width2);
                needsInvalidate |= this.mRightEdge.draw(canvas);
                canvas.restoreToCount(restoreCount2);
            }
        } else {
            this.mLeftEdge.finish();
            this.mRightEdge.finish();
        }
        if (needsInvalidate) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float nextOffset;
        float marginOffset;
        float drawOffset;
        super.onDraw(canvas);
        if (this.mPageMargin <= 0 || this.mMarginDrawable == null || this.mItems.size() <= 0 || this.mAdapter == null) {
            return;
        }
        int scrollX = getScrollX();
        int width = getWidth();
        float pageMargin = this.mPageMargin / width;
        int i = 0;
        ItemInfo itemInfo = this.mItems.get(0);
        float offset = itemInfo.offset;
        int itemCount = this.mItems.size();
        int position = itemInfo.position;
        int lastPosition = this.mItems.get(itemCount - 1).position;
        while (position < lastPosition) {
            while (position > itemInfo.position && i < itemCount) {
                i++;
                itemInfo = this.mItems.get(i);
            }
            if (position == itemInfo.position) {
                drawOffset = (itemInfo.offset + itemInfo.widthFactor) * width;
                nextOffset = itemInfo.offset + itemInfo.widthFactor + pageMargin;
            } else {
                float pageWidth = this.mAdapter.getPageWidth(position);
                drawOffset = (offset + pageWidth) * width;
                nextOffset = offset + pageWidth + pageMargin;
            }
            if (this.mPageMargin + drawOffset > scrollX) {
                this.mMarginDrawable.setBounds(Math.round(drawOffset), this.mTopPageBounds, Math.round(this.mPageMargin + drawOffset), this.mBottomPageBounds);
                this.mMarginDrawable.draw(canvas);
            }
            if (drawOffset > scrollX + width) {
                return;
            }
            position++;
            offset = nextOffset;
        }
    }

    public boolean beginFakeDrag() {
        if (this.mIsBeingDragged) {
            return false;
        }
        this.mFakeDragging = true;
        setScrollState(1);
        this.mLastMotionX = 0.0f;
        this.mInitialMotionX = 0.0f;
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        } else {
            velocityTracker.clear();
        }
        long time = SystemClock.uptimeMillis();
        MotionEvent ev = MotionEvent.obtain(time, time, 0, 0.0f, 0.0f, 0);
        this.mVelocityTracker.addMovement(ev);
        ev.recycle();
        this.mFakeDragBeginTime = time;
        return true;
    }

    public void endFakeDrag() {
        if (!this.mFakeDragging) {
            throw new IllegalStateException("No fake drag in progress. Call beginFakeDrag first.");
        }
        if (this.mAdapter != null) {
            VelocityTracker velocityTracker = this.mVelocityTracker;
            velocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
            int initialVelocity = (int) VelocityTrackerCompat.getXVelocity(velocityTracker, this.mActivePointerId);
            this.mPopulatePending = true;
            int width = getClientWidth();
            int scrollX = getScrollX();
            ItemInfo itemInfo = infoForCurrentScrollPosition();
            setCurrentItemInternal(determineTargetPage(itemInfo.position, ((scrollX / width) - itemInfo.offset) / itemInfo.widthFactor, initialVelocity, (int) (this.mLastMotionX - this.mInitialMotionX)), true, true, initialVelocity);
        }
        endDrag();
        this.mFakeDragging = false;
    }

    public void fakeDragBy(float xOffset) {
        if (!this.mFakeDragging) {
            throw new IllegalStateException("No fake drag in progress. Call beginFakeDrag first.");
        }
        if (this.mAdapter == null) {
            return;
        }
        this.mLastMotionX += xOffset;
        float scrollX = getScrollX() - xOffset;
        float clientWidth = getClientWidth();
        float leftBound = this.mFirstOffset * clientWidth;
        float rightBound = this.mLastOffset * clientWidth;
        ItemInfo firstItem = this.mItems.get(0);
        ArrayList<ItemInfo> items = this.mItems;
        ItemInfo lastItem = items.get(items.size() - 1);
        if (firstItem.position != 0) {
            leftBound = firstItem.offset * clientWidth;
        }
        if (lastItem.position != this.mAdapter.getCount() - 1) {
            rightBound = lastItem.offset * clientWidth;
        }
        if (scrollX < leftBound) {
            scrollX = leftBound;
        } else if (scrollX > rightBound) {
            scrollX = rightBound;
        }
        int scrollXInt = (int) scrollX;
        this.mLastMotionX += scrollX - scrollXInt;
        scrollTo(scrollXInt, getScrollY());
        pageScrolled(scrollXInt);
        MotionEvent ev = MotionEvent.obtain(this.mFakeDragBeginTime, SystemClock.uptimeMillis(), 2, this.mLastMotionX, 0.0f, 0);
        this.mVelocityTracker.addMovement(ev);
        ev.recycle();
    }

    public boolean isFakeDragging() {
        return this.mFakeDragging;
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        int pointerIndex = MotionEventCompat.getActionIndex(ev);
        if (MotionEventCompat.getPointerId(ev, pointerIndex) == this.mActivePointerId) {
            int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            this.mLastMotionX = MotionEventCompat.getX(ev, newPointerIndex);
            this.mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
            VelocityTracker velocityTracker = this.mVelocityTracker;
            if (velocityTracker != null) {
                velocityTracker.clear();
            }
        }
    }

    private void endDrag() {
        this.mIsBeingDragged = false;
        this.mIsUnableToDrag = false;
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    private void setScrollingCacheEnabled(boolean enabled) {
        if (this.mScrollingCacheEnabled != enabled) {
            this.mScrollingCacheEnabled = enabled;
        }
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        if (this.mAdapter == null) {
            return false;
        }
        int width = getClientWidth();
        int scrollX = getScrollX();
        if (direction < 0) {
            return scrollX > ((int) (((float) width) * this.mFirstOffset));
        }
        return direction > 0 && scrollX < ((int) (((float) width) * this.mLastOffset));
    }

    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        int childY;
        if (v instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) v;
            int scrollX = v.getScrollX();
            int scrollY = v.getScrollY();
            for (int childCount = group.getChildCount() - 1; childCount >= 0; childCount--) {
                View child = group.getChildAt(childCount);
                int childX = x + scrollX;
                if (childX >= child.getLeft() && childX < child.getRight() && (childY = y + scrollY) >= child.getTop() && childY < child.getBottom() && canScroll(child, true, dx, childX - child.getLeft(), childY - child.getTop())) {
                    return true;
                }
            }
        }
        return checkV && ViewCompat.canScrollHorizontally(v, -dx);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event) || executeKeyEvent(event);
    }

    public boolean executeKeyEvent(KeyEvent event) {
        if (event.getAction() == 0) {
            int keyCode = event.getKeyCode();
            if (keyCode == 21) {
                return arrowScroll(17);
            }
            if (keyCode == 22) {
                return arrowScroll(66);
            }
            if (keyCode == 61 && Build.VERSION.SDK_INT >= 11) {
                if (KeyEventCompat.hasNoModifiers(event)) {
                    return arrowScroll(2);
                }
                if (KeyEventCompat.hasModifiers(event, 1)) {
                    return arrowScroll(1);
                }
            }
        }
        return false;
    }

    public boolean arrowScroll(int direction) {
        boolean handled = false;
        boolean isChild;
        View currentFocus = findFocus();
        View focused = null;
        if (currentFocus != this) {
            if (currentFocus != null) {
                ViewParent parent = currentFocus.getParent();
                while (true) {
                    if (!(parent instanceof ViewGroup)) {
                        isChild = false;
                        break;
                    }
                    if (parent == this) {
                        isChild = true;
                        break;
                    }
                    parent = parent.getParent();
                }
                if (isChild) {
                    focused = currentFocus;
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(currentFocus.getClass().getSimpleName());
                    for (ViewParent ancestor = currentFocus.getParent(); ancestor instanceof ViewGroup; ancestor = ancestor.getParent()) {
                        sb.append(" => ");
                        sb.append(ancestor.getClass().getSimpleName());
                    }
                }
            } else {
                focused = currentFocus;
            }
        }
        View nextFocus = FocusFinder.getInstance().findNextFocus(this, focused, direction);
        if (nextFocus != null && nextFocus != focused) {
            if (direction == 17) {
                int nextLeft = getChildRectInPagerCoordinates(this.mTempRect, nextFocus).left;
                int currentLeft = getChildRectInPagerCoordinates(this.mTempRect, focused).left;
                if (focused != null && nextLeft >= currentLeft) {
                    handled = pageLeft();
                } else {
                    handled = nextFocus.requestFocus();
                }
            } else if (direction == 66) {
                int nextLeft = getChildRectInPagerCoordinates(this.mTempRect, nextFocus).left;
                int currentLeft = getChildRectInPagerCoordinates(this.mTempRect, focused).left;
                if (focused != null && nextLeft <= currentLeft) {
                    handled = pageRight();
                } else {
                    handled = nextFocus.requestFocus();
                }
            }
        } else if (direction == 17 || direction == 1) {
            handled = pageLeft();
        } else if (direction == 66 || direction == 2) {
            handled = pageRight();
        }
        if (handled) {
            playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
        }
        return handled;
    }

    private Rect getChildRectInPagerCoordinates(Rect outRect, View child) {
        if (outRect == null) {
            outRect = new Rect();
        }
        if (child == null) {
            outRect.set(0, 0, 0, 0);
            return outRect;
        }
        outRect.left = child.getLeft();
        outRect.right = child.getRight();
        outRect.top = child.getTop();
        outRect.bottom = child.getBottom();
        ViewParent parent = child.getParent();
        while ((parent instanceof ViewGroup) && parent != this) {
            ViewGroup group = (ViewGroup) parent;
            outRect.left += group.getLeft();
            outRect.right += group.getRight();
            outRect.top += group.getTop();
            outRect.bottom += group.getBottom();
            parent = group.getParent();
        }
        return outRect;
    }

    boolean pageLeft() {
        int currentItem = this.mCurItem;
        if (currentItem <= 0) {
            return false;
        }
        setCurrentItem(currentItem - 1, true);
        return true;
    }

    boolean pageRight() {
        PagerAdapter pagerAdapter = this.mAdapter;
        if (pagerAdapter == null || this.mCurItem >= pagerAdapter.getCount() - 1) {
            return false;
        }
        setCurrentItem(this.mCurItem + 1, true);
        return true;
    }
    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        ItemInfo itemInfo;
        int count = views.size();
        int descendantFocusability = getDescendantFocusability();
        if (descendantFocusability != 393216) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child.getVisibility() == 0 && (itemInfo = infoForChild(child)) != null && itemInfo.position == this.mCurItem) {
                    child.addFocusables(views, direction, focusableMode);
                }
            }
        }
        if ((descendantFocusability != 262144 || count == views.size()) && isFocusable()) {
            if (((focusableMode & 1) == 1 && isInTouchMode() && !isFocusableInTouchMode()) || views == null) {
                return;
            }
            views.add(this);
        }
    }

    @Override
    public void addTouchables(ArrayList<View> views) {
        ItemInfo itemInfo;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == 0 && (itemInfo = infoForChild(child)) != null && itemInfo.position == this.mCurItem) {
                child.addTouchables(views);
            }
        }
    }

    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        int index;
        int end;
        int increment;
        ItemInfo itemInfo;
        int childCount = getChildCount();
        if ((direction & 2) != 0) {
            index = 0;
            end = childCount;
            increment = 1;
        } else {
            index = childCount - 1;
            end = -1;
            increment = -1;
        }
        while (index != end) {
            View child = getChildAt(index);
            if (child.getVisibility() == 0 && (itemInfo = infoForChild(child)) != null && itemInfo.position == this.mCurItem && child.requestFocus(direction, previouslyFocusedRect)) {
                return true;
            }
            index += increment;
        }
        return false;
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        ItemInfo itemInfo;
        if (event.getEventType() == 4096) {
            return super.dispatchPopulateAccessibilityEvent(event);
        }
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == 0 && (itemInfo = infoForChild(child)) != null && itemInfo.position == this.mCurItem && child.dispatchPopulateAccessibilityEvent(event)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams params) {
        return generateDefaultLayoutParams();
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams params) {
        return (params instanceof LayoutParams) && super.checkLayoutParams(params);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    class MyAccessibilityDelegate extends AccessibilityDelegateCompat {
        MyAccessibilityDelegate() {
        }

        @Override
        public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
            super.onInitializeAccessibilityEvent(host, event);
            event.setClassName(ForbidScrollViewPager.class.getName());
            AccessibilityRecordCompat record = AccessibilityEventCompat.asRecord(event);
            record.setScrollable(canScroll());
            if (event.getEventType() != 4096 || ForbidScrollViewPager.this.mAdapter == null) {
                return;
            }
            record.setItemCount(ForbidScrollViewPager.this.mAdapter.getCount());
            record.setFromIndex(ForbidScrollViewPager.this.mCurItem);
            record.setToIndex(ForbidScrollViewPager.this.mCurItem);
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
            super.onInitializeAccessibilityNodeInfo(host, info);
            info.setClassName(ForbidScrollViewPager.class.getName());
            info.setScrollable(canScroll());
            if (ForbidScrollViewPager.this.canScrollHorizontally(1)) {
                info.addAction(4096);
            }
            if (ForbidScrollViewPager.this.canScrollHorizontally(-1)) {
                info.addAction(8192);
            }
        }

        @Override
        public boolean performAccessibilityAction(View host, int action, Bundle args) {
            if (super.performAccessibilityAction(host, action, args)) {
                return true;
            }
            if (action == 4096) {
                if (!ForbidScrollViewPager.this.canScrollHorizontally(1)) {
                    return false;
                }
                ForbidScrollViewPager pager = ForbidScrollViewPager.this;
                pager.setCurrentItem(pager.mCurItem + 1);
                return true;
            }
            if (action != 8192 || !ForbidScrollViewPager.this.canScrollHorizontally(-1)) {
                return false;
            }
            ForbidScrollViewPager pager2 = ForbidScrollViewPager.this;
            pager2.setCurrentItem(pager2.mCurItem - 1);
            return true;
        }

        private boolean canScroll() {
            return ForbidScrollViewPager.this.mAdapter != null && ForbidScrollViewPager.this.mAdapter.getCount() > 1;
        }
    }

    private class PagerObserver extends DataSetObserver {
        private PagerObserver() {
        }

        @Override
        public void onChanged() {
            ForbidScrollViewPager.this.dataSetChanged();
        }

        @Override
        public void onInvalidated() {
            ForbidScrollViewPager.this.dataSetChanged();
        }
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
        int childIndex;
        public int gravity;
        public boolean isDecor;
        boolean needsMeasure;
        int position;
        float widthFactor;

        public LayoutParams() {
            super(-1, -1);
            this.widthFactor = 0.0f;
        }

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.widthFactor = 0.0f;
            TypedArray array = context.obtainStyledAttributes(attrs, ForbidScrollViewPager.LAYOUT_ATTRS);
            this.gravity = array.getInteger(0, 48);
            array.recycle();
        }
    }

    static class ViewPositionComparator implements Comparator<View> {
        ViewPositionComparator() {
        }

        @Override
        public int compare(View first, View second) {
            LayoutParams firstParams = (LayoutParams) first.getLayoutParams();
            LayoutParams secondParams = (LayoutParams) second.getLayoutParams();
            if (firstParams.isDecor != secondParams.isDecor) {
                return firstParams.isDecor ? 1 : -1;
            }
            return firstParams.position - secondParams.position;
        }
    }

    public void setForbidScroll(boolean forbidScroll) {
        this.isForbidScroll = forbidScroll;
        if (forbidScroll && this.mIsBeingDragged) {
            int currentItem = this.mCurItem;
            scrollToItem(currentItem, currentItem, false, 0, false);
            resetTouch();
        }
    }

    public boolean isForbidScroll() {
        return this.isForbidScroll;
    }
}