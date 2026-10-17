package com.xtc.moment.module.publish.topic;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.net.bean.TopicListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 已选「小括号」横向列表，每项右侧带删除按钮。
 */
public class ShowTopicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "ShowTopicAdapter";

    private List<TopicListBean> topicList = new ArrayList<>();
    private OnDeleteItemListener onDeleteItemListener;

    /** 删除某一项的监听。 */
    public interface OnDeleteItemListener {
        void onDeleteItem(int position);
    }

    public void setData(List<TopicListBean> topicList) {
        this.topicList.clear();
        this.topicList.addAll(topicList);
        notifyDataSetChanged();
    }

    public void setOnDeleteItemListener(OnDeleteItemListener listener) {
        this.onDeleteItemListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TopicViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_show_topic, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        TopicViewHolder viewHolder = (TopicViewHolder) holder;
        TopicListBean topicListBean = this.topicList.get(position);
        if (!TextUtils.isEmpty(topicListBean.getColor())) {
            try {
                viewHolder.tvContent.setTextColor(Color.parseColor(topicListBean.getColor()));
            } catch (Exception e) {
                LogUtil.e(TAG, "话题颜色异常 =", e);
                viewHolder.tvContent.setTextColor(Color.parseColor("#FFFFFFCC"));
            }
        }
        viewHolder.tvContent.setText(topicListBean.getTopicName());
        viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShowTopicAdapter.this.onDeleteItemListener != null) {
                    ShowTopicAdapter.this.onDeleteItemListener.onDeleteItem(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.topicList.size();
    }

    static class TopicViewHolder extends RecyclerView.ViewHolder {

        TextView tvContent;
        ImageView ivDelete;

        TopicViewHolder(View itemView) {
            super(itemView);
            this.tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            this.ivDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
        }
    }
}

