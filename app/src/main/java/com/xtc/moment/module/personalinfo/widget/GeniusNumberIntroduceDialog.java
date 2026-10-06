package com.xtc.moment.module.personalinfo.widget;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;

/**
 * 天才号介绍弹窗，展示当前好友的天才号（未设置时显示"未设置"）。
 */
public class GeniusNumberIntroduceDialog extends Dialog {

    private static final String TAG = "GeniusNumberIntroduceDialog";

    private final Context mContext;
    private final TextView tvGeniusNumber;
    private String geniusNumber;

    public GeniusNumberIntroduceDialog(Context context, String geniusNumber) {
        super(context, R.style.dialog_default_style);
        super.setContentView(R.layout.dialog_genius_number_introduce);
        this.mContext = context;
        this.geniusNumber = geniusNumber;
        this.tvGeniusNumber = (TextView) findViewById(R.id.tv_genius_number);
        showGeniusNumber();
    }

    private void showGeniusNumber() {
        String text;
        if (TextUtils.isEmpty(this.geniusNumber)) {
            Context context = this.mContext;
            text = context.getString(R.string.genius_number, context.getString(R.string.not_set));
        } else {
            text = this.mContext.getString(R.string.genius_number, this.geniusNumber);
        }
        this.tvGeniusNumber.setText(text);
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
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setAttributes(attributes);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}