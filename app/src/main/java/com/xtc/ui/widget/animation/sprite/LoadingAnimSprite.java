package com.xtc.ui.widget.animation.sprite;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;

/** 环形加载动画精灵：由 18 个小圆点围成一圈，按顺序延迟缩放形成跑马灯效果。 */
public class LoadingAnimSprite extends CircleSpriteGroup {
    private final int DELAY_TIME;
    private final int DOT_COUNT;
    private boolean mShouldInvokeParentMethod;

    public LoadingAnimSprite(Context context) {
        super(context);
        this.DOT_COUNT = 18;
        this.DELAY_TIME = 100;
        this.mShouldInvokeParentMethod = true;
    }

    @Override
    public Sprite[] onCreateChild() {
        Dot[] dots = new Dot[18];
        for (int index = 0; index < dots.length; index++) {
            dots[index] = new Dot(this.context);
            dots[index].setAnimationDelay(index * 100);
        }
        return dots;
    }

    /** 单个加载圆点，通过缩放关键帧循环播放动画。 */
    class Dot extends CircleSprite {
        private static final String TAG = "Dot";

        private Dot(Context context) {
            super(context);
            setScale(0.0f);
        }

        @Override
        public ValueAnimator getAnimation() {
            return new SpriteAnimatorBuilder(this).scale(new float[]{0.0f, 1.0f}, 1.0f, 0.0f).duration(1700L).build();
        }
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        if (this.mShouldInvokeParentMethod) {
            super.onBoundsChange(bounds);
        }
    }

    public boolean shouldInvokeParentMethod() {
        return this.mShouldInvokeParentMethod;
    }

    public void setShouldInvokeParentMethod(boolean shouldInvoke) {
        this.mShouldInvokeParentMethod = shouldInvoke;
    }

    public void resetDotRect(int radius, int centerX, int centerY) {
        int halfCenterX = centerX / 2;
        int halfCenterY = centerY / 2;
        for (int index = 0; index < getChildCount(); index++) {
            int halfSize = radius * 2;
            getChildAt(index).setDrawBounds(halfCenterX - halfSize, halfCenterY - halfSize,
                    halfCenterX + halfSize, halfSize + halfCenterY);
        }
        invalidateSelf();
    }
}