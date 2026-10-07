package com.xtc.ui.widget.scalablecontainer;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.animation.DynamicAnimation;
import android.support.animation.FloatPropertyCompat;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ScrollView;
import com.xtc.moment.R;
import com.xtc.ui.widget.util.SpringAnimationUtils;
import com.xtc.ui.widget.util.ViewUtils;
import java.util.ArrayList;
import java.util.List;

/** 带边缘回弹与子项缩放的 ScrollView。 */
public class AppScrollView extends ScrollView {
    public static final float DEFAULT_TOUCH_DRAG_MOVE_RATIO = 1.5f;
    private static final int DRAG_SIDE_END = 2;
    private static final int DRAG_SIDE_START = 1;
    public static final int OVER_SCROLLING_STATE = 1;
    public static final int OVER_SCROLL_FLING_CHECKING = 3;
    public static final int OVER_SCROLL_FLING_ING = 4;
    public static final int OVER_SCROLL_STATE_BACKING = 2;
    public static final int OVER_SCROLL_STATE_IDLE = 0;
    public static final float RESET_SCALE = 1.0f;
    public static final float START_SCALE = 0.8f;
    private static final String TAG = "AppScrollView";
    public static final int TYPE_DRAG_OVER_BACK = 1;
    public static final int TYPE_FLING_BACK = 0;
    private SpringAnimation anim;
    private List<View> animScaleViews;
    private DynamicAnimation.OnAnimationEndListener animationEndListener;
    private boolean enableEnd;
    private boolean enableStart;
    private int flingOverScrollState;
    private float flingVelocityY;
    private boolean isAnimScale;
    private long lastTrackTime;
    private int lastY;
    private int overScrollState;
    private int startDragSide;
    private int startPointId;

    public AppScrollView(Context context) {
        this(context, null);
    }

    public AppScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.overScrollState = 0;
        this.flingOverScrollState = 0;
        this.enableStart = true;
        this.enableEnd = true;
        this.animationEndListener = new DynamicAnimation.OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                AppScrollView.this.overScrollState = 0;
                AppScrollView.this.flingOverScrollState = 0;
            }
        };
        setOverScrollMode(2);
        this.isAnimScale = true;
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.AppScrollView, 0, 0);
        if (attributes != null) {
            this.isAnimScale = attributes.getBoolean(R.styleable.AppScrollView_asv_anim_scale, true);
            this.enableStart = attributes.getBoolean(R.styleable.AppNestedScrollView_ansv_enable_start, true);
            this.enableEnd = attributes.getBoolean(R.styleable.AppNestedScrollView_ansv_enable_end, true);
            attributes.recycle();
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if (this.isAnimScale) {
            View firstChild = getChildAt(0);
            if (firstChild instanceof ViewGroup) {
                setAnimScaleViews(collectChildren((ViewGroup) firstChild));
            }
        }
    }

    public void setAnimScale(boolean animScale) {
        this.isAnimScale = animScale;
    }

    public boolean isEnableStart() {
        return this.enableStart;
    }

    public void setEnableStart(boolean enableStart) {
        this.enableStart = enableStart;
    }

    public boolean isEnableEnd() {
        return this.enableEnd;
    }

    public void setEnableEnd(boolean enableEnd) {
        this.enableEnd = enableEnd;
    }

    public void setAnimScaleViews(List<View> animScaleViews) {
        this.animScaleViews = animScaleViews;
        post(new Runnable() {
            @Override
            public void run() {
                AppScrollView.this.scaleVerticalChildView();
            }
        });
    }

    private boolean hasChild() {
        return getChildCount() != 0;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!this.isAnimScale || this.animScaleViews == null) {
            return;
        }
        scaleVerticalChildView();
    }

    @Override
    protected void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        super.onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY);
        doScrollChanged(scrollY);
    }

    private void doScrollChanged(int scrollY) {
        if (hasChild()) {
            long now = System.currentTimeMillis();
            long elapsed = now - this.lastTrackTime;
            int currentScrollY = getScrollY();
            int previousScrollY = this.lastY;
            if (currentScrollY != previousScrollY && elapsed > 0) {
                this.flingVelocityY = ((currentScrollY - previousScrollY) * 1000.0f) / elapsed;
                this.lastY = currentScrollY;
                this.lastTrackTime = now;
            }
            if (this.flingOverScrollState == 3) {
                float translationY = getChildAt(0).getTranslationY();
                boolean atStart = ViewUtils.isInAbsoluteStart(this, 1);
                boolean atEnd = ViewUtils.isInAbsoluteEnd(this, 1);
                if ((atStart && this.enableStart && this.flingVelocityY < 0.0f)
                        || (atEnd && this.enableEnd && this.flingVelocityY > 0.0f)) {
                    this.flingOverScrollState = 4;
                    float startVelocity = ((-this.flingVelocityY) * 1.0f) / 2.0f;
                    createAnimIfNeed(0);
                    this.anim.setStartVelocity(startVelocity);
                    this.anim.animateToFinalPosition(0.0f);
                } else if ((atStart || atEnd) && translationY != 0.0f) {
                    this.flingOverScrollState = 4;
                    createAnimIfNeed(0);
                    this.anim.animateToFinalPosition(0.0f);
                }
            }
            if (!this.isAnimScale || this.animScaleViews == null) {
                return;
            }
            scaleVerticalChildView();
        }
    }

    private void scaleVerticalChildView() {
        int scrollY = getScrollY();
        int measuredHeight = getMeasuredHeight();
        for (View view : this.animScaleViews) {
            if (view.getVisibility() == 0) {
                int top = view.getTop();
                int bottom = view.getBottom();
                int height = view.getHeight();
                int width = view.getWidth();
                int bottomEdge = scrollY + measuredHeight;
                if (bottom >= scrollY && top <= bottomEdge) {
                    float scale = ((((bottom <= bottomEdge || top >= bottomEdge) ? height : bottomEdge - top) * 0.19999999f) / height) + 0.8f;
                    view.setPivotX(width / 2.0f);
                    view.setPivotY(0.0f);
                    view.setScaleX(scale);
                    view.setScaleY(scale);
                }
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!hasChild()) {
            return super.dispatchTouchEvent(event);
        }
        View firstChild = getChildAt(0);
        int action = event.getAction();
        float translationY = firstChild.getTranslationY();
        SpringAnimation springAnimation = this.anim;
        if (springAnimation != null && springAnimation.isRunning()) {
            this.anim.cancel();
        }
        if (action == 1) {
            int state = this.overScrollState;
            if (state != 2) {
                if (state == 1) {
                    finishOverScroll();
                } else if (this.flingOverScrollState == 0) {
                    this.flingVelocityY = 0.0f;
                    this.lastY = getScrollY();
                    this.flingOverScrollState = 3;
                    this.lastTrackTime = System.currentTimeMillis();
                    doScrollChanged(this.lastY);
                }
            }
        } else if (action != 2) {
            if (action == 3) {
                int state = this.overScrollState;
                if (state != 2) {
                    if (state == 1) {
                        finishOverScroll();
                    } else if (this.flingOverScrollState == 0) {
                        this.flingVelocityY = 0.0f;
                        this.lastY = getScrollY();
                        this.flingOverScrollState = 3;
                        this.lastTrackTime = System.currentTimeMillis();
                        doScrollChanged(this.lastY);
                    }
                }
            }
        } else if (event.getHistorySize() != 0) {
            float deltaY = event.getY(0) - event.getHistoricalY(0, 0);
            if (Math.abs(deltaY) >= Math.abs(event.getX(0) - event.getHistoricalX(0, 0))) {
                int dragSide = deltaY > 0.0f ? 1 : 2;
                boolean startEnabled = ViewUtils.isInAbsoluteStart(this, 1) && this.enableStart;
                boolean endEnabled = ViewUtils.isInAbsoluteEnd(this, 1) && this.enableEnd;
                if (this.overScrollState == 0) {
                    if ((dragSide == 1 && startEnabled) || (dragSide == 2 && endEnabled)) {
                        this.startPointId = event.getPointerId(0);
                        this.startDragSide = dragSide;
                        this.overScrollState = 1;
                    }
                }
                if (this.overScrollState == 1) {
                    if (this.startPointId != event.getPointerId(0)) {
                        finishOverScroll();
                    } else {
                        float newTranslationY = translationY + (deltaY / 1.5f);
                        int currentDragSide = this.startDragSide;
                        if (dragSide != currentDragSide
                                && ((currentDragSide == 1 && newTranslationY <= 0.0f)
                                || (this.startDragSide == 2 && newTranslationY > 0.0f))) {
                            this.overScrollState = 0;
                        } else {
                            ViewParent parent = getParent();
                            if (parent != null) {
                                parent.requestDisallowInterceptTouchEvent(true);
                            }
                            firstChild.setTranslationY(newTranslationY);
                        }
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void finishOverScroll() {
        this.overScrollState = 2;
        createAnimIfNeed(1);
        this.anim.setStartVelocity(0.0f).animateToFinalPosition(0.0f);
    }

    private void createAnimIfNeed(int type) {
        if (this.anim == null) {
            View firstChild = getChildAt(0);
            this.anim = new SpringAnimation(firstChild, (FloatPropertyCompat<View>) SpringAnimationUtils.FLOAT_PROPERTY_TRANSLATION_Y)
                    .setSpring(new SpringForce().setDampingRatio(1.0f).setStiffness(115.0f));
            this.anim.addEndListener(this.animationEndListener);
        }
        SpringForce spring = this.anim.getSpring();
        if (type == 0) {
            spring.setStiffness(115.0f);
        }
        if (type == 1) {
            spring.setStiffness(200.0f);
        }
    }

    private List<View> collectChildren(ViewGroup viewGroup) {
        int childCount = viewGroup.getChildCount();
        ArrayList<View> children = new ArrayList<>();
        for (int index = 0; index < childCount; index++) {
            children.add(viewGroup.getChildAt(index));
        }
        return children;
    }
}