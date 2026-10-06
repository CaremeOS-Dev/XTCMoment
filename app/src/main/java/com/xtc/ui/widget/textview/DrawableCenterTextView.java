package com.xtc.ui.widget.textview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

/** 左侧图标与文本整体居中的 TextView。 */
public class DrawableCenterTextView extends TextView {

    public DrawableCenterTextView(Context context) {
        super(context);
    }

    public DrawableCenterTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableCenterTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable[] compoundDrawables = getCompoundDrawables();
        if (compoundDrawables != null) {
            Drawable leftDrawable = compoundDrawables[0];
            if (leftDrawable != null) {
                canvas.translate((getWidth() - ((getPaint().measureText(getText().toString())
                        + leftDrawable.getIntrinsicWidth()) + getCompoundDrawablePadding())) / 2.0f, 0.0f);
            }
        }
        super.onDraw(canvas);
    }
}