package com.xtc.ui.widget.permission.cta.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xtc.moment.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Lists the CTA dialog rows: an optional title, an optional tip and the
 * permission descriptions.
 */
public class CtaPermissionAdapter extends RecyclerView.Adapter {

    private static final String TAG = "CtaPermissionAdapter";

    public static final int TYPE_ITEM = 1;
    public static final int TYPE_HEAD = 2;
    public static final int TYPE_TIP = 3;

    Context context;
    List<String> data = new ArrayList<String>();
    boolean hasTip;

    public CtaPermissionAdapter(Context context, List<String> permissions, String title, String tip) {
        this.hasTip = false;
        this.context = context;
        this.data.clear();
        this.data.add(title);
        if (TextUtils.isEmpty(tip)) {
            this.hasTip = false;
        } else {
            this.hasTip = true;
            this.data.add(tip);
        }
        this.data.addAll(permissions);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEAD;
        }
        return (this.hasTip && position == 1) ? TYPE_TIP : TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEAD) {
            return new TitleViewHolder(LayoutInflater.from(this.context).inflate(R.layout.item_cta_permission_head, parent, false));
        }
        if (viewType == TYPE_TIP) {
            return new TipViewHolder(LayoutInflater.from(this.context).inflate(R.layout.item_cta_permission_tip, parent, false));
        }
        return new ItemViewHolder(LayoutInflater.from(this.context).inflate(R.layout.item_cta_permission, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (TYPE_ITEM == type) {
            ((ItemViewHolder) holder).tv_desc.setText(this.data.get(position));
            return;
        }
        if (TYPE_HEAD == type) {
            TitleViewHolder titleHolder = (TitleViewHolder) holder;
            titleHolder.tv_title.setText(this.data.get(position));
            titleHolder.tv_title.setTextSize(this.hasTip ? 16.0f : 20.0f);
        } else if (TYPE_TIP == type) {
            ((TipViewHolder) holder).tv_title.setText(this.data.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    class TitleViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;

        public TitleViewHolder(View view) {
            super(view);
            this.tv_title = (TextView) view.findViewById(R.id.tv_title);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tv_desc;

        public ItemViewHolder(View view) {
            super(view);
            this.tv_desc = (TextView) view.findViewById(R.id.tv_desc);
        }
    }

    class TipViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;

        public TipViewHolder(View view) {
            super(view);
            this.tv_title = (TextView) view.findViewById(R.id.tv_title);
        }
    }
}
