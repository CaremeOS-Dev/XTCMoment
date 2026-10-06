package com.xtc.moment.module.community;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.Preconditions;
import com.xtc.bigdata.collector.utils.CollectionUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.net.bean.CommunityConversationResponse;
import com.xtc.moment.util.ClickUtils;

/**
 * 社区会话（章节）列表适配器，第一项为带图片的头部。
 */
public class CommunityConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "CommunityConversationAdapter";
    private static final int HEAD = 1;
    private static final int BODY = 2;

    private Context mContext;
    private CommunityConversationResponse mCommunityConversationResponse;
    private OnItemClickListener mOnItemClickListener;
    private final RequestOptions options = new RequestOptions()
            .error(R.drawable.loading)
            .placeholder(R.drawable.loading);

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public int getHeadViewCount() {
        return 1;
    }

    public CommunityConversationAdapter(Context context,
            CommunityConversationResponse communityConversationResponse) {
        this.mContext = Preconditions.checkNotNull(context);
        this.mCommunityConversationResponse = communityConversationResponse;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == HEAD) {
            return new HeadViewHolder(inflater.inflate(R.layout.item_community_conversation_head, parent, false),
                    this.mCommunityConversationResponse);
        }
        return new MyViewHolder(inflater.inflate(R.layout.item_community_conversation, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).setData(
                    this.mCommunityConversationResponse.getCatalog().get(position - getHeadViewCount()));
        }
    }

    @Override
    public int getItemCount() {
        if (CollectionUtil.isEmpty(this.mCommunityConversationResponse.getCatalog())) {
            return getHeadViewCount();
        }
        return this.mCommunityConversationResponse.getCatalog().size() + getHeadViewCount();
    }

    @Override
    public int getItemViewType(int position) {
        return (position != 0 || getHeadViewCount() <= 0) ? BODY : HEAD;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public class HeadViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_community;
        private TextView tv_content;

        public HeadViewHolder(View itemView, CommunityConversationResponse response) {
            super(itemView);
            this.iv_community = (ImageView) itemView.findViewById(R.id.iv_community);
            this.tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            this.tv_content.setText(response.getContent());
            Glide.with(CommunityConversationAdapter.this.mContext).asGif()
                    .load(response.getImageUrl())
                    .apply(CommunityConversationAdapter.this.options)
                    .into(this.iv_community);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvTitle;
        private TextView mTvContent;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            this.mTvContent = (TextView) itemView.findViewById(R.id.tv_content);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ClickUtils.isFastClick()) {
                        if (CommunityConversationAdapter.this.mOnItemClickListener == null) {
                            return;
                        }
                        CommunityConversationAdapter.this.mOnItemClickListener
                                .onItemClick(MyViewHolder.this.getAdapterPosition());
                        return;
                    }
                    LogUtil.d(TAG, "click too fast");
                }
            });
        }

        public void setData(CommunityConversationResponse.CatalogBean catalogBean) {
            if (catalogBean == null) {
                return;
            }
            this.mTvTitle.setText(catalogBean.getChapter());
            this.mTvContent.setText(catalogBean.getChapterTitle());
        }
    }
}