package com.xtc.moment.module.illegal.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.xtc.moment.R;

/**
 * 「封禁通知」弹窗。
 *
 * <p>服务端对封禁账号的每个请求都返回 {@code 000007}（见 {@code PublishErrorUtil}），
 * 登录接口同样如此，因此客户端只要拿到这个错误码就弹本弹窗。</p>
 *
 * <p>文案与按钮和线上一致：标题、正文说明，外加「申请复核」「下载我发布过的动态」
 * 和「我知道了」三个按钮。申请复核会回调
 * {@link BannedDialogListener#onAppealClick()}，由调用方发
 * {@code /extra/review-request}。</p>
 */
public class AccountBannedDialog extends Dialog {

    /** 三个按钮的点击回调，调用方按需实现。 */
    public interface BannedDialogListener {
        void onAppealClick();

        void onDownloadClick();

        void onConfirmClick();
    }

    private BannedDialogListener listener;

    public AccountBannedDialog(Context context) {
        this(context, true);
    }

    public AccountBannedDialog(Context context, boolean forbidSwipeToDismiss) {
        super(context, forbidSwipeToDismiss
                ? R.style.dialog_default_style_forbidSwipe : R.style.dialog_default_style);
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_account_banned);
        setCanceledOnTouchOutside(false);
        Button appeal = (Button) findViewById(R.id.btn_ban_appeal);
        Button download = (Button) findViewById(R.id.btn_ban_download);
        Button confirm = (Button) findViewById(R.id.btn_ban_confirm);
        appeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onAppealClick();
                }
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onDownloadClick();
                }
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onConfirmClick();
                }
                dismiss();
            }
        });
    }

    public void setBannedDialogListener(BannedDialogListener listener) {
        this.listener = listener;
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
