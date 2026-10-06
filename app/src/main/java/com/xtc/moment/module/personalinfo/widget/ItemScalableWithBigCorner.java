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

public class ItemScalableWithBigCorner extends FrameLayout {

    private final TextView tvContent;
    private OnClickListener listener;

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
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick();
                }
            }
        });
        tvContent = (TextView) findViewById(R.id.tv_content);
    }

    public TextView getTextView() {
        return tvContent;
    }

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }
}