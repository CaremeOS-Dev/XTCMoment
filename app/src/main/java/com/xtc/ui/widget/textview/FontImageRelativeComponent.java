package com.xtc.ui.widget.textview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.xtc.ui.widget.R;
import com.xtc.ui.widget.UiConstants;
import com.xtc.ui.widget.util.UiBgUtil;
import com.xtc.ui.widget.util.UiIconUtil;

/** 图标 + 文字组合的相对布局组件（默认删除样式）。 */
public class FontImageRelativeComponent extends RelativeLayout {
    private ImageView mImageView;
    private RelativeLayout mRelativeLayout;
    private TextView mTextView;

    public FontImageRelativeComponent(Context context) {
        this(context, null);
    }

    public FontImageRelativeComponent(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FontImageRelativeComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        initWidgetId();
        initDefaultResource();
        setPressedEffect();
    }

    private void initWidgetId() {
        LayoutInflater.from(getContext()).inflate(R.layout.font_image_relative_conpoment, this);
        this.mRelativeLayout = (RelativeLayout) findViewById(R.id.font_image_relative_component);
        this.mImageView = (ImageView) findViewById(R.id.image_component);
        this.mTextView = (TextView) findViewById(R.id.font_component);
    }

    private void initDefaultResource() {
        setImageViewBackground(UiBgUtil.getGradientRoundStateDrawable(getContext(), UiConstants.Color.GRAY, UiConstants.Color.MASK, 30));
        setImageViewIcon(UiIconUtil.getIconResourceId(2));
        setTextContent(getContext().getResources().getString(R.string.delete));
    }

    private void setPressedEffect() {
        ImageView imageView = this.mImageView;
        if (imageView != null) {
            imageView.setClickable(true);
        }
    }

    public void setRelativeLayoutListener(View.OnClickListener listener) {
        RelativeLayout relativeLayout = this.mRelativeLayout;
        if (relativeLayout != null) {
            relativeLayout.setOnClickListener(listener);
        }
    }

    public void setImageViewListener(View.OnClickListener listener) {
        ImageView imageView = this.mImageView;
        if (imageView != null) {
            imageView.setOnClickListener(listener);
        }
    }

    public void setTextViewListener(View.OnClickListener listener) {
        TextView textView = this.mTextView;
        if (textView != null) {
            textView.setOnClickListener(listener);
        }
    }

    public void setImageViewBackground(Drawable background) {
        ImageView imageView = this.mImageView;
        if (imageView != null) {
            imageView.setBackground(background);
        }
    }

    public void setImageViewBackground(int[] colorResArray) {
        setImageViewBackground(colorResArray, UiConstants.Color.MASK);
    }

    public void setImageViewBackground(int[] colorResArray, int[] maskColorResArray) {
        setImageViewBackground(colorResArray, UiConstants.Color.MASK, 30);
    }

    public void setImageViewBackground(int[] colorResArray, int[] maskColorResArray, int radiusDp) {
        if (this.mImageView != null) {
            this.mImageView.setBackground(UiBgUtil.getGradientRoundStateDrawable(getContext(), colorResArray, maskColorResArray, radiusDp));
        }
    }

    public void setTextContent(String content) {
        if (this.mTextView == null || TextUtils.isEmpty(content)) {
            return;
        }
        this.mTextView.setText(content);
    }

    public void setImageViewIcon(int iconResId) {
        ImageView imageView = this.mImageView;
        if (imageView != null) {
            imageView.setImageResource(iconResId);
        }
    }

    public RelativeLayout getRelativeLayout() {
        return this.mRelativeLayout;
    }

    public ImageView getImageView() {
        return this.mImageView;
    }

    public TextView getTextView() {
        return this.mTextView;
    }
}