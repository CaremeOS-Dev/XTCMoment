package com.xtc.ui.widget.animation.progressview;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import com.xtc.log.LogUtil;
import com.xtc.ui.widget.R;
import com.xtc.ui.widget.util.UiCommonUtil;

/** 圆形进度条：支持纯色、逐段渐变、扫描渐变三种进度弧样式，并带入场动画。 */
public class CircleProgressBar extends View {
    private static final String TAG = "CircleProgressBar";
    private static final int COLOR_GRADIENT_START_DEFAULT = -16711936; // 0xFF00FF00
    private static final int COLOR_GRADIENT_END_DEFAULT = -65536;     // SupportMenu.CATEGORY_MASK 0xFFFF0000
    private ObjectAnimator animator;
    private Context context;
    private int mAnimationDuration;
    private ArgbEvaluator mArgbEvaluator;
    private int mBottomColor;
    private float mCircleWidth;
    private int mGradientConstant;
    private int mGradientEndColor;
    private int mGradientStartColor;
    private int mTopColor;
    private boolean mUseRoundAngle;
    private int max;
    private Paint paint;
    private float progress;

    /** 渐变样式常量。 */
    public interface Gradient {
        public static final int GRADIENT_END_ROUND = 1;
        public static final int GRADIENT_END_SUQARE = 2;
        public static final int NO_GRADIENT = 0;
    }

    public CircleProgressBar(Context context) {
        this(context, null);
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDefaultValue();
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);
        if (attributes != null) {
            this.mCircleWidth = attributes.getDimension(R.styleable.CircleProgressBar_circleWidth, this.mCircleWidth);
            this.mBottomColor = attributes.getInt(R.styleable.CircleProgressBar_bottomColor, this.mBottomColor);
            this.mTopColor = attributes.getInt(R.styleable.CircleProgressBar_topColor, this.mTopColor);
            this.mGradientStartColor = attributes.getInt(R.styleable.CircleProgressBar_gradientStartColor, this.mGradientStartColor);
            this.mGradientEndColor = attributes.getInt(R.styleable.CircleProgressBar_gradientEndColor, this.mGradientEndColor);
            this.max = attributes.getInt(R.styleable.CircleProgressBar_maxProgress, this.max);
            this.progress = attributes.getFloat(R.styleable.CircleProgressBar_currentProgress, this.progress);
            this.mAnimationDuration = attributes.getInt(R.styleable.CircleProgressBar_animationDuration, this.mAnimationDuration);
            this.mUseRoundAngle = attributes.getBoolean(R.styleable.CircleProgressBar_useRoundAngle, this.mUseRoundAngle);
            this.mGradientConstant = attributes.getInt(R.styleable.CircleProgressBar_gradientConstant, this.mGradientConstant);
            attributes.recycle();
        }
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setDither(true);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeWidth(this.mCircleWidth);
        this.animator = ObjectAnimator.ofFloat(this, "progress", 0.0f, this.progress);
        this.animator.setInterpolator(new FastOutSlowInInterpolator());
        this.animator.setDuration(this.mAnimationDuration);
        LogUtil.i(TAG, "init completed !");
    }

    private void initDefaultValue() {
        this.context = getContext();
        Resources resources = this.context.getResources();
        this.max = 100;
        this.progress = 0.0f;
        this.mUseRoundAngle = true;
        this.mGradientConstant = 0;
        this.mBottomColor = resources.getColor(R.color.color_0c2b35);
        this.mTopColor = resources.getColor(R.color.color_00c0ff);
        this.mCircleWidth = resources.getDimension(R.dimen.circle_progress_bar_default_width);
        this.mGradientStartColor = COLOR_GRADIENT_START_DEFAULT;
        this.mGradientEndColor = COLOR_GRADIENT_END_DEFAULT;
        this.mAnimationDuration = 1000;
        this.mArgbEvaluator = new ArgbEvaluator();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = Math.min(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int center = getWidth() / 2;
        float radius = center - (this.mCircleWidth / 2.0f);
        this.paint.setShader(null);
        drawCircle(canvas, center, radius);
        drawArc(canvas, center, radius, this.progress);
    }

    private void drawCircle(Canvas canvas, int center, float radius) {
        this.paint.setColor(this.mBottomColor);
        float centerF = center;
        canvas.drawCircle(centerF, centerF, radius, this.paint);
    }

    private void drawArc(Canvas canvas, int center, float radius, float currentProgress) {
        float centerF = center;
        float left = centerF - radius;
        float right = centerF + radius;
        RectF bounds = new RectF(left, left, right, right);
        if (this.mUseRoundAngle) {
            this.paint.setStrokeCap(Paint.Cap.ROUND);
        } else {
            this.paint.setStrokeCap(Paint.Cap.SQUARE);
        }
        int gradientType = this.mGradientConstant;
        if (gradientType == 1) {
            drawTopEndRound(canvas, bounds, currentProgress);
        } else if (gradientType != 2) {
            drawTopNoGradient(canvas, bounds, currentProgress);
        } else {
            drawTopEndSquare(canvas, bounds, center, currentProgress);
        }
    }

    private void drawTopNoGradient(Canvas canvas, RectF bounds, float currentProgress) {
        this.paint.setColor(this.mTopColor);
        canvas.drawArc(bounds, -90.0f, (currentProgress / this.max) * 360.0f, false, this.paint);
    }

    private void drawTopEndRound(Canvas canvas, RectF bounds, float currentProgress) {
        float sweepAngle = (currentProgress / this.max) * 360.0f;
        int degree = 0;
        while (true) {
            float degreeF = degree;
            if (degreeF >= sweepAngle) {
                return;
            }
            this.paint.setColor(((Integer) this.mArgbEvaluator.evaluate(degreeF / sweepAngle,
                    Integer.valueOf(this.mGradientStartColor), Integer.valueOf(this.mGradientEndColor))).intValue());
            canvas.drawArc(bounds, degree - 90, 1.0f, false, this.paint);
            degree++;
        }
    }

    private void drawTopEndSquare(Canvas canvas, RectF bounds, int center, float currentProgress) {
        float centerF = center;
        this.paint.setShader(getCustomShader(-90.0f, centerF, centerF, currentProgress));
        canvas.drawArc(bounds, -90.0f, (currentProgress / this.max) * 360.0f, false, this.paint);
    }

    private Shader getCustomShader(float rotateDegree, float centerX, float centerY, float currentProgress) {
        int startColor = this.mGradientStartColor;
        SweepGradient sweepGradient = new SweepGradient(centerX, centerY,
                new int[]{startColor, this.mGradientEndColor, startColor},
                new float[]{0.0f, currentProgress / this.max, 1.0f});
        Matrix matrix = new Matrix();
        matrix.setRotate(rotateDegree, centerX, centerY);
        sweepGradient.setLocalMatrix(matrix);
        return sweepGradient;
    }

    public void setUseRoundAngle(boolean useRoundAngle) {
        if (this.mUseRoundAngle == useRoundAngle) {
            return;
        }
        this.mUseRoundAngle = useRoundAngle;
        invalidate();
    }

    public void setGradientConstant(int gradientConstant) {
        if (this.mGradientConstant == gradientConstant) {
            return;
        }
        this.mGradientConstant = gradientConstant;
        invalidate();
    }

    public void setMaxProgressValue(int maxProgress) {
        if (this.max != maxProgress) {
            this.max = maxProgress;
            invalidate();
        }
    }

    public void setCircleWidth(float circleWidth) {
        if (this.mCircleWidth != circleWidth) {
            this.mCircleWidth = circleWidth;
            invalidate();
        }
    }

    public void setBottomColor(int colorResId) {
        int color = UiCommonUtil.getColor(this.context, colorResId);
        if (this.mBottomColor != color) {
            this.mBottomColor = color;
            invalidate();
        }
    }

    public void setTopColor(int colorResId) {
        int color = UiCommonUtil.getColor(this.context, colorResId);
        if (this.mTopColor != color) {
            this.mTopColor = color;
            invalidate();
        }
    }

    public void setAnimationDuration(int animationDuration) {
        if (this.mAnimationDuration == animationDuration) {
            return;
        }
        this.mAnimationDuration = animationDuration;
        this.animator.setDuration(this.mAnimationDuration);
    }

    public void setGradientStartColor(int colorResId) {
        int color = UiCommonUtil.getColor(this.context, colorResId);
        if (this.mGradientStartColor != color) {
            this.mGradientStartColor = color;
            invalidate();
        }
    }

    public void setGradientEndColor(int colorResId) {
        int color = UiCommonUtil.getColor(this.context, colorResId);
        if (this.mGradientEndColor != color) {
            this.mGradientEndColor = color;
            invalidate();
        }
    }

    public void setProgress(float progress) {
        if (progress < 0.0f) {
            progress = 0.0f;
        }
        int maxValue = this.max;
        if (progress > maxValue) {
            progress = maxValue;
        }
        if (this.progress == progress) {
            return;
        }
        this.progress = progress;
        this.animator.setFloatValues(0.0f, this.progress);
        invalidate();
    }

    public float getProgress() {
        return this.progress;
    }

    public void setProgressWithAnim(float progress) {
        LogUtil.w(TAG, "setProgress , progress = " + progress);
        if (progress < 0.0f) {
            progress = 0.0f;
        }
        int maxValue = this.max;
        if (progress > maxValue) {
            progress = maxValue;
        }
        float lastProgress = this.progress;
        if (lastProgress == progress) {
            return;
        }
        this.animator.setFloatValues(lastProgress, progress);
        this.animator.start();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.animator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.animator.end();
    }
}