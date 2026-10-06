package com.xtc.moment.module.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.view.KeyEventCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Drawer layout that slides vertically from the top of the screen.
 *
 * <p>The layout expects exactly two children: the content view first and the drawer view second.
 */
public class VerticalDrawerLayout extends ViewGroup {

    public static final int STATE_IDLE = 0;
    public static final int STATE_DRAGGING = 1;
    public static final int STATE_SETTLING = 2;

    /** Default scrim colour drawn over the content while the drawer is visible. */
    private static final int DEFAULT_CONTENT_SCRIM_COLOR = -1728053248;
    /** Minimum margin kept below the drawer, in dp. */
    private static final int MIN_DRAWER_MARGIN_DP = 64;
    /** Minimum fling velocity that settles the drawer, in dp/s. */
    private static final int MIN_FLING_VELOCITY_DP = 400;

    private static final int[] LAYOUT_ATTRS = {android.R.attr.layout_gravity};

    /** Drawer state values. */
    @Retention(RetentionPolicy.SOURCE)
    private @interface State {
    }

    /** Receives drawer open/close events. */
    public interface DrawerListener {
        void onDrawerClosed(View drawerView);

        void onDrawerOpened(View drawerView);

        void onDrawerSlide(View drawerView, float slideOffset);

        void onDrawerStateChanged(int newState);
    }

    /** Empty implementation of {@link DrawerListener}. */
    public static abstract class SimpleDrawerListener implements DrawerListener {
        @Override
        public void onDrawerClosed(View drawerView) {
        }

        @Override
        public void onDrawerOpened(View drawerView) {
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
        }

        @Override
        public void onDrawerStateChanged(int newState) {
        }
    }

    private final Paint mContentScrimPaint = new Paint();
    private final int mContentScrimColor = DEFAULT_CONTENT_SCRIM_COLOR;
    private final int mMinDrawerMargin;

    View mContentView;
    View mDrawerView;
    ViewDragHelper mDragHelper;
    DrawerListener mListener;
    Drawable mShadow;

    private float mContentScrimOpacity;
    private int mDrawerState;
    private boolean mFirstLayout = true;
    private boolean mInLayout;

    public VerticalDrawerLayout(Context context) {
        this(context, null);
    }

    public VerticalDrawerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalDrawerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        float density = getResources().getDisplayMetrics().density;
        this.mMinDrawerMargin = (int) ((MIN_DRAWER_MARGIN_DP * density) + 0.5f);
        this.mDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragCallback());
        this.mDragHelper.setMinVelocity(density * MIN_FLING_VELOCITY_DP);
        setFocusableInTouchMode(true);
    }

    public void setDrawerShadow(Drawable shadow) {
        this.mShadow = shadow;
        invalidate();
    }

    public void setDrawerListener(DrawerListener listener) {
        this.mListener = listener;
    }

    public void closeDrawer() {
        closeDrawerView(this.mDrawerView);
    }

    public void openDrawerView() {
        openDrawerView(this.mDrawerView);
    }

    public boolean isDrawerOpen() {
        return ((LayoutParams) this.mDrawerView.getLayoutParams()).knownOpen;
    }

    @Override
    public void computeScroll() {
        int childCount = getChildCount();
        float maxOffset = 0.0f;
        for (int i = 0; i < childCount; i++) {
            maxOffset = Math.max(maxOffset, ((LayoutParams) getChildAt(i).getLayoutParams()).onScreen);
        }
        this.mContentScrimOpacity = maxOffset;
        if (this.mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /** Callback that keeps the drawer view in sync while it is dragged. */
    public class ViewDragCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return false;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            float offset = 1.0f - ((-top) / (float) changedView.getHeight());
            setDrawerViewOffset(changedView, offset);
            mContentView.setVisibility(offset == 1.0f && changedView.getMeasuredHeight() == getMeasuredHeight()
                    ? INVISIBLE : VISIBLE);
            invalidate();
        }

        @Override
        public void onViewDragStateChanged(int state) {
            updateDrawerState(state, mDragHelper.getCapturedView());
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mFirstLayout = true;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mFirstLayout = true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 2) {
            throw new IllegalArgumentException("There must be 2 child in VerticalDrawerLayout");
        }
        this.mContentView = getChildAt(0);
        this.mDrawerView = getChildAt(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            if (isContentView(child)) {
                child.measure(MeasureSpec.makeMeasureSpec(
                                (width - params.leftMargin) - params.rightMargin, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(
                                (height - params.topMargin) - params.bottomMargin, MeasureSpec.EXACTLY));
            } else {
                child.measure(getChildMeasureSpec(widthMeasureSpec,
                                params.leftMargin + params.rightMargin, params.width),
                        getChildMeasureSpec(heightMeasureSpec,
                                this.mMinDrawerMargin + params.topMargin + params.bottomMargin, params.height));
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.mInLayout = true;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            if (isContentView(child)) {
                child.layout(params.leftMargin, params.topMargin,
                        params.leftMargin + child.getMeasuredWidth(),
                        params.topMargin + child.getMeasuredHeight());
                View drawer = this.mDrawerView;
                if (drawer != null && ((LayoutParams) drawer.getLayoutParams()).onScreen == 1.0f
                        && drawer.getMeasuredHeight() == getMeasuredHeight()) {
                    child.setVisibility(INVISIBLE);
                }
            } else if (params.onScreen == 0.0f) {
                child.layout(params.leftMargin, -child.getMeasuredHeight(),
                        params.leftMargin + child.getMeasuredWidth(), 0);
            } else {
                child.layout(params.leftMargin,
                        (-child.getMeasuredHeight()) + ((int) (child.getMeasuredHeight() * params.onScreen)),
                        params.leftMargin + child.getMeasuredWidth(),
                        (int) (child.getMeasuredHeight() * params.onScreen));
            }
        }
        this.mInLayout = false;
        this.mFirstLayout = false;
    }

    @Override
    public void requestLayout() {
        if (this.mInLayout) {
            return;
        }
        super.requestLayout();
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        int width = getWidth();
        int height = getHeight();
        boolean isContentView = isContentView(child);
        int saveCount = canvas.save();
        int clipBottom = 0;
        if (isContentView) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View other = getChildAt(i);
                if (other != child && other.getVisibility() == VISIBLE && hasOpaqueBackground(other)
                        && other == this.mDrawerView && other.getWidth() >= width) {
                    int bottom = other.getBottom();
                    if (bottom > clipBottom) {
                        clipBottom = bottom;
                    }
                }
            }
            canvas.clipRect(0, clipBottom, getWidth(), height);
        }
        boolean drawn = super.drawChild(canvas, child, drawingTime);
        canvas.restoreToCount(saveCount);
        float scrimOpacity = this.mContentScrimOpacity;
        if (scrimOpacity > 0.0f && isContentView) {
            this.mContentScrimPaint.setColor((this.mContentScrimColor & ViewCompat.MEASURED_SIZE_MASK)
                    | (((int) ((((-16777216) & this.mContentScrimColor) >>> 24) * scrimOpacity)) << 24));
            canvas.drawRect(0.0f, clipBottom, getWidth(), height, this.mContentScrimPaint);
        } else {
            Drawable shadow = this.mShadow;
            if (shadow != null) {
                int intrinsicHeight = shadow.getIntrinsicHeight();
                int bottom = child.getBottom();
                float alpha = Math.max(0.0f, Math.min(bottom / (float) this.mDragHelper.getEdgeSize(), 1.0f));
                this.mShadow.setBounds(child.getLeft(), bottom - intrinsicHeight, child.getRight(), getHeight());
                this.mShadow.setAlpha((int) (alpha * 255.0f));
                this.mShadow.draw(canvas);
            }
        }
        return drawn;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean intercepted = this.mDragHelper.shouldInterceptTouchEvent(event);
        boolean tapOnContent = false;
        if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
            tapOnContent = isContentView(this.mDragHelper.findTopChildUnder((int) event.getX(), (int) event.getY()))
                    && this.mContentScrimOpacity > 0.0f;
        }
        return intercepted || tapOnContent;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDragHelper.processTouchEvent(event);
        int action = event.getAction() & 0xFF;
        if (action == MotionEvent.ACTION_UP) {
            View child = this.mDragHelper.findTopChildUnder((int) event.getX(), (int) event.getY());
            if (child == null) {
                return false;
            }
            if (isContentView(child) && this.mContentScrimOpacity > 0.0f) {
                closeDrawer();
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isDrawerVisible()) {
            KeyEventCompat.startTracking(event);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            boolean visible = isDrawerVisible();
            if (visible) {
                closeDrawer();
            }
            return visible;
        }
        return super.onKeyUp(keyCode, event);
    }

    private boolean isDrawerVisible() {
        return ((LayoutParams) this.mDrawerView.getLayoutParams()).onScreen > 0.0f;
    }

    private boolean isContentView(View child) {
        return child == this.mContentView;
    }

    private void closeDrawerView(View drawerView) {
        if (this.mFirstLayout) {
            LayoutParams params = (LayoutParams) drawerView.getLayoutParams();
            params.onScreen = 0.0f;
            params.knownOpen = false;
        } else {
            this.mDragHelper.smoothSlideViewTo(drawerView, drawerView.getLeft(), -drawerView.getHeight());
        }
        invalidate();
    }

    private void openDrawerView(View drawerView) {
        if (this.mFirstLayout) {
            LayoutParams params = (LayoutParams) drawerView.getLayoutParams();
            params.onScreen = 1.0f;
            params.knownOpen = true;
        } else {
            this.mDragHelper.smoothSlideViewTo(drawerView, drawerView.getLeft(), 0);
        }
        invalidate();
    }

    void updateDrawerState(int state, View activeDrawer) {
        int dragState = this.mDragHelper.getViewDragState();
        int newState;
        if (dragState == ViewDragHelper.STATE_DRAGGING) {
            newState = STATE_DRAGGING;
        } else if (dragState == ViewDragHelper.STATE_SETTLING) {
            newState = STATE_SETTLING;
        } else {
            newState = STATE_IDLE;
        }
        if (activeDrawer != null && state == 0) {
            LayoutParams params = (LayoutParams) activeDrawer.getLayoutParams();
            if (params.onScreen == 0.0f) {
                dispatchOnDrawerClosed(activeDrawer);
            } else if (params.onScreen == 1.0f) {
                dispatchOnDrawerOpened(activeDrawer);
            }
        }
        if (newState != this.mDrawerState) {
            this.mDrawerState = newState;
            DrawerListener listener = this.mListener;
            if (listener != null) {
                listener.onDrawerStateChanged(newState);
            }
        }
    }

    void dispatchOnDrawerClosed(View drawerView) {
        LayoutParams params = (LayoutParams) drawerView.getLayoutParams();
        if (!params.knownOpen) {
            return;
        }
        params.knownOpen = false;
        DrawerListener listener = this.mListener;
        if (listener != null) {
            listener.onDrawerClosed(drawerView);
        }
        updateChildrenImportantForAccessibility(drawerView, false);
        View rootView = getRootView();
        if (hasWindowFocus() && rootView != null) {
            rootView.sendAccessibilityEvent(32);
        }
    }

    void dispatchOnDrawerOpened(View drawerView) {
        LayoutParams params = (LayoutParams) drawerView.getLayoutParams();
        if (params.knownOpen) {
            return;
        }
        params.knownOpen = true;
        DrawerListener listener = this.mListener;
        if (listener != null) {
            listener.onDrawerOpened(drawerView);
        }
        updateChildrenImportantForAccessibility(drawerView, true);
        if (hasWindowFocus()) {
            sendAccessibilityEvent(32);
        }
        drawerView.requestFocus();
    }

    private void updateChildrenImportantForAccessibility(View drawerView, boolean isDrawerOpen) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if ((!isDrawerOpen && child != this.mDrawerView) || (isDrawerOpen && child == drawerView)) {
                ViewCompat.setImportantForAccessibility(child, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES);
            } else {
                ViewCompat.setImportantForAccessibility(child, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
            }
        }
    }

    void dispatchOnDrawerSlide(View drawerView, float slideOffset) {
        DrawerListener listener = this.mListener;
        if (listener != null) {
            listener.onDrawerSlide(drawerView, slideOffset);
        }
    }

    void setDrawerViewOffset(View drawerView, float slideOffset) {
        LayoutParams params = (LayoutParams) drawerView.getLayoutParams();
        if (slideOffset == params.onScreen) {
            return;
        }
        params.onScreen = slideOffset;
        dispatchOnDrawerSlide(drawerView, slideOffset);
    }

    float getDrawerViewOffset(View drawerView) {
        return ((LayoutParams) drawerView.getLayoutParams()).onScreen;
    }

    private static boolean hasOpaqueBackground(View view) {
        Drawable background = view.getBackground();
        return background != null && background.getOpacity() == -1;
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams params) {
        if (params instanceof LayoutParams) {
            return new LayoutParams((LayoutParams) params);
        }
        return params instanceof ViewGroup.MarginLayoutParams
                ? new LayoutParams((ViewGroup.MarginLayoutParams) params)
                : new LayoutParams(params);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams params) {
        return (params instanceof LayoutParams) && super.checkLayoutParams(params);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /** Layout params that remember the drawer slide offset and open state. */
    public static class LayoutParams extends ViewGroup.MarginLayoutParams {

        public int gravity;
        boolean knownOpen;
        float onScreen;

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.gravity = 0;
            TypedArray typedArray = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
            this.gravity = typedArray.getInt(0, 0);
            typedArray.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            this.gravity = 0;
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height);
            this.gravity = 0;
            this.gravity = gravity;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
            this.gravity = 0;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
            this.gravity = 0;
        }

        public LayoutParams(LayoutParams source) {
            super(source);
            this.gravity = 0;
            this.gravity = source.gravity;
        }
    }
}