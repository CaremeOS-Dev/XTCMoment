package com.xtc.ui.widget.textview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

/** 绘制时绕中心旋转 45 度的 TextView（用于角标）。 */
public class RotateTextView extends TextView {

    private static final float ROTATE_DEGREE = 45.0f;

    public RotateTextView(Context context) {
        this(context, null);
    }

    public RotateTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotateTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.rotate(ROTATE_DEGREE, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
        super.onDraw(canvas);
    }
}