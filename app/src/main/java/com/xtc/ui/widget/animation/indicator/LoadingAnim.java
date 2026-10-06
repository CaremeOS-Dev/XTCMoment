package com.xtc.ui.widget.animation.indicator;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;

import com.xtc.ui.widget.R;
import com.xtc.ui.widget.util.UiCommonUtil;

/**
 * Builds the six-dot {@link AnimationDrawable} used on buttons.
 *
 * <p>Each frame is a {@link BallSpinFadeAnimation} at a different rotation of
 * the alpha ring, so the dots appear to chase each other.
 */
public class LoadingAnim {

    private Context mContext;

    public LoadingAnim(Context context) {
        this.mContext = context;
    }

    public AnimationDrawable createAnim() {
        return createAnim(R.color.color_ffffff);
    }

    public AnimationDrawable createAnim(int colorRes) {
        return createAnim(colorRes, 0.9f, 100);
    }

    public AnimationDrawable createAnim(int colorRes, float scale, int duration) {
        int color = UiCommonUtil.getColor(this.mContext, colorRes);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int a64 = Color.argb(64, red, green, blue);
        int a102 = Color.argb(102, red, green, blue);
        int a128 = Color.argb(128, red, green, blue);
        int a191 = Color.argb(191, red, green, blue);
        int a217 = Color.argb(217, red, green, blue);
        int a255 = Color.argb(255, red, green, blue);
        int[] frame1 = {a128, a191, a217, a255, a64, a102};
        int[] frame2 = {a102, a128, a191, a217, a255, a64};
        int[] frame3 = {a64, a102, a128, a191, a217, a255};
        int[] frame4 = {a255, a64, a102, a128, a191, a217};
        int[] frame5 = {a217, a255, a64, a102, a128, a191};
        int[] frame6 = {a191, a217, a255, a64, a102, a128};
        AnimationDrawable animationDrawable = new AnimationDrawable();
        setAnimationDrawableFrame(frame1, animationDrawable, scale, duration);
        setAnimationDrawableFrame(frame2, animationDrawable, scale, duration);
        setAnimationDrawableFrame(frame3, animationDrawable, scale, duration);
        setAnimationDrawableFrame(frame4, animationDrawable, scale, duration);
        setAnimationDrawableFrame(frame5, animationDrawable, scale, duration);
        setAnimationDrawableFrame(frame6, animationDrawable, scale, duration);
        animationDrawable.setOneShot(false);
        return animationDrawable;
    }

    private void setAnimationDrawableFrame(int[] colors, AnimationDrawable animationDrawable, float scale, int duration) {
        animationDrawable.addFrame(new BallSpinFadeAnimation(this.mContext, colors, scale), duration);
    }
}
