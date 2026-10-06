package com.xtc.ui.widget.animation.progressview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/** 水平进度条：一条横线上叠加进度文字，并根据剩余空间决定是否绘制未完成段。 */
public class HorizontalProgressBar extends BaseProgressBar {
    public HorizontalProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), measureHeight(heightMeasureSpec));
        this.mRealWidth = (getMeasuredWidth() - getPaddingRight()) - getPaddingLeft();
    }

    private int measureHeight(int measureSpec) {
        int mode = View.MeasureSpec.getMode(measureSpec);
        int size = View.MeasureSpec.getSize(measureSpec);
        if (mode == 1073741824) {
            return size;
        }
        int desiredHeight = (int) (getPaddingTop() + getPaddingBottom()
                + Math.max(Math.max(this.mReachedProgressBarHeight, this.mUnReachedProgressBarHeight),
                Math.abs(this.mPaint.descent() - this.mPaint.ascent())));
        return mode == Integer.MIN_VALUE ? Math.min(desiredHeight, size) : desiredHeight;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft(), getHeight() / 2);
        float progressX = (int) (this.mRealWidth * ((getProgress() * 1.0f) / getMax()));
        String progressText = getProgress() + "%";
        float textWidth = this.mPaint.measureText(progressText);
        float textCenterY = (this.mPaint.descent() + this.mPaint.ascent()) / 2.0f;
        boolean progressFits = true;
        if (this.mIfDrawText) {
            if (progressX + textWidth > this.mRealWidth) {
                progressX = this.mRealWidth - textWidth;
            } else {
                progressFits = false;
            }
        } else if (progressX > this.mRealWidth) {
            progressX = this.mRealWidth;
        } else {
            progressFits = false;
        }
        float reachedEndX = this.mIfDrawText ? progressX - (this.mTextOffset / 2) : progressX;
        if (reachedEndX > 0.0f) {
            this.mPaint.setColor(this.mReachedBarColor);
            this.mPaint.setStrokeWidth(this.mReachedProgressBarHeight);
            canvas.drawLine(0.0f, 0.0f, reachedEndX, 0.0f, this.mPaint);
        }
        if (this.mIfDrawText) {
            this.mPaint.setColor(this.mTextColor);
            canvas.drawText(progressText, progressX, -textCenterY, this.mPaint);
        }
        if (!progressFits && this.mIfDrawText) {
            float unreachedStartX = progressX + (this.mTextOffset / 2) + textWidth;
            this.mPaint.setColor(this.mUnReachedBarColor);
            this.mPaint.setStrokeWidth(this.mUnReachedProgressBarHeight);
            canvas.drawLine(unreachedStartX, 0.0f, this.mRealWidth, 0.0f, this.mPaint);
        } else if (!progressFits && !this.mIfDrawText) {
            this.mPaint.setColor(this.mUnReachedBarColor);
            this.mPaint.setStrokeWidth(this.mUnReachedProgressBarHeight);
            canvas.drawLine(progressX, 0.0f, this.mRealWidth, 0.0f, this.mPaint);
        }
        canvas.restore();
    }
}