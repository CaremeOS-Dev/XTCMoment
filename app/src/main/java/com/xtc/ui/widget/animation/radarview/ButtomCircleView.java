package com.xtc.ui.widget.animation.radarview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.xtc.httplib.netstate.SwitchNetworkManager;
import com.xtc.ui.widget.R;

/** 雷达底部同心圆背景（包内可见）。 */
class ButtomCircleView extends View {
    private String TAG;
    private int centerX;
    private int centerY;
    private String circleColor;
    private boolean isRed;
    private Paint mPaintCircle1;
    private Paint mPaintCircle2;
    private Paint mPaintCircle3;
    private Paint mPaintCircle4;
    private Matrix matrix;

    public ButtomCircleView(Context context) {
        this(context, null);
    }

    public ButtomCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ButtomCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.TAG = "ButtomCircleView";
        initData();
        initPaint();
    }

    private void initData() {
        if (this.isRed) {
            this.circleColor = "fd4243";
        } else {
            this.circleColor = "409eff";
        }
        this.matrix = new Matrix();
    }

    private void initPaint() {
        this.mPaintCircle1 = setMyPaint(Color.parseColor("#ff" + this.circleColor), getResources().getDimension(R.dimen.dp_2));
        this.mPaintCircle2 = setMyPaint(Color.parseColor("#ff" + this.circleColor), getResources().getDimension(R.dimen.dp_2));
        this.mPaintCircle3 = setMyPaint(Color.parseColor("#99" + this.circleColor), getResources().getDimension(R.dimen.dp_1));
        this.mPaintCircle4 = setMyPaint(Color.parseColor("#66" + this.circleColor), getResources().getDimension(R.dimen.dp_0_point_5));
    }

    private Paint setMyPaint(int color, float strokeWidth) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokeWidth);
        return paint;
    }

    public void setCircleColor(boolean isRed) {
        Log.i(this.TAG, "setCircleColor");
        this.isRed = isRed;
        initData();
        initPaint();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        this.centerX = width / 2;
        this.centerY = height / 2;
        Log.i(this.TAG, "onSizeChanged=" + width + SwitchNetworkManager.SEP_SP + height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(this.centerX, this.centerY, getResources().getDimension(R.dimen.dp_34), this.mPaintCircle1);
        canvas.drawCircle(this.centerX, this.centerY, getResources().getDimension(R.dimen.dp_76_point_5), this.mPaintCircle2);
        canvas.drawCircle(this.centerX, this.centerY, getResources().getDimension(R.dimen.dp_120), this.mPaintCircle3);
        canvas.drawCircle(this.centerX, this.centerY, getResources().getDimension(R.dimen.dp_162), this.mPaintCircle4);
    }
}