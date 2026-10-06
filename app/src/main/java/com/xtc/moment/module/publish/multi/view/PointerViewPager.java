package com.xtc.moment.module.publish.multi.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.xtc.log.LogUtil;

/**
 * View pager that swallows the rare {@link IllegalArgumentException} thrown by the framework while
 * the adapter is being swapped during a touch gesture.
 */
public class PointerViewPager extends ViewPager {

    private static final String TAG = "PointerViewPager";

    public PointerViewPager(Context context) {
        super(context);
    }

    public PointerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            return super.onTouchEvent(event);
        } catch (IllegalArgumentException e) {
            LogUtil.e(TAG, "onTouchEvent illegalArgumentException", e);
            return false;
        } catch (Exception e) {
            LogUtil.e(TAG, "onTouchEvent Exception", e);
            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        try {
            return super.onInterceptTouchEvent(event);
        } catch (IllegalArgumentException e) {
            LogUtil.e(TAG, "onInterceptTouchEvent illegalArgumentException", e);
            return false;
        } catch (Exception e) {
            LogUtil.e(TAG, "onInterceptTouchEvent Exception", e);
            return false;
        }
    }
}