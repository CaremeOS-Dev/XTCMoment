package com.xtc.ui.widget.animation.indicator;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Base for the animated indicator drawables.
 *
 * <p>Subclasses supply {@link #onCreateAnimators()} and {@link #draw(Canvas, Paint)};
 * this class owns the animator lifecycle and the draw bounds.
 */
public abstract class AbstractAnimation extends Drawable implements Animatable {

    private static final Rect ZERO_BOUNDS_RECT = new Rect();

    private ArrayList<ValueAnimator> mAnimators;
    private boolean mHasAnimators;
    private HashMap<ValueAnimator, ValueAnimator.AnimatorUpdateListener> mUpdateListeners = new HashMap<ValueAnimator, ValueAnimator.AnimatorUpdateListener>();
    private int mAlpha = 255;
    protected Rect mDrawBounds = ZERO_BOUNDS_RECT;
    private Paint mPaint = new Paint();

    public AbstractAnimation() {
        this.mPaint.setColor(-1);
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setAntiAlias(true);
    }

    public abstract void draw(Canvas canvas, Paint paint);

    public abstract ArrayList<ValueAnimator> onCreateAnimators();

    public int getColor() {
        return this.mPaint.getColor();
    }

    public void setColor(int color) {
        this.mPaint.setColor(color);
    }

    @Override
    public void setAlpha(int alpha) {
        this.mAlpha = alpha;
    }

    @Override
    public int getAlpha() {
        return this.mAlpha;
    }

    @Override
    public void draw(Canvas canvas) {
        draw(canvas, this.mPaint);
    }

    @Override
    public void start() {
        ensureAnimators();
        if (this.mAnimators == null || isStarted()) {
            return;
        }
        startAnimators();
        invalidateSelf();
    }

    private void startAnimators() {
        for (int i = 0; i < this.mAnimators.size(); i++) {
            ValueAnimator animator = this.mAnimators.get(i);
            ValueAnimator.AnimatorUpdateListener listener = this.mUpdateListeners.get(animator);
            if (listener != null) {
                animator.addUpdateListener(listener);
            }
            animator.start();
        }
    }

    private void stopAnimators() {
        ArrayList<ValueAnimator> animators = this.mAnimators;
        if (animators != null) {
            for (ValueAnimator animator : animators) {
                if (animator != null && animator.isStarted()) {
                    animator.removeAllUpdateListeners();
                    animator.end();
                }
            }
        }
    }

    private void ensureAnimators() {
        if (this.mHasAnimators) {
            return;
        }
        this.mAnimators = onCreateAnimators();
        this.mHasAnimators = true;
    }

    @Override
    public void stop() {
        stopAnimators();
    }

    private boolean isStarted() {
        Iterator<ValueAnimator> iterator = this.mAnimators.iterator();
        if (iterator.hasNext()) {
            return iterator.next().isStarted();
        }
        return false;
    }

    @Override
    public boolean isRunning() {
        Iterator<ValueAnimator> iterator = this.mAnimators.iterator();
        if (iterator.hasNext()) {
            return iterator.next().isRunning();
        }
        return false;
    }

    public void addUpdateListener(ValueAnimator animator, ValueAnimator.AnimatorUpdateListener listener) {
        this.mUpdateListeners.put(animator, listener);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        setDrawBounds(bounds);
    }

    public void setDrawBounds(Rect bounds) {
        setDrawBounds(bounds.left, bounds.top, bounds.right, bounds.bottom);
    }

    public void setDrawBounds(int left, int top, int right, int bottom) {
        this.mDrawBounds = new Rect(left, top, right, bottom);
    }

    public void postInvalidate() {
        invalidateSelf();
    }

    public Rect getDrawBounds() {
        return this.mDrawBounds;
    }

    public int getWidth() {
        return this.mDrawBounds.width();
    }

    public int getHeight() {
        return this.mDrawBounds.height();
    }

    public int centerX() {
        return this.mDrawBounds.centerX();
    }

    public int centerY() {
        return this.mDrawBounds.centerY();
    }

    public float exactCenterX() {
        return this.mDrawBounds.exactCenterX();
    }

    public float exactCenterY() {
        return this.mDrawBounds.exactCenterY();
    }

    @Override
    public int getOpacity() {
        return android.graphics.PixelFormat.OPAQUE;
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
    }
}
