package com.xtc.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.ui.widget.button.LongSolidButton;
import com.xtc.ui.widget.dialog.bean.noIcon.LongSolidBtnWithTitleBean;
import com.xtc.ui.widget.util.UiCommonUtil;
import com.xtc.utils.ui.WatchBlurUtil;

/** Dialog with a title, content and one full-width solid button. */
public class LongSolidBtnWithTitleDialog extends Dialog {

    private static final String TAG = "LongSolidBtnWithTitleDialog";

    private Context mContext;
    private RelativeLayout rlRoot;
    private TextView tvTitle;
    private TextView tvContent;
    private LongSolidButton tvBottom;

    public LongSolidBtnWithTitleDialog(Context context, boolean forbidSwipeToDismiss) {
        this(context, forbidSwipeToDismiss ? R.style.dialog_default_style_forbidSwipe : R.style.dialog_default_style);
    }

    public LongSolidBtnWithTitleDialog(Context context, int themeResId) {
        super(context, themeResId);
        super.setContentView(R.layout.layout_long_solid_button_with_title_dialog);
        this.mContext = context;
        initView();
    }

    private void initView() {
        this.rlRoot = (RelativeLayout) findViewById(R.id.rl_root_normal_dialog_with_title);
        this.tvTitle = (TextView) findViewById(R.id.tv_title_normal_dialog_with_title);
        this.tvContent = (TextView) findViewById(R.id.tv_content_normal_dialog_with_title);
        this.tvBottom = (LongSolidButton) findViewById(R.id.btn_bottom_with_title);
    }

    public void initData(LongSolidBtnWithTitleBean bean) {
        setTitleString(bean.getTitleString());
        setContentString(bean.getContentString());
        this.tvBottom.getTv().setText(bean.getBtnStringId());
    }

    public void setTitleString(CharSequence title) {
        LogUtil.d(TAG, "setTitleString = " + ((Object) title));
        if (TextUtils.isEmpty(title)) {
            this.tvTitle.setVisibility(View.GONE);
        } else {
            this.tvTitle.setVisibility(View.VISIBLE);
            this.tvTitle.setText(title);
        }
    }

    public void setContentString(CharSequence content) {
        LogUtil.d(TAG, "setContentString = " + ((Object) content));
        if (TextUtils.isEmpty(content)) {
            this.tvContent.setVisibility(View.GONE);
        } else {
            this.tvContent.setVisibility(View.VISIBLE);
            this.tvContent.setText(content);
        }
    }

    public TextView getTvTitle() {
        return this.tvTitle;
    }

    public TextView getTvContent() {
        return this.tvContent;
    }

    public LongSolidButton getBottomBtn() {
        return this.tvBottom;
    }

    public void setTransparentBackgroud() {
        RelativeLayout relativeLayout = this.rlRoot;
        if (relativeLayout != null) {
            relativeLayout.setBackground(null);
        }
    }

    @Deprecated
    public void setWholeBackgroud(View view) {
        if (this.rlRoot == null) {
            LogUtil.e(TAG, "rlRoot = null , return !");
            return;
        }
        Resources resources = this.mContext.getResources();
        Bitmap bitmap = WatchBlurUtil.blur(view, this.mContext);
        if (bitmap != null) {
            this.rlRoot.setBackground(new BitmapDrawable(resources, bitmap));
        } else {
            LogUtil.w(TAG, "dialog 高斯模糊失败 !");
        }
    }

    public void setWholeBackgroud(int colorRes) {
        RelativeLayout relativeLayout = this.rlRoot;
        if (relativeLayout != null) {
            relativeLayout.setBackgroundColor(UiCommonUtil.getColor(this.mContext, colorRes));
        }
    }

    public void setWholeBackgroud(Drawable drawable) {
        RelativeLayout relativeLayout = this.rlRoot;
        if (relativeLayout != null) {
            relativeLayout.setBackground(drawable);
        }
    }

    @Override
    public void show() {
        LogUtil.i(TAG, "show()");
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
        LogUtil.i(TAG, "dismiss()");
        super.dismiss();
    }
}
