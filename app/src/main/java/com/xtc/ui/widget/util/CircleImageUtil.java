package com.xtc.ui.widget.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;

/** 圆形图片工具：生成圆形遮罩位图或把 Drawable 绘制为圆形位图。 */
public class CircleImageUtil {

    private Bitmap convertDrawableToBitmap(Drawable drawable, int width, int height) {
        PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        Paint paint = new Paint();
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(result);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, 3));
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        Bitmap mask = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        paint.reset();
        paint.setFilterBitmap(false);
        paint.setXfermode(xfermode);
        canvas.drawBitmap(mask, 0.0f, 0.0f, paint);
        paint.setXfermode(null);
        paint.setColor(-1);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        int centerX = width / 2;
        int centerY = height / 2;
        canvas.drawCircle(centerX, centerY, (centerX > centerY ? centerY : centerX) - 5, paint);
        return result;
    }

    public Bitmap createMaskBitmap(int width, int height) {
        Bitmap mask = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(mask);
        Paint paint = new Paint(1);
        paint.setColor(-16777216);
        int centerX = width / 2;
        int centerY = height / 2;
        canvas.drawCircle(centerX, centerY, centerX > centerY ? centerY : centerX, paint);
        return mask;
    }
}