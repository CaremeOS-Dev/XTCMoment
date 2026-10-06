package com.xtc.moment.module.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.xtc.log.LogUtil;

/**
 * 圆角图片控件。
 */
public class RoundCornerImageView extends AppCompatImageView {

    private static final String TAG = "RoundCornerImageView";

    private final Path path = new Path();
    private RectF rect;
    private float radius = 22.0f;

    public RoundCornerImageView(Context context) {
        super(context);
        init();
    }

    public RoundCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundCornerImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.rect == null) {
            this.rect = new RectF(0.0f, 0.0f, getWidth(), getHeight());
            float corner = this.radius;
            this.path.addRoundRect(this.rect, corner, corner, Path.Direction.CW);
            LogUtil.d(TAG, "onDraw: init path");
        }
        canvas.clipPath(this.path);
        super.onDraw(canvas);
    }

    public void setRadius(float radius) {
        LogUtil.d(TAG, "setRadius: radius = [" + radius + "]");
        this.radius = radius;
        this.path.reset();
        if (this.rect != null) {
            this.path.addRoundRect(this.rect, radius, radius, Path.Direction.CW);
        }
        invalidate();
    }
}