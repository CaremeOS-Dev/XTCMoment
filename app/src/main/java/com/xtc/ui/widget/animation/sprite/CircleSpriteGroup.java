package com.xtc.ui.widget.animation.sprite;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import com.xtc.log.LogUtil;
import com.xtc.ui.widget.util.UiCommonUtil;

/** 圆形排布的精灵组：子精灵围绕中心按角度均匀分布，并支持整体缩放与半径调整。 */
public abstract class CircleSpriteGroup extends SpriteGroup {
    private static final String TAG = "CircleSpriteGroup";
    private float groupScale;
    protected int radiusPx;

    public CircleSpriteGroup(Context context) {
        super(context);
        this.groupScale = 1.0f;
        this.radiusPx = UiCommonUtil.dp2Px(context, 4.0f);
    }

    public void setRadiusDp(int radiusDp) {
        LogUtil.d(TAG, "setRadiusdp = " + radiusDp);
        this.radiusPx = UiCommonUtil.dp2Px(this.context, (float) radiusDp);
    }

    @Override
    public void drawChild(Canvas canvas) {
        for (int index = 0; index < getChildCount(); index++) {
            Sprite child = getChildAt(index);
            int saveCount = canvas.save();
            canvas.rotate((index * 360) / getChildCount(), getBounds().centerX(), getBounds().centerY());
            child.draw(canvas);
            canvas.restoreToCount(saveCount);
        }
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        Rect scaledBounds = scaleFix(clipSquare(bounds));
        for (int index = 0; index < getChildCount(); index++) {
            getChildAt(index).setDrawBounds(scaledBounds.left, scaledBounds.top,
                    scaledBounds.right + (this.radiusPx * 2), scaledBounds.top + (this.radiusPx * 2));
        }
    }

    public void setGroupScale(float groupScale) {
        LogUtil.i(TAG, "setGroupScale = " + groupScale);
        if (groupScale > 1.0f) {
            LogUtil.w(TAG, "error , scale > 1.0f , return !");
        } else {
            this.groupScale = groupScale;
        }
    }

    private Rect scaleFix(Rect rect) {
        int centerX = rect.centerX();
        int centerY = rect.centerY();
        int halfWidth = (int) ((rect.width() / 2) * this.groupScale);
        return new Rect(centerX - halfWidth, centerY - halfWidth, centerX + halfWidth, centerY + halfWidth);
    }
}