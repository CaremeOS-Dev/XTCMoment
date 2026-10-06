package com.xtc.ui.widget.circle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import com.xtc.ui.widget.R;
import com.xtc.utils.ui.DimenUtil;

/** 录音按钮圆环：外圈白色空心圆 + 内部红色实心圆。 */
public class CircleRecordView extends View {
    private static final String TAG = CircleRecordView.class.getSimpleName();
    private int mBigCircleHallowWidth;
    private Paint mCirclePaint;
    private RectF mCircleRectF;
    private int mDefaultBigCircleColor;
    private int mDefaultBigCircleRadius;
    private int mDefaultSmallCircleColor;
    private int mDefaultSmallCircleRadius;
    private int mHeight;
    private int mWidth;

    public CircleRecordView(Context context) {
        this(context, null);
    }

    public CircleRecordView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleRecordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mDefaultBigCircleRadius = DimenUtil.dp2px(context, 55.0f);
        this.mDefaultBigCircleColor = R.color.color_ffffff;
        this.mBigCircleHallowWidth = DimenUtil.dp2px(context, 3.0f);
        this.mDefaultSmallCircleRadius = DimenUtil.dp2px(context, 46.0f);
        this.mDefaultSmallCircleColor = R.color.color_ff4830;
        this.mCirclePaint = new Paint();
        this.mCirclePaint.setAntiAlias(true);
        this.mCirclePaint.setStyle(Paint.Style.FILL);
        this.mCircleRectF = new RectF();
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        this.mWidth = width;
        this.mHeight = height;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int bigRadius = this.mDefaultBigCircleRadius;
        int hallowWidth = this.mBigCircleHallowWidth;
        drawHollowCircle(canvas, bigRadius - hallowWidth, this.mDefaultBigCircleColor, this.mCirclePaint, hallowWidth);
        drawCircle(canvas, this.mDefaultSmallCircleRadius, this.mDefaultSmallCircleColor, this.mCirclePaint);
    }

    private void drawCircle(Canvas canvas, int radius, int colorResId, Paint paint) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(colorResId));
        paint.setStrokeWidth(radius);
        int left = (this.mWidth - radius) / 2;
        int top = (this.mHeight - radius) / 2;
        this.mCircleRectF.set(left, top, left + radius, top + radius);
        canvas.drawArc(this.mCircleRectF, 0.0f, 360.0f, false, paint);
    }

    private void drawHollowCircle(Canvas canvas, int radius, int colorResId, Paint paint, int strokeWidth) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(colorResId));
        paint.setStrokeWidth(strokeWidth);
        int left = (this.mWidth - radius) / 2;
        int top = (this.mHeight - radius) / 2;
        this.mCircleRectF.set(left, top, left + radius, top + radius);
        canvas.drawArc(this.mCircleRectF, 0.0f, 360.0f, false, paint);
    }
}