package com.xtc.ui.widget.animation.dynamicwave;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;

/** 动态波浪背景：用二次贝塞尔曲线绘制滚动的水波。 */
public class DynamicWave extends View {
    // 反编译常量：0x33000000
    private static final int DEFAULT_WAVE_COLOR = 855638015;
    private ValueAnimator mAnim;
    private DrawFilter mDrawFilter;
    private int mHeight;
    private float mOffset;
    private Path mPath;
    private int mWaveColor;
    private Paint mWavePaint;
    private float mWaveWidth;
    private int mWidth;
    private ViewTreeObserver.OnPreDrawListener onPreDrawListener;

    public DynamicWave(Context context) {
        super(context);
        this.onPreDrawListener = null;
        init();
    }

    public DynamicWave(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.onPreDrawListener = null;
        init();
    }

    public DynamicWave(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.onPreDrawListener = null;
        init();
    }

    private void init() {
        this.mOffset = 0.0f;
        this.mWaveColor = DEFAULT_WAVE_COLOR;
        this.mPath = new Path();
        this.mWavePaint = new Paint();
        this.mWavePaint.setAntiAlias(true);
        this.mWavePaint.setStyle(Paint.Style.FILL);
        this.mWavePaint.setColor(this.mWaveColor);
        this.mDrawFilter = new PaintFlagsDrawFilter(0, 3);
        this.onPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                DynamicWave.this.getViewTreeObserver().removeOnPreDrawListener(this);
                DynamicWave.this.startAnim();
                return false;
            }
        };
        getViewTreeObserver().addOnPreDrawListener(this.onPreDrawListener);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (this.onPreDrawListener != null) {
            getViewTreeObserver().removeOnPreDrawListener(this.onPreDrawListener);
            this.onPreDrawListener = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(this.mDrawFilter);
        canvas.save();
        canvas.translate(this.mOffset, 0.0f);
        canvas.drawPath(this.mPath, this.mWavePaint);
        canvas.restore();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.mWidth = right - left;
        this.mHeight = bottom - top;
        this.mWaveWidth = this.mWidth;
        float height = this.mHeight;
        this.mOffset = (this.mWaveWidth * (-4.0f)) / 5.0f;
        this.mPath.reset();
        this.mPath.moveTo(0.0f, this.mHeight);
        Path path = this.mPath;
        float waveWidth = this.mWaveWidth;
        path.rQuadTo(waveWidth / 2.0f, (height * (-4.0f)) / 5.0f, waveWidth, 0.0f);
        this.mPath.close();
    }

    public void startAnim() {
        cancelAnim();
        this.mAnim = ValueAnimator.ofFloat(-this.mWaveWidth, this.mWidth);
        this.mAnim.setDuration(7000);
        this.mAnim.setRepeatCount(-1);
        this.mAnim.setInterpolator(new LinearInterpolator());
        this.mAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                DynamicWave.this.mOffset = ((Float) animation.getAnimatedValue()).floatValue();
                DynamicWave.this.invalidate();
            }
        });
        this.mAnim.start();
    }

    public void cancelAnim() {
        ValueAnimator animator = this.mAnim;
        if (animator != null) {
            animator.cancel();
            this.mAnim = null;
        }
    }
}