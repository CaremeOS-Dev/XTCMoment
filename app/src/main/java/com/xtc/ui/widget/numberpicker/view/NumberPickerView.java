package com.xtc.ui.widget.numberpicker.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ScrollerCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.ui.widget.numberpicker.interfaces.IValueChangeListener;
import com.xtc.ui.widget.numberpicker.interfaces.IValueChangeListenerRelativeToRaw;
import com.xtc.ui.widget.numberpicker.interfaces.OnClickItemListener;
import com.xtc.ui.widget.numberpicker.interfaces.OnScrollListener;
import com.xtc.utils.ui.BitmapUtil;

/** 可滚动的数字/文本选择器（滚轮控件）。 */
public class NumberPickerView extends View {
    private static final boolean DEFAULT_CURRENT_ITEM_INDEX_EFFECT = false;
    private static final int DEFAULT_DIVIDER_HEIGHT = 2;
    private static final int DEFAULT_DIVIDER_MARGIN_HORIZONTAL = 0;
    private static final int DEFAULT_INTERVAL_REVISE_DURATION = 300;
    private static final int DEFAULT_ITEM_PADDING_DP_H = 5;
    private static final int DEFAULT_ITEM_PADDING_DP_V = 2;
    private static final int DEFAULT_MARGIN_END_OF_HINT_DP = 8;
    private static final int DEFAULT_MARGIN_START_OF_HINT_DP = 8;
    private static final int DEFAULT_MAX_SCROLL_BY_INDEX_DURATION = 600;
    private static final int DEFAULT_MIN_SCROLL_BY_INDEX_DURATION = 300;
    private static final boolean DEFAULT_RESPOND_CHANGE_IN_MAIN_THREAD = true;
    private static final boolean DEFAULT_RESPOND_CHANGE_ON_DETACH = false;
    private static final boolean DEFAULT_SHOW_CENTER_BOTTOM_SHADOW = false;
    private static final int DEFAULT_SHOW_COUNT = 3;
    private static final boolean DEFAULT_SHOW_DIVIDER = true;
    private static final int DEFAULT_TEXT_SIZE_HINT_SP = 14;
    private static final int DEFAULT_TEXT_SIZE_NORMAL_SP = 14;
    private static final int DEFAULT_TEXT_SIZE_SELECTED_SP = 16;
    private static final boolean DEFAULT_WRAP_SELECTOR_WHEEL = true;
    private static final int HANDLER_INTERVAL_REFRESH = 30;
    private static final int HANDLER_WHAT_LISTENER_VALUE_CHANGED = 2;
    private static final int HANDLER_WHAT_REFRESH = 1;
    private static final int HANDLER_WHAT_REQUEST_LAYOUT = 3;
    private static final String TAG = NumberPickerView.class.getSimpleName();
    private Context context;
    private float currY;
    private float dividerY0;
    private float dividerY1;
    private float downY;
    private float downYGlobal;
    private String mAlterHint;
    private CharSequence[] mAlterTextArrayWithMeasureHint;
    private CharSequence[] mAlterTextArrayWithoutMeasureHint;
    private int mCurrDrawFirstItemIndex;
    private int mCurrDrawFirstItemY;
    private int mCurrDrawGlobalY;
    private boolean mCurrentItemIndexEffect;
    private String[] mDisplayedValues;
    private int mDividerColor;
    private int mDividerHeight;
    private int mDividerIndex0;
    private int mDividerIndex1;
    private int mDividerMarginL;
    private int mDividerMarginR;
    private String mEmptyItemHint;
    private boolean mFlagMayPress;
    private float mFriction;
    private Handler mHandlerInMainThread;
    private Handler mHandlerInNewThread;
    private HandlerThread mHandlerThread;
    private boolean mHasInit;
    private String mHintText;
    private int mImagePaddingHorizontalNormal;
    private int mImagePaddingHorizontalSelected;
    private int mItemHeight;
    private int mItemPaddingHorizontal;
    private int mItemPaddingVertical;
    private int mMarginEndOfHint;
    private int mMarginStartOfHint;
    private int mMaxHeightOfDisplayedValues;
    private int mMaxShowIndex;
    private int mMaxValue;
    private int mMaxWidthOfAlterArrayWithMeasureHint;
    private int mMaxWidthOfAlterArrayWithoutMeasureHint;
    private int mMaxWidthOfDisplayedValues;
    private int mMinShowIndex;
    private int mMinValue;
    private int mMiniVelocityFling;
    private Bitmap mNormalBitmap;
    private int mNotWrapLimitYBottom;
    private int mNotWrapLimitYTop;
    private OnScrollListener mOnScrollListener;
    private IValueChangeListener mOnValueChangeListener;
    private IValueChangeListenerRelativeToRaw mOnValueChangeListenerRaw;
    private Paint mPaintDivider;
    private Paint mPaintHint;
    private Paint mPaintText;
    private boolean mPendingWrapToLinear;
    private int mPrevPickedIndex;
    private boolean mRespondChangeInMainThread;
    private boolean mRespondChangeOnDetach;
    private int mScaledTouchSlop;
    private int mScrollState;
    private ScrollerCompat mScroller;
    private Bitmap mSelectedBitmap;
    private boolean mShowCenterBottomShadow;
    private int mShowCount;
    private boolean mShowDivider;
    private int mSpecModeH;
    private int mSpecModeW;
    private int mTextColorHint;
    private int mTextColorNormal;
    private int mTextColorSelected;
    private int mTextSizeHint;
    private float mTextSizeHintCenterYOffset;
    private int mTextSizeNormal;
    private float mTextSizeNormalCenterYOffset;
    private int mTextSizeSelected;
    private float mTextSizeSelectedCenterYOffset;
    private VelocityTracker mVelocityTracker;
    private float mViewCenterX;
    private int mViewHeight;
    private int mViewWidth;
    private int mWidthOfAlterHint;
    private int mWidthOfHintText;
    private boolean mWrapSelectorWheel;
    private boolean mWrapSelectorWheelCheck;
    private int newValue;
    private OnClickItemListener onClickItemListener;

    private int getEvaluateColor(float fraction, int startColor, int endColor) {
        int startAlpha = (startColor & (-16777216)) >>> 24;
        int startRed = (startColor & 16711680) >>> 16;
        int startGreen = (startColor & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >>> 8;
        int startBlue = (startColor & 255) >>> 0;
        return ((int) (startBlue + ((((endColor & 255) >>> 0) - startBlue) * fraction))) | (((int) (startAlpha + (((((-16777216) & endColor) >>> 24) - startAlpha) * fraction))) << 24) | (((int) (startRed + ((((16711680 & endColor) >>> 16) - startRed) * fraction))) << 16) | (((int) (startGreen + ((((65280 & endColor) >>> 8) - startGreen) * fraction))) << 8);
    }

    private float getEvaluateSize(float fraction, float startSize, float endSize) {
        return startSize + ((endSize - startSize) * fraction);
    }

    public NumberPickerView(Context context) {
        this(context, null);
    }

    public NumberPickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mTextSizeSelected = 0;
        this.mTextSizeHint = 0;
        this.mWidthOfHintText = 0;
        this.mWidthOfAlterHint = 0;
        this.mMarginStartOfHint = 0;
        this.mMarginEndOfHint = 0;
        this.mItemPaddingVertical = 0;
        this.mItemPaddingHorizontal = 0;
        this.mMinValue = 0;
        this.mMaxValue = 0;
        this.mDividerIndex0 = 0;
        this.mDividerIndex1 = 0;
        this.mMaxWidthOfDisplayedValues = 0;
        this.mMaxHeightOfDisplayedValues = 0;
        this.mMaxWidthOfAlterArrayWithMeasureHint = 0;
        this.mMaxWidthOfAlterArrayWithoutMeasureHint = 0;
        this.mPrevPickedIndex = 0;
        this.mMiniVelocityFling = 150;
        this.mScaledTouchSlop = 8;
        this.mFriction = 1.0f;
        this.mTextSizeNormalCenterYOffset = 0.0f;
        this.mTextSizeSelectedCenterYOffset = 0.0f;
        this.mTextSizeHintCenterYOffset = 0.0f;
        this.mCurrentItemIndexEffect = false;
        this.mHasInit = false;
        this.mWrapSelectorWheelCheck = true;
        this.mPendingWrapToLinear = false;
        this.mRespondChangeOnDetach = false;
        this.mRespondChangeInMainThread = true;
        this.mPaintDivider = new Paint();
        this.mPaintText = new Paint();
        this.mPaintHint = new Paint();
        this.mScrollState = 0;
        this.mImagePaddingHorizontalSelected = 0;
        this.mImagePaddingHorizontalNormal = 0;
        this.downYGlobal = 0.0f;
        this.downY = 0.0f;
        this.currY = 0.0f;
        this.mFlagMayPress = false;
        this.mCurrDrawFirstItemIndex = 0;
        this.mCurrDrawFirstItemY = 0;
        this.mCurrDrawGlobalY = 0;
        this.mSpecModeW = 0;
        this.mSpecModeH = 0;
        if (context == null) {
            LogUtil.e(TAG, "context is null.");
            return;
        }
        this.context = context;
        initAttr(context, attrs, defStyleAttr);
        init(context);
    }

    private void initAttr(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.NumberPickerView, defStyleAttr, 0);
        this.mShowCount = attributes.getInt(R.styleable.NumberPickerView_npv_ShowCount, 3);
        this.mDividerColor = attributes.getColor(R.styleable.NumberPickerView_npv_DividerColor, getResources().getColor(R.color.color_f56313));
        this.mDividerHeight = attributes.getDimensionPixelSize(R.styleable.NumberPickerView_npv_DividerHeight, 2);
        this.mDividerMarginL = attributes.getDimensionPixelSize(R.styleable.NumberPickerView_npv_DividerMarginLeft, 0);
        this.mDividerMarginR = attributes.getDimensionPixelSize(R.styleable.NumberPickerView_npv_DividerMarginRight, 0);
        this.mDisplayedValues = convertCharSequenceArrayToStringArray(attributes.getTextArray(R.styleable.NumberPickerView_npv_TextArray));
        this.mTextColorNormal = attributes.getColor(R.styleable.NumberPickerView_npv_TextColorNormal, getResources().getColor(R.color.color_333333));
        this.mTextColorSelected = attributes.getColor(R.styleable.NumberPickerView_npv_TextColorSelected, getResources().getColor(R.color.color_f56313));
        this.mTextColorHint = attributes.getColor(R.styleable.NumberPickerView_npv_TextColorHint, getResources().getColor(R.color.color_f56313));
        this.mTextSizeNormal = attributes.getDimensionPixelSize(R.styleable.NumberPickerView_npv_TextSizeNormal, 14);
        this.mTextSizeSelected = attributes.getDimensionPixelSize(R.styleable.NumberPickerView_npv_TextSizeSelected, 16);
        this.mTextSizeHint = attributes.getDimensionPixelSize(R.styleable.NumberPickerView_npv_TextSizeHint, 14);
        this.mMinShowIndex = attributes.getInteger(R.styleable.NumberPickerView_npv_MinValue, 0);
        this.mMaxShowIndex = attributes.getInteger(R.styleable.NumberPickerView_npv_MaxValue, 0);
        this.mWrapSelectorWheel = attributes.getBoolean(R.styleable.NumberPickerView_npv_WrapSelectorWheel, true);
        this.mShowDivider = attributes.getBoolean(R.styleable.NumberPickerView_npv_ShowDivider, true);
        this.mHintText = attributes.getString(R.styleable.NumberPickerView_npv_HintText);
        this.mAlterHint = attributes.getString(R.styleable.NumberPickerView_npv_AlternativeHint);
        this.mEmptyItemHint = attributes.getString(R.styleable.NumberPickerView_npv_EmptyItemHint);
        this.mMarginStartOfHint = attributes.getDimensionPixelSize(R.styleable.NumberPickerView_npv_MarginStartOfHint, 8);
        this.mMarginEndOfHint = attributes.getDimensionPixelSize(R.styleable.NumberPickerView_npv_MarginEndOfHint, 8);
        this.mItemPaddingVertical = attributes.getDimensionPixelSize(R.styleable.NumberPickerView_npv_ItemPaddingVertical, 2);
        this.mItemPaddingHorizontal = attributes.getDimensionPixelSize(R.styleable.NumberPickerView_npv_ItemPaddingHorizontal, 5);
        this.mAlterTextArrayWithMeasureHint = attributes.getTextArray(R.styleable.NumberPickerView_npv_AlternativeTextArrayWithMeasureHint);
        this.mAlterTextArrayWithoutMeasureHint = attributes.getTextArray(R.styleable.NumberPickerView_npv_AlternativeTextArrayWithoutMeasureHint);
        this.mRespondChangeOnDetach = attributes.getBoolean(R.styleable.NumberPickerView_npv_RespondChangeOnDetached, false);
        this.mRespondChangeInMainThread = attributes.getBoolean(R.styleable.NumberPickerView_npv_RespondChangeInMainThread, true);
        Drawable normalDrawable = attributes.getDrawable(R.styleable.NumberPickerView_npv_ImageViewNormal);
        if (normalDrawable != null) {
            this.mNormalBitmap = BitmapUtil.drawableToBitmap(normalDrawable);
        } else {
            this.mNormalBitmap = null;
        }
        Drawable selectedDrawable = attributes.getDrawable(R.styleable.NumberPickerView_npv_ImageViewSelected);
        if (selectedDrawable != null) {
            this.mSelectedBitmap = BitmapUtil.drawableToBitmap(selectedDrawable);
        } else {
            this.mSelectedBitmap = null;
        }
        this.mImagePaddingHorizontalSelected = attributes.getDimensionPixelSize(R.styleable.NumberPickerView_npv_ImagePaddingHorizontalSelected, 5);
        this.mImagePaddingHorizontalNormal = attributes.getDimensionPixelSize(R.styleable.NumberPickerView_npv_ImagePaddingHorizontalNormal, 5);
        this.mShowCenterBottomShadow = attributes.getBoolean(R.styleable.NumberPickerView_npv_CenterBottomShadow, false);
        attributes.recycle();
    }

    private void init(Context context) {
        this.mScroller = ScrollerCompat.create(context);
        this.mMiniVelocityFling = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();
        this.mScaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        if (this.mTextSizeNormal <= 0) {
            this.mTextSizeNormal = 14;
        }
        if (this.mTextSizeSelected <= 0) {
            this.mTextSizeSelected = 16;
        }
        if (this.mTextSizeHint <= 0) {
            this.mTextSizeHint = 14;
        }
        if (this.mMarginStartOfHint <= 0) {
            this.mMarginStartOfHint = 8;
        }
        if (this.mMarginEndOfHint <= 0) {
            this.mMarginEndOfHint = 8;
        }
        this.mPaintDivider.setColor(this.mDividerColor);
        this.mPaintDivider.setAntiAlias(true);
        this.mPaintDivider.setStyle(Paint.Style.STROKE);
        this.mPaintDivider.setStrokeWidth(this.mDividerHeight);
        this.mPaintText.setColor(this.mTextColorNormal);
        this.mPaintText.setAntiAlias(true);
        this.mPaintText.setTextAlign(Paint.Align.CENTER);
        this.mPaintHint.setColor(this.mTextColorHint);
        this.mPaintHint.setAntiAlias(true);
        this.mPaintHint.setTextAlign(Paint.Align.CENTER);
        this.mPaintHint.setTextSize(this.mTextSizeHint);
        int showCount = this.mShowCount;
        if (showCount % 2 == 0) {
            this.mShowCount = showCount + 1;
        }
        if (this.mMinShowIndex < 0 || this.mMaxShowIndex < 1) {
            updateValueForInit();
        }
        initHandler();
    }

    private void updateValueForInit() {
        inflateDisplayedValuesIfNull();
        updateWrapStateByContent();
        if (this.mMinShowIndex < 0) {
            this.mMinShowIndex = 0;
        }
        if (this.mMaxShowIndex < 1) {
            this.mMaxShowIndex = this.mDisplayedValues.length - 1;
        }
        setMinAndMaxShowIndex(this.mMinShowIndex, this.mMaxShowIndex, false);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mItemHeight == 0) {
            return true;
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(event);
        this.currY = event.getY();
        int action = event.getAction();
        if (action == 0) {
            this.mFlagMayPress = true;
            this.mHandlerInNewThread.removeMessages(1);
            stopScrolling();
            this.downY = this.currY;
            this.downYGlobal = this.mCurrDrawGlobalY;
            onScrollStateChange(0);
        } else if (action != 1) {
            if (action == 2) {
                float deltaY = this.downY - this.currY;
                if (this.mFlagMayPress) {
                    int touchSlop = this.mScaledTouchSlop;
                    if ((-touchSlop) >= deltaY || deltaY >= touchSlop) {
                        this.mFlagMayPress = false;
                        this.mCurrDrawGlobalY = limitY((int) (this.downYGlobal + deltaY));
                        calculateFirstItemParameterByGlobalY();
                        invalidate();
                    }
                } else {
                    this.mFlagMayPress = false;
                    this.mCurrDrawGlobalY = limitY((int) (this.downYGlobal + deltaY));
                    calculateFirstItemParameterByGlobalY();
                    invalidate();
                }
                onScrollStateChange(1);
            } else if (action == 3) {
                this.downYGlobal = this.mCurrDrawGlobalY;
                stopScrolling();
                this.mHandlerInNewThread.sendMessageDelayed(getMsg(1), 0L);
            }
        } else if (this.mFlagMayPress) {
            click(event);
        } else {
            VelocityTracker velocityTracker = this.mVelocityTracker;
            velocityTracker.computeCurrentVelocity(1000);
            int yVelocity = (int) (velocityTracker.getYVelocity() * this.mFriction);
            if (Math.abs(yVelocity) > this.mMiniVelocityFling) {
                this.mScroller.fling(0, this.mCurrDrawGlobalY, 0, -yVelocity, Integer.MIN_VALUE, Integer.MAX_VALUE,
                        limitY(Integer.MIN_VALUE), limitY(Integer.MAX_VALUE));
                invalidate();
                onScrollStateChange(2);
            }
            this.mHandlerInNewThread.sendMessageDelayed(getMsg(1), 0L);
            releaseVelocityTracker();
        }
        return true;
    }

    private void initHandler() {
        this.mHandlerThread = new HandlerThread("HandlerThread-For-Refreshing");
        this.mHandlerThread.start();
        this.mHandlerInNewThread = new Handler(this.mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message message) {
                int willPickIndex;
                int duration;
                int what = message.what;
                if (what != 1) {
                    if (what != 2) {
                        return;
                    }
                    NumberPickerView.this.respondPickedValueChanged(message.arg1, message.arg2, message.obj);
                    return;
                }
                int scrollDuration = 0;
                if (!NumberPickerView.this.mScroller.isFinished()) {
                    if (NumberPickerView.this.mScrollState == 0) {
                        NumberPickerView.this.onScrollStateChange(1);
                    }
                    NumberPickerView.this.mHandlerInNewThread.sendMessageDelayed(
                            NumberPickerView.this.getMsg(1, 0, 0, message.obj), 30L);
                    return;
                }
                if (NumberPickerView.this.mCurrDrawFirstItemY != 0) {
                    if (NumberPickerView.this.mScrollState == 0) {
                        NumberPickerView.this.onScrollStateChange(1);
                    }
                    if (NumberPickerView.this.mCurrDrawFirstItemY < (-NumberPickerView.this.mItemHeight) / 2) {
                        duration = (int) (((NumberPickerView.this.mItemHeight + NumberPickerView.this.mCurrDrawFirstItemY) * 300.0f)
                                / NumberPickerView.this.mItemHeight);
                        NumberPickerView.this.mScroller.startScroll(0, NumberPickerView.this.mCurrDrawGlobalY, 0,
                                NumberPickerView.this.mCurrDrawFirstItemY + NumberPickerView.this.mItemHeight, duration * 2);
                        NumberPickerView picker = NumberPickerView.this;
                        willPickIndex = picker.getWillPickIndexByGlobalY(picker.mCurrDrawGlobalY
                                + NumberPickerView.this.mItemHeight + NumberPickerView.this.mCurrDrawFirstItemY);
                    } else {
                        duration = (int) (((-NumberPickerView.this.mCurrDrawFirstItemY) * 300.0f)
                                / NumberPickerView.this.mItemHeight);
                        NumberPickerView.this.mScroller.startScroll(0, NumberPickerView.this.mCurrDrawGlobalY, 0,
                                NumberPickerView.this.mCurrDrawFirstItemY, duration * 2);
                        NumberPickerView picker = NumberPickerView.this;
                        willPickIndex = picker.getWillPickIndexByGlobalY(picker.mCurrDrawGlobalY
                                + NumberPickerView.this.mCurrDrawFirstItemY);
                    }
                    scrollDuration = duration;
                    NumberPickerView.this.postInvalidate();
                } else {
                    NumberPickerView.this.onScrollStateChange(0);
                    NumberPickerView picker = NumberPickerView.this;
                    willPickIndex = picker.getWillPickIndexByGlobalY(picker.mCurrDrawGlobalY);
                }
                NumberPickerView picker = NumberPickerView.this;
                Message valueChangedMsg = picker.getMsg(2, picker.mPrevPickedIndex, willPickIndex, message.obj);
                if (NumberPickerView.this.mRespondChangeInMainThread) {
                    NumberPickerView.this.mHandlerInMainThread.sendMessageDelayed(valueChangedMsg, scrollDuration * 2);
                } else {
                    NumberPickerView.this.mHandlerInNewThread.sendMessageDelayed(valueChangedMsg, scrollDuration * 2);
                }
            }
        };
        this.mHandlerInMainThread = new Handler() {
            @Override
            public void handleMessage(Message message) {
                super.handleMessage(message);
                int what = message.what;
                if (what == 2) {
                    NumberPickerView.this.respondPickedValueChanged(message.arg1, message.arg2, message.obj);
                } else if (what == 3) {
                    NumberPickerView.this.requestLayout();
                }
            }
        };
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        HandlerThread handlerThread = this.mHandlerThread;
        if (handlerThread == null || !handlerThread.isAlive()) {
            initHandler();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        updateMaxWHOfDisplayedValues(false);
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        int value;
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        this.mViewWidth = width;
        this.mViewHeight = height;
        this.mItemHeight = this.mViewHeight / this.mShowCount;
        this.mViewCenterX = ((this.mViewWidth - getPaddingLeft()) - getPaddingRight()) / 2.0f;
        boolean wrap = false;
        if (getOneRecycleSize() <= 1) {
            value = 0;
        } else if (this.mHasInit) {
            value = getValue() - this.mMinValue;
        } else if (this.mCurrentItemIndexEffect) {
            value = this.mCurrDrawFirstItemIndex + ((this.mShowCount - 1) / 2);
        } else {
            value = 0;
        }
        if (this.mWrapSelectorWheel && this.mWrapSelectorWheelCheck) {
            wrap = true;
        }
        correctPositionByDefaultValue(value, wrap);
        updateFontAttr();
        updateNotWrapYLimit();
        updateDividerAttr();
        this.mHasInit = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawContent(canvas);
        drawLine(canvas);
        drawHint(canvas);
    }

    private void drawContent(Canvas canvas) {
        float selectedFraction;
        float itemColorFraction;
        float itemTextSize;
        float itemTextSizeCenterYOffset;
        int itemColor;
        Bitmap itemBitmap;
        int itemBitmapPadding;
        float textCenterX;
        float textOffsetX;
        float bitmapY;
        if (this.mShowCenterBottomShadow) {
            int itemHeight = this.mItemHeight;
            Rect shadowRect = new Rect(0, itemHeight, this.mViewWidth, itemHeight * 2);
            Paint shadowPaint = new Paint();
            shadowPaint.setColor(this.context.getResources().getColor(R.color.color_f5f5f5));
            shadowPaint.setAlpha(255);
            canvas.drawRect(shadowRect, shadowPaint);
        }
        int index = 0;
        float previousSelectedFraction = 0.0f;
        while (index < this.mShowCount + 1) {
            float itemTop = this.mCurrDrawFirstItemY + (this.mItemHeight * index);
            int rawIndex = getIndexByRawIndex(this.mCurrDrawFirstItemIndex + index, getOneRecycleSize(),
                    this.mWrapSelectorWheel && this.mWrapSelectorWheelCheck);
            int showCount = this.mShowCount;
            if (index == showCount / 2) {
                int itemHeight = this.mItemHeight;
                selectedFraction = (this.mCurrDrawFirstItemY + itemHeight) / itemHeight;
                itemColor = getEvaluateColor(selectedFraction, this.mTextColorNormal, this.mTextColorSelected);
                itemTextSize = getEvaluateSize(selectedFraction, this.mTextSizeNormal, this.mTextSizeSelected);
                itemTextSizeCenterYOffset = getEvaluateSize(selectedFraction, this.mTextSizeNormalCenterYOffset, this.mTextSizeSelectedCenterYOffset);
            } else if (index == (showCount / 2) + 1) {
                float inverseFraction = 1.0f - previousSelectedFraction;
                int evaluatedColor = getEvaluateColor(inverseFraction, this.mTextColorNormal, this.mTextColorSelected);
                float evaluatedSize = getEvaluateSize(inverseFraction, this.mTextSizeNormal, this.mTextSizeSelected);
                float evaluatedCenterYOffset = getEvaluateSize(inverseFraction, this.mTextSizeNormalCenterYOffset, this.mTextSizeSelectedCenterYOffset);
                selectedFraction = previousSelectedFraction;
                itemColor = evaluatedColor;
                itemTextSize = evaluatedSize;
                itemTextSizeCenterYOffset = evaluatedCenterYOffset;
            } else {
                int normalColor = this.mTextColorNormal;
                itemTextSize = this.mTextSizeNormal;
                itemTextSizeCenterYOffset = this.mTextSizeNormalCenterYOffset;
                selectedFraction = previousSelectedFraction;
                itemColor = normalColor;
            }
            this.mPaintText.setColor(itemColor);
            this.mPaintText.setTextSize(itemTextSize);
            if (index == this.mShowCount / 2) {
                itemBitmap = this.mSelectedBitmap;
                itemBitmapPadding = this.mImagePaddingHorizontalSelected;
            } else {
                itemBitmap = this.mNormalBitmap;
                itemBitmapPadding = this.mImagePaddingHorizontalNormal;
            }
            float textBaselineY = (this.mItemHeight / 2) + itemTop + itemTextSizeCenterYOffset;
            if (rawIndex >= 0 && rawIndex < getOneRecycleSize()) {
                if (itemBitmap != null) {
                    textCenterX = this.mViewCenterX + (itemBitmapPadding / 2);
                    textOffsetX = itemBitmap.getWidth() / 2;
                } else {
                    textCenterX = this.mViewCenterX;
                    textOffsetX = itemBitmapPadding / 2;
                }
                float textX = textCenterX + textOffsetX;
                canvas.drawText(this.mDisplayedValues[this.mMinShowIndex + rawIndex], textX, textBaselineY, this.mPaintText);
                float textHalfWidth = this.mPaintText.measureText(this.mDisplayedValues[rawIndex + this.mMinShowIndex]) / 2.0f;
                float bitmapX;
                if (itemBitmap != null) {
                    bitmapX = ((textX - itemBitmap.getWidth()) - itemBitmapPadding) - textHalfWidth;
                    bitmapY = (itemTop + (this.mItemHeight / 2)) - (itemBitmap.getHeight() / 2);
                } else {
                    bitmapX = (textX - itemBitmapPadding) - textHalfWidth;
                    bitmapY = itemTop + (this.mItemHeight / 2);
                }
                if (itemBitmap != null) {
                    canvas.drawBitmap(itemBitmap, bitmapX, bitmapY, (Paint) null);
                }
            } else if (!TextUtils.isEmpty(this.mEmptyItemHint)) {
                canvas.drawText(this.mEmptyItemHint, this.mViewCenterX, textBaselineY, this.mPaintText);
            }
            index++;
            previousSelectedFraction = selectedFraction;
        }
    }

    private void drawLine(Canvas canvas) {
        if (this.mShowDivider) {
            canvas.drawLine(getPaddingLeft() + this.mDividerMarginL, this.dividerY0,
                    (this.mViewWidth - getPaddingRight()) - this.mDividerMarginR, this.dividerY0, this.mPaintDivider);
            canvas.drawLine(getPaddingLeft() + this.mDividerMarginL, this.dividerY1,
                    (this.mViewWidth - getPaddingRight()) - this.mDividerMarginR, this.dividerY1, this.mPaintDivider);
        }
    }

    private void drawHint(Canvas canvas) {
        if (TextUtils.isEmpty(this.mHintText)) {
            return;
        }
        canvas.drawText(this.mHintText,
                this.mViewCenterX + ((this.mMaxWidthOfDisplayedValues + this.mWidthOfHintText) / 2) + this.mMarginStartOfHint,
                ((this.dividerY0 + this.dividerY1) / 2.0f) + this.mTextSizeHintCenterYOffset, this.mPaintHint);
        Bitmap selectedBitmap = this.mSelectedBitmap;
        if (selectedBitmap != null) {
            canvas.drawBitmap(selectedBitmap,
                    this.mViewCenterX + ((this.mMaxWidthOfDisplayedValues + this.mWidthOfHintText) / 2) + this.mMarginStartOfHint,
                    (this.dividerY0 + this.dividerY1) / 2.0f, (Paint) null);
        }
    }

    @Override
    public void computeScroll() {
        if (this.mItemHeight != 0 && this.mScroller.computeScrollOffset()) {
            this.mCurrDrawGlobalY = this.mScroller.getCurrY();
            calculateFirstItemParameterByGlobalY();
            postInvalidate();
        }
    }
    public void setMinAndMaxShowIndex(int minShowIndex, int maxShowIndex, boolean needUpdate) {
        if (minShowIndex > maxShowIndex) {
            throw new IllegalArgumentException("设置的最小值应该小于最大值, minShowIndex:" + minShowIndex + ", maxShowIndex: " + maxShowIndex + ".");
        }
        String[] displayedValues = this.mDisplayedValues;
        if (displayedValues == null) {
            throw new IllegalArgumentException("用于在滚轮中显示的值不能为空.");
        }
        if (minShowIndex < 0) {
            throw new IllegalArgumentException("设置的最小值不能小于0, 当前的 minShowIndex：" + minShowIndex);
        }
        if (minShowIndex > displayedValues.length - 1) {
            throw new IllegalArgumentException("设置的最小值不能大于(mDisplayedValues.length - 1), 当前的 (mDisplayedValues.length - 1): "
                    + (this.mDisplayedValues.length - 1) + ",minShowIndex:" + minShowIndex);
        }
        if (maxShowIndex < 0) {
            throw new IllegalArgumentException("设置的最大值不能小于0,当前的 maxShowIndex：" + maxShowIndex);
        }
        if (maxShowIndex > displayedValues.length - 1) {
            throw new IllegalArgumentException("设置的最大值不能大于(mDisplayedValues.length - 1), 当前的(mDisplayedValues.length - 1):"
                    + (this.mDisplayedValues.length - 1) + ",maxShowIndex:" + maxShowIndex);
        }
        this.mMinShowIndex = minShowIndex;
        this.mMaxShowIndex = maxShowIndex;
        if (needUpdate) {
            this.mPrevPickedIndex = this.mMinShowIndex;
            correctPositionByDefaultValue(0, this.mWrapSelectorWheel && this.mWrapSelectorWheelCheck);
            postInvalidate();
        }
    }

    private void inflateDisplayedValuesIfNull() {
        if (this.mDisplayedValues == null) {
            this.mDisplayedValues = new String[1];
            this.mDisplayedValues[0] = "0";
        }
    }

    private String[] convertCharSequenceArrayToStringArray(CharSequence[] charSequences) {
        if (charSequences == null) {
            return null;
        }
        String[] result = new String[charSequences.length];
        for (int index = 0; index < charSequences.length; index++) {
            result[index] = charSequences[index].toString();
        }
        return result;
    }

    private void respondPickedValueChanged(int previousIndex, int pickedIndex, Object payload) {
        onScrollStateChange(0);
        if (previousIndex != pickedIndex
                && (payload == null || !(payload instanceof Boolean) || ((Boolean) payload).booleanValue())) {
            IValueChangeListener valueChangeListener = this.mOnValueChangeListener;
            if (valueChangeListener != null) {
                int minValue = this.mMinValue;
                valueChangeListener.onValueChange(this, previousIndex + minValue, minValue + pickedIndex);
                LogUtil.d(TAG, "旧的值和新的值" + (this.mMinValue + pickedIndex) + (this.mMinValue + previousIndex));
                this.newValue = this.mMinValue + pickedIndex;
            }
            IValueChangeListenerRelativeToRaw valueChangeListenerRaw = this.mOnValueChangeListenerRaw;
            if (valueChangeListenerRaw != null) {
                valueChangeListenerRaw.onValueChangeRelativeToRaw(this, previousIndex, pickedIndex, this.mDisplayedValues);
            }
        }
        this.mPrevPickedIndex = pickedIndex;
        if (this.mPendingWrapToLinear) {
            this.mPendingWrapToLinear = false;
            internalSetWrapToLinear();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mHandlerThread.quit();
        if (this.mItemHeight == 0) {
            return;
        }
        if (!this.mScroller.isFinished()) {
            this.mScroller.abortAnimation();
            this.mCurrDrawGlobalY = this.mScroller.getCurrY();
            calculateFirstItemParameterByGlobalY();
            int firstItemY = this.mCurrDrawFirstItemY;
            if (firstItemY != 0) {
                int itemHeight = this.mItemHeight;
                if (firstItemY < (-itemHeight) / 2) {
                    this.mCurrDrawGlobalY = this.mCurrDrawGlobalY + itemHeight + firstItemY;
                } else {
                    this.mCurrDrawGlobalY += firstItemY;
                }
                calculateFirstItemParameterByGlobalY();
            }
            onScrollStateChange(0);
        }
        int willPickIndex = getWillPickIndexByGlobalY(this.mCurrDrawGlobalY);
        int previousIndex = this.mPrevPickedIndex;
        if (willPickIndex != previousIndex && this.mRespondChangeOnDetach) {
            try {
                if (this.mOnValueChangeListener != null) {
                    this.mOnValueChangeListener.onValueChange(this, previousIndex + this.mMinValue, this.mMinValue + willPickIndex);
                    LogUtil.d("test", "currPickedIndex + mMinValue--->>" + (this.mMinValue + willPickIndex));
                }
                if (this.mOnValueChangeListenerRaw != null) {
                    this.mOnValueChangeListenerRaw.onValueChangeRelativeToRaw(this, this.mPrevPickedIndex, willPickIndex, this.mDisplayedValues);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.mPrevPickedIndex = willPickIndex;
    }

    public int getOneRecycleSize() {
        return (this.mMaxShowIndex - this.mMinShowIndex) + 1;
    }

    public int getRawContentSize() {
        String[] displayedValues = this.mDisplayedValues;
        if (displayedValues != null) {
            return displayedValues.length;
        }
        return 0;
    }

    public void setDisplayedValuesAndPickedIndex(String[] newDisplayedValues, int pickedIndex, boolean needUpdate) {
        stopScrolling();
        if (newDisplayedValues == null) {
            throw new IllegalArgumentException("newDisplayedValues should not be null.");
        }
        if (pickedIndex < 0) {
            throw new IllegalArgumentException("pickedIndex should not be negative, now pickedIndex is " + pickedIndex);
        }
        updateContent(newDisplayedValues);
        updateMaxWHOfDisplayedValues(true);
        updateNotWrapYLimit();
        updateValue();
        this.mPrevPickedIndex = this.mMinShowIndex + pickedIndex;
        correctPositionByDefaultValue(pickedIndex, this.mWrapSelectorWheel && this.mWrapSelectorWheelCheck);
        if (needUpdate) {
            this.mHandlerInNewThread.sendMessageDelayed(getMsg(1), 0L);
            postInvalidate();
        }
    }

    public void setDisplayedValues(String[] newDisplayedValues, boolean needUpdate) {
        setDisplayedValuesAndPickedIndex(newDisplayedValues, 0, needUpdate);
    }

    public void setDisplayedValues(String[] newDisplayedValues) {
        stopRefreshing();
        stopScrolling();
        if (newDisplayedValues == null) {
            throw new IllegalArgumentException("newDisplayedValues should not be null.");
        }
        if ((this.mMaxValue - this.mMinValue) + 1 > newDisplayedValues.length) {
            throw new IllegalArgumentException("mMaxValue - mMinValue + 1 should not be greater than mDisplayedValues.length, now ((mMaxValue - mMinValue + 1) is "
                    + ((this.mMaxValue - this.mMinValue) + 1) + " newDisplayedValues.length is " + newDisplayedValues.length
                    + ", you need to set MaxValue and MinValue before setDisplayedValues(String[])");
        }
        updateContent(newDisplayedValues);
        updateMaxWHOfDisplayedValues(true);
        this.mPrevPickedIndex = this.mMinShowIndex + 0;
        correctPositionByDefaultValue(0, this.mWrapSelectorWheel && this.mWrapSelectorWheelCheck);
        postInvalidate();
        this.mHandlerInMainThread.sendEmptyMessage(3);
    }

    public String[] getDisplayedValues() {
        return this.mDisplayedValues;
    }

    public void setWrapSelectorWheel(boolean wrapSelectorWheel) {
        if (this.mWrapSelectorWheel != wrapSelectorWheel) {
            if (!wrapSelectorWheel) {
                if (this.mScrollState == 0) {
                    internalSetWrapToLinear();
                    return;
                } else {
                    this.mPendingWrapToLinear = true;
                    return;
                }
            }
            this.mWrapSelectorWheel = wrapSelectorWheel;
            updateWrapStateByContent();
            postInvalidate();
        }
    }

    public void smoothScrollToValue(int value) {
        smoothScrollToValue(getValue(), value, true);
    }

    public void smoothScrollToValue(int value, boolean needUpdate) {
        smoothScrollToValue(getValue(), value, needUpdate);
    }

    public void smoothScrollToValue(int fromValue, int toValue) {
        smoothScrollToValue(fromValue, toValue, true);
    }

    public void smoothScrollToValue(int fromValue, int toValue, boolean needUpdate) {
        int deltaIndex;
        boolean wrap = this.mWrapSelectorWheel && this.mWrapSelectorWheelCheck;
        int refinedFromValue = refineValueByLimit(fromValue, this.mMinValue, this.mMaxValue, wrap);
        int refinedToValue = refineValueByLimit(toValue, this.mMinValue, this.mMaxValue, wrap);
        if (wrap) {
            deltaIndex = refinedToValue - refinedFromValue;
            int halfRecycleSize = getOneRecycleSize() / 2;
            if (deltaIndex < (-halfRecycleSize) || halfRecycleSize < deltaIndex) {
                int oneRecycleSize = getOneRecycleSize();
                deltaIndex = deltaIndex > 0 ? deltaIndex - oneRecycleSize : deltaIndex + oneRecycleSize;
            }
        } else {
            deltaIndex = refinedToValue - refinedFromValue;
        }
        setValue(refinedFromValue);
        if (refinedFromValue == refinedToValue) {
            return;
        }
        scrollByIndexSmoothly(deltaIndex, needUpdate);
    }

    public void refreshByNewDisplayedValues(String[] newDisplayedValues) {
        int minValue = getMinValue();
        int maxValue = (getMaxValue() - minValue) + 1;
        int newMaxIndex = newDisplayedValues.length - 1;
        if ((newMaxIndex - minValue) + 1 > maxValue) {
            setDisplayedValues(newDisplayedValues);
            setMaxValue(newMaxIndex);
        } else {
            setMaxValue(newMaxIndex);
            setDisplayedValues(newDisplayedValues);
        }
    }

    private void scrollByIndexSmoothly(int indexDelta) {
        scrollByIndexSmoothly(indexDelta, true);
    }

    private void scrollByIndexSmoothly(int indexDelta, boolean needUpdate) {
        int pickedIndex;
        int clampedIndex;
        int duration;
        boolean wrap = this.mWrapSelectorWheel && this.mWrapSelectorWheelCheck;
        if (!wrap) {
            pickedIndex = getPickedIndexRelativeToRaw();
            int targetIndex = pickedIndex + indexDelta;
            int limit = this.mMaxShowIndex;
            if (targetIndex > limit || targetIndex < (limit = this.mMinShowIndex)) {
                indexDelta = limit - pickedIndex;
            }
        }
        int firstItemY = this.mCurrDrawFirstItemY;
        int itemHeight = this.mItemHeight;
        if (firstItemY < (-itemHeight) / 2) {
            int adjustedFirstItemY = itemHeight + firstItemY;
            int baseDuration = (int) (((firstItemY + itemHeight) * 300.0f) / itemHeight);
            duration = indexDelta < 0 ? (-baseDuration) - (indexDelta * 300) : baseDuration + (indexDelta * 300);
            firstItemY = adjustedFirstItemY;
        } else {
            int baseDuration = (int) (((-firstItemY) * 300.0f) / itemHeight);
            duration = indexDelta < 0 ? baseDuration - (indexDelta * 300) : baseDuration + (indexDelta * 300);
        }
        int scrollDistance = firstItemY + (indexDelta * this.mItemHeight);
        int scrollDuration = duration >= 300 ? duration : 300;
        if (scrollDuration > 600) {
            scrollDuration = 600;
        }
        this.mScroller.startScroll(0, this.mCurrDrawGlobalY, 0, scrollDistance, scrollDuration);
        if (needUpdate) {
            this.mHandlerInNewThread.sendMessageDelayed(getMsg(1), scrollDuration / 4);
        } else {
            this.mHandlerInNewThread.sendMessageDelayed(getMsg(1, 0, 0, new Boolean(needUpdate)), scrollDuration / 4);
        }
        postInvalidate();
    }

    public int getMinValue() {
        return this.mMinValue;
    }

    public int getMaxValue() {
        return this.mMaxValue;
    }

    public void setMinValue(int minValue) {
        this.mMinValue = minValue;
        this.mMinShowIndex = 0;
        updateNotWrapYLimit();
    }

    public void setMaxValue(int maxValue) {
        String[] displayedValues = this.mDisplayedValues;
        if (displayedValues == null) {
            throw new NullPointerException("mDisplayedValues should not be null");
        }
        int minValue = this.mMinValue;
        if ((maxValue - minValue) + 1 > displayedValues.length) {
            throw new IllegalArgumentException("(maxValue - mMinValue + 1) should not be greater than mDisplayedValues.length now  (maxValue - mMinValue + 1) is "
                    + ((maxValue - this.mMinValue) + 1) + " and mDisplayedValues.length is " + this.mDisplayedValues.length);
        }
        this.mMaxValue = maxValue;
        int offset = this.mMaxValue - minValue;
        int minShowIndex = this.mMinShowIndex;
        this.mMaxShowIndex = offset + minShowIndex;
        setMinAndMaxShowIndex(minShowIndex, this.mMaxShowIndex);
        updateNotWrapYLimit();
    }

    public void setValue(int value) {
        int minValue = this.mMinValue;
        if (value < minValue) {
            throw new IllegalArgumentException("should not set a value less than mMinValue, value is " + value);
        }
        if (value > this.mMaxValue) {
            throw new IllegalArgumentException("should not set a value greater than mMaxValue, value is " + value);
        }
        setPickedIndexRelativeToRaw(value - minValue);
    }

    public int getValue() {
        return getPickedIndexRelativeToRaw() + this.mMinValue;
    }

    public String getContentByCurrValue() {
        return this.mDisplayedValues[getValue() - this.mMinValue];
    }

    public boolean getWrapSelectorWheel() {
        return this.mWrapSelectorWheel;
    }

    public boolean getWrapSelectorWheelAbsolutely() {
        return this.mWrapSelectorWheel && this.mWrapSelectorWheelCheck;
    }

    public void setHintText(String hintText) {
        if (isStringEqual(this.mHintText, hintText)) {
            return;
        }
        this.mHintText = hintText;
        this.mTextSizeHintCenterYOffset = getTextCenterYOffset(this.mPaintHint.getFontMetrics());
        this.mWidthOfHintText = getTextWidth(this.mHintText, this.mPaintHint);
        this.mHandlerInMainThread.sendEmptyMessage(3);
    }

    public void setPickedIndexRelativeToMin(int pickedIndexRelativeToMin) {
        if (pickedIndexRelativeToMin < 0 || pickedIndexRelativeToMin >= getOneRecycleSize()) {
            return;
        }
        this.mPrevPickedIndex = this.mMinShowIndex + pickedIndexRelativeToMin;
        correctPositionByDefaultValue(pickedIndexRelativeToMin, this.mWrapSelectorWheel && this.mWrapSelectorWheelCheck);
        postInvalidate();
    }

    public void setNormalTextColor(int normalTextColor) {
        if (this.mTextColorNormal == normalTextColor) {
            return;
        }
        this.mTextColorNormal = normalTextColor;
        postInvalidate();
    }

    public void setSelectedTextColor(int selectedTextColor) {
        if (this.mTextColorSelected == selectedTextColor) {
            return;
        }
        this.mTextColorSelected = selectedTextColor;
        postInvalidate();
    }

    public void setHintTextColor(int hintTextColor) {
        if (this.mTextColorHint == hintTextColor) {
            return;
        }
        this.mTextColorHint = hintTextColor;
        this.mPaintHint.setColor(this.mTextColorHint);
        postInvalidate();
    }

    public void setDividerColor(int dividerColor) {
        if (this.mDividerColor == dividerColor) {
            return;
        }
        this.mDividerColor = dividerColor;
        this.mPaintDivider.setColor(this.mDividerColor);
        postInvalidate();
    }

    public void setPickedIndexRelativeToRaw(int pickedIndexRelativeToRaw) {
        int minShowIndex = this.mMinShowIndex;
        if (minShowIndex <= -1 || minShowIndex > pickedIndexRelativeToRaw || pickedIndexRelativeToRaw > this.mMaxShowIndex) {
            return;
        }
        this.mPrevPickedIndex = pickedIndexRelativeToRaw;
        correctPositionByDefaultValue(pickedIndexRelativeToRaw - minShowIndex, this.mWrapSelectorWheel && this.mWrapSelectorWheelCheck);
        postInvalidate();
    }

    public int getPickedIndexRelativeToRaw() {
        int firstItemY = this.mCurrDrawFirstItemY;
        if (firstItemY != 0) {
            int itemHeight = this.mItemHeight;
            if (firstItemY < (-itemHeight) / 2) {
                return getWillPickIndexByGlobalY(this.mCurrDrawGlobalY + itemHeight + firstItemY);
            }
            return getWillPickIndexByGlobalY(this.mCurrDrawGlobalY + firstItemY);
        }
        return getWillPickIndexByGlobalY(this.mCurrDrawGlobalY);
    }

    public void setMinAndMaxShowIndex(int minShowIndex, int maxShowIndex) {
        setMinAndMaxShowIndex(minShowIndex, maxShowIndex, true);
    }

    public void setFriction(float friction) {
        if (friction <= 0.0f) {
            throw new IllegalArgumentException("you should set a a positive float friction, now friction is " + friction);
        }
        ViewConfiguration.get(getContext());
        this.mFriction = ViewConfiguration.getScrollFriction() / friction;
    }

    private void onScrollStateChange(int scrollState) {
        if (this.mScrollState == scrollState) {
            return;
        }
        this.mScrollState = scrollState;
        OnScrollListener onScrollListener = this.mOnScrollListener;
        if (onScrollListener != null) {
            onScrollListener.onScrollStateChange(this, scrollState);
        }
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.mOnScrollListener = onScrollListener;
    }

    public void setOnValueChangedListener(IValueChangeListener onValueChangeListener) {
        this.mOnValueChangeListener = onValueChangeListener;
    }

    public void setOnValueChangedListenerRelativeToRaw(IValueChangeListenerRelativeToRaw onValueChangeListenerRaw) {
        this.mOnValueChangeListenerRaw = onValueChangeListenerRaw;
    }

    private int getWillPickIndexByGlobalY(int globalY) {
        int itemHeight = this.mItemHeight;
        boolean wrap = false;
        if (itemHeight == 0) {
            return 0;
        }
        int index = (globalY / itemHeight) + (this.mShowCount / 2);
        int oneRecycleSize = getOneRecycleSize();
        if (this.mWrapSelectorWheel && this.mWrapSelectorWheelCheck) {
            wrap = true;
        }
        int rawIndex = getIndexByRawIndex(index, oneRecycleSize, wrap);
        if (rawIndex >= 0 && rawIndex < getOneRecycleSize()) {
            return rawIndex + this.mMinShowIndex;
        }
        throw new IllegalArgumentException("getWillPickIndexByGlobalY illegal index : " + rawIndex
                + " getOneRecycleSize() : " + getOneRecycleSize() + " mWrapSelectorWheel : " + this.mWrapSelectorWheel);
    }

    private int getIndexByRawIndex(int rawIndex, int oneRecycleSize, boolean wrap) {
        if (oneRecycleSize <= 0) {
            return 0;
        }
        if (!wrap) {
            return rawIndex;
        }
        int index = rawIndex % oneRecycleSize;
        return index < 0 ? index + oneRecycleSize : index;
    }

    private void internalSetWrapToLinear() {
        correctPositionByDefaultValue(getPickedIndexRelativeToRaw() - this.mMinShowIndex, false);
        this.mWrapSelectorWheel = false;
        postInvalidate();
    }

    private void updateDividerAttr() {
        int showCount = this.mShowCount;
        this.mDividerIndex0 = showCount / 2;
        int dividerIndex0 = this.mDividerIndex0;
        this.mDividerIndex1 = dividerIndex0 + 1;
        int viewHeight = this.mViewHeight;
        this.dividerY0 = (dividerIndex0 * viewHeight) / showCount;
        this.dividerY1 = (this.mDividerIndex1 * viewHeight) / showCount;
        if (this.mDividerMarginL < 0) {
            this.mDividerMarginL = 0;
        }
        if (this.mDividerMarginR < 0) {
            this.mDividerMarginR = 0;
        }
        if (this.mDividerMarginL + this.mDividerMarginR != 0
                && getPaddingLeft() + this.mDividerMarginL >= (this.mViewWidth - getPaddingRight()) - this.mDividerMarginR) {
            int totalPadding = getPaddingLeft() + this.mDividerMarginL + getPaddingRight();
            int dividerMarginR = this.mDividerMarginR;
            int overflow = (totalPadding + dividerMarginR) - this.mViewWidth;
            int dividerMarginL = this.mDividerMarginL;
            float overflowF = overflow;
            this.mDividerMarginL = (int) (dividerMarginL - ((dividerMarginL * overflowF) / (dividerMarginL + dividerMarginR)));
            this.mDividerMarginR = (int) (dividerMarginR - ((overflowF * dividerMarginR) / (this.mDividerMarginL + dividerMarginR)));
        }
    }

    private void updateFontAttr() {
        int textSizeNormal = this.mTextSizeNormal;
        int itemHeight = this.mItemHeight;
        if (textSizeNormal > itemHeight) {
            this.mTextSizeNormal = itemHeight;
        }
        int textSizeSelected = this.mTextSizeSelected;
        int itemHeight2 = this.mItemHeight;
        if (textSizeSelected > itemHeight2) {
            this.mTextSizeSelected = itemHeight2;
        }
        Paint hintPaint = this.mPaintHint;
        if (hintPaint == null) {
            throw new IllegalArgumentException("mPaintHint should not be null.");
        }
        hintPaint.setTextSize(this.mTextSizeHint);
        this.mTextSizeHintCenterYOffset = getTextCenterYOffset(this.mPaintHint.getFontMetrics());
        this.mWidthOfHintText = getTextWidth(this.mHintText, this.mPaintHint);
        Paint textPaint = this.mPaintText;
        if (textPaint == null) {
            throw new IllegalArgumentException("mPaintText should not be null.");
        }
        textPaint.setTextSize(this.mTextSizeSelected);
        this.mTextSizeSelectedCenterYOffset = getTextCenterYOffset(this.mPaintText.getFontMetrics());
        this.mPaintText.setTextSize(this.mTextSizeNormal);
        this.mTextSizeNormalCenterYOffset = getTextCenterYOffset(this.mPaintText.getFontMetrics());
    }

    private void updateNotWrapYLimit() {
        this.mNotWrapLimitYTop = 0;
        this.mNotWrapLimitYBottom = (-this.mShowCount) * this.mItemHeight;
        if (this.mDisplayedValues != null) {
            int oneRecycleSize = getOneRecycleSize();
            int showCount = this.mShowCount;
            int itemHeight = this.mItemHeight;
            this.mNotWrapLimitYTop = ((oneRecycleSize - (showCount / 2)) - 1) * itemHeight;
            this.mNotWrapLimitYBottom = (-(showCount / 2)) * itemHeight;
        }
    }

    private int limitY(int y) {
        if (this.mWrapSelectorWheel && this.mWrapSelectorWheelCheck) {
            return y;
        }
        int limitYBottom = this.mNotWrapLimitYBottom;
        if (y < limitYBottom) {
            return limitYBottom;
        }
        int limitYTop = this.mNotWrapLimitYTop;
        return y > limitYTop ? limitYTop : y;
    }

    private void click(MotionEvent event) {
        float y = event.getY();
        for (int index = 0; index < this.mShowCount; index++) {
            int itemHeight = this.mItemHeight;
            if (itemHeight * index <= y && y < itemHeight * (index + 1)) {
                clickItem(index);
                return;
            }
        }
    }

    private void clickItem(int index) {
        int showCount;
        OnClickItemListener clickItemListener;
        if (index < 0 || index >= (showCount = this.mShowCount)) {
            return;
        }
        scrollByIndexSmoothly(index - (showCount / 2));
        if (index != this.mShowCount / 2 || (clickItemListener = this.onClickItemListener) == null) {
            return;
        }
        clickItemListener.onClickItemPosition(this.newValue);
        LogUtil.d("test", "newValue--->>" + this.newValue + "," + index);
    }

    private float getTextCenterYOffset(Paint.FontMetrics fontMetrics) {
        if (fontMetrics != null) {
            return Math.abs(fontMetrics.top + fontMetrics.bottom) / 2.0f;
        }
        return 0.0f;
    }

    private void correctPositionByDefaultValue(int defaultValue, boolean wrap) {
        this.mCurrDrawFirstItemIndex = defaultValue - ((this.mShowCount - 1) / 2);
        this.mCurrDrawFirstItemIndex = getIndexByRawIndex(this.mCurrDrawFirstItemIndex, getOneRecycleSize(), wrap);
        int itemHeight = this.mItemHeight;
        if (itemHeight == 0) {
            this.mCurrentItemIndexEffect = true;
        } else {
            this.mCurrDrawGlobalY = this.mCurrDrawFirstItemIndex * itemHeight;
            calculateFirstItemParameterByGlobalY();
        }
    }

    private void calculateFirstItemParameterByGlobalY() {
        this.mCurrDrawFirstItemIndex = (int) Math.floor(this.mCurrDrawGlobalY / this.mItemHeight);
        this.mCurrDrawFirstItemY = -(this.mCurrDrawGlobalY - (this.mCurrDrawFirstItemIndex * this.mItemHeight));
    }

    private void releaseVelocityTracker() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.clear();
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    private void updateMaxWHOfDisplayedValues(boolean needRequestLayout) {
        updateMaxWidthOfDisplayedValues();
        updateMaxHeightOfDisplayedValues();
        if (needRequestLayout) {
            if (this.mSpecModeW == Integer.MIN_VALUE || this.mSpecModeH == Integer.MIN_VALUE) {
                this.mHandlerInMainThread.sendEmptyMessage(3);
            }
        }
    }

    private int measureWidth(int widthMeasureSpec) {
        int mode = View.MeasureSpec.getMode(widthMeasureSpec);
        this.mSpecModeW = mode;
        int size = View.MeasureSpec.getSize(widthMeasureSpec);
        if (mode == 1073741824) {
            return size;
        }
        int hintWidth = Math.max(this.mWidthOfHintText, this.mWidthOfAlterHint);
        int contentWidth = Math.max(this.mMaxWidthOfAlterArrayWithMeasureHint,
                Math.max(this.mMaxWidthOfDisplayedValues, this.mMaxWidthOfAlterArrayWithoutMeasureHint)
                        + (((hintWidth != 0 ? this.mMarginStartOfHint : 0) + hintWidth + (hintWidth == 0 ? 0 : this.mMarginEndOfHint)
                        + (this.mItemPaddingHorizontal * 2)) * 2)) + getPaddingLeft() + getPaddingRight();
        return mode == Integer.MIN_VALUE ? Math.min(contentWidth, size) : contentWidth;
    }

    private int measureHeight(int heightMeasureSpec) {
        int mode = View.MeasureSpec.getMode(heightMeasureSpec);
        this.mSpecModeH = mode;
        int size = View.MeasureSpec.getSize(heightMeasureSpec);
        if (mode == 1073741824) {
            return size;
        }
        int contentHeight = (this.mShowCount * (this.mMaxHeightOfDisplayedValues + (this.mItemPaddingVertical * 2)))
                + getPaddingTop() + getPaddingBottom();
        return mode == Integer.MIN_VALUE ? Math.min(contentHeight, size) : contentHeight;
    }

    private void updateMaxWidthOfDisplayedValues() {
        float originalTextSize = this.mPaintText.getTextSize();
        this.mPaintText.setTextSize(this.mTextSizeSelected);
        this.mMaxWidthOfDisplayedValues = getMaxWidthOfTextArray(this.mDisplayedValues, this.mPaintText);
        this.mMaxWidthOfAlterArrayWithMeasureHint = getMaxWidthOfTextArray(this.mAlterTextArrayWithMeasureHint, this.mPaintText);
        this.mMaxWidthOfAlterArrayWithoutMeasureHint = getMaxWidthOfTextArray(this.mAlterTextArrayWithoutMeasureHint, this.mPaintText);
        this.mPaintText.setTextSize(this.mTextSizeHint);
        this.mWidthOfAlterHint = getTextWidth(this.mAlterHint, this.mPaintText);
        this.mPaintText.setTextSize(originalTextSize);
    }

    private int getMaxWidthOfTextArray(CharSequence[] textArray, Paint paint) {
        if (textArray == null) {
            return 0;
        }
        int maxWidth = 0;
        for (CharSequence text : textArray) {
            if (text != null) {
                maxWidth = Math.max(getTextWidth(text, paint), maxWidth);
            }
        }
        return maxWidth;
    }

    private int getTextWidth(CharSequence text, Paint paint) {
        if (TextUtils.isEmpty(text)) {
            return 0;
        }
        return (int) (paint.measureText(text.toString()) + 0.5f);
    }

    private void updateMaxHeightOfDisplayedValues() {
        float originalTextSize = this.mPaintText.getTextSize();
        this.mPaintText.setTextSize(this.mTextSizeSelected);
        double height = this.mPaintText.getFontMetrics().bottom - this.mPaintText.getFontMetrics().top;
        this.mMaxHeightOfDisplayedValues = (int) (height + 0.5d);
        this.mPaintText.setTextSize(originalTextSize);
    }

    private void updateContentAndIndex(String[] newDisplayedValues) {
        this.mMinShowIndex = 0;
        this.mMaxShowIndex = newDisplayedValues.length - 1;
        this.mDisplayedValues = newDisplayedValues;
        updateWrapStateByContent();
    }

    private void updateContent(String[] newDisplayedValues) {
        this.mDisplayedValues = newDisplayedValues;
        updateWrapStateByContent();
    }

    private void updateValue() {
        inflateDisplayedValuesIfNull();
        updateWrapStateByContent();
        this.mMinShowIndex = 0;
        this.mMaxShowIndex = this.mDisplayedValues.length - 1;
    }

    private void updateWrapStateByContent() {
        this.mWrapSelectorWheelCheck = this.mDisplayedValues.length > this.mShowCount;
    }

    private int refineValueByLimit(int value, int minValue, int maxValue, boolean wrap) {
        if (wrap) {
            if (value > maxValue) {
                return (((value - maxValue) % getOneRecycleSize()) + minValue) - 1;
            }
            return value < minValue ? ((value - minValue) % getOneRecycleSize()) + maxValue + 1 : value;
        }
        if (value > maxValue) {
            return maxValue;
        }
        return value < minValue ? minValue : value;
    }

    private void stopRefreshing() {
        Handler handler = this.mHandlerInNewThread;
        if (handler != null) {
            handler.removeMessages(1);
        }
    }

    private void stopScrolling() {
        ScrollerCompat scroller = this.mScroller;
        if (scroller == null || scroller.isFinished()) {
            return;
        }
        ScrollerCompat currentScroller = this.mScroller;
        currentScroller.startScroll(0, currentScroller.getCurrY(), 0, 0, 1);
        this.mScroller.abortAnimation();
        postInvalidate();
    }

    private Message getMsg(int what) {
        return getMsg(what, 0, 0, null);
    }

    private Message getMsg(int what, int arg1, int arg2, Object payload) {
        Message message = Message.obtain();
        message.what = what;
        message.arg1 = arg1;
        message.arg2 = arg2;
        message.obj = payload;
        return message;
    }

    private boolean isStringEqual(String first, String second) {
        if (first == null) {
            return second == null;
        }
        return first.equals(second);
    }

    private int sp2px(Context context, float sp) {
        return (int) ((sp * context.getResources().getDisplayMetrics().scaledDensity) + 0.5f);
    }

    private int dp2px(Context context, float dp) {
        return (int) ((dp * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public void setOnClickItemListener(OnClickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }
}