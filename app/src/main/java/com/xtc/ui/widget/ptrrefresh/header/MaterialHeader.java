package com.xtc.ui.widget.ptrrefresh.header;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import com.xtc.ui.widget.ptrrefresh.header.material.MaterialProgressDrawable;
import com.xtc.ui.widget.ptrrefresh.indicator.Indicator;
import com.xtc.ui.widget.ptrrefresh.layout.BaseFrameLayout;

/** Material 风格下拉刷新头部，使用圆形进度指示器。 */
public class MaterialHeader extends View implements UIRefreshHandler {
    private BaseFrameLayout mBaseFrameLayout;
    private MaterialProgressDrawable mDrawable;
    private float mScale;
    private Animation mScaleAnimation;

    @Override
    public void onUIRefreshPrepare(BaseFrameLayout frameLayout) {
    }

    public MaterialHeader(Context context) {
        super(context);
        this.mScale = 1.0f;
        this.mScaleAnimation = new ScaleAnimationImpl();
        initView();
    }

    public MaterialHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mScale = 1.0f;
        this.mScaleAnimation = new ScaleAnimationImpl();
        initView();
    }

    public MaterialHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mScale = 1.0f;
        this.mScaleAnimation = new ScaleAnimationImpl();
        initView();
    }

    /** 刷新完成时的淡出缩放动画。 */
    private class ScaleAnimationImpl extends Animation {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation transformation) {
            MaterialHeader.this.mScale = 1.0f - interpolatedTime;
            MaterialHeader.this.mDrawable.setAlpha((int) (MaterialHeader.this.mScale * 255.0f));
            MaterialHeader.this.invalidate();
        }
    }

    public void setBaseFrameLayout(BaseFrameLayout frameLayout) {
        final UIRefreshHandlerHook hook = new UIRefreshHandlerHook() {
            @Override
            public void run() {
                MaterialHeader header = MaterialHeader.this;
                header.startAnimation(header.mScaleAnimation);
            }
        };
        this.mScaleAnimation.setDuration(200L);
        this.mScaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                hook.resume();
            }
        });
        this.mBaseFrameLayout = frameLayout;
        this.mBaseFrameLayout.setRefreshCompleteHook(hook);
    }

    private void initView() {
        this.mDrawable = new MaterialProgressDrawable(getContext(), this);
        this.mDrawable.setBackgroundColor(-1);
        this.mDrawable.setCallback(this);
    }

    @Override
    public void invalidateDrawable(Drawable drawable) {
        if (drawable == this.mDrawable) {
            invalidate();
        } else {
            super.invalidateDrawable(drawable);
        }
    }

    public void setColorSchemeColors(int[] colors) {
        this.mDrawable.setColorSchemeColors(colors);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(
                this.mDrawable.getIntrinsicHeight() + getPaddingTop() + getPaddingBottom(), 1073741824));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int intrinsicHeight = this.mDrawable.getIntrinsicHeight();
        this.mDrawable.setBounds(0, 0, intrinsicHeight, intrinsicHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int saveCount = canvas.save();
        Rect bounds = this.mDrawable.getBounds();
        canvas.translate(getPaddingLeft() + ((getMeasuredWidth() - this.mDrawable.getIntrinsicWidth()) / 2), getPaddingTop());
        float scale = this.mScale;
        canvas.scale(scale, scale, bounds.exactCenterX(), bounds.exactCenterY());
        this.mDrawable.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    @Override
    public void onUIReset(BaseFrameLayout frameLayout) {
        this.mScale = 1.0f;
        this.mDrawable.stop();
    }

    @Override
    public void onUIRefreshBegin(BaseFrameLayout frameLayout) {
        this.mDrawable.setAlpha(255);
        this.mDrawable.start();
    }

    @Override
    public void onUIRefreshComplete(BaseFrameLayout frameLayout, boolean isSuccess) {
        this.mDrawable.stop();
    }

    @Override
    public void onUIPositionChange(BaseFrameLayout frameLayout, boolean isUnderTouch, byte status, Indicator indicator) {
        float percent = Math.min(1.0f, indicator.getCurrentPercent());
        if (status == 2) {
            this.mDrawable.setAlpha((int) (255.0f * percent));
            this.mDrawable.showArrow(true);
            this.mDrawable.setStartEndTrim(0.0f, Math.min(0.8f, percent * 0.8f));
            this.mDrawable.setArrowScale(Math.min(1.0f, percent));
            this.mDrawable.setProgressRotation((((0.4f * percent) - 0.25f) + (percent * 2.0f)) * 0.5f);
            invalidate();
        }
    }
}