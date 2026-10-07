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
import com.xtc.moment.R;
import com.xtc.ui.widget.scalablecontainer.AppLinearLayout;

/** 左侧带图标的按压反馈列表项。 */
public class LeftIconItem extends FrameLayout {
    private static final String TAG = "LeftIconItem";
    private Context context;
    private ImageView iv;
    private View.OnClickListener listener;
    private AppLinearLayout root;
    private TextView tv;

    /** 点击回调。 */
    public interface OnClickListener extends View.OnClickListener {
        @Override
        void onClick(View view);
    }

    public LeftIconItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LeftIconItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.item_left_icon, (ViewGroup) this, true);
        this.root = (AppLinearLayout) findViewById(R.id.all_root_click_scale_left_icon);
        this.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LeftIconItem.this.listener != null) {
                    LeftIconItem.this.listener.onClick(view);
                }
            }
        });
        this.tv = (TextView) findViewById(R.id.tv_click_scale_left_icon);
        this.iv = (ImageView) findViewById(R.id.iv_click_scale_left_icon);
        this.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LeftIconItem.this.listener != null) {
                    LeftIconItem.this.listener.onClick(view);
                }
            }
        });
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.LeftIconItem);
        if (attributes != null) {
            String title = attributes.getString(R.styleable.LeftIconItem_li_title);
            if (!TextUtils.isEmpty(title)) {
                this.tv.setText(title);
            }
            Drawable icon = attributes.getDrawable(R.styleable.LeftIconItem_li_icon);
            if (icon != null) {
                this.iv.setImageDrawable(icon);
            }
            int iconSize = attributes.getDimensionPixelSize(R.styleable.LeftIconItem_li_iconSize, -1);
            if (iconSize != -1) {
                setIvSize(iconSize);
            }
            int minHeight = attributes.getDimensionPixelSize(R.styleable.LeftIconItem_li_minHeightX, -1);
            if (minHeight != -1) {
                this.root.setMinimumHeight(minHeight);
            }
            attributes.recycle();
        }
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

    public void setIvBigSize() {
        setIvSize((int) getResources().getDimension(R.dimen.item_click_scale_icon_big_width));
    }

    public void setIvSmallSize() {
        setIvSize((int) getResources().getDimension(R.dimen.item_click_scale_icon_small_width));
    }

    private void setIvSize(int size) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.iv.getLayoutParams();
        layoutParams.width = size;
        layoutParams.height = size;
        this.iv.setLayoutParams(layoutParams);
    }
}