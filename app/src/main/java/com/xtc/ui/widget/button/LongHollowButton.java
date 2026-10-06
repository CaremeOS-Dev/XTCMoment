package com.xtc.ui.widget.button;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.ui.widget.util.UiBgUtil;
import com.xtc.ui.widget.util.UiCommonUtil;

/** Full-width hollow (outline) button that can swap its label for a spinner. */
public class LongHollowButton extends LinearLayout {

    private static final String TAG = "LongHollowButton";

    private boolean backgroundUpdated;
    private final View loadingHolder;
    private final View marginView;
    private final TextView tv;
    private int pressColor;
    private int strokeColor;
    private float strokeWidth;

    public LongHollowButton(Context context) {
        this(context, null);
    }

    public LongHollowButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LongHollowButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.backgroundUpdated = false;
        int padding = (int) context.getResources().getDimension(R.dimen.normal_btn_loading_margin);
        setPadding(padding, 0, padding, 0);
        setGravity(17);
        setOrientation(HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.layout_hollow_btn, (ViewGroup) this, true);
        this.loadingHolder = findViewById(R.id.loading_long_hollow_btn);
        this.tv = (TextView) findViewById(R.id.tv_long_hollow_btn);
        this.marginView = findViewById(R.id.margin_long_hollow_btn);
        Resources resources = getResources();
        this.tv.setTextColor(resources.getColor(R.color.color_ffffff));
        this.tv.setTextSize(0, resources.getDimension(R.dimen.normal_title_text_size));
        this.tv.setGravity(17);
        int defaultStrokeColor = resources.getColor(R.color.color_55dd7b);
        float defaultStrokeWidth = resources.getDimension(R.dimen.long_btn_stroke_width);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LongHollowButton);
        if (typedArray != null) {
            this.strokeColor = typedArray.getInt(R.styleable.LongHollowButton_strokeColor, defaultStrokeColor);
            this.strokeWidth = typedArray.getDimension(R.styleable.LongHollowButton_strokeWidth, defaultStrokeWidth);
            typedArray.recycle();
        } else {
            LogUtil.e(TAG, "TypedArray = null !");
            this.strokeColor = defaultStrokeColor;
            this.strokeWidth = defaultStrokeWidth;
        }
        this.pressColor = R.color.mask_for_hollow;
        setClickable(true);
        LogUtil.d(TAG, "init , strokeColor = " + this.strokeColor + " , strokeWidth = " + this.strokeWidth);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed || !this.backgroundUpdated) {
            setBackground(UiBgUtil.getHollowRoundRectStateDrawable(getContext(), getWidth(), getHeight(), this.strokeColor, (int) this.strokeWidth, this.pressColor));
            this.backgroundUpdated = true;
        }
    }

    public void setButtonSize(int widthPx, int heightPx) {
        LogUtil.d(TAG, "setButtonSize widthPx = " + widthPx + " , heightPx = " + heightPx);
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = widthPx;
        layoutParams.height = heightPx;
        setLayoutParams(layoutParams);
        invalidBackground();
    }

    public void setPressBackground(int pressColor) {
        this.pressColor = pressColor;
        invalidBackground();
    }

    public void setStrokeWidth(float strokeWidth) {
        LogUtil.d(TAG, "setStrokeWidth = " + strokeWidth);
        this.strokeWidth = strokeWidth;
        invalidBackground();
    }

    public void setStrokeColor(int strokeColor) {
        LogUtil.d(TAG, "setStrokeColor = " + strokeColor);
        this.strokeColor = strokeColor;
        invalidBackground();
    }

    private void invalidBackground() {
        this.backgroundUpdated = false;
        requestLayout();
        invalidate();
    }

    public View getLoadingHolder() {
        return this.loadingHolder;
    }

    public TextView getTv() {
        return this.tv;
    }

    public AnimationDrawable getDefaultSizeLoadingAnim(Context context) {
        return UiBgUtil.getLoadingAnimForBtn(context, 17.0f);
    }

    public void setMarginPx(float marginPx) {
        ViewGroup.LayoutParams layoutParams = this.marginView.getLayoutParams();
        int old = layoutParams.width;
        layoutParams.width = (int) marginPx;
        LogUtil.d(TAG, "setMarginPx src = " + old + " , new = " + marginPx);
    }

    /** Shows/hides the spinner and/or label; the spacer only shows for both. */
    public void setViewVisible(boolean showLoading, boolean showText) {
        LogUtil.d(TAG, "setViewVisible , loading = " + showLoading + " , tv = " + showText);
        if (showLoading) {
            UiCommonUtil.showView(this.loadingHolder);
        } else {
            UiCommonUtil.hideView(this.loadingHolder);
        }
        if (showText) {
            UiCommonUtil.showView(this.tv);
        } else {
            UiCommonUtil.hideView(this.tv);
        }
        if (showLoading && showText) {
            UiCommonUtil.showView(this.marginView);
        } else {
            UiCommonUtil.hideView(this.marginView);
        }
    }
}
