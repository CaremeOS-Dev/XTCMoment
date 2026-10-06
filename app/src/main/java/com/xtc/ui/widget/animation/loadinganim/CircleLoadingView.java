package com.xtc.ui.widget.animation.loadinganim;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/** 圆形加载动画：底圆 + 旋转的加载弧。 */
public class CircleLoadingView extends View {
    private static final int DEFAULT_RADIUS = 8;
    // 反编译常量：SupportMenu.CATEGORY_MASK == 0xFFFF0000，0x1B000000
    private static final int DEFAULT_LOADING_COLOR = -65536;
    private static final int DEFAULT_NORMAL_COLOR = 452984832;
    private float mAngle;
    private ValueAnimator mAnim;
    private float mArcSize;
    private Context mContext;
    private int mHeight;
    private int mLoadingColor;
    private int mNormalColor;
    private Paint mPaint;
    private int mRadius;
    private RectF mRectF;
    private int mWidth;

    public CircleLoadingView(Context context) {
        this(context, null);
    }

    public CircleLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        this.mAngle = 0.0f;
        this.mArcSize = 0.0f;
        this.mLoadingColor = DEFAULT_LOADING_COLOR;
        this.mNormalColor = DEFAULT_NORMAL_COLOR;
        this.mRectF = new RectF();
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStrokeWidth(6.0f);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mRadius = (int) (getResources().getDisplayMetrics().density * 8.0f);
    }

    public void setRadius(int radius) {
        this.mRadius = radius;
        invalidate();
    }

    public void setColor(int color) {
        this.mLoadingColor = color;
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.mWidth = right - left;
        this.mHeight = bottom - top;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int centerX = this.mWidth / 2;
        int centerY = this.mHeight / 2;
        canvas.save();
        this.mPaint.setColor(this.mNormalColor);
        float centerXF = centerX;
        float centerYF = centerY;
        canvas.drawCircle(centerXF, centerYF, this.mRadius, this.mPaint);
        canvas.restore();
        canvas.save();
        canvas.rotate(this.mAngle, centerXF, centerYF);
        RectF rectF = this.mRectF;
        int radius = this.mRadius;
        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        this.mPaint.setColor(this.mLoadingColor);
        canvas.drawArc(this.mRectF, -90.0f, this.mArcSize, false, this.mPaint);
        canvas.restore();
    }

    public void startAnim() {
        cancelAnim();
        this.mAnim = ValueAnimator.ofFloat(0.0f, 360.0f);
        this.mAnim.setDuration(1000L);
        this.mAnim.setInterpolator(new LinearInterpolator());
        this.mAnim.setRepeatMode(1);
        this.mAnim.setRepeatCount(-1);
        this.mAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                CircleLoadingView.this.mAngle = ((Float) animation.getAnimatedValue()).floatValue();
                CircleLoadingView.this.invalidate();
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

    public void setRotateAngle(float angle) {
        if (this.mAngle == angle) {
            return;
        }
        this.mAngle = angle;
        invalidate();
    }

    public void setArcSize(float arcSize) {
        if (this.mArcSize == arcSize) {
            return;
        }
        this.mArcSize = arcSize;
        invalidate();
    }

    public boolean isAnimRunning() {
        ValueAnimator animator = this.mAnim;
        return animator != null && animator.isRunning();
    }
}