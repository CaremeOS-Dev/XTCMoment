package com.xtc.moment.module.illegal.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtc.moment.R;

/**
 * 违规提示弹窗：展示违规说明，可带申诉入口，点击申诉打开横幅详情。
 */
public class HintIllegalContentDialog extends Dialog {

    Context context;
    long endTime;

    private ImageView ivHint;
    private TextView tvTittle;
    private TextView tvHintContent;
    private TextView tvAppeal;
    private Button btnConfirm;
    private HintClickListener hintClickListener;

    public interface HintClickListener {
        void onConfirmClick();

        void onAppealClick();
    }

    public HintIllegalContentDialog(Context context) {
        super(context, R.style.dialog_default);
        this.context = context;
        this.endTime = this.endTime;
        initView();
    }

    public HintIllegalContentDialog(Context context, long endTime) {
        super(context, R.style.dialog_default);
        this.context = context;
        this.endTime = endTime;
        initView();
    }

    public HintIllegalContentDialog(Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_hint_one_button);
        this.ivHint = (ImageView) findViewById(R.id.iv_hint);
        this.tvHintContent = (TextView) findViewById(R.id.tv_hint_content);
        this.tvTittle = (TextView) findViewById(R.id.tv_hint_title);
        this.btnConfirm = (Button) findViewById(R.id.btn_i_know);
        this.tvAppeal = (TextView) findViewById(R.id.tv_appeal);
        this.tvAppeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (HintIllegalContentDialog.this.hintClickListener != null) {
                    HintIllegalContentDialog.this.hintClickListener.onAppealClick();
                }
                BannerDetailDialog detailDialog = new BannerDetailDialog(
                        HintIllegalContentDialog.this.context, HintIllegalContentDialog.this.endTime);
                detailDialog.setHintClickListener(HintIllegalContentDialog.this.hintClickListener);
                detailDialog.show();
                HintIllegalContentDialog.this.dismiss();
            }
        });
        this.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (HintIllegalContentDialog.this.hintClickListener != null) {
                    HintIllegalContentDialog.this.hintClickListener.onConfirmClick();
                }
                HintIllegalContentDialog.this.dismiss();
            }
        });
    }

    public void setContent(Drawable drawable, String title, String content, boolean showAppeal) {
        if (drawable != null) {
            this.ivHint.setImageDrawable(drawable);
            this.ivHint.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(title)) {
            this.tvTittle.setText(title);
            this.tvTittle.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(content)) {
            this.tvHintContent.setText(content);
        }
        if (showAppeal) {
            this.tvAppeal.setVisibility(View.VISIBLE);
        }
    }

    public void setHintClickListener(HintClickListener hintClickListener) {
        this.hintClickListener = hintClickListener;
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