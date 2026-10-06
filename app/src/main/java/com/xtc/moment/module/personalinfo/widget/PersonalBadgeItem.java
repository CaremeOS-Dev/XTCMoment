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

/**
 * 个人中心勋章展示条目。
 *
 * <p>没有勋章时显示数量为 0 的占位文案；有勋章时最多平铺展示 5 个图标。
 */
public class PersonalBadgeItem extends FrameLayout {

    private static final String TAG = "PersonalBadgeItem";

    /** 勋章图标最多展示的数量。 */
    private static final int MAX_BADGE_COUNT = 5;

    private final LinearLayout llBadge;
    private final TextView tvNoBadge;
    private OnClickListener clickListener;

    /** 条目点击回调。 */
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
            public void onClick(View view) {
                if (PersonalBadgeItem.this.clickListener != null) {
                    PersonalBadgeItem.this.clickListener.onClick();
                }
            }
        });
        this.tvNoBadge = (TextView) findViewById(R.id.tv_no_badge);
        this.llBadge = (LinearLayout) findViewById(R.id.ll_badge);
    }

    public void setClickListener(OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    /** 刷新勋章列表。 */
    public void updateBadge(List<BadgeBean> badgeBeanList) {
        LogUtil.d(TAG, "updateBadge() called with: badgeBeanList = [" + badgeBeanList + "]");
        if (getContext() == null) {
            return;
        }
        if (CollectionUtil.isEmpty(badgeBeanList)) {
            this.tvNoBadge.setText(getContext().getString(R.string.badge_num_format, "0 "));
            this.tvNoBadge.setTextColor(ContextCompat.getColor(getContext(), R.color.color_877eff));
            this.tvNoBadge.setTextSize(19.0f);
            this.tvNoBadge.setVisibility(View.VISIBLE);
            this.llBadge.setVisibility(View.GONE);
            return;
        }
        this.llBadge.setVisibility(View.VISIBLE);
        this.llBadge.removeAllViews();
        this.tvNoBadge.setVisibility(View.GONE);
        int badgeCount = badgeBeanList.size();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                DimenUtil.dp2px(getContext(), 22.0f), DimenUtil.dp2px(getContext(), 20.0f));
        int showCount = Math.min(MAX_BADGE_COUNT, badgeCount);
        for (int index = 0; index < showCount; index++) {
            String icon = badgeBeanList.get(index).getIcon();
            ImageView imageView = new ImageView(getContext());
            layoutParams.rightMargin = DimenUtil.dp2px(getContext(), 0);
            imageView.setLayoutParams(layoutParams);
            imageView.setAdjustViewBounds(true);
            this.llBadge.addView(imageView);
            showBadge(icon, imageView);
        }
    }

    private void showBadge(String iconUrl, ImageView imageView) {
        imageView.setVisibility(View.VISIBLE);
        Glide.with(getContext()).load(iconUrl)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(imageView);
    }
}