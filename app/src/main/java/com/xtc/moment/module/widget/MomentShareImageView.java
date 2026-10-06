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
import com.xtc.moment.util.HandlerUtil;
import com.xtc.utils.encode.JSONUtil;

/**
 * Row for an image that was shared into moments from another app.
 */
public class MomentShareImageView extends MomentPhotoView {

    private final String tag = MomentShareImageView.class.getSimpleName();

    private ImageView ivAppIcon;
    private TextView tvAppName;
    private MomentContentView mNoSupport;
    private RelativeLayout mRlShareContent;

    public MomentShareImageView(Context context) {
        this(context, null);
    }

    public MomentShareImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MomentShareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initView() {
        this.rootView = LayoutInflater.from(getContext()).inflate(R.layout.view_share_photo_moment, this);
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
            LogUtil.d(this.tag, "share image moment is null!");
            showNoSupportView(true, null);
            return;
        }
        if (checkContextIsNull(context)) {
            return;
        }
        showNoSupportView(false, shareImageMoment);
        super.loadImage(context, moment);
    }

    private void showNoSupportView(final boolean noSupport, final ShareImageMoment shareImageMoment) {
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (noSupport) {
                    mNoSupport.setVisibility(VISIBLE);
                    mRlShareContent.setVisibility(GONE);
                } else if (shareImageMoment != null) {
                    mNoSupport.setVisibility(GONE);
                    mRlShareContent.setVisibility(VISIBLE);
                    tvAppName.setText(shareImageMoment.getAppName());
                    if (shareImageMoment.getAppIcon() != null) {
                        Glide.with(getMyContext()).load(shareImageMoment.getAppIcon())
                                .apply(new RequestOptions().signature(new ObjectKey(String.valueOf(Math.random()))))
                                .into(ivAppIcon);
                    }
                }
            }
        });
    }

    @Override
    public void setContentSize(long textSize) {
        this.mTvName.setTextSize(textSize);
    }
}