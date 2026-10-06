package com.xtc.ui.widget.ptrrefresh.header.material;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/** 刷新成功圆环 + 对勾绘制视图，支持加载旋转与成功动画。 */
public class SuccCircleView extends View {
    private static final float HOOK_LEFT_LENGTH = 5.0f;
    private static final float HOOK_OFFSET_X = 1.25f;
    private static final float HOOK_OFFSET_Y = 3.0f;
    private static final float HOOK_RIGHT_LENGTH = 8.0f;
    private static final int INIT_ARC_SIZE = 60;
    private static final int MAX_RADIUS = 10;
    private static final float MAX_STROKE_WIDTH = 2.0f;
    private static final int MIN_RADIUS = 9;
    private static final float MIN_STROKE_WIDTH = 1.5f;
    // 反编译常量：0x33000000 与 0xFFFFFFFF
    private static final int DEFAULT_NORMAL_COLOR = 855638015;
    private static final int DEFAULT_REFRESH_COLOR = -1;
    private float mArcSize;
    private float mDensity;
    private int mHeight;
    private float mHookLeftLen;
    private float mHookOffsetX;
    private float mHookOffsetY;
    private Path mHookPath;
    private float mHookRightLen;
    private boolean mIsNeedDrawArc;
    private boolean mIsNeedDrawHook;
    private ValueAnimator mLoadingAnim;
    private int mNormalColor;
    private Paint mPaint;
    private float mRadius;
    private RectF mRectF;
    private int mRefreshColor;
    private float mRotateAngle;
    private float mStrokeWidth;
    private int mWidth;

    public SuccCircleView(Context context) {
        super(context);
        initView(context);
    }

    public SuccCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SuccCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.mArcSize = 0.0f;
        this.mRotateAngle = 0.0f;
        this.mNormalColor = DEFAULT_NORMAL_COLOR;
        this.mRefreshColor = DEFAULT_REFRESH_COLOR;
        this.mRectF = new RectF();
        this.mPaint = new Paint();
        this.mHookPath = new Path();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStrokeWidth(this.mStrokeWidth);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mDensity = getResources().getDisplayMetrics().density;
        float density = this.mDensity;
        this.mRadius = 10.0f * density;
        this.mStrokeWidth = 2.0f * density;
        this.mHookOffsetX = HOOK_OFFSET_X * density;
        this.mHookOffsetY = HOOK_OFFSET_Y * density;
        this.mHookLeftLen = HOOK_LEFT_LENGTH * density;
        this.mHookRightLen = density * HOOK_RIGHT_LENGTH;
    }

    public void resetCircle() {
        cancelAnim();
        this.mIsNeedDrawHook = false;
        this.mIsNeedDrawArc = false;
        float density = this.mDensity;
        this.mRadius = 10.0f * density;
        this.mStrokeWidth = density * 2.0f;
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
        this.mPaint.setStrokeWidth(this.mStrokeWidth);
        float centerXF = centerX;
        float centerYF = centerY;
        canvas.drawCircle(centerXF, centerYF, this.mRadius, this.mPaint);
        canvas.restore();
        if (this.mIsNeedDrawArc) {
            canvas.save();
            canvas.rotate(this.mRotateAngle, centerXF, centerYF);
            this.mPaint.setColor(this.mRefreshColor);
            RectF rectF = this.mRectF;
            float radius = this.mRadius;
            rectF.set(centerXF - radius, centerYF - radius, centerXF + radius, centerYF + radius);
            canvas.drawArc(this.mRectF, -90.0f, this.mArcSize, false, this.mPaint);
            canvas.restore();
        }
        if (this.mIsNeedDrawHook) {
            canvas.save();
            this.mPaint.setColor(this.mRefreshColor);
            canvas.translate(-this.mHookOffsetX, this.mHookOffsetY);
            canvas.drawPath(this.mHookPath, this.mPaint);
            canvas.restore();
        }
    }

    public void startLoadingAnim() {
        cancelAnim();
        this.mArcSize = 60.0f;
        this.mLoadingAnim = ValueAnimator.ofFloat(0.0f, 360.0f);
        this.mLoadingAnim.setDuration(1000L);
        this.mLoadingAnim.setInterpolator(new LinearInterpolator());
        this.mLoadingAnim.setRepeatMode(1);
        this.mLoadingAnim.setRepeatCount(-1);
        this.mLoadingAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                SuccCircleView.this.mRotateAngle = ((Float) animation.getAnimatedValue()).floatValue();
                SuccCircleView.this.invalidate();
            }
        });
        this.mLoadingAnim.start();
    }

    public void cancelAnim() {
        ValueAnimator animator = this.mLoadingAnim;
        if (animator != null) {
            animator.cancel();
            this.mLoadingAnim = null;
        }
    }

    public ValueAnimator createCircleAnim() {
        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setDuration(320);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = ((Float) animation.getAnimatedValue()).floatValue();
                SuccCircleView.this.mRadius = (SuccCircleView.this.mDensity * 10.0f) - ((1.0f * fraction) * SuccCircleView.this.mDensity);
                SuccCircleView.this.mStrokeWidth = (SuccCircleView.this.mDensity * 2.0f) - ((0.5f * fraction) * SuccCircleView.this.mDensity);
                SuccCircleView.this.mArcSize = (fraction * 300.0f) + 60.0f;
                SuccCircleView.this.invalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                SuccCircleView.this.mIsNeedDrawArc = true;
            }
        });
        return animator;
    }

    public ValueAnimator createHookAnim() {
        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setDuration(200);
        animator.setStartDelay(280);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                SuccCircleView.this.updateHookPath(((Float) animation.getAnimatedValue()).floatValue());
                SuccCircleView.this.invalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                SuccCircleView.this.mIsNeedDrawHook = true;
            }
        });
        return animator;
    }

    private void updateHookPath(float fraction) {
        float sin = (float) Math.sin(0.7853981633974483d);
        float cos = (float) Math.cos(0.7853981633974483d);
        float centerX = this.mWidth / 2.0f;
        float centerY = this.mHeight / 2.0f;
        float leftLength = this.mHookLeftLen;
        float startX = centerX - (cos * leftLength);
        float startY = centerY - (sin * leftLength);
        float totalLength = (leftLength + this.mHookRightLen) * fraction;
        this.mHookPath.reset();
        this.mHookPath.moveTo(startX, startY);
        if (totalLength <= leftLength) {
            this.mHookPath.lineTo(startX + (cos * totalLength), startY + (sin * totalLength));
        } else {
            this.mHookPath.lineTo(centerX, centerY);
            this.mHookPath.lineTo((cos * (totalLength - leftLength)) + centerX, centerY - (sin * (totalLength - leftLength)));
        }
    }

    public void setIsNeedDrawHook(boolean needDrawHook) {
        this.mIsNeedDrawHook = needDrawHook;
    }

    public void setIsNeedDrawArc(boolean needDrawArc) {
        this.mIsNeedDrawArc = needDrawArc;
    }

    public void setRadius(int radius) {
        this.mRadius = radius;
        invalidate();
    }

    public void setRefreshColor(int refreshColor) {
        this.mRefreshColor = refreshColor;
        invalidate();
    }

    public void setNormalColor(int normalColor) {
        this.mNormalColor = normalColor;
        invalidate();
    }

    public void setArcSize(float arcSize) {
        if (this.mArcSize == arcSize) {
            return;
        }
        this.mArcSize = arcSize;
        invalidate();
    }
}