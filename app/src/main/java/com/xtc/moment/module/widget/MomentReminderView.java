package com.xtc.moment.module.widget;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.util.StartWebUtils;

/**
 * 动态顶部提醒条。
 */
public class MomentReminderView extends ConstraintLayout {

    private static final String TAG = "MomentReminderView";

    private ImageView ivWarning;
    private ImageView ivJumpArrow;
    private TextView tvReminder;
    private String reminderUrl;
    private boolean urlAvailable;

    public MomentReminderView(Context context) {
        this(context, null);
    }

    public MomentReminderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MomentReminderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        bindEvent();
    }

    private void initView() {
        View content = LayoutInflater.from(getContext()).inflate(R.layout.view_moment_reminder, this);
        content.setBackgroundResource(R.drawable.bg_moment_reminder);
        this.ivWarning = (ImageView) content.findViewById(R.id.iv_warning);
        this.ivJumpArrow = (ImageView) content.findViewById(R.id.iv_jump_arrow);
        this.tvReminder = (TextView) content.findViewById(R.id.tv_reminder);
    }

    private void bindEvent() {
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MomentReminderView.this.urlAvailable) {
                    StartWebUtils.startH5Activity(MomentReminderView.this.getContext(),
                            MomentReminderView.this.reminderUrl);
                }
            }
        });
    }

    public void setReminderText(String text) {
        this.tvReminder.setText(text.replaceAll("\\s+", ""));
    }

    public void setReminderTextColor(int color) {
        this.tvReminder.setTextColor(color);
    }

    public void setReminderTextUrl(String url) {
        LogUtil.d(TAG, "setReminderUrl#url:" + url);
        this.urlAvailable = !TextUtils.isEmpty(url);
        this.ivJumpArrow.setVisibility(this.urlAvailable ? VISIBLE : GONE);
        setClickable(this.urlAvailable);
        if (this.urlAvailable) {
            this.reminderUrl = url;
        }
    }
}