package com.xtc.ui.widget.animation.radarview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.RotateAnimation;
import com.xtc.httplib.netstate.SwitchNetworkManager;
import com.xtc.moment.R;
import com.xtc.ui.widget.animation.radarview.interfaces.ScannerInterface;

/** 雷达扫描扇形视图，通过定时旋转矩阵实现扫描动画（包内可见）。 */
class ScannerView extends View {
    private static final int COLOR_SCAN_ALPHA = -1;          // 0xFFFFFFFF
    private static final int COLOR_SCAN_END_RED = -65536;    // SupportMenu.CATEGORY_MASK
    private static final int COLOR_SCAN_END_BLUE = -16776961; // 0xFF0000FF
    private String TAG;
    private int centerX;
    private int centerY;
    private boolean closeThr;
    private Context context;
    public Handler handler;
    private boolean isFind;
    private boolean isRed;
    private boolean isStartAnimation;
    private boolean isStop;
    private int length;
    private Paint mPaint;
    private Matrix matrix;
    private RectF myRectF;
    private ObjectAnimator objectAnimator;
    private RotateAnimation rotateAnimation;
    public Runnable runnable;
    ScannerInterface scannerInterface;
    public double start;
    private SweepGradient sweepGradient;

    public void setRaderColor(boolean isRed) {
        Log.i(this.TAG, "setRaderColor");
        this.isRed = isRed;
    }

    public ScannerView(Context context) {
        this(context, null);
    }

    public ScannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.TAG = "ScannerView";
        this.start = 0.0d;
        this.handler = new Handler();
        this.runnable = new Runnable() {
            @Override
            public void run() {
                ScannerView.this.matrix = new Matrix();
                if (!ScannerView.this.closeThr) {
                    if (ScannerView.this.isFind && ScannerView.this.start % 360.0d != 0.0d
                            && (ScannerView.this.start % 360.0d) % 270.0d == 0.0d) {
                        ScannerView.this.isStop = true;
                        Log.i(ScannerView.this.TAG, "已转完当前圈+start=" + ScannerView.this.start);
                        ScannerView.this.isFind = false;
                    }
                    if (ScannerView.this.isStop && ScannerView.this.start % 360.0d != 0.0d
                            && (ScannerView.this.start % 360.0d) % 330.0d == 0.0d) {
                        ScannerView.this.closeThr = true;
                        ScannerView.this.scannerInterface.wasStopAni();
                        ScannerView.this.isStartAnimation = false;
                        ScannerView.this.clearAnimation();
                        Log.i(ScannerView.this.TAG, "已转完60+start=" + ScannerView.this.start);
                    }
                    ScannerView.this.start += 1.0d;
                    ScannerView.this.matrix.postRotate((int) ScannerView.this.start, ScannerView.this.centerX, ScannerView.this.centerY);
                    ScannerView.this.postInvalidate();
                    ScannerView.this.handler.postDelayed(ScannerView.this.runnable, 10L);
                    return;
                }
                ScannerView.this.handler.removeCallbacks(ScannerView.this.runnable);
                ScannerView.this.closeThr = false;
                Log.i(ScannerView.this.TAG, "已关闭");
            }
        };
        this.context = context;
        initView();
        initData();
    }

    private void initView() {
        this.isStartAnimation = true;
    }

    private void initData() {
        this.mPaint = new Paint();
        this.mPaint.setColor(Color.parseColor("#33000000"));
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        this.centerX = width / 2;
        this.centerY = height / 2;
        this.length = getResources().getDimensionPixelOffset(R.dimen.dp_162);
        Log.i(this.TAG, "onSizeChanged=" + width + SwitchNetworkManager.SEP_SP + height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i(this.TAG, "onDraw");
        int centerX = this.centerX;
        int length = this.length;
        int centerY = this.centerY;
        this.myRectF = new RectF(centerX - length, centerY - length, centerX + length, centerY + length);
        if (this.isStartAnimation) {
            if (this.isRed) {
                this.sweepGradient = new SweepGradient(this.centerX, this.centerY,
                        new int[]{COLOR_SCAN_ALPHA, COLOR_SCAN_ALPHA, COLOR_SCAN_ALPHA, COLOR_SCAN_ALPHA, COLOR_SCAN_END_RED}, (float[]) null);
            } else {
                this.sweepGradient = new SweepGradient(this.centerX, this.centerY,
                        new int[]{COLOR_SCAN_ALPHA, COLOR_SCAN_ALPHA, COLOR_SCAN_ALPHA, COLOR_SCAN_ALPHA, COLOR_SCAN_END_BLUE}, (float[]) null);
            }
            this.mPaint.setShader(this.sweepGradient);
            canvas.concat(this.matrix);
            canvas.drawArc(this.myRectF, 0.0f, 360.0f, true, this.mPaint);
        }
    }

    public void startScannerAnimation() {
        Log.i(this.TAG, "startScannerAnimation");
        this.isFind = false;
        this.isStartAnimation = true;
        this.handler.post(this.runnable);
        invalidate();
    }

    public void start(View view) {
        this.isStartAnimation = true;
        Log.i(this.TAG, "v=" + view.toString());
        Log.i(this.TAG, "object=" + toString());
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 0.0f, 360.0f);
        animator.setDuration(1500L);
        animator.start();
    }

    public void stop() {
        this.objectAnimator.cancel();
    }

    public void stopScannerAnimation() {
        Log.i(this.TAG, "stopScannerAnimation");
        invalidate();
        this.isFind = true;
    }

    private void startAlpha() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(1000L);
        alphaAnimation.setFillAfter(true);
        setAnimation(alphaAnimation);
        startAnimation(alphaAnimation);
    }

    public void setAnimationOnclickListener(ScannerInterface scannerInterface) {
        this.scannerInterface = scannerInterface;
    }
}