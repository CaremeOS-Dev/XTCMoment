package com.xtc.shareapi.share.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;

import com.xtc.shareapi.share.constant.OpenApiConstant;

/**
 * 分享场景入口的线性布局，按下时缩放、抬起时恢复。
 */
public class AppLinearLayout extends LinearLayout {

    private AnimatorSet zoomSet;
    private AnimatorSet resetSet;

    private final Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            Log.d(OpenApiConstant.TAG, "AnimatorListener onAnimationEnd");
            setTranslationX(0.0f);
            setTranslationY(0.0f);
        }

        @Override
        public void onAnimationCancel(Animator animator) {
        }

        @Override
        public void onAnimationRepeat(Animator animator) {
        }
    };

    public AppLinearLayout(Context context) {
        this(context, null);
    }

    public AppLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAnimation();
    }

    private void initAnimation() {
        ObjectAnimator translationX = new ObjectAnimator();
        translationX.setTarget(this);
        translationX.setPropertyName("translationX");
        translationX.setDuration(350L);
        translationX.setInterpolator(new AccelerateDecelerateInterpolator());
        translationX.addListener(animatorListener);

        ObjectAnimator translationY = new ObjectAnimator();
        translationY.setTarget(this);
        translationY.setPropertyName("translationY");
        translationY.setDuration(400L);
        translationY.setInterpolator(new AccelerateDecelerateInterpolator());
        translationY.addListener(animatorListener);

        ObjectAnimator zoomAlpha = ObjectAnimator.ofFloat(this, "alpha", 1.0f, 0.6f);
        ObjectAnimator zoomScaleX = ObjectAnimator.ofFloat(this, "scaleX", 1.0f, 0.95f);
        ObjectAnimator zoomScaleY = ObjectAnimator.ofFloat(this, "scaleY", 1.0f, 0.95f);
        zoomSet = new AnimatorSet();
        zoomSet.setDuration(100L);
        zoomSet.setInterpolator(new AccelerateDecelerateInterpolator());
        zoomSet.playTogether(zoomAlpha, zoomScaleX, zoomScaleY);

        ObjectAnimator resetAlpha = ObjectAnimator.ofFloat(this, "alpha", 0.6f, 1.0f);
        ObjectAnimator resetScaleX = ObjectAnimator.ofFloat(this, "scaleX", 0.95f, 1.0f);
        ObjectAnimator resetScaleY = ObjectAnimator.ofFloat(this, "scaleY", 0.95f, 1.0f);
        resetSet = new AnimatorSet();
        resetSet.setDuration(100L);
        resetSet.setInterpolator(new AccelerateDecelerateInterpolator());
        resetSet.playTogether(resetAlpha, resetScaleX, resetScaleY);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (resetSet.isRunning()) {
                resetSet.cancel();
            }
            if (!zoomSet.isRunning()) {
                zoomSet.start();
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            if (zoomSet.isRunning()) {
                zoomSet.cancel();
            }
            if (!resetSet.isRunning()) {
                resetSet.start();
            }
        }
        return super.dispatchTouchEvent(event);
    }
}