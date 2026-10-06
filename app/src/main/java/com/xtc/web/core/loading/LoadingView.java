package com.xtc.web.core.loading;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/** WebView 加载中遮罩视图基类，具体样式由使用方提供子类。 */
public abstract class LoadingView extends RelativeLayout {

    public abstract void startLoading();

    public abstract void stopLoading();

    public LoadingView(Context context) {
        super(context);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}