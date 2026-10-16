package com.xtc.moment.module.publish.topic;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 推荐「小括号」列表。已选中的条目置灰且不可点击。
 */
public class TopicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "TopicAdapter";

    private final Context context;
    private List<TopicStateBean> topicStateList = new ArrayList<>();
    private OnTopicClickListener onTopicClickListener;

    /** 点击某一项的监听。 */
    public interface OnTopicClickListener {
        void onTopicClick(int position);
    }

    public TopicAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<TopicStateBean> topicStateList) {
        this.topicStateList.clear();
        this.topicStateList.addAll(topicStateList);
        notifyDataSetChanged();
    }

    public void setOnTopicClickListener(OnTopicClickListener listener) {
        this.onTopicClickListener = listener;
    }

    public TopicStateBean getTopicState(int position) {
        return this.topicStateList.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TopicViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_topic, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        TopicViewHolder viewHolder = (TopicViewHolder) holder;
        viewHolder.tvContent.setText(this.topicStateList.get(position).getTopicListBean().getTopicName());
        if (this.topicStateList.get(position).isSelected()) {
            viewHolder.tvContent.setTextColor(this.context.getResources().getColor(R.color.color_80ffffff));
            viewHolder.tvContent.setEnabled(false);
            viewHolder.llRoot.setBackground(this.context.getResources().getDrawable(R.drawable.bg_item_topic));
        } else {
            try {
                viewHolder.tvContent.setTextColor(Color.parseColor(this.topicStateList.get(position).getTopicListBean().getColor()));
            } catch (Exception e) {
                LogUtil.e(TAG, "话题颜色异常 =", e);
                viewHolder.tvContent.setTextColor(this.context.getResources().getColor(R.color.color_FFFFFFCC));
            }
            viewHolder.tvContent.setEnabled(true);
            viewHolder.llRoot.setBackground(this.context.getResources().getDrawable(R.drawable.bg_item_topic));
        }
        viewHolder.tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TopicAdapter.this.onTopicClickListener != null) {
                    TopicAdapter.this.onTopicClickListener.onTopicClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.topicStateList.size();
    }

    static class TopicViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llRoot;
        TextView tvContent;

        TopicViewHolder(View itemView) {
            super(itemView);
            this.tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            this.llRoot = (LinearLayout) itemView.findViewById(R.id.ll_root);
        }
    }
}

