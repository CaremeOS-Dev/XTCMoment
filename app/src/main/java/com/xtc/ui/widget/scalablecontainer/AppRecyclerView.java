package com.xtc.ui.widget.scalablecontainer;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.SystemClock;
import android.support.animation.FloatPropertyCompat;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import com.xtc.ui.widget.R;
import com.xtc.ui.widget.util.ViewUtils;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/** 带边缘拖拽回弹的 RecyclerView。 */
public class AppRecyclerView extends RecyclerView {
    public static final long FLING_CALCULATE_INTERVAL = 30;
    public static final float OVER_TRANSLATION_RATIO = 2.0f;
    private static final FloatPropertyCompat<AppRecyclerView> PROPERTY_OVER_TRANSLATION_Y = new FloatPropertyCompat<AppRecyclerView>("overTranslationY") {
        @Override
        public float getValue(AppRecyclerView recyclerView) {
            return recyclerView.getOverTranslationY();
        }

        @Override
        public void setValue(AppRecyclerView recyclerView, float value) {
            recyclerView.setOverTranslationY((int) value);
        }
    };
    private static final String TAG = "AppRecyclerView";
    private Set<View> animChildren;
    private SpringAnimation animation;
    private boolean enableEnd;
    private boolean enableStart;
    private int flingVelocityY;
    private boolean forbidEdgeDrag;
    private boolean isAvailable;
    private long lastScrollTime;
    private int lastScrollY;
    private int maxVelocityY;
    private float overTranslationY;
    private int scrollState;
    private int startPointId;
    private int totalScrollY;

    public AppRecyclerView(Context context) {
        this(context, null);
    }

    public AppRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.enableStart = true;
        this.enableEnd = true;
        this.isAvailable = Build.VERSION.SDK_INT >= 27;
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.AppRecyclerView, 0, 0);
        if (attributes != null) {
            this.enableStart = attributes.getBoolean(R.styleable.AppRecyclerView_arv_enable_start, true);
            this.enableEnd = attributes.getBoolean(R.styleable.AppRecyclerView_arv_enable_end, true);
            attributes.recycle();
        }
        this.animChildren = new HashSet<>();
        this.animation = new SpringAnimation(this, PROPERTY_OVER_TRANSLATION_Y)
                .setSpring(new SpringForce().setDampingRatio(1.0f).setStiffness(150.0f));
        this.maxVelocityY = ViewConfiguration.get(context).getScaledMaximumFlingVelocity() / 2;
    }

    public float getOverTranslationY() {
        return this.overTranslationY;
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }

    public void setForbidEdgeDrag(boolean forbidEdgeDrag) {
        this.forbidEdgeDrag = forbidEdgeDrag;
    }

    @Override
    public void onScrolled(int dx, int dy) {
        if (!this.isAvailable) {
            super.onScrolled(dx, dy);
            return;
        }
        this.totalScrollY += dy;
        boolean atStart = ViewUtils.isInAbsoluteStart(this, 1);
        boolean atEnd = ViewUtils.isInAbsoluteEnd(this, 1);
        if (!atStart && !atEnd && isOverTranslationNeedReset()) {
            stopAnim();
            setOverTranslationY(0.0f);
            this.flingVelocityY = 0;
            this.lastScrollY = this.totalScrollY;
            this.lastScrollTime = SystemClock.elapsedRealtime();
            return;
        }
        int scrollDelta = this.totalScrollY - this.lastScrollY;
        if (Math.abs(scrollDelta) < 30) {
            return;
        }
        long now = SystemClock.elapsedRealtime();
        long elapsed = now - this.lastScrollTime;
        if (elapsed <= 0) {
            return;
        }
        this.flingVelocityY = (int) (((long) (scrollDelta * 1000)) / elapsed);
        this.lastScrollY = this.totalScrollY;
        this.lastScrollTime = now;
    }

    private boolean isOverTranslationNeedReset() {
        return Math.abs(this.overTranslationY) > 30.0f;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!this.isAvailable) {
            return super.dispatchTouchEvent(event);
        }
        if (this.forbidEdgeDrag) {
            return super.dispatchTouchEvent(event);
        }
        int action = event.getAction();
        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        if (layoutManager == null) {
            return super.dispatchTouchEvent(event);
        }
        boolean canScrollVertically = layoutManager.canScrollVertically();
        int childCount = getChildCount();
        if (!canScrollVertically || childCount == 0) {
            return super.dispatchTouchEvent(event);
        }
        int newOverTranslation = 0;
        boolean startEnabled = ViewUtils.isInAbsoluteStart(this, 1) && this.enableStart;
        boolean endEnabled = ViewUtils.isInAbsoluteEnd(this, 1) && this.enableEnd;
        if (action == 1) {
            finishOverScroll();
        } else if (action != 2) {
            if (action == 3) {
                finishOverScroll();
            }
        } else if (event.getHistorySize() != 0) {
            float deltaY = event.getY(0) - event.getHistoricalY(0, 0);
            if (Math.abs(deltaY) >= Math.abs(event.getX(0) - event.getHistoricalX(0, 0))) {
                int pointerId = event.getPointerId(0);
                float currentTranslation = this.overTranslationY;
                if (currentTranslation != 0.0f) {
                    if (pointerId == this.startPointId) {
                        int candidate = (int) ((deltaY / 2.0f) + currentTranslation);
                        if (candidate * currentTranslation >= 0.0f) {
                            newOverTranslation = candidate;
                        }
                    }
                    stopAnim();
                    setOverTranslationY(newOverTranslation);
                } else if ((deltaY > 0.0f && startEnabled) || (deltaY < 0.0f && endEnabled)) {
                    this.startPointId = pointerId;
                    setOverTranslationY((int) (deltaY / 2.0f));
                    ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void stopAnim() {
        if (this.animation.isRunning()) {
            this.animation.cancel();
        }
    }

    private void setOverTranslationY(float translationY) {
        int childCount = getChildCount();
        for (int index = 0; index < childCount; index++) {
            this.animChildren.add(getChildAt(index));
        }
        Iterator<View> iterator = this.animChildren.iterator();
        while (iterator.hasNext()) {
            iterator.next().setTranslationY(translationY);
        }
        this.overTranslationY = translationY;
        if (translationY == 0.0f) {
            this.animChildren.clear();
        }
    }

    private void finishOverScroll() {
        this.animation.animateToFinalPosition(0.0f);
    }

    @Override
    public void onScrollStateChanged(int newState) {
        if (!this.isAvailable) {
            super.onScrollStateChanged(newState);
            return;
        }
        if (this.scrollState != 2 || newState != 0 || isOverTranslationNeedReset()) {
            this.scrollState = newState;
            return;
        }
        this.scrollState = newState;
        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        if (layoutManager != null && layoutManager.canScrollVertically()) {
            boolean shouldFlingBack = true;
            boolean startEnabled = ViewUtils.isInAbsoluteStart(this, 1) && this.enableStart;
            boolean endEnabled = ViewUtils.isInAbsoluteEnd(this, 1) && this.enableEnd;
            if ((!startEnabled || this.flingVelocityY >= 0) && (!endEnabled || this.flingVelocityY <= 0)) {
                shouldFlingBack = false;
            }
            if (shouldFlingBack) {
                float velocity = Math.min(Math.abs(this.flingVelocityY), this.maxVelocityY);
                if (velocity < 0.0f) {
                    return;
                }
                if (this.flingVelocityY >= 0) {
                    velocity = -velocity;
                }
                this.animation.setStartVelocity(velocity).animateToFinalPosition(0.0f);
            }
        }
    }
}