package com.xtc.ui.widget.animation.sprite;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/** 圆形精灵，在绘制区域内画实心圆。 */
public class CircleSprite extends ShapeSprite {
    private static final String TAG = "CircleSprite";

    @Override
    public ValueAnimator getAnimation() {
        return null;
    }

    public CircleSprite(Context context) {
        super(context);
    }

    @Override
    public void drawShape(Canvas canvas, Paint paint) {
        Rect drawBounds = getDrawBounds();
        if (drawBounds != null) {
            canvas.drawCircle(drawBounds.centerX(), drawBounds.centerY(),
                    Math.min(drawBounds.width(), drawBounds.height()) / 2, paint);
        }
    }
}