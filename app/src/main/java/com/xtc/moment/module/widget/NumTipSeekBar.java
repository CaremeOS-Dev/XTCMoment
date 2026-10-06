package com.xtc.moment.module.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.utils.ui.MotionEventUtils;

import java.math.BigDecimal;

/**
 * Seek bar that draws a rounded track, a filled progress area and an optional circular thumb with
 * a numeric label.
 */
public class NumTipSeekBar extends View {

    private static final String TAG = NumTipSeekBar.class.getSimpleName();

    /** Progress value used when the xml does not specify one. */
    private static final int DEFAULT_MAX_VALUE = 10;
    /** Rounding mode used when converting a touch position into a progress value. */
    private static final int ROUNDING_MODE = BigDecimal.ROUND_HALF_UP;

    private Paint mBorderPaint;
    private RectF mBorderRecf;
    private float mBorderSize;
    private int mBorderColor;

    private Paint mProgressPaint;
    private RectF mProgressRecf;
    private float mProgressHeight;
    private int mProgressColor;

    private Paint mCircleButtonPaint;
    private RectF mCircleRecf;
    private int mCircleButtonColor;
    private float mCircleButtonRadius;
    private float mCirclePotionX;

    private Paint mCircleAperturePaint;
    private RectF mCircleApertureRectF;
    private int mCircleApertureColor;
    private int mCircleApertureWidth;
    private boolean mIsShowCircleAperture;

    private Paint mCircleButtonTextPaint;
    private int mCircleButtonTextColor;
    private float mCircleButtonTextSize;

    private Paint mTickBarPaint;
    private RectF mTickBarRecf;
    private float mTickBarHeight;
    private int mTickBarColor;

    private int mViewWidth;
    private int mViewHeight;
    private int mWidth;
    private int mHeight;

    private int mMaxProgress;
    private int mSelectProgress;
    private int mStartProgress;

    private boolean mIsRound;
    private boolean mIsShowButton;
    private boolean mIsShowButtonText;

    private OnProgressChangeListener mOnProgressChangeListener;

    /** Notified when the selected value changes. */
    public interface OnProgressChangeListener {
        void onChange(int progress);
    }

    public void setOnProgressChangeListener(OnProgressChangeListener listener) {
        this.mOnProgressChangeListener = listener;
    }

    public NumTipSeekBar(Context context) {
        this(context, null);
    }

    public NumTipSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumTipSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mMaxProgress = DEFAULT_MAX_VALUE;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NumTipSeekBar);
        this.mTickBarHeight = typedArray.getDimensionPixelOffset(R.styleable.NumTipSeekBar_tickBarHeight, getDpValue(8));
        this.mTickBarColor = typedArray.getColor(R.styleable.NumTipSeekBar_tickBarColor, -16777216);
        this.mCircleButtonColor = typedArray.getColor(R.styleable.NumTipSeekBar_circleButtonColor, -1);
        this.mCircleButtonTextColor = typedArray.getColor(R.styleable.NumTipSeekBar_circleButtonTextColor, -1);
        this.mCircleButtonTextSize = typedArray.getDimension(R.styleable.NumTipSeekBar_circleButtonTextSize, 0.0f);
        this.mCircleButtonRadius = typedArray.getDimensionPixelOffset(R.styleable.NumTipSeekBar_circleButtonRadius, 0);
        this.mCircleApertureWidth = typedArray.getDimensionPixelOffset(R.styleable.NumTipSeekBar_circleApertureWidth, 0);
        this.mCircleApertureColor = typedArray.getColor(R.styleable.NumTipSeekBar_circleApertureColor,
                getResources().getColor(R.color.color_dc2323));
        this.mProgressHeight = typedArray.getDimensionPixelOffset(R.styleable.NumTipSeekBar_progressHeight, 0);
        this.mProgressColor = typedArray.getColor(R.styleable.NumTipSeekBar_progressColor, -1);
        this.mSelectProgress = typedArray.getInt(R.styleable.NumTipSeekBar_selectProgress, 0);
        this.mStartProgress = typedArray.getInt(R.styleable.NumTipSeekBar_startProgress, 0);
        this.mMaxProgress = typedArray.getInt(R.styleable.NumTipSeekBar_maxProgress, DEFAULT_MAX_VALUE);
        this.mIsShowButtonText = typedArray.getBoolean(R.styleable.NumTipSeekBar_isShowButtonText, false);
        this.mIsShowButton = typedArray.getBoolean(R.styleable.NumTipSeekBar_isShowButton, false);
        this.mIsRound = typedArray.getBoolean(R.styleable.NumTipSeekBar_isRound, false);
        this.mBorderSize = typedArray.getDimensionPixelOffset(R.styleable.NumTipSeekBar_borderSize, 0);
        this.mBorderColor = typedArray.getColor(R.styleable.NumTipSeekBar_borderColor, -1);
        initView();
        typedArray.recycle();
    }

    private void initView() {
        this.mBorderPaint = new Paint();
        this.mBorderPaint.setStrokeWidth(this.mBorderSize);
        this.mBorderPaint.setColor(this.mBorderColor);
        this.mBorderPaint.setStyle(Paint.Style.FILL);
        this.mBorderPaint.setAntiAlias(true);

        this.mProgressPaint = new Paint();
        this.mProgressPaint.setColor(this.mProgressColor);
        this.mProgressPaint.setStyle(Paint.Style.FILL);
        this.mProgressPaint.setAntiAlias(true);

        this.mCircleButtonPaint = new Paint();
        this.mCircleButtonPaint.setColor(this.mCircleButtonColor);
        this.mCircleButtonPaint.setStyle(Paint.Style.FILL);
        this.mCircleButtonPaint.setAntiAlias(true);

        this.mCircleAperturePaint = new Paint();
        this.mCircleAperturePaint.setColor(this.mCircleApertureColor);
        this.mCircleAperturePaint.setStyle(Paint.Style.FILL);
        this.mCircleAperturePaint.setAntiAlias(true);

        this.mCircleButtonTextPaint = new Paint();
        this.mCircleButtonTextPaint.setTextAlign(Paint.Align.CENTER);
        this.mCircleButtonTextPaint.setColor(this.mCircleButtonTextColor);
        this.mCircleButtonTextPaint.setStyle(Paint.Style.FILL);
        this.mCircleButtonTextPaint.setTextSize(this.mCircleButtonTextSize);
        this.mCircleButtonTextPaint.setAntiAlias(true);

        this.mTickBarPaint = new Paint();
        this.mTickBarPaint.setColor(this.mTickBarColor);
        this.mTickBarPaint.setStyle(Paint.Style.FILL);
        this.mTickBarPaint.setAntiAlias(true);

        this.mTickBarRecf = new RectF();
        this.mProgressRecf = new RectF();
        this.mCircleRecf = new RectF();
        this.mBorderRecf = new RectF();
        this.mCircleApertureRectF = new RectF();
        setCircleApertureWidth(this.mCircleApertureWidth);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        getParent().requestDisallowInterceptTouchEvent(true);
        float x = MotionEventUtils.getX(event);
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            judgePosition(x);
            return true;
        }
        if (action == MotionEvent.ACTION_UP) {
            OnProgressChangeListener listener = this.mOnProgressChangeListener;
            if (listener != null) {
                listener.onChange(this.mSelectProgress);
            }
            return true;
        }
        if (action == MotionEvent.ACTION_MOVE) {
            judgePosition(x);
            return true;
        }
        return super.onTouchEvent(event);
    }

    /** Maps a horizontal touch position to a progress value. */
    private void judgePosition(float x) {
        int viewWidth = this.mViewWidth;
        float paddingLeft = getPaddingLeft();
        if (viewWidth == 0) {
            LogUtil.e(TAG, "judgePosition: mViewWidth is 0!");
            return;
        }
        int progress = this.mSelectProgress;
        if (x >= paddingLeft) {
            int value = new BigDecimal(((x - paddingLeft) / viewWidth) * this.mMaxProgress)
                    .setScale(0, ROUNDING_MODE).intValue();
            progress = value > this.mMaxProgress ? this.mMaxProgress : value;
        } else {
            progress = 0;
        }
        if (progress != this.mSelectProgress) {
            setSelectProgress(progress, false);
        }
    }

    private int getMySize(int measureSpec, int desiredSize) {
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.AT_MOST) {
            return (int) (desiredSize + (this.mBorderSize * 2.0f));
        }
        if (mode == MeasureSpec.UNSPECIFIED || size < desiredSize) {
            return desiredSize;
        }
        return size;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMySize(widthMeasureSpec, 0);
        float height = this.mTickBarHeight;
        if (this.mCircleApertureWidth > 0) {
            float aperture = this.mCircleApertureWidth;
            if (height < (this.mCircleButtonRadius * 2.0f) + (aperture * 2)) {
                height = (aperture * 2) + (this.mCircleButtonRadius * 2.0f);
            }
        } else if (height < this.mCircleButtonRadius * 2.0f) {
            height = this.mCircleButtonRadius * 2.0f;
        }
        if (height < this.mProgressHeight) {
            height = this.mProgressHeight;
        }
        setMeasuredDimension(width, getMySize(heightMeasureSpec, (int) height));
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        this.mWidth = width;
        this.mHeight = height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initValues(this.mWidth, this.mHeight);
        if (this.mIsRound) {
            if (this.mBorderSize > 0.0f) {
                canvas.drawRoundRect(this.mBorderRecf, (this.mTickBarHeight / 2.0f) + this.mBorderSize,
                        (this.mHeight / 2) + this.mBorderSize, this.mBorderPaint);
            }
            canvas.drawRoundRect(this.mTickBarRecf, this.mTickBarHeight / 2.0f, this.mTickBarHeight / 2.0f, this.mTickBarPaint);
            if (this.mSelectProgress > this.mStartProgress) {
                int layer = canvas.saveLayer(this.mProgressRecf, this.mProgressPaint, 31);
                canvas.drawRoundRect(this.mTickBarRecf, this.mProgressHeight / 2.0f, this.mProgressHeight / 2.0f,
                        this.mProgressPaint);
                this.mProgressPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
                canvas.drawRect(this.mProgressRecf, this.mProgressPaint);
                this.mProgressPaint.setXfermode(null);
                canvas.restoreToCount(layer);
            }
        } else {
            if (this.mBorderSize > 0.0f) {
                canvas.drawRect(this.mBorderRecf, this.mBorderPaint);
            }
            canvas.drawRect(this.mTickBarRecf, this.mTickBarPaint);
            if (this.mSelectProgress > this.mStartProgress) {
                canvas.drawRect(this.mProgressRecf, this.mProgressPaint);
            }
        }
        if (this.mIsShowButton) {
            if (this.mIsShowCircleAperture) {
                canvas.drawCircle(this.mCirclePotionX, this.mHeight / 2,
                        this.mCircleButtonRadius + this.mCircleApertureWidth, this.mCircleAperturePaint);
            }
            canvas.drawCircle(this.mCirclePotionX, this.mHeight / 2, this.mCircleButtonRadius, this.mCircleButtonPaint);
        }
        if (this.mIsShowButtonText) {
            Paint.FontMetricsInt fontMetrics = this.mCircleButtonTextPaint.getFontMetricsInt();
            canvas.drawText(String.valueOf(this.mSelectProgress), this.mCircleRecf.centerX(),
                    (int) ((((this.mCircleRecf.bottom + this.mCircleRecf.top) - fontMetrics.bottom - fontMetrics.top) / 2.0f)),
                    this.mCircleButtonTextPaint);
        }
    }

    /** Recomputes all rectangles and the thumb position for the current size. */
    private void initValues(int width, int height) {
        this.mViewWidth = (int) (width - (this.mBorderSize * 2.0f));
        this.mViewHeight = (int) (height - (this.mBorderSize * 2.0f));
        int left = (int) this.mBorderSize;
        int top = (int) this.mBorderSize;
        if (this.mTickBarHeight > this.mViewHeight) {
            this.mTickBarHeight = this.mViewHeight;
        }
        this.mTickBarRecf.set(left, ((this.mViewHeight - this.mTickBarHeight) / 2.0f) + top,
                this.mViewWidth + left, (this.mTickBarHeight / 2.0f) + (this.mViewHeight / 2) + top);
        this.mBorderRecf.set(this.mTickBarRecf.left - this.mBorderSize, this.mTickBarRecf.top - this.mBorderSize,
                this.mTickBarRecf.right + this.mBorderSize, this.mTickBarRecf.bottom + this.mBorderSize);
        this.mCirclePotionX = (((this.mSelectProgress - this.mStartProgress)
                / (this.mMaxProgress - this.mStartProgress)) * this.mViewWidth) + left;
        if (this.mProgressHeight > this.mViewHeight) {
            this.mProgressHeight = this.mViewHeight;
        }
        this.mProgressRecf.set(left, ((this.mViewHeight - this.mProgressHeight) / 2.0f) + top,
                this.mCirclePotionX, (this.mProgressHeight / 2.0f) + (this.mViewHeight / 2) + top);
        if (this.mCircleButtonRadius > this.mViewHeight / 2) {
            this.mCircleButtonRadius = this.mViewHeight / 2;
        }
        this.mCircleRecf.set(this.mCirclePotionX - this.mCircleButtonRadius,
                ((this.mViewHeight / 2) - this.mCircleButtonRadius) + top,
                this.mCirclePotionX + this.mCircleButtonRadius,
                (this.mViewHeight / 2) + this.mCircleButtonRadius + top);
        this.mCircleApertureRectF.set(this.mCircleRecf.left - this.mCircleApertureWidth,
                this.mCircleRecf.top - this.mCircleApertureWidth,
                this.mCircleRecf.right + this.mCircleApertureWidth,
                this.mCircleRecf.bottom + this.mCircleApertureWidth);
    }

    public float getTickBarHeight() {
        return this.mTickBarHeight;
    }

    public void setTickBarHeight(float height) {
        this.mTickBarHeight = height;
    }

    public int getTickBarColor() {
        return this.mTickBarColor;
    }

    public void setTickBarColor(int color) {
        this.mTickBarColor = color;
        this.mTickBarPaint.setColor(this.mTickBarColor);
    }

    public int getCircleButtonColor() {
        return this.mCircleButtonColor;
    }

    public void setCircleButtonColor(int color) {
        this.mCircleButtonColor = color;
        this.mCircleButtonPaint.setColor(this.mCircleButtonColor);
    }

    public int getCircleButtonTextColor() {
        return this.mCircleButtonTextColor;
    }

    public void setCircleButtonTextColor(int color) {
        this.mCircleButtonTextColor = color;
        this.mCircleButtonTextPaint.setColor(this.mCircleButtonTextColor);
    }

    public float getCircleButtonTextSize() {
        return this.mCircleButtonTextSize;
    }

    public void setCircleButtonTextSize(float size) {
        this.mCircleButtonTextSize = size;
        this.mCircleButtonTextPaint.setTextSize(this.mCircleButtonTextSize);
    }

    public float getCircleButtonRadius() {
        return this.mCircleButtonRadius;
    }

    public void setCircleButtonRadius(float radius) {
        this.mCircleButtonRadius = radius;
    }

    public float getProgressHeight() {
        return this.mProgressHeight;
    }

    public void setProgressHeight(float height) {
        this.mProgressHeight = height;
    }

    public int getProgressColor() {
        return this.mProgressColor;
    }

    public void setProgressColor(int color) {
        this.mProgressColor = color;
        this.mProgressPaint.setColor(this.mProgressColor);
    }

    public int getMaxProgress() {
        return this.mMaxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.mMaxProgress = maxProgress;
    }

    public void setSelectProgress(int progress) {
        setSelectProgress(progress, true);
    }

    public void setSelectProgress(int progress, boolean notify) {
        clampSelectProgress(progress);
        OnProgressChangeListener listener = this.mOnProgressChangeListener;
        if (listener != null && notify) {
            listener.onChange(this.mSelectProgress);
        }
        invalidate();
    }

    private void clampSelectProgress(int progress) {
        this.mSelectProgress = progress;
        if (this.mSelectProgress > this.mMaxProgress) {
            this.mSelectProgress = this.mMaxProgress;
        } else if (this.mSelectProgress <= this.mStartProgress) {
            this.mSelectProgress = this.mStartProgress;
        }
    }

    public int getSelectProgress() {
        return this.mSelectProgress;
    }

    public int getStartProgress() {
        return this.mStartProgress;
    }

    public void setStartProgress(int startProgress) {
        this.mStartProgress = startProgress;
    }

    public void setCircleApertureWidth(int widthDp) {
        this.mCircleApertureWidth = getDpValue(widthDp);
        this.mIsShowCircleAperture = widthDp > 0;
    }

    public void setCircleApertureColor(int color) {
        this.mCircleApertureColor = color;
        this.mCircleAperturePaint.setColor(this.mCircleApertureColor);
    }

    private int getDpValue(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }

    public void setShowCircleAperture(boolean show) {
        this.mIsShowCircleAperture = show;
    }

    public void setRound(boolean round) {
        this.mIsRound = round;
    }

    public void setShowButtonText(boolean show) {
        this.mIsShowButtonText = show;
    }

    public void setShowButton(boolean show) {
        this.mIsShowButton = show;
    }

    public float getBorderSize() {
        return this.mBorderSize;
    }

    public void setBorderSize(float size) {
        this.mBorderSize = size;
        this.mBorderPaint.setStrokeWidth(this.mBorderSize);
    }

    public int getBorderColor() {
        return this.mBorderColor;
    }

    public void setBorderColor(int color) {
        this.mBorderColor = color;
        this.mBorderPaint.setColor(this.mBorderColor);
    }
}