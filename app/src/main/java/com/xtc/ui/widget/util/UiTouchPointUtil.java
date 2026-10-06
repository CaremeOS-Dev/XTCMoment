package com.xtc.ui.widget.util;

import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;

/** 触摸点判定工具：判断某点是否落在指定 View 或扩展矩形区域内。 */
public class UiTouchPointUtil {
    private static final String TAG = "UiTouchPointUtil";

    public static boolean isTouchPointInView(View view, Point[] points) {
        if (view == null) {
            return false;
        }
        for (Point point : points) {
            if (isTouchPointInView(view, point)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTouchPointInView(View view, Point point) {
        if (view == null || view.getVisibility() != 0) {
            return false;
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int viewLeft = location[0];
        int viewTop = location[1];
        return point.x >= viewLeft && point.x <= view.getMeasuredWidth() + viewLeft
                && point.y >= viewTop && point.y <= view.getMeasuredHeight() + viewTop;
    }

    public static boolean isTouchPointInRect(Point point, View view, int leftOffset, int rightOffset,
                                             int topOffset, int bottomOffset) {
        int bottom;
        int top;
        if (view == null || view.getVisibility() != 0) {
            return false;
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0] - leftOffset;
        int right = location[0] + view.getMeasuredWidth() + rightOffset;
        if (topOffset == -1 || bottomOffset == -1) {
            int[] parentLocation = new int[2];
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.getLocationOnScreen(parentLocation);
            int parentTop = parentLocation[1];
            bottom = parent.getMeasuredHeight() + parentLocation[1];
            top = parentTop;
        } else {
            top = location[1] - topOffset;
            bottom = location[1] + view.getMeasuredHeight() + bottomOffset;
        }
        return point.x >= left && point.x <= right && point.y >= top && point.y <= bottom;
    }
}