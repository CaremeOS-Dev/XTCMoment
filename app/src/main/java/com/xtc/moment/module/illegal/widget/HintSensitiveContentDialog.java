package com.xtc.moment.module.illegal.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.xtc.log.util.TextUtils;
import com.xtc.moment.R;

/**
 * 敏感内容提示弹窗。
 */
public class HintSensitiveContentDialog extends Dialog {

    private TextView tvTittle;
    private TextView tvHintContent;
    private Button btnConfirm;
    private HintClickListener hintClickListener;

    public interface HintClickListener {
        void onConfirmClick();
    }

    public HintSensitiveContentDialog(Context context) {
        super(context, R.style.dialog_default);
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_hint_one_button);
        this.tvHintContent = (TextView) findViewById(R.id.tv_hint_content);
        this.tvTittle = (TextView) findViewById(R.id.tv_hint_title);
        this.btnConfirm = (Button) findViewById(R.id.btn_i_know);
        this.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (HintSensitiveContentDialog.this.hintClickListener != null) {
                    HintSensitiveContentDialog.this.hintClickListener.onConfirmClick();
                }
                HintSensitiveContentDialog.this.dismiss();
            }
        });
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

    public void setDialogContent(String content, String title) {
        if (!TextUtils.isEmpty(title)) {
            this.tvTittle.setVisibility(View.VISIBLE);
            this.tvTittle.setText(title);
        }
        this.tvHintContent.setText(content);
    }

    public void setHintClickListener(HintClickListener hintClickListener) {
        this.hintClickListener = hintClickListener;
    }
}