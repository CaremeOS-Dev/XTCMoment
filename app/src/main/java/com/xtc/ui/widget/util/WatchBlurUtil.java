package com.xtc.ui.widget.util;

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
import com.xtc.ui.widget.R;

/** 手表高斯模糊工具（已废弃）：截取 View 画面并做模糊处理。 */
@Deprecated
public class WatchBlurUtil {
    private static final String TAG = "WatchBlurUtil";

    public static Bitmap getBlurBitmap(View view, Context context, float radius) {
        if (view != null) {
            LogUtil.i(TAG, "originView width=" + view.getWidth() + "originView height=" + view.getHeight());
            view.destroyDrawingCache();
            view.setDrawingCacheEnabled(true);
            try {
                Bitmap drawingCache = view.getDrawingCache();
                LogUtil.i(TAG, "cache=" + drawingCache);
                return blur(context, drawingCache, radius);
            } catch (Exception unused) {
                LogUtil.e(TAG, "高斯模糊传入view宽高过大，内存不够");
                return null;
            }
        }
        LogUtil.e(TAG, "传入view = null");
        return null;
    }

    public static Bitmap getBlurBitmap(View view, Context context) {
        if (view != null) {
            LogUtil.i(TAG, "originView width=" + view.getWidth() + "originView height=" + view.getHeight());
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
                    Bitmap snapshot = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(snapshot);
                    canvas.drawColor(context.getResources().getColor(R.color.color_000000));
                    canvas.drawBitmap(Bitmap.createBitmap(drawingCache), rect, rect, (Paint) null);
                    LogUtil.i(TAG, "newCache=" + drawingCache);
                    return blur(context, snapshot, 25.0f);
                }
            } catch (Exception unused) {
                LogUtil.e(TAG, "高斯模糊传入view宽高过大，内存不够");
            }
        } else {
            LogUtil.e(TAG, "传入view = null");
        }
        return null;
    }

    private static Bitmap blur(Context context, Bitmap bitmap, float radius) {
        Bitmap output = Bitmap.createBitmap(bitmap);
        RenderScript renderScript = RenderScript.create(context);
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        Allocation input = Allocation.createFromBitmap(renderScript, bitmap);
        Allocation result = Allocation.createFromBitmap(renderScript, output);
        blurScript.setRadius(radius);
        blurScript.setInput(input);
        blurScript.forEach(result);
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