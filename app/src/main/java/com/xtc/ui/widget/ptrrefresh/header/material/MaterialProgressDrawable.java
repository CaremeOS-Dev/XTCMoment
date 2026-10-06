package com.xtc.ui.widget.ptrrefresh.header.material;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import com.xtc.ui.widget.ptrrefresh.utils.LocalDisplay;
import java.util.ArrayList;

/** Material 风格的圆形进度 Drawable，含箭头、旋转与颜色循环动画。 */
public class MaterialProgressDrawable extends Drawable implements Animatable {
    private static final int ANIMATION_DURATION = 1333;
    private static final int ARROW_HEIGHT = 5;
    private static final int ARROW_HEIGHT_LARGE = 6;
    private static final float ARROW_OFFSET_ANGLE = 5.0f;
    private static final int ARROW_WIDTH = 10;
    private static final int ARROW_WIDTH_LARGE = 12;
    private static final float CENTER_RADIUS = 8.75f;
    private static final float CENTER_RADIUS_LARGE = 12.5f;
    private static final int CIRCLE_DIAMETER = 40;
    private static final int CIRCLE_DIAMETER_LARGE = 56;
    public static final int DEFAULT = 1;
    private static final Interpolator END_CURVE_INTERPOLATOR;
    private static final int FILL_SHADOW_COLOR = 1023410176;
    private static final int KEY_SHADOW_COLOR = 503316480;
    public static final int LARGE = 0;
    private static final float MAX_PROGRESS_ARC = 0.8f;
    private static final float NUM_POINTS = 5.0f;
    private static final float SHADOW_RADIUS = 3.5f;
    private static final Interpolator START_CURVE_INTERPOLATOR;
    private static final float STROKE_WIDTH = 2.5f;
    private static final float STROKE_WIDTH_LARGE = 3.0f;
    private static final float X_OFFSET = 0.0f;
    private static final float Y_OFFSET = 1.75f;
    private Animation mAnimation;
    private int mBackgroundColor;
    private Animation mFinishAnimation;
    private double mHeight;
    private View mParent;
    private Resources mResources;
    private float mRotation;
    private float mRotationCount;
    private ShapeDrawable mShadow;
    private double mWidth;
    private static final Interpolator LINEAR_INTERPOLATOR = new LinearInterpolator();
    private static final Interpolator EASE_INTERPOLATOR = new AccelerateDecelerateInterpolator();
    private final int[] COLORS = {-3591113, -13149199, -536002, -13327536};
    private final ArrayList<Animation> mAnimators = new ArrayList<>();
    private final Drawable.Callback mCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable drawable) {
            MaterialProgressDrawable.this.invalidateSelf();
        }

        @Override
        public void scheduleDrawable(Drawable drawable, Runnable runnable, long when) {
            MaterialProgressDrawable.this.scheduleSelf(runnable, when);
        }

        @Override
        public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
            MaterialProgressDrawable.this.unscheduleSelf(runnable);
        }
    };
    private final Ring mRing = new Ring(this.mCallback);

    @Override
    public int getOpacity() {
        return -3;
    }

    static {
        END_CURVE_INTERPOLATOR = new EndCurveInterpolator();
        START_CURVE_INTERPOLATOR = new StartCurveInterpolator();
    }

    public MaterialProgressDrawable(Context context, View parent) {
        this.mParent = parent;
        this.mResources = context.getResources();
        this.mRing.setColors(this.COLORS);
        updateSizes(1);
        setupAnimators();
    }

    private void setSizeParameters(double width, double height, double centerRadius, double strokeWidth,
                                   float arrowWidth, float arrowHeight) {
        Ring ring = this.mRing;
        float density = this.mResources.getDisplayMetrics().density;
        double densityD = density;
        this.mWidth = width * densityD;
        this.mHeight = height * densityD;
        ring.setStrokeWidth(((float) strokeWidth) * density);
        ring.setCenterRadius(centerRadius * densityD);
        ring.setColorIndex(0);
        ring.setArrowDimensions(arrowWidth * density, arrowHeight * density);
        ring.setInsets((int) this.mWidth, (int) this.mHeight);
        setUp(this.mWidth);
    }

    private void setUp(double diameter) {
        LocalDisplay.init(this.mParent.getContext());
        int yOffset = LocalDisplay.dp2px(Y_OFFSET);
        int xOffset = LocalDisplay.dp2px(0.0f);
        int shadowRadius = LocalDisplay.dp2px(SHADOW_RADIUS);
        this.mShadow = new ShapeDrawable(new OvalShadow(shadowRadius, (int) diameter));
        if (Build.VERSION.SDK_INT >= 11) {
            this.mParent.setLayerType(1, this.mShadow.getPaint());
        }
        this.mShadow.getPaint().setShadowLayer(shadowRadius, xOffset, yOffset, KEY_SHADOW_COLOR);
    }

    public void updateSizes(int size) {
        if (size == 0) {
            setSizeParameters(56.0d, 56.0d, 12.5d, 3.0d, 12.0f, 6.0f);
        } else {
            setSizeParameters(40.0d, 40.0d, 8.75d, 2.5d, 10.0f, 5.0f);
        }
    }

    public void showArrow(boolean showArrow) {
        this.mRing.setShowArrow(showArrow);
    }

    public void setArrowScale(float scale) {
        this.mRing.setArrowScale(scale);
    }

    public void setStartEndTrim(float startTrim, float endTrim) {
        this.mRing.setStartTrim(startTrim);
        this.mRing.setEndTrim(endTrim);
    }

    public void setProgressRotation(float rotation) {
        this.mRing.setRotation(rotation);
    }

    public void setBackgroundColor(int color) {
        this.mBackgroundColor = color;
        this.mRing.setBackgroundColor(color);
    }

    public void setColorSchemeColors(int... colors) {
        this.mRing.setColors(colors);
        this.mRing.setColorIndex(0);
    }

    @Override
    public int getIntrinsicHeight() {
        return (int) this.mHeight;
    }

    @Override
    public int getIntrinsicWidth() {
        return (int) this.mWidth;
    }

    @Override
    public void draw(Canvas canvas) {
        ShapeDrawable shadow = this.mShadow;
        if (shadow != null) {
            shadow.getPaint().setColor(this.mBackgroundColor);
            this.mShadow.draw(canvas);
        }
        Rect bounds = getBounds();
        int saveCount = canvas.save();
        canvas.rotate(this.mRotation, bounds.exactCenterX(), bounds.exactCenterY());
        this.mRing.draw(canvas, bounds);
        canvas.restoreToCount(saveCount);
    }

    @Override
    public int getAlpha() {
        return this.mRing.getAlpha();
    }

    @Override
    public void setAlpha(int alpha) {
        this.mRing.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        this.mRing.setColorFilter(colorFilter);
    }

    private float getRotation() {
        return this.mRotation;
    }

    void setRotation(float rotation) {
        this.mRotation = rotation;
        invalidateSelf();
    }

    @Override
    public boolean isRunning() {
        ArrayList<Animation> animators = this.mAnimators;
        int size = animators.size();
        for (int index = 0; index < size; index++) {
            Animation animation = animators.get(index);
            if (animation.hasStarted() && !animation.hasEnded()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void start() {
        this.mAnimation.reset();
        this.mRing.storeOriginals();
        if (this.mRing.getEndTrim() != this.mRing.getStartTrim()) {
            this.mParent.startAnimation(this.mFinishAnimation);
            return;
        }
        this.mRing.setColorIndex(0);
        this.mRing.resetOriginals();
        this.mParent.startAnimation(this.mAnimation);
    }

    @Override
    public void stop() {
        this.mParent.clearAnimation();
        setRotation(0.0f);
        this.mRing.setShowArrow(false);
        this.mRing.setColorIndex(0);
        this.mRing.resetOriginals();
    }

    private void setupAnimators() {
        final Ring ring = this.mRing;
        Animation finishAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation transformation) {
                float nextRotation = (float) (Math.floor(ring.getStartingRotation() / 0.8f) + 1.0d);
                ring.setStartTrim(ring.getStartingStartTrim()
                        + ((ring.getStartingEndTrim() - ring.getStartingStartTrim()) * interpolatedTime));
                ring.setRotation(ring.getStartingRotation() + ((nextRotation - ring.getStartingRotation()) * interpolatedTime));
                ring.setArrowScale(1.0f - interpolatedTime);
            }
        };
        finishAnimation.setInterpolator(EASE_INTERPOLATOR);
        finishAnimation.setDuration(666L);
        finishAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ring.goToNextColor();
                ring.storeOriginals();
                ring.setShowArrow(false);
                MaterialProgressDrawable.this.mParent.startAnimation(MaterialProgressDrawable.this.mAnimation);
            }
        });
        Animation rotationAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation transformation) {
                double strokeWidth = ring.getStrokeWidth();
                double circumference = ring.getCenterRadius() * 6.283185307179586d;
                float radians = (float) Math.toRadians(strokeWidth / circumference);
                float startingEndTrim = ring.getStartingEndTrim();
                float startingStartTrim = ring.getStartingStartTrim();
                float startingRotation = ring.getStartingRotation();
                ring.setEndTrim(startingEndTrim + ((0.8f - radians) * MaterialProgressDrawable.START_CURVE_INTERPOLATOR.getInterpolation(interpolatedTime)));
                ring.setStartTrim(startingStartTrim + (MaterialProgressDrawable.END_CURVE_INTERPOLATOR.getInterpolation(interpolatedTime) * 0.8f));
                ring.setRotation(startingRotation + (0.25f * interpolatedTime));
                MaterialProgressDrawable.this.setRotation((interpolatedTime * 144.0f)
                        + ((MaterialProgressDrawable.this.mRotationCount / 5.0f) * 720.0f));
            }
        };
        rotationAnimation.setRepeatCount(-1);
        rotationAnimation.setRepeatMode(1);
        rotationAnimation.setInterpolator(LINEAR_INTERPOLATOR);
        rotationAnimation.setDuration(1333L);
        rotationAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
                MaterialProgressDrawable.this.mRotationCount = 0.0f;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                ring.storeOriginals();
                ring.goToNextColor();
                ring.setStartTrim(ring.getEndTrim());
                MaterialProgressDrawable materialProgressDrawable = MaterialProgressDrawable.this;
                materialProgressDrawable.mRotationCount = (materialProgressDrawable.mRotationCount + 1.0f) % 5.0f;
            }
        });
        this.mFinishAnimation = finishAnimation;
        this.mAnimation = rotationAnimation;
    }

    /** 圆弧 + 箭头的绘制状态。 */
    private static class Ring {
        private int mAlpha;
        private Path mArrow;
        private int mArrowHeight;
        private float mArrowScale;
        private int mArrowWidth;
        private int mBackgroundColor;
        private int mColorIndex;
        private int[] mColors;
        private final Drawable.Callback mRingCallback;
        private double mRingCenterRadius;
        private boolean mShowArrow;
        private float mStartingEndTrim;
        private float mStartingRotation;
        private float mStartingStartTrim;
        private final RectF mTempBounds = new RectF();
        private final Paint mArcPaint = new Paint();
        private final Paint mArrowPaint = new Paint();
        private final Paint mCirclePaint = new Paint();
        private float mStartTrim = 0.0f;
        private float mEndTrim = 0.0f;
        private float mRotation = 0.0f;
        private float mStrokeWidth = 5.0f;
        private float mStrokeInset = MaterialProgressDrawable.STROKE_WIDTH;

        public Ring(Drawable.Callback callback) {
            this.mRingCallback = callback;
            this.mArcPaint.setStrokeCap(Paint.Cap.SQUARE);
            this.mArcPaint.setAntiAlias(true);
            this.mArcPaint.setStyle(Paint.Style.STROKE);
            this.mArrowPaint.setStyle(Paint.Style.FILL);
            this.mArrowPaint.setAntiAlias(true);
            this.mCirclePaint.setAntiAlias(true);
        }

        public void setBackgroundColor(int color) {
            this.mBackgroundColor = color;
        }

        public void setArrowDimensions(float width, float height) {
            this.mArrowWidth = (int) width;
            this.mArrowHeight = (int) height;
        }

        public void draw(Canvas canvas, Rect bounds) {
            this.mCirclePaint.setColor(this.mBackgroundColor);
            this.mCirclePaint.setAlpha(this.mAlpha);
            canvas.drawCircle(bounds.exactCenterX(), bounds.exactCenterY(), bounds.width() / 2, this.mCirclePaint);
            RectF arcBounds = this.mTempBounds;
            arcBounds.set(bounds);
            float inset = this.mStrokeInset;
            arcBounds.inset(inset, inset);
            float startTrim = this.mStartTrim;
            float rotation = this.mRotation;
            float startAngle = (startTrim + rotation) * 360.0f;
            float sweepAngle = ((this.mEndTrim + rotation) * 360.0f) - startAngle;
            this.mArcPaint.setColor(this.mColors[this.mColorIndex]);
            this.mArcPaint.setAlpha(this.mAlpha);
            canvas.drawArc(arcBounds, startAngle, sweepAngle, false, this.mArcPaint);
            drawTriangle(canvas, startAngle, sweepAngle, bounds);
        }

        private void drawTriangle(Canvas canvas, float startAngle, float sweepAngle, Rect bounds) {
            if (this.mShowArrow) {
                Path arrow = this.mArrow;
                if (arrow == null) {
                    this.mArrow = new Path();
                    this.mArrow.setFillType(Path.FillType.EVEN_ODD);
                } else {
                    arrow.reset();
                }
                float offset = (((int) this.mStrokeInset) / 2) * this.mArrowScale;
                double centerOffsetX = this.mRingCenterRadius * Math.cos(0.0d);
                double centerX = bounds.exactCenterX();
                float arrowX = (float) (centerOffsetX + centerX);
                double centerOffsetY = this.mRingCenterRadius * Math.sin(0.0d);
                double centerY = bounds.exactCenterY();
                float arrowY = (float) (centerOffsetY + centerY);
                this.mArrow.moveTo(0.0f, 0.0f);
                this.mArrow.lineTo(this.mArrowWidth * this.mArrowScale, 0.0f);
                Path arrowPath = this.mArrow;
                float arrowWidth = this.mArrowWidth;
                float arrowScale = this.mArrowScale;
                arrowPath.lineTo((arrowWidth * arrowScale) / 2.0f, this.mArrowHeight * arrowScale);
                this.mArrow.offset(arrowX - offset, arrowY);
                this.mArrow.close();
                this.mArrowPaint.setColor(this.mColors[this.mColorIndex]);
                this.mArrowPaint.setAlpha(this.mAlpha);
                canvas.rotate((startAngle + sweepAngle) - 5.0f, bounds.exactCenterX(), bounds.exactCenterY());
                canvas.drawPath(this.mArrow, this.mArrowPaint);
            }
        }

        public void setColors(int[] colors) {
            this.mColors = colors;
            setColorIndex(0);
        }

        public void setColorIndex(int colorIndex) {
            this.mColorIndex = colorIndex;
        }

        public void goToNextColor() {
            this.mColorIndex = (this.mColorIndex + 1) % this.mColors.length;
        }

        public void setColorFilter(ColorFilter colorFilter) {
            this.mArcPaint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        public int getAlpha() {
            return this.mAlpha;
        }

        public void setAlpha(int alpha) {
            this.mAlpha = alpha;
        }

        public float getStrokeWidth() {
            return this.mStrokeWidth;
        }

        public void setStrokeWidth(float strokeWidth) {
            this.mStrokeWidth = strokeWidth;
            this.mArcPaint.setStrokeWidth(strokeWidth);
            invalidateSelf();
        }

        public float getStartTrim() {
            return this.mStartTrim;
        }

        public void setStartTrim(float startTrim) {
            this.mStartTrim = startTrim;
            invalidateSelf();
        }

        public float getStartingStartTrim() {
            return this.mStartingStartTrim;
        }

        public float getStartingEndTrim() {
            return this.mStartingEndTrim;
        }

        public float getEndTrim() {
            return this.mEndTrim;
        }

        public void setEndTrim(float endTrim) {
            this.mEndTrim = endTrim;
            invalidateSelf();
        }

        public float getRotation() {
            return this.mRotation;
        }

        public void setRotation(float rotation) {
            this.mRotation = rotation;
            invalidateSelf();
        }

        public void setInsets(int width, int height) {
            double inset;
            float min = Math.min(width, height);
            double centerRadius = this.mRingCenterRadius;
            if (centerRadius <= 0.0d || min < 0.0f) {
                inset = Math.ceil(this.mStrokeWidth / 2.0f);
            } else {
                double halfMin = min / 2.0f;
                inset = halfMin - centerRadius;
            }
            this.mStrokeInset = (float) inset;
        }

        public float getInsets() {
            return this.mStrokeInset;
        }

        public double getCenterRadius() {
            return this.mRingCenterRadius;
        }

        public void setCenterRadius(double centerRadius) {
            this.mRingCenterRadius = centerRadius;
        }

        public void setShowArrow(boolean showArrow) {
            if (this.mShowArrow != showArrow) {
                this.mShowArrow = showArrow;
                invalidateSelf();
            }
        }

        public void setArrowScale(float arrowScale) {
            if (arrowScale != this.mArrowScale) {
                this.mArrowScale = arrowScale;
                invalidateSelf();
            }
        }

        public float getStartingRotation() {
            return this.mStartingRotation;
        }

        public void storeOriginals() {
            this.mStartingStartTrim = this.mStartTrim;
            this.mStartingEndTrim = this.mEndTrim;
            this.mStartingRotation = this.mRotation;
        }

        public void resetOriginals() {
            this.mStartingStartTrim = 0.0f;
            this.mStartingEndTrim = 0.0f;
            this.mStartingRotation = 0.0f;
            setStartTrim(0.0f);
            setEndTrim(0.0f);
            setRotation(0.0f);
        }

        private void invalidateSelf() {
            this.mRingCallback.invalidateDrawable(null);
        }
    }

    /** 结束段插值器。 */
    private static class EndCurveInterpolator extends AccelerateDecelerateInterpolator {
        private EndCurveInterpolator() {
        }

        @Override
        public float getInterpolation(float input) {
            return super.getInterpolation(Math.max(0.0f, (input - 0.5f) * 2.0f));
        }
    }

    /** 起始段插值器。 */
    private static class StartCurveInterpolator extends AccelerateDecelerateInterpolator {
        private StartCurveInterpolator() {
        }

        @Override
        public float getInterpolation(float input) {
            return super.getInterpolation(Math.min(1.0f, input * 2.0f));
        }
    }

    /** 圆形阴影。 */
    private class OvalShadow extends OvalShape {
        private int mCircleDiameter;
        private RadialGradient mRadialGradient;
        private Paint mShadowPaint = new Paint();
        private int mShadowRadius;

        public OvalShadow(int shadowRadius, int circleDiameter) {
            this.mShadowRadius = shadowRadius;
            this.mCircleDiameter = circleDiameter;
            int diameter = this.mCircleDiameter;
            this.mRadialGradient = new RadialGradient(diameter / 2, diameter / 2, this.mShadowRadius,
                    new int[]{MaterialProgressDrawable.FILL_SHADOW_COLOR, 0}, (float[]) null, Shader.TileMode.CLAMP);
            this.mShadowPaint.setShader(this.mRadialGradient);
        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            float width = MaterialProgressDrawable.this.getBounds().width() / 2;
            float height = MaterialProgressDrawable.this.getBounds().height() / 2;
            canvas.drawCircle(width, height, (this.mCircleDiameter / 2) + this.mShadowRadius, this.mShadowPaint);
            canvas.drawCircle(width, height, this.mCircleDiameter / 2, paint);
        }
    }
}