package com.xtc.ui.widget.button;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.xtc.moment.R;

/** Two large buttons side by side, dispatching through {@link OnClickListener}. */
public class BigDoubleButton extends LinearLayout implements View.OnClickListener {

    private Button leftBtn;
    private Button rightBtn;
    private OnClickListener mClickListener;

    /** Callbacks for the two buttons. */
    public interface OnClickListener {
        void onLeftClicked();

        void onRightClicked();
    }

    public BigDoubleButton(Context context) {
        this(context, null);
    }

    public BigDoubleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BigDoubleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View view = inflate(context, R.layout.layout_double_button_big, this);
        this.leftBtn = (Button) view.findViewById(R.id.double_button_big_left_btn);
        this.rightBtn = (Button) view.findViewById(R.id.double_button_big_right_btn);
        this.leftBtn.setOnClickListener(this);
        this.rightBtn.setOnClickListener(this);
        int shadowX = getResources().getDimensionPixelSize(R.dimen.dimen_button_text_shadow_x);
        int shadowY = getResources().getDimensionPixelSize(R.dimen.dimen_button_text_shadow_y);
        float shadowRadius = getResources().getDimensionPixelSize(R.dimen.dimen_button_text_shadow_radius);
        float f = shadowX;
        float f2 = shadowY;
        this.leftBtn.setShadowLayer(shadowRadius, f, f2, R.color.color_button_text_shadow);
        this.rightBtn.setShadowLayer(shadowRadius, f, f2, R.color.color_button_text_shadow);
    }

    public void setClickListener(OnClickListener listener) {
        this.mClickListener = listener;
    }

    public void setLeftButtonText(String text) {
        this.leftBtn.setText(text);
    }

    public void setRightButtonText(String text) {
        this.rightBtn.setText(text);
    }

    @Override
    public void onClick(View view) {
        if (this.mClickListener == null) {
            return;
        }
        if (view.getId() == R.id.double_button_big_left_btn) {
            this.mClickListener.onLeftClicked();
        }
        if (view.getId() == R.id.double_button_big_right_btn) {
            this.mClickListener.onRightClicked();
        }
    }
}
