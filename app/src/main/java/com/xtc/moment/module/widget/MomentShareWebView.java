package com.xtc.moment.module.widget;

import android.content.Context;
import android.text.TextUtils;
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
import com.xtc.moment.module.bean.ShareWebMoment;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.utils.encode.JSONUtil;

/**
 * Row for a web page that was shared into moments.
 */
public class MomentShareWebView extends MomentPhotoView {

    private static final String TAG = "MomentShareWebView";

    private ImageView ivAppIcon;
    private TextView tvAppName;
    private MomentContentView mDescriptions;
    private MomentContentView mNoSupport;
    private RelativeLayout rlShareContent;

    public MomentShareWebView(Context context) {
        super(context);
    }

    public MomentShareWebView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public MomentShareWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initView() {
        this.rootView = LayoutInflater.from(getContext()).inflate(R.layout.view_share_web_moment, this);
        this.mIcon = (ImageView) this.rootView.findViewById(R.id.iv_account_icon);
        this.ivAccountIconBg = (ImageView) this.rootView.findViewById(R.id.iv_account_icon_bg);
        this.ivOfficialEnterpriseIcon = (ImageView) this.rootView.findViewById(R.id.iv_official_enterprise);
        this.ivOfficialEnterpriseIconBg = (ImageView) this.rootView.findViewById(R.id.iv_official_enterprise_bg);
        this.mTvName = (TextView) this.rootView.findViewById(R.id.tv_account_name);
        this.ivOfficialLabel = (ImageView) this.rootView.findViewById(R.id.iv_official_label);
        this.mContent = (ImageView) this.rootView.findViewById(R.id.chat_msg_item_photo_iv);
        this.mContent.setImageResource(R.drawable.ic_selfie_album_default);
        this.mDescriptions = (MomentContentView) this.rootView.findViewById(R.id.tv_moment_description);
        this.rlMomentSender = (RelativeLayout) this.rootView.findViewById(R.id.rl_moment_sender);
        this.mIvReport = (ImageView) this.rootView.findViewById(R.id.iv_account_report);
        this.mNoSupport = (MomentContentView) this.rootView.findViewById(R.id.tv_no_support);
        this.rlShareContent = (RelativeLayout) this.rootView.findViewById(R.id.rl_share_content);
        this.ivAppIcon = (ImageView) this.rootView.findViewById(R.id.iv_app_icon);
        this.tvAppName = (TextView) this.rootView.findViewById(R.id.tv_app_name);
    }

    @Override
    public void loadImage(final Context context, DbMoment moment) {
        if (this.mContent == null || checkContextIsNull(context)) {
            LogUtil.w(TAG, "mContent == null");
            return;
        }
        if (TextUtils.isEmpty(moment.getResource())) {
            LogUtil.w(TAG, "momentBean.getResource == null");
            return;
        }
        super.loadImage(context, moment);
        final ShareWebMoment shareWebMoment = (ShareWebMoment) JSONUtil.fromJSON(moment.getContent(), ShareWebMoment.class);
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (shareWebMoment == null) {
                    mNoSupport.setVisibility(VISIBLE);
                    rlShareContent.setVisibility(GONE);
                    return;
                }
                mNoSupport.setVisibility(GONE);
                rlShareContent.setVisibility(VISIBLE);
                String desc = shareWebMoment.getDesc();
                if (TextUtils.isEmpty(desc)) {
                    mDescriptions.setVisibility(GONE);
                } else {
                    LogUtil.d(TAG, "Description:" + desc);
                    mDescriptions.setVisibility(VISIBLE);
                    mDescriptions.setContext(context);
                    mDescriptions.setText(desc);
                }
                tvAppName.setText(shareWebMoment.getAppName());
                if (shareWebMoment.getAppIcon() != null) {
                    Glide.with(context).load(shareWebMoment.getAppIcon())
                            .apply(new RequestOptions().signature(new ObjectKey(String.valueOf(Math.random()))))
                            .into(ivAppIcon);
                }
            }
        });
    }

    @Override
    public void setContentOnClickListener(Context context, final DbMoment moment,
                                          final AbsMomentView.OnContentOnClickListener listener) {
        if (this.mContent == null) {
            LogUtil.d(TAG, "mContent == null");
            return;
        }
        this.mContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.d(TAG, "setContentOnClickListener#onClick#momentBean:" + moment);
                if (listener != null) {
                    listener.previewH5();
                }
            }
        });
    }

    @Override
    public void setContentOnLongClickListener(Context context, DbMoment moment,
                                              AbsMomentView.OnContentOnLongClickListener listener) {
        super.setContentOnLongClickListener(context, moment, listener);
    }

    @Override
    public void setContentSize(long textSize) {
        this.mDescriptions.setTextSize(textSize);
        this.mDescriptions.setTextColor(this.mDescriptions.getContext().getResources().getColor(R.color.color_888888));
    }
}