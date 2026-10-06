package com.xtc.moment.util;

import android.content.Context;
import android.text.TextPaint;
import android.widget.TextView;

import com.xtc.log.LogUtil;

/**
 * 按文本测量宽度自适应调整字号。
 */
public class TextSizeUtil {

    private static final String TAG = "moment";

    private static final int NAME_MAX_WIDTH = 210;
    private static final int NAME_MIDDLE_WIDTH = 186;
    private static final int NAME_ORDINARY_WIDTH = 156;
    private static final int TEXT_SIZE_BIG = 14;
    private static final int TEXT_SIZE_MIDDLE = 12;
    private static final int TEXT_SIZE_SEVENTEEN = 15;
    private static final int TEXT_SIZE_SMALL = 10;

    public static void setUpdateText(Context context, TextView textView, String text) {
        if (context == null) {
            LogUtil.e(TAG, "context == null");
            return;
        }
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(context.getResources().getDisplayMetrics().scaledDensity * TEXT_SIZE_SEVENTEEN);
        float textWidth = textPaint.measureText(text);
        LogUtil.d(TAG, "测量文本的长度textPaintWidth：" + textWidth);
        if (textWidth > NAME_MAX_WIDTH) {
            textView.setTextSize(TEXT_SIZE_SMALL);
        } else if (textWidth > NAME_MIDDLE_WIDTH) {
            textView.setTextSize(TEXT_SIZE_MIDDLE);
        } else if (textWidth > NAME_ORDINARY_WIDTH) {
            textView.setTextSize(TEXT_SIZE_BIG);
        } else {
            textView.setTextSize(TEXT_SIZE_SEVENTEEN);
        }
        textView.setText(text);
    }
}