package com.xtc.ui.widget.drawable;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/**
 * Rounded rectangle drawable backed either by a solid colour or a bitmap
 * shader.
 */
public class RoundRectDrawable extends Drawable {

    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private Paint mPaint = new Paint();
    private float mRadius;
    private RectF mRect;

    public RoundRectDrawable(Bitmap bitmap) {
        this.mBitmap = bitmap;
        this.mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setShader(this.mBitmapShader);
    }

    public RoundRectDrawable(Bitmap bitmap, float radius) {
        this.mBitmap = bitmap;
        this.mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        this.mRadius = radius;
        this.mPaint.setAntiAlias(true);
        this.mPaint.setShader(this.mBitmapShader);
    }

    public RoundRectDrawable(int color, int radius) {
        this.mPaint.setColor(color);
        this.mPaint.setAntiAlias(true);
        this.mRadius = radius;
    }

    public void setRadius(float radius) {
        if (this.mRadius != radius) {
            this.mRadius = radius;
            invalidateSelf();
        }
    }

    public float getRadius() {
        return this.mRadius;
    }

    public void setRectParams(int left, int top, int right, int bottom) {
        setBounds(left, top, right, bottom);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        this.mRect = new RectF(left, top, right, bottom);
    }

    @Override
    public void draw(Canvas canvas) {
        RectF rect = this.mRect;
        float radius = this.mRadius;
        canvas.drawRoundRect(rect, radius, radius, this.mPaint);
    }

    @Override
    public int getIntrinsicHeight() {
        Bitmap bitmap = this.mBitmap;
        return bitmap != null ? bitmap.getHeight() : 0;
    }

    @Override
    public int getIntrinsicWidth() {
        Bitmap bitmap = this.mBitmap;
        return bitmap != null ? bitmap.getWidth() : 0;
    }

    @Override
    public void setAlpha(int alpha) {
        this.mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return android.graphics.PixelFormat.TRANSLUCENT;
    }
}
