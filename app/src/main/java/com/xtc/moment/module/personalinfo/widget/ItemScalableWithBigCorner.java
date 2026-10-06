package com.xtc.moment.module.personalinfo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xtc.moment.R;
import com.xtc.ui.widget.scalablecontainer.AppLinearLayout;

/**
 * 大圆角背景的文本条目，点击整块区域时回调外部监听。
 */
public class ItemScalableWithBigCorner extends FrameLayout {

    private final TextView contentText;
    private OnClickListener clickListener;

    /** 条目点击回调。 */
    public interface OnClickListener {
        void onClick();
    }

    public ItemScalableWithBigCorner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemScalableWithBigCorner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.item_scalable_with_big_corner, (ViewGroup) this, true);
        ((AppLinearLayout) findViewById(R.id.all_root)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ItemScalableWithBigCorner.this.clickListener != null) {
                    ItemScalableWithBigCorner.this.clickListener.onClick();
                }
            }
        });
        this.contentText = (TextView) findViewById(R.id.tv_content);
    }

    public TextView getTextView() {
        return this.contentText;
    }

    public void setOnClickListener(OnClickListener listener) {
        this.clickListener = listener;
    }
}