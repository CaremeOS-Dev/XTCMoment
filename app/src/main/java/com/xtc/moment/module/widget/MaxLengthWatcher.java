package com.xtc.moment.module.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.xtc.moment.util.ToastUtil;

/**
 * 输入长度超限提示监听。
 */
public class MaxLengthWatcher implements TextWatcher {

    private static final String TAG = "MaxLengthWatcher";

    private int maxLen = 0;
    private Context mContext = null;
    private String maxLenHint = "";
    private long lastToastTime = 0;

    public MaxLengthWatcher(Context context, int maxLen, String maxLenHint) {
        this.mContext = context;
        this.maxLen = maxLen;
        this.maxLenHint = maxLenHint;
    }

    public void setMaxLen(int maxLen) {
        this.maxLen = maxLen;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.length() < this.maxLen
                || System.currentTimeMillis() - this.lastToastTime <= 1000
                || TextUtils.isEmpty(this.maxLenHint)) {
            return;
        }
        ToastUtil.showShortCover(this.mContext.getApplicationContext(), this.maxLenHint);
    }
}