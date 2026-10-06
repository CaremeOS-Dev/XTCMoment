package com.xtc.ui.widget.privacy;

import android.app.Dialog;
import android.content.Context;

import com.xtc.ui.widget.util.DialogUtil;

import java.util.Map;

/** Convenience wrapper that owns the privacy dialog for a screen. */
public class XTCPrivacy {

    private Dialog dialog;

    /** Callbacks for the privacy request. */
    public interface PrivacyResponse {
        void onAllow();

        void onHadAllowed();

        void onRefuse();

        boolean shouldRequest();
    }

    public void requestPrivacy(Context context, PrivacyResponse response) {
        requestPrivacy(context, context.getPackageName(), response);
    }

    public void requestPrivacy(Context context, String packageName, PrivacyResponse response) {
        requestPrivacy(context, packageName, "", response);
    }

    public void requestPrivacy(Context context, String packageName, String title, PrivacyResponse response) {
        requestPrivacy(context, packageName, title, (String) null, response);
    }

    public void requestPrivacy(Context context, String packageName, String title, String appName, PrivacyResponse response) {
        requestPrivacy(context, packageName, title, appName, (Map<String, String>) null, response);
    }

    public void requestPrivacy(Context context, String packageName, String title, String appName, Map<String, String> reasonMap, final PrivacyResponse response) {
        if (response == null) {
            return;
        }
        if (!response.shouldRequest()) {
            response.onHadAllowed();
            return;
        }
        Dialog oldDialog = this.dialog;
        if (oldDialog != null) {
            if (oldDialog.isShowing()) {
                try {
                    this.dialog.dismiss();
                } catch (Exception ignored) {
                }
            }
            this.dialog = null;
        }
        this.dialog = DialogUtil.makePrivacyDialog(context, packageName, title, appName, reasonMap, new IPrivacyDialogListener() {
            @Override
            public void allow() {
                response.onAllow();
            }

            @Override
            public void refuse() {
                response.onRefuse();
            }
        });
        this.dialog.show();
    }

    public void onDestroy() {
        Dialog dialog = this.dialog;
        if (dialog == null || !dialog.isShowing()) {
            return;
        }
        try {
            this.dialog.dismiss();
        } catch (Exception ignored) {
        }
    }
}
