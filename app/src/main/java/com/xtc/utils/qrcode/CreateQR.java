package com.xtc.utils.qrcode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.nio.charset.Charset;
import java.util.HashMap;

/** Renders QR codes with an optional centre logo. */
public class CreateQR {

    /** Default foreground colour used by {@link #createBitmap(String)}. */
    private static final int DEFAULT_FOREGROUND = 0xFF07462A;
    /** Default background colour. */
    private static final int DEFAULT_BACKGROUND = 0xFFFFFFFF;

    private int width;
    private int height;

    public CreateQR() {
        this.width = 100;
        this.height = 100;
    }

    public CreateQR(int width, int height) {
        this.width = 100;
        this.height = 100;
        this.width = width;
        this.height = height;
    }

    /** Renders {@code content} with the default colours. */
    public Bitmap createBitmap(String content) {
        return createBitmap(content, DEFAULT_FOREGROUND, DEFAULT_BACKGROUND);
    }

    /** Renders {@code content} with a logo drawn in the centre. */
    public Bitmap createBitmap(String content, Bitmap logo) {
        if (content == null || "".equals(content)) {
            return null;
        }
        try {
            BitMatrix matrix = encode(content);
            int[] pixels = new int[this.width * this.height];
            for (int y = 0; y < this.height; y++) {
                for (int x = 0; x < this.width; x++) {
                    pixels[(this.width * y) + x] = matrix.get(x, y) ? 0xFF000000 : DEFAULT_BACKGROUND;
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.RGB_565);
            bitmap.setPixels(pixels, 0, this.width, 0, 0, this.width, this.height);
            return logo != null ? overlayLogo(bitmap, logo) : bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Renders {@code content} with explicit foreground/background colours. */
    public Bitmap createBitmap(String content, int foregroundColor, int backgroundColor) {
        if (content == null || "".equals(content) || content.length() < 1) {
            return null;
        }
        try {
            BitMatrix matrix = encode(content);
            int[] pixels = new int[this.width * this.height];
            for (int y = 0; y < this.height; y++) {
                for (int x = 0; x < this.width; x++) {
                    pixels[(this.width * y) + x] = matrix.get(x, y) ? foregroundColor : backgroundColor;
                }
            }
            return paint(pixels);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Renders {@code content} with explicit colours and a centre logo. */
    public Bitmap createBitmap(String content, Bitmap logo, int foregroundColor, int backgroundColor) {
        if (content == null || "".equals(content)) {
            return null;
        }
        try {
            BitMatrix matrix = encode(content);
            int[] pixels = new int[this.width * this.height];
            for (int y = 0; y < this.height; y++) {
                for (int x = 0; x < this.width; x++) {
                    pixels[(this.width * y) + x] = matrix.get(x, y) ? foregroundColor : backgroundColor;
                }
            }
            Bitmap bitmap = paint(pixels);
            return logo != null ? overlayLogo(bitmap, logo) : bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Encodes the content into a bit matrix using high error correction. */
    private BitMatrix encode(String content) throws WriterException {
        HashMap<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, Charset.forName("UTF-8").name());
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 1);
        return new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, this.width, this.height, hints);
    }

    /** Converts the pixel buffer into an RGB_565 bitmap. */
    private Bitmap paint(int[] pixels) {
        Bitmap bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.RGB_565);
        if (bitmap == null) {
            return null;
        }
        bitmap.setPixels(pixels, 0, this.width, 0, 0, this.width, this.height);
        return bitmap;
    }

    /** Scales the logo to one fifth of the QR width and centres it. */
    private Bitmap overlayLogo(Bitmap qrBitmap, Bitmap logo) {
        if (qrBitmap == null) {
            return null;
        }
        if (logo == null) {
            return qrBitmap;
        }
        int qrWidth = qrBitmap.getWidth();
        int qrHeight = qrBitmap.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();
        if (qrWidth == 0 || qrHeight == 0) {
            return null;
        }
        if (logoWidth == 0 || logoHeight == 0) {
            return qrBitmap;
        }
        float scale = ((qrWidth * 1.0f) / 5.0f) / logoWidth;
        Bitmap output = Bitmap.createBitmap(qrWidth, qrHeight, Bitmap.Config.RGB_565);
        try {
            Canvas canvas = new Canvas(output);
            canvas.drawBitmap(qrBitmap, 0.0f, 0.0f, (Paint) null);
            canvas.scale(scale, scale, qrWidth / 2, qrHeight / 2);
            canvas.drawBitmap(logo, (qrWidth - logoWidth) / 2, (qrHeight - logoHeight) / 2, (Paint) null);
            canvas.save();
            canvas.restore();
            return output;
        } catch (Exception e) {
            e.getStackTrace();
            return null;
        }
    }
}