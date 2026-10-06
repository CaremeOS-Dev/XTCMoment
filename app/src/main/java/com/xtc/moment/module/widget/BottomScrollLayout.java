package com.xtc.moment.module.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * 带边缘绘制效果的底部滚动容器。
 */
public class BottomScrollLayout extends RelativeLayout {

    private final EdgePainter edgePainter;

    public BottomScrollLayout(Context context) {
        this(context, null);
    }

    public BottomScrollLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomScrollLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        setBackgroundColor(0);
        this.edgePainter = new EdgePainter(context, this);
    }

    public EdgePainter getEdgePainter() {
        return this.edgePainter;
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.edgePainter.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.edgePainter.interceptTouchEvent(event)) {
            return true;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.edgePainter.onDraw(canvas);
    }
}