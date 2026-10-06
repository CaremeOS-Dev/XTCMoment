package com.xtc.moment.module.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.xtc.log.LogUtil;
import com.xtc.log.util.TextUtils;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.prerogative.DbMomentPrerogativeLike;
import com.xtc.moment.module.bean.MomentNewMsgBean;
import com.xtc.moment.prerogative.MomentPrerogativeServeImpl;
import com.xtc.moment.util.LanguageUtils;
import com.xtc.moment.util.TimeUtils;
import com.xtc.utils.ui.DimenUtil;

/**
 * 点赞/评论消息展示视图。
 */
public class CommentPraiseView extends FrameLayout {

    private static final String TAG = "CommentPraiseView";
    private static final int DOMESTIC_MAX_WIDTH = 120;
    private static final int FOREIGN_MAX_WIDTH = 70;

    public static final int TYPE_PRAISE = 1;
    public static final int TYPE_COMMENT = 2;

    private TextView tvAccountName;
    private TextView tvTime;
    private TextView tvCommentTime;
    private TextView tvComment;
    private TextView tvCommentKey;
    private ImageView ivLike;
    private View viewPlaceHolder;

    public CommentPraiseView(Context context) {
        this(context, null);
    }

    public CommentPraiseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentPraiseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View content = LayoutInflater.from(getContext()).inflate(R.layout.view_comment_praise, this);
        this.tvAccountName = (TextView) content.findViewById(R.id.tv_account_name);
        this.tvTime = (TextView) content.findViewById(R.id.tv_time);
        this.tvCommentTime = (TextView) content.findViewById(R.id.tv_comment_time);
        this.tvComment = (TextView) content.findViewById(R.id.tv_comment);
        this.tvCommentKey = (TextView) content.findViewById(R.id.tv_comment_key);
        this.ivLike = (ImageView) content.findViewById(R.id.iv_like);
        this.viewPlaceHolder = content.findViewById(R.id.view_placeholder);
    }

    public void setCommentType(int type, Drawable likeDrawable, MomentNewMsgBean message) {
        if (TYPE_PRAISE == type) {
            this.tvCommentKey.setVisibility(GONE);
            this.tvComment.setVisibility(GONE);
            this.ivLike.setVisibility(VISIBLE);
            if (message.getEmotionId() == 0) {
                this.ivLike.setBackground(likeDrawable);
            } else {
                DbMomentPrerogativeLike prerogativeLike = MomentPrerogativeServeImpl
                        .getInstance(getContext().getApplicationContext())
                        .getPrerogativeLikeByEmotionId(message.getEmotionId());
                if (prerogativeLike == null) {
                    LogUtil.i(TAG, "DbMomentPrerogativeLike is null");
                    return;
                }
                Glide.with(getContext()).load(prerogativeLike.getPraisedPic())
                        .apply(new RequestOptions().error(likeDrawable).diskCacheStrategy(DiskCacheStrategy.ALL)
                                .fallback(likeDrawable))
                        .into(this.ivLike);
            }
            this.viewPlaceHolder.setVisibility(VISIBLE);
        } else {
            this.tvCommentKey.setVisibility(VISIBLE);
            this.tvComment.setVisibility(VISIBLE);
            this.ivLike.setVisibility(GONE);
        }
        if (LanguageUtils.isZhCN(this.tvAccountName.getContext())
                || LanguageUtils.isZhHK(this.tvAccountName.getContext())) {
            this.tvAccountName.setMaxWidth(DimenUtil.dp2px(this.tvAccountName.getContext(), DOMESTIC_MAX_WIDTH));
        } else if (TYPE_PRAISE == type) {
            this.tvAccountName.setMaxWidth(DimenUtil.dp2px(this.tvAccountName.getContext(), DOMESTIC_MAX_WIDTH));
        } else {
            this.tvAccountName.setMaxWidth(DimenUtil.dp2px(this.tvAccountName.getContext(), FOREIGN_MAX_WIDTH));
        }
        this.tvTime.setVisibility(GONE);
        this.tvCommentTime.setVisibility(VISIBLE);
        this.tvCommentTime.setText(this.tvTime.getText());
    }

    public void setAccountName(String accountName) {
        if (TextUtils.isEmpty(accountName)) {
            return;
        }
        this.tvAccountName.setText(accountName);
    }

    public void setTime(Context context, long time) {
        setTvTime(context, time);
    }

    void setTvTime(Context context, long time) {
        this.tvTime.setText(TimeUtils.getNewMsgTime(context, time));
    }

    public void setComment(String comment) {
        if (TextUtils.isEmpty(comment)) {
            return;
        }
        this.tvComment.setText(comment);
    }
}