package com.xtc.web.core.jump;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xtc.log.LogUtil;
import com.xtc.ui.widget.button.DoubleFlatButton;
import com.xtc.ui.widget.dialog.bean.noIcon.DoubleFlatBtnWithTitleBean;
import com.xtc.ui.widget.util.UiCommonUtil;
import com.xtc.moment.R;

/** 引导用户去应用市场下载应用的全屏弹窗。 */
public class AppDownloadDescriptionDialog extends Dialog {

    private static final String TAG = "AppDownloadDescriptionDialog";

    private Context mContext;
    private RelativeLayout rlRoot;
    private DoubleFlatButton tvBottom;
    private ForceBreakTextView tvContent;
    private TextView tvTitle;

    public AppDownloadDescriptionDialog(Context context, boolean forbidSwipeToDismiss) {
        this(context, forbidSwipeToDismiss ? R.style.dialog_default_style_forbidSwipe : R.style.dialog_default_style);
    }

    public AppDownloadDescriptionDialog(Context context, int themeResId) {
        super(context, themeResId);
        super.setContentView(R.layout.dialog_app_download);
        this.mContext = context;
        initView();
    }

    private void initView() {
        this.rlRoot = (RelativeLayout) findViewById(R.id.rl_root_normal_dialog_with_title);
        this.tvTitle = (TextView) findViewById(R.id.tv_title_normal_dialog_with_title);
        this.tvContent = (ForceBreakTextView) findViewById(R.id.tv_content_normal_dialog_with_title);
        this.tvBottom = (DoubleFlatButton) findViewById(R.id.btn_bottom_with_title);
    }

    /** 填充标题、内容与左右按钮文案。 */
    public void initData(DoubleFlatBtnWithTitleBean bean) {
        setTitleString(bean.getTitleString());
        setContentString(bean.getContentString());
        this.tvBottom.getLeftButton().setText(bean.getLeftStringId());
        this.tvBottom.getRightButton().setText(bean.getRightStringId());
    }

    public void setTitleString(CharSequence title) {
        LogUtil.d(TAG, "setTitleString = " + title);
        if (TextUtils.isEmpty(title)) {
            this.tvTitle.setVisibility(android.view.View.GONE);
        } else {
            this.tvTitle.setVisibility(android.view.View.VISIBLE);
            this.tvTitle.setText(title);
        }
    }

    public void setContentString(CharSequence content) {
        LogUtil.d(TAG, "setContentString = " + content);
        if (TextUtils.isEmpty(content)) {
            this.tvContent.setVisibility(android.view.View.GONE);
        } else {
            this.tvContent.setVisibility(android.view.View.VISIBLE);
            this.tvContent.setMText(content);
        }
    }

    public DoubleFlatButton getBottomBtn() {
        return this.tvBottom;
    }

    /** 设置整个弹窗背景色。 */
    public void setWholeBackground(int colorResId) {
        RelativeLayout relativeLayout = this.rlRoot;
        if (relativeLayout != null) {
            relativeLayout.setBackgroundColor(UiCommonUtil.getColor(this.mContext, colorResId));
        }
    }

    @Override
    public void show() {
        LogUtil.i(TAG, "show()--");
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
        LogUtil.i(TAG, "dismiss()");
        super.dismiss();
    }
}