package com.xtc.ui.widget.circle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/** 简单实心圆视图。 */
public class CircleView extends View {
    private int color;
    private Paint mPaint;
    private int size;

    public CircleView(Context context) {
        super(context);
        init();
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.mPaint = new Paint();
        this.mPaint.setColor(this.color);
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
        invalidate();
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
        this.mPaint.setColor(color);
        this.mPaint.setAntiAlias(true);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getWidth() <= 0 || getHeight() <= 0) {
            return;
        }
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, this.size / 2, this.mPaint);
    }
}