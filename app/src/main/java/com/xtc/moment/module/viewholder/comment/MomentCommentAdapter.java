package com.xtc.moment.module.viewholder.comment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xtc.moment.R;
import com.xtc.moment.asynclayout.AsyncLayoutLoader;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.utils.ui.DimenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态评论列表适配器。
 */
public class MomentCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MomentCommentAdapter";

    private final int FOOTER_VIEW_TYPE = -100;

    private final LayoutInflater layoutInflater;
    private final AsyncLayoutLoader asyncLayoutLoader = AsyncLayoutLoader.getInstance();
    private final String meString;
    private final String replyString;
    private final String separatorString;

    private Context mContext;
    private DbMoment dbMoment;
    private List<DbMomentComment> dbMomentComments = new ArrayList<>();

    public OnMomentCommentListener listener;
    public OnCommentDeleteListener deleteListener;

    public interface OnMomentCommentListener {
        void onMomentCommentClick(int position, DbMomentComment comment);

        void onLoadMoreCommentClick(int position, DbMoment moment);
    }

    public interface OnCommentDeleteListener {
        void onCommentDeleteClick(int position, DbMomentComment comment);
    }

    public MomentCommentAdapter(Context context) {
        this.mContext = context;
        this.replyString = context.getString(R.string.reply);
        this.separatorString = context.getString(R.string.separator);
        this.meString = context.getString(R.string.me);
        this.layoutInflater = LayoutInflater.from(context);
    }

    public void setDatas(Context context, List<DbMomentComment> comments, String watchId, DbMoment moment) {
        this.dbMoment = moment;
        this.mContext = context;
        List<DbMomentComment> uniqueComments = new ArrayList<>();
        for (DbMomentComment comment : comments) {
            if (!TextUtils.isEmpty(comment.getWatchId()) && watchId.equals(comment.getWatchId())) {
                comment.setWatchName(this.meString);
            }
            if (!TextUtils.isEmpty(comment.getReplyId()) && watchId.equals(comment.getReplyId())) {
                comment.setReplyName(this.meString);
            }
            if (!uniqueComments.contains(comment)) {
                uniqueComments.add(comment);
            }
        }
        this.dbMomentComments = uniqueComments;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommentViewHolder(this.asyncLayoutLoader.inflateView(
                R.layout.item_recycle_moment_item_comment, this.layoutInflater, parent));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == FOOTER_VIEW_TYPE && position > 0) {
            position--;
        }
        DbMomentComment comment = this.dbMomentComments.get(position);
        CommentViewHolder commentHolder = (CommentViewHolder) holder;
        TextView textView = commentHolder.tvComment;
        commentHolder.setDbMomentComments(this.dbMomentComments);
        commentHolder.setComment(comment);
        commentHolder.setOnCommentDeleteListener(this.deleteListener);
        commentHolder.setOnMomentCommentListener(this.listener);
        textView.setPadding(DimenUtil.dp2px(this.mContext, 1.0f), DimenUtil.dp2px(this.mContext, 2.5f),
                DimenUtil.dp2px(this.mContext, 1.0f), DimenUtil.dp2px(this.mContext, 2.5f));
        String watchName = TextUtils.isEmpty(comment.getWatchName())
                ? this.mContext.getString(R.string.unknown_watch)
                : comment.getWatchName();
        if (!TextUtils.isEmpty(comment.getReplyName())) {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            SpannableStringBuilder nameSpan = new SpannableStringBuilder(watchName);
            nameSpan.setSpan(new ForegroundColorSpan(this.mContext.getResources().getColor(R.color.color_888888)),
                    0, nameSpan.length(), 33);
            SpannableStringBuilder replySpan = new SpannableStringBuilder(this.replyString);
            replySpan.setSpan(new ForegroundColorSpan(this.mContext.getResources().getColor(R.color.color_d9d9d9)),
                    0, replySpan.length(), 33);
            SpannableStringBuilder replyNameSpan = new SpannableStringBuilder(comment.getReplyName());
            replyNameSpan.setSpan(new ForegroundColorSpan(this.mContext.getResources().getColor(R.color.color_888888)),
                    0, replyNameSpan.length(), 33);
            SpannableStringBuilder contentSpan = new SpannableStringBuilder(this.separatorString + comment.getComment());
            contentSpan.setSpan(new ForegroundColorSpan(this.mContext.getResources().getColor(R.color.color_d9d9d9)),
                    0, contentSpan.length(), 33);
            textView.setText(builder.append(nameSpan).append(replySpan).append(replyNameSpan).append(contentSpan));
            return;
        }
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableStringBuilder nameSpan = new SpannableStringBuilder(watchName);
        nameSpan.setSpan(new ForegroundColorSpan(this.mContext.getResources().getColor(R.color.color_888888)),
                0, nameSpan.length(), 33);
        SpannableStringBuilder contentSpan = new SpannableStringBuilder(this.separatorString + comment.getComment());
        contentSpan.setSpan(new ForegroundColorSpan(this.mContext.getResources().getColor(R.color.color_d9d9d9)),
                0, contentSpan.length(), 33);
        textView.setText(builder.append(nameSpan).append(contentSpan));
    }

    @Override
    public int getItemCount() {
        return this.dbMomentComments.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void setOnMomentCommentListener(OnMomentCommentListener listener) {
        this.listener = listener;
    }

    public void setOnCommentDeleteListener(OnCommentDeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }
}