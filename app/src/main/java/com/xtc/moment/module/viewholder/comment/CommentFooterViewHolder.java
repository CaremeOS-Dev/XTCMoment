package com.xtc.moment.module.viewholder.comment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.module.details.MomentDetailsActivity;
import com.xtc.utils.encode.JSONUtil;

/**
 * 评论列表“查看更多”页脚。
 */
public class CommentFooterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private static final String TAG = "CommentFooterViewHolder";

    public final TextView tvFooter;

    private Context mContext;
    private DbMomentComment comment;
    private DbMoment dbMoment;

    public CommentFooterViewHolder(View itemView) {
        super(itemView);
        this.tvFooter = (TextView) itemView.findViewById(R.id.item_comment_footer_text);
        itemView.findViewById(R.id.item_comment_footer_parent).setOnClickListener(this);
    }

    public void setComment(Context context, DbMomentComment comment) {
        this.mContext = context;
        this.comment = comment;
    }

    public void setDbMoment(DbMoment moment) {
        this.dbMoment = moment;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this.mContext, MomentDetailsActivity.class);
        intent.putExtra("momentId", this.comment.getMomentId());
        intent.putExtra(MomentDetailsActivity.INTENT_MOMENT_KEY, JSONUtil.toJSON(this.dbMoment));
        intent.putExtra("watchId", this.comment.getMomentWatchId());
        this.mContext.startActivity(intent);
    }
}