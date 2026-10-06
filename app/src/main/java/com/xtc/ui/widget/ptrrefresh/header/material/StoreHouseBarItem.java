package com.xtc.ui.widget.ptrrefresh.header.material;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import java.util.Random;

/** StoreHouse 头部动画中的单根竖线。 */
public class StoreHouseBarItem extends Animation {
    public int index;
    private PointF mCEndPoint;
    private PointF mCStartPoint;
    public PointF midPoint;
    public float translationX;
    private final Paint mPaint = new Paint();
    private float mFromAlpha = 1.0f;
    private float mToAlpha = 0.4f;

    public StoreHouseBarItem(int index, PointF startPoint, PointF endPoint, int color, int lineWidth) {
        this.index = index;
        this.midPoint = new PointF((startPoint.x + endPoint.x) / 2.0f, (startPoint.y + endPoint.y) / 2.0f);
        this.mCStartPoint = new PointF(startPoint.x - this.midPoint.x, startPoint.y - this.midPoint.y);
        this.mCEndPoint = new PointF(endPoint.x - this.midPoint.x, endPoint.y - this.midPoint.y);
        setColor(color);
        setLineWidth(lineWidth);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStyle(Paint.Style.STROKE);
    }

    public void setLineWidth(int lineWidth) {
        this.mPaint.setStrokeWidth(lineWidth);
    }

    public void setColor(int color) {
        this.mPaint.setColor(color);
    }

    public void resetPosition(int maxOffset) {
        this.translationX = (-new Random().nextInt(maxOffset)) + maxOffset;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        float fromAlpha = this.mFromAlpha;
        setAlpha(fromAlpha + ((this.mToAlpha - fromAlpha) * interpolatedTime));
    }

    public void start(float fromAlpha, float toAlpha) {
        this.mFromAlpha = fromAlpha;
        this.mToAlpha = toAlpha;
        super.start();
    }

    public void setAlpha(float alpha) {
        this.mPaint.setAlpha((int) (alpha * 255.0f));
    }

    public void draw(Canvas canvas) {
        canvas.drawLine(this.mCStartPoint.x, this.mCStartPoint.y, this.mCEndPoint.x, this.mCEndPoint.y, this.mPaint);
    }
}