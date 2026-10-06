package com.xtc.moment.module.gift;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.net.bean.GiftDataBean;
import com.xtc.utils.common.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 礼物详情列表适配器：头部标题、礼物赠送者列表与底部占位。
 */
public class GiftAdapter extends RecyclerView.Adapter {

    private static final String TAG = "GiftAdapter";
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_NORMAL = 2;
    private static final int TYPE_FOOTER = 3;
    private static final int TYPE_EMPTY = 4;

    private Context mContext;
    private String titleText;
    private List<GiftDataBean> dataBeans = new ArrayList<>();

    public GiftAdapter(Context context, String titleText) {
        this.mContext = context;
        this.titleText = titleText;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_EMPTY) {
            return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_footer_moment_gift_empty, parent, false)) {
            };
        }
        if (viewType == TYPE_HEADER) {
            return new ItemHeaderHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_head_moment_gift, parent, false));
        }
        if (viewType == TYPE_FOOTER) {
            return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_footer_moment_gift, parent, false)) {
            };
        }
        return new ItemContentHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_moment_gift, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_HEADER) {
            ((ItemHeaderHolder) holder).tvTitle.setText(this.titleText);
            return;
        }
        if (viewType == TYPE_NORMAL) {
            ItemContentHolder contentHolder = (ItemContentHolder) holder;
            if (CollectionUtil.isEmpty(this.dataBeans)) {
                return;
            }
            GiftDataBean giftDataBean = this.dataBeans.get(position - 1);
            LogUtil.e(TAG, "onBindViewHolder: " + position + "  " + holder.getAdapterPosition()
                    + "  " + this.dataBeans.size() + giftDataBean);
            contentHolder.tvFriendName.setText(giftDataBean.getWatchName());
            Glide.with(contentHolder.ivFriendIcon.getContext())
                    .load(giftDataBean.getWatchIcon())
                    .into(contentHolder.ivFriendIcon);
        }
    }

    @Override
    public int getItemCount() {
        if (CollectionUtil.isEmpty(this.dataBeans)) {
            return 2;
        }
        return this.dataBeans.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        if (CollectionUtil.isEmpty(this.dataBeans)) {
            return TYPE_EMPTY;
        }
        return position == getItemCount() - 1 ? TYPE_FOOTER : TYPE_NORMAL;
    }

    public void setGiftList(List<GiftDataBean> giftList) {
        this.dataBeans = giftList;
        notifyDataSetChanged();
    }

    class ItemHeaderHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;

        public ItemHeaderHolder(View itemView) {
            super(itemView);
            this.tvTitle = (TextView) itemView.findViewById(R.id.tv_moment_gift_title);
        }
    }

    class ItemContentHolder extends RecyclerView.ViewHolder {

        private final ImageView ivFriendIcon;
        private final TextView tvFriendName;

        public ItemContentHolder(View itemView) {
            super(itemView);
            this.ivFriendIcon = (ImageView) itemView.findViewById(R.id.moment_gift_friend_icon);
            this.tvFriendName = (TextView) itemView.findViewById(R.id.moment_gift_friend_name);
        }
    }
}