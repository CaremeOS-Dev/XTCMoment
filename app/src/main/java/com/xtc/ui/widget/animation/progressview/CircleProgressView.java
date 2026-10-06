package com.xtc.ui.widget.animation.progressview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import com.xtc.log.LogUtil;

/** 简单圆形进度控件：固定尺寸圆环 + 蓝色进度弧，主要用于测试/占位展示。 */
public class CircleProgressView extends View {
    private static final String TAG = "CircleProgressBar";
    // com.badlogic.gdx.Input.Keys.bC == 144，bR == 248（反编译常量，保持原值）
    private static final int ARC_RIGHT_BOTTOM = 144;
    private static final int ARC_COLOR_RED = 248;
    private final int mCircleLineStrokeWidth;
    private final Context mContext;
    private int mMaxProgress;
    private final Paint mPaint;
    private int mProgress;
    private final RectF mRectF;

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mMaxProgress = 100;
        this.mProgress = 30;
        this.mCircleLineStrokeWidth = 8;
        this.mContext = context;
        this.mRectF = new RectF();
        this.mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(Color.rgb(233, 233, 233));
        canvas.drawColor(0);
        this.mPaint.setStrokeWidth(8.0f);
        this.mPaint.setStyle(Paint.Style.STROKE);
        RectF arcBounds = this.mRectF;
        arcBounds.left = 4.0f;
        arcBounds.top = 4.0f;
        arcBounds.right = ARC_RIGHT_BOTTOM;
        arcBounds.bottom = ARC_RIGHT_BOTTOM;
        LogUtil.d("test", "mRectF--->>" + this.mRectF.left + "," + this.mRectF.top + "," + this.mRectF.right + "," + this.mRectF.bottom + ",148,148");
        canvas.drawArc(this.mRectF, -90.0f, 360.0f, false, this.mPaint);
        this.mPaint.setColor(Color.rgb(ARC_COLOR_RED, 96, 48));
        canvas.drawArc(this.mRectF, -90.0f, (((float) this.mProgress) / ((float) this.mMaxProgress)) * 360.0f, false, this.mPaint);
    }

    public int getMaxProgress() {
        return this.mMaxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.mMaxProgress = maxProgress;
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
        invalidate();
    }

    public void setProgressNotInUiThread(int progress) {
        this.mProgress = progress;
        postInvalidate();
    }
}