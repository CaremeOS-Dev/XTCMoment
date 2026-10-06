package com.xtc.moment.module.share.holder;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.bean.ShareAppMoment;
import com.xtc.moment.module.share.adapter.ShareAdapter;
import com.xtc.moment.module.widget.MainMomentCommentView;
import com.xtc.moment.module.widget.MomentContentView;
import com.xtc.moment.util.ShareAppStartUtil;
import com.xtc.utils.encode.JSONUtil;

/**
 * 分享应用动态的 ViewHolder，点击整体跳转回来源应用。
 */
public class ShareAppViewHolder extends PhotoViewHolder {

    private static final String TAG = "ShareAppViewHolder";

    ViewGroup rlShareContent;
    TextView tvAppName;
    TextView tvDesc;
    MomentContentView tvNoSupport;

    public ShareAppViewHolder(View view) {
        super(view);
        this.tvAppName = (TextView) view.findViewById(R.id.tv_app_name);
        this.tvDesc = (TextView) view.findViewById(R.id.tv_desc);
        this.momentCommentView = (MainMomentCommentView) view.findViewById(R.id.moment_comment);
        this.commentRecyclerView = (RecyclerView) view.findViewById(R.id.moment_comment_rec);
        this.commentRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        this.tvNoSupport = (MomentContentView) view.findViewById(R.id.tv_no_support);
        this.rlShareContent = (ViewGroup) view.findViewById(R.id.rl_share_content);
        this.ivShareReportIcon = (ImageView) view.findViewById(R.id.iv_share_account_report);
    }

    @Override
    public void loadImage(Context context, DbMoment moment, AbsViewHolder holder) {
        ShareAppMoment shareAppMoment = JSONUtil.fromJSON(moment.getContent(), ShareAppMoment.class);
        if (shareAppMoment == null) {
            this.tvNoSupport.setVisibility(View.VISIBLE);
            this.rlShareContent.setVisibility(View.GONE);
            return;
        }
        this.tvNoSupport.setVisibility(View.GONE);
        this.rlShareContent.setVisibility(View.VISIBLE);
        super.loadImage(context, moment, holder);
        this.tvAppName.setText(shareAppMoment.getAppName());
        this.tvDesc.setText(shareAppMoment.getDesc());
    }

    @Override
    public void setContentOnClickListener(final DbMoment moment, ShareAdapter.OnContentOnClickListener listener) {
        if (moment == null) {
            LogUtil.d(TAG, "click share app, moment bean is null! ");
            return;
        }
        this.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareAppStartUtil.startApp(ShareAppViewHolder.this.getHolderContext(), moment);
            }
        });
    }

    @Override
    public void setContentOnLongClickListener(final DbMoment moment, final ShareAdapter.OnContentOnLongClickListener listener) {
        this.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (listener == null || !ShareAppViewHolder.this.ismIsSelf()) {
                    return true;
                }
                listener.deleteItem(moment);
                return true;
            }
        });
    }
}