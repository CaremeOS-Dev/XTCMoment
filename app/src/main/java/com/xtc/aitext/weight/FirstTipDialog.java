package com.xtc.aitext.weight;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.xtc.moment.R;

/**
 * 首次使用引导弹窗，分两步展示使用说明。
 */
public class FirstTipDialog extends Dialog {

    private static final String TAG = "ai_text_FirstTipDialog";

    private Context mContext;
    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvButton;
    private ClickCallback clickCallback;
    private boolean isFirst;
    private boolean isSecond;

    /** 按钮点击回调。 */
    public interface ClickCallback {
        void clickButton();
    }

    public FirstTipDialog(Context context) {
        this(context, R.style.dialog_default_style);
    }

    public FirstTipDialog(Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.dialog_first_tip);
        this.mContext = context.getApplicationContext();
        this.isFirst = true;
        initView();
        initData();
    }

    private void initView() {
        this.tvTitle = findViewById(R.id.tv_title);
        this.tvContent = findViewById(R.id.tv_content);
        this.tvButton = findViewById(R.id.tv_button);
        this.tvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initData();
            }
        });
    }

    private void initData() {
        if (this.isFirst) {
            this.isFirst = false;
            this.isSecond = true;
            this.tvTitle.setText(this.mContext.getString(R.string.string_use_tip_first));
            this.tvContent.setText(this.mContext.getString(R.string.string_use_tip_second));
            this.tvButton.setText(this.mContext.getString(R.string.string_use_next));
            return;
        }
        if (this.isSecond) {
            this.isSecond = false;
            this.tvTitle.setTextSize(14.0f);
            this.tvTitle.setText(this.mContext.getString(R.string.string_use_tip_third));
            this.tvContent.setText(this.mContext.getString(R.string.string_use_tip_fourth));
            this.tvContent.setTextSize(14.0f);
            this.tvButton.setText(this.mContext.getString(R.string.string_use_try));
            return;
        }
        dismiss();
        ClickCallback callback = this.clickCallback;
        if (callback != null) {
            callback.clickButton();
        }
    }

    @Override
    public void show() {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.width = ViewGroup.LayoutParams.MATCH_PARENT;
            attributes.height = ViewGroup.LayoutParams.MATCH_PARENT;
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setAttributes(attributes);
            window.setBackgroundDrawable(null);
        }
        super.show();
    }

    public void setClickCallback(ClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }
}