package com.xtc.architecture.mvp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.xtc.architecture.mvp.util.PermissionUtil;
import com.xtc.ui.widget.dialog.bean.noIcon.CtaPermissionBean;
import com.xtc.ui.widget.permission.cta.CtaPermissionView;
import com.xtc.ui.widget.permission.cta.interfaces.ICtaPermissionCallback;
import com.xtc.ui.widget.util.CtaPermissionUtil;

/** Hosts the CTA permission view and forwards the user's answer. */
public class PermissionListActivity extends Activity {

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());
    }

    private void handleIntent(final Intent intent) {
        String tip = intent.getStringExtra(PermissionUtil.EXTRA_TIP);
        String ctaTitle = intent.getStringExtra(PermissionUtil.EXTRA_CTA_TITLE);
        if (TextUtils.isEmpty(tip)) {
            tip = "";
        }
        if (TextUtils.isEmpty(ctaTitle)) {
            try {
                ctaTitle = getString(getPackageManager().getPackageInfo(getPackageName(), 0).applicationInfo.labelRes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        CtaPermissionView ctaPermissionView = new CtaPermissionView(this);
        ctaPermissionView.initData(new CtaPermissionBean(CtaPermissionUtil.getAuthorityInfos(this, getPackageName()), ctaTitle, tip));
        ctaPermissionView.setCallBack(new ICtaPermissionCallback() {
            @Override
            public void allow() {
                Intent original = (Intent) intent.getParcelableExtra(PermissionUtil.EXTRA_INTENT);
                original.setExtrasClassLoader(CtaPermissionView.class.getClassLoader());
                original.putExtra(PermissionUtil.EXTRA_CTA_PERMISSION_ALLOWED, true);
                PermissionListActivity.this.startActivity(original);
                PermissionListActivity.this.finish();
            }

            @Override
            public void refuse() {
                PermissionListActivity.this.finish();
            }
        });
        setContentView(ctaPermissionView);
    }
}
