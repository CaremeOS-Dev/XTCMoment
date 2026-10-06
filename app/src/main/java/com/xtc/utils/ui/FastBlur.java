package com.xtc.utils.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.view.MotionEventCompat;
import android.view.View;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;

import java.lang.reflect.Array;

/**
 * Blur helper supporting both RenderScript and a pure-Java stack blur, then
 * overlaying the {@code ic_blur_mask} drawable.
 */
public class FastBlur {

    /** Stack-blur implementation. */
    public static final int BLUR_TYPE_STACK = 1;

    /** RenderScript implementation. */
    public static final int BLUR_TYPE_RENDER_SCRIPT = 2;

    private static final String TAG = "FastBlur";

    /** Default blur radius. */
    private static final int DEFAULT_RADIUS = 25;

    /** Default blur implementation. */
    private static final int DEFAULT_BLUR_TYPE = 2;

    /** Sets the blurred background drawable on the view. */
    public static void setBackground(View view, BitmapDrawable drawable) {
        if (Build.VERSION.SDK_INT < 16) {
            view.setBackgroundDrawable(drawable);
        } else {
            view.setBackground(drawable);
        }
    }

    /** Wraps {@code bitmap} into a {@link BitmapDrawable}. */
    public static BitmapDrawable toDrawable(Context context, Bitmap bitmap) {
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    /** Captures the view and blurs it with the default radius. */
    public static Bitmap blur(Context context, View view, boolean recycleSource) {
        return blur(context, view, DEFAULT_RADIUS, recycleSource);
    }

    /** Captures the view and blurs it with the given radius. */
    public static Bitmap blur(Context context, View view, int radius, boolean recycleSource) {
        Bitmap blurred = blur(context, getViewBitmap(view), radius, recycleSource);
        recycleViewCache(view);
        return blurred;
    }

    /** Captures the current content of the view into a bitmap. */
    public static Bitmap getViewBitmap(View view) {
        long startTime = System.currentTimeMillis();
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap drawingCache = view.getDrawingCache();
        LogUtil.d("getViewBitmap spend time = " + (System.currentTimeMillis() - startTime));
        return drawingCache;
    }

    /** Disables the drawing cache of the view. */
    public static void recycleViewCache(View view) {
        view.setDrawingCacheEnabled(false);
    }

    /** Blurs {@code bitmap} with the default radius. */
    public static Bitmap blur(Context context, Bitmap bitmap, boolean recycleSource) {
        return blur(context, bitmap, DEFAULT_RADIUS, recycleSource);
    }

    /** Blurs {@code bitmap} with the given radius using the default implementation. */
    public static Bitmap blur(Context context, Bitmap bitmap, int radius, boolean recycleSource) {
        return blur(context, bitmap, radius, recycleSource, BLUR_TYPE_STACK);
    }

    /** Blurs {@code bitmap}, downscaling by half when requested and overlaying the mask. */
    public static Bitmap blur(Context context, Bitmap bitmap, int radius, boolean recycleSource, int blurType) {
        Bitmap result;
        long startTime = System.currentTimeMillis();
        Bitmap mask = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_blur_mask);
        if (recycleSource) {
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, true);
            radius /= 2;
        }
        if (bitmap != null) {
            Bitmap blurred = doBlur(context, bitmap, radius, blurType);
            result = Bitmap.createBitmap(blurred.getWidth(), blurred.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setFlags(2);
            canvas.drawBitmap(blurred, 0.0f, 0.0f, paint);
            canvas.drawBitmap(mask, 0.0f, 0.0f, paint);
        } else {
            result = null;
        }
        LogUtil.d("getBlurBitmap spend time = " + (System.currentTimeMillis() - startTime));
        return result;
    }

    /** Dispatches to the RenderScript or stack implementation. */
    private static Bitmap doBlur(Context context, Bitmap bitmap, int radius, int blurType) {
        if (blurType == BLUR_TYPE_RENDER_SCRIPT) {
            return blurByRenderScript(context, bitmap, radius);
        }
        return blurByStack(bitmap, radius, true);
    }

    /** Blurs {@code bitmap} with RenderScript. */
    private static Bitmap blurByRenderScript(Context context, Bitmap bitmap, int radius) {
        long startTime = System.currentTimeMillis();
        RenderScript renderScript = RenderScript.create(context);
        Allocation input = Allocation.createFromBitmap(renderScript, bitmap);
        Allocation output = Allocation.createTyped(renderScript, input.getType());
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        blur.setInput(input);
        blur.setRadius(radius);
        blur.forEach(output);
        output.copyTo(bitmap);
        renderScript.destroy();
        LogUtil.d(TAG, "doBlurByRenderScript: spend time: " + (System.currentTimeMillis() - startTime));
        return bitmap;
    }

    /** Blurs {@code bitmap} with a stack blur, optionally in place. */
    private static Bitmap blurByStack(Bitmap bitmap, int radius, boolean inPlace) {
        long startTime = System.currentTimeMillis();
        Bitmap source = inPlace ? bitmap : bitmap.copy(bitmap.getConfig(), true);
        if (radius < 1) {
            return null;
        }
        int width = source.getWidth();
        int height = source.getHeight();
        int pixelCount = width * height;
        int[] pixels = new int[pixelCount];
        source.getPixels(pixels, 0, width, 0, 0, width, height);
        int lastX = width - 1;
        int lastY = height - 1;
        int diameter = radius + radius + 1;
        int[] red = new int[pixelCount];
        int[] green = new int[pixelCount];
        int[] blue = new int[pixelCount];
        int[] rowAccumulator = new int[Math.max(width, height)];
        int halfDiameter = (diameter + 1) >> 1;
        int divisor = halfDiameter * halfDiameter;
        int tableSize = divisor * 256;
        int[] divideTable = new int[tableSize];
        for (int index = 0; index < tableSize; index++) {
            divideTable[index] = index / divisor;
        }
        int[][] columnKernel = (int[][]) Array.newInstance((Class<?>) int.class, diameter, 3);
        int kernelWeightBase = radius + 1;
        int row = 0;
        int rowOffset = 0;
        int columnOffset = 0;
        while (row < height) {
            int radiusDelta = -radius;
            int redSum = 0;
            int greenSum = 0;
            int blueSum = 0;
            int redIn = 0;
            int greenIn = 0;
            int blueIn = 0;
            int redOut = 0;
            int greenOut = 0;
            int blueOut = 0;
            while (radiusDelta <= radius) {
                int pixel = pixels[rowOffset + Math.min(lastX, Math.max(radiusDelta, 0))];
                int[] kernel = columnKernel[radiusDelta + radius];
                kernel[0] = (pixel & 0xFF0000) >> 16;
                kernel[1] = (pixel & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8;
                kernel[2] = pixel & 0xFF;
                int weight = kernelWeightBase - Math.abs(radiusDelta);
                redSum += kernel[0] * weight;
                greenSum += kernel[1] * weight;
                blueSum += kernel[2] * weight;
                if (radiusDelta > 0) {
                    redIn += kernel[0];
                    greenIn += kernel[1];
                    blueIn += kernel[2];
                } else {
                    redOut += kernel[0];
                    greenOut += kernel[1];
                    blueOut += kernel[2];
                }
                radiusDelta++;
            }
            int kernelIndex = radius;
            int column = 0;
            while (column < width) {
                red[rowOffset] = divideTable[redSum];
                green[rowOffset] = divideTable[greenSum];
                blue[rowOffset] = divideTable[blueSum];
                int redMinusOut = redSum - redOut;
                int greenMinusOut = greenSum - greenOut;
                int blueMinusOut = blueSum - blueOut;
                int[] outKernel = columnKernel[((kernelIndex - radius) + diameter) % diameter];
                int redRemove = redOut - outKernel[0];
                int greenRemove = greenOut - outKernel[1];
                int blueRemove = blueOut - outKernel[2];
                if (row == 0) {
                    rowAccumulator[column] = Math.min(column + radius + 1, lastX);
                }
                int pixel = pixels[columnOffset + rowAccumulator[column]];
                outKernel[0] = (pixel & 0xFF0000) >> 16;
                outKernel[1] = (pixel & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8;
                outKernel[2] = pixel & 0xFF;
                int redAdd = redIn + outKernel[0];
                int greenAdd = greenIn + outKernel[1];
                int blueAdd = blueIn + outKernel[2];
                redSum = redMinusOut + redAdd;
                greenSum = greenMinusOut + greenAdd;
                blueSum = blueMinusOut + blueAdd;
                kernelIndex = (kernelIndex + 1) % diameter;
                int[] inKernel = columnKernel[kernelIndex % diameter];
                redOut = redRemove + inKernel[0];
                greenOut = greenRemove + inKernel[1];
                blueOut = blueRemove + inKernel[2];
                redIn = redAdd - inKernel[0];
                greenIn = greenAdd - inKernel[1];
                blueIn = blueAdd - inKernel[2];
                rowOffset++;
                column++;
            }
            columnOffset += width;
            row++;
        }
        int column = 0;
        while (column < width) {
            int radiusDelta = -radius;
            int columnStride = radiusDelta * width;
            int redSum = 0;
            int greenSum = 0;
            int blueSum = 0;
            int redIn = 0;
            int greenIn = 0;
            int blueIn = 0;
            int redOut = 0;
            int greenOut = 0;
            int blueOut = 0;
            while (radiusDelta <= radius) {
                int index = Math.max(0, columnStride) + column;
                int[] kernel = columnKernel[radiusDelta + radius];
                kernel[0] = red[index];
                kernel[1] = green[index];
                kernel[2] = blue[index];
                int weight = kernelWeightBase - Math.abs(radiusDelta);
                redSum += red[index] * weight;
                greenSum += green[index] * weight;
                blueSum += blue[index] * weight;
                if (radiusDelta > 0) {
                    redIn += kernel[0];
                    greenIn += kernel[1];
                    blueIn += kernel[2];
                } else {
                    redOut += kernel[0];
                    greenOut += kernel[1];
                    blueOut += kernel[2];
                }
                if (radiusDelta < lastY) {
                    columnStride += width;
                }
                radiusDelta++;
            }
            int rowIndex = column;
            int kernelIndex = radius;
            for (int y = 0; y < height; y++) {
                pixels[rowIndex] = (pixels[rowIndex] & 0xFF000000) | (divideTable[redSum] << 16) | (divideTable[greenSum] << 8) | divideTable[blueSum];
                int redMinusOut = redSum - redOut;
                int greenMinusOut = greenSum - greenOut;
                int blueMinusOut = blueSum - blueOut;
                int[] outKernel = columnKernel[((kernelIndex - radius) + diameter) % diameter];
                int redRemove = redOut - outKernel[0];
                int greenRemove = greenOut - outKernel[1];
                int blueRemove = blueOut - outKernel[2];
                if (column == 0) {
                    rowAccumulator[y] = Math.min(y + kernelWeightBase, lastY) * width;
                }
                int index = rowAccumulator[y] + column;
                outKernel[0] = red[index];
                outKernel[1] = green[index];
                outKernel[2] = blue[index];
                int redAdd = redIn + outKernel[0];
                int greenAdd = greenIn + outKernel[1];
                int blueAdd = blueIn + outKernel[2];
                redSum = redMinusOut + redAdd;
                greenSum = greenMinusOut + greenAdd;
                blueSum = blueMinusOut + blueAdd;
                kernelIndex = (kernelIndex + 1) % diameter;
                int[] inKernel = columnKernel[kernelIndex];
                redOut = redRemove + inKernel[0];
                greenOut = greenRemove + inKernel[1];
                blueOut = blueRemove + inKernel[2];
                redIn = redAdd - inKernel[0];
                greenIn = greenAdd - inKernel[1];
                blueIn = blueAdd - inKernel[2];
                rowIndex += width;
            }
            column++;
        }
        source.setPixels(pixels, 0, width, 0, 0, width, height);
        LogUtil.d("doBlur spend time = " + (System.currentTimeMillis() - startTime));
        return source;
    }
}