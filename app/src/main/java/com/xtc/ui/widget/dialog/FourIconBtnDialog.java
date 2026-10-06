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
import com.xtc.ui.widget.R;
import com.xtc.ui.widget.UiConstants;
import com.xtc.ui.widget.dialog.bean.icon.FourIconBtnBuilder;
import com.xtc.ui.widget.util.UiBgUtil;
import com.xtc.ui.widget.util.UiCommonUtil;
import com.xtc.ui.widget.util.UiIconUtil;
import com.xtc.ui.widget.util.WatchBlurUtil;

/** 四图标按钮对话框。 */
public class FourIconBtnDialog extends Dialog {
    private static final String INVALID = "";
    private static final String TAG = "FourIconBtnDialog";
    private ImageView ivBottomNegative;
    private ImageView ivBottomPositive;
    private ImageView ivTopNegative;
    private ImageView ivTopPositive;
    private Context mContext;
    private RelativeLayout rlBottomNegative;
    private RelativeLayout rlBottomPositive;
    private RelativeLayout rlRoot;
    private RelativeLayout rlTopNegative;
    private RelativeLayout rlTopPositive;
    private TextView tvBottomNegative;
    private TextView tvBottomPositive;
    private TextView tvTopNegative;
    private TextView tvTopPositive;

    public FourIconBtnDialog(Context context, boolean forbidSwipeToDismiss) {
        this(context, forbidSwipeToDismiss ? R.style.dialog_default_style_forbidSwipe : R.style.dialog_default_style);
    }

    public FourIconBtnDialog(Context context, int themeResId) {
        super(context, themeResId);
        super.setContentView(R.layout.layout_four_icon_dialog);
        this.mContext = context;
        initView();
    }

    private void initView() {
        this.rlRoot = (RelativeLayout) findViewById(R.id.rl_root_normal_dialog);
        this.rlTopPositive = (RelativeLayout) findViewById(R.id.rl_top_positive);
        this.ivTopPositive = (ImageView) findViewById(R.id.iv_top_positive);
        this.tvTopPositive = (TextView) findViewById(R.id.tv_top_positive);
        this.rlBottomPositive = (RelativeLayout) findViewById(R.id.rl_bottom_positive);
        this.ivBottomPositive = (ImageView) findViewById(R.id.iv_bottom_positive);
        this.tvBottomPositive = (TextView) findViewById(R.id.tv_bottom_positive);
        this.rlTopNegative = (RelativeLayout) findViewById(R.id.rl_top_negative);
        this.ivTopNegative = (ImageView) findViewById(R.id.iv_top_negative);
        this.tvTopNegative = (TextView) findViewById(R.id.tv_top_negative);
        this.rlBottomNegative = (RelativeLayout) findViewById(R.id.rl_bottom_negative);
        this.ivBottomNegative = (ImageView) findViewById(R.id.iv_bottom_negative);
        this.tvBottomNegative = (TextView) findViewById(R.id.tv_bottom_negative);
    }

    public void initData(FourIconBtnBuilder builder) {
        Resources resources = this.mContext.getResources();
        final FourIconBtnBuilder.OnClickListener listener = builder.getListener();
        setIconBackground(this.ivTopPositive, builder.getTopPositiveBtnBgColorIds(), UiConstants.Color.MASK, 30);
        setIcon(this.ivTopPositive, builder.getTopPositiveIconConstants());
        setIconOnClickListener(this.ivTopPositive, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onTopPositiveBtnClick(FourIconBtnDialog.this, view);
                }
            }
        });
        setIconBackground(this.ivTopNegative, builder.getTopNegativeBtnBgColorIds(), UiConstants.Color.MASK, 30);
        setIcon(this.ivTopNegative, builder.getTopNegativeIconConstants());
        setIconOnClickListener(this.ivTopNegative, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onTopNegativeBtnClick(FourIconBtnDialog.this, view);
                }
            }
        });
        setIconBackground(this.ivBottomPositive, builder.getBottomPositiveBtnBgColorIds(), UiConstants.Color.MASK, 30);
        setIcon(this.ivBottomPositive, builder.getBottomPositiveIconConstants());
        setIconOnClickListener(this.ivBottomPositive, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onBottomPositiveBtnClick(FourIconBtnDialog.this, view);
                }
            }
        });
        setIconBackground(this.ivBottomNegative, builder.getBottomNegativeBtnBgColorIds(), UiConstants.Color.MASK, 30);
        setIcon(this.ivBottomNegative, builder.getBottomNegativeIconConstants());
        setIconOnClickListener(this.ivBottomNegative, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onBottomNegativeBtnClick(FourIconBtnDialog.this, view);
                }
            }
        });
        setLeftAndRightText(this.tvTopPositive, this.tvTopNegative,
                resources.getString(builder.getTopPositiveStringId()), resources.getString(builder.getTopNegativeStringId()));
        setLeftAndRightText(this.tvBottomPositive, this.tvBottomNegative,
                resources.getString(builder.getBottomPositiveStringId()), resources.getString(builder.getBottomNegativeStringId()));
    }

    public void setLeftAndRightText(TextView leftTextView, TextView rightTextView, String leftText, String rightText) {
        setText(leftTextView, leftText);
        setText(rightTextView, rightText);
        resetTextViewHeight(leftTextView, rightTextView, (String) leftTextView.getText(), (String) rightTextView.getText());
    }

    private void setRlMarginTop(RelativeLayout relativeLayout) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) relativeLayout.getLayoutParams();
        layoutParams.removeRule(12);
        layoutParams.removeRule(15);
        float marginTop = this.mContext.getResources().getDimension(R.dimen.normal_dialog_three_btn_margin_vertical_top);
        relativeLayout.setY(marginTop);
        relativeLayout.setLayoutParams(layoutParams);
        LogUtil.w(TAG, "setRlMarginTop done --> " + marginTop);
    }

    private void setIconOnClickListener(View view, View.OnClickListener listener) {
        view.setOnClickListener(listener);
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
        Bitmap blurBitmap = WatchBlurUtil.getBlurBitmap(view, this.mContext);
        if (blurBitmap != null) {
            this.rlRoot.setBackground(new BitmapDrawable(resources, blurBitmap));
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

    public void setIconBackground(ImageView imageView, int[] colorResArray, int[] maskColorResArray, int radiusDp) {
        if (imageView != null) {
            imageView.setBackground(UiBgUtil.getGradientRoundStateDrawable(this.mContext, colorResArray, maskColorResArray, radiusDp));
        }
    }

    public void setIcon(ImageView imageView, int iconConstant) {
        if (imageView != null) {
            imageView.setImageResource(UiIconUtil.getIconResourceId(iconConstant));
        }
    }

    private void setText(TextView textView, CharSequence text) {
        if (textView == null || TextUtils.isEmpty(text)) {
            return;
        }
        textView.setText(text);
    }

    private void resetTextViewHeight(TextView leftTextView, TextView rightTextView, String leftText, String rightText) {
        if (TextUtils.isEmpty(leftText)) {
            leftText = "";
        }
        if (TextUtils.isEmpty(leftText)) {
            rightText = "";
        }
        int width = (int) this.mContext.getResources().getDimension(R.dimen.normal_dialog_tv_width_under_btn);
        int leftHeight = measureTextViewHeight(leftTextView, leftText, width, 2);
        int rightHeight = measureTextViewHeight(rightTextView, rightText, width, 2);
        if (leftHeight != rightHeight) {
            LogUtil.d(TAG, "leftHeight = " + leftHeight + " , rightHeight = " + rightHeight);
            int maxHeight = Math.max(leftHeight, rightHeight);
            ViewGroup.LayoutParams leftParams = leftTextView.getLayoutParams();
            leftParams.height = maxHeight;
            leftTextView.setLayoutParams(leftParams);
            ViewGroup.LayoutParams rightParams = rightTextView.getLayoutParams();
            rightParams.height = maxHeight;
            rightTextView.setLayoutParams(rightParams);
        }
    }

    private int measureTextViewHeight(TextView textView, String text, int width, int maxLines) {
        TextPaint paint = textView.getPaint();
        StaticLayout staticLayout = new StaticLayout(text, paint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        int[] result = new int[2];
        if (staticLayout.getLineCount() > maxLines) {
            int lineStart = staticLayout.getLineStart(maxLines) - 1;
            result[0] = lineStart;
            result[1] = new StaticLayout(text.substring(0, lineStart), paint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false).getHeight();
        } else {
            result[0] = -1;
            result[1] = staticLayout.getHeight();
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