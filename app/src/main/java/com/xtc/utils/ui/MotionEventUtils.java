package com.xtc.utils.ui;

import android.view.MotionEvent;

import com.xtc.log.LogUtil;

/** Null/illegal-pointer safe wrappers around {@link MotionEvent} accessors. */
public class MotionEventUtils {

    public static final String TAG = "MotionEventUtils";

    /** {@link MotionEvent#getX(int)} returning 0 on an illegal pointer index. */
    public static float getX(MotionEvent event, int pointerIndex) {
        try {
            return event.getX(pointerIndex);
        } catch (IllegalArgumentException e) {
            LogUtil.e(TAG, e);
            return 0.0f;
        }
    }

    /** {@link MotionEvent#getY(int)} returning 0 on an illegal pointer index. */
    public static float getY(MotionEvent event, int pointerIndex) {
        try {
            return event.getY(pointerIndex);
        } catch (IllegalArgumentException e) {
            LogUtil.e(TAG, e);
            return 0.0f;
        }
    }

    /** {@link MotionEvent#getX()} returning 0 when the event has no coordinates. */
    public static float getX(MotionEvent event) {
        try {
            return event.getX();
        } catch (IllegalArgumentException e) {
            LogUtil.e(TAG, e);
            return 0.0f;
        }
    }

    /** {@link MotionEvent#getY()} returning 0 when the event has no coordinates. */
    public static float getY(MotionEvent event) {
        try {
            return event.getY();
        } catch (IllegalArgumentException e) {
            LogUtil.e(TAG, e);
            return 0.0f;
        }
    }
}