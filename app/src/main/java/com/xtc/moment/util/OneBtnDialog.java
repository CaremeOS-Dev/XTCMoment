package com.xtc.moment.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xtc.moment.R;

/**
 * 单按钮提示弹窗。
 */
public class OneBtnDialog extends Dialog {

    private View root;
    private TextView tvHint;

    public OneBtnDialog(Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_moment_one_btn);
        this.tvHint = (TextView) findViewById(R.id.tv_grade_dialog_one_btn_hint);
        this.root = findViewById(R.id.rl_dialog_one_btn);
        ((Button) findViewById(R.id.btn_grade_dialog_one_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OneBtnDialog.this.dismiss();
            }
        });
    }

    public void setTopText(CharSequence text) {
        this.tvHint.setText(text);
    }

    public void setTopTextColor(int color) {
        this.tvHint.setTextColor(color);
    }

    @Override
    public void setBackground(Drawable background) {
        View contentRoot = this.root;
        if (contentRoot != null) {
            contentRoot.setBackground(background);
        }
    }
}