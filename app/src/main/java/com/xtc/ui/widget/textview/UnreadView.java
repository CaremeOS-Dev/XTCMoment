package com.xtc.ui.widget.textview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;
import com.xtc.ui.widget.R;

/** 未读提示控件：支持小红点、数字、99+ 三种展示形态。 */
public class UnreadView extends TextView {
    private static final int MAX_UNREAD_NUMBER = 99;
    private static final int NORMAL_TEXT_SIZE = 14;
    private static final int SMALL_TEXT_SIZE = 12;
    private int noneResId;
    private int oneDigitResId;
    private int twoDigitResId;

    public UnreadView(Context context) {
        this(context, null);
    }

    public UnreadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UnreadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.noneResId = R.drawable.bg_unread_none;
        this.oneDigitResId = R.drawable.bg_unread_one_digit;
        this.twoDigitResId = R.drawable.bg_unread_two_digit;
        setTextSize(2, 14.0f);
        setTextColor(-1);
        setGravity(17);
        setShadowLayer(getResources().getDimensionPixelOffset(R.dimen.dp_3), 0.0f,
                getResources().getDimensionPixelOffset(R.dimen.dp_2), R.color.color_c11b02);
    }

    public void hideView() {
        setText("");
        setVisibility(8);
    }

    public void showPoint() {
        showPoint(getResources().getDimensionPixelOffset(R.dimen.dp_11),
                getResources().getDimensionPixelOffset(R.dimen.dp_11));
    }

    public void showPoint(int width, int height) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        setLayoutParams(layoutParams);
        setBackgroundResource(this.noneResId);
        setText("");
        showView();
    }

    public void showNumber(int number, int width, int height, int textSize, boolean oneDigit) {
        if (number <= 0) {
            hideView();
            return;
        }
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        setLayoutParams(layoutParams);
        setText("" + number);
        setTextSize(2, (float) textSize);
        setBackgroundResource(oneDigit ? this.oneDigitResId : this.twoDigitResId);
        showView();
    }

    public void showNumber(int number) {
        if (number <= 0) {
            hideView();
            return;
        }
        if (number < 10) {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.width = getResources().getDimensionPixelOffset(R.dimen.dp_20);
            layoutParams.height = getResources().getDimensionPixelOffset(R.dimen.dp_20);
            setLayoutParams(layoutParams);
            setText("" + number);
            setTextSize(2, 14.0f);
            setBackgroundResource(this.oneDigitResId);
        } else if (number < 100) {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.width = getResources().getDimensionPixelOffset(R.dimen.dp_25);
            layoutParams.height = getResources().getDimensionPixelOffset(R.dimen.dp_20);
            setLayoutParams(layoutParams);
            setText("" + number);
            setTextSize(2, 14.0f);
            setBackgroundResource(this.twoDigitResId);
        } else {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.width = getResources().getDimensionPixelOffset(R.dimen.dp_25);
            layoutParams.height = getResources().getDimensionPixelOffset(R.dimen.dp_20);
            setLayoutParams(layoutParams);
            setText("99+");
            setTextSize(2, 12.0f);
            setBackgroundResource(this.twoDigitResId);
        }
        showView();
    }

    private void showView() {
        setVisibility(0);
    }
}