package com.xtc.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.ui.widget.UiConstants;
import com.xtc.ui.widget.dialog.bean.icon.NormalIconDialogBean;
import com.xtc.ui.widget.util.UiBgUtil;
import com.xtc.ui.widget.util.UiCommonUtil;
import com.xtc.ui.widget.util.UiIconUtil;
import com.xtc.utils.ui.WatchBlurUtil;

/**
 * General icon dialog: two or three circular icon buttons, optionally with a
 * title. The layout rules differ per {@link #DOUBLE_BUTTON} /
 * {@link #DOUBLE_BUTTON_WITH_TITLE} / {@link #THREE_BUTTON} /
 * {@link #THREE_BUTTON_WITH_TITLE} style.
 */
public class NormalIconDialog extends Dialog {

    public static final int DOUBLE_BUTTON = 0;
    public static final int DOUBLE_BUTTON_WITH_TITLE = 1;
    public static final int THREE_BUTTON = 2;
    public static final int THREE_BUTTON_WITH_TITLE = 3;

    private static final String TAG = "NormalIconDialog";
    private static final String INVALID = "";

    private Context mContext;
    private RelativeLayout rlRoot;
    private TextView tvTitle;
    private RelativeLayout rlLeft;
    private ImageView ivLeft;
    private TextView tvLeft;
    private RelativeLayout rlRight;
    private ImageView ivRight;
    private TextView tvRight;
    private TextView tvBottom;
    private boolean ischeck;

    public NormalIconDialog(Context context, boolean forbidSwipeToDismiss, boolean ischeck) {
        this(context, forbidSwipeToDismiss ? R.style.dialog_default_style_forbidSwipe : R.style.dialog_default_style);
        this.ischeck = ischeck;
    }

    public NormalIconDialog(Context context, int themeResId) {
        super(context, themeResId);
        super.setContentView(R.layout.layout_normal_icon_dialog);
        this.mContext = context;
        initView();
    }

    private void initView() {
        this.rlRoot = (RelativeLayout) findViewById(R.id.rl_root_normal_dialog);
        this.tvTitle = (TextView) findViewById(R.id.tv_title_normal_dialog);
        this.rlLeft = (RelativeLayout) findViewById(R.id.rl_left_normal_dialog);
        this.ivLeft = (ImageView) findViewById(R.id.iv_left_normal_dialog);
        this.tvLeft = (TextView) findViewById(R.id.tv_left_normal_dialog);
        this.rlRight = (RelativeLayout) findViewById(R.id.rl_right_normal_dialog);
        this.ivRight = (ImageView) findViewById(R.id.iv_right_normal_dialog);
        this.tvRight = (TextView) findViewById(R.id.tv_right_normal_dialog);
        this.tvBottom = (TextView) findViewById(R.id.tv_bottom_normal_dialog);
    }

    public void initData(final NormalIconDialogBean bean) {
        Resources resources = this.mContext.getResources();
        final NormalIconDialogBean.OnClickListener listener = bean.getListener();
        setLeftIconBackground(bean.getLeftBtnBgColorIds(), UiConstants.Color.MASK, 30);
        LogUtil.i(TAG, "initData" + this.ischeck);
        if (this.ischeck) {
            setIvLeftIcon(bean.getLeftIconConstants());
        } else {
            setLeftIcon(bean.getLeftIconConstants());
        }
        setLeftIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onLeftBtnClick(NormalIconDialog.this, view);
            }
        });
        setRightIconBackground(bean.getRightBtnBgColorIds(), UiConstants.Color.MASK, 30);
        if (this.ischeck) {
            setIvRightIcon(bean.getRightIconConstants());
        } else {
            setRightIcon(bean.getRightIconConstants());
        }
        setRightIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRightBtnClick(NormalIconDialog.this, view);
            }
        });
        setBottomBtnOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onBottomBtnClick(NormalIconDialog.this, view);
            }
        });
        setLeftAndRightText(resources.getString(bean.getLeftStringId()), resources.getString(bean.getRightStringId()));
        int style = bean.getStyle();
        if (style == DOUBLE_BUTTON_WITH_TITLE) {
            whenDoubleButtonWithTitle(bean);
            return;
        }
        if (style == THREE_BUTTON) {
            whenThreeButton(bean);
        } else if (style != THREE_BUTTON_WITH_TITLE) {
            whenDoubleButton(bean);
        } else {
            whenThreeButtonWithTitle(bean);
        }
    }

    public void setLeftAndRightText(String left, String right) {
        setLeftText(left);
        setRightText(right);
        resetTextViewHeight((String) this.tvLeft.getText(), (String) this.tvRight.getText());
    }

    private void whenDoubleButton(NormalIconDialogBean bean) {
        this.tvTitle.setVisibility(View.GONE);
        this.tvBottom.setVisibility(View.GONE);
        setRlCenterInVertical(this.rlLeft);
        setRlCenterInVertical(this.rlRight);
    }

    private void setRlCenterInVertical(RelativeLayout relativeLayout) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) relativeLayout.getLayoutParams();
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        relativeLayout.setLayoutParams(layoutParams);
        LogUtil.d(TAG, "setRlCenterInVertical done ---");
    }

    private void whenDoubleButtonWithTitle(NormalIconDialogBean bean) {
        this.tvBottom.setVisibility(View.GONE);
        setTitleText(bean.getTitleString());
        setRlAlignParentBottom(this.rlLeft);
        setRlAlignParentBottom(this.rlRight);
    }

    private void setRlAlignParentBottom(RelativeLayout relativeLayout) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) relativeLayout.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.removeRule(RelativeLayout.CENTER_VERTICAL);
        relativeLayout.setLayoutParams(layoutParams);
        LogUtil.d(TAG, "setRlAlignParentBottom done---");
    }

    private void whenThreeButton(NormalIconDialogBean bean) {
        this.tvTitle.setVisibility(View.GONE);
        this.tvBottom.setVisibility(View.VISIBLE);
        int bottomStringId = bean.getBottomStringId();
        if (bottomStringId != 0 && bottomStringId != -1) {
            setBottomText(this.mContext.getResources().getString(bottomStringId));
        }
        setBottomTVBackground(UiConstants.Color.GRAY_NO_GRADIENT, UiConstants.Color.MASK, 16);
        setRlMarginTop(this.rlLeft);
        setRlMarginTop(this.rlRight);
    }

    private void whenThreeButtonWithTitle(NormalIconDialogBean bean) {
        setTitleTextSingleLine(bean.getTitleString());
        this.tvBottom.setVisibility(View.VISIBLE);
        int bottomStringId = bean.getBottomStringId();
        if (bottomStringId != 0 && bottomStringId != -1) {
            setBottomText(this.mContext.getResources().getString(bottomStringId));
        }
        setBottomTVBackground(UiConstants.Color.GRAY_NO_GRADIENT, UiConstants.Color.MASK, 16);
        setRlMarginTop(this.rlLeft);
        setRlMarginTop(this.rlRight);
    }

    private void setRlMarginTop(RelativeLayout relativeLayout) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) relativeLayout.getLayoutParams();
        layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.removeRule(RelativeLayout.CENTER_VERTICAL);
        float dimension = this.mContext.getResources().getDimension(R.dimen.normal_dialog_three_btn_margin_vertical_top);
        relativeLayout.setY(dimension);
        relativeLayout.setLayoutParams(layoutParams);
        LogUtil.w(TAG, "setRlMarginTop done --> " + dimension);
    }

    private void setLeftIconOnClickListener(View.OnClickListener onClickListener) {
        LogUtil.i(TAG, "setLeftIconOnClickListener()");
        this.ivLeft.setOnClickListener(onClickListener);
    }

    private void setRightIconOnClickListener(View.OnClickListener onClickListener) {
        LogUtil.i(TAG, "setRightIconOnClickListener()");
        this.ivRight.setOnClickListener(onClickListener);
    }

    private void setBottomBtnOnClickListener(View.OnClickListener onClickListener) {
        LogUtil.i(TAG, "setBottomBtnOnClickListener()");
        this.tvBottom.setOnClickListener(onClickListener);
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

    public void setTitleText(CharSequence title) {
        if (this.tvTitle == null) {
            LogUtil.e(TAG, "error , tvTitle = null !");
            return;
        }
        if (!TextUtils.isEmpty(title)) {
            this.tvTitle.setVisibility(View.VISIBLE);
            int maxLines = this.mContext.getResources().getInteger(R.integer.normal_dialog_with_icon_title_max_line);
            LogUtil.i(TAG, "动态适配的 title 的 maxLines = " + maxLines);
            this.tvTitle.setMaxLines(maxLines);
            this.tvTitle.setText(title);
            return;
        }
        this.tvTitle.setVisibility(View.GONE);
    }

    public void setTitleTextSingleLine(CharSequence title) {
        if (this.tvTitle == null) {
            LogUtil.e(TAG, "error , tvTitle = null !");
            return;
        }
        if (!TextUtils.isEmpty(title)) {
            this.tvTitle.setVisibility(View.VISIBLE);
            this.tvTitle.setMaxLines(1);
            this.tvTitle.setText(title);
            return;
        }
        this.tvTitle.setVisibility(View.GONE);
    }

    public void setLeftIconBackground(int[] colorResArray, int[] maskColorResArray, int radius) {
        if (this.ivLeft == null || this.ischeck) {
            return;
        }
        this.ivLeft.setBackground(UiBgUtil.getGradientRoundStateDrawable(this.mContext, colorResArray, maskColorResArray, radius));
    }

    public void setLeftIcon(int iconConstant) {
        if (this.ivLeft == null || this.ischeck) {
            return;
        }
        this.ivLeft.setImageResource(UiIconUtil.getIconResourceId(iconConstant));
    }

    public void setIvLeftIcon(int drawableRes) {
        ImageView imageView = this.ivLeft;
        if (imageView != null) {
            imageView.setImageResource(drawableRes);
        }
    }

    public void setIvRightIcon(int drawableRes) {
        if (this.ivLeft != null) {
            this.ivRight.setImageResource(drawableRes);
        }
    }

    private void setLeftText(CharSequence text) {
        if (this.tvLeft == null || TextUtils.isEmpty(text)) {
            return;
        }
        this.tvLeft.setText(text);
    }

    public void setRightIconBackground(int[] colorResArray, int[] maskColorResArray, int radius) {
        if (this.ivRight != null) {
            this.ivRight.setBackground(UiBgUtil.getGradientRoundStateDrawable(this.mContext, colorResArray, maskColorResArray, radius));
        }
    }

    public void setRightIcon(int iconConstant) {
        if (this.ivRight != null) {
            this.ivRight.setImageResource(UiIconUtil.getIconResourceId(iconConstant));
        }
    }

    private void setRightText(CharSequence text) {
        if (this.tvRight == null || TextUtils.isEmpty(text)) {
            return;
        }
        this.tvRight.setText(text);
    }

    public void setBottomText(CharSequence text) {
        if (this.tvBottom == null || TextUtils.isEmpty(text)) {
            return;
        }
        this.tvBottom.setText(text);
    }

    public void setBottomTVBackground(int[] colorResArray, int[] maskColorResArray, int radius) {
        if (this.tvBottom != null) {
            float f = radius;
            this.tvBottom.setBackground(UiBgUtil.getGradientRoundRectStateDrawable(this.mContext, 0, colorResArray, maskColorResArray, f, f, 80, radius * 2));
        }
    }

    private void resetTextViewHeight(String left, String right) {
        if (TextUtils.isEmpty(left)) {
            left = INVALID;
        }
        if (TextUtils.isEmpty(right)) {
            right = INVALID;
        }
        int width = (int) this.mContext.getResources().getDimension(R.dimen.normal_dialog_tv_width_under_btn);
        int leftHeight = measureTextViewHeight(this.tvLeft, left, width, 2);
        int rightHeight = measureTextViewHeight(this.tvRight, right, width, 2);
        if (leftHeight != rightHeight) {
            LogUtil.d(TAG, "leftHeight = " + leftHeight + " , rightHeight = " + rightHeight);
            int maxHeight = Math.max(leftHeight, rightHeight);
            ViewGroup.LayoutParams leftParams = this.tvLeft.getLayoutParams();
            leftParams.height = maxHeight;
            this.tvLeft.setLayoutParams(leftParams);
            ViewGroup.LayoutParams rightParams = this.tvRight.getLayoutParams();
            rightParams.height = maxHeight;
            this.tvRight.setLayoutParams(rightParams);
        }
    }

    /** Height of {@code text} wrapped to {@code width} and capped at {@code maxLines}. */
    private int measureTextViewHeight(TextView textView, String text, int width, int maxLines) {
        TextPaint paint = textView.getPaint();
        StaticLayout layout = new StaticLayout(text, paint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        if (layout.getLineCount() > maxLines) {
            int cut = layout.getLineStart(maxLines) - 1;
            return new StaticLayout(text.substring(0, cut), paint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false).getHeight();
        }
        return layout.getHeight();
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
