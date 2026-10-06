package com.xtc.ui.widget.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;

import com.xtc.ui.widget.R;
import com.xtc.ui.widget.UiConstants;
import com.xtc.ui.widget.animation.indicator.LoadingAnim;
import com.xtc.ui.widget.circle.CompatibleRoundDrawable;
import com.xtc.ui.widget.drawable.RoundDrawable;
import com.xtc.ui.widget.drawable.RoundRectDrawable;

/**
 * Factory for the gradient/rounded backgrounds used by buttons and dialogs.
 *
 * <p>Every "state" drawable pairs a normal layer with a pressed layer (the same
 * shape filled with {@link UiConstants.Color#MASK}), which is how the stock
 * buttons get their press feedback without a separate selector xml.
 */
public class UiBgUtil {

    private static final String TAG = "UiBgUtil";

    public static Drawable getGradientRoundRectStateDrawable(Context context, int width, int height, int topRadius, int bottomRadius, int[] colorResArray) {
        float top = topRadius;
        float bottom = bottomRadius;
        float[] radii = {top, top, bottom, bottom, bottom, bottom, top, top};
        int[] colors = UiCommonUtil.getColorArray(context, colorResArray);
        return getStateDrawable(
                getGradientRoundRectDrawable(0, colors[0], colors[1], radii, width, height),
                getGradientRoundRectMaskDrawable(0, colors, UiCommonUtil.getColorArray(context, UiConstants.Color.MASK), radii, radii, width, height));
    }

    public static Drawable getGradientRoundRectStateDrawable(Context context, int width, int height, int[] colorResArray) {
        float radius = height / 2;
        float[] radii = {radius, radius, radius, radius, radius, radius, radius, radius};
        int[] colors = UiCommonUtil.getColorArray(context, colorResArray);
        return getStateDrawable(
                getGradientRoundRectDrawable(0, colors[0], colors[1], radii, width, height),
                getGradientRoundRectMaskDrawable(0, colors, UiCommonUtil.getColorArray(context, UiConstants.Color.MASK), radii, radii, width, height));
    }

    public static Drawable getGradientRoundRectMaskDrawable(int shape, int[] colorResArray, int[] maskColorResArray, float[] radii, float[] maskRadii, int width, int height) {
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{
                getGradientRoundRectDrawable(shape, colorResArray[0], colorResArray[1], radii, width, height),
                getGradientRoundRectDrawable(shape, maskColorResArray[0], maskColorResArray[1], maskRadii, width, height)});
        layerDrawable.setLayerInset(1, 0, 0, 0, 0);
        return layerDrawable;
    }

    public static Drawable getGradientRoundRectStateDrawable(Context context) {
        return getGradientRoundRectStateDrawable(context, 0, UiConstants.Color.GREED, UiConstants.Color.MASK, 20.0f, 20.0f,
                UiCommonUtil.px2Dp(context, context.getResources().getDimension(R.dimen.long_btn_width)), 40);
    }

    public static Drawable getGradientRoundRectStateDrawable(Context context, int shape, int[] colorResArray, int[] maskColorResArray, float topRadius, float bottomRadius, int width, int height) {
        return getGradientRoundRectStateDrawable(context, shape, colorResArray, maskColorResArray,
                new float[]{topRadius, topRadius, bottomRadius, bottomRadius, bottomRadius, bottomRadius, topRadius, topRadius}, width, height);
    }

    public static Drawable getGradientRoundRectStateDrawable(Context context, int shape, int[] colorResArray, int[] maskColorResArray, float[] radii, int width, int height) {
        return getStateDrawable(
                getGradientRoundRectDrawable(context, shape, colorResArray[0], colorResArray[1], radii, width, height),
                getGradientRoundRectMaskDrawable(context, shape, colorResArray, maskColorResArray, radii, radii, width, height));
    }

    public static GradientDrawable getGradientRoundRectDrawable(Context context) {
        return getGradientRoundRectDrawable(context, 0, R.color.color_55dd7b, R.color.color_0ab158, 20.0f, 20.0f, 150, 40);
    }

    public static GradientDrawable getGradientRoundRectDrawable(Context context, int shape, int startColorRes, int endColorRes, float topRadius, float bottomRadius, int width, int height) {
        return getGradientRoundRectDrawable(context, shape, startColorRes, endColorRes,
                new float[]{topRadius, topRadius, bottomRadius, bottomRadius, bottomRadius, bottomRadius, topRadius, topRadius}, width, height);
    }

    public static GradientDrawable getGradientRoundRectDrawable(Context context, int shape, int startColorRes, int endColorRes, float[] radii, int width, int height) {
        return getGradientRoundRectDrawable(shape, UiCommonUtil.getColor(context, startColorRes), UiCommonUtil.getColor(context, endColorRes),
                UiCommonUtil.dp2Px(context, radii), UiCommonUtil.dp2Px(context, width), UiCommonUtil.dp2Px(context, height));
    }

    private static GradientDrawable getGradientRoundRectDrawable(int shape, int startColor, int endColor, float[] radii, int width, int height) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        drawable.setColors(new int[]{startColor, endColor});
        drawable.setShape(shape);
        drawable.setCornerRadii(radii);
        drawable.setSize(width, height);
        return drawable;
    }

    public static Drawable getGradientRoundRectMaskDrawable(Context context, int shape, int[] colorResArray, int[] maskColorResArray, int radius, int maskRadius, int width, int height) {
        float r = radius;
        float[] radii = {r, r, r, r, r, r, r, r};
        float mr = maskRadius;
        return getGradientRoundRectMaskDrawable(context, shape, colorResArray, maskColorResArray, radii,
                new float[]{mr, mr, mr, mr, mr, mr, mr, mr}, width, height);
    }

    public static Drawable getGradientRoundRectMaskDrawable(Context context, int shape, int[] colorResArray, int[] maskColorResArray, float[] radii, float[] maskRadii, int width, int height) {
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{
                getGradientRoundRectDrawable(context, shape, colorResArray[0], colorResArray[1], radii, width, height),
                getGradientRoundRectDrawable(context, shape, maskColorResArray[0], maskColorResArray[1], maskRadii, width, height)});
        layerDrawable.setLayerInset(1, 0, 0, 0, 0);
        return layerDrawable;
    }

    public static Drawable getGradientRoundStateDrawable(Context context) {
        return getGradientRoundStateDrawable(context, UiConstants.Color.GREED, UiConstants.Color.MASK, 30);
    }

    public static Drawable getGradientRoundStateDrawable(Context context, int[] colorResArray, int[] maskColorResArray, int radius) {
        float r = radius;
        int size = radius * 2;
        return getStateDrawable(
                getGradientRoundRectDrawable(context, 1, colorResArray[0], colorResArray[1], r, r, size, size),
                getGradientRoundRectMaskDrawable(context, 1, colorResArray, maskColorResArray, radius, radius, size, size));
    }

    public static StateListDrawable getStateDrawable(Drawable normal, Drawable pressed) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressed);
        stateListDrawable.addState(new int[0], normal);
        return stateListDrawable;
    }

    public static StateListDrawable getHollowRoundRectStateDrawable(Context context, int fillColorRes, int strokeColorRes, int strokeWidthRes, int topRadius, int bottomRadius) {
        return getStateDrawable(
                getHollowRoundRectDrawable(context, 0, fillColorRes, strokeColorRes, strokeWidthRes, topRadius, bottomRadius),
                getHollowRoundRectMaskDrawable(context, fillColorRes, strokeColorRes, strokeWidthRes, topRadius, bottomRadius));
    }

    private static Drawable getHollowRoundRectMaskDrawable(Context context, int fillColorRes, int strokeColorRes, int strokeWidthRes, int topRadius, int bottomRadius) {
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{
                getHollowRoundRectDrawable(context, 0, fillColorRes, strokeColorRes, strokeWidthRes, topRadius, bottomRadius),
                getHollowRoundRectDrawable(context, R.color.mask, fillColorRes, strokeColorRes, strokeWidthRes, topRadius, bottomRadius)});
        layerDrawable.setLayerInset(1, 0, 0, 0, 0);
        return layerDrawable;
    }

    public static StateListDrawable getHollowRoundRectStateDrawable(Context context, int fillColorRes, int strokeColorRes, int topRadius, int bottomRadius) {
        return getStateDrawable(
                getHollowRoundRectDrawable(context, 0, fillColorRes, strokeColorRes, topRadius, bottomRadius),
                getHollowRoundRectMaskDrawable(context, fillColorRes, strokeColorRes, topRadius, bottomRadius));
    }

    private static Drawable getHollowRoundRectMaskDrawable(Context context, int fillColorRes, int strokeColorRes, int topRadius, int bottomRadius) {
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{
                getHollowRoundRectDrawable(context, 0, fillColorRes, strokeColorRes, topRadius, bottomRadius),
                getHollowRoundRectDrawable(context, R.color.mask, fillColorRes, strokeColorRes, topRadius, bottomRadius)});
        layerDrawable.setLayerInset(1, 0, 0, 0, 0);
        return layerDrawable;
    }

    private static Drawable getHollowRoundRectDrawable(Context context, int pressedColorRes, int fillColorRes, int strokeColorRes, int strokeWidthRes, int topRadius, int bottomRadius) {
        float radius = bottomRadius / 2;
        return getHollowRoundRectDrawable(context, 0, fillColorRes, strokeColorRes, topRadius, bottomRadius,
                new float[]{radius, radius, radius, radius, radius, radius, radius, radius}, pressedColorRes);
    }

    public static StateListDrawable getHollowRoundRectStateDrawable(Context context) {
        return getStateDrawable(getHollowRoundRectDrawable(context, 0), getHollowRoundRectMaskDrawable(context));
    }

    private static Drawable getHollowRoundRectMaskDrawable(Context context) {
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{
                getHollowRoundRectDrawable(context, 0), getHollowRoundRectDrawable(context, R.color.mask)});
        layerDrawable.setLayerInset(1, 0, 0, 0, 0);
        return layerDrawable;
    }

    public static GradientDrawable getHollowRoundRectDrawable(Context context, int pressedColorRes) {
        return getHollowRoundRectDrawable(context, 0, 20.0f, 20.0f,
                UiCommonUtil.px2Dp(context, context.getResources().getDimension(R.dimen.long_btn_width)), 40,
                R.color.color_55dd7b, 1, pressedColorRes);
    }

    public static GradientDrawable getHollowRoundRectDrawable(Context context, int pressedColorRes, float topRadius, float bottomRadius, int width, int height, int strokeColorRes, int strokeWidthRes, int fillColorRes) {
        return getHollowRoundRectDrawable(context, pressedColorRes,
                new float[]{topRadius, topRadius, bottomRadius, bottomRadius, bottomRadius, bottomRadius, topRadius, topRadius},
                width, height, strokeColorRes, strokeWidthRes, fillColorRes);
    }

    private static GradientDrawable getHollowRoundRectDrawable(Context context, int pressedColorRes, float[] radii, int width, int height, int strokeColorRes, int strokeWidthRes, int fillColorRes) {
        return getHollowRoundRectDrawable(context, pressedColorRes, UiCommonUtil.getColor(context, strokeColorRes),
                UiCommonUtil.dp2Px(context, strokeWidthRes), UiCommonUtil.dp2Px(context, width), UiCommonUtil.dp2Px(context, height),
                UiCommonUtil.dp2Px(context, radii), fillColorRes);
    }

    private static GradientDrawable getHollowRoundRectDrawable(Context context, int pressedColorRes, int strokeColor, int strokeWidthPx, int widthPx, int heightPx, float[] radiiPx, int fillColorRes) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setStroke(strokeWidthPx, strokeColor);
        if (fillColorRes != 0) {
            drawable.setColor(UiCommonUtil.getColor(context, fillColorRes));
        }
        drawable.setShape(0);
        drawable.setCornerRadii(radiiPx);
        drawable.setSize(widthPx, heightPx);
        return drawable;
    }

    public static Drawable getRoundStateDrawable(Context context, int normalColorRes, int pressedColorRes, int radiusDp) {
        int radiusPx = UiCommonUtil.dp2Px(context, radiusDp);
        int normalColor = UiCommonUtil.getColor(context, normalColorRes);
        return getStateDrawable(getRoundDrawable(normalColor, radiusPx),
                getRoundMaskDrawable(normalColor, UiCommonUtil.getColor(context, pressedColorRes), radiusPx, radiusPx));
    }

    public static Drawable getRoundMaskDrawable(int normalColor, int pressedColor, int normalRadius, int pressedRadius) {
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{
                getRoundDrawable(normalColor, normalRadius), getRoundDrawable(pressedColor, pressedRadius)});
        layerDrawable.setLayerInset(1, 0, 0, 0, 0);
        return layerDrawable;
    }

    public static Drawable getRoundDrawable(int color, int radius) {
        return new RoundDrawable(color, radius);
    }

    public static CompatibleRoundDrawable getRoundRectDrawable(Context context, int colorRes, int radiusDp) {
        return getRoundRectDrawable(context, colorRes, radiusDp, 0, 0);
    }

    public static CompatibleRoundDrawable getRoundRectDrawable(Context context, int colorRes, float[] radiiDp) {
        return getRoundRectDrawable(context, colorRes, radiiDp, 0, 0);
    }

    public static CompatibleRoundDrawable getRoundRectDrawable(Context context, int colorRes, int radiusDp, int borderColorRes, int borderWidthDp) {
        float r = radiusDp;
        return getRoundRectDrawable(context, colorRes, new float[]{r, r, r, r, r, r, r, r}, borderColorRes, borderWidthDp);
    }

    public static CompatibleRoundDrawable getRoundRectDrawable(Context context, int colorRes, float[] radiiDp, int borderColorRes, int borderWidthDp) {
        CompatibleRoundDrawable drawable = new CompatibleRoundDrawable(UiCommonUtil.getColor(context, colorRes));
        drawable.setRadii(UiCommonUtil.dp2Px(context, radiiDp));
        drawable.setBorder(UiCommonUtil.getColor(context, borderColorRes), UiCommonUtil.dp2Px(context, borderWidthDp));
        return drawable;
    }

    public static Drawable getRoundRectMaskDrawable(Context context, int normalColorRes, int pressedColorRes, int normalRadiusDp, int pressedRadiusDp) {
        float n = normalRadiusDp;
        float p = pressedRadiusDp;
        return getRoundRectMaskDrawable(context, normalColorRes, pressedColorRes,
                new float[]{n, n, n, n, n, n, n, n}, new float[]{p, p, p, p, p, p, p, p}, 0, 0);
    }

    public static Drawable getRoundRectMaskDrawable(Context context, int normalColorRes, int pressedColorRes, float[] normalRadiiDp, float[] pressedRadiiDp, int borderColorRes, int borderWidthDp) {
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{
                getRoundRectDrawable(context, normalColorRes, normalRadiiDp, borderColorRes, borderWidthDp),
                getRoundRectDrawable(context, pressedColorRes, pressedRadiiDp, borderColorRes, borderWidthDp)});
        layerDrawable.setLayerInset(1, 0, 0, 0, 0);
        return layerDrawable;
    }

    public static Drawable getRoundRectStateDrawable(Context context, int normalColorRes, int pressedColorRes, int normalRadiusDp, int pressedRadiusDp) {
        float n = normalRadiusDp;
        float p = pressedRadiusDp;
        return getRoundRectStateDrawable(context, normalColorRes, pressedColorRes,
                new float[]{n, n, n, n, n, n, n, n}, new float[]{p, p, p, p, p, p, p, p}, 0, 0);
    }

    public static Drawable getRoundRectStateDrawable(Context context, int normalColorRes, int pressedColorRes, float[] normalRadiiDp, float[] pressedRadiiDp, int borderColorRes, int borderWidthDp) {
        return getStateDrawable(
                getRoundRectDrawable(context, normalColorRes, normalRadiiDp, borderColorRes, borderWidthDp),
                getRoundRectMaskDrawable(context, normalColorRes, pressedColorRes, normalRadiiDp, pressedRadiiDp, borderColorRes, borderWidthDp));
    }

    public static Drawable getRoundRectMaskDrawable(int normalColor, int pressedColor, int normalRadius, int pressedRadius, int width, int height) {
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{
                getRoundRectDrawable(normalColor, normalRadius, width, height),
                getRoundRectDrawable(pressedColor, pressedRadius, width, height)});
        layerDrawable.setLayerInset(1, 0, 0, 0, 0);
        return layerDrawable;
    }

    public static RoundRectDrawable getRoundRectDrawable(int color, int radius, int width, int height) {
        RoundRectDrawable drawable = new RoundRectDrawable(color, radius);
        drawable.setRectParams(0, 0, width, height);
        return drawable;
    }

    @Deprecated
    public static Drawable createRoundDrawable(Bitmap bitmap, float radius) {
        return new BitmapDrawable(createRoundBitmap(bitmap, radius));
    }

    @Deprecated
    public static Bitmap createRoundBitmap(Bitmap bitmap, float radius) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, radius, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static AnimationDrawable getLoadingAnimForBtn(Context context, float sizeDp) {
        return new LoadingAnim(context).createAnim(R.color.color_ffffff, sizeDp / 29.0f, 100);
    }
}
