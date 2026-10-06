package com.xtc.ui.widget.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import com.xtc.ui.widget.R;

/** 高斯模糊工具（已废弃）：使用 RenderScript 对位图做模糊并叠加蒙层。 */
@Deprecated
public class GaussBlurUtils {

    public static Bitmap blur(Context context, Bitmap bitmap, float radius) {
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