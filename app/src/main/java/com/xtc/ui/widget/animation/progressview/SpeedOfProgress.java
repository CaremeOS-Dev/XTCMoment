package com.xtc.ui.widget.animation.progressview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import com.xtc.moment.R;

/** 速度环形进度控件：背景环 + 渐变前景环，进度变化时带动画过渡。 */
public class SpeedOfProgress extends View {
    // 反编译常量：InputDeviceCompat.SOURCE_ANY == 0xFFFF0000，SupportMenu.CATEGORY_MASK == 0xFFFF0000
    private static final int COLOR_BG_DEFAULT = -7829368; // 0xFF888888
    private float centerX;
    private float centerY;
    private Context context;
    private long mAnimTime;
    private ValueAnimator mAnimator;
    private int mArcColors;
    private Paint mArcPaint;
    private float mArcWidth;
    private int mBgArcColor;
    private Paint mBgArcPaint;
    private float mBgArcWidth;
    private Point mCenterPoint;
    private int[] mColors;
    private int[] mGradientColors;
    private float mMaxValue;
    private Paint mPaint;
    private float mPercent;
    private float mRadius;
    private RectF mRectF;
    private float mStartAngle;
    private float mSweepAngle;
    private SweepGradient mSweepGradient;
    private float radius;
    private float ringWidth;
    private float start;

    public SpeedOfProgress(Context context) {
        this(context, null);
    }

    public SpeedOfProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpeedOfProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.start = 270.0f;
        this.context = context;
        initAttar(attrs);
        initData();
        initView();
    }

    private void initAttar(AttributeSet attrs) {
        TypedArray attributes = this.context.obtainStyledAttributes(attrs, R.styleable.speedOfProgress);
        attributes.getDrawable(R.styleable.speedOfProgress_mBackground);
        this.mMaxValue = attributes.getFloat(R.styleable.speedOfProgress_maxValue, 100.0f);
        this.mStartAngle = attributes.getFloat(R.styleable.speedOfProgress_startAngle, 270.0f);
        this.mSweepAngle = attributes.getFloat(R.styleable.speedOfProgress_sweepAngle, 360.0f);
        this.mArcWidth = attributes.getDimension(R.styleable.speedOfProgress_arcWidth, getResources().getDimension(R.dimen.dp_5));
        this.mBgArcColor = attributes.getColor(R.styleable.speedOfProgress_bgArcColor, COLOR_BG_DEFAULT);
        this.mBgArcWidth = attributes.getDimension(R.styleable.speedOfProgress_bgArcWidth, getResources().getDimension(R.dimen.dp_5));
        this.mArcColors = attributes.getColor(R.styleable.speedOfProgress_arcColors, COLOR_BG_DEFAULT);
        this.mAnimTime = attributes.getInt(R.styleable.speedOfProgress_animTime, 500);
    }

    private void initData() {
        this.mRectF = new RectF();
        this.mCenterPoint = new Point();
        this.mGradientColors = new int[]{-16711936, -65536, -65536, -16711936};
    }

    private void initView() {
        this.mArcPaint = new Paint();
        this.mArcPaint.setAntiAlias(true);
        this.mArcPaint.setStyle(Paint.Style.STROKE);
        this.mArcPaint.setStrokeWidth(this.mArcWidth);
        this.mArcPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mArcPaint.setColor(this.mArcColors);
        this.mBgArcPaint = new Paint();
        this.mBgArcPaint.setAntiAlias(true);
        this.mBgArcPaint.setColor(this.mBgArcColor);
        this.mBgArcPaint.setStyle(Paint.Style.STROKE);
        this.mBgArcPaint.setStrokeWidth(this.mBgArcWidth);
        this.mBgArcPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setValue(float value, long animTime) {
        float maxValue = this.mMaxValue;
        if (value > maxValue) {
            value = maxValue;
        }
        float lastPercent = this.mPercent;
        float targetPercent = value / this.mMaxValue;
        this.mAnimTime = animTime;
        startAnimator(lastPercent, targetPercent, this.mAnimTime);
    }

    public void setGradientColors(int[] gradientColors) {
        this.mGradientColors = gradientColors;
        updateArcPaint();
    }

    private void startAnimator(float startPercent, float endPercent, long animTime) {
        this.mAnimator = ValueAnimator.ofFloat(startPercent, endPercent);
        this.mAnimator.setDuration(animTime);
        this.mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                SpeedOfProgress.this.mPercent = ((Float) animation.getAnimatedValue()).floatValue();
                SpeedOfProgress.this.invalidate();
            }
        });
        this.mAnimator.start();
    }

    private void drawArc(Canvas canvas) {
        canvas.save();
        float sweep = this.mSweepAngle * this.mPercent;
        canvas.rotate(this.mStartAngle, this.mCenterPoint.x, this.mCenterPoint.y);
        canvas.drawArc(this.mRectF, sweep, (this.mSweepAngle - sweep) + 2.0f, false, this.mBgArcPaint);
        canvas.drawArc(this.mRectF, 2.0f, sweep, false, this.mArcPaint);
        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        float maxWidth = Math.max(this.mArcWidth, this.mBgArcWidth);
        int widthOffset = ((int) maxWidth) * 2;
        this.mRadius = Math.min(((width - getPaddingLeft()) - getPaddingRight()) - widthOffset,
                ((height - getPaddingTop()) - getPaddingBottom()) - widthOffset) / 2;
        Point centerPoint = this.mCenterPoint;
        centerPoint.x = width / 2;
        centerPoint.y = height / 2;
        float halfWidth = maxWidth / 2.0f;
        this.mRectF.left = (centerPoint.x - this.mRadius) - halfWidth;
        this.mRectF.top = (this.mCenterPoint.y - this.mRadius) - halfWidth;
        this.mRectF.right = this.mCenterPoint.x + this.mRadius + halfWidth;
        this.mRectF.bottom = this.mCenterPoint.y + this.mRadius + halfWidth;
    }

    private void updateArcPaint() {
        this.mSweepGradient = new SweepGradient(this.mCenterPoint.x, this.mCenterPoint.y, this.mGradientColors, (float[]) null);
        this.mArcPaint.setShader(this.mSweepGradient);
    }

    public float getMaxValue() {
        return this.mMaxValue;
    }

    public void setMaxValue(float maxValue) {
        this.mMaxValue = maxValue;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawArc(canvas);
    }
}