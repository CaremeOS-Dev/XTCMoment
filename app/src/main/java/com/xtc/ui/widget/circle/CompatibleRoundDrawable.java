package com.xtc.ui.widget.circle;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import java.util.Arrays;

/**
 * Rounded rectangle drawable that also supports an optional border, padding and
 * a plain circle mode; works on API levels whose {@code GradientDrawable} lacks
 * the needed corner handling.
 */
public class CompatibleRoundDrawable extends Drawable {

    private int mAlpha;
    private int mBorderColor;
    private Path mBorderPath;
    final float[] mBorderRadii;
    private float mBorderWidth;
    private int mColor;
    private boolean mIsCircle;
    private float mPadding;
    final Paint mPaint;
    final Path mPath;
    private final float[] mRadii;
    private final RectF mTempRect;

    public CompatibleRoundDrawable(int color) {
        this.mRadii = new float[8];
        this.mBorderRadii = new float[8];
        this.mPaint = new Paint(1);
        this.mIsCircle = false;
        this.mBorderWidth = 0.0f;
        this.mPadding = 0.0f;
        this.mBorderColor = 0;
        this.mPath = new Path();
        this.mBorderPath = new Path();
        this.mColor = 0;
        this.mTempRect = new RectF();
        this.mAlpha = 255;
        setColor(color);
    }

    public CompatibleRoundDrawable(float[] radii, int color) {
        this(color);
        setRadii(radii);
    }

    public CompatibleRoundDrawable(float radius, int color) {
        this(color);
        setRadius(radius);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        updatePath();
    }

    @Override
    public void draw(Canvas canvas) {
        this.mPaint.setColor(this.mColor);
        this.mPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(this.mPath, this.mPaint);
        if (this.mBorderWidth != 0.0f) {
            this.mPaint.setColor(this.mBorderColor);
            this.mPaint.setStyle(Paint.Style.STROKE);
            this.mPaint.setStrokeWidth(this.mBorderWidth);
            canvas.drawPath(this.mBorderPath, this.mPaint);
        }
    }

    public void setCircle(boolean isCircle) {
        this.mIsCircle = isCircle;
        updatePath();
        invalidateSelf();
    }

    public boolean getCircle() {
        return this.mIsCircle;
    }

    public void setRadii(float[] radii) {
        if (radii == null) {
            Arrays.fill(this.mRadii, 0.0f);
        } else {
            System.arraycopy(processRadiiArray(radii), 0, this.mRadii, 0, 8);
        }
        updatePath();
        invalidateSelf();
    }

    public float[] processRadiiArray(float[] radii) {
        if (radii.length == 8) {
            return radii;
        }
        float[] result = new float[8];
        System.arraycopy(radii, 0, result, 0, Math.min(radii.length, 8));
        return result;
    }

    public float[] getRadii() {
        return this.mRadii;
    }

    public void setRadius(float radius) {
        if (radius > 0.0f) {
            Arrays.fill(this.mRadii, radius);
            updatePath();
            invalidateSelf();
        }
    }

    public void setColor(int color) {
        if (this.mColor != color) {
            this.mColor = color;
            invalidateSelf();
        }
    }

    public int getColor() {
        return this.mColor;
    }

    public void setBorder(int color, int width) {
        if (this.mBorderColor != color) {
            this.mBorderColor = color;
            invalidateSelf();
        }
        float f = width;
        if (this.mBorderWidth != f) {
            this.mBorderWidth = f;
            updatePath();
            invalidateSelf();
        }
    }

    public int getBorderColor() {
        return this.mBorderColor;
    }

    public float getBorderWidth() {
        return this.mBorderWidth;
    }

    public void setPadding(float padding) {
        if (this.mPadding != padding) {
            this.mPadding = padding;
            updatePath();
            invalidateSelf();
        }
    }

    public float getPadding() {
        return this.mPadding;
    }

    @Override
    public void setAlpha(int alpha) {
        if (this.mAlpha != alpha) {
            this.mAlpha = alpha;
            updatePath();
            invalidateSelf();
        }
    }

    @Override
    public int getAlpha() {
        return this.mAlpha;
    }

    /** Rebuilds the fill and border paths from the current bounds. */
    public void updatePath() {
        this.mPath.reset();
        this.mBorderPath.reset();
        this.mTempRect.set(getBounds());
        this.mTempRect.inset(this.mBorderWidth / 2.0f, this.mBorderWidth / 2.0f);
        if (this.mIsCircle) {
            this.mBorderPath.addCircle(this.mTempRect.centerX(), this.mTempRect.centerY(),
                    Math.min(this.mTempRect.width(), this.mTempRect.height()) / 2.0f, Path.Direction.CW);
        } else {
            for (int i = 0; i < this.mBorderRadii.length; i++) {
                float radius = (this.mRadii[i] + this.mPadding) - (this.mBorderWidth / 2.0f);
                this.mBorderRadii[i] = radius < 0.0f ? 0.0f : radius;
            }
            this.mBorderPath.addRoundRect(this.mTempRect, this.mBorderRadii, Path.Direction.CW);
        }
        this.mTempRect.inset((-this.mBorderWidth) / 2.0f, this.mBorderWidth / 2.0f);
        this.mTempRect.inset(this.mPadding, this.mPadding);
        if (this.mIsCircle) {
            this.mPath.addCircle(this.mTempRect.centerX(), this.mTempRect.centerY(),
                    Math.min(this.mTempRect.width(), this.mTempRect.height()) / 2.0f, Path.Direction.CW);
        } else {
            this.mPath.addRoundRect(this.mTempRect, this.mRadii, Path.Direction.CW);
        }
        this.mTempRect.inset(-this.mPadding, -this.mPadding);
    }

    @Override
    public int getOpacity() {
        return android.graphics.PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
    }
}
