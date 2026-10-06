package com.xtc.ui.widget.textview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import com.xtc.log.LogUtil;

/** 自适应字号文本：当行数超过最大行数时逐次缩小字号。 */
public class AutoResizeTextView extends TextView implements ViewTreeObserver.OnGlobalLayoutListener {
    private static final String TAG = "AutoResizeTextView";
    private int maxFontSize;
    private int maxLineCount;
    private int minFontSize;

    public int getMaxLineCount() {
        return this.maxLineCount;
    }

    public void setMaxLineCount(int maxLineCount) {
        this.maxLineCount = maxLineCount;
    }

    public int getMinFontSize() {
        return this.minFontSize;
    }

    public void setMinFontSize(int minFontSize) {
        this.minFontSize = minFontSize;
    }

    public int getMaxFontSize() {
        return this.maxFontSize;
    }

    public void setMaxFontSize(int maxFontSize) {
        this.maxFontSize = maxFontSize;
    }

    public AutoResizeTextView(Context context) {
        this(context, null);
    }

    public AutoResizeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoResizeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.maxLineCount = 2;
        this.minFontSize = 11;
        this.maxFontSize = 13;
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        int lineCount = getLineCount();
        int maxLines = getMaxLines();
        LogUtil.w(TAG, "this time , lineCount = " + lineCount + " , src maxLines = " + maxLines + " , default maxLines = " + this.maxLineCount);
        if (maxLines == -1 || maxLines == Integer.MAX_VALUE) {
            maxLines = this.maxLineCount;
        }
        if (lineCount > maxLines) {
            float textSize = getTextSize() - 2.0f;
            setTextSize(0, textSize);
            LogUtil.d(TAG, "after textsize = " + textSize);
        }
    }
}