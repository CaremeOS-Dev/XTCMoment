package com.xtc.ui.widget.drawable;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import com.xtc.ui.widget.R;
import com.xtc.ui.widget.util.UiCommonUtil;

/** 空心圆角渐变背景构建器（默认绿色描边）。 */
public class GradientHollowDrawable {
    private Context mContext;
    private GradientDrawable mGradientDrawable = new GradientDrawable();

    public GradientHollowDrawable(Context context) {
        this.mContext = context;
        int strokeWidth = UiCommonUtil.dp2Px(context, 1.0f);
        int width = UiCommonUtil.dp2Px(context, 150.0f);
        int height = UiCommonUtil.dp2Px(context, 40.0f);
        int radius = UiCommonUtil.dp2Px(context, 20.0f);
        this.mGradientDrawable.setStroke(strokeWidth, UiCommonUtil.getColor(context, R.color.color_55dd7b));
        this.mGradientDrawable.setShape(0);
        this.mGradientDrawable.setCornerRadius(radius);
        this.mGradientDrawable.setSize(width, height);
    }

    public GradientDrawable getGradientDrawable() {
        return this.mGradientDrawable;
    }

    public GradientDrawable addRadii(float radius) {
        return addRadii(radius, true);
    }

    public GradientDrawable addRadii(float radius, boolean useDp) {
        if (useDp) {
            radius = UiCommonUtil.dp2Px(this.mContext, radius);
        }
        this.mGradientDrawable.setCornerRadius(radius);
        return this.mGradientDrawable;
    }

    public GradientDrawable addRadii(float topRadius, float bottomRadius) {
        return addRadii(topRadius, bottomRadius, true);
    }

    public GradientDrawable addRadii(float topRadius, float bottomRadius, boolean useDp) {
        if (useDp) {
            topRadius = UiCommonUtil.dp2Px(this.mContext, topRadius);
            bottomRadius = UiCommonUtil.dp2Px(this.mContext, bottomRadius);
        }
        this.mGradientDrawable.setCornerRadii(new float[]{topRadius, topRadius, bottomRadius, bottomRadius,
                bottomRadius, bottomRadius, topRadius, topRadius});
        return this.mGradientDrawable;
    }

    public GradientDrawable addRadius(float[] radii) {
        return addRadius(radii, true);
    }

    public GradientDrawable addRadius(float[] radii, boolean useDp) {
        if (useDp) {
            radii = UiCommonUtil.dp2Px(this.mContext, radii);
        }
        this.mGradientDrawable.setCornerRadii(radii);
        return this.mGradientDrawable;
    }

    public GradientDrawable addSize(int width, int height) {
        return addSize(width, height, true);
    }

    public GradientDrawable addSize(int width, int height, boolean useDp) {
        if (useDp) {
            width = UiCommonUtil.dp2Px(this.mContext, width);
            height = UiCommonUtil.dp2Px(this.mContext, height);
        }
        this.mGradientDrawable.setSize(width, height);
        return this.mGradientDrawable;
    }

    public GradientDrawable addShape(int shape) {
        this.mGradientDrawable.setShape(shape);
        return this.mGradientDrawable;
    }

    public GradientDrawable addStroke(int width, int colorResId) {
        return addStroke(width, colorResId, true);
    }

    public GradientDrawable addStroke(int width, int colorResId, boolean useDp) {
        if (useDp) {
            width = UiCommonUtil.dp2Px(this.mContext, width);
        }
        this.mGradientDrawable.setStroke(width, UiCommonUtil.getColor(this.mContext, colorResId));
        return this.mGradientDrawable;
    }
}