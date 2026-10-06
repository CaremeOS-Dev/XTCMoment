package com.xtc.moment.module.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.bean.ShareAppMoment;
import com.xtc.moment.util.ShareAppStartUtil;
import com.xtc.utils.encode.JSONUtil;

/**
 * Comment page variant of {@link MomentShareAppView}.
 */
public class MomentShareAppViewComment extends MomentPhotoView {

    private static final String TAG = "MomentShareAppView";

    private TextView tvAppName;
    private TextView tvDesc;
    private MomentContentView mNoSupport;
    private RelativeLayout mRlShareContent;

    public MomentShareAppViewComment(Context context) {
        this(context, null);
    }

    public MomentShareAppViewComment(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MomentShareAppViewComment(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initView() {
        this.rootView = LayoutInflater.from(getContext()).inflate(R.layout.view_share_app_moment_comment, this);
        this.mIcon = (ImageView) this.rootView.findViewById(R.id.iv_account_icon);
        this.ivAccountIconBg = (ImageView) this.rootView.findViewById(R.id.iv_account_icon_bg);
        this.ivOfficialEnterpriseIcon = (ImageView) this.rootView.findViewById(R.id.iv_official_enterprise);
        this.ivOfficialEnterpriseIconBg = (ImageView) this.rootView.findViewById(R.id.iv_official_enterprise_bg);
        this.mTvName = (TextView) this.rootView.findViewById(R.id.tv_account_name);
        this.ivOfficialLabel = (ImageView) this.rootView.findViewById(R.id.iv_official_label);
        this.mContent = (ImageView) this.rootView.findViewById(R.id.chat_msg_item_photo_iv);
        this.mContent.setImageResource(R.drawable.ic_selfie_album_default);
        this.rlMomentSender = (RelativeLayout) this.rootView.findViewById(R.id.rl_moment_sender);
        this.tvAppName = (TextView) this.rootView.findViewById(R.id.tv_app_name);
        this.tvDesc = (TextView) this.rootView.findViewById(R.id.tv_desc);
        this.mNoSupport = (MomentContentView) this.rootView.findViewById(R.id.tv_no_support);
        this.mRlShareContent = (RelativeLayout) this.rootView.findViewById(R.id.rl_share_content);
        this.mIvReport = (ImageView) this.rootView.findViewById(R.id.iv_account_report);
    }

    @Override
    public void loadImage(Context context, DbMoment moment) {
        ShareAppMoment shareAppMoment = (ShareAppMoment) JSONUtil.fromJSON(moment.getContent(), ShareAppMoment.class);
        if (shareAppMoment == null) {
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
        this.tvAppName.setText(shareAppMoment.getAppName());
        this.tvDesc.setText(shareAppMoment.getDesc());
    }

    @Override
    public void setContentOnClickListener(final Context context, final DbMoment moment,
                                          AbsMomentView.OnContentOnClickListener listener) {
        if (moment == null) {
            LogUtil.d(TAG, "click share app, moment bean is null! ");
            return;
        }
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareAppStartUtil.startApp(context, moment);
            }
        });
    }

    @Override
    public void setContentOnLongClickListener(Context context, final DbMoment moment,
                                              final AbsMomentView.OnContentOnLongClickListener listener) {
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

    @Override
    public void setContentSize(long textSize) {
        this.tvAppName.setTextSize(textSize);
    }
}