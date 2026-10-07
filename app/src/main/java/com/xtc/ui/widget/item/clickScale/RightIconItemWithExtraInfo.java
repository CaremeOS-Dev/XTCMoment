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

/** 右侧带图标、且带副标题的按压反馈列表项。 */
public class RightIconItemWithExtraInfo extends FrameLayout {
    private static final String TAG = "RightIconItemWithExtraInfo";
    private Context context;
    private ImageView iv;
    private View.OnClickListener listener;
    private LinearLayout llContent;
    private View rlHolder;
    private AppLinearLayout root;
    private TextView tvExtra;
    private TextView tvTitle;

    /** 点击回调。 */
    public interface OnClickListener extends View.OnClickListener {
        @Override
        void onClick(View view);
    }

    public RightIconItemWithExtraInfo(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RightIconItemWithExtraInfo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.item_right_icon_with_extra, (ViewGroup) this, true);
        this.root = (AppLinearLayout) findViewById(R.id.all_root_click_scale_right_icon_with_extra);
        this.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (RightIconItemWithExtraInfo.this.listener != null) {
                    RightIconItemWithExtraInfo.this.listener.onClick(view);
                }
            }
        });
        this.llContent = (LinearLayout) findViewById(R.id.ll_click_scale_right_icon_with_extra);
        this.tvTitle = (TextView) findViewById(R.id.tv_click_scale_right_icon_with_extra);
        this.tvExtra = (TextView) findViewById(R.id.tv_extra_click_scale_right_icon_with_extra);
        this.iv = (ImageView) findViewById(R.id.iv_click_scale_right_icon_with_extra);
        this.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (RightIconItemWithExtraInfo.this.listener != null) {
                    RightIconItemWithExtraInfo.this.listener.onClick(view);
                }
            }
        });
        this.rlHolder = findViewById(R.id.rl_click_scale_right_icon_with_extra);
        this.rlHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (RightIconItemWithExtraInfo.this.listener != null) {
                    RightIconItemWithExtraInfo.this.listener.onClick(view);
                }
            }
        });
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.RightIconItemWithExtraInfo);
        if (attributes != null) {
            String title = attributes.getString(R.styleable.RightIconItemWithExtraInfo_ris_title);
            if (!TextUtils.isEmpty(title)) {
                this.tvTitle.setText(title);
            }
            String summary = attributes.getString(R.styleable.RightIconItemWithExtraInfo_ris_summary);
            if (!TextUtils.isEmpty(summary)) {
                this.tvExtra.setText(summary);
            }
            Drawable icon = attributes.getDrawable(R.styleable.RightIconItemWithExtraInfo_ris_icon);
            if (icon != null) {
                this.iv.setImageDrawable(icon);
            }
            this.iv.setVisibility(attributes.getInt(R.styleable.RightIconItemWithExtraInfo_ris_icon_visibility, 0));
            int minHeight = attributes.getDimensionPixelSize(R.styleable.RightIconItemWithExtraInfo_ris_minHeightX, -1);
            if (minHeight != -1) {
                this.root.setMinimumHeight(minHeight);
            }
            attributes.recycle();
        }
        this.root.setForbidView(this.rlHolder);
    }

    public void setTitle(String title) {
        this.tvTitle.setText(title);
    }

    public void setSummary(String summary) {
        this.tvExtra.setText(summary);
    }

    public void setSummary(String summary, int color) {
        this.tvExtra.setText(summary);
        this.tvExtra.setTextColor(color);
    }

    public void setIconResource(int resId) {
        if (resId != 0) {
            this.iv.setImageResource(resId);
        }
    }

    public TextView getTvTitle() {
        return this.tvTitle;
    }

    public TextView getTvExtra() {
        return this.tvExtra;
    }

    public ImageView getImageView() {
        return this.iv;
    }

    @Deprecated
    public void setClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void setOnClickListener(View.OnClickListener listener) {
        super.setOnClickListener(listener);
        this.listener = listener;
    }

    public void hideRightIcon() {
        if (this.iv.getVisibility() != 8) {
            this.iv.setVisibility(8);
            setContentMarginEnd((int) getResources().getDimension(R.dimen.item_click_scale_tv_margin_left));
        }
    }

    public void showRightIcon() {
        if (this.iv.getVisibility() == 8) {
            this.iv.setVisibility(0);
            setContentMarginEnd(0);
        }
    }

    private void setContentMarginEnd(int marginEnd) {
        LogUtil.d(TAG, "setContentMarginEnd = " + marginEnd);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.llContent.getLayoutParams();
        layoutParams.setMarginEnd(marginEnd);
        this.llContent.setLayoutParams(layoutParams);
    }

    public void setForbidView(View view) {
        this.root.setForbidView(view);
    }

    public View getForbidView() {
        return this.root.getForbidView();
    }
}