package com.xtc.ui.widget.circle;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;
import com.xtc.moment.R;

/** 支持四角独立圆角裁剪的相对布局。 */
public class RoundCornerRelativeComponent extends RelativeLayout {
    private static final int DEFAULT_RADIUS = 30;
    private static final String TAG = RoundCornerRelativeComponent.class.getSimpleName();
    private float mLeftToBottomRadii;
    private float mLeftToTopRadii;
    private Path mPath;
    private float mRadius;
    private RectF mRect;
    private float mRightToBottomRadii;
    private float mRightToTopRadii;
    private boolean mUseRadius;

    public RoundCornerRelativeComponent(Context context) {
        this(context, null);
    }

    public RoundCornerRelativeComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.RoundCornerRelativeComponent);
        this.mRadius = attributes.getDimensionPixelSize(R.styleable.RoundCornerRelativeComponent_relative_radius, 30);
        this.mUseRadius = attributes.getBoolean(R.styleable.RoundCornerRelativeComponent_relative_use_radius, true);
        this.mLeftToTopRadii = attributes.getDimensionPixelOffset(R.styleable.RoundCornerRelativeComponent_relative_left_to_top_radii, 30);
        this.mLeftToBottomRadii = attributes.getDimensionPixelOffset(R.styleable.RoundCornerRelativeComponent_relative_left_to_bottom_radii, 30);
        this.mRightToTopRadii = attributes.getDimensionPixelOffset(R.styleable.RoundCornerRelativeComponent_relative_right_to_top_radii, 30);
        this.mRightToBottomRadii = attributes.getDimensionPixelOffset(R.styleable.RoundCornerRelativeComponent_relative_right_to_bottom_radii, 30);
        attributes.recycle();
    }

    private void init() {
        this.mPath = new Path();
        this.mRect = new RectF();
        this.mUseRadius = true;
        this.mRadius = 30.0f;
        this.mLeftToTopRadii = 30.0f;
        this.mLeftToBottomRadii = 30.0f;
        this.mRightToTopRadii = 30.0f;
        this.mRightToBottomRadii = 30.0f;
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        Log.d(TAG, "onSizeChanged() called with: w = [" + width + "], h = [" + height + "], oldw = [" + oldWidth + "], oldh = [" + oldHeight + "]");
        this.mPath.reset();
        this.mRect.set(0.0f, 0.0f, (float) width, (float) height);
        if (this.mUseRadius) {
            Path path = this.mPath;
            RectF rect = this.mRect;
            float radius = this.mRadius;
            path.addRoundRect(rect, radius, radius, Path.Direction.CW);
        } else {
            float leftTop = this.mLeftToTopRadii;
            float rightTop = this.mRightToTopRadii;
            float rightBottom = this.mRightToBottomRadii;
            float leftBottom = this.mLeftToBottomRadii;
            this.mPath.addRoundRect(this.mRect, new float[]{leftTop, leftTop, rightTop, rightTop,
                    rightBottom, rightBottom, leftBottom, leftBottom}, Path.Direction.CW);
        }
        this.mPath.close();
    }

    @Override
    public void draw(Canvas canvas) {
        Log.d(TAG, "onDraw() called with: canvas = [" + canvas + "]");
        int saveCount = canvas.save();
        canvas.clipPath(this.mPath);
        super.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    public void setRadius(float radius) {
        Path path;
        if (this.mRadius == radius || radius < 0.0f || (path = this.mPath) == null) {
            return;
        }
        this.mRadius = radius;
        path.reset();
        Path path2 = this.mPath;
        RectF rect = this.mRect;
        float currentRadius = this.mRadius;
        path2.addRoundRect(rect, currentRadius, currentRadius, Path.Direction.CW);
        this.mPath.close();
        invalidate();
    }

    public void setRadius(float[] radii) {
        if (radii != null && radii.length < 8) {
            throw new ArrayIndexOutOfBoundsException("radii[] needs 8 values");
        }
        Path path = this.mPath;
        if (path != null) {
            path.reset();
            this.mPath.addRoundRect(this.mRect, radii, Path.Direction.CW);
            this.mPath.close();
            invalidate();
        }
    }
}