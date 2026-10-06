package com.xtc.ui.widget.textview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.xtc.log.LogUtil;
import java.util.ArrayList;
import java.util.List;

/** 两端对齐文本：按字数拆分每行后逐字绘制，支持左/中/右对齐与最大行数限制。 */
public class AlignTextView extends TextView {
    private static final String TAG = "AlignTextView";
    private Align align;
    private boolean firstCalc;
    private float lineSpacingAdd;
    private float lineSpacingMultiplier;
    private List<String> lines;
    private int maxLines;
    private int originalHeight;
    private int originalLineCount;
    private int originalPaddingBottom;
    private boolean setPaddingFromMe;
    private List<Integer> tailLines;
    private float textHeight;
    private float textLineSpaceExtra;
    private int width;

    /** 对齐方式。 */
    public enum Align {
        ALIGN_LEFT,
        ALIGN_CENTER,
        ALIGN_RIGHT
    }

    public AlignTextView(Context context) {
        super(context);
        this.textLineSpaceExtra = 0.0f;
        this.lines = new ArrayList<>();
        this.tailLines = new ArrayList<>();
        this.align = Align.ALIGN_LEFT;
        this.firstCalc = true;
        this.lineSpacingMultiplier = 1.0f;
        this.lineSpacingAdd = 0.0f;
        this.originalHeight = 0;
        this.originalLineCount = 0;
        this.originalPaddingBottom = 0;
        this.setPaddingFromMe = false;
        setTextIsSelectable(false);
    }

    public AlignTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.textLineSpaceExtra = 0.0f;
        this.lines = new ArrayList<>();
        this.tailLines = new ArrayList<>();
        this.align = Align.ALIGN_LEFT;
        this.firstCalc = true;
        this.lineSpacingMultiplier = 1.0f;
        this.lineSpacingAdd = 0.0f;
        this.originalHeight = 0;
        this.originalLineCount = 0;
        this.originalPaddingBottom = 0;
        this.setPaddingFromMe = false;
        setTextIsSelectable(false);
        this.lineSpacingMultiplier = attrs.getAttributeFloatValue("http://schemas.android.com/apk/res/android", "lineSpacingMultiplier", 1.0f);
        TypedArray lineSpacingAttrs = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.lineSpacingExtra});
        this.lineSpacingAdd = lineSpacingAttrs.getDimensionPixelSize(0, 0);
        lineSpacingAttrs.recycle();
        this.originalPaddingBottom = getPaddingBottom();
        TypedArray alignAttrs = context.obtainStyledAttributes(attrs, com.xtc.ui.widget.R.styleable.AlignTextView);
        this.maxLines = alignAttrs.getInt(com.xtc.ui.widget.R.styleable.AlignTextView_maxLines, 0);
        int alignValue = alignAttrs.getInt(com.xtc.ui.widget.R.styleable.AlignTextView_align, 0);
        if (alignValue == 1) {
            this.align = Align.ALIGN_CENTER;
        } else if (alignValue == 2) {
            this.align = Align.ALIGN_RIGHT;
        } else {
            this.align = Align.ALIGN_LEFT;
        }
        alignAttrs.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        LogUtil.d(TAG, "onLayout--->>");
        LogUtil.d(TAG, "clear 之前:" + this.lines.size());
        super.onLayout(changed, left, top, right, bottom);
        if (this.firstCalc) {
            calc();
        }
    }

    private void calc() {
        this.width = getMeasuredWidth();
        String text = getText().toString();
        TextPaint paint = getPaint();
        this.lines.clear();
        this.tailLines.clear();
        LogUtil.d(TAG, "clear 之后:" + this.lines.size());
        for (String paragraph : text.split("\\n")) {
            calc(paint, paragraph);
        }
        measureTextViewHeight(text, paint.getTextSize(), (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight());
        this.textHeight = (this.originalHeight * 1.0f) / this.originalLineCount;
        float height = this.textHeight;
        this.textLineSpaceExtra = ((this.lineSpacingMultiplier - 1.0f) * height) + this.lineSpacingAdd;
        int extraPadding = (int) ((this.textLineSpaceExtra + height) * (this.lines.size() - this.originalLineCount));
        this.setPaddingFromMe = true;
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), this.originalPaddingBottom + extraPadding);
        this.firstCalc = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        LogUtil.d(TAG, "ondraw:" + this.lines.size());
        if (this.firstCalc) {
            calc();
        }
        TextPaint paint = getPaint();
        paint.setColor(getCurrentTextColor());
        paint.drawableState = getDrawableState();
        this.width = getMeasuredWidth();
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float baselineOffset = getTextSize() - (((fontMetrics.bottom - fontMetrics.descent) + fontMetrics.ascent) - fontMetrics.top);
        float half = 2.0f;
        if ((getGravity() & 4096) == 0) {
            baselineOffset += (this.textHeight - baselineOffset) / 2.0f;
        }
        int paddingTop = getPaddingTop();
        int paddingLeft = getPaddingLeft();
        this.width = (this.width - paddingLeft) - getPaddingRight();
        int lineCount = this.lines.size();
        if (this.maxLines != 0) {
            int totalLines = this.lines.size();
            int maxLinesValue = this.maxLines;
            if (totalLines > maxLinesValue) {
                lineCount = maxLinesValue;
            }
        }
        int lineIndex = 0;
        while (lineIndex < lineCount) {
            float lineIndexF = lineIndex;
            float lineY = (this.textHeight * lineIndexF) + baselineOffset;
            String lineText = this.lines.get(lineIndex);
            float lineX = paddingLeft;
            float tailOffset = this.width - paint.measureText(lineText);
            lineText.length();
            if (this.tailLines.contains(Integer.valueOf(lineIndex))) {
                if (this.align == Align.ALIGN_CENTER) {
                    tailOffset /= half;
                } else if (this.align == Align.ALIGN_RIGHT) {
                    // 右对齐时 tailOffset 保持不变，整段右移
                }
                lineX += tailOffset;
            }
            int charIndex = 0;
            while (charIndex < lineText.length()) {
                int nextCharIndex = charIndex + 1;
                canvas.drawText(lineText.substring(charIndex, nextCharIndex),
                        paint.measureText(lineText.substring(0, charIndex)) + lineX,
                        paddingTop + lineY + (this.textLineSpaceExtra * lineIndexF), paint);
                charIndex = nextCharIndex;
            }
            lineIndex++;
            half = 2.0f;
        }
    }

    public void setAlign(Align align) {
        this.align = align;
        invalidate();
    }

    private void calc(Paint paint, String paragraph) {
        String sanitized = paragraph.replaceAll("\\p{So}+", "☒");
        if (sanitized.length() == 0) {
            this.lines.add("\n");
            return;
        }
        int charsPerLine = (int) (this.width / paint.measureText("中"));
        int lineStart = 0;
        StringBuilder lineBuilder = new StringBuilder(sanitized.substring(0, Math.min(charsPerLine, sanitized.length())));
        int index = charsPerLine;
        while (index < sanitized.length()) {
            int nextIndex = index + 1;
            if (paint.measureText(sanitized.substring(lineStart, nextIndex)) > this.width) {
                this.lines.add(lineBuilder.toString());
                lineBuilder = new StringBuilder();
                if (sanitized.length() - index > charsPerLine) {
                    int segmentEnd = index + charsPerLine;
                    if (!TextUtils.isEmpty(sanitized.substring(index, segmentEnd))) {
                        lineBuilder.append(sanitized.substring(index, segmentEnd));
                        nextIndex = index;
                    } else {
                        this.lines.add(sanitized.substring(index));
                    }
                    lineStart = index;
                    index = (nextIndex + charsPerLine) - 1;
                } else {
                    this.lines.add(sanitized.substring(index));
                    break;
                }
            } else {
                lineBuilder.append(sanitized.charAt(index));
            }
            index++;
        }
        if (lineBuilder.length() > 0) {
            this.lines.add(lineBuilder.toString());
        }
        this.tailLines.add(Integer.valueOf(this.lines.size() - 1));
    }

    @Override
    public void setText(CharSequence text, TextView.BufferType type) {
        this.firstCalc = true;
        LogUtil.d(TAG, text.toString());
        super.setText(text, type);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        if (!this.setPaddingFromMe) {
            this.originalPaddingBottom = bottom;
        }
        this.setPaddingFromMe = false;
        super.setPadding(left, top, right, bottom);
    }

    private void measureTextViewHeight(String text, float textSize, int width) {
        TextView measuringView = new TextView(getContext());
        if (this.maxLines != 0) {
            measuringView.setMaxLines(2);
            measuringView.setEllipsize(TextUtils.TruncateAt.END);
        }
        measuringView.setText(text);
        measuringView.setTextSize(0, textSize);
        measuringView.measure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(0, 0));
        this.originalLineCount = measuringView.getLineCount();
        this.originalHeight = measuringView.getMeasuredHeight();
    }
}