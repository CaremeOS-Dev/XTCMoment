package com.xtc.ui.widget.animation.progressview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import com.xtc.ui.widget.R;
import com.xtc.utils.ui.DimenUtil;
import com.xtc.virtualselfapi.constants.Constants;

/** 视频加载进度控件：外圈显示进度弧并持续旋转，常用于视频缓冲提示。 */
public class VideoLoadingProgressBar extends View {
    private ValueAnimator animator;
    private int direction;
    private int insideColor;
    private int maxProgress;
    private int outsideColor;
    private float outsideRadius;
    private Paint paint;
    private float progress;
    private String progressText;
    private int progressTextColor;
    private float progressTextSize;
    private float progressWidth;
    private Rect rect;

    /** 进度弧起始方向，degree 为该方向对应的起始角度。 */
    enum DirectionEnum {
        LEFT(0, 180.0f),
        TOP(1, 270.0f),
        RIGHT(2, 0.0f),
        BOTTOM(3, 90.0f);

        private final float degree;
        private final int direction;

        DirectionEnum(int direction, float degree) {
            this.direction = direction;
            this.degree = degree;
        }

        public int getDirection() {
            return this.direction;
        }

        public float getDegree() {
            return this.degree;
        }

        public boolean equalsDescription(int direction) {
            return this.direction == direction;
        }

        public static DirectionEnum getDirection(int direction) {
            for (DirectionEnum directionEnum : values()) {
                if (directionEnum.equalsDescription(direction)) {
                    return directionEnum;
                }
            }
            return RIGHT;
        }

        public static float getDegree(int direction) {
            DirectionEnum directionEnum = getDirection(direction);
            if (directionEnum == null) {
                return 0.0f;
            }
            return directionEnum.getDegree();
        }
    }

    public VideoLoadingProgressBar(Context context) {
        this(context, null);
    }

    public VideoLoadingProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoLoadingProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.VideoLoadingProgressBar, defStyleAttr, 0);
        this.outsideColor = attributes.getColor(R.styleable.VideoLoadingProgressBar_outside_color, ContextCompat.getColor(getContext(), R.color.color_FFFFFF));
        this.outsideRadius = attributes.getDimension(R.styleable.VideoLoadingProgressBar_outside_radius, DimenUtil.dp2px(getContext(), 60.0f));
        this.insideColor = attributes.getColor(R.styleable.VideoLoadingProgressBar_inside_color, ContextCompat.getColor(getContext(), R.color.color_999999));
        this.progressTextColor = attributes.getColor(R.styleable.VideoLoadingProgressBar_progress_text_color, ContextCompat.getColor(getContext(), R.color.color_FFFFFF));
        this.progressTextSize = attributes.getDimension(R.styleable.VideoLoadingProgressBar_progress_text_size, DimenUtil.dp2px(getContext(), 14.0f));
        this.progressWidth = attributes.getDimension(R.styleable.VideoLoadingProgressBar_progress_width, DimenUtil.dp2px(getContext(), 10.0f));
        this.progress = attributes.getFloat(R.styleable.VideoLoadingProgressBar_progress, 50.0f);
        this.maxProgress = attributes.getInt(R.styleable.VideoLoadingProgressBar_max_progress, 100);
        this.direction = attributes.getInt(R.styleable.VideoLoadingProgressBar_direction, 3);
        attributes.recycle();
        this.paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int center = getWidth() / 2;
        this.paint.setColor(this.insideColor);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeWidth(this.progressWidth);
        this.paint.setAntiAlias(true);
        float centerF = center;
        canvas.drawCircle(centerF, centerF, this.outsideRadius, this.paint);
        this.paint.setColor(this.outsideColor);
        float radius = this.outsideRadius;
        canvas.drawArc(new RectF(centerF - radius, centerF - radius, centerF + radius, centerF + radius),
                DirectionEnum.getDegree(this.direction), (this.progress / this.maxProgress) * 360.0f, false, this.paint);
        startRotateAnim();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        if (View.MeasureSpec.getMode(widthMeasureSpec) != 1073741824) {
            width = (int) ((this.outsideRadius * 2.0f) + this.progressWidth);
        }
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        if (View.MeasureSpec.getMode(heightMeasureSpec) != 1073741824) {
            height = (int) ((this.outsideRadius * 2.0f) + this.progressWidth);
        }
        setMeasuredDimension(width, height);
    }

    private String getProgressText() {
        return ((int) ((this.progress / this.maxProgress) * 100.0f)) + "%";
    }

    public int getOutsideColor() {
        return this.outsideColor;
    }

    public void setOutsideColor(int outsideColor) {
        this.outsideColor = outsideColor;
    }

    public float getOutsideRadius() {
        return this.outsideRadius;
    }

    public void setOutsideRadius(float outsideRadius) {
        this.outsideRadius = outsideRadius;
    }

    public int getInsideColor() {
        return this.insideColor;
    }

    public void setInsideColor(int insideColor) {
        this.insideColor = insideColor;
    }

    public int getProgressTextColor() {
        return this.progressTextColor;
    }

    public void setProgressTextColor(int progressTextColor) {
        this.progressTextColor = progressTextColor;
    }

    public float getProgressTextSize() {
        return this.progressTextSize;
    }

    public void setProgressTextSize(float progressTextSize) {
        this.progressTextSize = progressTextSize;
    }

    public float getProgressWidth() {
        return this.progressWidth;
    }

    public void setProgressWidth(float progressWidth) {
        this.progressWidth = progressWidth;
    }

    public synchronized int getMaxProgress() {
        return this.maxProgress;
    }

    public synchronized void setMaxProgress(int maxProgress) {
        if (maxProgress < 0) {
            throw new IllegalArgumentException("maxProgress should not be less than 0");
        }
        this.maxProgress = maxProgress;
    }

    public synchronized float getProgress() {
        return this.progress;
    }

    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress should not be less than 0");
        }
        this.progress = progress;
        postInvalidate();
    }

    private void startAnim(float targetProgress) {
        this.animator = ObjectAnimator.ofFloat(0.0f, targetProgress);
        this.animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                VideoLoadingProgressBar.this.progress = ((Float) animation.getAnimatedValue()).floatValue();
                VideoLoadingProgressBar.this.postInvalidate();
            }
        });
        this.animator.setStartDelay(500L);
        this.animator.setDuration(Constants.DEFAULT_INIT_DELAY_TIME);
        this.animator.setInterpolator(new LinearInterpolator());
        this.animator.start();
    }

    private void startRotateAnim() {
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(this, "rotation", 0.0f, 360.0f);
        rotateAnimator.setDuration(1000L);
        rotateAnimator.setRepeatCount(-1);
        rotateAnimator.start();
    }
}