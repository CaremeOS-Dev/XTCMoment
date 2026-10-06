package com.xtc.ui.widget.circle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import com.xtc.ui.widget.R;
import com.xtc.utils.ui.DimenUtil;

/** 导航点圆环视图。 */
public class NavCircleView extends View {
    private Paint mCirclePaint;
    private RectF mCircleRectF;
    private int mDefaultSmallCircleColor;
    private int mDefaultSmallCircleRadius;
    private int mHeight;
    private int mWidth;

    public NavCircleView(Context context) {
        this(context, null);
    }

    public NavCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public NavCircleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initData(context);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        this.mWidth = width;
        this.mHeight = height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircle(canvas, this.mDefaultSmallCircleRadius, this.mDefaultSmallCircleColor, this.mCirclePaint);
    }

    private void initData(Context context) {
        this.mCircleRectF = new RectF();
        this.mDefaultSmallCircleRadius = DimenUtil.dp2px(context, 3.0f);
        this.mDefaultSmallCircleColor = R.color.color_80ffffff;
        this.mCirclePaint = new Paint();
        this.mCirclePaint.setAntiAlias(true);
        this.mCirclePaint.setStyle(Paint.Style.FILL);
    }

    private void drawCircle(Canvas canvas, int radius, int colorResId, Paint paint) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(colorResId));
        paint.setStrokeWidth(radius);
        int width = this.mWidth;
        int left = (width / 2) - radius;
        int height = this.mHeight;
        int top = (height / 2) - radius;
        int right = (width / 2) + radius;
        int bottom = (height / 2) + radius;
        if (left < 0) {
            left = 0;
        }
        int clampedRight = right >= 0 ? right : 0;
        int maxWidth = this.mWidth;
        if (clampedRight > maxWidth) {
            clampedRight = maxWidth;
        }
        int clampedBottom = this.mHeight;
        if (bottom <= clampedBottom) {
            clampedBottom = bottom;
        }
        this.mCircleRectF.set(left, top, clampedRight, clampedBottom);
        canvas.drawArc(this.mCircleRectF, 0.0f, 360.0f, false, paint);
    }

    public void setColorAndRadius(int colorResId, int radius) {
        this.mDefaultSmallCircleColor = colorResId;
        this.mDefaultSmallCircleRadius = radius;
        invalidate();
    }
}