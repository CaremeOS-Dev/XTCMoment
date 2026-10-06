package com.xtc.ui.widget.animation.sprite;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;

/** 带画笔与基础色的形状精灵，负责把 Sprite 的透明度换算到实际绘制颜色。 */
public abstract class ShapeSprite extends Sprite {
    private static final String TAG = "ShapeSprite";
    private int mBaseColor;
    private Paint mPaint;
    private int mUseColor;

    public abstract void drawShape(Canvas canvas, Paint paint);

    public ShapeSprite(Context context) {
        super(context);
        setColor(-1);
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(this.mUseColor);
    }

    @Override
    public void setColor(int color) {
        this.mBaseColor = color;
        updateUseColor();
    }

    @Override
    public int getColor() {
        return this.mBaseColor;
    }

    public int getUseColor() {
        return this.mUseColor;
    }

    @Override
    public void setAlpha(int alpha) {
        super.setAlpha(alpha);
        updateUseColor();
    }

    private void updateUseColor() {
        int alpha = getAlpha();
        int baseColor = this.mBaseColor;
        this.mUseColor = ((((baseColor >>> 24) * (alpha + (alpha >> 7))) >> 8) << 24) | ((baseColor << 8) >>> 8);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
    }

    @Override
    protected final void drawSelf(Canvas canvas) {
        this.mPaint.setColor(this.mUseColor);
        drawShape(canvas, this.mPaint);
    }
}