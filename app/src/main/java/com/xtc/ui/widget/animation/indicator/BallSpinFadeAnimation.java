package com.xtc.ui.widget.animation.indicator;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.xtc.log.LogUtil;
import com.xtc.ui.widget.util.UiCommonUtil;

import java.util.ArrayList;

/**
 * Six-dot spinner: dots are placed on a circle and fade/scale out of phase.
 *
 * <p>The per-dot start delays are {@code 0,120,...,600} ms and the whole loop is
 * one second long.
 */
public class BallSpinFadeAnimation extends AbstractAnimation {

    public static final int ALPHA = 255;
    public static final float SCALE = 1.0f;

    private static final String TAG = "BallSpinFadeAnimation";
    private static final int DefaultSmallCircleDiameterDp = 9;
    private static final int DefaultSmallCircleMarginDp = 1;

    /** Per-dot animation start delays, in milliseconds. */
    private static final int[] START_DELAYS = {0, 120, 240, 360, 480, 600, 720, 780, 840};

    private int exoSquareHeight;
    final int[] mAlphas;
    int[] mColors;
    private Context mContext;
    float[] scaleFloats;
    private float scaleRatio;
    private int smallCircleDiameter;
    private int smallCircleDistance;
    private int smallCircleMargin;

    public BallSpinFadeAnimation(Context context, int[] colors) {
        this(context, colors, 1.0f);
    }

    public BallSpinFadeAnimation(Context context, int[] colors, float scaleRatio) {
        this.scaleRatio = 1.0f;
        this.scaleFloats = new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};
        this.mAlphas = new int[]{255, 255, 255, 255, 255, 255};
        this.mColors = new int[6];
        this.mContext = context;
        this.mColors = colors;
        this.scaleRatio = scaleRatio;
        this.smallCircleDiameter = (int) (this.scaleRatio * UiCommonUtil.dp2Px(this.mContext, DefaultSmallCircleDiameterDp));
        this.smallCircleMargin = (int) (this.scaleRatio * UiCommonUtil.dp2Px(this.mContext, DefaultSmallCircleMarginDp));
        int i = this.smallCircleDiameter;
        this.smallCircleDistance = this.smallCircleMargin + i;
        this.exoSquareHeight = i + (this.smallCircleDistance * 2);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        for (int i = 0; i < 6; i++) {
            canvas.save();
            Point center = getSmallCircleCenter((i * 60) + 30);
            canvas.translate(center.x, center.y);
            paint.setColor(this.mColors[i]);
            canvas.drawCircle(0.0f, 0.0f, this.smallCircleDiameter / 2, paint);
            canvas.restore();
        }
    }

    /** Centre of the dot at {@code degrees} around the ring. */
    private Point getSmallCircleCenter(double degrees) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        double radians = ((degrees * Math.PI) * 2.0d) / 360.0d;
        return new Point(
                (float) (((double) centerX) + (((double) this.smallCircleDistance) * Math.cos(radians))),
                (float) (((double) centerY) + (((double) this.smallCircleDistance) * Math.sin(radians))));
    }

    /** Simple mutable point. */
    final class Point {
        public float x;
        public float y;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    @Override
    public ArrayList<ValueAnimator> onCreateAnimators() {
        LogUtil.i(TAG, "onCreateAnimators ---");
        ArrayList<ValueAnimator> animators = new ArrayList<ValueAnimator>();
        for (final int i = 0; i < 6; i++) {
            ValueAnimator scaleAnimator = ValueAnimator.ofFloat(1.0f, 0.4f, 1.0f);
            scaleAnimator.setDuration(1000L);
            scaleAnimator.setRepeatCount(-1);
            scaleAnimator.setStartDelay(START_DELAYS[i]);
            addUpdateListener(scaleAnimator, new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    LogUtil.d(BallSpinFadeAnimation.TAG, "scaleAnim onAnimationUpdate");
                    BallSpinFadeAnimation.this.scaleFloats[i] = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    BallSpinFadeAnimation.this.postInvalidate();
                }
            });
            ValueAnimator alphaAnimator = ValueAnimator.ofInt(255, 0, 255);
            alphaAnimator.setDuration(1000L);
            alphaAnimator.setRepeatCount(-1);
            alphaAnimator.setStartDelay(START_DELAYS[i]);
            addUpdateListener(alphaAnimator, new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    LogUtil.d(BallSpinFadeAnimation.TAG, "alphaAnim onAnimationUpdate");
                    BallSpinFadeAnimation.this.mAlphas[i] = ((Integer) valueAnimator.getAnimatedValue()).intValue();
                    BallSpinFadeAnimation.this.postInvalidate();
                }
            });
            animators.add(scaleAnimator);
            animators.add(alphaAnimator);
        }
        return animators;
    }
}
