package com.xtc.xtcoco;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

/** Receives the coverage dump request broadcast. */
public class CodeCoverageReceiver extends BroadcastReceiver {

    public static final String TAG = "CodeCoverageReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) {
            return;
        }
        String packageName = intent.getStringExtra("package");
        if (!TextUtils.isEmpty(packageName) && packageName.equals(context.getPackageName())) {
            try {
                JacocoUtils.getInstance().generateEcFile(context);
            } catch (Throwable ignored) {
                // the coverage agent is optional
            }
        }
    }
}