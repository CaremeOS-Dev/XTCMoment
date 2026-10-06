package com.xtc.ui.widget.privacy.setting;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** 设置类页面的基类，统一处理内容布局与条目点击。 */
public abstract class AbstractSettingActivity extends Activity {

    /** 条目可见性标记。 */
    @Retention(RetentionPolicy.SOURCE)
    public @interface Visibility {
    }

    protected abstract int setContentViewId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setContentViewId());
    }

    protected final void setItemViewStateByViewId(int viewId, View.OnClickListener listener) {
        setItemViewStateByViewId(viewId, View.VISIBLE, listener);
    }

    protected final void setItemViewStateByViewId(int viewId, int visibility, View.OnClickListener listener) {
        View view = findViewById(viewId);
        if (view == null) {
            return;
        }
        view.setVisibility(visibility);
        view.setOnClickListener(listener);
    }
}