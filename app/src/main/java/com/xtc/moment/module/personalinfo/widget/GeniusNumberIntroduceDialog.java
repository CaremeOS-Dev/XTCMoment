package com.xtc.moment.module.personalinfo.widget;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;

public class GeniusNumberIntroduceDialog extends Dialog {

    private static final String TAG = "GeniusNumberIntroduceDialog";

    private final Context mContext;
    private String geniusNumber;
    private TextView tvGeniusNumber;

    public GeniusNumberIntroduceDialog(Context context, String geniusNumber) {
        super(context, R.style.dialog_default_style);
        super.setContentView(R.layout.dialog_genius_number_introduce);
        mContext = context;
        this.geniusNumber = geniusNumber;
        initView();
    }

    private void initView() {
        tvGeniusNumber = (TextView) findViewById(R.id.tv_genius_number);
        showGeniusNumber();
    }

    private void showGeniusNumber() {
        String text;
        if (TextUtils.isEmpty(geniusNumber)) {
            text = mContext.getString(R.string.genius_number, mContext.getString(R.string.not_set));
        } else {
            text = mContext.getString(R.string.genius_number, geniusNumber);
        }
        tvGeniusNumber.setText(text);
    }

    public void setGeniusNumber(String geniusNumber) {
        this.geniusNumber = geniusNumber;
        showGeniusNumber();
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
    }
}