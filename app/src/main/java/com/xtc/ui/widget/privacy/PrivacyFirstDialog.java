package com.xtc.ui.widget.privacy;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtc.log.LogUtil;
import com.xtc.log.util.TextUtils;
import com.xtc.moment.R;
import com.xtc.ui.widget.button.DoubleFlatButton;
import com.xtc.ui.widget.util.CtaPermissionUtil;

import java.text.MessageFormat;
import java.util.List;

/**
 * First-run privacy dialog: lists the permissions the app requests and links to
 * the privacy/agreement/disclaimer pages.
 */
public class PrivacyFirstDialog extends Dialog implements View.OnClickListener {

    private static final String TAG = "PrivacyFirstDialog";

    /** Minimum gap between accepted clicks, to debounce double taps. */
    private static final long CLICK_INTERVAL_MS = 500L;

    private Handler mHandler;
    private IPrivacyDialogListener mListener;
    private PrivacyBean mPrivacyBean;
    private TextView tvPrivacy;
    private TextView tvAgreement;
    private TextView tvDisclaimer;
    private long clickTime;

    public PrivacyFirstDialog(Context context) {
        super(context, R.style.dialog_default_style_forbidSwipe);
        this.mHandler = new Handler(Looper.getMainLooper());
        this.clickTime = 0L;
        setContentView(R.layout.dialog_privacy_first);
    }

    public void setListener(IPrivacyDialogListener listener) {
        this.mListener = listener;
    }

    public void init(PrivacyBean privacyBean) {
        this.mPrivacyBean = privacyBean;
        TextView title = (TextView) findViewById(R.id.tv_title);
        if (privacyBean.getFirstTitle().isEmpty()) {
            title.setText(MessageFormat.format(getContext().getString(R.string.privacy_title1), this.mPrivacyBean.getAppName()));
        } else {
            title.setText(privacyBean.getFirstTitle());
        }
        List<String> list3 = this.mPrivacyBean.getList3();
        if (list3 != null && !list3.isEmpty()) {
            ((TextView) findViewById(R.id.tv_title1)).setVisibility(View.VISIBLE);
            LinearLayout llList1 = (LinearLayout) findViewById(R.id.ll_list1);
            llList1.setVisibility(View.VISIBLE);
            fullList(getContext(), llList1, list3);
        }
        List<String> list2 = this.mPrivacyBean.getList2();
        if (list2 != null && !list2.isEmpty()) {
            ((TextView) findViewById(R.id.tv_title2)).setVisibility(View.VISIBLE);
            LinearLayout llList2 = (LinearLayout) findViewById(R.id.ll_list2);
            llList2.setVisibility(View.VISIBLE);
            fullList(getContext(), llList2, list2);
        }
        List<String> list1 = this.mPrivacyBean.getList1();
        if (list1 != null && !list1.isEmpty()) {
            ((TextView) findViewById(R.id.tv_title3)).setVisibility(View.VISIBLE);
            LinearLayout llList3 = (LinearLayout) findViewById(R.id.ll_list3);
            llList3.setVisibility(View.VISIBLE);
            fullList(getContext(), llList3, list1);
        }
        this.tvPrivacy = (TextView) findViewById(R.id.tv_privacy);
        this.tvPrivacy.setOnClickListener(this);
        if (!TextUtils.isEmpty(this.mPrivacyBean.getAgreementUrl())) {
            this.tvAgreement = (TextView) findViewById(R.id.tv_agreement);
            this.tvAgreement.setOnClickListener(this);
            this.tvAgreement.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(this.mPrivacyBean.getDisclaimerUrl())) {
            this.tvDisclaimer = (TextView) findViewById(R.id.tv_disclaimer);
            this.tvDisclaimer.setOnClickListener(this);
            this.tvDisclaimer.setVisibility(View.VISIBLE);
        }
        DoubleFlatButton operate = (DoubleFlatButton) findViewById(R.id.dfb_operate);
        operate.getLeftButton().setText(R.string.disagree);
        operate.getRightButton().setText(R.string.agree);
        operate.getLeftButton().setOnClickListener(this);
        operate.getRightArea().setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        long now = SystemClock.elapsedRealtime();
        if (now - this.clickTime < CLICK_INTERVAL_MS) {
            return;
        }
        this.clickTime = now;
        int id = view.getId();
        if (id == R.id.tv_privacy) {
            PrivacyUriActivity.start(getContext(), this.mPrivacyBean.getPrivacyUrl(), "", 1, this.mPrivacyBean.getPackageName());
            return;
        }
        if (id == R.id.tv_agreement) {
            PrivacyUriActivity.start(getContext(), this.mPrivacyBean.getAgreementUrl(), getContext().getString(R.string.privacy_agreement), 2, this.mPrivacyBean.getPackageName());
            return;
        }
        if (id == R.id.tv_disclaimer) {
            PrivacyUriActivity.start(getContext(), this.mPrivacyBean.getDisclaimerUrl(), getContext().getString(R.string.privacy_disclaimer), 0, this.mPrivacyBean.getPackageName());
            return;
        }
        if (id == R.id.btn_left) {
            this.mListener.refuse();
            this.mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    PrivacyFirstDialog.this.dismiss();
                }
            }, 100L);
        } else if (id == R.id.ll_right) {
            this.mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    PrivacyFirstDialog.this.dismiss();
                }
            }, 100L);
            try {
                CtaPermissionUtil.setPackageWhiteListByLauncher(getContext(), this.mPrivacyBean.getPackageName());
            } catch (Throwable ignored) {
            }
            this.mListener.allow();
        }
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
        super.dismiss();
        this.mHandler.removeCallbacksAndMessages(null);
    }

    public static void fullList(Context context, LinearLayout container, List<String> items) {
        for (String item : items) {
            View row = LayoutInflater.from(context).inflate(R.layout.item_privacy, (ViewGroup) null);
            ((TextView) row.findViewById(R.id.tv_desc)).setText(item);
            container.addView(row);
        }
    }
}