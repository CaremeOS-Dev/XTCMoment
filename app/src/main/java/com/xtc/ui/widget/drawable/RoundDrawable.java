package com.xtc.ui.widget.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import com.xtc.log.LogUtil;

/** Solid circle drawable. */
public class RoundDrawable extends Drawable {

    private static final String TAG = "RoundDrawable";

    private Paint mCirclePaint = new Paint();
    private float cx;
    private float cy;
    private float radius;
    private int size;

    public RoundDrawable(int color, int radius) {
        this.mCirclePaint.setAntiAlias(true);
        this.mCirclePaint.setColor(color);
        this.size = radius * 2;
        float f = radius;
        this.cx = f;
        this.cy = f;
        this.radius = f;
        LogUtil.d(TAG, "radius = " + radius);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(this.cx, this.cy, this.radius, this.mCirclePaint);
    }

    @Override
    public void setAlpha(int alpha) {
        this.mCirclePaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        this.mCirclePaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return android.graphics.PixelFormat.TRANSLUCENT;
    }
}
