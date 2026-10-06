package com.xtc.moment.module.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.bean.ShareImageMoment;
import com.xtc.utils.encode.JSONUtil;

/**
 * Comment page variant of {@link MomentShareImageView}.
 */
public class MomentShareImageViewComment extends MomentPhotoView {

    private static final String TAG = MomentShareImageViewComment.class.getSimpleName();

    private ImageView ivAppIcon;
    private TextView tvAppName;
    private MomentContentView mNoSupport;
    private RelativeLayout mRlShareContent;

    public MomentShareImageViewComment(Context context) {
        this(context, null);
    }

    public MomentShareImageViewComment(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MomentShareImageViewComment(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initView() {
        this.rootView = LayoutInflater.from(getContext()).inflate(R.layout.view_share_photo_moment_comment, this);
        this.mIcon = (ImageView) this.rootView.findViewById(R.id.iv_account_icon);
        this.ivAccountIconBg = (ImageView) this.rootView.findViewById(R.id.iv_account_icon_bg);
        this.ivOfficialEnterpriseIcon = (ImageView) this.rootView.findViewById(R.id.iv_official_enterprise);
        this.ivOfficialEnterpriseIconBg = (ImageView) this.rootView.findViewById(R.id.iv_official_enterprise_bg);
        this.mTvName = (TextView) this.rootView.findViewById(R.id.tv_account_name);
        this.ivOfficialLabel = (ImageView) this.rootView.findViewById(R.id.iv_official_label);
        this.mContent = (ImageView) this.rootView.findViewById(R.id.chat_msg_item_photo_iv);
        this.mContent.setImageResource(R.drawable.ic_selfie_album_default);
        this.rlMomentSender = (RelativeLayout) this.rootView.findViewById(R.id.rl_moment_sender);
        this.ivAppIcon = (ImageView) this.rootView.findViewById(R.id.iv_app_icon);
        this.tvAppName = (TextView) this.rootView.findViewById(R.id.tv_app_name);
        this.mNoSupport = (MomentContentView) this.rootView.findViewById(R.id.tv_no_support);
        this.mRlShareContent = (RelativeLayout) this.rootView.findViewById(R.id.rl_share_content);
        this.mIvReport = (ImageView) this.rootView.findViewById(R.id.iv_account_report);
    }

    @Override
    public void loadImage(Context context, DbMoment moment) {
        ShareImageMoment shareImageMoment = (ShareImageMoment) JSONUtil.fromJSON(moment.getContent(), ShareImageMoment.class);
        if (shareImageMoment == null) {
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
        this.tvAppName.setText(shareImageMoment.getAppName());
        if (shareImageMoment.getAppIcon() != null) {
            Glide.with(context).load(shareImageMoment.getAppIcon())
                    .apply(new RequestOptions().signature(new ObjectKey(String.valueOf(Math.random()))))
                    .into(this.ivAppIcon);
        }
    }

    @Override
    public void setContentSize(long textSize) {
        this.mTvName.setTextSize(textSize);
    }
}