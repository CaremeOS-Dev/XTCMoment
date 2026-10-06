package com.xtc.ui.widget.animation.sprite;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;

/** 精灵组：把多个子精灵组合在一起统一绘制、统一控制动画与颜色。 */
public abstract class SpriteGroup extends Sprite {
    private int color;
    private Sprite[] sprites;

    @Override
    protected void drawSelf(Canvas canvas) {
    }

    @Override
    public ValueAnimator getAnimation() {
        return null;
    }

    public void onChildCreated(Sprite... sprites) {
    }

    public abstract Sprite[] onCreateChild();

    public SpriteGroup(Context context) {
        super(context);
        this.sprites = onCreateChild();
        initCallBack();
        onChildCreated(this.sprites);
    }

    private void initCallBack() {
        Sprite[] sprites = this.sprites;
        if (sprites != null) {
            for (Sprite sprite : sprites) {
                sprite.setCallback(this);
            }
        }
    }

    public int getChildCount() {
        Sprite[] sprites = this.sprites;
        if (sprites == null) {
            return 0;
        }
        return sprites.length;
    }

    public Sprite getChildAt(int index) {
        Sprite[] sprites = this.sprites;
        if (sprites == null) {
            return null;
        }
        return sprites[index];
    }

    @Override
    public void setColor(int color) {
        this.color = color;
        for (int index = 0; index < getChildCount(); index++) {
            getChildAt(index).setColor(color);
        }
    }

    @Override
    public int getColor() {
        return this.color;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawChild(canvas);
    }

    public void drawChild(Canvas canvas) {
        Sprite[] sprites = this.sprites;
        if (sprites != null) {
            for (Sprite sprite : sprites) {
                int saveCount = canvas.save();
                sprite.draw(canvas);
                canvas.restoreToCount(saveCount);
            }
        }
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        for (Sprite sprite : this.sprites) {
            sprite.setBounds(bounds);
        }
    }

    @Override
    public void start() {
        super.start();
        AnimationUtils.start(this.sprites);
    }

    @Override
    public void stop() {
        super.stop();
        AnimationUtils.stop(this.sprites);
    }

    @Override
    public boolean isRunning() {
        return AnimationUtils.isRunning(this.sprites) || super.isRunning();
    }
}