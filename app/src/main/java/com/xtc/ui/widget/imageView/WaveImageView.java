package com.xtc.ui.widget.imageView;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/** 播放帧动画的 ImageView，用于声波动画。 */
public class WaveImageView extends ImageView {
    private AnimationDrawable mDrawable;

    public WaveImageView(Context context) {
        super(context);
    }

    public WaveImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WaveImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
    }

    @Override
    public void setBackgroundResource(int resId) {
        super.setBackgroundResource(resId);
    }

    public void startAnimation() {
        if (getDrawable() == null || !(getDrawable() instanceof AnimationDrawable)) {
            return;
        }
        this.mDrawable = (AnimationDrawable) getDrawable();
        this.mDrawable.start();
    }

    public void stopAnimation() {
        AnimationDrawable animationDrawable = this.mDrawable;
        if (animationDrawable == null || !animationDrawable.isRunning()) {
            return;
        }
        this.mDrawable.stop();
    }
}