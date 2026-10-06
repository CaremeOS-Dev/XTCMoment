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
 * Factory for the gradient / rounded backgrounds used by buttons and dialogs.
 *
 * <p>Every "state" drawable pairs a normal layer with a pressed layer (the same
 * shape filled with {@link UiConstants.Color#MASK}), which is how the stock
 * buttons get their press feedback without a separate selector xml.
 *
 * <p>All radii / sizes are expressed the way the call sites pass them: the
 * "gradient" family takes pixels, the "roundRect" family takes dp and converts.
 */
public class UiBgUtil {

    private static final String TAG = "UiBgUtil";

    // ------------------------------------------------------------------ gradient round rect

    /** Gradient rounded rect, per-corner radii in px, with a pressed state. */
    public static Drawable getGradientRoundRectStateDrawable(Context context, int widthPx, int heightPx, int topRadiusPx, int bottomRadiusPx, int[] colorResArray) {
        float top = topRadiusPx;
        float bottom = bottomRadiusPx;
        float[] radii = {top, top, bottom, bottom, bottom, bottom, top, top};
        int[] colors = UiCommonUtil.getColorArray(context, colorResArray);
        return getStateDrawable(
                getGradientRoundRectDrawable(0, colors[0], colors[1], radii, widthPx, heightPx),
                getGradientRoundRectMaskDrawable(0, colors, UiCommonUtil.getColorArray(context, UiConstants.Color.MASK), radii, radii, widthPx, heightPx));
    }

    /** Gradient rounded rect with a pill radius (height / 2), with a pressed state. */
    public static Drawable getGradientRoundRectStateDrawable(Context context, int widthPx, int heightPx, int[] colorResArray) {
        float radius = heightPx / 2;
        float[] radii = {radius, radius, radius, radius, radius, radius, radius, radius};
        int[] colors = UiCommonUtil.getColorArray(context, colorResArray);
        return getStateDrawable(
                getGradientRoundRectDrawable(0, colors[0], colors[1], radii, widthPx, heightPx),
                getGradientRoundRectMaskDrawable(0, colors, UiCommonUtil.getColorArray(context, UiConstants.Color.MASK), radii, radii, widthPx, heightPx));
    }

    /** Two stacked gradient rounded rects (normal + pressed colours). */
    public static Drawable getGradientRoundRectMaskDrawable(int shape, int[] colorResArray, int[] maskColorResArray, float[] radii, float[] maskRadii, int widthPx, int heightPx) {
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{
                getGradientRoundRectDrawable(shape, colorResArray[0], colorResArray[1], radii, widthPx, heightPx),
                getGradientRoundRectDrawable(shape, maskColorResArray[0], maskColorResArray[1], maskRadii, widthPx, heightPx)});
        layerDrawable.setLayerInset(1, 0, 0, 0, 0);
        return layerDrawable;
    }

    public static Drawable getGradientRoundRectStateDrawable(Context context) {
        return getGradientRoundRectStateDrawable(context, 0, UiConstants.Color.GREED, UiConstants.Color.MASK, 20.0f, 20.0f,
                UiCommonUtil.px2Dp(context, context.getResources().getDimension(R.dimen.long_btn_width)), 40);
    }

    public static Drawable getGradientRoundRectStateDrawable(Context context, int shape, int[] colorResArray, int[] maskColorResArray, float topRadius, float bottomRadius, int widthPx, int heightPx) {
        return getGradientRoundRectStateDrawable(context, shape, colorResArray, maskColorResArray,
                new float[]{topRadius, topRadius, bottomRadius, bottomRadius, bottomRadius, bottomRadius, topRadius, topRadius}, widthPx, heightPx);
    }

    public static Drawable getGradientRoundRectStateDrawable(Context context, int shape, int[] colorResArray, int[] maskColorResArray, float[] radiiDp, int widthPx, int heightPx) {
        return getStateDrawable(
                getGradientRoundRectDrawable(context, shape, colorResArray[0], colorResArray[1], radiiDp, widthPx, heightPx),
                getGradientRoundRectMaskDrawable(context, shape, colorResArray, maskColorResArray, radiiDp, radiiDp, widthPx, heightPx));
    }

    public static GradientDrawable getGradientRoundRectDrawable(Context context) {
        return getGradientRoundRectDrawable(context, 0, R.color.color_55dd7b, R.color.color_0ab158, 20.0f, 20.0f, 150, 40);
    }

    public static GradientDrawable getGradientRoundRectDrawable(Context context, int shape, int startColorRes, int endColorRes, float topRadiusDp, float bottomRadiusDp, int widthDp, int heightDp) {
        return getGradientRoundRectDrawable(context, shape, startColorRes, endColorRes,
                new float[]{topRadiusDp, topRadiusDp, bottomRadiusDp, bottomRadiusDp, bottomRadiusDp, bottomRadiusDp, topRadiusDp, topRadiusDp}, widthDp, heightDp);
    }

    public static GradientDrawable getGradientRoundRectDrawable(Context context, int shape, int startColorRes, int endColorRes, float[] radiiDp, int widthDp, int heightDp) {
        return getGradientRoundRectDrawable(shape, UiCommonUtil.getColor(context, startColorRes), UiCommonUtil.getColor(context, endColorRes),
                UiCommonUtil.dp2Px(context, radiiDp), UiCommonUtil.dp2Px(context, widthDp), UiCommonUtil.dp2Px(context, heightDp));
    }

    private static GradientDrawable getGradientRoundRectDrawable(int shape, int startColor, int endColor, float[] radiiPx, int widthPx, int heightPx) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        drawable.setColors(new int[]{startColor, endColor});
        drawable.setShape(shape);
        drawable.setCornerRadii(radiiPx);
        drawable.setSize(widthPx, heightPx);
        return drawable;
    }

    public static Drawable getGradientRoundRectMaskDrawable(Context context, int shape, int[] colorResArray, int[] maskColorResArray, int radiusDp, int maskRadiusDp, int widthDp, int heightDp) {
        float radius = radiusDp;
        float[] radii = {radius, radius, radius, radius, radius, radius, radius, radius};
        float maskRadius = maskRadiusDp;
        return getGradientRoundRectMaskDrawable(context, shape, colorResArray, maskColorResArray, radii,
                new float[]{maskRadius, maskRadius, maskRadius, maskRadius, maskRadius, maskRadius, maskRadius, maskRadius}, widthDp, heightDp);
    }

    public static Drawable getGradientRoundRectMaskDrawable(Context context, int shape, int[] colorResArray, int[] maskColorResArray, float[] radiiDp, float[] maskRadiiDp, int widthDp, int heightDp) {
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{
                getGradientRoundRectDrawable(context, shape, colorResArray[0], colorResArray[1], radiiDp, widthDp, heightDp),
                getGradientRoundRectDrawable(context, shape, maskColorResArray[0], maskColorResArray[1], maskRadiiDp, widthDp, heightDp)});
        layerDrawable.setLayerInset(1, 0, 0, 0, 0);
        return layerDrawable;
    }

    // ------------------------------------------------------------------ gradient round

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

    // ------------------------------------------------------------------ hollow round rect

    /** Hollow rounded rect with a pressed state; all sizes in px. */
    public static StateListDrawable getHollowRoundRectStateDrawable(Context context, int widthPx, int heightPx, int strokeColor, int strokeWidthPx, int pressColorRes) {
        return getStateDrawable(
                getHollowRoundRectDrawable(context, 0, widthPx, heightPx, strokeColor, strokeWidthPx),
                getHollowRoundRectMaskDrawable(context, widthPx, heightPx, strokeColor, strokeWidthPx, pressColorRes));
    }

    private static Drawable getHollowRoundRectMaskDrawable(Context context, int widthPx, int heightPx, int strokeColor, int strokeWidthPx, int pressColorRes) {
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{
                getHollowRoundRectDrawable(context, 0, widthPx, heightPx, strokeColor, strokeWidthPx),
                getHollowRoundRectDrawable(context, pressColorRes, widthPx, heightPx, strokeColor, strokeWidthPx)});
        layerDrawable.setLayerInset(1, 0, 0, 0, 0);
        return layerDrawable;
    }

    public static StateListDrawable getHollowRoundRectStateDrawable(Context context, int widthPx, int heightPx, int strokeColor, int strokeWidthPx) {
        return getStateDrawable(
                getHollowRoundRectDrawable(context, 0, widthPx, heightPx, strokeColor, strokeWidthPx),
                getHollowRoundRectMaskDrawable(context, widthPx, heightPx, strokeColor, strokeWidthPx));
    }

    private static Drawable getHollowRoundRectMaskDrawable(Context context, int widthPx, int heightPx, int strokeColor, int strokeWidthPx) {
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{
                getHollowRoundRectDrawable(context, 0, widthPx, heightPx, strokeColor, strokeWidthPx),
                getHollowRoundRectDrawable(context, R.color.mask, widthPx, heightPx, strokeColor, strokeWidthPx)});
        layerDrawable.setLayerInset(1, 0, 0, 0, 0);
        return layerDrawable;
    }

    private static Drawable getHollowRoundRectDrawable(Context context, int pressedColorRes, int widthPx, int heightPx, int strokeColor, int strokeWidthPx) {
        float radius = heightPx / 2;
        return getHollowRoundRectDrawable(context, 0, widthPx, heightPx, strokeColor, strokeWidthPx,
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

    public static GradientDrawable getHollowRoundRectDrawable(Context context, int pressedColorRes, float topRadiusDp, float bottomRadiusDp, int widthDp, int heightDp, int strokeColorRes, int strokeWidthDp, int fillColorRes) {
        return getHollowRoundRectDrawable(context, pressedColorRes,
                new float[]{topRadiusDp, topRadiusDp, bottomRadiusDp, bottomRadiusDp, bottomRadiusDp, bottomRadiusDp, topRadiusDp, topRadiusDp},
                widthDp, heightDp, strokeColorRes, strokeWidthDp, fillColorRes);
    }

    private static GradientDrawable getHollowRoundRectDrawable(Context context, int pressedColorRes, float[] radiiDp, int widthDp, int heightDp, int strokeColorRes, int strokeWidthDp, int fillColorRes) {
        return getHollowRoundRectDrawable(context, pressedColorRes, UiCommonUtil.getColor(context, strokeColorRes),
                UiCommonUtil.dp2Px(context, strokeWidthDp), UiCommonUtil.dp2Px(context, widthDp), UiCommonUtil.dp2Px(context, heightDp),
                UiCommonUtil.dp2Px(context, radiiDp), fillColorRes);
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

    // ------------------------------------------------------------------ round / round rect

    public static Drawable getRoundStateDrawable(Context context, int normalColorRes, int pressedColorRes, int radiusDp) {
        int radiusPx = UiCommonUtil.dp2Px(context, radiusDp);
        int normalColor = UiCommonUtil.getColor(context, normalColorRes);
        return getStateDrawable(getRoundDrawable(normalColor, radiusPx),
                getRoundMaskDrawable(normalColor, UiCommonUtil.getColor(context, pressedColorRes), radiusPx, radiusPx));
    }

    public static Drawable getRoundMaskDrawable(int normalColor, int pressedColor, int normalRadiusPx, int pressedRadiusPx) {
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{
                getRoundDrawable(normalColor, normalRadiusPx), getRoundDrawable(pressedColor, pressedRadiusPx)});
        layerDrawable.setLayerInset(1, 0, 0, 0, 0);
        return layerDrawable;
    }

    public static Drawable getRoundDrawable(int color, int radiusPx) {
        return new RoundDrawable(color, radiusPx);
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

    public static Drawable getRoundRectMaskDrawable(int normalColor, int pressedColor, int normalRadiusPx, int pressedRadiusPx, int widthPx, int heightPx) {
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{
                getRoundRectDrawable(normalColor, normalRadiusPx, widthPx, heightPx),
                getRoundRectDrawable(pressedColor, pressedRadiusPx, widthPx, heightPx)});
        layerDrawable.setLayerInset(1, 0, 0, 0, 0);
        return layerDrawable;
    }

    public static RoundRectDrawable getRoundRectDrawable(int color, int radiusPx, int widthPx, int heightPx) {
        RoundRectDrawable drawable = new RoundRectDrawable(color, radiusPx);
        drawable.setRectParams(0, 0, widthPx, heightPx);
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
