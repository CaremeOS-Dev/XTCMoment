package com.xtc.ui.widget.textview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import com.xtc.log.LogUtil;
import com.xtc.ui.widget.R;
import com.xtc.ui.widget.util.UiCommonUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** 自动滚动文本：新文本从底部淡入上移，用于语音识别结果展示。 */
public class AutoScrollTextView extends View {
    public static final int SCROLL_DOWN = 1;
    public static final int SCROLL_UP = 0;
    private float lineHeight;
    private float lineMaxWidth;
    private int maxLineCount;
    private OnTextChangedListener onTextChangedListener;
    private int scrollDirect;
    private boolean scrolling;
    private boolean setTitle;
    private Paint textPaint;
    private List<TextStyle> textRows;
    private String title;

    /** 文本变化回调。 */
    public interface OnTextChangedListener {
        void onTextChanged(String text);
    }

    /** 单行文本及其透明度与纵坐标。 */
    private class TextStyle {
        int alpha;
        String text;
        float y;

        TextStyle(String text, int alpha, float y) {
            this.text = text;
            this.alpha = alpha;
            this.y = y;
        }
    }

    public AutoScrollTextView(Context context) {
        super(context);
        this.textRows = new ArrayList<>();
        init();
    }

    public AutoScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.textRows = new ArrayList<>();
        init();
    }

    public AutoScrollTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.textRows = new ArrayList<>();
        init();
    }

    public AutoScrollTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.textRows = new ArrayList<>();
        init();
    }

    private void init() {
        this.textPaint = createTextPaint(255);
        this.lineMaxWidth = this.textPaint.measureText("一二三四五六七八九十");
        this.maxLineCount = 4;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint.FontMetrics fontMetrics = this.textPaint.getFontMetrics();
        float measuredLineHeight = fontMetrics.bottom - fontMetrics.top;
        this.lineHeight = measuredLineHeight;
        if (this.setTitle) {
            canvas.drawText(this.title, (getWidth() / 2) - (this.textPaint.measureText(this.title) / 2.0f),
                    measuredLineHeight, this.textPaint);
            return;
        }
        synchronized (this) {
            if (this.textRows.isEmpty()) {
                return;
            }
            this.scrolling = true;
            float startX = (getWidth() / 2) - (this.textPaint.measureText(this.textRows.get(0).text) / 2.0f);
            if (this.textRows.size() <= 2) {
                for (int index = 0; index < 2 && index < this.textRows.size(); index++) {
                    TextStyle textRow = this.textRows.get(index);
                    this.textPaint.setAlpha(textRow.alpha);
                    canvas.drawText(textRow.text, startX, textRow.y, this.textPaint);
                }
            } else {
                boolean needsRedraw = false;
                for (int index = 0; index < this.textRows.size(); index++) {
                    TextStyle textRow = this.textRows.get(index);
                    this.textPaint.setAlpha(textRow.alpha);
                    canvas.drawText(textRow.text, startX, textRow.y, this.textPaint);
                    if (textRow.alpha < 255) {
                        textRow.alpha += 51;
                        needsRedraw = true;
                    }
                    if (this.textRows.size() > 2) {
                        if (this.scrollDirect == 0) {
                            textRow.y -= this.lineHeight / 9.0f;
                        } else if (textRow.y < this.lineHeight + (this.lineHeight * index)) {
                            textRow.y += this.lineHeight / 9.0f;
                            needsRedraw = true;
                        }
                    }
                }
                if (needsRedraw) {
                    postInvalidateDelayed(50L);
                } else {
                    this.scrolling = false;
                }
            }
        }
    }

    private Paint createTextPaint(int alpha) {
        Paint paint = new Paint();
        paint.setTextSize(TypedValue.applyDimension(2, 15.0f, getContext().getResources().getDisplayMetrics()));
        paint.setColor(UiCommonUtil.getColor(getContext(), R.color.color_999999));
        paint.setAlpha(alpha);
        return paint;
    }

    public void resetText() {
        synchronized (this) {
            this.textRows.clear();
        }
    }

    public void formatText() {
        this.scrollDirect = 1;
        StringBuffer allText = new StringBuffer("\n");
        synchronized (this) {
            for (int index = 0; index < this.textRows.size(); index++) {
                TextStyle textRow = this.textRows.get(index);
                if (textRow != null) {
                    textRow.alpha = 255;
                    allText.append(textRow.text + "\n");
                }
            }
        }
        postInvalidateDelayed(100L);
        LogUtil.i("formatText:" + allText.toString());
    }

    public void appendText(String text) {
        this.setTitle = false;
        this.scrollDirect = 0;
        synchronized (this) {
            if (this.textRows.size() > this.maxLineCount) {
                return;
            }
            if (text.length() <= 10) {
                if (this.textRows.isEmpty()) {
                    this.textRows.add(new TextStyle(text, 255, this.lineHeight + (this.lineHeight * this.textRows.size())));
                } else {
                    TextStyle lastRow = this.textRows.get(this.textRows.size() - 1);
                    this.textRows.set(this.textRows.size() - 1, new TextStyle(text, lastRow.alpha, lastRow.y));
                }
            } else {
                ArrayList<String> wrappedLines = new ArrayList<>();
                StringBuffer lineBuffer = new StringBuffer();
                int index = 0;
                float lineWidth = 0.0f;
                while (index < text.length()) {
                    char currentChar = text.charAt(index);
                    lineWidth += this.textPaint.measureText(String.valueOf(currentChar));
                    if (lineWidth <= this.lineMaxWidth) {
                        lineBuffer.append(currentChar);
                    } else {
                        if (wrappedLines.size() >= this.maxLineCount) {
                            break;
                        }
                        wrappedLines.add(lineBuffer.toString());
                        index--;
                        lineBuffer.delete(0, lineBuffer.length());
                        lineWidth = 0.0f;
                    }
                    index++;
                }
                if (!TextUtils.isEmpty(lineBuffer.toString()) && wrappedLines.size() < this.maxLineCount) {
                    wrappedLines.add(lineBuffer.toString());
                }
                if (this.textRows.isEmpty()) {
                    for (int lineIndex = 0; lineIndex < wrappedLines.size(); lineIndex++) {
                        if (lineIndex < 2) {
                            this.textRows.add(new TextStyle(wrappedLines.get(lineIndex), 255, this.lineHeight + (this.lineHeight * lineIndex)));
                        } else {
                            this.textRows.add(new TextStyle(wrappedLines.get(lineIndex), 0, this.lineHeight + (this.lineHeight * lineIndex)));
                        }
                    }
                } else {
                    for (int lineIndex = 0; lineIndex < wrappedLines.size(); lineIndex++) {
                        if (this.textRows.size() > lineIndex) {
                            TextStyle existingRow = this.textRows.get(lineIndex);
                            this.textRows.set(lineIndex, new TextStyle(wrappedLines.get(lineIndex), existingRow.alpha, existingRow.y));
                        } else {
                            TextStyle lastRow = this.textRows.get(this.textRows.size() - 1);
                            if (lineIndex < 2) {
                                this.textRows.add(new TextStyle(wrappedLines.get(lineIndex), 255, lastRow.y + this.lineHeight));
                            } else {
                                this.textRows.add(new TextStyle(wrappedLines.get(lineIndex), 0, lastRow.y + this.lineHeight));
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
        this.setTitle = true;
        invalidate();
    }

    public void setTitle(String title) {
        this.title = title;
        this.setTitle = true;
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
        StringBuffer allText = new StringBuffer();
        Iterator<TextStyle> iterator = this.textRows.iterator();
        while (iterator.hasNext()) {
            allText.append(iterator.next().text);
        }
        return allText.toString();
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