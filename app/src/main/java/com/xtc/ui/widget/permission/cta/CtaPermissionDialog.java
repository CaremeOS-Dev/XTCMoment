package com.xtc.ui.widget.permission.cta;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.ui.widget.button.DoubleFlatButton;
import com.xtc.ui.widget.dialog.bean.noIcon.CtaPermissionBean;
import com.xtc.ui.widget.permission.cta.interfaces.ICtaPermissionCallback;
import com.xtc.ui.widget.util.CtaPermissionUtil;

/** Dialog wrapper around {@link CtaPermissionView}. */
public class CtaPermissionDialog extends Dialog {

    private static final String TAG = "CtaPermissionDialog";

    private ICtaPermissionCallback callback;
    private CtaPermissionView ctaPermissionView;
    private Context mContext;

    public CtaPermissionDialog(Context context, boolean forbidSwipeToDismiss) {
        this(context, forbidSwipeToDismiss ? R.style.dialog_default_style_forbidSwipe : R.style.dialog_default_style);
    }

    public CtaPermissionDialog(Context context, int themeResId) {
        super(context, themeResId);
        super.setContentView(R.layout.layout_cta_permission_dialog);
        this.mContext = context;
        initView();
        initListener();
    }

    private void initView() {
        this.ctaPermissionView = (CtaPermissionView) findViewById(R.id.view_cta);
    }

    public void initData(CtaPermissionBean bean) {
        this.ctaPermissionView.initData(bean);
    }

    private void initListener() {
        final DoubleFlatButton operate = ((CtaPermissionView) findViewById(R.id.view_cta)).bfbOperate;
        operate.getLeftButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                operate.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CtaPermissionDialog.this.dismiss();
                    }
                }, 100L);
                if (CtaPermissionDialog.this.callback != null) {
                    CtaPermissionDialog.this.callback.refuse();
                }
            }
        });
        operate.getRightButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                operate.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CtaPermissionDialog.this.dismiss();
                    }
                }, 100L);
                if (CtaPermissionDialog.this.callback != null) {
                    CtaPermissionDialog.this.callback.allow();
                }
                try {
                    CtaPermissionUtil.setPackageWhiteListByLauncher(CtaPermissionDialog.this.getContext());
                } catch (Throwable ignored) {
                }
            }
        });
    }

    public void setCallBack(ICtaPermissionCallback callback) {
        this.callback = callback;
    }

    @Override
    public void show() {
        super.show();
        Window window = getWindow();
        if (window == null) {
            LogUtil.e(TAG, "error , window = null !");
            return;
        }
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = -1;
        attributes.height = -1;
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setAttributes(attributes);
    }

    @Override
    public void dismiss() {
        LogUtil.i(TAG, "dismiss()");
        super.dismiss();
    }
}