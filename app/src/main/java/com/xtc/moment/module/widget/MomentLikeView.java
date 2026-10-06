package com.xtc.moment.module.widget;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.prerogative.DbMomentPrerogativeLike;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.illegal.handler.IllegalMessageHandler;
import com.xtc.moment.module.like.MomentLikesActivity;
import com.xtc.moment.prerogative.IPrerogativeServe;
import com.xtc.moment.prerogative.MomentPrerogativeServeImpl;

/**
 * 动态点赞按钮视图。
 */
public class MomentLikeView extends RelativeLayout {

    private static final String TAG = "MomentLikeView";

    private ImageView mIvLike;
    private TextView mTvLikeCount;
    private int color1;
    private int color2;
    private int usingLikeResId = -1;
    private IPrerogativeServe instance;
    private int currentUseLikeEmotionId;
    private DbMomentPrerogativeLike likeByEmotionId;

    public MomentLikeView(Context context) {
        this(context, null);
    }

    public MomentLikeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MomentLikeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View content = LayoutInflater.from(getContext()).inflate(R.layout.view_moment_like, this);
        this.mIvLike = (ImageView) content.findViewById(R.id.iv_like);
        this.mTvLikeCount = (TextView) content.findViewById(R.id.tv_like_count);
        this.color1 = content.getResources().getColor(R.color.tv_like_text_color);
        this.color2 = content.getResources().getColor(R.color.tv_like_text_color_2);
        this.instance = MomentPrerogativeServeImpl.getInstance(getContext().getApplicationContext());
        this.currentUseLikeEmotionId = this.instance.getCurrentUseLikeEmotionId();
        this.likeByEmotionId = this.instance.getPrerogativeLikeByEmotionId(this.currentUseLikeEmotionId);
    }

    public void setLike(Context context, boolean liked) {
        IPrerogativeServe serve = this.instance;
        if (serve != null && this.currentUseLikeEmotionId != serve.getCurrentUseLikeEmotionId()) {
            this.currentUseLikeEmotionId = this.instance.getCurrentUseLikeEmotionId();
            this.likeByEmotionId = this.instance.getPrerogativeLikeByEmotionId(this.currentUseLikeEmotionId);
        }
        if (this.likeByEmotionId == null) {
            this.currentUseLikeEmotionId = 0;
        }
        int likeResId = R.drawable.circle_like_selectedl_not;
        if (this.currentUseLikeEmotionId == 0) {
            if (!IllegalMessageHandler.getInstance(getContext()).isDisableSend()) {
                likeResId = liked ? R.drawable.circle_like_pressed : R.drawable.circle_like_normal;
            } else if (!liked) {
                likeResId = R.drawable.circle_like_normal_not;
            }
            setLikeRes(likeResId);
        } else {
            String praisedPic;
            if (IllegalMessageHandler.getInstance(getContext()).isDisableSend()) {
                if (!liked) {
                    likeResId = R.drawable.circle_like_normal_not;
                }
                praisedPic = this.likeByEmotionId.getPraisedDisablePic();
            } else {
                likeResId = liked ? R.drawable.circle_like_pressed : R.drawable.circle_like_normal;
                praisedPic = this.likeByEmotionId.getPraisedPic();
            }
            if (liked || TextUtils.isEmpty(praisedPic)) {
                Glide.with(context).load(praisedPic)
                        .apply(new RequestOptions().fitCenter().diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(likeResId))
                        .into(this.mIvLike);
                this.usingLikeResId = -1;
            } else {
                setLikeRes(likeResId);
            }
        }
        this.mTvLikeCount.setTextColor(liked ? this.color1 : this.color2);
    }

    private void setLikeRes(int resId) {
        if (this.usingLikeResId == resId) {
            return;
        }
        this.usingLikeResId = resId;
        this.mIvLike.setImageResource(this.usingLikeResId);
    }

    public void setCount(int count) {
        this.mTvLikeCount.setText(String.valueOf(count));
    }

    public void startMomentLikesActivity(DbMoment moment, Context context) {
        Intent intent = new Intent(getContext(), MomentLikesActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_WATCH_ID, moment.getWatchId());
        intent.putExtra(Constants.INTENT_EXTRA_LIKE_TOTAL, moment.getLikeTotal());
        intent.putExtra(Constants.INTENT_EXTRA_MOMENT_ID, moment.getMomentId());
        context.startActivity(intent);
    }
}