package com.xtc.utils.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;

/**
 * Gaussian blur helper used by dialogs to blur the screen behind them.
 *
 * <p>Captures the view's drawing cache, blurs it with RenderScript and then
 * overlays {@code ic_blur_mask}.
 */
public class WatchBlurUtil {

    /** Default blur radius used by the two-argument overload. */
    private static final float DEFAULT_RADIUS = 25.0f;

    /** Blurs {@code view} at an explicit radius; returns null on failure. */
    public static Bitmap blur(View view, Context context, float radius) {
        if (view != null) {
            LogUtil.i("originView width=" + view.getWidth() + "originView height=" + view.getHeight());
            view.destroyDrawingCache();
            view.setDrawingCacheEnabled(true);
            try {
                Bitmap drawingCache = view.getDrawingCache();
                LogUtil.i("cache=" + drawingCache);
                return blur(context, drawingCache, radius);
            } catch (Exception e) {
                LogUtil.e("高斯模糊传入view宽高过大，内存不够");
                return null;
            }
        }
        LogUtil.e("传入view = null");
        return null;
    }

    /** Blurs {@code view} at {@link #DEFAULT_RADIUS}; returns null on failure. */
    public static Bitmap blur(View view, Context context) {
        if (view != null) {
            LogUtil.i("originView width=" + view.getWidth() + "originView height=" + view.getHeight());
            view.destroyDrawingCache();
            view.setDrawingCacheEnabled(true);
            try {
                Bitmap drawingCache = view.getDrawingCache();
                if (drawingCache != null && !drawingCache.isRecycled()) {
                    Rect rect = new Rect();
                    rect.left = 0;
                    rect.top = 0;
                    rect.right = view.getWidth();
                    rect.bottom = view.getHeight();
                    Bitmap source = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(source);
                    canvas.drawColor(0xFF000000);
                    canvas.drawBitmap(Bitmap.createBitmap(drawingCache), rect, rect, (Paint) null);
                    LogUtil.i("newCache=" + drawingCache);
                    return blur(context, source, DEFAULT_RADIUS);
                }
            } catch (Exception e) {
                LogUtil.e("高斯模糊传入view宽高过大，内存不够");
            }
        } else {
            LogUtil.e("传入view = null");
        }
        return null;
    }

    /** Applies the RenderScript blur and overlays the blur mask. */
    private static Bitmap blur(Context context, Bitmap bitmap, float radius) {
        Bitmap output = Bitmap.createBitmap(bitmap);
        RenderScript renderScript = RenderScript.create(context);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        Allocation input = Allocation.createFromBitmap(renderScript, bitmap);
        Allocation result = Allocation.createFromBitmap(renderScript, output);
        blur.setRadius(radius);
        blur.setInput(input);
        blur.forEach(result);
        result.copyTo(output);
        renderScript.destroy();
        Bitmap mask = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_blur_mask);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setFlags(2);
        canvas.drawBitmap(mask, new Matrix(), paint);
        return output;
    }
}
