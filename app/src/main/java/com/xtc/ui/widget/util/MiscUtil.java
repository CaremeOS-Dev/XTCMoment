package com.xtc.ui.widget.util;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;

/** 尺寸测量与格式化杂项工具。 */
public class MiscUtil {

    public static int measure(int measureSpec, int defaultValue) {
        int mode = View.MeasureSpec.getMode(measureSpec);
        int size = View.MeasureSpec.getSize(measureSpec);
        if (mode == View.MeasureSpec.EXACTLY) {
            return size;
        }
        return mode == View.MeasureSpec.AT_MOST ? Math.min(defaultValue, size) : defaultValue;
    }

    public static int dipToPx(Context context, float dip) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) ((density * dip) + ((dip >= 0.0f ? 1 : -1) * 0.5f));
    }

    public static String getPrecisionFormat(int precision) {
        return "%." + precision + "f";
    }

    public static <T> T[] reverse(T[] array) {
        if (array == null) {
            return null;
        }
        int length = array.length;
        for (int index = 0; index < length / 2; index++) {
            T temp = array[index];
            int swapIndex = (length - index) - 1;
            array[index] = array[swapIndex];
            array[swapIndex] = temp;
        }
        return array;
    }

    public static float measureTextHeight(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return Math.abs(fontMetrics.ascent) - fontMetrics.descent;
    }
}