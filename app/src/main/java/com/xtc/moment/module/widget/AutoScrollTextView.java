package com.xtc.moment.module.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Text view that draws its content line by line and can append new lines that fade in and scroll
 * upwards, used to display long debug/status text in a fixed area.
 */
public class AutoScrollTextView extends View {

    private static final String TAG = "AutoScrollTextView";

    /** Maximum number of characters of a single line before it is truncated. */
    public static final int MUL_LINE_MAX_LENGTH = 40;
    /** Scroll direction: content moves up. */
    public static final int SCROLL_UP = 0;
    /** Scroll direction: content moves down. */
    public static final int SCROLL_DOWN = 1;

    /** Lines shorter than this are treated as single-line content. */
    private static final int SINGLE_LINE_MAX_LENGTH = 10;
    /** Alpha step applied on every animation frame while fading a line in. */
    private static final int ALPHA_STEP = 51;
    /** Delay between animation frames, in milliseconds. */
    private static final long FRAME_DELAY_MS = 50L;

    private Paint textPaint;
    private List<TextStyle> textRows = new ArrayList<TextStyle>();
    private float lineHeight;
    private float lineMaxWidth;
    private int maxLineCount;
    private int scrollDirect;
    private boolean scrolling;
    private boolean titleMode;
    private String title;
    private OnTextChangedListener onTextChangedListener;

    /** Receives the plain text content whenever it changes. */
    public interface OnTextChangedListener {
        void onTextChanged(String text);
    }

    /** A single rendered line with its current alpha and vertical position. */
    private class TextStyle {
        String text;
        int alpha;
        float y;

        TextStyle(String text, int alpha, float y) {
            this.text = text;
            this.alpha = alpha;
            this.y = y;
        }
    }

    public AutoScrollTextView(Context context) {
        super(context);
        init();
    }

    public AutoScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AutoScrollTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.textPaint = createTextPaint(255);
        this.lineMaxWidth = this.textPaint.measureText(getContext().getString(R.string.line_length_holder));
        this.maxLineCount = 4;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint.FontMetrics fontMetrics = this.textPaint.getFontMetrics();
        float lineHeight = fontMetrics.bottom - fontMetrics.top;
        this.lineHeight = lineHeight;
        if (this.titleMode) {
            canvas.drawText(this.title, (getWidth() / 2) - (this.textPaint.measureText(this.title) / 2.0f),
                    lineHeight, this.textPaint);
            return;
        }
        synchronized (this) {
            if (this.textRows.isEmpty()) {
                return;
            }
            this.scrolling = true;
            float startX = (getWidth() / 2) - (this.textPaint.measureText(this.textRows.get(0).text) / 2.0f);
            if (this.textRows.size() <= 2) {
                for (int i = 0; i < 2 && i < this.textRows.size(); i++) {
                    TextStyle row = this.textRows.get(i);
                    this.textPaint.setAlpha(row.alpha);
                    canvas.drawText(row.text, startX, row.y, this.textPaint);
                }
            } else {
                boolean needsAnotherFrame = false;
                for (int i = 0; i < this.textRows.size(); i++) {
                    TextStyle row = this.textRows.get(i);
                    this.textPaint.setAlpha(row.alpha);
                    canvas.drawText(row.text, startX, row.y, this.textPaint);
                    if (row.alpha < 255) {
                        row.alpha += ALPHA_STEP;
                        needsAnotherFrame = true;
                    }
                    if (this.scrollDirect == SCROLL_UP) {
                        row.y -= this.lineHeight / 9.0f;
                    } else if (row.y < this.lineHeight + (this.lineHeight * i)) {
                        row.y += this.lineHeight / 9.0f;
                        needsAnotherFrame = true;
                    }
                }
                if (needsAnotherFrame) {
                    postInvalidateDelayed(FRAME_DELAY_MS);
                } else {
                    this.scrolling = false;
                }
            }
        }
    }

    private Paint createTextPaint(int alpha) {
        Paint paint = new Paint();
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15.0f,
                getContext().getResources().getDisplayMetrics()));
        paint.setColor(getContext().getResources().getColor(R.color.color_999999));
        paint.setAlpha(alpha);
        paint.setAntiAlias(true);
        return paint;
    }

    public void resetText() {
        synchronized (this) {
            this.textRows.clear();
        }
    }

    /** Marks every line fully opaque and starts scrolling down. */
    public void formatText() {
        this.scrollDirect = SCROLL_DOWN;
        StringBuffer buffer = new StringBuffer("\n");
        synchronized (this) {
            for (int i = 0; i < this.textRows.size(); i++) {
                TextStyle row = this.textRows.get(i);
                if (row != null) {
                    row.alpha = 255;
                    buffer.append(row.text + "\n");
                }
            }
        }
        postInvalidateDelayed(100L);
        LogUtil.i(TAG, "formatText:" + buffer.toString());
    }

    /** Appends {@code text}, wrapping it into at most {@link #maxLineCount} lines. */
    public void appendText(String text) {
        this.titleMode = false;
        this.scrollDirect = SCROLL_UP;
        synchronized (this) {
            if (this.textRows.size() > this.maxLineCount) {
                return;
            }
            if (text.length() > MUL_LINE_MAX_LENGTH) {
                LogUtil.d(TAG, "appendText: textLength:" + text.length() + ";text:" + text);
                text = text.substring(0, MUL_LINE_MAX_LENGTH);
                LogUtil.d(TAG, "appendText: new textLength:" + text.length() + ";new text:" + text);
            }
            if (text.length() <= SINGLE_LINE_MAX_LENGTH) {
                if (this.textRows.isEmpty()) {
                    this.textRows.add(new TextStyle(text, 255, this.lineHeight + (this.lineHeight * this.textRows.size())));
                } else {
                    TextStyle last = this.textRows.get(this.textRows.size() - 1);
                    this.textRows.set(this.textRows.size() - 1, new TextStyle(text, last.alpha, last.y));
                }
            } else {
                ArrayList<String> lines = new ArrayList<String>();
                StringBuffer line = new StringBuffer();
                int index = 0;
                float measured = 0.0f;
                while (index < text.length()) {
                    char c = text.charAt(index);
                    measured += this.textPaint.measureText(String.valueOf(c));
                    if (measured <= this.lineMaxWidth) {
                        line.append(c);
                    } else {
                        if (lines.size() >= this.maxLineCount) {
                            break;
                        }
                        lines.add(line.toString());
                        index--;
                        line.delete(0, line.length());
                        measured = 0.0f;
                    }
                    index++;
                }
                if (!TextUtils.isEmpty(line.toString()) && lines.size() < this.maxLineCount) {
                    lines.add(line.toString());
                }
                if (this.textRows.isEmpty()) {
                    for (int i = 0; i < lines.size(); i++) {
                        if (i < 2) {
                            this.textRows.add(new TextStyle(lines.get(i), 255, this.lineHeight + (this.lineHeight * i)));
                        } else {
                            this.textRows.add(new TextStyle(lines.get(i), 0, this.lineHeight + (this.lineHeight * i)));
                        }
                    }
                } else {
                    for (int i = 0; i < lines.size(); i++) {
                        if (this.textRows.size() > i) {
                            TextStyle existing = this.textRows.get(i);
                            this.textRows.set(i, new TextStyle(lines.get(i), existing.alpha, existing.y));
                        } else {
                            TextStyle last = this.textRows.get(this.textRows.size() - 1);
                            if (i < 2) {
                                this.textRows.add(new TextStyle(lines.get(i), 255, last.y + this.lineHeight));
                            } else {
                                this.textRows.add(new TextStyle(lines.get(i), 0, last.y + this.lineHeight));
                            }
                        }
                    }
                }
            }
            if (!this.scrolling) {
                invalidate();
            }
            textChanged();
        }
    }

    public void setTextColor(int color) {
        this.textPaint.setColor(color);
        invalidate();
    }

    public void setTitle(int resId) {
        this.title = getContext().getString(resId);
        this.titleMode = true;
        invalidate();
    }

    public void setTitle(String title) {
        this.title = title;
        this.titleMode = true;
        invalidate();
    }

    public void setOnTextChangedListener(OnTextChangedListener listener) {
        this.onTextChangedListener = listener;
    }

    private void textChanged() {
        OnTextChangedListener listener = this.onTextChangedListener;
        if (listener != null) {
            listener.onTextChanged(getText());
        }
    }

    public String getText() {
        StringBuffer buffer = new StringBuffer();
        Iterator<TextStyle> iterator = this.textRows.iterator();
        while (iterator.hasNext()) {
            buffer.append(iterator.next().text);
        }
        return buffer.toString();
    }

    public int getScrollDirect() {
        return this.scrollDirect;
    }

    public void setScrollDirect(int scrollDirect) {
        this.scrollDirect = scrollDirect;
    }

    public float getLineMaxWidth() {
        return this.lineMaxWidth;
    }

    public void setLineMaxWidth(float lineMaxWidth) {
        this.lineMaxWidth = lineMaxWidth;
    }

    public int getMaxLineCount() {
        return this.maxLineCount;
    }

    public void setMaxLineCount(int maxLineCount) {
        this.maxLineCount = maxLineCount;
    }

    public boolean isScrolling() {
        return this.scrolling;
    }
}