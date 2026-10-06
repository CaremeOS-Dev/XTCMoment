package com.xtc.aitext.weight;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xtc.aitext.R;
import com.xtc.aitext.behavior.AIBehaviorUtil;
import com.xtc.aitext.manager.AITextManager;
import com.xtc.aitext.weight.callback.AISuccessCallback;
import com.xtc.aitext.weight.callback.ActivityControllListener;
import com.xtc.log.LogUtil;
import com.xtc.ui.widget.button.DoubleFlatButton;
import com.xtc.ui.widget.util.DialogUtil;

/**
 * AI 文案生成成功弹窗。
 */
public class AISuccessDialog extends Dialog {

    private static final String TAG = "ai_text_FirstTipDialog";

    private final Context mContext;
    private View contentView;
    private TextView tvContent;
    private DoubleFlatButton dfbButton;
    private AISuccessCallback aiSuccessCallback;
    private boolean isInRecord;

    public AISuccessDialog(Context context) {
        this(context, R.style.dialog_default_style);
    }

    public AISuccessDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        initConfig();
        setContentView(this.contentView);
        initView();
    }

    private void initConfig() {
        this.contentView = AITextManager.getInstance(mContext).getConfig().getHostView();
        if (this.contentView == null) {
            this.contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_ai_success, null, false);
        }
        View view = this.contentView;
        if (view == null) {
            return;
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(this.contentView);
        }
        this.aiSuccessCallback = AITextManager.getInstance(mContext).getSuccessCallback();
    }

    private void initView() {
        this.tvContent = findViewById(R.id.tv_content);
        this.dfbButton = findViewById(R.id.dfb_button);
        this.dfbButton.setLeftBgColorIdArray(new int[]{R.color.color_555555, R.color.color_444444});
        this.dfbButton.setRightBgColorIdArray(new int[]{R.color.color_5988FF, R.color.color_2873FF});
        this.dfbButton.getLeftButton().setText(R.string.cancel);
        this.dfbButton.getRightButton().setText(R.string.send);
        this.dfbButton.getLeftButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil.dismissDialog(AISuccessDialog.this);
                AISuccessCallback callback = aiSuccessCallback;
                if (callback != null) {
                    callback.clickLeft();
                }
            }
        });
        this.dfbButton.getRightButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AIBehaviorUtil.reportShareResult(isInRecord);
                DialogUtil.dismissDialog(AISuccessDialog.this);
                AISuccessCallback callback = aiSuccessCallback;
                if (callback != null) {
                    callback.clickRight(tvContent.getText().toString());
                }
                ActivityControllListener.getInstance().finishActivity();
            }
        });
    }

    public void setTvContent(String content) {
        this.tvContent.setText(content);
    }

    public void setInRecord(boolean inRecord) {
        this.isInRecord = inRecord;
    }

    public boolean isInRecord() {
        return isInRecord;
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

    private void releaseView() {
        View view = this.contentView;
        if (view == null) {
            return;
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(this.contentView);
        }
        View rootView = this.contentView.getRootView();
        LogUtil.d(TAG, "initConfig: rootView");
        if (rootView instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) rootView;
            LogUtil.d(TAG, "initConfig: rootView" + scrollView.getScrollY());
            if (scrollView.getScrollY() != 0) {
                scrollView.smoothScrollTo(0, 0);
            }
        }
    }

    @Override
    public void dismiss() {
        releaseView();
        super.dismiss();
    }
}