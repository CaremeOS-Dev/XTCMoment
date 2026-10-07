package com.xtc.ui.widget.selfstart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.view.View;
import com.xtc.moment.R;
import com.xtc.ui.widget.dialog.DoubleFlatBtnDialog;

/** 自启动权限引导页：展示对话框并跳转到系统自启动设置。 */
public class SelfStartActivity extends Activity {
    private String contentTextExtra;
    private DoubleFlatBtnDialog dialog;
    private String packageNameExtra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        initData();
        showOpenSelfStartPermissionDialog(this, this.packageNameExtra, this.contentTextExtra);
    }

    private void initData() {
        if (getIntent() == null) {
            finish();
        } else {
            this.contentTextExtra = getIntent().getStringExtra(SelfStartConstant.EXTRA_CONTENT_TEXT);
            this.packageNameExtra = getIntent().getStringExtra(SelfStartConstant.EXTRA_PACKAGE_NAME);
        }
    }

    private void showOpenSelfStartPermissionDialog(final Context context, final String packageName, String contentText) {
        this.dialog = new DoubleFlatBtnDialog(context, true);
        this.dialog.setTitleString(contentText);
        this.dialog.getTvTitle().setGravity(GravityCompat.START);
        this.dialog.setWholeBackgroud(R.color.color_ff000000);
        this.dialog.getBottomBtn().setVisibility(0);
        this.dialog.getBottomBtn().getLeftButton().setText(R.string.cancel);
        this.dialog.getBottomBtn().getLeftButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelfStartActivity.this.finish();
            }
        });
        this.dialog.getBottomBtn().getRightButton().setText(R.string.self_start_go_to_set);
        this.dialog.getBottomBtn().getRightButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(SelfStartConstant.ACTION_SETTINGS_SELF_START);
                intent.putExtra(SelfStartConstant.EXTRA_PACKAGE_NAME, packageName);
                context.startActivity(intent);
                SelfStartActivity.this.finish();
            }
        });
        this.dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DoubleFlatBtnDialog dialog = this.dialog;
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}