package com.xtc.utils.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;

/** Bitmap creation, scaling, transforms, compression and blur helpers. */
public class ImageUtils {

    private ImageUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /** Compresses {@code bitmap} into a byte array. */
    public static byte[] bitmap2Bytes(Bitmap bitmap, Bitmap.CompressFormat format) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(format, 100, outputStream);
        return outputStream.toByteArray();
    }

    /** Decodes a bitmap from {@code bytes}. */
    public static Bitmap bytes2Bitmap(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /** Extracts the bitmap of a {@link BitmapDrawable}. */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        return ((BitmapDrawable) drawable).getBitmap();
    }

    /** Wraps {@code bitmap} into a {@link BitmapDrawable}. */
    public static Drawable bitmap2Drawable(Resources resources, Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        return new BitmapDrawable(resources, bitmap);
    }

    /** Compresses a drawable into a byte array. */
    public static byte[] drawable2Bytes(Drawable drawable, Bitmap.CompressFormat format) {
        if (drawable == null) {
            return null;
        }
        return bitmap2Bytes(drawable2Bitmap(drawable), format);
    }

    /** Decodes a drawable from {@code bytes}. */
    public static Drawable bytes2Drawable(Resources resources, byte[] bytes) {
        if (resources == null) {
            return null;
        }
        return bitmap2Drawable(resources, bytes2Bitmap(bytes));
    }

    /** Renders the view into a bitmap, drawing its background first. */
    public static Bitmap view2Bitmap(View view) {
        if (view == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable background = view.getBackground();
        if (background != null) {
            background.draw(canvas);
        } else {
            canvas.drawColor(-1);
        }
        view.draw(canvas);
        return bitmap;
    }

    /** Computes the {@code inSampleSize} that keeps the bitmap close to the target size. */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        if (reqWidth == 0 || reqHeight == 0) {
            return 1;
        }
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        while (true) {
            height >>= 1;
            if (height < reqHeight || (width = width >> 1) < reqWidth) {
                break;
            }
            inSampleSize <<= 1;
        }
        return inSampleSize;
    }

    /** Decodes a bitmap from a file. */
    public static Bitmap getBitmap(File file) {
        BufferedInputStream inputStream = null;
        if (file == null) {
            return null;
        }
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            PrivateUtils.close(inputStream);
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            PrivateUtils.close(inputStream);
            return null;
        }
    }

    /** Decodes a downsampled bitmap from a file. */
    public static Bitmap getBitmap(File file, int reqWidth, int reqHeight) {
        BufferedInputStream inputStream = null;
        if (file == null) {
            return null;
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            inputStream = new BufferedInputStream(new FileInputStream(file));
            BitmapFactory.decodeStream(inputStream, null, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            PrivateUtils.close(inputStream);
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            PrivateUtils.close(inputStream);
            return null;
        }
    }

    /** Decodes a bitmap from a file path. */
    public static Bitmap getBitmap(String filePath) {
        if (TextUtils.isEmpty(filePath.trim())) {
            return null;
        }
        return BitmapFactory.decodeFile(filePath);
    }

    /** Decodes a downsampled bitmap from a file path. */
    public static Bitmap getBitmap(String filePath, int reqWidth, int reqHeight) {
        if (TextUtils.isEmpty(filePath.trim())) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /** Decodes a bitmap from a stream. */
    public static Bitmap getBitmap(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }
        return BitmapFactory.decodeStream(inputStream);
    }

    /** Decodes a downsampled bitmap from a stream. */
    public static Bitmap getBitmap(InputStream inputStream, int reqWidth, int reqHeight) {
        if (inputStream == null) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(inputStream, null, options);
    }

    /** Decodes a bitmap from {@code bytes} starting at {@code offset}. */
    public static Bitmap getBitmap(byte[] bytes, int offset) {
        if (bytes.length == 0) {
            return null;
        }
        return BitmapFactory.decodeByteArray(bytes, offset, bytes.length);
    }

    /** Decodes a downsampled bitmap from {@code bytes}. */
    public static Bitmap getBitmap(byte[] bytes, int offset, int reqWidth, int reqHeight) {
        if (bytes.length == 0) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, offset, bytes.length, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(bytes, offset, bytes.length, options);
    }

    /** Decodes a bitmap from a drawable resource. */
    public static Bitmap getBitmap(Resources resources, int id) {
        if (resources == null) {
            return null;
        }
        return BitmapFactory.decodeResource(resources, id);
    }

    /** Decodes a bitmap from a file path, falling back to a drawable resource. */
    public static Bitmap getBitmap(String filePath, Resources resources, int id) {
        Bitmap bitmap = getBitmap(filePath);
        if (bitmap != null) {
            return bitmap;
        }
        if (resources == null) {
            return null;
        }
        return BitmapFactory.decodeResource(resources, id);
    }

    /** Decodes a downsampled bitmap from a drawable resource. */
    public static Bitmap getBitmap(Resources resources, int id, int reqWidth, int reqHeight) {
        if (resources == null) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, id, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(resources, id, options);
    }

    /** Decodes a bitmap from a file descriptor. */
    public static Bitmap getBitmap(FileDescriptor fileDescriptor) {
        if (fileDescriptor == null) {
            return null;
        }
        return BitmapFactory.decodeFileDescriptor(fileDescriptor);
    }

    /** Decodes a downsampled bitmap from a file descriptor. */
    public static Bitmap getBitmap(FileDescriptor fileDescriptor, int reqWidth, int reqHeight) {
        if (fileDescriptor == null) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
    }

    /** Scales {@code src} to the given size. */
    public static Bitmap scale(Bitmap src, int newWidth, int newHeight) {
        return scale(src, newWidth, newHeight, false);
    }

    /** Scales {@code src}, optionally recycling the source. */
    public static Bitmap scale(Bitmap src, int newWidth, int newHeight, boolean recycleSource) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Bitmap scaled = Bitmap.createScaledBitmap(src, newWidth, newHeight, true);
        if (recycleSource && !src.isRecycled()) {
            src.recycle();
        }
        return scaled;
    }

    /** Scales {@code src} by the given factors. */
    public static Bitmap scale(Bitmap src, float scaleWidth, float scaleHeight) {
        return scale(src, scaleWidth, scaleHeight, false);
    }

    /** Scales {@code src} by the given factors, optionally recycling the source. */
    public static Bitmap scale(Bitmap src, float scaleWidth, float scaleHeight, boolean recycleSource) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.setScale(scaleWidth, scaleHeight);
        Bitmap scaled = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if (recycleSource && !src.isRecycled()) {
            src.recycle();
        }
        return scaled;
    }

    /** Crops {@code src} to the given rectangle. */
    public static Bitmap crop(Bitmap src, int x, int y, int width, int height) {
        return crop(src, x, y, width, height, false);
    }

    /** Crops {@code src}, optionally recycling the source. */
    public static Bitmap crop(Bitmap src, int x, int y, int width, int height, boolean recycleSource) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Bitmap cropped = Bitmap.createBitmap(src, x, y, width, height);
        if (recycleSource && !src.isRecycled()) {
            src.recycle();
        }
        return cropped;
    }

    /** Skews {@code src} by the given factors. */
    public static Bitmap skew(Bitmap src, float kx, float ky) {
        return skew(src, kx, ky, 0.0f, 0.0f, false);
    }

    /** Skews {@code src}, optionally recycling the source. */
    public static Bitmap skew(Bitmap src, float kx, float ky, boolean recycleSource) {
        return skew(src, kx, ky, 0.0f, 0.0f, recycleSource);
    }

    /** Skews {@code src} around the pivot. */
    public static Bitmap skew(Bitmap src, float kx, float ky, float px, float py) {
        return skew(src, kx, ky, px, py, false);
    }

    /** Skews {@code src} around the pivot, optionally recycling the source. */
    public static Bitmap skew(Bitmap src, float kx, float ky, float px, float py, boolean recycleSource) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.setSkew(kx, ky, px, py);
        Bitmap skewed = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if (recycleSource && !src.isRecycled()) {
            src.recycle();
        }
        return skewed;
    }

    /** Rotates {@code src} around the pivot. */
    public static Bitmap rotate(Bitmap src, int degrees, float px, float py) {
        return rotate(src, degrees, px, py, false);
    }

    /** Rotates {@code src} around the pivot, optionally recycling the source. */
    public static Bitmap rotate(Bitmap src, int degrees, float px, float py, boolean recycleSource) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        if (degrees == 0) {
            return src;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, px, py);
        Bitmap rotated = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if (recycleSource && !src.isRecycled()) {
            src.recycle();
        }
        return rotated;
    }

    /** Reads the EXIF orientation of the image and maps it to a rotation degree. */
    public static int getRotateDegree(String filePath) {
        try {
            int orientation = new ExifInterface(filePath).getAttributeInt(android.support.media.ExifInterface.TAG_ORIENTATION, 1);
            if (orientation != 3) {
                return orientation != 8 ? 90 : 270;
            }
            return 180;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /** Crops {@code src} into a circle. */
    public static Bitmap toRound(Bitmap src) {
        return toRound(src, false);
    }

    /** Crops {@code src} into a circle, optionally recycling the source. */
    public static Bitmap toRound(Bitmap src, boolean recycleSource) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        int width = src.getWidth();
        int height = src.getHeight();
        int radius = Math.min(width, height) >> 1;
        Bitmap bitmap = Bitmap.createBitmap(width, height, src.getConfig());
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bitmap);
        Rect rect = new Rect(0, 0, width, height);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(width >> 1, height >> 1, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(src, rect, rect, paint);
        if (recycleSource && !src.isRecycled()) {
            src.recycle();
        }
        return bitmap;
    }

    /** Rounds the corners of {@code src}. */
    public static Bitmap toRoundCorner(Bitmap src, float radius) {
        return toRoundCorner(src, radius, false);
    }

    /** Rounds the corners of {@code src}, optionally recycling the source. */
    public static Bitmap toRoundCorner(Bitmap src, float radius, boolean recycleSource) {
        if (src == null) {
            return null;
        }
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, src.getConfig());
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bitmap);
        Rect rect = new Rect(0, 0, width, height);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(new RectF(rect), radius, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(src, rect, rect, paint);
        if (recycleSource && !src.isRecycled()) {
            src.recycle();
        }
        return bitmap;
    }
    /** Scales then blurs {@code src}, restoring the original size afterwards. */
    public static Bitmap scaleAndBlur(Context context, Bitmap src, float scaleFactor, float radius) {
        return scaleAndBlur(context, src, scaleFactor, radius, false);
    }

    /** Scales then blurs {@code src}, restoring the original size afterwards. */
    public static Bitmap scaleAndBlur(Context context, Bitmap src, float scaleFactor, float radius, boolean recycleSource) {
        Bitmap blurred;
        if (isEmptyBitmap(src)) {
            return null;
        }
        int width = src.getWidth();
        int height = src.getHeight();
        int scaledWidth = (int) ((width * scaleFactor) + 0.5f);
        int scaledHeight = (int) ((height * scaleFactor) + 0.5f);
        if (scaledWidth == 0 || scaledHeight == 0) {
            return null;
        }
        Bitmap scaled = Bitmap.createScaledBitmap(src, scaledWidth, scaledHeight, true);
        Paint paint = new Paint(3);
        Canvas canvas = new Canvas();
        paint.setColorFilter(new PorterDuffColorFilter(0, PorterDuff.Mode.SRC_ATOP));
        canvas.scale(scaleFactor, scaleFactor);
        canvas.drawBitmap(scaled, 0.0f, 0.0f, paint);
        if (Build.VERSION.SDK_INT >= 17) {
            blurred = blur(context, scaled, radius);
        } else {
            blurred = compressByQuality(scaled, (int) radius, recycleSource);
        }
        if (scaleFactor == 1.0f) {
            return blurred;
        }
        Bitmap restored = Bitmap.createScaledBitmap(blurred, width, height, true);
        if (blurred != null && !blurred.isRecycled()) {
            blurred.recycle();
        }
        if (recycleSource && !src.isRecycled()) {
            src.recycle();
        }
        return restored;
    }

    /** Blurs {@code src} with RenderScript. */
    public static Bitmap blur(Context context, Bitmap src, float radius) {
        RenderScript renderScript = null;
        if (isEmptyBitmap(src)) {
            return null;
        }
        try {
            renderScript = RenderScript.create(context);
            renderScript.setMessageHandler(new RenderScript.RSMessageHandler());
            Allocation input = Allocation.createFromBitmap(renderScript, src, Allocation.MipmapControl.MIPMAP_NONE, 1);
            Allocation output = Allocation.createTyped(renderScript, input.getType());
            ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
            if (radius > 25.0f) {
                radius = 25.0f;
            } else if (radius <= 0.0f) {
                radius = 1.0f;
            }
            blur.setInput(input);
            blur.setRadius(radius);
            blur.forEach(output);
            output.copyTo(src);
            return src;
        } finally {
            if (renderScript != null) {
                renderScript.destroy();
            }
        }
    }

    /** Blurs {@code src} with a stack blur, optionally in place. */
    public static Bitmap blurByStack(Bitmap src, int radius, boolean inPlace) {
        Bitmap source = inPlace ? src : src.copy(src.getConfig(), true);
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
        return source;
    }

    /** Adds a colored border around {@code src}. */
    public static Bitmap addBorder(Bitmap src, int borderWidth, int color) {
        return addBorder(src, borderWidth, color, false);
    }

    /** Adds a colored border around {@code src}, optionally recycling the source. */
    public static Bitmap addBorder(Bitmap src, int borderWidth, int color, boolean recycleSource) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        int totalWidth = borderWidth << 1;
        int width = src.getWidth() + totalWidth;
        int height = src.getHeight() + totalWidth;
        Bitmap bitmap = Bitmap.createBitmap(width, height, src.getConfig());
        Canvas canvas = new Canvas(bitmap);
        Rect rect = new Rect(0, 0, width, height);
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(totalWidth);
        canvas.drawRect(rect, paint);
        float offset = borderWidth;
        canvas.drawBitmap(src, offset, offset, (Paint) null);
        if (recycleSource && !src.isRecycled()) {
            src.recycle();
        }
        return bitmap;
    }

    /** Adds a mirrored reflection below {@code src}. */
    public static Bitmap addReflection(Bitmap src, int reflectionHeight) {
        return addReflection(src, reflectionHeight, false);
    }

    /** Adds a mirrored reflection below {@code src}, optionally recycling the source. */
    public static Bitmap addReflection(Bitmap src, int reflectionHeight, boolean recycleSource) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        int width = src.getWidth();
        int height = src.getHeight();
        if (width == 0 || height == 0) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.preScale(1.0f, -1.0f);
        Bitmap reflection = Bitmap.createBitmap(src, 0, height - reflectionHeight, width, reflectionHeight, matrix, false);
        if (reflection == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height + reflectionHeight, src.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(src, 0.0f, 0.0f, (Paint) null);
        canvas.drawBitmap(reflection, 0.0f, height + 0, (Paint) null);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        float gradientStart = height;
        paint.setShader(new LinearGradient(0.0f, gradientStart, 0.0f, bitmap.getHeight() + 0, 1895825407, ViewCompat.MEASURED_SIZE_MASK, Shader.TileMode.MIRROR));
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.save();
        canvas.drawRect(0.0f, gradientStart, width, bitmap.getHeight() + 0, paint);
        canvas.restore();
        if (!reflection.isRecycled()) {
            reflection.recycle();
        }
        if (recycleSource && !src.isRecycled()) {
            src.recycle();
        }
        return bitmap;
    }

    /** Draws {@code text} onto a copy of {@code src}. */
    public static Bitmap addTextWatermark(Bitmap src, String text, int color, int fontSize, float x, float y) {
        return addTextWatermark(src, text, color, fontSize, x, y, false);
    }

    /** Draws {@code text} onto a copy of {@code src}, optionally recycling the source. */
    public static Bitmap addTextWatermark(Bitmap src, String text, float size, int color, float x, float y, boolean recycleSource) {
        if (isEmptyBitmap(src) || text == null) {
            return null;
        }
        Bitmap bitmap = src.copy(src.getConfig(), true);
        Paint paint = new Paint(1);
        Canvas canvas = new Canvas(bitmap);
        paint.setColor(color);
        paint.setTextSize(size);
        paint.getTextBounds(text, 0, text.length(), new Rect());
        canvas.drawText(text, x, y + size, paint);
        if (recycleSource && !src.isRecycled()) {
            src.recycle();
        }
        return bitmap;
    }

    /** Overlays {@code watermark} onto a copy of {@code src}. */
    public static Bitmap addImageWatermark(Bitmap src, Bitmap watermark, int x, int y, int alpha) {
        return addImageWatermark(src, watermark, x, y, alpha, false);
    }

    /** Overlays {@code watermark} onto a copy of {@code src}, optionally recycling the source. */
    public static Bitmap addImageWatermark(Bitmap src, Bitmap watermark, int x, int y, int alpha, boolean recycleSource) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Bitmap bitmap = src.copy(src.getConfig(), true);
        if (!isEmptyBitmap(watermark)) {
            Paint paint = new Paint(1);
            Canvas canvas = new Canvas(bitmap);
            paint.setAlpha(alpha);
            canvas.drawBitmap(watermark, x, y, paint);
        }
        if (recycleSource && !src.isRecycled()) {
            src.recycle();
        }
        return bitmap;
    }

    /** Extracts the alpha channel of {@code src}. */
    public static Bitmap toAlpha(Bitmap src) {
        return toAlpha(src, false);
    }

    /** Extracts the alpha channel of {@code src}, optionally recycling the source. */
    public static Bitmap toAlpha(Bitmap src, Boolean recycleSource) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Bitmap alpha = src.extractAlpha();
        if (recycleSource.booleanValue() && !src.isRecycled()) {
            src.recycle();
        }
        return alpha;
    }

    /** Converts {@code src} to grayscale. */
    public static Bitmap toGray(Bitmap src) {
        return toGray(src, false);
    }

    /** Converts {@code src} to grayscale, optionally recycling the source. */
    public static Bitmap toGray(Bitmap src, boolean recycleSource) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0.0f);
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(src, 0.0f, 0.0f, paint);
        if (recycleSource && !src.isRecycled()) {
            src.recycle();
        }
        return bitmap;
    }
    /** Saves {@code src} to the given file path. */
    public static boolean save(Bitmap src, String filePath, Bitmap.CompressFormat format) {
        return save(src, PrivateUtils.toFile(filePath), format, false);
    }

    /** Saves {@code src} to the given file. */
    public static boolean save(Bitmap src, File file, Bitmap.CompressFormat format) {
        return save(src, file, format, false);
    }

    /** Saves {@code src} to the given file path, optionally recycling the source. */
    public static boolean save(Bitmap src, String filePath, Bitmap.CompressFormat format, boolean recycleSource) {
        return save(src, PrivateUtils.toFile(filePath), format, recycleSource);
    }

    /** Saves {@code src} to the given file, optionally recycling the source. */
    public static boolean save(Bitmap src, File file, Bitmap.CompressFormat format, boolean recycleSource) {
        boolean compressed;
        if (isEmptyBitmap(src) || !PrivateUtils.createFile(file)) {
            return false;
        }
        System.out.println(src.getWidth() + ", " + src.getHeight());
        BufferedOutputStream outputStream = null;
        try {
            try {
                outputStream = new BufferedOutputStream(new FileOutputStream(file));
                try {
                    compressed = src.compress(format, 100, outputStream);
                    if (recycleSource) {
                        try {
                            if (!src.isRecycled()) {
                                src.recycle();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            PrivateUtils.close(outputStream);
                        }
                    }
                    PrivateUtils.close(outputStream);
                } catch (Throwable throwable) {
                    PrivateUtils.close(outputStream);
                    throw throwable;
                }
            } catch (IOException e) {
                compressed = false;
                e.printStackTrace();
                PrivateUtils.close(outputStream);
                return compressed;
            }
            return compressed;
        } catch (Throwable throwable) {
            PrivateUtils.close(outputStream);
            throw throwable;
        }
    }

    /** @return true when the file name has a recognized image suffix. */
    public static boolean isImage(File file) {
        return file != null && isImage(file.getPath());
    }

    /** @return true when the path has a recognized image suffix. */
    public static boolean isImage(String filePath) {
        String upperCase = filePath.toUpperCase();
        return upperCase.endsWith(".PNG") || upperCase.endsWith(".JPG") || upperCase.endsWith(".JPEG") || upperCase.endsWith(".BMP") || upperCase.endsWith(".GIF");
    }

    /** Detects the image type of a file path. */
    public static String getImageType(String filePath) {
        return getImageType(PrivateUtils.toFile(filePath));
    }

    /** Detects the image type from the first bytes of the file. */
    public static String getImageType(File file) {
        FileInputStream inputStream = null;
        if (file == null) {
            return null;
        }
        try {
            inputStream = new FileInputStream(file);
            try {
                String type = getImageType(inputStream);
                PrivateUtils.close(inputStream);
                return type;
            } catch (IOException e) {
                e.printStackTrace();
                PrivateUtils.close(inputStream);
                return null;
            }
        } catch (IOException e) {
            PrivateUtils.close(inputStream);
            return null;
        }
    }

    /** Detects the image type from a stream. */
    public static String getImageType(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }
        try {
            byte[] header = new byte[8];
            if (inputStream.read(header, 0, 8) != -1) {
                return getImageType(header);
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Detects the image type from the leading bytes. */
    public static String getImageType(byte[] header) {
        if (isJpeg(header)) {
            return "JPEG";
        }
        if (isGif(header)) {
            return "GIF";
        }
        if (isPng(header)) {
            return "PNG";
        }
        if (isBmp(header)) {
            return "BMP";
        }
        return null;
    }

    /** @return true when the bytes are a JPEG header. */
    private static boolean isJpeg(byte[] header) {
        return header.length >= 2 && header[0] == -1 && header[1] == -40;
    }

    /** @return true when the bytes are a GIF header. */
    private static boolean isGif(byte[] header) {
        return header.length >= 6 && header[0] == 71 && header[1] == 73 && header[2] == 70 && header[3] == 56 && (header[4] == 55 || header[4] == 57) && header[5] == 97;
    }

    /** @return true when the bytes are a PNG header. */
    private static boolean isPng(byte[] header) {
        return header.length >= 8 && header[0] == -119 && header[1] == 80 && header[2] == 78 && header[3] == 71 && header[4] == 13 && header[5] == 10 && header[6] == 26 && header[7] == 10;
    }

    /** @return true when the bytes are a BMP header. */
    private static boolean isBmp(byte[] header) {
        return header.length >= 2 && header[0] == 66 && header[1] == 77;
    }

    /** @return true when the bitmap is null or has no pixels. */
    private static boolean isEmptyBitmap(Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }

    /** Scales the bitmap to the given size. */
    public static Bitmap compressByScale(Bitmap src, int newWidth, int newHeight) {
        return scale(src, newWidth, newHeight, false);
    }

    /** Scales the bitmap to the given size, optionally recycling the source. */
    public static Bitmap compressByScale(Bitmap src, int newWidth, int newHeight, boolean recycleSource) {
        return scale(src, newWidth, newHeight, recycleSource);
    }

    /** Scales the bitmap by the given factors. */
    public static Bitmap compressByScale(Bitmap src, float scaleWidth, float scaleHeight) {
        return scale(src, scaleWidth, scaleHeight, false);
    }

    /** Scales the bitmap by the given factors, optionally recycling the source. */
    public static Bitmap compressByScale(Bitmap src, float scaleWidth, float scaleHeight, boolean recycleSource) {
        return scale(src, scaleWidth, scaleHeight, recycleSource);
    }

    /** Compresses the bitmap to the given JPEG quality. */
    public static Bitmap compressByQuality(Bitmap src, int quality) {
        return compressByQuality(src, quality, false);
    }

    /** Compresses the bitmap to the given JPEG quality, optionally recycling the source. */
    public static Bitmap compressByQuality(Bitmap src, int quality, boolean recycleSource) {
        if (isEmptyBitmap(src) || quality < 0 || quality > 100) {
            return null;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
        byte[] bytes = outputStream.toByteArray();
        if (recycleSource && !src.isRecycled()) {
            src.recycle();
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /** Compresses the bitmap until it fits within {@code maxByteSize}. */
    public static Bitmap compressBySize(Bitmap src, long maxByteSize) {
        return compressBySize(src, maxByteSize, false);
    }

    /** Compresses the bitmap until it fits within {@code maxByteSize}, optionally recycling the source. */
    public static Bitmap compressBySize(Bitmap src, long maxByteSize, boolean recycleSource) {
        if (isEmptyBitmap(src) || maxByteSize <= 0) {
            return null;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int quality = 100;
        src.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        while (outputStream.toByteArray().length > maxByteSize && quality >= 0) {
            outputStream.reset();
            quality -= 5;
            src.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
        }
        if (quality < 0) {
            return null;
        }
        byte[] bytes = outputStream.toByteArray();
        if (recycleSource && !src.isRecycled()) {
            src.recycle();
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /** Compresses the bitmap using the given sample size. */
    public static Bitmap compressBySampleSize(Bitmap src, int sampleSize) {
        return compressBySampleSize(src, sampleSize, false);
    }

    /** Compresses the bitmap using the given sample size, optionally recycling the source. */
    public static Bitmap compressBySampleSize(Bitmap src, int sampleSize, boolean recycleSource) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] bytes = outputStream.toByteArray();
        if (recycleSource && !src.isRecycled()) {
            src.recycle();
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }
}