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
import com.xtc.ui.widget.button.LongHollowButton;
import com.xtc.ui.widget.button.LongSolidButton;
import com.xtc.ui.widget.dialog.bean.noIcon.DoubleLongBtnBean;
import com.xtc.ui.widget.util.UiCommonUtil;
import com.xtc.utils.ui.WatchBlurUtil;

/** Dialog with a title and two stacked full-width buttons (solid on top). */
public class DoubleLongBtnDialog extends Dialog {

    private static final String TAG = "LongHollowBtnDialog";

    private Context mContext;
    private RelativeLayout rlRoot;
    private TextView tvTitle;
    private LongHollowButton tvBottom;
    private LongSolidButton tvTop;

    public DoubleLongBtnDialog(Context context, boolean forbidSwipeToDismiss) {
        this(context, forbidSwipeToDismiss ? R.style.dialog_default_style_forbidSwipe : R.style.dialog_default_style);
    }

    public DoubleLongBtnDialog(Context context, int themeResId) {
        super(context, themeResId);
        super.setContentView(R.layout.layout_double_long_button_dialog);
        this.mContext = context;
        initView();
    }

    private void initView() {
        this.rlRoot = (RelativeLayout) findViewById(R.id.rl_root_normal_dialog);
        this.tvTitle = (TextView) findViewById(R.id.tv_title_normal_dialog);
        this.tvBottom = (LongHollowButton) findViewById(R.id.btn_bottom);
        this.tvTop = (LongSolidButton) findViewById(R.id.btn_top);
    }

    public void initData(DoubleLongBtnBean bean) {
        setTitleString(bean.getTitleString());
        this.tvBottom.getTv().setText(bean.getBtnBottomString());
        this.tvTop.getTv().setText(bean.getBtnTopString());
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

    public TextView getTvTitle() {
        return this.tvTitle;
    }

    public LongHollowButton getBottomBtn() {
        return this.tvBottom;
    }

    public LongSolidButton getTvTop() {
        return this.tvTop;
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
