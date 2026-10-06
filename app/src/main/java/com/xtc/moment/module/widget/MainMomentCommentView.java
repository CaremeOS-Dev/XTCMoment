package com.xtc.moment.module.widget;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtc.bigdata.collector.utils.CollectionUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.module.details.MomentDetailsActivity;
import com.xtc.moment.module.viewholder.comment.MomentCommentAdapter;
import com.xtc.moment.util.SystemUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.ui.DimenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Comment summary shown on the moment list card: up to five comment lines plus a "show more" row.
 */
public class MainMomentCommentView extends LinearLayout implements View.OnClickListener,
        View.OnLongClickListener {

    private static final String TAG = "MainMomentCommentView";

    /** Number of comment lines shown before the summary collapses. */
    private static final int MAX_VISIBLE_COMMENTS = 5;

    private final SpannableStringBuilder commentBuilder = new SpannableStringBuilder();
    private final SpannableStringBuilder watchNameBuilder = new SpannableStringBuilder();
    private final SpannableStringBuilder replyStringBuilder = new SpannableStringBuilder();
    private final SpannableStringBuilder replyWatchNameBuilder = new SpannableStringBuilder();
    private final SpannableStringBuilder commentText = new SpannableStringBuilder();

    private TextView tvComment1;
    private TextView tvComment2;
    private TextView tvComment3;
    private TextView tvComment4;
    private TextView tvComment5;
    private TextView tvUnfoldComment;

    private Context mContext;
    private DbMoment dbMoment;
    private List<DbMomentComment> momentComments;
    private boolean isLoadMoreComment;

    private MomentCommentAdapter.OnMomentCommentListener listener;
    private MomentCommentAdapter.OnCommentDeleteListener deleteListener;

    public MainMomentCommentView(Context context) {
        super(context);
        initView(context);
    }

    public MainMomentCommentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MainMomentCommentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        setOrientation(VERTICAL);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_comment_view, this);
        this.tvComment1 = (TextView) view.findViewById(R.id.tv_comment_view_1);
        this.tvComment2 = (TextView) view.findViewById(R.id.tv_comment_view_2);
        this.tvComment3 = (TextView) view.findViewById(R.id.tv_comment_view_3);
        this.tvComment4 = (TextView) view.findViewById(R.id.tv_comment_view_4);
        this.tvComment5 = (TextView) view.findViewById(R.id.tv_comment_view_5);
        this.tvComment1.setOnClickListener(this);
        this.tvComment2.setOnClickListener(this);
        this.tvComment3.setOnClickListener(this);
        this.tvComment4.setOnClickListener(this);
        this.tvComment5.setOnClickListener(this);
        this.tvComment1.setOnLongClickListener(this);
        this.tvComment2.setOnLongClickListener(this);
        this.tvComment3.setOnLongClickListener(this);
        this.tvComment4.setOnLongClickListener(this);
        this.tvComment5.setOnLongClickListener(this);
        this.tvUnfoldComment = (TextView) view.findViewById(R.id.tv_comment_view_last);
        this.tvUnfoldComment.setOnClickListener(this);
        setTvPadding(context, this.tvComment1);
        setTvPadding(context, this.tvComment2);
        setTvPadding(context, this.tvComment3);
        setTvPadding(context, this.tvComment4);
        setTvPadding(context, this.tvComment5);
        setTvPadding(context, this.tvUnfoldComment);
    }

    private void setTvPadding(Context context, TextView textView) {
        textView.setPadding(DimenUtil.dp2px(context, 1.0f), DimenUtil.dp2px(context, 2.5f),
                DimenUtil.dp2px(context, 1.0f), DimenUtil.dp2px(context, 2.5f));
    }

    /** Binds the comments of a moment; {@code selfWatchId} marks the user's own comments. */
    public void setDatas(Context context, List<DbMomentComment> comments, String selfWatchId, DbMoment moment) {
        this.mContext = context;
        this.dbMoment = moment;
        this.momentComments = new ArrayList<DbMomentComment>();
        this.isLoadMoreComment = false;
        hideAllComments();
        LogUtil.d(TAG, "setDatas: commentSize = " + (comments == null ? 0 : comments.size())
                + ";   commentsTotalCount = " + moment.getCommentsTotalCount());
        if (CollectionUtil.isEmpty(comments)) {
            if (moment.getCommentsTotalCount() > 0) {
                this.isLoadMoreComment = true;
                setUnfoldCount(context, moment);
            }
            return;
        }
        for (int i = 0; i < comments.size() && i != MAX_VISIBLE_COMMENTS; i++) {
            DbMomentComment comment = comments.get(i);
            if (!TextUtils.isEmpty(comment.getWatchId()) && selfWatchId.equals(comment.getWatchId())) {
                comment.setWatchName(context.getString(R.string.me));
            }
            if (!TextUtils.isEmpty(comment.getReplyId()) && selfWatchId.equals(comment.getReplyId())) {
                comment.setReplyName(context.getString(R.string.me));
            }
            this.momentComments.add(comment);
            if (i == 0) {
                setCommentText(this.tvComment1, context, comment);
            } else if (i == 1) {
                setCommentText(this.tvComment2, context, comment);
            } else if (i == 2) {
                setCommentText(this.tvComment3, context, comment);
            } else if (i == 3) {
                setCommentText(this.tvComment4, context, comment);
            } else if (i == 4) {
                setCommentText(this.tvComment5, context, comment);
            }
        }
        if (moment.getCommentsTotalCount() > MAX_VISIBLE_COMMENTS) {
            setUnfoldCount(context, moment);
        } else if (comments.size() == 0 || comments.size() > moment.getCommentsTotalCount()) {
            setUnfoldCount(context, moment);
        } else {
            this.tvUnfoldComment.setVisibility(GONE);
        }
    }

    private void setUnfoldCount(Context context, DbMoment moment) {
        int remaining = moment.getCommentsTotalCount() - MAX_VISIBLE_COMMENTS;
        if (remaining <= 99) {
            String text = remaining + "";
            LogUtil.d(TAG, "setUnfoldCount: " + text);
        }
        this.tvUnfoldComment.setVisibility(VISIBLE);
        this.tvUnfoldComment.setText(context.getString(R.string.unfold_other_comment));
    }

    private void hideAllComments() {
        this.tvComment1.setVisibility(GONE);
        this.tvComment2.setVisibility(GONE);
        this.tvComment3.setVisibility(GONE);
        this.tvComment4.setVisibility(GONE);
        this.tvComment5.setVisibility(GONE);
    }

    /** Renders one comment line with the coloured sender and reply names. */
    public void setCommentText(TextView textView, Context context, DbMomentComment comment) {
        clearBuilder();
        textView.setTag(comment.getCommentId());
        textView.setVisibility(VISIBLE);
        String watchName = TextUtils.isEmpty(comment.getWatchName())
                ? context.getString(R.string.unknown_watch) : comment.getWatchName();
        if (!TextUtils.isEmpty(comment.getReplyName())) {
            this.watchNameBuilder.append(watchName);
            this.watchNameBuilder.setSpan(new ForegroundColorSpan(
                            context.getResources().getColor(R.color.color_888888)),
                    0, this.watchNameBuilder.length(), 33);
            this.replyStringBuilder.append(context.getString(R.string.reply));
            this.replyStringBuilder.setSpan(new ForegroundColorSpan(
                            context.getResources().getColor(R.color.color_d9d9d9)),
                    0, this.replyStringBuilder.length(), 33);
            this.replyWatchNameBuilder.append(comment.getReplyName());
            this.replyWatchNameBuilder.setSpan(new ForegroundColorSpan(
                            context.getResources().getColor(R.color.color_888888)),
                    0, this.replyWatchNameBuilder.length(), 33);
            this.commentText.append(context.getString(R.string.separator)).append(comment.getComment());
            this.commentText.setSpan(new ForegroundColorSpan(
                            context.getResources().getColor(R.color.color_d9d9d9)),
                    0, this.commentText.length(), 33);
            textView.setText(this.commentBuilder.append(this.watchNameBuilder)
                    .append(this.replyStringBuilder).append(this.replyWatchNameBuilder)
                    .append(this.commentText));
            return;
        }
        this.watchNameBuilder.append(watchName);
        this.watchNameBuilder.setSpan(new ForegroundColorSpan(
                        context.getResources().getColor(R.color.color_888888)),
                0, this.watchNameBuilder.length(), 33);
        this.commentText.append(context.getString(R.string.separator)).append(comment.getComment());
        this.commentText.setSpan(new ForegroundColorSpan(
                        context.getResources().getColor(R.color.color_d9d9d9)),
                0, this.commentText.length(), 33);
        textView.setText(this.commentBuilder.append(this.watchNameBuilder).append(this.commentText));
    }

    private void clearBuilder() {
        this.commentBuilder.clear();
        this.watchNameBuilder.clear();
        this.replyStringBuilder.clear();
        this.replyWatchNameBuilder.clear();
        this.commentText.clear();
    }

    @Override
    public void onClick(View view) {
        if (SystemUtil.isFastDoubleClick()) {
            LogUtil.i(TAG, "onClick: click too fast.");
            return;
        }
        if (view.getId() == R.id.tv_comment_view_last) {
            if (this.isLoadMoreComment) {
                if (!SystemUtil.isFastLongDoubleClick()) {
                    this.listener.onLoadMoreCommentClick(0, this.dbMoment);
                }
                this.isLoadMoreComment = false;
                return;
            }
            if (this.dbMoment == null || this.mContext == null) {
                return;
            }
            Intent intent = new Intent(this.mContext, MomentDetailsActivity.class);
            intent.putExtra("momentId", this.dbMoment.getMomentId());
            intent.putExtra(MomentDetailsActivity.INTENT_MOMENT_KEY, JSONUtil.toJSON(this.dbMoment));
            intent.putExtra("watchId", this.dbMoment.getWatchId());
            this.mContext.startActivity(intent);
            return;
        }
        if (this.listener == null || this.momentComments == null || this.momentComments.isEmpty()) {
            return;
        }
        TextView textView = (TextView) view;
        for (int i = 0; i < this.momentComments.size(); i++) {
            if (textView.getTag().equals(this.momentComments.get(i).getCommentId())) {
                this.listener.onMomentCommentClick(i, this.momentComments.get(i));
            }
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (this.deleteListener == null || this.momentComments == null || this.momentComments.isEmpty()) {
            return false;
        }
        TextView textView = (TextView) view;
        LogUtil.d(TAG, textView.getText() + "onLongClick: " + this.momentComments);
        for (int i = 0; i < this.momentComments.size(); i++) {
            if (textView.getTag().equals(this.momentComments.get(i).getCommentId())) {
                this.deleteListener.onCommentDeleteClick(i, this.momentComments.get(i));
            }
        }
        return true;
    }

    public void setOnMomentCommentListener(MomentCommentAdapter.OnMomentCommentListener listener) {
        this.listener = listener;
    }

    public void setOnCommentDeleteListener(MomentCommentAdapter.OnCommentDeleteListener listener) {
        this.deleteListener = listener;
    }

    public void refreshVisible(int permissionType) {
        if (this.dbMoment == null) {
            return;
        }
        this.dbMoment.setPermissionType(permissionType);
    }
}