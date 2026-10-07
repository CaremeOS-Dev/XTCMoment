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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.ui.widget.UiConstants;
import com.xtc.ui.widget.dialog.bean.icon.ThreeLongItemBuilder;
import com.xtc.ui.widget.util.UiBgUtil;
import com.xtc.ui.widget.util.UiCommonUtil;
import com.xtc.ui.widget.util.UiIconUtil;
import com.xtc.ui.widget.util.WatchBlurUtil;

/** 三个长条目 + 取消按钮的对话框。 */
public class ThreeLongItemDialog extends Dialog {
    private static final String INVALID = "";
    private static final String TAG = "ThreeLongItemDialog";
    private ImageView ivBottom;
    private ImageView ivMiddle;
    private ImageView ivTop;
    private LinearLayout llBottomLayout;
    private LinearLayout llMiddleLayout;
    private LinearLayout llRoot;
    private LinearLayout llTopLayout;
    private Context mContext;
    private TextView tvBottom;
    private TextView tvBottomCancel;
    private TextView tvMiddle;
    private TextView tvTop;

    public ThreeLongItemDialog(Context context, boolean forbidSwipeToDismiss) {
        this(context, forbidSwipeToDismiss ? R.style.dialog_default_style_forbidSwipe : R.style.dialog_default_style);
    }

    public ThreeLongItemDialog(Context context, int themeResId) {
        super(context, themeResId);
        super.setContentView(R.layout.layout_three_long_item_dialog);
        this.mContext = context;
        initView();
    }

    private void initView() {
        this.llRoot = (LinearLayout) findViewById(R.id.ll_root_dialog);
        this.llTopLayout = (LinearLayout) findViewById(R.id.ll_top);
        this.ivTop = (ImageView) findViewById(R.id.iv_top);
        this.tvTop = (TextView) findViewById(R.id.tv_top);
        this.llBottomLayout = (LinearLayout) findViewById(R.id.ll_bottom);
        this.ivBottom = (ImageView) findViewById(R.id.iv_bottom);
        this.tvBottom = (TextView) findViewById(R.id.tv_bottom);
        this.llMiddleLayout = (LinearLayout) findViewById(R.id.ll_middle);
        this.ivMiddle = (ImageView) findViewById(R.id.iv_middle);
        this.tvMiddle = (TextView) findViewById(R.id.tv_middle);
        this.tvBottomCancel = (TextView) findViewById(R.id.tv_bottom_cancel);
    }

    public void initData(ThreeLongItemBuilder builder) {
        Resources resources = this.mContext.getResources();
        final ThreeLongItemBuilder.OnClickListener listener = builder.getListener();
        setIcon(this.ivTop, builder.getTopIconConstants());
        setText(this.tvTop, resources.getString(builder.getTopStringId()));
        setTextColor(this.tvTop, builder.getTopStringColorInt());
        setLayoutBackground(this.llTopLayout, UiConstants.Color.GRAY_NO_GRADIENT2, UiConstants.Color.MASK, 20);
        setIconOnClickListener(this.llTopLayout, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onTopItemClick(ThreeLongItemDialog.this, view);
                }
            }
        });
        setIcon(this.ivMiddle, builder.getMiddleIconConstants());
        setText(this.tvMiddle, resources.getString(builder.getMiddleStringId()));
        setTextColor(this.tvMiddle, builder.getMiddleStringColorInt());
        setLayoutBackground(this.llMiddleLayout, UiConstants.Color.GRAY_NO_GRADIENT2, UiConstants.Color.MASK, 20);
        setIconOnClickListener(this.llMiddleLayout, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onMiddleItemClick(ThreeLongItemDialog.this, view);
                }
            }
        });
        setIcon(this.ivBottom, builder.getBottomIconConstants());
        setText(this.tvBottom, resources.getString(builder.getBottomStringId()));
        setTextColor(this.tvBottom, builder.getBottomStringColorInt());
        setLayoutBackground(this.llBottomLayout, UiConstants.Color.GRAY_NO_GRADIENT2, UiConstants.Color.MASK, 20);
        setIconOnClickListener(this.llBottomLayout, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onBottomItemClick(ThreeLongItemDialog.this, view);
                }
            }
        });
        setText(this.tvBottomCancel, resources.getString(builder.getBottomCancelStringId()));
        setBottomTVBackground(this.tvBottomCancel, UiConstants.Color.GRAY_NO_GRADIENT2, UiConstants.Color.MASK, 16);
        setIconOnClickListener(this.tvBottomCancel, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.oncancelClick(ThreeLongItemDialog.this, view);
                }
            }
        });
    }

    private void setIconOnClickListener(View view, View.OnClickListener listener) {
        view.setOnClickListener(listener);
    }

    public void setTransparentBackgroud() {
        LinearLayout root = this.llRoot;
        if (root != null) {
            root.setBackground(null);
        }
    }

    @Deprecated
    public void setWholeBackgroud(View view) {
        if (this.llRoot == null) {
            LogUtil.e(TAG, "llRoot = null , return !");
            return;
        }
        Resources resources = this.mContext.getResources();
        Bitmap blurBitmap = WatchBlurUtil.getBlurBitmap(view, this.mContext);
        if (blurBitmap != null) {
            this.llRoot.setBackground(new BitmapDrawable(resources, blurBitmap));
        } else {
            LogUtil.w(TAG, "dialog 高斯模糊失败 !");
        }
    }

    public void setWholeBackgroud(int colorResId) {
        LinearLayout root = this.llRoot;
        if (root != null) {
            root.setBackgroundColor(UiCommonUtil.getColor(this.mContext, colorResId));
        }
    }

    public void setWholeBackgroud(Drawable drawable) {
        LinearLayout root = this.llRoot;
        if (root != null) {
            root.setBackground(drawable);
        }
    }

    private void setLayoutBackground(View view, int[] colorResArray, int[] maskColorResArray, int radiusDp) {
        if (view != null) {
            float radius = radiusDp;
            view.setBackground(UiBgUtil.getGradientRoundRectStateDrawable(this.mContext, 0, colorResArray, maskColorResArray,
                    radius, radius, view.getWidth(), radiusDp * 2));
        }
    }

    private void setBottomTVBackground(TextView textView, int[] colorResArray, int[] maskColorResArray, int radiusDp) {
        if (textView != null) {
            float radius = radiusDp;
            textView.setBackground(UiBgUtil.getGradientRoundRectStateDrawable(this.mContext, 0, colorResArray, maskColorResArray,
                    radius, radius, 80, radiusDp * 2));
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

    private void setTextColor(TextView textView, int color) {
        if (textView == null || color == -1) {
            return;
        }
        textView.setTextColor(color);
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