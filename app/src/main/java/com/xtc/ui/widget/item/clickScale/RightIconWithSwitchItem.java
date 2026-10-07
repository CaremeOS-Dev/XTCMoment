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
import com.xtc.ui.widget.toggleswitch.SwitchButton;

/** 右侧同时带图标与开关的按压反馈列表项。 */
public class RightIconWithSwitchItem extends FrameLayout {
    private static final String TAG = "RightIconWithSwitchItem";
    private Context context;
    private ImageView iv;
    private View.OnClickListener listener;
    private View rlHolder;
    private View rlHolderSwitch;
    private AppLinearLayout root;
    private AppLinearLayout rootIcon;
    private AppLinearLayout rootSwitch;
    private SwitchButton sb;
    private TextView tv;
    private TextView tvSwitch;

    /** 点击回调。 */
    public interface OnClickListener extends View.OnClickListener {
        @Override
        void onClick(View view);
    }

    public RightIconWithSwitchItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RightIconWithSwitchItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.item_right_icon_with_switch, (ViewGroup) this, true);
        this.root = (AppLinearLayout) findViewById(R.id.all_root_click_scale_right_icon_with_switch);
        this.rootIcon = (AppLinearLayout) findViewById(R.id.all_root_click_scale_right_icon);
        this.rootSwitch = (AppLinearLayout) findViewById(R.id.all_click_scale_right_switch);
        this.tv = (TextView) findViewById(R.id.tv_click_scale_right_icon);
        this.iv = (ImageView) findViewById(R.id.iv_click_scale_right_icon);
        this.tvSwitch = (TextView) findViewById(R.id.tv_click_scale_right_switch);
        this.sb = (SwitchButton) findViewById(R.id.sb__right_switch);
        this.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.d(RightIconWithSwitchItem.TAG, "root onClick");
                if (RightIconWithSwitchItem.this.listener != null) {
                    RightIconWithSwitchItem.this.listener.onClick(view);
                }
            }
        });
        this.rootIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.d(RightIconWithSwitchItem.TAG, "rootIcon onClick");
                if (RightIconWithSwitchItem.this.listener != null) {
                    RightIconWithSwitchItem.this.listener.onClick(view);
                }
            }
        });
        this.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.d(RightIconWithSwitchItem.TAG, "iv onClick");
                if (RightIconWithSwitchItem.this.listener != null) {
                    RightIconWithSwitchItem.this.listener.onClick(view);
                }
            }
        });
    }

    public SwitchButton getSwitchButton() {
        return this.sb;
    }

    public TextView getTextView() {
        return this.tv;
    }

    public TextView getSwitchTextView() {
        return this.tvSwitch;
    }

    public ImageView getImageView() {
        return this.iv;
    }

    public AppLinearLayout getRootSwitch() {
        return this.rootSwitch;
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
        this.rootIcon.setMinimumHeight((int) getResources().getDimension(R.dimen.item_click_scale_min_height_1));
    }

    public void setMinHeightBig() {
        this.rootIcon.setMinimumHeight((int) getResources().getDimension(R.dimen.item_click_scale_min_height_2));
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
        this.rootIcon.setForbidView(view);
    }
}