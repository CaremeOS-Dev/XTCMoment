package com.xtc.moment.module.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.ui.widget.button.LongSolidButton;

/**
 * Full screen agreement dialog shown before publishing: a scrollable content area and a single
 * confirmation button.
 */
public class PublishAgreementDialog extends Dialog {

    private static final String TAG = "InviteDialog";

    private TextView tvTitle;
    private TextView tvContent;
    private LongSolidButton button;
    private IDialogOnClickListener onClickListener;

    /** Callbacks for the agreement dialog buttons. */
    public interface IDialogOnClickListener {
        void onCancelClick();

        void onSureClick();
    }

    public PublishAgreementDialog(Context context) {
        super(context, R.style.dialog_default);
        setContentView(R.layout.layout_double_flat_button_with_title_dialog_1);
        initView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initView() {
        this.tvTitle = (TextView) findViewById(R.id.tv_title_normal_dialog_with_title);
        this.tvContent = (TextView) findViewById(R.id.tv_content_normal_dialog_with_title);
        this.tvContent.setMovementMethod(new ScrollingMovementMethod());
        this.button = (LongSolidButton) findViewById(R.id.lsb_solid_button);
        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PublishAgreementDialog.this.onClickListener.onSureClick();
            }
        });
    }

    public void setButtonText(String text) {
        this.button.getTv().setText(text);
    }

    public void setTvTitle(String title) {
        this.tvTitle.setText(title);
    }

    public void setTvContent(String content) {
        this.tvContent.setText(content);
    }

    public String getTvContent() {
        return this.tvTitle.getText().toString();
    }

    public void setIvBackground(int resId) {
        if (ContextCompat.getDrawable(getContext(), resId) != null) {
            return;
        }
        LogUtil.w(TAG, "setIvBackground: contentIvId is error. contentIvId is " + resId);
    }

    @Override
    public void show() {
        super.show();
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
            attributes.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setAttributes(attributes);
        }
    }

    @Override
    public void dismiss() {
        LogUtil.i(TAG, "dismiss: InviteDialog dismiss");
        super.dismiss();
    }

    public void setOnClickListener(IDialogOnClickListener listener) {
        this.onClickListener = listener;
    }
}