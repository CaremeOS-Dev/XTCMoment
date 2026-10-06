package com.xtc.moment.module.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.xtc.log.LogUtil;
import com.xtc.utils.ui.DimenUtil;

import java.security.MessageDigest;

/**
 * Glide transformation that scales and crops the source bitmap to the requested output size and
 * then draws it with rounded corners.
 *
 * <p>The crop anchor is selected by {@code scaleType}:
 * <ul>
 *     <li>{@link #MATRIX_TOP} keeps the top/left edge,</li>
 *     <li>{@link #MATRIX_BOTTOM} keeps the bottom/right edge,</li>
 *     <li>{@link #CENTER_CROP} centers the crop.</li>
 * </ul>
 */
public class GlideRoundImageView extends BitmapTransformation {

    private static final String TAG = "GlideRoundImageView";

    /** Anchors the crop at the top (or left) edge of the bitmap. */
    private static final int MATRIX_TOP = 0;
    /** Anchors the crop at the bottom (or right) edge of the bitmap. */
    private static final int MATRIX_BOTTOM = 1;
    /** Centers the crop on the bitmap. */
    private static final int CENTER_CROP = 2;

    /** Corner radius in pixels. */
    private float radius;
    /** One of {@link #MATRIX_TOP}, {@link #MATRIX_BOTTOM} or {@link #CENTER_CROP}. */
    private int scaleType;

    public GlideRoundImageView(Context context) {
        this(context, 4);
    }

    public GlideRoundImageView(Context context, int radiusDp) {
        this(context, radiusDp, MATRIX_TOP);
        this.radius = radiusDp;
    }

    public GlideRoundImageView(Context context, int radiusDp, int scaleType) {
        this.radius = 0.0f;
        this.scaleType = 0;
        this.radius = DimenUtil.dp2pxFloat(context, radiusDp);
        this.scaleType = scaleType;
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap source, int outWidth, int outHeight) {
        LogUtil.d(TAG, "outWidth:" + outWidth + ";outHeight:" + outHeight);
        return roundCrop(pool, source, outWidth, outHeight);
    }

    /** Scales the bitmap to the output size, crops it and applies the rounded corners. */
    private Bitmap roundCrop(BitmapPool pool, Bitmap source, int outWidth, int outHeight) {
        if (source == null) {
            return null;
        }
        Bitmap scaled = changeBitmapSize(source, outWidth, outHeight);
        Bitmap target = pool.get(scaled.getWidth(), scaled.getHeight(), Bitmap.Config.ARGB_4444);
        if (target == null) {
            target = Bitmap.createBitmap(scaled.getWidth(), scaled.getHeight(), Bitmap.Config.ARGB_4444);
        }
        Canvas canvas = new Canvas(target);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(scaled, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        RectF bounds = new RectF(0.0f, 0.0f, scaled.getWidth(), scaled.getHeight());
        canvas.drawRoundRect(bounds, this.radius, this.radius, paint);
        return target;
    }

    /**
     * Scales the bitmap by the larger of the two axis ratios and crops the result to the requested
     * output size, honouring {@link #scaleType}.
     */
    public Bitmap changeBitmapSize(Bitmap bitmap, int outWidth, int outHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        LogUtil.d(TAG, "width:" + width + ";height:" + height);

        float widthRatio = ((float) outWidth) / width;
        float heightRatio = ((float) outHeight) / height;
        float scale = Math.max(heightRatio, widthRatio);
        LogUtil.d(TAG, "scaleWidth:" + widthRatio + ";scaleHeight:" + heightRatio);

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        LogUtil.d(TAG, "scaleType:" + this.scaleType);

        int cropX = 0;
        int cropY = 0;
        int cropWidth = width;
        int cropHeight = height;
        if (this.scaleType == MATRIX_TOP) {
            cropWidth = width;
            cropHeight = height;
            if (heightRatio < widthRatio) {
                cropHeight = (int) (height * heightRatio);
            } else {
                cropWidth = (int) (width * widthRatio);
            }
        } else if (this.scaleType == MATRIX_BOTTOM) {
            if (heightRatio < widthRatio) {
                cropHeight = (int) (height * heightRatio);
                cropY = height - cropHeight;
            } else {
                cropWidth = (int) (width * widthRatio);
                cropX = width - cropWidth;
                cropHeight = height;
            }
        } else if (this.scaleType == CENTER_CROP) {
            if (heightRatio < widthRatio) {
                cropHeight = (int) (height * heightRatio);
                cropY = (height - cropHeight) / 2;
                cropWidth = width;
            } else {
                cropWidth = (int) (width * widthRatio);
                cropX = (width - cropWidth) / 2;
                cropHeight = height;
            }
        } else {
            cropWidth = width;
            cropHeight = height;
        }

        LogUtil.d(TAG, "x:" + cropX + ";y:" + cropY + ";width:" + cropWidth + ";height:" + cropHeight);
        Bitmap cropped = Bitmap.createBitmap(bitmap, cropX, cropY, cropWidth, cropHeight, matrix, true);
        cropped.getWidth();
        cropped.getHeight();
        LogUtil.d(TAG, "newWidth" + cropped.getWidth());
        LogUtil.d(TAG, "newHeight" + cropped.getHeight());
        return cropped;
    }
}
