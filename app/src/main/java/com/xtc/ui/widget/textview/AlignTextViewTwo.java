package com.xtc.ui.widget.textview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;
import com.xtc.moment.R;

/** 简易两端对齐文本：按宽度逐字换行绘制，支持自定义文字大小与颜色。 */
public class AlignTextViewTwo extends TextView {
    private float marginLeft;
    private float marginRight;
    private final String namespace;
    private float paddingLeft;
    private float paddingRight;
    private Paint paint1;
    private String text;
    private int textColor;
    private float textShowWidth;
    private float textSize;

    public AlignTextViewTwo(Context context) {
        this(context, null);
    }

    public AlignTextViewTwo(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlignTextViewTwo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.namespace = "http://www.xtc.com/";
        this.paint1 = new Paint();
        setTextIsSelectable(false);
        this.text = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "text");
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.AlignTextViewTwo);
        this.textShowWidth = attributes.getInt(R.styleable.AlignTextViewTwo_tv_maxWidth, 320);
        this.textSize = attributes.getInt(R.styleable.AlignTextViewTwo_tv_textSize, 33);
        this.textColor = attributes.getInt(R.styleable.AlignTextViewTwo_tv_textColor, -1);
        attributes.recycle();
        this.paint1.setTextSize(this.textSize);
        this.paint1.setColor(this.textColor);
        this.paint1.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.text = getText().toString();
        if (TextUtils.isEmpty(this.text)) {
            return;
        }
        this.text = this.text.replaceAll("\\p{So}+", "☒");
        char[] chars = this.text.toCharArray();
        int currentLine = 0;
        float currentX = 0.0f;
        for (int index = 0; index < chars.length; index++) {
            float charWidth = this.paint1.measureText(chars, index, 1);
            if (chars[index] == '\n') {
                currentLine++;
                currentX = 0.0f;
            } else {
                int drawLine;
                float drawX;
                if (this.textShowWidth - currentX < charWidth) {
                    drawLine = currentLine + 1;
                    drawX = 0.0f;
                } else {
                    drawLine = currentLine;
                    drawX = currentX;
                }
                canvas.drawText(chars, index, 1, drawX, (drawLine + 1) * this.textSize, this.paint1);
                currentX = drawX + charWidth;
                currentLine = drawLine;
            }
        }
        int totalLines = currentLine + 1;
        canvas.drawText("", 0.0f, (totalLines * this.textSize) + 5.0f, this.paint1);
        setHeight((totalLines * ((int) this.textSize)) + 10);
    }
}