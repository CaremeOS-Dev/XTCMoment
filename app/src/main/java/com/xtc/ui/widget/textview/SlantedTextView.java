package com.xtc.ui.widget.textview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;

/** 斜角标签控件：绘制不同方位的斜角背景并叠加旋转文字。 */
public class SlantedTextView extends View {
    private static final String TAG = "SlantedTextView";
    int DEFAULT_SLANTED_DEGREES;
    private int mMode;
    private Paint mPaint;
    private int mSlantedBackgroundColor;
    private float mSlantedLength;
    private String mSlantedText;
    private int mTextColor;
    private TextPaint mTextPaint;
    private float mTextSize;
    int slantedDegrees;

    /** 斜角方位模式常量。 */
    interface SlantedMode {
        public static final int MODE_LEFT = 0;
        public static final int MODE_LEFT_BOTTOM = 2;
        public static final int MODE_LEFT_BOTTOM_TRIANGLE = 6;
        public static final int MODE_LEFT_TRIANGLE = 4;
        public static final int MODE_RIGHT = 1;
        public static final int MODE_RIGHT_BOTTOM = 3;
        public static final int MODE_RIGHT_BOTTOM_TRIANGLE = 7;
        public static final int MODE_RIGHT_TRIANGLE = 5;
    }

    public SlantedTextView(Context context) {
        this(context, null);
    }

    public SlantedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SlantedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mMode = 0;
        this.DEFAULT_SLANTED_DEGREES = 45;
        this.mSlantedLength = 40.0f;
        this.mSlantedBackgroundColor = 0;
        this.mTextSize = 16.0f;
        this.mTextColor = -1;
        this.mSlantedText = "";
        init(attrs);
    }

    public SlantedTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mMode = 0;
        this.DEFAULT_SLANTED_DEGREES = 45;
        this.mSlantedLength = 40.0f;
        this.mSlantedBackgroundColor = 0;
        this.mTextSize = 16.0f;
        this.mTextColor = -1;
        this.mSlantedText = "";
        init(attrs);
    }

    public void init(AttributeSet attrs) {
        initAttrs(attrs);
        initPaints();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.SlantedTextView);
        this.mTextSize = attributes.getDimension(R.styleable.SlantedTextView_slantedTextSize, this.mTextSize);
        this.mTextColor = attributes.getColor(R.styleable.SlantedTextView_slantedTextColor, this.mTextColor);
        this.mSlantedLength = attributes.getDimension(R.styleable.SlantedTextView_slantedLength, this.mSlantedLength);
        this.mSlantedBackgroundColor = attributes.getColor(R.styleable.SlantedTextView_slantedBackgroundColor, this.mSlantedBackgroundColor);
        if (attributes.hasValue(R.styleable.SlantedTextView_slantedText)) {
            this.mSlantedText = attributes.getString(R.styleable.SlantedTextView_slantedText);
        }
        if (attributes.hasValue(R.styleable.SlantedTextView_slantedMode)) {
            this.mMode = attributes.getInt(R.styleable.SlantedTextView_slantedMode, 0);
        }
        this.slantedDegrees = attributes.getInteger(R.styleable.SlantedTextView_slantedDegree, this.DEFAULT_SLANTED_DEGREES);
        attributes.recycle();
    }

    private void initPaints() {
        this.mPaint = new Paint();
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(this.mSlantedBackgroundColor);
        this.mTextPaint = new TextPaint(1);
        this.mTextPaint.setAntiAlias(true);
        this.mTextPaint.setTextSize(this.mTextSize);
        this.mTextPaint.setColor(this.mTextColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        drawText(canvas);
    }

    private void drawBackground(Canvas canvas) {
        Path path = new Path();
        int width = getWidth();
        int height = getHeight();
        switch (this.mMode) {
            case 0:
                path = getModeLeftPath(path, width, height);
                break;
            case 1:
                path = getModeRightPath(path, width, height);
                break;
            case 2:
                path = getModeLeftBottomPath(path, width, height);
                break;
            case 3:
                path = getModeRightBottomPath(path, width, height);
                break;
            case 4:
                path = getModeLeftTrianglePath(path, width, height);
                break;
            case 5:
                path = getModeRightTrianglePath(path, width, height);
                break;
            case 6:
                path = getModeLeftBottomTrianglePath(path, width, height);
                break;
            case 7:
                path = getModeRightBottomTrianglePath(path, width, height);
                break;
            default:
                Log.d(TAG, "mode is error!");
                break;
        }
        path.close();
        canvas.drawPath(path, this.mPaint);
        canvas.save();
    }

    private Path getModeLeftPath(Path path, int width, int height) {
        float widthF = width;
        path.moveTo(widthF, 0.0f);
        float heightF = height;
        path.lineTo(0.0f, heightF);
        path.lineTo(0.0f, heightF - this.mSlantedLength);
        path.lineTo(widthF - this.mSlantedLength, 0.0f);
        return path;
    }

    private Path getModeRightPath(Path path, int width, int height) {
        float widthF = width;
        float arcSize = widthF - this.mSlantedLength;
        if (Build.VERSION.SDK_INT >= 21) {
            float arcDiameter = arcSize * 2.0f;
            path.addArc(widthF - arcDiameter, 0.0f, widthF, arcDiameter, 0.0f, -90.0f);
        } else {
            float arcDiameter = arcSize * 2.0f;
            path.addArc(new RectF(widthF - arcDiameter, 0.0f, widthF, arcDiameter), 0.0f, -90.0f);
        }
        Path trianglePath = new Path();
        trianglePath.moveTo(this.mSlantedLength, 0.0f);
        trianglePath.lineTo(0.0f, 0.0f);
        float heightF = height;
        trianglePath.lineTo(widthF, heightF);
        trianglePath.lineTo(widthF, heightF - this.mSlantedLength);
        path.op(trianglePath, Path.Op.UNION);
        return path;
    }

    private Path getModeLeftBottomPath(Path path, int width, int height) {
        float widthF = width;
        float heightF = height;
        path.lineTo(widthF, heightF);
        path.lineTo(widthF - this.mSlantedLength, heightF);
        path.lineTo(0.0f, this.mSlantedLength);
        return path;
    }

    private Path getModeRightBottomPath(Path path, int width, int height) {
        float heightF = height;
        path.moveTo(0.0f, heightF);
        path.lineTo(this.mSlantedLength, heightF);
        float widthF = width;
        path.lineTo(widthF, this.mSlantedLength);
        path.lineTo(widthF, 0.0f);
        return path;
    }

    private Path getModeLeftTrianglePath(Path path, int width, int height) {
        path.lineTo(0.0f, height);
        path.lineTo(width, 0.0f);
        return path;
    }

    private Path getModeRightTrianglePath(Path path, int width, int height) {
        float widthF = width;
        path.lineTo(widthF, 0.0f);
        path.lineTo(widthF, height);
        return path;
    }

    private Path getModeLeftBottomTrianglePath(Path path, int width, int height) {
        float heightF = height;
        path.lineTo(width, heightF);
        path.lineTo(0.0f, heightF);
        return path;
    }

    private Path getModeRightBottomTrianglePath(Path path, int width, int height) {
        float heightF = height;
        path.moveTo(0.0f, heightF);
        float widthF = width;
        path.lineTo(widthF, heightF);
        path.lineTo(widthF, 0.0f);
        return path;
    }

    private void drawText(Canvas canvas) {
        LogUtil.i(TAG, "drawText: canvas.width = " + canvas.getWidth() + ", canvas.height = " + canvas.getHeight() + ", slantedLength = " + this.mSlantedLength);
        float[] textParams = calculateXY((int) (((float) canvas.getWidth()) - (this.mSlantedLength / 2.0f)),
                (int) (((float) canvas.getHeight()) - (this.mSlantedLength / 2.0f)));
        float textX = textParams[0];
        float textY = textParams[1];
        canvas.rotate(textParams[4], textParams[2], textParams[3]);
        canvas.drawText(this.mSlantedText, textX, textY, this.mTextPaint);
    }

    private float[] calculateXY(int width, int height) {
        float[] textParams = new float[5];
        int halfSlantedLength = (int) (this.mSlantedLength / 2.0f);
        switch (this.mMode) {
            case 0:
            case 4:
                Rect bounds = new Rect(0, 0, width, height);
                RectF textBounds = new RectF(bounds);
                TextPaint textPaint = this.mTextPaint;
                String text = this.mSlantedText;
                textBounds.right = textPaint.measureText(text, 0, text.length());
                textBounds.bottom = this.mTextPaint.descent() - this.mTextPaint.ascent();
                textBounds.left += (bounds.width() - textBounds.right) / 2.0f;
                textBounds.top += (bounds.height() - textBounds.bottom) / 2.0f;
                textParams[0] = textBounds.left;
                textParams[1] = textBounds.top - this.mTextPaint.ascent();
                textParams[2] = width / 2;
                textParams[3] = height / 2;
                textParams[4] = -this.slantedDegrees;
                return textParams;
            case 1:
            case 5:
                Rect boundsRight = new Rect(halfSlantedLength, 0, width + halfSlantedLength, height);
                RectF textBoundsRight = new RectF(boundsRight);
                TextPaint textPaintRight = this.mTextPaint;
                String textRight = this.mSlantedText;
                textBoundsRight.right = textPaintRight.measureText(textRight, 0, textRight.length());
                textBoundsRight.bottom = this.mTextPaint.descent() - this.mTextPaint.ascent();
                textBoundsRight.left += (boundsRight.width() - textBoundsRight.right) / 2.0f;
                textBoundsRight.top += (boundsRight.height() - textBoundsRight.bottom) / 2.0f;
                textParams[0] = textBoundsRight.left;
                textParams[1] = (textBoundsRight.top - this.mTextPaint.ascent()) + 3.0f;
                textParams[2] = (width / 2) + halfSlantedLength;
                textParams[3] = height / 2;
                textParams[4] = this.slantedDegrees;
                return textParams;
            case 2:
            case 6:
                Rect boundsLeftBottom = new Rect(0, halfSlantedLength, width, height + halfSlantedLength);
                RectF textBoundsLeftBottom = new RectF(boundsLeftBottom);
                TextPaint textPaintLeftBottom = this.mTextPaint;
                String textLeftBottom = this.mSlantedText;
                textBoundsLeftBottom.right = textPaintLeftBottom.measureText(textLeftBottom, 0, textLeftBottom.length());
                textBoundsLeftBottom.bottom = this.mTextPaint.descent() - this.mTextPaint.ascent();
                textBoundsLeftBottom.left += (boundsLeftBottom.width() - textBoundsLeftBottom.right) / 2.0f;
                textBoundsLeftBottom.top += (boundsLeftBottom.height() - textBoundsLeftBottom.bottom) / 2.0f;
                textParams[0] = textBoundsLeftBottom.left;
                textParams[1] = textBoundsLeftBottom.top - this.mTextPaint.ascent();
                textParams[2] = width / 2;
                textParams[3] = (height / 2) + halfSlantedLength;
                textParams[4] = this.slantedDegrees;
                return textParams;
            case 3:
            case 7:
                Rect boundsRightBottom = new Rect(halfSlantedLength, halfSlantedLength, width + halfSlantedLength, height + halfSlantedLength);
                RectF textBoundsRightBottom = new RectF(boundsRightBottom);
                TextPaint textPaintRightBottom = this.mTextPaint;
                String textRightBottom = this.mSlantedText;
                textBoundsRightBottom.right = textPaintRightBottom.measureText(textRightBottom, 0, textRightBottom.length());
                textBoundsRightBottom.bottom = this.mTextPaint.descent() - this.mTextPaint.ascent();
                textBoundsRightBottom.left += (boundsRightBottom.width() - textBoundsRightBottom.right) / 2.0f;
                textBoundsRightBottom.top += (boundsRightBottom.height() - textBoundsRightBottom.bottom) / 2.0f;
                textParams[0] = textBoundsRightBottom.left;
                textParams[1] = textBoundsRightBottom.top - this.mTextPaint.ascent();
                textParams[2] = (width / 2) + halfSlantedLength;
                textParams[3] = (height / 2) + halfSlantedLength;
                textParams[4] = -this.slantedDegrees;
                return textParams;
            default:
                Log.d(TAG, "mode is error!");
                return textParams;
        }
    }

    public SlantedTextView setText(String text) {
        this.mSlantedText = text;
        postInvalidate();
        return this;
    }

    public SlantedTextView setText(int resId) {
        String text = getResources().getString(resId);
        if (!TextUtils.isEmpty(text)) {
            setText(text);
        }
        return this;
    }

    public String getText() {
        return this.mSlantedText;
    }

    public SlantedTextView setSlantedBackgroundColor(int color) {
        this.mSlantedBackgroundColor = color;
        this.mPaint.setColor(this.mSlantedBackgroundColor);
        postInvalidate();
        return this;
    }

    public SlantedTextView setTextColor(int color) {
        this.mTextColor = color;
        this.mTextPaint.setColor(this.mTextColor);
        postInvalidate();
        return this;
    }

    public SlantedTextView setTextSize(int textSize) {
        this.mTextSize = textSize;
        this.mTextPaint.setTextSize(this.mTextSize);
        postInvalidate();
        return this;
    }

    public SlantedTextView setSlantedLength(int slantedLength) {
        this.mSlantedLength = slantedLength;
        postInvalidate();
        return this;
    }

    public SlantedTextView setMode(int mode) {
        int currentMode = this.mMode;
        if (currentMode > 7 || currentMode < 0) {
            throw new IllegalArgumentException(mode + "is illegal argument ,please use right value");
        }
        this.mMode = mode;
        postInvalidate();
        return this;
    }

    public int getMode() {
        return this.mMode;
    }
}