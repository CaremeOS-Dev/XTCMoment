package com.xtc.moment.module.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.xtc.aitext.util.AITextRxUtils;
import com.xtc.contactapi.contact.manager.ContactManager;
import com.xtc.log.LogUtil;
import com.xtc.log.util.TextUtils;
import com.xtc.moment.R;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.bean.LivePhotoMsg;
import com.xtc.moment.module.bean.PhotoMsg;
import com.xtc.moment.module.details.MomentDetailsActivity;
import com.xtc.moment.module.illegal.handler.IllegalMessageHandler;
import com.xtc.moment.module.report.ReportActivity;
import com.xtc.moment.module.report.adapter.AbsInteractionAdapter;
import com.xtc.moment.module.share.ShareActivity;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.third.bean.PushReportBean;
import com.xtc.moment.third.behavior.MomentBehavior;
import com.xtc.moment.util.MomentTypeUtil;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;
import com.xtc.ui.widget.UiConstants;
import com.xtc.ui.widget.toast.view.ToastUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.system.NetworkUtils;

import java.util.concurrent.Callable;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 动态内容视图基类：头像、昵称、举报入口与跳转详情。
 */
public abstract class AbsMomentView extends FrameLayout {

    private static final String TAG = "AbsMomentView";

    public ImageView mIcon;
    public ImageView ivAccountIconBg;
    public ImageView ivOfficialEnterpriseIcon;
    public ImageView ivOfficialEnterpriseIconBg;
    public ImageView ivOfficialLabel;
    public ImageView mIvReport;
    public TextView mTvName;
    public TextView tvAdvertWord;
    public RelativeLayout rlMomentSender;
    public RelativeLayout rlShareContent;

    private final ContactManager contactManager;
    private final IllegalMessageHandler illegalMessageHandler;
    private Context context;
    private DbMoment dbMoment;
    private String mIconPath;
    private String mNameStr;
    private boolean isTextSupportExpand;

    public interface IOnDialogClickLister {
        void onRightBtnClick();
    }

    public interface OnContentOnClickListener {
        void previewPhoto(PhotoMsg photoMsg);

        void previewLivePhoto(LivePhotoMsg livePhotoMsg);

        void previewH5();

        void preVideoView(String videoPath, boolean isShare);

        void previewPhoto(String localPath);
    }

    public interface OnContentOnLongClickListener {
        void deleteItem(DbMoment moment);
    }

    public interface OnAdvertWordOnClickListener {
        void deleteItem(DbMoment moment);
    }

    public AbsMomentView(Context context) {
        this(context, null);
    }

    public AbsMomentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbsMomentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.contactManager = ContactManager.getInstance(context);
        initView();
        ImageView icon = this.mIcon;
        if (icon != null) {
            icon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        this.illegalMessageHandler = IllegalMessageHandler.getInstance(context);
    }

    public abstract void initView();

    public void setContent(int type, String content) {
    }

    public void setContent(int type, String content, int extra) {
    }

    public void setContent(String content, String resource, int type) {
    }

    public void loadImage(Context context, DbMoment moment) {
    }

    public void loadDefaultImage(Context context, int resId) {
    }

    public void setContentOnClickListener(Context context, DbMoment moment, OnContentOnClickListener listener) {
    }

    public void setContentOnLongClickListener(Context context, DbMoment moment, OnContentOnLongClickListener listener) {
    }

    public void setAdvertWordOnClickListener(Context context, DbMoment moment, OnAdvertWordOnClickListener listener) {
    }

    public void setContentSize(long textSize) {
    }

    public void setContentVisibility(boolean visible) {
    }

    public Object getTags(int key) {
        return null;
    }

    public void setTags(int key, Object value) {
    }

    public boolean checkContextIsNull(Context context) {
        if (context == null) {
            LogUtil.d(TAG, "checkContextIsNull mContext is null");
            return true;
        }
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (!activity.isFinishing() && !activity.isDestroyed()) {
                return false;
            }
            LogUtil.d(TAG, "checkContextIsNull context is finish");
            return true;
        }
        LogUtil.d(TAG, "checkContextIsNull not activity");
        return false;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getMyContext() {
        Context current = this.context;
        if (current == null) {
            return getContext();
        }
        if (!(current instanceof Activity)) {
            return current;
        }
        Activity activity = (Activity) current;
        return (activity.isFinishing() || activity.isDestroyed()) ? getContext() : activity;
    }

    public void setDbMoment(DbMoment moment) {
        this.dbMoment = moment;
    }

    public DbMoment getDbMoment() {
        return this.dbMoment;
    }

    public void setTextSupportExpand(boolean supportExpand) {
        this.isTextSupportExpand = supportExpand;
    }

    public boolean isTextSupportExpand() {
        return this.isTextSupportExpand;
    }

    public void setIconOnClickListener(final DbMoment moment) {
        ImageView icon = this.mIcon;
        if (icon == null) {
            return;
        }
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (moment != null && MomentTypeUtil.isOfficialType(moment.getType().intValue())) {
                    LogUtil.d(TAG, "官方消息类型不响应头像点击事件");
                    return;
                }
                String accountWatchId = AccountInfoServerImpl.getInstance(getContext())
                        .getWatchAccountInfo().getWatchId(getContext());
                boolean isSelf = accountWatchId != null && accountWatchId.equals(moment.getWatchId());
                Intent intent = new Intent(AbsMomentView.this.context, ShareActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_WATCH_ID, moment.getWatchId());
                intent.putExtra(Constants.INTENT_EXTRA_IS_SELF, isSelf);
                intent.putExtra(Constants.INTENT_EXTRA_ICON_PATH, AbsMomentView.this.mIconPath);
                intent.putExtra(Constants.INTENT_EXTRA_START_FROM_MOMENT, true);
                intent.putExtra(Constants.INTENT_EXTRA_NAME, AbsMomentView.this.mNameStr);
                AbsMomentView.this.context.startActivity(intent);
            }
        });
    }

    public String getIcon() {
        return this.mIconPath;
    }

    public void setIcon(int resId) {
        Glide.with(getMyContext()).load(Integer.valueOf(resId))
                .apply(new RequestOptions().centerCrop().placeholder(R.drawable.default_custom_default))
                .into(this.mIcon);
    }

    public void setIcon(String iconPath) {
        if (getMyContext() == null) {
            return;
        }
        if (getMyContext() instanceof Activity) {
            Activity activity = (Activity) getMyContext();
            if (activity.isDestroyed() || activity.isFinishing()) {
                return;
            }
        }
        if (TextUtils.isEmpty(iconPath)) {
            Glide.with(getMyContext()).load(this.contactManager.getDefaultPortraitPath(getMyContext()))
                    .apply(new RequestOptions().centerCrop()
                            .placeholder(R.drawable.default_custom_default)
                            .transform((Transformation<Bitmap>) new CircleCrop()))
                    .into(this.mIcon);
        } else {
            this.mIconPath = iconPath;
            Glide.with(getMyContext()).load(iconPath)
                    .apply(new RequestOptions().centerCrop()
                            .placeholder(R.drawable.default_custom_default)
                            .transform((Transformation<Bitmap>) new CircleCrop())
                            .signature(new ObjectKey(String.valueOf(Math.random()))))
                    .into(this.mIcon);
        }
    }

    public void showView() {
        RelativeLayout sender = this.rlMomentSender;
        if (sender == null) {
            return;
        }
        sender.setVisibility(VISIBLE);
    }

    public void hideView() {
        RelativeLayout sender = this.rlMomentSender;
        if (sender == null) {
            return;
        }
        sender.setVisibility(GONE);
    }

    public String getNameStr() {
        return this.mNameStr;
    }

    public void setTvName(boolean isOfficial, String name) {
        TextView nameView = this.mTvName;
        if (nameView == null) {
            return;
        }
        this.mNameStr = name;
        nameView.setText(name);
        nameView.setTextColor(Color.parseColor(isOfficial ? "#fec02b" : "#ccffffff"));
    }

    protected void showReportBtnDialog(final AbsInteractionAdapter.IOnDialogClickLister listener) {
        if (!ModuleSwitchUtil.queryModuleSwitchByBoolean(this.context, ModuleSwitchConstant.MODULE_REPORT_SUPPORT, false)) {
            return;
        }
        com.xtc.ui.widget.util.DialogUtil.showDialog(
                com.xtc.ui.widget.util.DialogUtil.makeDoubleIconBtnDialog(this.context,
                        new com.xtc.ui.widget.dialog.bean.icon.DoubleIconBtnBean(this.context, false, UiConstants.Color.GRAY, 0, R.string.cancel,
                                new int[]{R.color.color_ffac35, R.color.color_ff6833},
                                R.drawable.ic_chat_report, R.string.report_text_, true,
                                new com.xtc.ui.widget.dialog.bean.icon.DoubleIconBtnBean.OnClickListener() {
                                    @Override
                                    public void onBottomBtnClick(Dialog dialog, View view) {
                                    }

                                    @Override
                                    public void onLeftBtnClick(Dialog dialog, View view) {
                                        com.xtc.ui.widget.util.DialogUtil.dismissDialog(dialog);
                                    }

                                    @Override
                                    public void onRightBtnClick(Dialog dialog, View view) {
                                        com.xtc.ui.widget.util.DialogUtil.dismissDialog(dialog);
                                        listener.onRightBtnClick();
                                    }
                                })));
    }

    protected boolean isNetAvailable() {
        if (NetworkUtils.isNetworkAvailable(this.context)) {
            return false;
        }
        Context current = this.context;
        ToastUtil.showShortCover(current, current.getString(R.string.net_work_exception));
        return true;
    }

    protected void startReportActivity(String friendWatchId, String momentId, String commentId) {
        if (isNetAvailable()) {
            Context current = this.context;
            MomentBehavior.clickReportBtnHint(current,
                    new PushReportBean(momentId, friendWatchId, current.getString(R.string.net_work_exception)));
            return;
        }
        Intent intent = new Intent(this.context, ReportActivity.class);
        intent.putExtra("momentId", momentId);
        intent.putExtra("commentId", commentId);
        intent.putExtra(ReportActivity.FRIEND_WATCH_ID, friendWatchId);
        intent.putExtra(ReportActivity.IN_FORM_SOURCE, 3);
        intent.putExtra(ReportActivity.REPORT_TYPE, TextUtils.isEmpty(commentId) ? 1 : 2);
        MomentBehavior.clickReportBtn(this.context, new PushReportBean(momentId, friendWatchId));
        this.context.startActivity(intent);
    }

    protected void startReportActivity(DbMoment moment, String commentId, String content, String momentContent,
            int contentType) {
        if (isNetAvailable()) {
            MomentBehavior.clickReportBtnHint(this.context, new PushReportBean(moment.getMomentId(),
                    moment.getWatchId(), this.context.getString(R.string.net_work_exception)));
            return;
        }
        Intent intent = new Intent(this.context, ReportActivity.class);
        intent.putExtra("momentId", moment.getMomentId());
        intent.putExtra("commentId", commentId);
        intent.putExtra(ReportActivity.FRIEND_WATCH_ID, moment.getWatchId());
        intent.putExtra(ReportActivity.IN_FORM_SOURCE, 6);
        intent.putExtra(ReportActivity.REPORT_TYPE, TextUtils.isEmpty(commentId) ? 1 : 2);
        intent.putExtra("moment_content", momentContent);
        if (!TextUtils.isEmpty(content)) {
            intent.putExtra(ReportActivity.CONTENT, content);
            intent.putExtra(ReportActivity.CONTENT_TYPE, contentType);
        }
        MomentBehavior.clickReportBtn(this.context, new PushReportBean(moment.getMomentId(), moment.getWatchId()));
        this.context.startActivity(intent);
    }

    protected void startDetailActivity(final DbMoment moment) {
        if (moment == null) {
            return;
        }
        AITextRxUtils.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return JSONUtil.toJSON(moment);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String json) {
                if (TextUtils.isEmpty(json)) {
                    return;
                }
                Intent intent = new Intent(AbsMomentView.this.context, MomentDetailsActivity.class);
                intent.putExtra("momentId", moment.getMomentId());
                intent.putExtra(MomentDetailsActivity.INTENT_MOMENT_KEY, json);
                intent.putExtra("watchId", moment.getWatchId());
                intent.putExtra(MomentDetailsActivity.INTENT_NEED_SCROLL, false);
                AbsMomentView.this.context.startActivity(intent);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtil.e(TAG, "startDetailActivity: throwable", throwable);
            }
        });
    }

    protected boolean isDisableSend() {
        return this.illegalMessageHandler.isDisableSend();
    }

    protected boolean isDisableSend(boolean showToast) {
        boolean disableSend = isDisableSend();
        if (showToast && disableSend) {
            Context current = this.context;
            ToastUtil.showLongCover(current, current.getResources().getString(R.string.you_dis_say));
        }
        return disableSend;
    }

    protected boolean supportReportType(DbMoment moment) {
        int type = moment.getType().intValue();
        if (type == 26 || type == 27) {
            return true;
        }
        switch (type) {
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                return true;
            default:
                switch (type) {
                    case 22:
                    case 23:
                    case 24:
                        return true;
                    default:
                        return false;
                }
        }
    }
}