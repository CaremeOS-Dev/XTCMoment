package com.xtc.moment.share.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.xtc.moment.R;

import java.util.List;

/**
 * 多图分享预览的横向封面流适配器。
 */
public class MultiImageShareAdapter extends RecyclerView.Adapter<MultiImageShareAdapter.ViewHolder> {

    private final Context mContext;
    private final List<String> mImagePathList;
    private int mSelectPosition = 0;

    public MultiImageShareAdapter(Context context, List<String> imagePathList) {
        this.mContext = context;
        this.mImagePathList = imagePathList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(this.mContext)
                .inflate(R.layout.item_multi_share_image, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String imagePath = this.mImagePathList.get(position);
        if (TextUtils.isEmpty(imagePath)) {
            return;
        }
        Glide.with(this.mContext).load(imagePath)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA))
                .into(holder.mIvContent);
    }

    @Override
    public int getItemCount() {
        return this.mImagePathList.size();
    }

    public void refreshSelectItem(int position) {
        this.mSelectPosition = position;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mIvContent;
        ImageView mIvContentBg;

        public ViewHolder(View view) {
            super(view);
            this.mIvContent = (ImageView) view.findViewById(R.id.iv_content);
            this.mIvContentBg = (ImageView) view.findViewById(R.id.iv_content_bg);
        }
    }
}