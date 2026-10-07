package com.xtc.ui.widget.item.clickScale;

import android.content.Context;
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

/** 左右两侧都带图标、且带副标题的按压反馈列表项。 */
public class LeftRightIconItemWithExtraInfo extends FrameLayout {
    private static final String TAG = "LeftRightIconItemWithExtraInfo";
    private final ImageView ivLeftIcon;
    private final ImageView ivRightIcon;
    private final LinearLayout llContent;
    private View.OnClickListener onClickListener;
    private final TextView tvExtra;
    private final TextView tvTitle;

    /** 点击回调。 */
    public interface OnClickListener extends View.OnClickListener {
        @Override
        void onClick(View view);
    }

    public LeftRightIconItemWithExtraInfo(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LeftRightIconItemWithExtraInfo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.item_left_and_right_icon_with_extra, (ViewGroup) this, true);
        ((AppLinearLayout) findViewById(R.id.all_root_click_scale_left_icon_with_extra)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.d(LeftRightIconItemWithExtraInfo.TAG, "allRoot onClick");
                if (LeftRightIconItemWithExtraInfo.this.onClickListener != null) {
                    LeftRightIconItemWithExtraInfo.this.onClickListener.onClick(view);
                }
            }
        });
        this.llContent = (LinearLayout) findViewById(R.id.ll_click_scale_left_icon_with_extra);
        this.tvTitle = (TextView) findViewById(R.id.tv_click_scale_left_icon_with_extra);
        this.tvExtra = (TextView) findViewById(R.id.tv_extra_click_scale_left_icon_with_extra);
        this.ivLeftIcon = (ImageView) findViewById(R.id.iv_click_scale_left_icon_with_extra);
        this.ivRightIcon = (ImageView) findViewById(R.id.iv_click_scale_right_icon_with_extra);
        this.ivLeftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.d(LeftRightIconItemWithExtraInfo.TAG, "ivIcon onClick");
                if (LeftRightIconItemWithExtraInfo.this.onClickListener != null) {
                    LeftRightIconItemWithExtraInfo.this.onClickListener.onClick(view);
                }
            }
        });
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

    public ImageView getLeftImageView() {
        return this.ivLeftIcon;
    }

    public ImageView getRightImageView() {
        return this.ivRightIcon;
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