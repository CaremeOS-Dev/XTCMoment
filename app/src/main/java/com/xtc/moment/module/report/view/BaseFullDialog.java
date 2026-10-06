package com.xtc.moment.module.report.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

/**
 * 全屏 Dialog，show 时把窗口铺满屏幕并去掉内边距。
 */
public class BaseFullDialog extends Dialog {

    public BaseFullDialog(Context context) {
        super(context);
    }

    public BaseFullDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public void show() {
        super.show();
        Window window = getWindow();
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setAttributes(attributes);
    }
}