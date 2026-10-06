package com.xtc.ui.widget.blur;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import com.xtc.log.LogUtil;
import com.xtc.ui.widget.R;

/** 模糊背景视图：绘制全局模糊位图或半透明遮罩。 */
public class BlurBackgroundView extends View {
    private static final String TAG = BlurBackgroundView.class.getSimpleName();
    private static Bitmap blurBitmap;
    private Rect bitmapRect;
    private Paint paint;

    public BlurBackgroundView(Context context) {
        this(context, null);
    }

    public BlurBackgroundView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BlurBackgroundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        this.bitmapRect = new Rect();
        this.bitmapRect.left = 0;
        this.paint = new Paint();
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setAntiAlias(true);
        this.paint.setColor(getResources().getColor(R.color.color_b2000000));
    }

    public void setBlurBitmap(Bitmap bitmap) {
        blurBitmap = bitmap;
    }

    public static Bitmap getBlurBitmap() {
        return blurBitmap;
    }

    public void setViewTopAndBottom(int top, int bottom) {
        Rect rect = this.bitmapRect;
        rect.top = top;
        rect.bottom = bottom;
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.bitmapRect.right = View.MeasureSpec.getSize(widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            if (blurBitmap != null) {
                canvas.drawBitmap(blurBitmap, this.bitmapRect, this.bitmapRect, (Paint) null);
            } else {
                canvas.drawRect(this.bitmapRect, this.paint);
            }
        } catch (Exception unused) {
            LogUtil.e(TAG, " trying to use a recycled bitmap");
        }
    }
}