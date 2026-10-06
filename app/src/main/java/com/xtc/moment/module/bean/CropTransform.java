package com.xtc.moment.module.bean;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.xtc.log.LogUtil;

import jp.wasabeef.glide.transformations.CropTransformation;

/**
 * 带自定义裁剪起点/顶点的图片变换。
 * 在原厂实现基础上额外记录裁剪起始偏移与顶部偏移，并按比例铺满目标区域。
 */
public class CropTransform extends CropTransformation {

    private int cropStart;
    private int cropTop;
    private CropTransformation.CropType cropType;
    private int width;
    private int height;

    public CropTransform(int width, int height, int cropStart, int cropTop) {
        super(width, height);
        this.cropType = CropTransformation.CropType.CENTER;
        this.width = width;
        this.height = height;
        this.cropTop = cropTop;
        this.cropStart = cropStart;
    }

    @Override
    protected Bitmap transform(Context context, BitmapPool bitmapPool, Bitmap bitmap, int outWidth, int outHeight) {
        int targetWidth = this.width;
        if (targetWidth == 0) {
            targetWidth = bitmap.getWidth();
        }
        this.width = targetWidth;

        int targetHeight = this.height;
        if (targetHeight == 0) {
            targetHeight = bitmap.getHeight();
        }
        this.height = targetHeight;

        Bitmap.Config config = bitmap.getConfig() != null ? bitmap.getConfig() : Bitmap.Config.ARGB_8888;
        Bitmap target = bitmapPool.get(this.width, this.height, config);
        target.setHasAlpha(true);

        float scale = Math.max(this.width / (float) bitmap.getWidth(), this.height / (float) bitmap.getHeight());
        float scaledWidth = bitmap.getWidth() * scale;
        float scaledHeight = scale * bitmap.getHeight();

        LogUtil.d("CropTransform", this.cropStart + "," + this.cropTop + "," + scaledHeight + "," + scaledWidth);

        float left = this.cropStart;
        float top = -this.cropTop;
        RectF destination = new RectF(left, top, left + scaledWidth, this.cropTop + scaledHeight);
        new Canvas(target).drawBitmap(bitmap, (Rect) null, destination, (Paint) null);
        return target;
    }

    @Override
    public String key() {
        return "CropTransformation(width=" + this.width + ", height=" + this.height + ", cropType=" + this.cropType + ")";
    }
}