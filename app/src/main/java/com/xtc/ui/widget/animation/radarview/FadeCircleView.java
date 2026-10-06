package com.xtc.ui.widget.animation.radarview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.xtc.ui.widget.R;

/** 雷达中心的淡出圆形（包内可见）。 */
class FadeCircleView extends View {
    private int centerX;
    private int centerY;
    private Context context;
    private Paint mPaint;

    private void initData() {
    }

    public FadeCircleView(Context context) {
        this(context, null);
    }

    public FadeCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FadeCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
        initData();
    }

    private void initView() {
        this.mPaint = new Paint();
        this.mPaint.setColor(Color.parseColor("#ffffffff"));
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        this.centerX = width / 2;
        this.centerY = height / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(this.centerX, this.centerY, getResources().getDimension(R.dimen.dp_33), this.mPaint);
    }
}