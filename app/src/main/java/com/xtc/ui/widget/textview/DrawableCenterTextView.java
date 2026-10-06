package com.xtc.ui.widget.textview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

/** 让左侧 drawable 与文字整体居中的 TextView。 */
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
        Drawable leftDrawable = compoundDrawables != null ? compoundDrawables[0] : null;
        if (leftDrawable != null) {
            float textWidth = getPaint().measureText(getText().toString());
            float offset = (getWidth() - (textWidth + leftDrawable.getIntrinsicWidth()
                    + getCompoundDrawablePadding())) / 2.0f;
            canvas.translate(offset, 0.0f);
        }
        super.onDraw(canvas);
    }
}