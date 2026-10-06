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
import com.xtc.ui.widget.R;
import com.xtc.ui.widget.scalablecontainer.AppLinearLayout;

/** 右侧带图标的按压反馈列表项。 */
public class RightIconItem extends FrameLayout {
    private static final String TAG = "RightIconItem";
    private Context context;
    private ImageView iv;
    private View.OnClickListener listener;
    private View rlHolder;
    private AppLinearLayout root;
    private TextView tv;

    /** 点击回调。 */
    public interface OnClickListener extends View.OnClickListener {
        @Override
        void onClick(View view);
    }

    public RightIconItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RightIconItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.item_right_icon, (ViewGroup) this, true);
        this.root = (AppLinearLayout) findViewById(R.id.all_root_click_scale_right_icon);
        this.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.d(RightIconItem.TAG, "root onClick");
                if (RightIconItem.this.listener != null) {
                    RightIconItem.this.listener.onClick(view);
                }
            }
        });
        this.tv = (TextView) findViewById(R.id.tv_click_scale_right_icon);
        this.iv = (ImageView) findViewById(R.id.iv_click_scale_right_icon);
        this.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.d(RightIconItem.TAG, "iv onClick");
                if (RightIconItem.this.listener != null) {
                    RightIconItem.this.listener.onClick(view);
                }
            }
        });
        this.rlHolder = findViewById(R.id.rl_click_scale_right_icon);
        this.rlHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.d(RightIconItem.TAG, "rlHolder onClick");
                if (RightIconItem.this.listener != null) {
                    RightIconItem.this.listener.onClick(view);
                }
            }
        });
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.RightIconItem);
        if (attributes != null) {
            String title = attributes.getString(R.styleable.RightIconItem_ri_title);
            if (!TextUtils.isEmpty(title)) {
                this.tv.setText(title);
            }
            Drawable icon = attributes.getDrawable(R.styleable.RightIconItem_ri_icon);
            if (icon != null) {
                this.iv.setImageDrawable(icon);
            }
            int minHeight = attributes.getDimensionPixelSize(R.styleable.RightIconItem_ri_minHeightX, -1);
            if (minHeight != -1) {
                this.root.setMinimumHeight(minHeight);
            }
            attributes.recycle();
        }
        this.root.setForbidView(this.rlHolder);
    }

    public void setTitle(String title) {
        this.tv.setText(title);
    }

    public void setIconResource(int resId) {
        if (resId != 0) {
            this.iv.setImageResource(resId);
        }
    }

    public TextView getTextView() {
        return this.tv;
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

    public void setMinHeightSmall() {
        this.root.setMinimumHeight((int) getResources().getDimension(R.dimen.item_click_scale_min_height_1));
    }

    public void setMinHeightBig() {
        this.root.setMinimumHeight((int) getResources().getDimension(R.dimen.item_click_scale_min_height_2));
    }

    public void hideRightIcon() {
        if (this.iv.getVisibility() != 8) {
            this.iv.setVisibility(8);
            setTvMarginEnd((int) getResources().getDimension(R.dimen.item_click_scale_tv_margin_left));
        }
    }

    public void showRightIcon() {
        if (this.iv.getVisibility() == 8) {
            this.iv.setVisibility(0);
            setTvMarginEnd(0);
        }
    }

    private void setTvMarginEnd(int marginEnd) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.tv.getLayoutParams();
        layoutParams.setMarginEnd(marginEnd);
        this.tv.setLayoutParams(layoutParams);
    }

    public void setForbidView(View view) {
        this.root.setForbidView(view);
    }

    public View getForbidView() {
        return this.root.getForbidView();
    }
}