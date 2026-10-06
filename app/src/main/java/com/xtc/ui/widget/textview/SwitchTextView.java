package com.xtc.ui.widget.textview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import com.xtc.ui.widget.R;

/** 轮播文本：按固定间隔循环切换文案。 */
public class SwitchTextView extends TextSwitcher implements ViewSwitcher.ViewFactory {
    private final int DEFAULT_DURATION;
    private final float DEFAULT_TEXT_SIZE;
    private final Runnable animRunnable;
    private int duration;
    private CharSequence[] textContents;
    private int textPosition;
    private float textSize;

    public SwitchTextView(Context context) {
        this(context, null);
    }

    public SwitchTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.DEFAULT_DURATION = 3000;
        this.textPosition = 0;
        this.DEFAULT_TEXT_SIZE = 15.0f;
        this.animRunnable = new Runnable() {
            @Override
            public void run() {
                SwitchTextView switchTextView = SwitchTextView.this;
                switchTextView.textPosition = switchTextView.textPosition == SwitchTextView.this.textContents.length + (-1)
                        ? 0 : SwitchTextView.this.textPosition + 1;
                SwitchTextView.this.start();
            }
        };
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.SwitchTextView);
        this.duration = attributes.getInteger(R.styleable.SwitchTextView_duration, 3000);
        this.textSize = attributes.getFloat(R.styleable.SwitchTextView_text_size, 15.0f);
        this.textContents = attributes.getTextArray(R.styleable.SwitchTextView_texts);
        attributes.recycle();
        setFactory(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void start() {
        setText(this.textContents[this.textPosition]);
        postDelayed(this.animRunnable, this.duration);
    }

    public void stop() {
        removeCallbacks(this.animRunnable);
    }

    @Override
    public View makeView() {
        TextView textView = new TextView(getContext());
        textView.setHeight(60);
        textView.setGravity(17);
        textView.setTextSize(this.textSize);
        textView.setTextColor(getContext().getColor(R.color.color_cccccc));
        return textView;
    }
}