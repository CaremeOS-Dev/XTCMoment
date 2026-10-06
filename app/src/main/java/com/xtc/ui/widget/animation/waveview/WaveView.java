package com.xtc.ui.widget.animation.waveview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import com.xtc.virtualselfapi.constants.Constants;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** 水波纹扩散动画：周期性生成从中心向外扩散并渐隐的圆。 */
public class WaveView extends View {
    private List<Circle> mCircleList;
    private int mColor;
    private Runnable mCreateCircle;
    private long mDuration;
    private float mInitialRadius;
    private Interpolator mInterpolator;
    private boolean mIsRunning;
    private long mLastCreateTime;
    private float mMaxRadius;
    private float mMaxRadiusRate;
    private boolean mMaxRadiusSet;
    private Paint mPaint;
    private int mSpeed;

    public WaveView(Context context) {
        super(context);
        this.mDuration = Constants.DEFAULT_INIT_DELAY_TIME;
        this.mSpeed = 500;
        this.mMaxRadiusRate = 0.85f;
        this.mCircleList = new ArrayList<>();
        this.mCreateCircle = new Runnable() {
            @Override
            public void run() {
                if (WaveView.this.mIsRunning) {
                    WaveView.this.newCircle();
                    WaveView waveView = WaveView.this;
                    waveView.postDelayed(waveView.mCreateCircle, WaveView.this.mSpeed);
                }
            }
        };
        this.mInterpolator = new LinearInterpolator();
        this.mPaint = new Paint(1);
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mDuration = Constants.DEFAULT_INIT_DELAY_TIME;
        this.mSpeed = 500;
        this.mMaxRadiusRate = 0.85f;
        this.mCircleList = new ArrayList<>();
        this.mCreateCircle = new Runnable() {
            @Override
            public void run() {
                if (WaveView.this.mIsRunning) {
                    WaveView.this.newCircle();
                    WaveView waveView = WaveView.this;
                    waveView.postDelayed(waveView.mCreateCircle, WaveView.this.mSpeed);
                }
            }
        };
        this.mInterpolator = new LinearInterpolator();
        this.mPaint = new Paint(1);
    }

    public void setStyle(Paint.Style style) {
        this.mPaint.setStyle(style);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        if (this.mMaxRadiusSet) {
            return;
        }
        this.mMaxRadius = (Math.min(width, height) * this.mMaxRadiusRate) / 2.0f;
    }

    public void setMaxRadiusRate(float maxRadiusRate) {
        this.mMaxRadiusRate = maxRadiusRate;
    }

    public void setColor(int color) {
        this.mColor = color;
        this.mPaint.setColor(color);
    }

    public void start() {
        if (this.mIsRunning) {
            return;
        }
        this.mIsRunning = true;
        this.mCreateCircle.run();
    }

    public void stop() {
        this.mIsRunning = false;
    }

    public void stopImmediately() {
        this.mIsRunning = false;
        this.mCircleList.clear();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.mPaint.setColor(this.mColor);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, this.mInitialRadius, this.mPaint);
        Iterator<Circle> iterator = this.mCircleList.iterator();
        while (iterator.hasNext()) {
            Circle circle = iterator.next();
            float currentRadius = circle.getCurrentRadius();
            if (System.currentTimeMillis() - circle.mCreateTime < this.mDuration) {
                this.mPaint.setColor(this.mColor);
                this.mPaint.setAlpha(circle.getAlpha());
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, currentRadius, this.mPaint);
            } else {
                iterator.remove();
            }
        }
        if (this.mCircleList.size() > 0) {
            postInvalidateDelayed(10L);
        }
    }

    public static Bitmap createBitmapBySize(Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    public void setInitialRadius(float initialRadius) {
        this.mInitialRadius = initialRadius;
    }

    public void setDuration(long duration) {
        this.mDuration = duration;
    }

    public void setMaxRadius(float maxRadius) {
        this.mMaxRadius = maxRadius;
        this.mMaxRadiusSet = true;
    }

    public void setSpeed(int speed) {
        this.mSpeed = speed;
    }

    private void newCircle() {
        long now = System.currentTimeMillis();
        if (now - this.mLastCreateTime < this.mSpeed) {
            return;
        }
        this.mCircleList.add(new Circle());
        invalidate();
        this.mLastCreateTime = now;
    }

    /** 单个扩散圆，按创建时间与插值器计算当前半径与透明度。 */
    private class Circle {
        private long mCreateTime = System.currentTimeMillis();

        Circle() {
        }

        int getAlpha() {
            return (int) (255.0f - (WaveView.this.mInterpolator.getInterpolation(
                    (getCurrentRadius() - WaveView.this.mInitialRadius)
                            / (WaveView.this.mMaxRadius - WaveView.this.mInitialRadius)) * 255.0f));
        }

        float getCurrentRadius() {
            return WaveView.this.mInitialRadius + (WaveView.this.mInterpolator.getInterpolation(
                    ((System.currentTimeMillis() - this.mCreateTime) * 1.0f) / WaveView.this.mDuration)
                    * (WaveView.this.mMaxRadius - WaveView.this.mInitialRadius));
        }
    }

    public void setInterpolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
        if (this.mInterpolator == null) {
            this.mInterpolator = new LinearInterpolator();
        }
    }
}