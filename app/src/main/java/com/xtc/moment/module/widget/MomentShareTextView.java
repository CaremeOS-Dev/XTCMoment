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
import com.xtc.moment.module.bean.ShareTextPublish;
import com.xtc.utils.encode.JSONUtil;

/**
 * Row for a text that was shared into moments from another app.
 *
 * <p>When the payload cannot be parsed the row falls back to the "not supported" hint.
 */
public class MomentShareTextView extends AbsMomentView {

    private static final String TAG = "MomentShareTextView";

    private ImageView mAppIcon;
    private TextView mAppName;
    private MomentContentView mContent;
    private MomentContentView mNoSupport;
    private RelativeLayout mRlShareContent;

    public MomentShareTextView(Context context) {
        this(context, null);
    }

    public MomentShareTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MomentShareTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @Override
    public void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_share_text_moment, this);
        this.mIcon = (ImageView) view.findViewById(R.id.iv_account_icon);
        this.ivAccountIconBg = (ImageView) view.findViewById(R.id.iv_account_icon_bg);
        this.ivOfficialEnterpriseIcon = (ImageView) view.findViewById(R.id.iv_official_enterprise);
        this.ivOfficialEnterpriseIconBg = (ImageView) view.findViewById(R.id.iv_official_enterprise_bg);
        this.mTvName = (TextView) view.findViewById(R.id.tv_account_name);
        this.ivOfficialLabel = (ImageView) view.findViewById(R.id.iv_official_label);
        this.mAppIcon = (ImageView) view.findViewById(R.id.iv_app_icon);
        this.mAppName = (TextView) view.findViewById(R.id.tv_app_name);
        this.mContent = (MomentContentView) view.findViewById(R.id.tv_moment_content);
        this.rlMomentSender = (RelativeLayout) view.findViewById(R.id.rl_moment_sender);
        this.mNoSupport = (MomentContentView) view.findViewById(R.id.tv_no_support);
        this.mRlShareContent = (RelativeLayout) view.findViewById(R.id.rl_share_content);
        this.mIvReport = (ImageView) view.findViewById(R.id.iv_account_report);
    }

    @Override
    public void setContent(String resource, String text, int type) {
        ShareTextPublish shareText = (ShareTextPublish) JSONUtil.fromJSON(text, ShareTextPublish.class);
        if (shareText == null) {
            this.mNoSupport.setVisibility(View.VISIBLE);
            this.mRlShareContent.setVisibility(View.GONE);
            return;
        }
        this.mNoSupport.setVisibility(View.GONE);
        this.mRlShareContent.setVisibility(View.VISIBLE);
        this.mContent.setContext(getMyContext());
        this.mContent.setText(shareText.getContent());
        this.mAppName.setText(shareText.getAppName());
        if (checkContextIsNull(getMyContext()) || shareText.getAppIcon() == null) {
            return;
        }
        Glide.with(getMyContext()).load(shareText.getAppIcon())
                .apply(new RequestOptions().signature(new ObjectKey(String.valueOf(Math.random()))))
                .into(this.mAppIcon);
    }

    @Override
    public void setContentOnLongClickListener(Context context, final DbMoment moment,
                                              final OnContentOnLongClickListener listener) {
        setOnLongClickListener(new View.OnLongClickListener() {
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