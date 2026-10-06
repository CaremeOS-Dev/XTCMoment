package com.xtc.ui.widget.textview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;
import com.xtc.ui.widget.R;

/** 带左侧箭头的圆角气泡文本。 */
public class ArrowTextView extends TextView {
    // 反编译常量：SupportMenu.CATEGORY_MASK == 0xFFFF0000
    private static final int DEFAULT_BG_COLOR = -65536;
    private float arrowHeight;
    private float arrowWidth;
    private int color;
    private Context context;
    private float radius;

    public ArrowTextView(Context context) {
        this(context, null);
    }

    public ArrowTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArrowTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initAttr(attrs);
    }

    private void initAttr(AttributeSet attrs) {
        TypedArray attributes = this.context.obtainStyledAttributes(attrs, R.styleable.ArrowTextView);
        this.radius = attributes.getDimension(R.styleable.ArrowTextView_arrowRadius, 0.0f);
        this.arrowWidth = attributes.getDimension(R.styleable.ArrowTextView_arrowTvWidth, 0.0f);
        this.arrowHeight = attributes.getDimension(R.styleable.ArrowTextView_arrowTvHeight, 0.0f);
        this.color = attributes.getColor(R.styleable.ArrowTextView_bgColor, DEFAULT_BG_COLOR);
    }

    private void setArrowTvBackground(int radiusPx, int color) {
        float radius = radiusPx;
        ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(
                new float[]{radius, radius, radius, radius, radius, radius, radius, radius}, null, null));
        shapeDrawable.getPaint().setStrokeWidth(2.0f);
        shapeDrawable.getPaint().setStyle(Paint.Style.STROKE);
        shapeDrawable.getPaint().setAntiAlias(true);
        shapeDrawable.getPaint().setColor(color);
        setBackgroundDrawable(shapeDrawable);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, 3));
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1.0f);
        paint.setColor(Color.parseColor("#29a9ff"));
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        if (this.radius == 0.0f) {
            this.radius = TypedValue.applyDimension(1, 5.0f, getResources().getDisplayMetrics());
        }
        if (this.arrowWidth == 0.0f) {
            this.arrowWidth = TypedValue.applyDimension(1, 10.0f, getResources().getDisplayMetrics());
        }
        float bubbleLeft = (int) (getPaddingLeft() - this.arrowWidth);
        float height = getHeight();
        RectF bubbleBounds = new RectF(bubbleLeft, 0.0f, getWidth(), height);
        float cornerRadius = this.radius;
        canvas.drawRoundRect(bubbleBounds, cornerRadius, cornerRadius, paint);
        if (this.arrowHeight == 0.0f) {
            this.arrowHeight = TypedValue.applyDimension(1, 50.0f, getResources().getDisplayMetrics());
        }
        float effectiveArrowHeight = this.arrowHeight;
        if (height <= effectiveArrowHeight) {
            effectiveArrowHeight = height;
        }
        Path arrowPath = new Path();
        arrowPath.setFillType(Path.FillType.EVEN_ODD);
        float centerY = ((int) effectiveArrowHeight) / 2;
        float halfArrowWidth = this.arrowWidth;
        float arrowTop = centerY - (halfArrowWidth / 2.0f);
        float arrowBottom = (halfArrowWidth / 2.0f) + centerY;
        arrowPath.moveTo(0.0f, centerY);
        arrowPath.lineTo(bubbleLeft, arrowTop);
        arrowPath.lineTo(bubbleLeft, arrowBottom);
        arrowPath.lineTo(0.0f, centerY);
        arrowPath.close();
        canvas.drawPath(arrowPath, paint);
        Path dividerPath = new Path();
        Paint dividerPaint = new Paint();
        dividerPaint.setColor(-1);
        dividerPaint.setStyle(Paint.Style.STROKE);
        dividerPaint.setStrokeWidth(1.0f);
        dividerPath.moveTo(bubbleLeft, arrowTop + 2.0f);
        dividerPath.lineTo(bubbleLeft, arrowBottom - 2.0f);
        canvas.drawPath(dividerPath, dividerPaint);
        super.onDraw(canvas);
    }
}