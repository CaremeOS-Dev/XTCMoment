package com.xtc.moment.module.personalinfo.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.module.personalinfo.net.bean.BadgeBean;
import com.xtc.ui.widget.scalablecontainer.AppLinearLayout;
import com.xtc.utils.common.CollectionUtil;
import com.xtc.utils.ui.DimenUtil;

import java.util.List;

public class PersonalBadgeItem extends FrameLayout {

    private static final String TAG = "PersonalBadgeItem";

    private final TextView tvNoBadge;
    private final LinearLayout llBadge;
    private OnClickListener onClickListener;

    public interface OnClickListener {
        void onClick();
    }

    public PersonalBadgeItem(Context context) {
        this(context, null);
    }

    public PersonalBadgeItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PersonalBadgeItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.item_personal_badge, (ViewGroup) this, true);
        ((AppLinearLayout) findViewById(R.id.ll_root)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onClick();
                }
            }
        });
        tvNoBadge = (TextView) findViewById(R.id.tv_no_badge);
        llBadge = (LinearLayout) findViewById(R.id.ll_badge);
    }

    public void setClickListener(OnClickListener listener) {
        onClickListener = listener;
    }

    public void updateBadge(List<BadgeBean> badgeBeanList) {
        LogUtil.d(TAG, "updateBadge() called with: badgeBeanList = [" + badgeBeanList + "]");
        if (getContext() == null) {
            return;
        }
        if (CollectionUtil.isEmpty(badgeBeanList)) {
            tvNoBadge.setText(getContext().getString(R.string.badge_num_format, "0 "));
            tvNoBadge.setTextColor(ContextCompat.getColor(getContext(), R.color.color_877eff));
            tvNoBadge.setTextSize(19.0f);
            tvNoBadge.setVisibility(View.VISIBLE);
            llBadge.setVisibility(View.GONE);
            return;
        }
        llBadge.setVisibility(View.VISIBLE);
        llBadge.removeAllViews();
        tvNoBadge.setVisibility(View.GONE);
        int size = badgeBeanList.size();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                DimenUtil.dp2px(getContext(), 22.0f), DimenUtil.dp2px(getContext(), 20.0f));
        int showCount = Math.min(5, size);
        for (int i = 0; i < showCount; i++) {
            String icon = badgeBeanList.get(i).getIcon();
            ImageView imageView = new ImageView(getContext());
            layoutParams.rightMargin = DimenUtil.dp2px(getContext(), 0);
            imageView.setLayoutParams(layoutParams);
            imageView.setAdjustViewBounds(true);
            llBadge.addView(imageView);
            showBadge(icon, imageView);
        }
    }

    private void showBadge(String icon, ImageView imageView) {
        imageView.setVisibility(View.VISIBLE);
        Glide.with(getContext()).load(icon).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)).into(imageView);
    }
}