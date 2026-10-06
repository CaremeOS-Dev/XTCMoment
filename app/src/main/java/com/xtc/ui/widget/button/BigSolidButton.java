package com.xtc.ui.widget.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Button;

import com.xtc.ui.widget.R;

/** Large solid button, in green or yellow. */
public class BigSolidButton extends Button {

    public static final int BG_COLOR_GREEN = 0;
    public static final int BG_COLOR_YELLOW = 1;

    private int bgColor;

    public BigSolidButton(Context context) {
        this(context, (AttributeSet) null);
    }

    public BigSolidButton(Context context, int bgColor) {
        this(context, (AttributeSet) null);
        this.bgColor = bgColor;
    }

    public BigSolidButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BigSolidButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.bgColor = 0;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BigSolidButton, 0, 0);
        float textSize = getResources().getDimension(R.dimen.dimen_big_button_text);
        if (typedArray != null) {
            this.bgColor = typedArray.getInt(R.styleable.BigSolidButton_BigSolidButtonBgColor, 0);
            textSize = typedArray.getDimension(R.styleable.BigSolidButton_bsb_textSize, textSize);
            typedArray.recycle();
        }
        if (this.bgColor == BG_COLOR_YELLOW) {
            setYellowDefault();
        } else {
            setGreenDefault();
        }
        setTextSize(0, textSize);
    }

    private void setGreenDefault() {
        setBackgroundResource(R.drawable.bg_big_button_solid_green);
        setTextConfig();
    }

    private void setYellowDefault() {
        setBackgroundResource(R.drawable.bg_big_button_solid_yellow);
        setTextConfig();
    }

    private void setTextConfig() {
        setGravity(17);
        setTextColor(getResources().getColor(R.color.text_color_big_solid_button));
        setShadowLayer(
                getResources().getDimensionPixelSize(R.dimen.dimen_button_text_shadow_radius),
                getResources().getDimensionPixelSize(R.dimen.dimen_button_text_shadow_x),
                getResources().getDimensionPixelSize(R.dimen.dimen_button_text_shadow_y),
                R.color.color_button_text_shadow);
    }
}
