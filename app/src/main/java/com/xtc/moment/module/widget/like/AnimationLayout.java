package com.xtc.moment.module.widget.like;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.xtc.moment.module.widget.like.evaluator.CurveEvaluatorRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Base layout for "like" animations that fly small images along a curve.
 *
 * <p>Subclasses decide which resources are used and how the animation is built; this class keeps
 * the shared state (measured size, picture size, running animator sets) and the lifecycle handling.
 */
public abstract class AnimationLayout extends FrameLayout implements IAnimationLayout {

    /** Animator sets that are still running; cancelled when the view is detached. */
    protected List<AnimatorSet> animatorSets;
    /** Cached curve evaluators reused once enough animations have been played. */
    protected CurveEvaluatorRecord evaluatorRecord;
    /** Width of the last decoded picture, in pixels. */
    protected float picWidth;
    /** Height of the last decoded picture, in pixels. */
    protected float picHeight;
    /** Width of this view, in pixels. */
    protected int viewWidth;
    /** Height of this view, in pixels. */
    protected int viewHeight;
    protected final Random random;

    public AnimationLayout(Context context) {
        this(context, null);
    }

    public AnimationLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.random = new Random();
        init();
    }

    protected void init() {
        this.animatorSets = new ArrayList<AnimatorSet>();
        this.evaluatorRecord = new CurveEvaluatorRecord();
    }

    /** Decodes the bounds of {@code resId} without loading the bitmap itself. */
    public void getPictureInfo(int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getContext().getResources(), resId, options);
        this.picWidth = options.outWidth;
        this.picHeight = options.outHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.viewWidth = getMeasuredWidth();
        this.viewHeight = getMeasuredHeight();
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        this.viewWidth = getMeasuredWidth();
        this.viewHeight = getMeasuredHeight();
    }

    /** Moves the animated child along the curve and fades it out. */
    protected static class CurveUpdateLister implements ValueAnimator.AnimatorUpdateListener {

        private final View child;

        protected CurveUpdateLister(View child) {
            this.child = child;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            PointF point = (PointF) animation.getAnimatedValue();
            this.child.setX(point.x);
            this.child.setY(point.y);
            this.child.setAlpha(1.0f - animation.getAnimatedFraction());
        }
    }

    /** Removes the animated child from its parent once the animation has finished. */
    protected class AnimationEndListener extends AnimatorListenerAdapter {

        private final View child;
        private final ViewGroup parent;
        private final AnimatorSet animatorSet;

        protected AnimationEndListener(View child, ViewGroup parent, AnimatorSet animatorSet) {
            this.child = child;
            this.parent = parent;
            this.animatorSet = animatorSet;
            AnimationLayout.this.animatorSets.add(this.animatorSet);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            this.parent.removeView(this.child);
            AnimationLayout.this.animatorSets.remove(this.animatorSet);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onPause();
    }

    /** Cancels every running animation and hides the layout. */
    public void onPause() {
        removeAllViews();
        for (AnimatorSet animatorSet : this.animatorSets) {
            animatorSet.getListeners().clear();
            animatorSet.cancel();
        }
        this.animatorSets.clear();
        this.evaluatorRecord.destroy();
        setVisibility(GONE);
    }

    public void onResume() {
        setVisibility(VISIBLE);
    }
}