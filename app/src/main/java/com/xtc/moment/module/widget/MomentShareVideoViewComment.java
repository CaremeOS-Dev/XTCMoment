package com.xtc.moment.module.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.bean.ShareVideoMoment;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.utils.encode.JSONUtil;

/**
 * Comment page variant of {@link MomentShareVideoView}.
 */
public class MomentShareVideoViewComment extends MomentVideoView {

    private static final String TAG = "MomentShareVideoViewComment";

    private ImageView ivAppIcon;
    private TextView tvAppName;
    private ImageView videoPlayLogo;
    private MomentContentView mNoSupport;
    private RelativeLayout mRlShareContent;

    public MomentShareVideoViewComment(Context context) {
        super(context);
    }

    public MomentShareVideoViewComment(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MomentShareVideoViewComment(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_share_video_moment_comment, this);
        this.mIcon = (ImageView) view.findViewById(R.id.iv_account_icon);
        this.ivAccountIconBg = (ImageView) view.findViewById(R.id.iv_account_icon_bg);
        this.ivOfficialEnterpriseIcon = (ImageView) view.findViewById(R.id.iv_official_enterprise);
        this.ivOfficialEnterpriseIconBg = (ImageView) view.findViewById(R.id.iv_official_enterprise_bg);
        this.mTvName = (TextView) view.findViewById(R.id.tv_account_name);
        this.ivOfficialLabel = (ImageView) view.findViewById(R.id.iv_official_label);
        this.mContent = (ImageView) view.findViewById(R.id.chat_msg_item_photo_iv);
        this.mContent.setImageResource(R.drawable.ic_selfie_album_default);
        this.rlMomentSender = (RelativeLayout) view.findViewById(R.id.rl_moment_sender);
        this.mIvReport = (ImageView) view.findViewById(R.id.iv_account_report);
        this.videoPlayLogo = (ImageView) view.findViewById(R.id.view_video_moment_video_logo);
        this.mNoSupport = (MomentContentView) view.findViewById(R.id.tv_no_support);
        this.mRlShareContent = (RelativeLayout) view.findViewById(R.id.rl_share_content);
        this.ivAppIcon = (ImageView) view.findViewById(R.id.iv_app_icon);
        this.tvAppName = (TextView) view.findViewById(R.id.tv_app_name);
    }

    @Override
    public void loadDefaultImage(Context context, int resId) {
        super.loadDefaultImage(context, resId);
        this.mContent.setImageResource(resId);
    }

    @Override
    public void loadImage(Context context, DbMoment moment) {
        ShareVideoMoment shareVideoMoment = (ShareVideoMoment) JSONUtil.fromJSON(moment.getContent(), ShareVideoMoment.class);
        if (shareVideoMoment == null) {
            LogUtil.d(TAG, "share image moment is null!");
            this.mNoSupport.setVisibility(VISIBLE);
            this.mRlShareContent.setVisibility(GONE);
            return;
        }
        this.mNoSupport.setVisibility(GONE);
        this.mRlShareContent.setVisibility(VISIBLE);
        if (checkContextIsNull(context)) {
            return;
        }
        super.loadImage(context, moment);
        this.tvAppName.setText(shareVideoMoment.getAppName());
        if (shareVideoMoment.getAppIcon() != null) {
            Glide.with(context).load(shareVideoMoment.getAppIcon())
                    .apply(new RequestOptions().signature(new ObjectKey(String.valueOf(Math.random()))))
                    .into(this.ivAppIcon);
        }
    }

    @Override
    public void setTags(int key, Object value) {
        this.mContent.setTag(key, value);
    }

    @Override
    public Object getTags(int key) {
        return this.mContent.getTag(key);
    }

    @Override
    public void setContentOnClickListener(final Context context, final DbMoment moment,
                                          final AbsMomentView.OnContentOnClickListener listener) {
        if (this.mContent == null) {
            LogUtil.d(TAG, "mContent == null");
            return;
        }
        this.mContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickView(moment, listener);
            }
        });
        this.videoPlayLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickView(moment, listener);
            }
        });
    }

    private void clickView(final DbMoment moment, final AbsMomentView.OnContentOnClickListener listener) {
        LogUtil.d(TAG, "setContentOnClickListener#onClick#momentBean:" + moment);
        if (moment == null) {
            return;
        }
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                if (listener == null || moment.getType().intValue() != 24) {
                    return;
                }
                listener.preVideoView(JSONUtil.toJSON(moment), true);
            }
        });
    }

    @Override
    public void setContentOnLongClickListener(Context context, final DbMoment moment,
                                              final AbsMomentView.OnContentOnLongClickListener listener) {
        if (this.mContent == null) {
            LogUtil.d(TAG, "mContent == null");
            return;
        }
        this.mContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                LogUtil.d(TAG, "setContentOnLongClickListener#onClick#momentBean:" + moment);
                if (listener == null) {
                    return true;
                }
                listener.deleteItem(moment);
                return true;
            }
        });
        this.videoPlayLogo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                LogUtil.d(TAG, "setContentOnLongClickListener#onClick#momentBean:" + moment);
                if (listener == null) {
                    return true;
                }
                listener.deleteItem(moment);
                return true;
            }
        });
    }
}