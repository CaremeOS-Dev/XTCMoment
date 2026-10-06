package com.xtc.moment.module.widget;

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
 * Moment variant of the general icon dialog that also supports drawing a raw icon resource
 * directly (see {@link #setIvLeftIcon(int)} / {@link #setIvRightIcon(int)}), controlled by the
 * {@code useRawIcon} flag passed to the constructor.
 */
public class NormalThreeIconDialog extends Dialog {

    public static final int DOUBLE_BUTTON = 0;
    public static final int DOUBLE_BUTTON_WITH_TITLE = 1;
    public static final int THREE_BUTTON = 2;
    public static final int THREE_BUTTON_WITH_TITLE = 3;

    private static final String INVALID = "";
    private static final String TAG = "NormalIconDialog";

    /** When true the icon constants are treated as raw drawable ids. */
    private boolean useRawIcon;

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

    public NormalThreeIconDialog(Context context, boolean forbidSwipeToDismiss, boolean useRawIcon) {
        this(context, forbidSwipeToDismiss ? R.style.dialog_default_style_forbidSwipe : R.style.dialog_default_style);
        this.useRawIcon = useRawIcon;
    }

    public NormalThreeIconDialog(Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.layout_normal_icon_dialog);
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

    /** Applies the bean data and lays the dialog out according to its style. */
    public void initData(NormalIconDialogBean bean) {
        Resources resources = this.mContext.getResources();
        final NormalIconDialogBean.OnClickListener listener = bean.getListener();
        setLeftIconBackground(bean.getLeftBtnBgColorIds(), UiConstants.Color.MASK, 30);
        LogUtil.i(TAG, "initData" + this.useRawIcon);
        if (this.useRawIcon) {
            setIvLeftIcon(bean.getLeftIconConstants());
        } else {
            setLeftIcon(bean.getLeftIconConstants());
        }
        setLeftIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onLeftBtnClick(NormalThreeIconDialog.this, view);
            }
        });
        setRightIconBackground(bean.getRightBtnBgColorIds(), UiConstants.Color.MASK, 30);
        if (this.useRawIcon) {
            setIvRightIcon(bean.getRightIconConstants());
        } else {
            setRightIcon(bean.getRightIconConstants());
        }
        setRightIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRightBtnClick(NormalThreeIconDialog.this, view);
            }
        });
        setBottomBtnOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onBottomBtnClick(NormalThreeIconDialog.this, view);
            }
        });
        setLeftAndRightText(resources.getString(bean.getLeftStringId()), resources.getString(bean.getRightStringId()));
        int style = bean.getStyle();
        if (style == DOUBLE_BUTTON_WITH_TITLE) {
            whenDoubleButtonWithTitle(bean);
        } else if (style == THREE_BUTTON) {
            whenThreeButton(bean);
        } else if (style == THREE_BUTTON_WITH_TITLE) {
            whenThreeButtonWithTitle(bean);
        } else {
            whenDoubleButton(bean);
        }
    }

    public void setLeftAndRightText(String leftText, String rightText) {
        setLeftText(leftText);
        setRightText(rightText);
        resetTextViewHeight((String) this.tvLeft.getText(), (String) this.tvRight.getText());
    }

    private void whenDoubleButton(NormalIconDialogBean bean) {
        this.tvTitle.setVisibility(View.GONE);
        this.tvBottom.setVisibility(View.GONE);
        setRlCenterInVertical(this.rlLeft);
        setRlCenterInVertical(this.rlRight);
    }

    private void setRlCenterInVertical(RelativeLayout layout) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layout.setLayoutParams(params);
        LogUtil.d(TAG, "setRlCenterInVertical done ---");
    }

    private void whenDoubleButtonWithTitle(NormalIconDialogBean bean) {
        this.tvBottom.setVisibility(View.GONE);
        setTitleText(bean.getTitleString());
        setRlAlignParentBottom(this.rlLeft);
        setRlAlignParentBottom(this.rlRight);
    }

    private void setRlAlignParentBottom(RelativeLayout layout) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.removeRule(RelativeLayout.CENTER_VERTICAL);
        layout.setLayoutParams(params);
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

    private void setRlMarginTop(RelativeLayout layout) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
        params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.removeRule(RelativeLayout.CENTER_VERTICAL);
        float top = this.mContext.getResources().getDimension(R.dimen.normal_dialog_three_btn_margin_vertical_top);
        layout.setY(top);
        layout.setLayoutParams(params);
        LogUtil.w(TAG, "setRlMarginTop done --> " + top);
    }

    private void setLeftIconOnClickListener(View.OnClickListener listener) {
        LogUtil.i(TAG, "setLeftIconOnClickListener()");
        this.ivLeft.setOnClickListener(listener);
    }

    private void setRightIconOnClickListener(View.OnClickListener listener) {
        LogUtil.i(TAG, "setRightIconOnClickListener()");
        this.ivRight.setOnClickListener(listener);
    }

    private void setBottomBtnOnClickListener(View.OnClickListener listener) {
        LogUtil.i(TAG, "setBottomBtnOnClickListener()");
        this.tvBottom.setOnClickListener(listener);
    }

    public void setTransparentBackgroud() {
        RelativeLayout root = this.rlRoot;
        if (root != null) {
            root.setBackground(null);
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

    public void setWholeBackgroud(int colorResId) {
        RelativeLayout root = this.rlRoot;
        if (root != null) {
            root.setBackgroundColor(UiCommonUtil.getColor(this.mContext, colorResId));
        }
    }

    public void setWholeBackgroud(Drawable drawable) {
        RelativeLayout root = this.rlRoot;
        if (root != null) {
            root.setBackground(drawable);
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
        } else {
            if (!TextUtils.isEmpty(title)) {
                this.tvTitle.setVisibility(View.VISIBLE);
                this.tvTitle.setMaxLines(1);
                this.tvTitle.setText(title);
                return;
            }
            this.tvTitle.setVisibility(View.GONE);
        }
    }

    public void setLeftIconBackground(int[] colorResArray, int[] maskColorResArray, int radius) {
        if (this.ivLeft != null) {
            this.ivLeft.setBackground(UiBgUtil.getGradientRoundStateDrawable(this.mContext, colorResArray, maskColorResArray, radius));
        }
    }

    public void setLeftIcon(int iconConstant) {
        if (this.ivLeft == null || this.useRawIcon) {
            return;
        }
        this.ivLeft.setImageResource(UiIconUtil.getIconResourceId(iconConstant));
    }

    public void setIvLeftIcon(int resId) {
        ImageView view = this.ivLeft;
        if (view != null) {
            view.setImageResource(resId);
        }
    }

    public void setIvRightIcon(int resId) {
        if (this.ivLeft != null) {
            this.ivRight.setImageResource(resId);
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
            float corner = radius;
            this.tvBottom.setBackground(UiBgUtil.getGradientRoundRectStateDrawable(this.mContext, 0, colorResArray,
                    maskColorResArray, corner, corner, 80, radius * 2));
        }
    }

    /** Makes the left and right captions share the same height so both buttons line up. */
    private void resetTextViewHeight(String leftText, String rightText) {
        if (TextUtils.isEmpty(leftText)) {
            leftText = "";
        }
        if (TextUtils.isEmpty(leftText)) {
            rightText = "";
        }
        int width = (int) this.mContext.getResources().getDimension(R.dimen.normal_dialog_tv_width_under_btn);
        int leftHeight = measureTextViewHeight(this.tvLeft, leftText, width, 2);
        int rightHeight = measureTextViewHeight(this.tvRight, rightText, width, 2);
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

    /** Measures the height of {@code text} truncated to {@code maxLines} lines. */
    private int measureTextViewHeight(TextView textView, String text, int width, int maxLines) {
        TextPaint paint = textView.getPaint();
        StaticLayout layout = new StaticLayout(text, paint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        int[] result = new int[2];
        if (layout.getLineCount() > maxLines) {
            int lineStart = layout.getLineStart(maxLines) - 1;
            result[0] = lineStart;
            result[1] = new StaticLayout(text.substring(0, lineStart), paint, width,
                    Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false).getHeight();
        } else {
            result[0] = -1;
            result[1] = layout.getHeight();
        }
        return result[1];
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
        attributes.width = ViewGroup.LayoutParams.MATCH_PARENT;
        attributes.height = ViewGroup.LayoutParams.MATCH_PARENT;
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setAttributes(attributes);
    }

    @Override
    public void dismiss() {
        LogUtil.i(TAG, "dismiss()");
        super.dismiss();
    }
}