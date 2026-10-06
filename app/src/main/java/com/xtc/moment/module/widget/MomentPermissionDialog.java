package com.xtc.moment.module.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.xtc.moment.R;
import com.xtc.ui.widget.button.DoubleFlatButton;

/**
 * 权限申请弹窗。
 */
public class MomentPermissionDialog extends Dialog {

    private final Context mContext;
    private final PermissionClickListener permissionClickListener;

    public interface PermissionClickListener {
        void onClickAgree();

        void onClickRefuse();
    }

    public MomentPermissionDialog(Context context, PermissionClickListener listener) {
        super(context, R.style.CommonDialogStyle);
        this.mContext = context;
        this.permissionClickListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_moment_permission);
        initDoubleFlatButton();
    }

    private void initDoubleFlatButton() {
        DoubleFlatButton doubleFlatButton = (DoubleFlatButton) findViewById(R.id.dfb_operate);
        doubleFlatButton.setRightBgColorIdArray(new int[]{R.color.color_0ab158, R.color.color_55dd7b});
        doubleFlatButton.getLeftButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MomentPermissionDialog.this.permissionClickListener != null) {
                    MomentPermissionDialog.this.permissionClickListener.onClickRefuse();
                }
                MomentPermissionDialog.this.dismiss();
            }
        });
        doubleFlatButton.getRightArea().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MomentPermissionDialog.this.permissionClickListener != null) {
                    MomentPermissionDialog.this.permissionClickListener.onClickAgree();
                }
                MomentPermissionDialog.this.dismiss();
            }
        });
        doubleFlatButton.getLeftButton().setText(R.string.string_permission_refuse);
        doubleFlatButton.getRightButton().setText(R.string.string_permission_agree);
    }
}