package com.xtc.ui.widget.util;

import android.os.Build;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;

/** 按压反馈动画助手：按下缩放/降透明度，抬起恢复。 */
public class PressAnimHelper {
    private static final float ALPHA_SMALL = 0.8f;
    public static final float RESET_ALPHA = 1.0f;
    public static final float RESET_SCALE = 1.0f;
    private static final float SCALE_SMALL = 0.95f;
    private boolean isAlpha;
    private boolean isScale;
    private Interpolator pressInterpolator;
    private Interpolator releaseInterpolator;
    private View view;
    private float smallScale = SCALE_SMALL;
    private float smallAlpha = 0.8f;

    public PressAnimHelper(View view, boolean isAlpha, boolean isScale) {
        this.view = view;
        this.isAlpha = isAlpha;
        this.isScale = isScale;
        if (Build.VERSION.SDK_INT >= 21) {
            this.pressInterpolator = new PathInterpolator(0.0f, 0.0f, 0.58f, 1.0f);
            this.releaseInterpolator = new PathInterpolator(0.42f, 0.0f, 1.0f, 1.0f);
        }
    }

    public void setSmallScale(float smallScale) {
        this.smallScale = smallScale;
    }

    public void setSmallAlpha(float smallAlpha) {
        this.smallAlpha = smallAlpha;
    }

    public void press() {
        ViewPropertyAnimator animator = this.view.animate().setDuration(150L);
        if (this.isScale) {
            animator.scaleX(this.smallScale).scaleY(this.smallScale);
        }
        if (this.isAlpha) {
            animator.alpha(this.smallAlpha);
        }
        Interpolator interpolator = this.pressInterpolator;
        if (interpolator != null) {
            animator.setInterpolator(interpolator);
        }
        animator.start();
    }

    public void release() {
        ViewPropertyAnimator animator = this.view.animate().setDuration(150L);
        if (this.isScale) {
            animator.scaleX(1.0f).scaleY(1.0f);
        }
        if (this.isAlpha) {
            animator.alpha(1.0f);
        }
        Interpolator interpolator = this.releaseInterpolator;
        if (interpolator != null) {
            animator.setInterpolator(interpolator);
        }
        animator.start();
    }
}