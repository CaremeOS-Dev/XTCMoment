package com.xtc.ui.widget.scalablecontainer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.ui.widget.util.PressAnimHelper;
import com.xtc.ui.widget.util.UiTouchPointUtil;

/** 带按压缩放反馈的相对布局，并提供水平/垂直位移动画。 */
public class AppRelativeLayout extends RelativeLayout {
    private static final String TAG = "AppRelativeLayout";
    private Animator.AnimatorListener animatorListener;
    private ObjectAnimator horizontalAnimator;
    private Point point;
    private PressAnimHelper pressAnimHelper;
    private final Runnable pressTask;
    private final Runnable releaseTask;
    private View specialView;
    private View.OnClickListener specialViewListener;
    private ObjectAnimator verticalAnimator;

    public AppRelativeLayout(Context context) {
        this(context, null);
    }

    public AppRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.animatorListener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                LogUtil.d(AppRelativeLayout.TAG, "AnimatorListener onAnimationEnd");
                AppRelativeLayout.this.resetTranslation();
            }
        };
        this.pressTask = new Runnable() {
            @Override
            public void run() {
                AppRelativeLayout.this.pressAnimHelper.press();
            }
        };
        this.releaseTask = new Runnable() {
            @Override
            public void run() {
                AppRelativeLayout.this.pressAnimHelper.release();
            }
        };
        setClickable(true);
        TypedArray attributes = attrs != null ? context.obtainStyledAttributes(attrs, R.styleable.AppRelativeLayout) : null;
        boolean useAlpha = optBoolean(attributes, R.styleable.AppRelativeLayout_useAlphaForRL, true);
        boolean useZoom = optBoolean(attributes, R.styleable.AppRelativeLayout_useZoomForRL, true);
        if (attributes != null) {
            attributes.recycle();
        }
        this.pressAnimHelper = new PressAnimHelper(this, useAlpha, useZoom);
        initAnimation();
    }

    private boolean optBoolean(TypedArray attributes, int index, boolean defaultValue) {
        return attributes == null ? defaultValue : attributes.getBoolean(index, defaultValue);
    }

    private void initAnimation() {
        this.horizontalAnimator = new ObjectAnimator();
        this.horizontalAnimator.setTarget(this);
        this.horizontalAnimator.setPropertyName("translationX");
        this.horizontalAnimator.setDuration(350L);
        this.horizontalAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        this.horizontalAnimator.addListener(this.animatorListener);
        this.verticalAnimator = new ObjectAnimator();
        this.verticalAnimator.setTarget(this);
        this.verticalAnimator.setPropertyName("translationY");
        this.verticalAnimator.setDuration(400L);
        this.verticalAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        this.verticalAnimator.addListener(this.animatorListener);
    }

    public void startHorizontalAnimation(float fromTranslation) {
        this.verticalAnimator.cancel();
        this.horizontalAnimator.cancel();
        resetTranslation();
        this.horizontalAnimator.setFloatValues(fromTranslation, 0.0f);
        this.horizontalAnimator.start();
    }

    public void startVerticalAnimation(float fromTranslation) {
        this.horizontalAnimator.cancel();
        this.verticalAnimator.cancel();
        resetTranslation();
        this.verticalAnimator.setFloatValues(fromTranslation, 0.0f);
        this.verticalAnimator.start();
    }

    private void resetTranslation() {
        LogUtil.d(TAG, "resetTranslation ---");
        setTranslationX(0.0f);
        setTranslationY(0.0f);
    }

    public void setSpecialView(View specialView) {
        this.specialView = specialView;
    }

    private void setSpecialViewAndListener(View specialView, View.OnClickListener listener) {
        this.specialView = specialView;
        this.specialViewListener = listener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Point point = this.point;
        if (point == null) {
            this.point = new Point((int) event.getRawX(), (int) event.getRawY());
        } else {
            point.set((int) event.getRawX(), (int) event.getRawY());
        }
        if (UiTouchPointUtil.isTouchPointInView(this.specialView, this.point)) {
            this.pressAnimHelper.release();
            return super.dispatchTouchEvent(event);
        }
        int action = event.getAction();
        LogUtil.d(TAG, "dispatchTouchEvent:action=" + action);
        if (action == 0) {
            post(this.pressTask);
        } else if (action == 1 || action == 3) {
            post(this.releaseTask);
        }
        return super.dispatchTouchEvent(event);
    }

    private void dealSpecialView() {
        View specialView = this.specialView;
        if (specialView == null) {
            return;
        }
        View.OnClickListener listener = this.specialViewListener;
        if (listener == null) {
            return;
        }
        listener.onClick(specialView);
    }
}