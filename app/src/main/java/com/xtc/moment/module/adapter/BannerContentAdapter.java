package com.xtc.moment.module.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.module.illegal.net.bean.request.BannerNetBean;
import com.xtc.moment.module.widget.GlideRoundImageView;
import com.xtc.utils.common.CollectionUtil;

import java.util.List;

/**
 * 违规详情中敏感内容列表的适配器，支持文本、图片与视频缩略图。
 */
public class BannerContentAdapter extends RecyclerView.Adapter<BannerContentAdapter.ContentHolder> {

    private static final String TAG = "BannerContentAdapter";
    private static final int TYPE_TEXT = 1;
    private static final int TYPE_PHOTO = 2;
    private static final int TYPE_VIDEO = 3;

    private Context context;
    private List<BannerNetBean.BannerContentBent> bannerNetBeans;

    public BannerContentAdapter(List<BannerNetBean.BannerContentBent> bannerNetBeans, Context context) {
        this.bannerNetBeans = bannerNetBeans;
        this.context = context;
    }

    @Override
    public ContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContentHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner_content, parent, false));
    }

    @Override
    public void onBindViewHolder(final ContentHolder holder, int position) {
        BannerNetBean.BannerContentBent contentBean = this.bannerNetBeans.get(position);
        holder.tvTime.setText(contentBean.getLongDate()
                + this.context.getResources().getString(R.string.publish));
        LogUtil.d(TAG, "contentBenType: " + contentBean.getType());
        LogUtil.d(TAG, "contentBenString: " + contentBean.getLongDate());
        LogUtil.d(TAG, "contentBenContent: " + contentBean.getContent());
        if (contentBean.getType() == TYPE_TEXT) {
            holder.tvContent.setVisibility(View.VISIBLE);
            holder.rlImageLayout.setVisibility(View.GONE);
            holder.tvContent.setText(contentBean.getContent());
            return;
        }
        holder.ivVideoIcon.setVisibility(contentBean.getType() == TYPE_VIDEO ? View.VISIBLE : View.GONE);
        holder.tvContent.setVisibility(View.GONE);
        holder.rlImageLayout.setVisibility(View.VISIBLE);
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_selfie_album_default)
                .error(R.drawable.ic_selfie_album_default)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .override(holder.ivContent.getWidth(), holder.ivContent.getHeight())
                .transform(new GlideRoundImageView(this.context, 4, 0))
                .dontAnimate()
                .signature(new ObjectKey(String.valueOf(Math.random())));
        Context currentContext = this.context;
        if (!(currentContext instanceof Activity)
                || ((Activity) currentContext).isDestroyed() || ((Activity) this.context).isFinishing()) {
            LogUtil.i(TAG, "video set bg error");
            return;
        }
        Glide.with(this.context).load(contentBean.getContent()).apply(requestOptions)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        holder.ivContent.setImageDrawable(resource);
                    }

                    @Override
                    public void onLoadFailed(Drawable errorDrawable) {
                        LogUtil.e(TAG, "photo loading error：" + errorDrawable);
                    }
                });
    }

    @Override
    public int getItemCount() {
        List<BannerNetBean.BannerContentBent> data = this.bannerNetBeans;
        return data == null ? 0 : data.size();
    }

    public void addDatas(List<BannerNetBean.BannerContentBent> newData) {
        LogUtil.i(TAG, "addDatasm = " + newData);
        if (CollectionUtil.isEmpty(newData) || CollectionUtil.isEmpty(this.bannerNetBeans)) {
            return;
        }
        int size = this.bannerNetBeans.size();
        this.bannerNetBeans.addAll(newData);
        notifyItemRangeChanged(size + 1, newData.size());
    }

    public class ContentHolder extends RecyclerView.ViewHolder {

        private final TextView tvTime;
        private final TextView tvContent;
        private final ImageView ivContent;
        private final ImageView ivVideoIcon;
        private final RelativeLayout rlImageLayout;

        public ContentHolder(View itemView) {
            super(itemView);
            this.tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            this.tvContent = (TextView) itemView.findViewById(R.id.tvContent);
            this.ivContent = (ImageView) itemView.findViewById(R.id.ivContent);
            this.ivVideoIcon = (ImageView) itemView.findViewById(R.id.video_icon);
            this.rlImageLayout = (RelativeLayout) itemView.findViewById(R.id.rlImageLayout);
        }
    }
}