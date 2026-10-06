package com.xtc.ui.widget.animation.sprite;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.util.Property;
import android.view.animation.Interpolator;
import com.xtc.virtualselfapi.constants.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** 以关键帧方式为 Sprite 属性构建 ObjectAnimator 的链式构建器。 */
public class SpriteAnimatorBuilder {

    private Interpolator interpolator;
    private Sprite sprite;
    private List<PropertyValuesHolder> propertyValuesHolders = new ArrayList<>();
    private int repeatCount = -1;
    private long duration = Constants.DEFAULT_INIT_DELAY_TIME;

    public SpriteAnimatorBuilder(Sprite sprite) {
        this.sprite = sprite;
    }

    public SpriteAnimatorBuilder scale(float[] fractions, float... values) {
        getHolder(fractions, Sprite.SCALE, values);
        return this;
    }

    public SpriteAnimatorBuilder alpha(float[] fractions, int... values) {
        getHolder(fractions, Sprite.ALPHA, values);
        return this;
    }

    public SpriteAnimatorBuilder scaleX(float[] fractions, float... values) {
        getHolder(fractions, Sprite.SCALE, values);
        return this;
    }

    public SpriteAnimatorBuilder scaleY(float[] fractions, float... values) {
        getHolder(fractions, Sprite.SCALE_Y, values);
        return this;
    }

    public SpriteAnimatorBuilder rotateX(float[] fractions, int... values) {
        getHolder(fractions, Sprite.ROTATE_X, values);
        return this;
    }

    public SpriteAnimatorBuilder rotateY(float[] fractions, int... values) {
        getHolder(fractions, Sprite.ROTATE_Y, values);
        return this;
    }

    public SpriteAnimatorBuilder translateX(float[] fractions, int... values) {
        getHolder(fractions, Sprite.TRANSLATE_X, values);
        return this;
    }

    public SpriteAnimatorBuilder translateY(float[] fractions, int... values) {
        getHolder(fractions, Sprite.TRANSLATE_Y, values);
        return this;
    }

    public SpriteAnimatorBuilder rotate(float[] fractions, int... values) {
        getHolder(fractions, Sprite.ROTATE, values);
        return this;
    }

    public SpriteAnimatorBuilder translateXPercentage(float[] fractions, float... values) {
        getHolder(fractions, Sprite.TRANSLATE_X_PERCENTAGE, values);
        return this;
    }

    public SpriteAnimatorBuilder translateYPercentage(float[] fractions, float... values) {
        getHolder(fractions, Sprite.TRANSLATE_Y_PERCENTAGE, values);
        return this;
    }

    public SpriteAnimatorBuilder interpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
        return this;
    }

    public SpriteAnimatorBuilder duration(long duration) {
        this.duration = duration;
        return this;
    }

    public SpriteAnimatorBuilder repeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
        return this;
    }

    public ObjectAnimator build() {
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(this.sprite,
                this.propertyValuesHolders.toArray(new PropertyValuesHolder[this.propertyValuesHolders.size()]));
        animator.setDuration(this.duration);
        animator.setRepeatCount(this.repeatCount);
        animator.setInterpolator(this.interpolator);
        return animator;
    }

    public PropertyValuesHolder getHolder(float[] fractions, Property property, float[] values) {
        ensurePair(fractions.length, values.length);
        Keyframe[] keyframes = new Keyframe[fractions.length];
        for (int index = 0; index < values.length; index++) {
            keyframes[index] = Keyframe.ofFloat(fractions[index], values[index]);
        }
        PropertyValuesHolder holder = PropertyValuesHolder.ofKeyframe(property, keyframes);
        this.propertyValuesHolders.add(holder);
        return holder;
    }

    public PropertyValuesHolder getHolder(float[] fractions, Property property, int[] values) {
        ensurePair(fractions.length, values.length);
        Keyframe[] keyframes = new Keyframe[fractions.length];
        for (int index = 0; index < values.length; index++) {
            keyframes[index] = Keyframe.ofInt(fractions[index], values[index]);
        }
        PropertyValuesHolder holder = PropertyValuesHolder.ofKeyframe(property, keyframes);
        this.propertyValuesHolders.add(holder);
        return holder;
    }

    private void ensurePair(int fractionLength, int valueLength) {
        if (fractionLength != valueLength) {
            throw new IllegalStateException(String.format(Locale.getDefault(),
                    "The fractions.length must equal values.length, fraction.length[%d], values.length[%d]",
                    Integer.valueOf(fractionLength), Integer.valueOf(valueLength)));
        }
    }
}