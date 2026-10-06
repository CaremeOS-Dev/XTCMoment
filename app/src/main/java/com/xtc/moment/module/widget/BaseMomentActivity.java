package com.xtc.moment.module.widget;

import android.view.MotionEvent;

import com.xtc.architecture.mvp.core.MvpActivity;
import com.xtc.architecture.mvp.core.MvpPresenter;
import com.xtc.architecture.mvp.core.MvpView;
import com.xtc.log.LogUtil;

import java.lang.reflect.Field;

/**
 * 动态模块 Activity 基类，补充异常信息与返回键兜底。
 */
public abstract class BaseMomentActivity<V extends MvpView, P extends MvpPresenter<V>> extends MvpActivity<V, P> {

    private static final String TAG = "BaseMomentActivity";

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        try {
            return super.dispatchTouchEvent(event);
        } catch (RuntimeException e) {
            replaceMessageOfThrowable(e, this + " crash msg: " + e.getMessage());
            throw e;
        }
    }

    private static void replaceMessageOfThrowable(Throwable throwable, String message) {
        try {
            Field field = Class.forName("java.lang.Throwable").getDeclaredField("detailMessage");
            field.setAccessible(true);
            field.set(throwable, message);
        } catch (ClassNotFoundException e) {
            LogUtil.e(TAG, "ClassNotFoundException", e);
        } catch (IllegalAccessException e) {
            LogUtil.e(TAG, "ClassNotFoundException", e);
        } catch (NoSuchFieldException e) {
            LogUtil.e(TAG, "ClassNotFoundException", e);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
        } catch (IllegalStateException e) {
            if (!"Can not perform this action after onSaveInstanceState".equals(e.getMessage())) {
                throw e;
            }
            finish();
        }
    }
}