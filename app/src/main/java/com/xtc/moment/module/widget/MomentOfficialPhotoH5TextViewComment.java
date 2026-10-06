package com.xtc.moment.module.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xtc.log.LogUtil;
import com.xtc.log.util.TextUtils;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbMoment;

/**
 * Official photo row whose body opens an H5 page instead of the native preview.
 */
public class MomentOfficialPhotoH5TextViewComment extends MomentOfficialPhotoTextView {

    private static final String TAG = "MomentOfficialPhotoH5TextViewComment";

    private static int type;

    private MomentContentView mDescriptions;

    public MomentOfficialPhotoH5TextViewComment(Context context) {
        this(context, null);
    }

    public MomentOfficialPhotoH5TextViewComment(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MomentOfficialPhotoH5TextViewComment(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initView() {
        this.rootView = LayoutInflater.from(getContext()).inflate(R.layout.view_photo_text_h5_moment_comment, this);
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
    }

    @Override
    public void loadDefaultImage(Context context, int resId) {
        super.loadDefaultImage(context, resId);
    }

    @Override
    public void loadImage(Context context, DbMoment moment) {
        type = moment.getType().intValue();
        if (this.mContent == null) {
            LogUtil.d(TAG, "mContent == null");
            return;
        }
        if (checkContextIsNull(context)) {
            return;
        }
        super.loadImage(context, moment);
        if (TextUtils.isEmpty(moment.getDescription())) {
            this.mDescriptions.setVisibility(GONE);
            return;
        }
        LogUtil.d(TAG, "Description:" + moment.getDescription());
        this.mDescriptions.setContext(getMyContext());
        this.mDescriptions.setVisibility(VISIBLE);
        this.mDescriptions.setText(moment.getDescription());
    }

    @Override
    public void setTags(int key, Object value) {
        super.setTags(key, value);
    }

    @Override
    public Object getTags(int key) {
        return super.getTags(key);
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
                return true;
            }
        });
    }

    @Override
    public void setContentSize(long textSize) {
        this.mDescriptions.setTextSize(textSize);
        this.mDescriptions.setTextColor(this.mDescriptions.getContext().getResources().getColor(R.color.color_888888));
    }

    public static int getType() {
        return type;
    }
}