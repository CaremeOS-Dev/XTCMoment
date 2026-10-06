package com.xtc.ui.widget.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Button;

import com.xtc.ui.widget.R;

/** Large hollow (outline) button. */
public class BigHollowButton extends Button {

    public BigHollowButton(Context context) {
        this(context, null);
    }

    public BigHollowButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BigHollowButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BigHollowButton, 0, 0);
        float textSize = getResources().getDimension(R.dimen.dimen_big_button_text);
        if (typedArray != null) {
            textSize = typedArray.getDimension(R.styleable.BigHollowButton_bhb_textSize, textSize);
            typedArray.recycle();
        }
        setDefaultConfig();
        setTextSize(0, textSize);
    }

    private void setDefaultConfig() {
        setBackgroundResource(R.drawable.bg_big_button_hollow_green);
        setGravity(17);
        setTextColor(getResources().getColor(R.color.color_big_hollow_button_text));
        setShadowLayer(
                getResources().getDimensionPixelSize(R.dimen.dimen_button_text_shadow_radius),
                getResources().getDimensionPixelSize(R.dimen.dimen_button_text_shadow_x),
                getResources().getDimensionPixelSize(R.dimen.dimen_button_text_shadow_y),
                R.color.color_button_text_shadow);
    }
}
