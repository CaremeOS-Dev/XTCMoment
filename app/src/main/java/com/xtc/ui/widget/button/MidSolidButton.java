package com.xtc.ui.widget.button;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.xtc.moment.R;

/** Medium solid button; a {@link Button} wrapped in a fixed-size frame. */
public class MidSolidButton extends FrameLayout {

    private Button mButton;

    public MidSolidButton(Context context) {
        this(context, null);
    }

    public MidSolidButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MidSolidButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mButton = (Button) inflate(context, R.layout.layout_mid_solid_button, this).findViewById(R.id.mid_solid_button_btn);
        this.mButton.setShadowLayer(
                getResources().getDimensionPixelSize(R.dimen.dimen_button_text_shadow_radius),
                getResources().getDimensionPixelSize(R.dimen.dimen_button_text_shadow_x),
                getResources().getDimensionPixelSize(R.dimen.dimen_button_text_shadow_y),
                R.color.color_button_text_shadow);
    }

    public void setText(String text) {
        this.mButton.setText(text);
    }

    @Override
    public void setOnClickListener(View.OnClickListener listener) {
        if (listener != null) {
            this.mButton.setOnClickListener(listener);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(
                getResources().getDimensionPixelSize(R.dimen.dimen_mid_solid_button_width),
                getResources().getDimensionPixelSize(R.dimen.dimen_mid_solid_button_height));
    }
}
