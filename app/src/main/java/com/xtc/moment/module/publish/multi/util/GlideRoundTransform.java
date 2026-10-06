package com.xtc.moment.module.publish.multi.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.xtc.utils.ui.DimenUtil;

import java.security.MessageDigest;

/**
 * Center crop followed by a rounded corner mask, used by the multi picture publish preview.
 */
public class GlideRoundTransform extends CenterCrop {

    /** Default corner radius, in dp. */
    private static final int DEFAULT_RADIUS_DP = 10;

    private static float radius = 10.0f;

    public GlideRoundTransform(Context context) {
        this(context, DEFAULT_RADIUS_DP);
    }

    public GlideRoundTransform(Context context, int radiusDp) {
        radius = DimenUtil.dp2px(context, radiusDp);
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return roundCrop(pool, super.transform(pool, toTransform, outWidth, outHeight));
    }

    private static Bitmap roundCrop(BitmapPool pool, Bitmap source) {
        if (source == null) {
            return null;
        }
        Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        RectF rectF = new RectF(0.0f, 0.0f, source.getWidth(), source.getHeight());
        canvas.drawRoundRect(rectF, radius, radius, paint);
        return result;
    }

    public String getId() {
        return getClass().getName() + Math.round(radius);
    }
}