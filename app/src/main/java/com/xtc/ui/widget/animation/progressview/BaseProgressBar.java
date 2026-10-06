package com.xtc.ui.widget.animation.progressview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;
import com.xtc.ui.widget.R;
import com.xtc.utils.ui.DimenUtil;

/** 进度条基类：解析文字/前景/背景相关的自定义属性并保存通用绘制状态。 */
public class BaseProgressBar extends ProgressBar {
    private static int DEFAULT_COLOR_UNREACHED_COLOR = 0;
    private static final int DEFAULT_HEIGHT_REACHED_PROGRESS_BAR = 2;
    private static final int DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR = 2;
    private static final int DEFAULT_SIZE_TEXT_OFFSET = 10;
    private static int DEFAULT_TEXT_COLOR = 0;
    private static final int DEFAULT_TEXT_SIZE = 10;
    protected static final int VISIBLE = 0;
    protected boolean mIfDrawText;
    protected Paint mPaint;
    protected int mReachedBarColor;
    protected int mReachedProgressBarHeight;
    protected int mRealWidth;
    protected int mTextColor;
    protected int mTextOffset;
    protected int mTextSize;
    protected int mUnReachedBarColor;
    protected int mUnReachedProgressBarHeight;

    public BaseProgressBar(Context context) {
        super(context);
        this.mPaint = new Paint();
        this.mIfDrawText = false;
    }

    public BaseProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mPaint = new Paint();
        this.mIfDrawText = false;
    }

    public BaseProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mPaint = new Paint();
        this.mIfDrawText = false;
        initData(context);
        obtainStyledAttributes(attrs);
        this.mPaint.setTextSize(this.mTextSize);
        this.mPaint.setColor(this.mTextColor);
    }

    private void initData(Context context) {
        DEFAULT_TEXT_COLOR = context.getResources().getColor(R.color.color_dc2323);
        DEFAULT_COLOR_UNREACHED_COLOR = context.getResources().getColor(R.color.color_333333);
        this.mTextColor = DEFAULT_TEXT_COLOR;
        this.mTextSize = sp2px(10);
        this.mTextOffset = DimenUtil.dp2px(context, 10.0f);
        this.mReachedProgressBarHeight = DimenUtil.dp2px(context, 2.0f);
        this.mReachedBarColor = DEFAULT_TEXT_COLOR;
        this.mUnReachedBarColor = DEFAULT_COLOR_UNREACHED_COLOR;
        this.mUnReachedProgressBarHeight = DimenUtil.dp2px(context, 2.0f);
    }

    private void obtainStyledAttributes(AttributeSet attrs) {
        TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.BaseProgressBar);
        this.mTextColor = attributes.getColor(R.styleable.BaseProgressBar_base_progress_text_color, DEFAULT_TEXT_COLOR);
        this.mTextSize = (int) attributes.getDimension(R.styleable.BaseProgressBar_base_progress_text_size, this.mTextSize);
        this.mReachedBarColor = attributes.getColor(R.styleable.BaseProgressBar_progress_reached_color, this.mTextColor);
        this.mUnReachedBarColor = attributes.getColor(R.styleable.BaseProgressBar_progress_unreached_color, DEFAULT_COLOR_UNREACHED_COLOR);
        this.mReachedProgressBarHeight = (int) attributes.getDimension(R.styleable.BaseProgressBar_progress_reached_bar_height, this.mReachedProgressBarHeight);
        this.mUnReachedProgressBarHeight = (int) attributes.getDimension(R.styleable.BaseProgressBar_progress_unreached_bar_height, this.mUnReachedProgressBarHeight);
        this.mTextOffset = (int) attributes.getDimension(R.styleable.BaseProgressBar_progress_text_offset, this.mTextOffset);
        if (attributes.getInt(R.styleable.BaseProgressBar_progress_text_visibility, 0) != 0) {
            this.mIfDrawText = false;
        }
        attributes.recycle();
    }

    protected int sp2px(int spValue) {
        return (int) TypedValue.applyDimension(2, spValue, getResources().getDisplayMetrics());
    }
}