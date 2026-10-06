package com.xtc.ui.widget.textview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.xtc.ui.widget.R;
import com.xtc.ui.widget.UiConstants;
import com.xtc.ui.widget.util.UiBgUtil;

/** 左右两个圆角按钮组成的相对布局组件（默认取消/删除）。 */
public class RelativeRoundComponent extends RelativeLayout {
    private TextView mLeftTextView;
    private RelativeLayout mRelativeLayout;
    private TextView mRightTextView;

    public RelativeRoundComponent(Context context) {
        this(context, null);
    }

    public RelativeRoundComponent(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RelativeRoundComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        initWidgetId();
        initDefaultResource();
        setPressedEffect();
    }

    private void initWidgetId() {
        LayoutInflater.from(getContext()).inflate(R.layout.relative_round_component, this);
        this.mLeftTextView = (TextView) findViewById(R.id.left_tv);
        this.mRightTextView = (TextView) findViewById(R.id.right_tv);
    }

    private void initDefaultResource() {
        Drawable leftBackground = UiBgUtil.getGradientRoundRectStateDrawable(getContext());
        Drawable rightBackground = UiBgUtil.getGradientRoundRectStateDrawable(getContext(), 0,
                UiConstants.Color.GREED, UiConstants.Color.MASK, 4.0f, 20.0f, 79, 40);
        setLeftTextViewBackground(leftBackground);
        setRightTextViewBackground(rightBackground);
        setLeftTextViewContent(getContext().getResources().getString(R.string.cancel));
        setRightTextViewContent(getContext().getResources().getString(R.string.delete));
    }

    private void setPressedEffect() {
        TextView leftTextView = this.mLeftTextView;
        if (leftTextView != null) {
            leftTextView.setClickable(true);
        }
        TextView rightTextView = this.mRightTextView;
        if (rightTextView != null) {
            rightTextView.setClickable(true);
        }
    }

    public void setRightTextViewContent(String content) {
        if (this.mRightTextView == null || TextUtils.isEmpty(content)) {
            return;
        }
        this.mRightTextView.setText(content);
    }

    public void setLeftTextViewContent(String content) {
        if (this.mLeftTextView == null || TextUtils.isEmpty(content)) {
            return;
        }
        this.mLeftTextView.setText(content);
    }

    public void setLeftTextViewBackground(Drawable background) {
        TextView leftTextView = this.mLeftTextView;
        if (leftTextView != null) {
            leftTextView.setBackground(background);
        }
    }

    public void setRightTextViewBackground(Drawable background) {
        TextView rightTextView = this.mRightTextView;
        if (rightTextView != null) {
            rightTextView.setBackground(background);
        }
    }

    public void setRelativeLayoutListener(View.OnClickListener listener) {
        RelativeLayout relativeLayout = this.mRelativeLayout;
        if (relativeLayout != null) {
            relativeLayout.setOnClickListener(listener);
        }
    }

    public void setLeftTextViewListener(View.OnClickListener listener) {
        TextView leftTextView = this.mLeftTextView;
        if (leftTextView != null) {
            leftTextView.setOnClickListener(listener);
        }
    }

    public void setRightTextViewListener(View.OnClickListener listener) {
        TextView rightTextView = this.mRightTextView;
        if (rightTextView != null) {
            rightTextView.setOnClickListener(listener);
        }
    }

    public RelativeLayout getRelativeLayout() {
        return this.mRelativeLayout;
    }

    public TextView getLeftTextView() {
        return this.mLeftTextView;
    }

    public TextView getRightTextView() {
        return this.mRightTextView;
    }
}