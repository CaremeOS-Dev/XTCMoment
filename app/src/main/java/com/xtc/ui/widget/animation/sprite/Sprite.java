package com.xtc.ui.widget.animation.sprite;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.util.FloatProperty;
import android.util.IntProperty;
import android.util.Property;

/**
 * 可绘制精灵基类：在 Drawable 基础上提供平移、旋转、缩放、透明度等属性，
 * 并支持通过属性动画驱动这些属性。
 */
public abstract class Sprite extends Drawable implements ValueAnimator.AnimatorUpdateListener, Animatable, Drawable.Callback {
    private static final String TAG = "Sprite";
    private int animationDelay;
    private ValueAnimator animator;
    protected Context context;
    private float pivotX;
    private float pivotY;
    private int rotate;
    private int rotateX;
    private int rotateY;
    private int translateX;
    private float translateXPercentage;
    private int translateY;
    private float translateYPercentage;
    private static final Rect ZERO_BOUNDS_RECT = new Rect();

    public static final Property<Sprite, Integer> ROTATE_X = new IntProperty<Sprite>("rotateX") {
        @Override
        public void setValue(Sprite sprite, int value) {
            sprite.setRotateX(value);
        }

        @Override
        public Integer get(Sprite sprite) {
            return Integer.valueOf(sprite.getRotateX());
        }
    };
    public static final Property<Sprite, Integer> ROTATE = new IntProperty<Sprite>("rotate") {
        @Override
        public void setValue(Sprite sprite, int value) {
            sprite.setRotate(value);
        }

        @Override
        public Integer get(Sprite sprite) {
            return Integer.valueOf(sprite.getRotate());
        }
    };
    public static final Property<Sprite, Integer> ROTATE_Y = new IntProperty<Sprite>("rotateY") {
        @Override
        public void setValue(Sprite sprite, int value) {
            sprite.setRotateY(value);
        }

        @Override
        public Integer get(Sprite sprite) {
            return Integer.valueOf(sprite.getRotateY());
        }
    };
    public static final Property<Sprite, Integer> TRANSLATE_X = new IntProperty<Sprite>("translateX") {
        @Override
        public void setValue(Sprite sprite, int value) {
            sprite.setTranslateX(value);
        }

        @Override
        public Integer get(Sprite sprite) {
            return Integer.valueOf(sprite.getTranslateX());
        }
    };
    public static final Property<Sprite, Integer> TRANSLATE_Y = new IntProperty<Sprite>("translateY") {
        @Override
        public void setValue(Sprite sprite, int value) {
            sprite.setTranslateY(value);
        }

        @Override
        public Integer get(Sprite sprite) {
            return Integer.valueOf(sprite.getTranslateY());
        }
    };
    public static final Property<Sprite, Float> TRANSLATE_X_PERCENTAGE = new FloatProperty<Sprite>("translateXPercentage") {
        @Override
        public void setValue(Sprite sprite, float value) {
            sprite.setTranslateXPercentage(value);
        }

        @Override
        public Float get(Sprite sprite) {
            return Float.valueOf(sprite.getTranslateXPercentage());
        }
    };
    public static final Property<Sprite, Float> TRANSLATE_Y_PERCENTAGE = new FloatProperty<Sprite>("translateYPercentage") {
        @Override
        public void setValue(Sprite sprite, float value) {
            sprite.setTranslateYPercentage(value);
        }

        @Override
        public Float get(Sprite sprite) {
            return Float.valueOf(sprite.getTranslateYPercentage());
        }
    };
    public static final Property<Sprite, Float> SCALE_X = new FloatProperty<Sprite>("scaleX") {
        @Override
        public void setValue(Sprite sprite, float value) {
            sprite.setScaleX(value);
        }

        @Override
        public Float get(Sprite sprite) {
            return Float.valueOf(sprite.getScaleX());
        }
    };
    public static final Property<Sprite, Float> SCALE_Y = new FloatProperty<Sprite>("scaleY") {
        @Override
        public void setValue(Sprite sprite, float value) {
            sprite.setScaleY(value);
        }

        @Override
        public Float get(Sprite sprite) {
            return Float.valueOf(sprite.getScaleY());
        }
    };
    public static final Property<Sprite, Float> SCALE = new FloatProperty<Sprite>("scale") {
        @Override
        public void setValue(Sprite sprite, float value) {
            sprite.setScale(value);
        }

        @Override
        public Float get(Sprite sprite) {
            return Float.valueOf(sprite.getScale());
        }
    };
    public static final Property<Sprite, Integer> ALPHA = new IntProperty<Sprite>("alpha") {
        @Override
        public void setValue(Sprite sprite, int value) {
            sprite.setAlpha(value);
        }

        @Override
        public Integer get(Sprite sprite) {
            return Integer.valueOf(sprite.getAlpha());
        }
    };

    private float scale = 1.0f;
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;
    private int alpha = 255;
    protected Rect drawBounds = ZERO_BOUNDS_RECT;
    private Camera mCamera = new Camera();
    private Matrix mMatrix = new Matrix();

    protected abstract void drawSelf(Canvas canvas);

    public abstract ValueAnimator getAnimation();

    public abstract int getColor();

    @Override
    public int getOpacity() {
        return 1;
    }

    @Override
    public void scheduleDrawable(Drawable drawable, Runnable runnable, long when) {
    }

    public abstract void setColor(int color);

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override
    public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
    }

    public Sprite(Context context) {
        this.context = context;
    }

    @Override
    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    @Override
    public int getAlpha() {
        return this.alpha;
    }

    public float getTranslateXPercentage() {
        return this.translateXPercentage;
    }

    public void setTranslateXPercentage(float percentage) {
        this.translateXPercentage = percentage;
    }

    public float getTranslateYPercentage() {
        return this.translateYPercentage;
    }

    public void setTranslateYPercentage(float percentage) {
        this.translateYPercentage = percentage;
    }

    public int getTranslateX() {
        return this.translateX;
    }

    public void setTranslateX(int translateX) {
        this.translateX = translateX;
    }

    public int getTranslateY() {
        return this.translateY;
    }

    public void setTranslateY(int translateY) {
        this.translateY = translateY;
    }

    public int getRotate() {
        return this.rotate;
    }

    public void setRotate(int rotate) {
        this.rotate = rotate;
    }

    public float getScale() {
        return this.scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
        setScaleX(scale);
        setScaleY(scale);
    }

    public float getScaleX() {
        return this.scaleX;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public float getScaleY() {
        return this.scaleY;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    public int getRotateX() {
        return this.rotateX;
    }

    public void setRotateX(int rotateX) {
        this.rotateX = rotateX;
    }

    public int getRotateY() {
        return this.rotateY;
    }

    public void setRotateY(int rotateY) {
        this.rotateY = rotateY;
    }

    public float getPivotX() {
        return this.pivotX;
    }

    public void setPivotX(float pivotX) {
        this.pivotX = pivotX;
    }

    public float getPivotY() {
        return this.pivotY;
    }

    public void setPivotY(float pivotY) {
        this.pivotY = pivotY;
    }

    public int getAnimationDelay() {
        return this.animationDelay;
    }

    public Sprite setAnimationDelay(int animationDelay) {
        this.animationDelay = animationDelay;
        return this;
    }

    @Override
    public void start() {
        if (AnimationUtils.isStarted(this.animator)) {
            return;
        }
        this.animator = obtainAnimation();
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator == null) {
            return;
        }
        AnimationUtils.start(valueAnimator);
        invalidateSelf();
    }

    private ValueAnimator obtainAnimation() {
        if (this.animator == null) {
            this.animator = getAnimation();
        }
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator != null) {
            valueAnimator.addUpdateListener(this);
            this.animator.setStartDelay(this.animationDelay);
        }
        return this.animator;
    }

    @Override
    public void stop() {
        if (AnimationUtils.isStarted(this.animator)) {
            this.animator.removeAllUpdateListeners();
            this.animator.end();
            reset();
        }
    }

    public void reset() {
        this.scale = 1.0f;
        this.rotateX = 0;
        this.rotateY = 0;
        this.translateX = 0;
        this.translateY = 0;
        this.rotate = 0;
        this.translateXPercentage = 0.0f;
        this.translateYPercentage = 0.0f;
    }

    @Override
    public boolean isRunning() {
        return AnimationUtils.isRunning(this.animator);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        setDrawBounds(bounds);
    }

    public void setDrawBounds(Rect bounds) {
        setDrawBounds(bounds.left, bounds.top, bounds.right, bounds.bottom);
    }

    public void setDrawBounds(int left, int top, int right, int bottom) {
        this.drawBounds = new Rect(left, top, right, bottom);
        setPivotX(getDrawBounds().centerX());
        setPivotY(getDrawBounds().centerY());
    }

    @Override
    public void invalidateDrawable(Drawable who) {
        invalidateSelf();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        Drawable.Callback callback = getCallback();
        if (callback != null) {
            callback.invalidateDrawable(this);
        }
    }

    public Rect getDrawBounds() {
        return this.drawBounds;
    }

    @Override
    public void draw(Canvas canvas) {
        int translateX = getTranslateX();
        if (translateX == 0) {
            translateX = (int) (getBounds().width() * getTranslateXPercentage());
        }
        int translateY = getTranslateY();
        if (translateY == 0) {
            translateY = (int) (getBounds().height() * getTranslateYPercentage());
        }
        canvas.translate(translateX, translateY);
        canvas.scale(getScaleX(), getScaleY(), getPivotX(), getPivotY());
        canvas.rotate(getRotate(), getPivotX(), getPivotY());
        if (getRotateX() != 0 || getRotateY() != 0) {
            this.mCamera.save();
            this.mCamera.rotateX(getRotateX());
            this.mCamera.rotateY(getRotateY());
            this.mCamera.getMatrix(this.mMatrix);
            this.mMatrix.preTranslate(-getPivotX(), -getPivotY());
            this.mMatrix.postTranslate(getPivotX(), getPivotY());
            this.mCamera.restore();
            canvas.concat(this.mMatrix);
        }
        drawSelf(canvas);
    }

    public Rect clipSquare(Rect rect) {
        int minSide = Math.min(rect.width(), rect.height());
        int centerX = rect.centerX();
        int centerY = rect.centerY();
        int halfSide = minSide / 2;
        return new Rect(centerX - halfSide, centerY - halfSide, centerX + halfSide, centerY + halfSide);
    }
}