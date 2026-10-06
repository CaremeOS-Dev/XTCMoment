package com.xtc.web.core.jump;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.DynamicDrawableSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 支持中英文混排强制换行的 TextView。
 *
 * <p>原生 TextView 在超长无空格文本（例如中文长句）上换行表现不佳，这里在 onMeasure 阶段按字符逐个
 * 测量宽度，构造出行结构（{@link Line}）后在 onDraw 中自行绘制，从而精确控制断行位置、行距与段间距，
 * 并支持 {@link DynamicDrawableSpan}（表情）与 {@link BackgroundColorSpan}（高亮）两种 span。</p>
 */
public class ForceBreakTextView extends TextView {

    /** 已测量结果缓存，key 为文本内容。 */
    private static HashMap<String, SoftReference<MeasuredData>> measuredData = new HashMap<>();
    private static int hashIndex = 0;

    ArrayList<Line> contentList;
    protected CharSequence text;

    private Context context;
    private DisplayMetrics displayMetrics;
    private float lineSpacing;
    private int lineSpacingDP;
    private float lineWidthMax;
    private Paint.FontMetrics mFontMetrics;
    private Paint.FontMetricsInt mSpanFmInt;
    private int maxWidth;
    private int minHeight;
    private ArrayList<Object> obList;
    private int oneLineWidth;
    private TextPaint paint;
    private int paragraphSpacing;
    private Paint textBgColorPaint;
    private Rect textBgColorRect;
    private int textColor;
    private boolean useDefault;

    public ForceBreakTextView(Context context) {
        super(context);
        initFields();
        init(context);
    }

    public ForceBreakTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFields();
        init(context);
    }

    public ForceBreakTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFields();
        init(context);
    }

    private void initFields() {
        this.contentList = new ArrayList<>();
        this.paint = new TextPaint();
        this.mSpanFmInt = new Paint.FontMetricsInt();
        this.mFontMetrics = new Paint.FontMetrics();
        this.textColor = 0xFF000000;
        this.lineSpacingDP = 5;
        this.paragraphSpacing = -1;
        this.oneLineWidth = -1;
        this.lineWidthMax = -1.0f;
        this.obList = new ArrayList<>();
        this.useDefault = false;
        this.text = "";
        this.textBgColorPaint = new Paint();
        this.textBgColorRect = new Rect();
    }

    public void init(Context context) {
        this.context = context;
        this.paint.setAntiAlias(true);
        this.lineSpacing = dip2px(context, this.lineSpacingDP);
        this.minHeight = dip2px(context, 30.0f);
        this.displayMetrics = new DisplayMetrics();
    }

    public static int px2sp(Context context, float pxValue) {
        return (int) ((pxValue / context.getResources().getDisplayMetrics().scaledDensity) + 0.5f);
    }

    public static int dip2px(Context context, float dpValue) {
        return (int) ((dpValue * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    @Override
    public void setMaxWidth(int maxWidth) {
        super.setMaxWidth(maxWidth);
        this.maxWidth = maxWidth;
    }

    @Override
    public void setMinHeight(int minHeight) {
        super.setMinHeight(minHeight);
        this.minHeight = minHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.useDefault) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int availableWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int availableHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == View.MeasureSpec.AT_MOST) {
            // 保持使用父容器给出的宽度上限
        } else if (widthMode == View.MeasureSpec.UNSPECIFIED) {
            ((Activity) this.context).getWindowManager().getDefaultDisplay().getMetrics(this.displayMetrics);
            availableWidth = this.displayMetrics.widthPixels;
        } else if (widthMode != View.MeasureSpec.EXACTLY) {
            availableWidth = 0;
        }
        if (this.maxWidth > 0) {
            availableWidth = Math.min(availableWidth, this.maxWidth);
        }
        setTextColor(getTextColors().getDefaultColor());
        this.paint.setTextSize(getTextSize());
        this.paint.setColor(this.textColor);
        int contentHeight = measureContentHeight(availableWidth);
        int measuredWidth = Math.min(availableWidth,
                ((int) this.lineWidthMax) + getCompoundPaddingLeft() + getCompoundPaddingRight());
        if (this.oneLineWidth > -1) {
            measuredWidth = this.oneLineWidth;
        }
        if (heightMode == View.MeasureSpec.AT_MOST || heightMode == View.MeasureSpec.UNSPECIFIED) {
            availableHeight = contentHeight;
        } else if (heightMode != View.MeasureSpec.EXACTLY) {
            availableHeight = 0;
        }
        setMeasuredDimension(measuredWidth, Math.max(availableHeight + getCompoundPaddingTop()
                + getCompoundPaddingBottom(), this.minHeight));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.useDefault) {
            super.onDraw(canvas);
            return;
        }
        if (this.contentList.isEmpty()) {
            return;
        }
        int paddingLeft = getCompoundPaddingLeft();
        float lineTop = getCompoundPaddingTop() + this.lineSpacing;
        if (this.oneLineWidth != -1) {
            lineTop = (getMeasuredHeight() / 2.0f) - (this.contentList.get(0).height / 2.0f);
        }
        Iterator<Line> lineIterator = this.contentList.iterator();
        while (lineIterator.hasNext()) {
            Line line = lineIterator.next();
            float cursorX = paddingLeft;
            boolean lineEndsWithParagraphBreak = false;
            for (int index = 0; index < line.line.size(); index++) {
                Object item = line.line.get(index);
                int itemWidth = line.widthList.get(index).intValue();
                this.paint.getFontMetrics(this.mFontMetrics);
                float baseline = (line.height + lineTop) - this.paint.getFontMetrics().descent;
                float spanTop = baseline - line.height;
                float spanBottom = this.mFontMetrics.descent + baseline;
                if (item instanceof String) {
                    String textPart = (String) item;
                    canvas.drawText(textPart, cursorX, baseline, this.paint);
                    cursorX += itemWidth;
                    if (textPart.endsWith("\n") && index == line.line.size() - 1) {
                        lineEndsWithParagraphBreak = true;
                    }
                } else if (item instanceof SpanObject) {
                    SpanObject spanObject = (SpanObject) item;
                    Object span = spanObject.span;
                    if (span instanceof DynamicDrawableSpan) {
                        ((DynamicDrawableSpan) span).draw(canvas, this.text,
                                ((Spannable) this.text).getSpanStart(span),
                                ((Spannable) this.text).getSpanEnd(span),
                                (int) cursorX, (int) spanTop, (int) baseline, (int) spanBottom, this.paint);
                    } else if (span instanceof BackgroundColorSpan) {
                        this.textBgColorPaint.setColor(((BackgroundColorSpan) span).getBackgroundColor());
                        this.textBgColorPaint.setStyle(Paint.Style.FILL);
                        this.textBgColorRect.left = (int) cursorX;
                        this.textBgColorRect.top = (int) ((line.height + lineTop)
                                - ((int) getTextSize()) - this.mFontMetrics.descent);
                        this.textBgColorRect.right = this.textBgColorRect.left + itemWidth;
                        this.textBgColorRect.bottom = (int) ((line.height + lineTop + this.lineSpacing)
                                - this.mFontMetrics.descent);
                        canvas.drawRect(this.textBgColorRect, this.textBgColorPaint);
                        canvas.drawText(spanObject.source.toString(), cursorX,
                                (line.height + lineTop) - this.mFontMetrics.descent, this.paint);
                    } else {
                        canvas.drawText(spanObject.source.toString(), cursorX,
                                (line.height + lineTop) - this.mFontMetrics.descent, this.paint);
                    }
                    cursorX += itemWidth;
                }
            }
            lineTop += line.height + (lineEndsWithParagraphBreak ? this.paragraphSpacing : this.lineSpacing);
        }
    }
    /**
     * 按字符测量并构造行结构，返回内容总高度。
     *
     * @param availableWidth 去掉左右 padding 后的可用宽度
     */
    private int measureContentHeight(int availableWidth) {
        int cachedHeight = getCachedData(this.text.toString(), availableWidth);
        if (cachedHeight > 0) {
            return cachedHeight;
        }
        float textSize = getTextSize();
        Paint.FontMetrics fontMetrics = this.paint.getFontMetrics();
        float lineHeight = fontMetrics.bottom - fontMetrics.top;
        int paddingLeft = getCompoundPaddingLeft();
        int paddingRight = getCompoundPaddingRight();
        int contentWidth = availableWidth - paddingLeft - paddingRight;
        this.oneLineWidth = -1;
        this.contentList.clear();
        Line currentLine = new Line();
        float totalHeight = this.lineSpacing;
        float currentLineHeight = lineHeight;
        int index = 0;
        float lineWidth = 0.0f;
        boolean lineBreakByBackgroundSpan = false;
        while (index < this.obList.size()) {
            Object item = this.obList.get(index);
            float measuredWidth;
            float itemHeight = textSize;
            if (item instanceof String) {
                measuredWidth = this.paint.measureText((String) item);
                if ("\n".equals(item)) {
                    measuredWidth = contentWidth - lineWidth;
                }
            } else if (item instanceof SpanObject) {
                SpanObject spanObject = (SpanObject) item;
                Object span = spanObject.span;
                if (span instanceof DynamicDrawableSpan) {
                    measuredWidth = ((DynamicDrawableSpan) span).getSize(getPaint(), this.text,
                            ((Spannable) this.text).getSpanStart(span),
                            ((Spannable) this.text).getSpanEnd(span), this.mSpanFmInt);
                    float spanHeight = Math.abs(this.mSpanFmInt.top) + Math.abs(this.mSpanFmInt.bottom);
                    if (spanHeight > currentLineHeight) {
                        currentLineHeight = spanHeight;
                    }
                    itemHeight = spanHeight;
                } else if (span instanceof BackgroundColorSpan) {
                    String spanText = spanObject.source.toString();
                    measuredWidth = this.paint.measureText(spanText);
                    int splitIndex = spanText.length() - 1;
                    while (contentWidth - lineWidth < measuredWidth) {
                        measuredWidth = this.paint.measureText(spanText.substring(0, splitIndex));
                        splitIndex--;
                    }
                    if (splitIndex < spanText.length() - 1) {
                        SpanObject head = new SpanObject();
                        head.start = spanObject.start;
                        head.end = head.start + splitIndex;
                        head.source = spanText.substring(0, splitIndex + 1);
                        head.span = spanObject.span;
                        SpanObject tail = new SpanObject();
                        tail.start = head.end;
                        tail.end = spanObject.end;
                        tail.source = spanText.substring(splitIndex + 1, spanText.length());
                        tail.span = spanObject.span;
                        this.obList.set(index, tail);
                        index--;
                        item = head;
                        lineBreakByBackgroundSpan = true;
                    }
                } else {
                    measuredWidth = this.paint.measureText(spanObject.source.toString());
                }
            } else {
                measuredWidth = 0.0f;
            }
            if (contentWidth - lineWidth < measuredWidth || lineBreakByBackgroundSpan) {
                this.contentList.add(currentLine);
                if (lineWidth > this.lineWidthMax) {
                    this.lineWidthMax = lineWidth;
                }
                int lastItemIndex = currentLine.line.size() - 1;
                float spacing;
                if (this.paragraphSpacing > 0 && lastItemIndex >= 0
                        && currentLine.line.get(lastItemIndex) instanceof String
                        && "\n".equals(currentLine.line.get(lastItemIndex))) {
                    spacing = this.paragraphSpacing;
                } else {
                    spacing = this.lineSpacing;
                }
                totalHeight += currentLine.height + spacing;
                currentLine = new Line();
                currentLineHeight = itemHeight;
                lineWidth = 0.0f;
                lineBreakByBackgroundSpan = false;
            }
            lineWidth += measuredWidth;
            int previousIndex = currentLine.line.size() - 1;
            if (!(item instanceof String) && previousIndex >= 0
                    && currentLine.line.get(previousIndex) instanceof String) {
                String merged = currentLine.line.get(previousIndex) + String.valueOf(item);
                float mergedWidth = measuredWidth + currentLine.widthList.get(previousIndex).intValue();
                currentLine.line.set(previousIndex, merged);
                currentLine.widthList.set(previousIndex, (int) mergedWidth);
                currentLine.height = (int) currentLineHeight;
            } else {
                currentLine.line.add(item);
                currentLine.widthList.add((int) measuredWidth);
                currentLine.height = (int) currentLineHeight;
            }
            index++;
        }
        if (lineWidth > this.lineWidthMax) {
            this.lineWidthMax = lineWidth;
        }
        if (currentLine.line.size() > 0) {
            this.contentList.add(currentLine);
            totalHeight += this.lineSpacing + currentLineHeight;
        }
        if (this.contentList.size() <= 1) {
            this.oneLineWidth = ((int) lineWidth) + paddingLeft + paddingRight;
            totalHeight = currentLineHeight + this.lineSpacing + this.lineSpacing;
        }
        int measuredHeight = (int) totalHeight;
        cacheData(contentWidth, measuredHeight);
        return measuredHeight;
    }

    /** 命中缓存时直接复用上一次的测量结果，未命中返回 -1。 */
    private int getCachedData(String text, int width) {
        SoftReference<MeasuredData> reference = measuredData.get(text);
        MeasuredData data = reference == null ? null : reference.get();
        if (data == null || data.textSize != getTextSize() || width != data.width) {
            return -1;
        }
        this.lineWidthMax = data.lineWidthMax;
        this.contentList = new ArrayList<>(data.contentList);
        this.oneLineWidth = data.oneLineWidth;
        return data.measuredHeight;
    }

    private void cacheData(int width, int measuredHeight) {
        MeasuredData data = new MeasuredData();
        data.contentList = new ArrayList<>(this.contentList);
        data.textSize = getTextSize();
        data.lineWidthMax = this.lineWidthMax;
        data.oneLineWidth = this.oneLineWidth;
        data.measuredHeight = measuredHeight;
        data.width = width;
        int currentHashIndex = hashIndex + 1;
        hashIndex = currentHashIndex;
        data.hashIndex = currentHashIndex;
        measuredData.put(this.text.toString(), new SoftReference<>(data));
    }

    /** 设置文本，并把文本拆成“单字符 / span”序列，供逐字符断行使用。 */
    public void setMText(CharSequence charSequence) {
        this.text = charSequence;
        this.obList.clear();
        ArrayList<SpanObject> spanObjects = new ArrayList<>();
        this.useDefault = false;
        if (charSequence instanceof Spannable) {
            Spannable spannable = (Spannable) charSequence;
            CharacterStyle[] characterStyles = spannable.getSpans(0, charSequence.length(), CharacterStyle.class);
            for (CharacterStyle characterStyle : characterStyles) {
                int spanStart = spannable.getSpanStart(characterStyle);
                int spanEnd = spannable.getSpanEnd(characterStyle);
                SpanObject spanObject = new SpanObject();
                spanObject.span = characterStyle;
                spanObject.start = spanStart;
                spanObject.end = spanEnd;
                spanObject.source = charSequence.subSequence(spanStart, spanEnd);
                spanObjects.add(spanObject);
            }
        }
        SpanObject[] sortedSpans = spanObjects.toArray(new SpanObject[spanObjects.size()]);
        Arrays.sort(sortedSpans, 0, sortedSpans.length, new SpanObjectComparator());
        spanObjects.clear();
        for (SpanObject spanObject : sortedSpans) {
            spanObjects.add(spanObject);
        }
        String plainText = charSequence.toString();
        int spanIndex = 0;
        int charIndex = 0;
        while (charIndex < charSequence.length()) {
            if (spanIndex < spanObjects.size()) {
                SpanObject spanObject = spanObjects.get(spanIndex);
                if (charIndex < spanObject.start) {
                    int codePoint = plainText.codePointAt(charIndex);
                    charIndex += Character.isSupplementaryCodePoint(codePoint) ? 2 : 1;
                    this.obList.add(new String(Character.toChars(codePoint)));
                } else if (charIndex >= spanObject.start) {
                    this.obList.add(spanObject);
                    spanIndex++;
                    charIndex = spanObject.end;
                }
            } else {
                int codePoint = plainText.codePointAt(charIndex);
                charIndex += Character.isSupplementaryCodePoint(codePoint) ? 2 : 1;
                this.obList.add(new String(Character.toChars(codePoint)));
            }
        }
        requestLayout();
    }

    /** 切换回系统默认的换行与绘制逻辑。 */
    public void setUseDefault(boolean useDefault) {
        this.useDefault = useDefault;
        if (useDefault) {
            setText(this.text);
            setTextColor(this.textColor);
        }
    }

    public void setLineSpacingDP(int lineSpacingDP) {
        this.lineSpacingDP = lineSpacingDP;
        this.lineSpacing = dip2px(this.context, lineSpacingDP);
    }

    public void setParagraphSpacingDP(int paragraphSpacingDP) {
        this.paragraphSpacing = dip2px(this.context, paragraphSpacingDP);
    }

    public int getLineSpacingDP() {
        return this.lineSpacingDP;
    }

    /** obList 中的 span 片段。 */
    class SpanObject {
        public int end;
        public CharSequence source;
        public Object span;
        public int start;
    }

    /** 按 span 起始位置排序。 */
    class SpanObjectComparator implements Comparator<SpanObject> {
        @Override
        public int compare(SpanObject first, SpanObject second) {
            return first.start - second.start;
        }
    }

    /** 一行内容：按顺序保存的片段及其宽度。 */
    class Line {
        public float height;
        public ArrayList<Object> line = new ArrayList<>();
        public ArrayList<Integer> widthList = new ArrayList<>();

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder("height:" + this.height + "   ");
            for (int index = 0; index < this.line.size(); index++) {
                builder.append(this.line.get(index)).append(":").append(this.widthList.get(index));
            }
            return builder.toString();
        }
    }

    /** 一次测量的缓存数据。 */
    class MeasuredData {
        ArrayList<Line> contentList;
        public int hashIndex;
        public float lineWidthMax;
        public int measuredHeight;
        public int oneLineWidth;
        public float textSize;
        public int width;
    }
}