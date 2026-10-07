package com.xtc.ui.widget.item.clickScale;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.ui.widget.scalablecontainer.AppLinearLayout;
import com.xtc.ui.widget.toggleswitch.SwitchButton;
import com.xtc.ui.widget.util.UiCommonUtil;

/** 右侧带开关、左侧带图标与副标题的按压反馈列表项。 */
public class RightSwitchItemWithExtraInfo extends FrameLayout {
    private static final String TAG = "RightSwitchItemWithExtraInfo";
    private Context context;
    private ImageView ivBottom;
    private ImageView ivTop;
    private LinearLayout llContent;
    private View rlHolder;
    private RelativeLayout rlIcon;
    private AppLinearLayout root;
    private SwitchButton sb;
    private TextView tvExtra;
    private TextView tvTitle;

    public RightSwitchItemWithExtraInfo(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RightSwitchItemWithExtraInfo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.item_right_switch_with_extra, (ViewGroup) this, true);
        this.root = (AppLinearLayout) findViewById(R.id.all_root_click_scale_right_switch_with_extra);
        this.rlIcon = (RelativeLayout) findViewById(R.id.rl_Icon_click_scale_right_switch_with_extra);
        this.ivBottom = (ImageView) findViewById(R.id.iv_click_scale_right_switch_with_extra);
        this.ivTop = (ImageView) findViewById(R.id.iv_top_click_scale_right_switch_with_extra);
        this.llContent = (LinearLayout) findViewById(R.id.ll_click_scale_right_switch_with_extra);
        this.tvTitle = (TextView) findViewById(R.id.tv_click_scale_right_switch_with_extra);
        this.tvExtra = (TextView) findViewById(R.id.tv_extra_click_scale_right_switch_with_extra);
        this.sb = (SwitchButton) findViewById(R.id.sb_click_scale_right_switch_with_extra);
        this.rlHolder = findViewById(R.id.rl_click_scale_right_switch_with_extra);
        this.rlHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.d(RightSwitchItemWithExtraInfo.TAG, "rlHolder onClick");
                RightSwitchItemWithExtraInfo.this.sb.toggle();
            }
        });
        this.root.setForbidView(this.rlHolder);
    }

    public ImageView getImageView() {
        return this.ivBottom;
    }

    public ImageView getTopImageView() {
        return this.ivTop;
    }

    public TextView getTvTitle() {
        return this.tvTitle;
    }

    public TextView getTvExtra() {
        return this.tvExtra;
    }

    public SwitchButton getSwitchButton() {
        return this.sb;
    }

    @Override
    public AppLinearLayout getRootView() {
        return this.root;
    }

    public void setLeftIconVisible(boolean bottomVisible, boolean topVisible) {
        LogUtil.d(TAG, "bottomShow = " + bottomVisible + " , topShow = " + topVisible);
        if (bottomVisible) {
            UiCommonUtil.showView(this.ivBottom);
        } else {
            UiCommonUtil.hideView(this.ivBottom);
        }
        if (topVisible) {
            UiCommonUtil.showView(this.ivTop);
        } else {
            UiCommonUtil.hideView(this.ivTop);
        }
        if (!bottomVisible && !topVisible) {
            UiCommonUtil.hideView(this.rlIcon);
        } else {
            UiCommonUtil.showView(this.rlIcon);
        }
    }
}