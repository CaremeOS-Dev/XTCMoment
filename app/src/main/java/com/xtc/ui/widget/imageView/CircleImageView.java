package com.xtc.ui.widget.imageView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.xtc.moment.R;
import com.xtc.utils.ui.DimenUtil;

/** 圆形头像 ImageView，支持描边颜色与宽度。 */
public class CircleImageView extends ImageView {
    private static final String TAG = "CircleImageView";
    private static final Xfermode sXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private boolean mHasStroke;
    private Bitmap mMaskBitmap;
    private Paint mPaint;
    private int mStrokeColor;
    private float mStrokeWidth;

    public CircleImageView(Context context) {
        this(context, null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        this.mPaint = new Paint();
        TypedArray attributes = attrs == null ? null : getContext().obtainStyledAttributes(attrs, R.styleable.CircleImageView);
        if (attributes == null) {
            return;
        }
        this.mHasStroke = attributes.getBoolean(R.styleable.CircleImageView_civHasStroke, true);
        this.mStrokeColor = attributes.getColor(R.styleable.CircleImageView_civStrokeColor, -1);
        this.mStrokeWidth = attributes.getDimension(R.styleable.CircleImageView_civStrokeWidth, DimenUtil.dp2pxFloat(getContext(), 1.5f));
        attributes.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isInEditMode()) {
            super.onDraw(canvas);
        } else {
            drawBySelf(canvas);
        }
    }

    private void drawBySelf(Canvas canvas) {
        int saveCount = canvas.saveLayer(0.0f, 0.0f, getWidth(), getHeight(), null, 31);
        Drawable drawable = getDrawable();
        if (drawable != null) {
            drawToCanvas(canvas, getSrcBmp(drawable));
        }
        canvas.restoreToCount(saveCount);
    }

    private void drawToCanvas(Canvas canvas, Bitmap bitmap) {
        this.mPaint.setXfermode(null);
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, this.mPaint);
    }

    private Bitmap getSrcBmp(Drawable drawable) {
        Bitmap result = createEmptyBmp();
        Canvas canvas = new Canvas(result);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, 3));
        drawable.setBounds(0, 0, getWidth(), getHeight());
        drawable.draw(canvas);
        Bitmap maskBitmap = this.mMaskBitmap;
        if (maskBitmap == null || maskBitmap.isRecycled()) {
            this.mMaskBitmap = createMaskBitmap();
        }
        this.mPaint.reset();
        this.mPaint.setFilterBitmap(false);
        this.mPaint.setXfermode(sXfermode);
        canvas.drawBitmap(this.mMaskBitmap, 0.0f, 0.0f, this.mPaint);
        if (this.mHasStroke) {
            this.mPaint.setXfermode(null);
            this.mPaint.setColor(this.mStrokeColor);
            this.mPaint.setStyle(Paint.Style.STROKE);
            this.mPaint.setStrokeWidth(this.mStrokeWidth);
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            canvas.drawCircle(centerX, centerY,
                    (centerX > centerY ? centerY : centerX) - (this.mStrokeWidth / 2.0f), this.mPaint);
        }
        return result;
    }

    public Bitmap createMaskBitmap() {
        Bitmap result = createEmptyBmp();
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(1);
        paint.setColor(-16777216);
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        canvas.drawCircle(centerX, centerY, centerX > centerY ? centerY : centerX, paint);
        return result;
    }

    private Bitmap createEmptyBmp() {
        return Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_4444);
    }

    public void setHasStroke(boolean hasStroke) {
        if (this.mHasStroke == hasStroke) {
            return;
        }
        this.mHasStroke = hasStroke;
        invalidate();
    }

    public void setStrokeWidth(float strokeWidth) {
        if (this.mStrokeWidth == strokeWidth) {
            return;
        }
        this.mStrokeWidth = strokeWidth;
        invalidate();
    }

    public void setStrokeColor(int strokeColor) {
        if (this.mStrokeColor == strokeColor) {
            return;
        }
        this.mStrokeColor = strokeColor;
        invalidate();
    }

    public boolean isHasStroke() {
        return this.mHasStroke;
    }

    public float getStrokeWidth() {
        return this.mStrokeWidth;
    }

    public int getStrokeColor() {
        return this.mStrokeColor;
    }
}