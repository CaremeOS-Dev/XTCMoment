package com.xtc.moment.module.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;

/**
 * 可展开的正文控件：超出最大行数时展示“展开”入口。
 */
public class ExpandTextView extends LinearLayout {

    private static final String TAG = "ExpandTextView";

    private final ExpandCusTomTextView.OnMeasureCallback onMeasureCallback;
    private final Context mContext;
    private ExpandCusTomTextView tvMoodContent;
    private TextView tvShowMore;
    private LinearLayout llCheck;
    private ClickCheckAllListener clickCheckAllListener;
    private boolean isSupportExpand;

    public interface ClickCheckAllListener {
        void click();
    }

    public ExpandTextView(Context context) {
        this(context, null);
    }

    public ExpandTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.onMeasureCallback = new ExpandCusTomTextView.OnMeasureCallback() {
            @Override
            public void measureBack(boolean exceeded) {
                LogUtil.d(TAG, "measureBack: isExpand = " + exceeded);
                ExpandTextView.this.llCheck.setVisibility(exceeded ? VISIBLE : GONE);
                ExpandTextView.this.tvMoodContent.removeMeasureCallback();
            }
        };
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_expand_view, this);
        this.tvMoodContent = (ExpandCusTomTextView) findViewById(R.id.tv_mood_content);
        this.tvShowMore = (TextView) findViewById(R.id.tv_show_more);
        this.llCheck = (LinearLayout) findViewById(R.id.ll_check);
        this.llCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickCheckAllListener listener = ExpandTextView.this.clickCheckAllListener;
                if (listener != null) {
                    listener.click();
                }
            }
        });
    }

    public void setText(CharSequence text, ClickCheckAllListener listener) {
        this.clickCheckAllListener = listener;
        if (this.isSupportExpand) {
            this.tvMoodContent.setOnMeasureCallback(this.onMeasureCallback);
        }
        this.tvMoodContent.setText(text);
    }

    public void setTextColor(int color) {
        this.tvMoodContent.setTextColor(color);
    }

    public void setTextSize(long size) {
        this.tvMoodContent.setTextSize(size);
    }

    public void setMaxLines(int maxLines) {
        this.tvMoodContent.setMaxLines(maxLines);
    }

    public void setMoreText(String text) {
        this.tvShowMore.setText(text);
    }

    public void setMoreColor(int color) {
        this.tvShowMore.setTextColor(color);
    }

    public String getText() {
        return this.tvMoodContent.getText().toString();
    }

    public void setSupportExpand(boolean supportExpand) {
        this.isSupportExpand = supportExpand;
    }

    public boolean isSupportExpand() {
        return this.isSupportExpand;
    }
}