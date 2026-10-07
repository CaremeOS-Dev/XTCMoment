package com.xtc.aitext.weight;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.xtc.moment.R;
import com.xtc.ui.widget.button.DoubleFlatButton;

/**
 * AI 文案权限同意弹窗。
 */
public class PermissionDialog extends Dialog {

    private final Context context;
    private PermissionCallback permissionCallback;

    /** 同意/拒绝回调。 */
    public interface PermissionCallback {
        void clickAgree();

        void clickNoAgree();
    }

    public PermissionDialog(Context context) {
        super(context, R.style.dialog_default_style);
        this.context = context;
        setContentView(R.layout.dialog_permission);
        initView();
    }

    private void initView() {
        DoubleFlatButton doubleFlatButton = findViewById(R.id.dfb_agree);
        doubleFlatButton.getRightButton().setText(this.context.getString(R.string.string_agree));
        doubleFlatButton.getLeftButton().setText(this.context.getString(R.string.string_no_agree));
        doubleFlatButton.setLeftBgColorIdArray(new int[]{R.color.color_555555, R.color.color_444444});
        doubleFlatButton.setRightBgColorIdArray(new int[]{R.color.color_5988FF, R.color.color_2873FF});
        doubleFlatButton.getLeftButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionCallback callback = permissionCallback;
                if (callback != null) {
                    callback.clickNoAgree();
                }
            }
        });
        doubleFlatButton.getRightButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionCallback callback = permissionCallback;
                if (callback != null) {
                    callback.clickAgree();
                }
            }
        });
    }

    public void setPermissionCallback(PermissionCallback permissionCallback) {
        this.permissionCallback = permissionCallback;
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
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}