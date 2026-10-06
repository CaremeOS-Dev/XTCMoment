package com.xtc.ui.widget.item.clickScale;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.xtc.log.LogUtil;
import com.xtc.ui.widget.R;
import com.xtc.ui.widget.scalablecontainer.AppLinearLayout;
import com.xtc.ui.widget.toggleswitch.SwitchButton;

/** 右侧带开关的按压反馈列表项。 */
public class RightSwitchItem extends FrameLayout {
    private static final String TAG = "RightSwitchItem";
    private Context context;
    private View rlHolder;
    private AppLinearLayout root;
    private SwitchButton sb;
    private TextView tv;

    public RightSwitchItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RightSwitchItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.item_right_switch, (ViewGroup) this, true);
        this.root = (AppLinearLayout) findViewById(R.id.all_root_click_scale_right_switch);
        this.tv = (TextView) findViewById(R.id.tv_click_scale_right_switch);
        this.sb = (SwitchButton) findViewById(R.id.sb_click_scale_right_switch);
        this.rlHolder = findViewById(R.id.rl_click_scale_right_switch);
        this.rlHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.d(RightSwitchItem.TAG, "rlHolder onClick");
                RightSwitchItem.this.sb.toggle();
            }
        });
        this.root.setForbidView(this.rlHolder);
    }

    public TextView getTextView() {
        return this.tv;
    }

    public SwitchButton getSwitchButton() {
        return this.sb;
    }

    @Override
    public AppLinearLayout getRootView() {
        return this.root;
    }

    public void setMinHeightSmall() {
        this.root.setMinimumHeight((int) getResources().getDimension(R.dimen.item_click_scale_min_height_1));
    }

    public void setMinHeightBig() {
        this.root.setMinimumHeight((int) getResources().getDimension(R.dimen.item_click_scale_min_height_2));
    }
}