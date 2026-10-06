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
import com.xtc.ui.widget.UiConstants;
import com.xtc.ui.widget.util.UiBgUtil;
import com.xtc.ui.widget.util.UiCommonUtil;

/** Full-width solid (filled) button that can swap its label for a spinner. */
public class LongSolidButton extends LinearLayout {

    private static final String TAG = "LongSolidButton";

    private boolean backgroundUpdated;
    private int[] bgColorIdArray;
    private final View loadingHolder;
    private final View marginView;
    private final TextView tv;

    public LongSolidButton(Context context) {
        this(context, null);
    }

    public LongSolidButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LongSolidButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.backgroundUpdated = false;
        int padding = (int) context.getResources().getDimension(R.dimen.normal_btn_loading_margin);
        setPadding(padding, 0, padding, 0);
        setGravity(17);
        setOrientation(HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.layout_solid_btn, (ViewGroup) this, true);
        this.loadingHolder = findViewById(R.id.loading_long_solid_btn);
        this.tv = (TextView) findViewById(R.id.tv_long_solid_btn);
        this.marginView = findViewById(R.id.margin_long_solid_btn);
        Resources resources = getResources();
        this.tv.setTextColor(resources.getColor(R.color.color_ffffff));
        this.tv.setTextSize(0, resources.getDimension(R.dimen.normal_btn_title_text_size));
        this.tv.setGravity(17);
        int[] defaultColors = UiConstants.Color.GREED;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LongSolidButton);
        if (typedArray != null) {
            int colorIndex = typedArray.getInt(R.styleable.LongSolidButton_bgColorArray, 1);
            if (colorIndex == 0) {
                this.bgColorIdArray = UiConstants.Color.RED;
            } else if (colorIndex == 2) {
                this.bgColorIdArray = UiConstants.Color.WHITE;
            } else if (colorIndex == 3) {
                this.bgColorIdArray = UiConstants.Color.YELLOW;
            } else if (colorIndex != 4) {
                this.bgColorIdArray = UiConstants.Color.GREED;
            } else {
                this.bgColorIdArray = UiConstants.Color.GRAY;
            }
            typedArray.recycle();
        } else {
            LogUtil.e(TAG, "TypedArray = null !");
            this.bgColorIdArray = defaultColors;
        }
        setClickable(true);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed || !this.backgroundUpdated) {
            Context context = getContext();
            int[] colors = this.bgColorIdArray;
            if (colors == null) {
                colors = UiConstants.Color.GREED;
            }
            setBackground(UiBgUtil.getGradientRoundRectStateDrawable(context, getWidth(), getHeight(), colors));
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

    public void setBgColorIdArray(int[] bgColorIdArray) {
        this.bgColorIdArray = bgColorIdArray;
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
