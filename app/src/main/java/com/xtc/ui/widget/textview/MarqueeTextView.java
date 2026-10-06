package com.xtc.ui.widget.textview;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;

/** 始终跑马灯滚动的 TextView。 */
public class MarqueeTextView extends AppCompatTextView {

    private static final int MARQUEE_REPEAT_LIMIT = -1;

    public MarqueeTextView(Context context) {
        super(context);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        setSingleLine();
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setMarqueeRepeatLimit(MARQUEE_REPEAT_LIMIT);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}