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

/**
 * Standard moment row: sender header plus the rich text content.
 *
 * <p>The content view is created from {@code view_moment} and configured through the abstract
 * setters of {@link AbsMomentView}.
 */
public class MomentView extends AbsMomentView {

    private final ExpandTextView.ClickCheckAllListener clickCheckAllListener;
    protected MomentContentView mContent;
    private String textContent;

    public MomentView(Context context) {
        this(context, null);
    }

    public MomentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MomentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.clickCheckAllListener = new ExpandTextView.ClickCheckAllListener() {
            @Override
            public void click() {
                MomentView view = MomentView.this;
                view.startDetailActivity(view.getDbMoment());
            }
        };
        initView();
    }

    @Override
    public void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_moment, this);
        this.mIcon = (ImageView) view.findViewById(R.id.iv_account_icon);
        this.ivAccountIconBg = (ImageView) view.findViewById(R.id.iv_account_icon_bg);
        this.ivOfficialEnterpriseIcon = (ImageView) view.findViewById(R.id.iv_official_enterprise);
        this.ivOfficialEnterpriseIconBg = (ImageView) view.findViewById(R.id.iv_official_enterprise_bg);
        this.tvAdvertWord = (TextView) view.findViewById(R.id.tv_advert_word);
        this.mTvName = (TextView) view.findViewById(R.id.tv_account_name);
        this.ivOfficialLabel = (ImageView) view.findViewById(R.id.iv_official_label);
        this.mContent = (MomentContentView) view.findViewById(R.id.tv_moment_content);
        this.rlMomentSender = (RelativeLayout) view.findViewById(R.id.rl_moment_sender);
        this.rlShareContent = (RelativeLayout) view.findViewById(R.id.rl_share_content);
        this.mIvReport = (ImageView) view.findViewById(R.id.iv_account_report);
    }

    @Override
    public void setContent(String resource, String text, int color) {
        this.mContent.setContext(getMyContext());
        this.textContent = text;
        this.mContent.setClickCheckAllListener(this.clickCheckAllListener);
        if (isTextSupportExpand()) {
            this.mContent.setTextMaxLines(3);
            this.mContent.setTextSupportExpand(true);
        }
        this.mContent.setRichText(resource, text);
        this.mContent.setTextColor(color);
    }

    @Override
    public void setContent(int resId, String text) {
        this.mContent.setContext(getMyContext());
        this.textContent = text;
        this.mContent.setClickCheckAllListener(this.clickCheckAllListener);
        if (isTextSupportExpand()) {
            this.mContent.setTextMaxLines(3);
            this.mContent.setTextSupportExpand(true);
        }
        this.mContent.setRichText(resId, text);
    }

    @Override
    public void setContent(int resId, String text, int color) {
        this.mContent.setContext(getMyContext());
        this.textContent = text;
        this.mContent.setClickCheckAllListener(this.clickCheckAllListener);
        if (isTextSupportExpand()) {
            this.mContent.setTextMaxLines(3);
            this.mContent.setTextSupportExpand(true);
        }
        this.mContent.setRichText(resId, text);
        this.mContent.setTextColor(color);
    }

    @Override
    public void setContentVisibility(boolean visible) {
        super.setContentVisibility(visible);
        this.mContent.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setContentSize(long textSize) {
        this.mContent.setTextSize(textSize);
    }

    @Override
    public void setContentOnLongClickListener(Context context, final DbMoment moment,
                                              final OnContentOnLongClickListener listener) {
        MomentContentView contentView = this.mContent;
        if (contentView == null) {
            LogUtil.d("MomentView", "mContent == null");
            return;
        }
        contentView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                LogUtil.d("MomentView", "setContentOnLongClickListener#onClick#momentBean:" + moment.toString());
                if (listener == null) {
                    return true;
                }
                listener.deleteItem(moment);
                return true;
            }
        });
    }
}