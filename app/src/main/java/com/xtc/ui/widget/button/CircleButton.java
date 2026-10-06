package com.xtc.ui.widget.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Button;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;

/** Fixed-diameter circular button with configurable background and text colour. */
public class CircleButton extends Button {

    public static final int BG_COLOR_RED = 0;
    public static final int BG_COLOR_WHITE = 1;
    public static final int BG_COLOR_GREEN = 2;

    public static final int TEXT_COLOR_WHITE = 0;
    public static final int TEXT_COLOR_RED = 1;
    public static final int TEXT_COLOR_BLACK = 2;

    private static final String TAG = "CircleButton";

    private int bgColor;
    private int textColor;

    public CircleButton(Context context) {
        this(context, null);
    }

    public CircleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.bgColor = BG_COLOR_RED;
        this.textColor = TEXT_COLOR_WHITE;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleButton, 0, 0);
        if (typedArray != null) {
            this.bgColor = typedArray.getInt(R.styleable.CircleButton_CircleButtonBgColor, 0);
            this.textColor = typedArray.getInt(R.styleable.CircleButton_CircleButtonTextColor, 0);
            typedArray.recycle();
        }
        setGravity(17);
        float textSize = getResources().getDimension(R.dimen.dimen_circle_button_text);
        LogUtil.d(TAG, "size:" + textSize);
        setTextSize(0, textSize);
        setShadowLayer(
                getResources().getDimensionPixelSize(R.dimen.dimen_button_text_shadow_radius),
                getResources().getDimensionPixelSize(R.dimen.dimen_button_text_shadow_x),
                getResources().getDimensionPixelSize(R.dimen.dimen_button_text_shadow_y),
                R.color.color_button_text_shadow);
        setBgColor();
        setButtonTextColor();
    }

    private void setBgColor() {
        if (BG_COLOR_GREEN == this.bgColor) {
            super.setBackgroundResource(R.drawable.bg_circle_button_green);
        }
        if (this.bgColor == BG_COLOR_RED) {
            super.setBackgroundResource(R.drawable.bg_circle_button_red);
        }
        if (BG_COLOR_WHITE == this.bgColor) {
            super.setBackgroundResource(R.drawable.bg_circle_button_white);
        }
    }

    private void setButtonTextColor() {
        if (TEXT_COLOR_BLACK == this.textColor) {
            super.setTextColor(getResources().getColor(R.color.color_circle_button_text_black));
        }
        if (this.textColor == TEXT_COLOR_WHITE) {
            super.setTextColor(getResources().getColor(R.color.color_circle_button_text_white));
        }
        if (TEXT_COLOR_RED == this.textColor) {
            super.setTextColor(getResources().getColor(R.color.color_circle_button_text_red));
        }
    }

    public void setButtonText(String text) {
        setText(text);
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
        setBgColor();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int diameter = getResources().getDimensionPixelSize(R.dimen.dimen_circle_button_diameter);
        setMeasuredDimension(diameter, diameter);
    }

    public void setButtonTextColor(int textColor) {
        this.textColor = textColor;
        setButtonTextColor();
    }
}
