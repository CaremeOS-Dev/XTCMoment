package com.xtc.ui.widget.util;

import android.view.View;

/** 视图滚动方向判断工具。 */
public class ViewUtils {

    public static final int DIRECTION_END = 1;
    public static final int DIRECTION_START = -1;
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    public static boolean isInAbsoluteStart(View view, int orientation) {
        if (orientation == HORIZONTAL) {
            return !view.canScrollHorizontally(DIRECTION_START);
        }
        return !view.canScrollVertically(DIRECTION_START);
    }

    public static boolean isInAbsoluteEnd(View view, int orientation) {
        if (orientation == HORIZONTAL) {
            return !view.canScrollHorizontally(DIRECTION_END);
        }
        return !view.canScrollVertically(DIRECTION_END);
    }
}