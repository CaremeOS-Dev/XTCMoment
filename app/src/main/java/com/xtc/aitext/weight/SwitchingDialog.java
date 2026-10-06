package com.xtc.aitext.weight;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xtc.aitext.R;

/**
 * 切换/生成中的全屏加载弹窗。
 */
public class SwitchingDialog extends Dialog {

    private FrameLayout flChanging;
    private SwitchingProgressView spvProgress;
    private TextView tvChanging;

    public SwitchingDialog(Context context) {
        super(context, R.style.dialog_default_style);
        setContentView(R.layout.dialog_switching);
        initView();
    }

    public void initView() {
        this.flChanging = findViewById(R.id.fl_changing);
        this.spvProgress = findViewById(R.id.spv_changing);
        this.tvChanging = findViewById(R.id.tv_changing);
    }

    public void setLlChangingBackground(Drawable drawable) {
        this.flChanging.setBackground(drawable);
    }

    public TextView getTvChanging() {
        return tvChanging;
    }

    @Override
    public void show() {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.width = ViewGroup.LayoutParams.MATCH_PARENT;
            attributes.height = ViewGroup.LayoutParams.MATCH_PARENT;
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setAttributes(attributes);
        }
        this.spvProgress.start();
        super.show();
    }

    @Override
    public void dismiss() {
        this.spvProgress.stop();
        super.dismiss();
    }
}