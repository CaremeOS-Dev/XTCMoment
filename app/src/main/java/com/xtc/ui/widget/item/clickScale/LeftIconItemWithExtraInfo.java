package com.xtc.ui.widget.item.clickScale;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.ui.widget.scalablecontainer.AppLinearLayout;

/** 左侧带图标、且带副标题的按压反馈列表项。 */
public class LeftIconItemWithExtraInfo extends FrameLayout {
    private static final String TAG = "LeftIconItemWithExtraInfo";
    private final ImageView ivIcon;
    private final LinearLayout llContent;
    private View.OnClickListener onClickListener;
    private final TextView tvExtra;
    private final TextView tvTitle;

    /** 点击回调。 */
    public interface OnClickListener extends View.OnClickListener {
        @Override
        void onClick(View view);
    }

    public LeftIconItemWithExtraInfo(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LeftIconItemWithExtraInfo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.item_left_icon_with_extra, (ViewGroup) this, true);
        AppLinearLayout root = (AppLinearLayout) findViewById(R.id.all_root_click_scale_left_icon_with_extra);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.d(LeftIconItemWithExtraInfo.TAG, "allRoot onClick");
                if (LeftIconItemWithExtraInfo.this.onClickListener != null) {
                    LeftIconItemWithExtraInfo.this.onClickListener.onClick(view);
                }
            }
        });
        this.llContent = (LinearLayout) findViewById(R.id.ll_click_scale_left_icon_with_extra);
        this.tvTitle = (TextView) findViewById(R.id.tv_click_scale_left_icon_with_extra);
        this.tvExtra = (TextView) findViewById(R.id.tv_extra_click_scale_left_icon_with_extra);
        this.ivIcon = (ImageView) findViewById(R.id.iv_click_scale_left_icon_with_extra);
        this.ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.d(LeftIconItemWithExtraInfo.TAG, "ivIcon onClick");
                if (LeftIconItemWithExtraInfo.this.onClickListener != null) {
                    LeftIconItemWithExtraInfo.this.onClickListener.onClick(view);
                }
            }
        });
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.LeftIconItemWithExtraInfo);
        if (attributes != null) {
            String title = attributes.getString(R.styleable.LeftIconItemWithExtraInfo_lis_title);
            if (!TextUtils.isEmpty(title)) {
                this.tvTitle.setText(title);
            }
            String summary = attributes.getString(R.styleable.LeftIconItemWithExtraInfo_lis_summary);
            if (!TextUtils.isEmpty(summary)) {
                this.tvExtra.setText(summary);
            }
            Drawable icon = attributes.getDrawable(R.styleable.LeftIconItemWithExtraInfo_lis_icon);
            if (icon != null) {
                this.ivIcon.setImageDrawable(icon);
            }
            int minHeight = attributes.getDimensionPixelSize(R.styleable.LeftIconItemWithExtraInfo_lis_minHeightX, -1);
            if (minHeight != -1) {
                root.setMinimumHeight(minHeight);
            }
            attributes.recycle();
        }
    }

    public void setTitle(String title) {
        this.tvTitle.setText(title);
    }

    public void setSummary(String summary) {
        this.tvExtra.setText(summary);
    }

    public void setIconResource(int resId) {
        if (resId != 0) {
            this.ivIcon.setImageResource(resId);
        }
    }

    public TextView getTvTitle() {
        return this.tvTitle;
    }

    public TextView getTvExtra() {
        return this.tvExtra;
    }

    public LinearLayout getContentLayout() {
        return this.llContent;
    }

    public ImageView getImageView() {
        return this.ivIcon;
    }

    @Deprecated
    public void setClickListener(OnClickListener listener) {
        this.onClickListener = listener;
    }

    @Override
    public void setOnClickListener(View.OnClickListener listener) {
        super.setOnClickListener(listener);
        this.onClickListener = listener;
    }

    public void setContentMarginStart(int marginStart) {
        LogUtil.d(TAG, "setContentMarginStart = " + marginStart);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.llContent.getLayoutParams();
        layoutParams.setMarginStart(marginStart);
        this.llContent.setLayoutParams(layoutParams);
    }

    public void setContentMarginEnd(int marginEnd) {
        LogUtil.d(TAG, "setContentMarginEnd = " + marginEnd);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.llContent.getLayoutParams();
        layoutParams.setMarginEnd(marginEnd);
        this.llContent.setLayoutParams(layoutParams);
    }
}