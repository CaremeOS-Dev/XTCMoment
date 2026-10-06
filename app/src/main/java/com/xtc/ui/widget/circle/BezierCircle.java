package com.xtc.ui.widget.circle;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

/** 由四段三次贝塞尔曲线构成的可变形圆，用于水波/呼吸动效。 */
public class BezierCircle {
    private static final float blackMagic = 0.5f;
    // 反编译常量：0xFF1F7A7E? 实际为 -14699010 == 0xFF1F8A7E
    private static final int DEFAULT_COLOR = -14699010;
    private int mHeight;
    private float mOffset;
    private HPoint mP1;
    private VPoint mP2;
    private HPoint mP3;
    private VPoint mP4;
    private float mRadius;
    private int mWidth;
    private float mAngle = 0.0f;
    private Path mPath = new Path();
    private Paint mPaint = new Paint();

    public BezierCircle() {
        this.mP2 = new VPoint();
        this.mP4 = new VPoint();
        this.mP1 = new HPoint();
        this.mP3 = new HPoint();
    }

    public void initParams(int width, int height, float radius) {
        this.mWidth = width;
        this.mHeight = height;
        this.mRadius = radius;
        this.mPaint.setColor(DEFAULT_COLOR);
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setStrokeWidth(1.0f);
        this.mPaint.setAntiAlias(true);
        this.mOffset = this.mRadius * 0.5f;
    }

    public void drawBezierCircle(Canvas canvas) {
        canvas.rotate(this.mAngle, this.mWidth / 2, this.mHeight / 2);
        this.mPath.reset();
        this.mPath.moveTo(this.mP1.x, this.mP1.y);
        this.mPath.cubicTo(this.mP1.right.x, this.mP1.right.y, this.mP2.bottom.x, this.mP2.bottom.y, this.mP2.x, this.mP2.y);
        this.mPath.cubicTo(this.mP2.top.x, this.mP2.top.y, this.mP3.right.x, this.mP3.right.y, this.mP3.x, this.mP3.y);
        this.mPath.cubicTo(this.mP3.left.x, this.mP3.left.y, this.mP4.top.x, this.mP4.top.y, this.mP4.x, this.mP4.y);
        this.mPath.cubicTo(this.mP4.bottom.x, this.mP4.bottom.y, this.mP1.left.x, this.mP1.left.y, this.mP1.x, this.mP1.y);
        canvas.drawPath(this.mPath, this.mPaint);
    }

    public void updateBezierCircle(float progress, float offsetY) {
        int width = this.mWidth;
        float centerX = (width / 2) + ((450 - (width / 2)) * progress);
        float centerY = (this.mHeight / 2) - offsetY;
        float radius = this.mRadius;
        this.mAngle = 40 * progress;
        HPoint p1 = this.mP1;
        p1.x = centerX;
        float topY = centerY + radius;
        p1.y = topY;
        p1.left.x = centerX - this.mOffset;
        this.mP1.left.y = topY;
        this.mP1.right.x = this.mOffset + centerX;
        this.mP1.right.y = topY;
        HPoint p3 = this.mP3;
        p3.x = centerX;
        float bottomY = centerY - radius;
        p3.y = bottomY;
        p3.left.x = centerX - this.mOffset;
        this.mP3.left.y = bottomY;
        p3.right.x = this.mOffset + centerX;
        this.mP3.right.y = bottomY;
        VPoint p2 = this.mP2;
        float rightX = centerX + radius;
        p2.x = rightX;
        p2.y = centerY;
        p2.top.x = rightX;
        this.mP2.top.y = centerY - this.mOffset;
        this.mP2.bottom.x = rightX;
        this.mP2.bottom.y = this.mOffset + centerY;
        VPoint p4 = this.mP4;
        float leftX = (centerX - radius) - ((radius * 1.4f) * progress);
        p4.x = leftX;
        p4.y = centerY;
        p4.top.x = leftX;
        float extraOffset = progress * 5.0f;
        this.mP4.top.y = (centerY - this.mOffset) - extraOffset;
        this.mP4.bottom.x = leftX;
        this.mP4.bottom.y = centerY + this.mOffset + extraOffset;
    }

    /** 竖直方向的贝塞尔控制点。 */
    private class VPoint {
        public PointF bottom;
        public PointF top;
        public float x;
        public float y;

        private VPoint() {
            this.top = new PointF();
            this.bottom = new PointF();
        }
    }

    /** 水平方向的贝塞尔控制点。 */
    private class HPoint {
        public PointF left;
        public PointF right;
        public float x;
        public float y;

        private HPoint() {
            this.left = new PointF();
            this.right = new PointF();
        }
    }
}