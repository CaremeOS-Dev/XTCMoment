package com.xtc.ui.widget.animation.progressview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import com.xtc.ui.widget.R;

/** 圆环进度条：先画背景整圆，再按进度画起始角度可控的圆弧。 */
public class RoundProgressBar extends BaseProgressBar {
    private static final int DEFAULT_START_ANGLE = 0;
    private int mMaxPaintWidth;
    private int mRadius;
    protected int mStartAngle;

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mRadius = 15;
        this.mStartAngle = 0;
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);
        this.mRadius = (int) attributes.getDimension(R.styleable.RoundProgressBar_round_progress_radius, this.mRadius);
        this.mStartAngle = attributes.getInteger(R.styleable.RoundProgressBar_progress_start_angle, this.mStartAngle);
        attributes.recycle();
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setDither(true);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.mMaxPaintWidth = Math.max(this.mReachedProgressBarHeight, this.mUnReachedProgressBarHeight);
        int desiredSize = (this.mRadius * 2) + this.mMaxPaintWidth + getPaddingLeft() + getPaddingRight();
        int measuredSize = Math.min(resolveSize(desiredSize, widthMeasureSpec), resolveSize(desiredSize, heightMeasureSpec));
        this.mRadius = (((measuredSize - getPaddingLeft()) - getPaddingRight()) - this.mMaxPaintWidth) / 2;
        setMeasuredDimension(measuredSize, measuredSize);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        String progressText = getProgress() + "%";
        float textWidth = this.mPaint.measureText(progressText);
        float textCenterY = (this.mPaint.descent() + this.mPaint.ascent()) / 2.0f;
        canvas.save();
        canvas.translate(getPaddingLeft() + (this.mMaxPaintWidth / 2), getPaddingTop() + (this.mMaxPaintWidth / 2));
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setColor(this.mUnReachedBarColor);
        this.mPaint.setStrokeWidth(this.mUnReachedProgressBarHeight);
        canvas.drawCircle(this.mRadius, this.mRadius, this.mRadius, this.mPaint);
        this.mPaint.setColor(this.mReachedBarColor);
        this.mPaint.setStrokeWidth(this.mReachedProgressBarHeight);
        canvas.drawArc(new RectF(0.0f, 0.0f, this.mRadius * 2, this.mRadius * 2), this.mStartAngle,
                ((getProgress() * 1.0f) / getMax()) * 360.0f, false, this.mPaint);
        this.mPaint.setStyle(Paint.Style.FILL);
        if (this.mIfDrawText) {
            canvas.drawText(progressText, this.mRadius - (textWidth / 2.0f), this.mRadius - textCenterY, this.mPaint);
        }
        canvas.restore();
    }
}