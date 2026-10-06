package com.xtc.ui.widget.readremind;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.xtc.log.LogUtil;
import com.xtc.ui.widget.R;
import java.util.regex.Pattern;

/** 未读提醒控件：按内容类型绘制圆点、圆形数字或圆角矩形数字/文本。 */
public class ReadRemindView extends TextView {
    private static final String TAG = "ReadRemindView";
    private final int CIRCLE_NO_CONTENT;
    private final int RECTANGLE_NUMBER_TEN;
    private final int RECTANGLE_NUMBER_UNIT;
    private final int RECTANGLE_STRING;
    private int backgroundColor;
    private Context context;
    private int heightExactly;
    private int mCircleRadius;
    private int mHeight;
    private Paint mPaintCircle;
    private Paint mPaintRec;
    private int mTextColor;
    private int mTextSize;
    private String mTextString;
    private int mTextViewPaddingLeft;
    private int mTextViewPaddingTop;
    private RectF rectF;
    private int type;

    public ReadRemindView(Context context) {
        this(context, null);
    }

    public ReadRemindView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReadRemindView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.CIRCLE_NO_CONTENT = 1;
        this.RECTANGLE_NUMBER_UNIT = 2;
        this.RECTANGLE_NUMBER_TEN = 3;
        this.RECTANGLE_STRING = 4;
        this.mTextViewPaddingTop = 4;
        this.mTextViewPaddingLeft = 5;
        this.context = context;
        initAttr(attrs);
        initView();
        initData();
    }

    private void initAttr(AttributeSet attrs) {
        TypedArray attributes = this.context.obtainStyledAttributes(attrs, R.styleable.ReadRemindView);
        this.backgroundColor = attributes.getColor(R.styleable.ReadRemindView_remindBackground, Color.parseColor("#ff4444"));
        this.mCircleRadius = attributes.getDimensionPixelOffset(R.styleable.ReadRemindView_remindCircleRadius, dip2Px(4.0f));
        this.mTextSize = attributes.getDimensionPixelOffset(R.styleable.ReadRemindView_remindTextSize, 14);
        this.mTextColor = attributes.getColor(R.styleable.ReadRemindView_remindTextColor, Color.parseColor("#ffffff"));
        setTextSize(this.mTextSize);
        setTextColor(this.mTextColor);
        setGravity(17);
    }

    private void initView() {
        dealType();
    }

    private void initData() {
        this.mPaintRec = new Paint();
        this.mPaintRec.setColor(this.backgroundColor);
        this.mPaintRec.setStyle(Paint.Style.FILL_AND_STROKE);
        this.mPaintRec.setAntiAlias(true);
        this.mPaintCircle = new Paint();
        this.mPaintCircle.setColor(this.backgroundColor);
        this.mPaintCircle.setStyle(Paint.Style.FILL_AND_STROKE);
        this.mPaintCircle.setAntiAlias(true);
    }

    private void dealType() {
        this.mTextString = (String) getText();
        LogUtil.i(TAG, "dealType =" + this.mTextString);
        if (TextUtils.isEmpty(this.mTextString)) {
            LogUtil.i(TAG, "text is null or ");
            this.type = 1;
            return;
        }
        this.mHeight = ((int) getTextViewHeight()) + dip2Px(5.0f);
        if (isDigit(this.mTextString)) {
            if (Integer.valueOf(this.mTextString).intValue() >= 10) {
                this.type = 3;
            }
            if (Integer.valueOf(this.mTextString).intValue() >= 10 || Integer.valueOf(this.mTextString).intValue() < 0) {
                return;
            }
            this.type = 2;
            return;
        }
        this.type = 4;
    }

    public void setReadRemindText(int count) {
        this.mTextString = count + "";
        if (count > 99) {
            setText("99+");
        } else {
            setText("" + count);
        }
        if (count >= 10) {
            this.type = 3;
        }
        if (count < 10 && count >= 0) {
            this.type = 2;
        }
        this.mHeight = ((int) getTextViewHeight()) + dip2Px(this.mTextViewPaddingTop);
        invalidate();
    }

    public void setReadRemindText(String text) {
        this.mTextString = text;
        if (text == null) {
            return;
        }
        setText(text);
        if (isDigit(text)) {
            setReadRemindText(Integer.valueOf(text).intValue());
            return;
        }
        this.type = 4;
        this.mHeight = ((int) getTextViewHeight()) + dip2Px(this.mTextViewPaddingTop);
        invalidate();
    }

    private int dip2Px(float dip) {
        return (int) ((dip * getContext().getResources().getDisplayMetrics().density) + 0.5f);
    }

    private float getTextViewHeight() {
        Paint.FontMetrics fontMetrics = getPaint().getFontMetrics();
        return fontMetrics.descent - fontMetrics.ascent;
    }

    private float getTextViewWidth() {
        return getPaint().measureText((String) getText());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childWidthSpec;
        int childHeightSpec;
        if (View.MeasureSpec.getMode(heightMeasureSpec) == 1073741824) {
            this.mHeight = View.MeasureSpec.getSize(heightMeasureSpec);
            this.heightExactly = this.mHeight;
        }
        int currentType = this.type;
        if (currentType == 1) {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(this.mCircleRadius * 2, 1073741824);
            childWidthSpec = View.MeasureSpec.makeMeasureSpec(this.mCircleRadius * 2, 1073741824);
        } else if (currentType == 2) {
            int exactHeight = this.heightExactly;
            childHeightSpec = exactHeight != 0
                    ? View.MeasureSpec.makeMeasureSpec(exactHeight, 1073741824)
                    : View.MeasureSpec.makeMeasureSpec(this.mHeight, 1073741824);
            childWidthSpec = View.MeasureSpec.makeMeasureSpec(this.mHeight, 1073741824);
        } else if (currentType == 3 || currentType == 4) {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(this.mHeight, 1073741824);
            childWidthSpec = View.MeasureSpec.makeMeasureSpec(this.mHeight + (dip2Px(this.mTextViewPaddingLeft) * 2), 1073741824);
        } else {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(this.mCircleRadius * 2, 1073741824);
            childWidthSpec = View.MeasureSpec.makeMeasureSpec(this.mCircleRadius * 2, 1073741824);
        }
        super.onMeasure(childWidthSpec, childHeightSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int currentType = this.type;
        if (currentType == 1) {
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, this.mCircleRadius, this.mPaintCircle);
        } else if (currentType == 2) {
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, this.mHeight / 2, this.mPaintCircle);
        } else if (currentType == 3 || currentType == 4) {
            this.rectF = new RectF(0.0f, 0.0f, getWidth(), getHeight());
            RectF bounds = this.rectF;
            int height = this.mHeight;
            canvas.drawRoundRect(bounds, height / 2, height / 2, this.mPaintRec);
        } else {
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, this.mCircleRadius, this.mPaintCircle);
        }
        setGravity(17);
        super.onDraw(canvas);
    }

    private boolean isDigit(String text) {
        if (isEmptyOrNullOrBlank(text)) {
            return false;
        }
        return isMatch("[0-9]*", text);
    }

    private boolean isEmptyOrNullOrBlank(String text) {
        return text == null || "".equals(text.trim());
    }

    private boolean isMatch(String pattern, String text) {
        return Pattern.compile(pattern).matcher(text).matches();
    }
}