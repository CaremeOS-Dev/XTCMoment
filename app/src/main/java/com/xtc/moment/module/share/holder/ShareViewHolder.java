package com.xtc.moment.module.share.holder;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.share.adapter.ShareAdapter;
import com.xtc.moment.module.widget.MainMomentCommentView;
import com.xtc.moment.module.widget.MomentContentView;

/**
 * 纯文本分享动态的 ViewHolder 基类。
 */
public class ShareViewHolder extends AbsViewHolder {

    private static final String TAG = "ShareViewHolder";

    public ShareViewHolder(View view) {
        super(view);
        this.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
        this.tvName = (TextView) view.findViewById(R.id.tv_name);
        this.tvTime = (TextView) view.findViewById(R.id.tv_time);
        this.tvContent = (MomentContentView) view.findViewById(R.id.tv_content);
        this.tvLikes = (TextView) view.findViewById(R.id.tv_likes);
        this.ivBanner = (ImageView) view.findViewById(R.id.iv_banner);
        this.momentCommentView = (MainMomentCommentView) view.findViewById(R.id.moment_comment);
        this.commentRecyclerView = (RecyclerView) view.findViewById(R.id.moment_comment_rec);
        if (this.commentRecyclerView != null) {
            this.commentRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        }
        this.ivShareReportIcon = (ImageView) view.findViewById(R.id.iv_share_account_report);
        if (this.tvLikes != null) {
            this.tvLikes.setHighlightColor(0);
        }
    }

    @Override
    public void setTvContent(String resource, String text) {
        this.tvContent.setContext(getHolderContext());
        this.tvContent.setRichText(resource, text);
    }

    @Override
    public void setTvContent(int resId, String text) {
        this.tvContent.setContext(getHolderContext());
        this.tvContent.setRichText(resId, text);
    }

    @Override
    public void setContentOnLongClickListener(final DbMoment moment, final ShareAdapter.OnContentOnLongClickListener listener) {
        this.tvContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (listener == null || !ShareViewHolder.this.ismIsSelf()) {
                    return true;
                }
                listener.deleteItem(moment);
                return true;
            }
        });
    }
}