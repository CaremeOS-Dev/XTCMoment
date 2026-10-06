package com.xtc.ui.widget.imageView;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/** 支持启动/停止帧动画的 ImageView。 */
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

    /** 启动帧动画。 */
    public void startAnimation() {
        if (getDrawable() == null || !(getDrawable() instanceof AnimationDrawable)) {
            return;
        }
        this.mDrawable = (AnimationDrawable) getDrawable();
        this.mDrawable.start();
    }

    /** 停止帧动画。 */
    public void stopAnimation() {
        AnimationDrawable drawable = this.mDrawable;
        if (drawable == null || !drawable.isRunning()) {
            return;
        }
        this.mDrawable.stop();
    }
}