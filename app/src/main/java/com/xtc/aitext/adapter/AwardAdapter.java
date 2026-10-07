package com.xtc.aitext.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtc.moment.R;
import com.xtc.aitext.bean.ButtonDescriptionBean;
import com.xtc.aitext.bean.UserAccessBean;
import com.xtc.database.ormlite.CollectionUtil;
import com.xtc.log.util.TextUtils;

import java.util.List;
import java.util.Objects;

/**
 * 权益领取列表适配器。
 */
public class AwardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "ai_text_AwardAdapter";

    private static final int TYPE_TITLE = 0;
    private static final int TYPE_CONTENT = 1;

    private final Context context;
    private final List<UserAccessBean> accessList;
    private ItemOnClickListener itemOnClickListener;

    /** 领取按钮点击回调。 */
    public interface ItemOnClickListener {
        void onItemClick(UserAccessBean userAccessBean);
    }

    public AwardAdapter(Context context, List<UserAccessBean> accessList) {
        this.context = context;
        this.accessList = accessList;
    }

    @Override
    public int getItemViewType(int position) {
        return position == TYPE_TITLE ? TYPE_TITLE : TYPE_CONTENT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_TITLE) {
            return new TitleViewHolder(LayoutInflater.from(context).inflate(R.layout.item_award_title, parent, false));
        }
        return new ContentViewHolder(LayoutInflater.from(context).inflate(R.layout.item_award_content, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentViewHolder) {
            ((ContentViewHolder) holder).bind(accessList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return accessList.size();
    }

    /** 更新权益列表。 */
    public void setAccessList(List<UserAccessBean> list) {
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        this.accessList.clear();
        this.accessList.add(new UserAccessBean());
        this.accessList.addAll(list);
        notifyDataSetChanged();
    }

    /** 更新单个权益项。 */
    public void updateAccess(UserAccessBean userAccessBean) {
        if (userAccessBean == null || CollectionUtil.isEmpty(this.accessList)) {
            return;
        }
        int index = this.accessList.indexOf(userAccessBean);
        if (index < 0) {
            return;
        }
        this.accessList.set(index, userAccessBean);
        notifyItemChanged(index);
    }

    public void setItemOnClickListener(ItemOnClickListener itemOnClickListener) {
        this.itemOnClickListener = itemOnClickListener;
    }

    /**
     * 标题条目。
     */
    class TitleViewHolder extends RecyclerView.ViewHolder {
        TitleViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 内容条目。
     */
    class ContentViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;
        private final TextView tvRule;
        private final TextView tvHint;
        private final Button btnAward;
        private final TextView ivLabel;
        private final ImageView loadingIv;

        private ObjectAnimator rotationAnimator;
        private UserAccessBean accessBean;

        ContentViewHolder(View itemView) {
            super(itemView);
            this.tvTitle = itemView.findViewById(R.id.award_tv_title);
            this.tvRule = itemView.findViewById(R.id.award_tv_rule);
            this.tvHint = itemView.findViewById(R.id.award_tv_hint);
            this.ivLabel = itemView.findViewById(R.id.award_iv_label);
            this.btnAward = itemView.findViewById(R.id.award_btn);
            this.loadingIv = itemView.findViewById(R.id.award_loading_iv);
            this.btnAward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (AwardAdapter.this.itemOnClickListener != null) {
                        AwardAdapter.this.itemOnClickListener.onItemClick(accessBean);
                    }
                }
            });
        }

        void bind(UserAccessBean accessBean) {
            this.accessBean = accessBean;
            bindButton(accessBean.getButtonDescription());
            this.tvTitle.setText(accessBean.getAccessName());
            this.tvRule.setText(accessBean.getAccessDescription());
            this.tvHint.setText(accessBean.getSubAccessDescription());
            if (TextUtils.isEmpty(accessBean.getTagText())) {
                this.ivLabel.setVisibility(View.GONE);
            } else {
                this.ivLabel.setVisibility(View.VISIBLE);
                this.ivLabel.setText(accessBean.getTagText());
            }
        }

        private void bindButton(ButtonDescriptionBean buttonDescription) {
            if (buttonDescription == null) {
                return;
            }
            ObjectAnimator animator = this.rotationAnimator;
            if (animator != null && animator.isRunning()) {
                this.rotationAnimator.cancel();
                this.loadingIv.setVisibility(View.GONE);
            }
            if (Objects.equals(0, buttonDescription.getObtainStatus())) {
                this.loadingIv.setVisibility(View.VISIBLE);
                this.rotationAnimator = createRotationAnimator(this.loadingIv);
                if (this.rotationAnimator != null) {
                    this.rotationAnimator.start();
                }
                return;
            }
            if (Objects.equals(1, buttonDescription.getObtainStatus())) {
                this.btnAward.setBackground(AwardAdapter.this.context.getResources().getDrawable(R.drawable.bg_can_award));
                this.btnAward.setText(R.string.string_award_can_award);
            }
            if (Objects.equals(2, buttonDescription.getObtainStatus())) {
                this.btnAward.setBackground(AwardAdapter.this.context.getResources().getDrawable(R.drawable.bg_had_award));
                this.btnAward.setText(R.string.string_award_had_award);
            }
            if (TextUtils.isEmpty(buttonDescription.getObtainDescription())) {
                return;
            }
            this.btnAward.setText(buttonDescription.getObtainDescription());
        }

        private ObjectAnimator createRotationAnimator(ImageView imageView) {
            if (imageView == null) {
                return null;
            }
            this.rotationAnimator = ObjectAnimator.ofFloat(imageView, "rotation", 0.0f, 360.0f);
            this.rotationAnimator.setDuration(2400L);
            this.rotationAnimator.setRepeatCount(-1);
            this.rotationAnimator.setInterpolator(new LinearInterpolator());
            return this.rotationAnimator;
        }
    }
}