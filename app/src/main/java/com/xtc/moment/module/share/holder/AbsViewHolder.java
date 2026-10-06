package com.xtc.moment.module.share.holder;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.xtc.contactapi.contact.bean.ContactBean;
import com.xtc.contactapi.contact.manager.ContactManager;
import com.xtc.contactapi.contacthead.impl.ContactHeadManager;
import com.xtc.httplib.net.HttpSubscriber;
import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.R;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.illegal.handler.IllegalMessageHandler;
import com.xtc.moment.module.report.ReportActivity;
import com.xtc.moment.module.report.adapter.AbsInteractionAdapter;
import com.xtc.moment.module.share.adapter.ShareAdapter;
import com.xtc.moment.module.viewholder.comment.MomentCommentAdapter;
import com.xtc.moment.module.widget.CommentIconView;
import com.xtc.moment.module.widget.DoubleIconBtnBean;
import com.xtc.moment.module.widget.MainMomentCommentView;
import com.xtc.moment.module.widget.MomentContentView;
import com.xtc.moment.module.widget.MomentLikeView;
import com.xtc.moment.module.widget.MomentReminderView;
import com.xtc.moment.serve.bean.FileUrlParam;
import com.xtc.moment.serve.impl.MomentPhotoServeImpl;
import com.xtc.moment.third.bean.PushReportBean;
import com.xtc.moment.third.behavior.MomentBehavior;
import com.xtc.moment.util.ClickUtils;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.LikeDrawableCache;
import com.xtc.moment.util.MomentLikeViewWindow;
import com.xtc.moment.util.TimeUtils;
import com.xtc.moment.util.ToastUtil;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;
import com.xtc.moment.widget.LbsLayout;
import com.xtc.ui.widget.UiConstants;
import com.xtc.ui.widget.util.DialogUtil;
import com.xtc.utils.system.NetworkUtils;
import com.xtc.utils.ui.DimenUtil;

import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 分享动态列表的 ViewHolder 基类，持有通用子控件与点赞/评论相关状态。
 */
public abstract class AbsViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "AbsViewHolder";
    /** 取消点赞后再次允许取消的冷却时间。 */
    private static final int CANCEL_LIKE_SUCCESS_DELAY_MILLIS = 3000;

    public MomentCommentAdapter commentAdapter;
    public RecyclerView commentRecyclerView;
    public String iconPath;
    public ImageView ivBanner;
    public ImageView ivContent;
    public ImageView ivIcon;
    public ImageView ivIconBg;
    public CommentIconView ivMomentComment;
    public ImageView ivMomentRange;
    public ImageView ivShareReportIcon;
    public LbsLayout llLbs;
    public MainMomentCommentView momentCommentView;
    public MomentLikeView momentLike;
    public MomentReminderView momentReminderView;
    public MomentContentView tvContent;
    public TextView tvLikes;
    public TextView tvName;
    public TextView tvTime;

    private int cancelLikeSuccessDelayMillis = CANCEL_LIKE_SUCCESS_DELAY_MILLIS;
    private ContactManager contactManager;
    private DbMoment dbMoment;
    private Context holderContext;
    private final IllegalMessageHandler illegalMessageHandler;
    private long lastCancelTime;
    private boolean mIsSelf;
    private MomentLikeViewWindow mLikeAnimationWindow;
    private ContactHeadManager manager;
    private AbsInteractionAdapter.OnLikeClickListener onLikeClickListener;

    /** 弹窗右侧按钮点击回调。 */
    public interface IOnDialogClickLister {
        void onRightBtnClick();
    }

    public AbsViewHolder(View view) {
        super(view);
        this.lastCancelTime = 0L;
        this.cancelLikeSuccessDelayMillis = CANCEL_LIKE_SUCCESS_DELAY_MILLIS;
        this.holderContext = MomentApp.getAppContext();
        this.contactManager = ContactManager.getInstance(this.holderContext);
        this.illegalMessageHandler = IllegalMessageHandler.getInstance(this.holderContext);
    }

    public Context getHolderContext() {
        return this.holderContext;
    }

    public void setHolderContext(Context context) {
        this.holderContext = context;
    }

    public void setmIsSelf(boolean isSelf) {
        this.mIsSelf = isSelf;
    }

    public boolean ismIsSelf() {
        return this.mIsSelf;
    }

    public void setManager(ContactHeadManager contactHeadManager) {
        if (this.manager == null) {
            this.manager = contactHeadManager;
        }
    }

    public void setOnLikeClickListener(AbsInteractionAdapter.OnLikeClickListener listener) {
        this.onLikeClickListener = listener;
    }

    public void setLastCancelTime(long lastCancelTime) {
        this.lastCancelTime = lastCancelTime;
    }

    public void setmLikeAnimationWindow(MomentLikeViewWindow likeAnimationWindow) {
        this.mLikeAnimationWindow = likeAnimationWindow;
    }

    public DbMoment getDbMoment() {
        return this.dbMoment;
    }

    public void setDbMoment(DbMoment moment) {
        this.dbMoment = moment;
    }

    public void hideRecyclerComment() {
        this.commentRecyclerView.setVisibility(View.GONE);
        this.momentCommentView.setVisibility(View.VISIBLE);
    }

    public void hideComment() {
        this.commentRecyclerView.setVisibility(View.GONE);
        this.momentCommentView.setVisibility(View.GONE);
    }

    public void showRecyclerComment() {
        this.commentRecyclerView.setVisibility(View.VISIBLE);
        this.momentCommentView.setVisibility(View.GONE);
    }

    public void hideCommentIcon() {
        CommentIconView commentIconView = this.ivMomentComment;
        if (commentIconView != null) {
            commentIconView.setVisibility(View.GONE);
        }
    }

    public void showCommentIcon() {
        CommentIconView commentIconView = this.ivMomentComment;
        if (commentIconView != null) {
            commentIconView.setVisibility(View.VISIBLE);
            this.ivMomentComment.setIcon();
        }
    }

    /** 子类按需覆写：加载动态配图。 */
    public void loadImage(Context context, DbMoment moment, AbsViewHolder holder) {
    }

    /** 子类按需覆写：设置内容点击事件。 */
    public void setContentOnClickListener(DbMoment moment, ShareAdapter.OnContentOnClickListener listener) {
    }

    /** 子类按需覆写：设置内容长按事件。 */
    public void setContentOnLongClickListener(DbMoment moment, ShareAdapter.OnContentOnLongClickListener listener) {
    }

    /** 子类按需覆写：控制内容区域显隐。 */
    public void setContentVisibility(boolean visible) {
    }

    /** 子类按需覆写：设置带图标资源 id 的正文。 */
    public void setTvContent(int resId, String text) {
    }

    /** 子类按需覆写：设置带图标路径的正文。 */
    public void setTvContent(String resource, String text) {
    }

    public void setIvIcon(int resId, boolean self) {
        Glide.with(this.holderContext).load(resId)
                .apply(new RequestOptions().centerCrop().error(R.drawable.default_custom_default))
                .into(this.ivIcon);
    }

    public void setIvIcon(String iconPath, boolean self) {
        if (TextUtils.isEmpty(iconPath)) {
            Glide.with(this.ivIcon.getContext()).load(this.contactManager.getDefaultPortraitPath(this.holderContext))
                    .apply(new RequestOptions().centerCrop().error(R.drawable.default_custom_default))
                    .into(this.ivIcon);
            return;
        }
        String currentPath = this.iconPath;
        if (currentPath == null || !currentPath.equals(iconPath)) {
            this.iconPath = iconPath;
            Glide.with(this.ivIcon.getContext()).load(this.iconPath)
                    .apply(new RequestOptions().centerCrop().error(R.drawable.default_custom_default)
                            .signature(new ObjectKey(String.valueOf(Math.random()))))
                    .into(this.ivIcon);
        }
    }

    public void setIvIcon(ContactBean contactBean, boolean self) {
        if (contactBean == null || contactBean.getPhotoPath() == null || contactBean.getPhotoPath().equals("")) {
            Glide.with(this.ivIcon.getContext()).load(this.contactManager.getDefaultPortraitPath(this.holderContext))
                    .apply(new RequestOptions().centerCrop().error(R.drawable.default_custom_default))
                    .into(this.ivIcon);
            return;
        }
        if (!self) {
            int width = this.holderContext.getResources().getDimensionPixelSize(R.dimen.dimen_item_avatar_size_width_max);
            int height = this.holderContext.getResources().getDimensionPixelSize(R.dimen.dimen_item_avatar_size_height_max);
            ContactBean targetContact = new ContactBean();
            targetContact.setPhotoPath(contactBean.getPhotoPath());
            targetContact.setContactServerId(contactBean.getContactServerId());
            targetContact.setLastUpdatedTimestamp(contactBean.getLastUpdatedTimestamp());
            Glide.with(this.holderContext).load(targetContact.getPhotoPath())
                    .apply(new RequestOptions().centerCrop().diskCacheStrategy(DiskCacheStrategy.DATA)
                            .skipMemoryCache(true).override(width, height)
                            .transform((Transformation<Bitmap>) new CircleCrop()))
                    .into(this.ivIcon);
            return;
        }
        Glide.with(this.holderContext).load(contactBean.getPhotoPath())
                .apply(new RequestOptions().centerCrop()
                        .signature(new ObjectKey(String.valueOf(TimeUtils.getFileLastModifiedTime(contactBean.getPhotoPath()))))
                        .placeholder(R.drawable.default_custom_default))
                .into(this.ivIcon);
    }

    public void setTvName(boolean self, String name) {
        if (name == null) {
            name = "";
        }
        int selfColor = this.tvName.getContext().getResources().getColor(R.color.my_self_text);
        int otherColor = this.tvName.getContext().getResources().getColor(R.color.no_my_self_text);
        this.tvName.setTextColor(self ? selfColor : otherColor);
        this.tvName.setText(self ? this.holderContext.getString(R.string.me) : name);
    }

    public void setTvTime(final Context context, final long timestamp) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                final String time = TimeUtils.getTime(context, timestamp);
                HandlerUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        AbsViewHolder.this.tvTime.setText(time);
                    }
                });
            }
        });
    }

    public void setTvLikes(DbMoment moment, Map<String, List<DbLikeMessage>> praiseRecordMap, String selfWatchId,
            LikeDrawableCache likeDrawableCache) {
        if (likeDrawableCache == null || this.tvLikes == null) {
            return;
        }
        this.tvLikes.setMovementMethod(LinkMovementMethod.getInstance());
        List<DbLikeMessage> likeMessages = praiseRecordMap == null ? null : praiseRecordMap.get(moment.getMomentId());
        if (this.mIsSelf || likeMessages == null || likeMessages.isEmpty()) {
            this.tvLikes.setVisibility(View.GONE);
            return;
        }
        this.tvLikes.setVisibility(View.VISIBLE);
        SpannableStringBuilder builder = new SpannableStringBuilder();
        for (int index = 0; index < likeMessages.size(); index++) {
            DbLikeMessage likeMessage = likeMessages.get(index);
            if (selfWatchId.equals(likeMessage.getWatchId())) {
                builder.append(richText(this.holderContext.getString(R.string.me), moment, true, likeMessage, likeDrawableCache));
            } else {
                builder.append(richText(likeMessage.getWatchName(), moment, false, likeMessage, likeDrawableCache));
            }
            if (index < likeMessages.size() - 1) {
                builder.append(this.holderContext.getString(R.string.comma));
            }
        }
        this.tvLikes.setText(builder);
    }

    /** 构造未点赞好友的点赞文案。 */
    public Spannable richText(String name, DbMoment moment) {
        Drawable drawable;
        Spannable spannable = Spannable.Factory.getInstance().newSpannable(" love " + name);
        if (!moment.isEnableLike() && !this.mIsSelf) {
            drawable = this.holderContext.getResources().getDrawable(R.drawable.circle_love);
        } else {
            drawable = this.holderContext.getResources().getDrawable(R.drawable.circle_loved);
        }
        drawable.setBounds(0, 0, DimenUtil.dp2px(this.holderContext, 15.0f), DimenUtil.dp2px(this.holderContext, 15.0f));
        spannable.setSpan(new ImageSpan(drawable), 1, 5, 33);
        AbsInteractionAdapter.OnLikeClickListener listener = this.onLikeClickListener;
        if (listener != null) {
            spannable.setSpan(new LikeClickableSpan(listener, moment), 0, 6, 33);
        }
        return spannable;
    }

    /** 构造点赞列表中某个好友的点赞文案。 */
    public Spannable richText(String name, DbMoment moment, boolean self, DbLikeMessage likeMessage,
            LikeDrawableCache likeDrawableCache) {
        LogUtil.d(TAG, "点赞好友 = [" + name + "]");
        if (self) {
            return dealSelfLike(name, moment, true, likeMessage, likeDrawableCache);
        }
        Spannable spannable = Spannable.Factory.getInstance().newSpannable(" love " + name);
        Drawable drawable = likeDrawableCache.getCacheDrawable(likeMessage.getLikedPic());
        if (drawable == null) {
            drawable = likeDrawableCache.getCacheDrawableById(R.drawable.circle_love);
        }
        drawable.setBounds(0, 0, DimenUtil.dp2px(this.holderContext, 15.0f), DimenUtil.dp2px(this.holderContext, 15.0f));
        spannable.setSpan(new ImageSpan(drawable), 1, 5, 33);
        AbsInteractionAdapter.OnLikeClickListener listener = this.onLikeClickListener;
        if (listener != null) {
            spannable.setSpan(new LikeClickableSpan(listener, moment), 0, 6, 33);
        }
        return spannable;
    }

    private Spannable dealSelfLike(String name, DbMoment moment, boolean self, DbLikeMessage likeMessage,
            LikeDrawableCache likeDrawableCache) {
        Spannable spannable = Spannable.Factory.getInstance().newSpannable(" love " + name);
        Drawable drawable = likeDrawableCache.getCacheDrawable(likeMessage.getLikedPic());
        if (drawable == null) {
            int resId;
            if (IllegalMessageHandler.getInstance(this.holderContext).isDisableSend()) {
                resId = !moment.isEnableLike() ? R.drawable.circle_like_selectedl_not : R.drawable.circle_like_normal_not;
            } else {
                resId = !moment.isEnableLike() ? R.drawable.circle_like_pressed : R.drawable.circle_like_normal;
            }
            drawable = likeDrawableCache.getCacheDrawableById(resId);
        }
        drawable.setBounds(0, 0, DimenUtil.dp2px(this.holderContext, 15.0f), DimenUtil.dp2px(this.holderContext, 15.0f));
        spannable.setSpan(new ImageSpan(drawable), 1, 5, 33);
        AbsInteractionAdapter.OnLikeClickListener listener = this.onLikeClickListener;
        if (listener != null) {
            spannable.setSpan(new LikeClickableSpan(listener, moment), 0, 6, 33);
        }
        return spannable;
    }

    /** 点赞文案中的可点击片段。 */
    public class LikeClickableSpan extends ClickableSpan {

        private final AbsInteractionAdapter.OnLikeClickListener likeRequestListener;
        private final DbMoment moment;

        public LikeClickableSpan(AbsInteractionAdapter.OnLikeClickListener listener, DbMoment moment) {
            this.likeRequestListener = listener;
            this.moment = moment;
        }

        @Override
        public void updateDrawState(TextPaint textPaint) {
            textPaint.setUnderlineText(false);
            textPaint.bgColor = 0;
        }

        @Override
        public void onClick(View view) {
            if (AbsViewHolder.this.mIsSelf) {
                return;
            }
            if (!ClickUtils.isFastClick()) {
                LogUtil.i(AbsViewHolder.TAG, "click isFastClick");
                return;
            }
            if (!NetworkUtils.isConnected(AbsViewHolder.this.holderContext)) {
                ToastUtil.showNoConnected(AbsViewHolder.this.holderContext);
                return;
            }
            if (AbsViewHolder.this.isDisableSend(true)) {
                LogUtil.i(AbsViewHolder.TAG, "disable send");
                return;
            }
            if (this.likeRequestListener == null) {
                return;
            }
            if (!this.moment.isEnableLike()) {
                if (!ModuleSwitchUtil.queryModuleSwitchByBoolean(AbsViewHolder.this.holderContext,
                        ModuleSwitchConstant.MODULE_SWITCH_MOMENT_CANCEL_PRAISE, false)) {
                    ToastUtil.showShortCover(AbsViewHolder.this.holderContext,
                            AbsViewHolder.this.holderContext.getString(R.string.moment_give_like));
                    LogUtil.i(AbsViewHolder.TAG, "取消点赞全网开关关闭");
                    return;
                }
                if (System.currentTimeMillis() - AbsViewHolder.this.lastCancelTime
                        <= AbsViewHolder.this.cancelLikeSuccessDelayMillis && AbsViewHolder.this.lastCancelTime != 0) {
                    LogUtil.i(AbsViewHolder.TAG, "click too fast ！");
                    return;
                }
                this.likeRequestListener.cancelLike(this.moment);
                return;
            }
            this.likeRequestListener.onClick(this.moment);
            AbsViewHolder.this.mLikeAnimationWindow.showLikeAnimation(view);
            MomentBehavior.likeClick(view.getContext(), this.moment.getWatchId(), this.moment.getContent());
            AbsViewHolder.this.lastCancelTime = System.currentTimeMillis();
        }
    }

    public void getPhotoUrl(final Context context, String key, final String type, final DbMoment moment,
            final ShareAdapter.OnContentOnClickListener listener) {
        LogUtil.d(TAG, "getPhotoUrl#key:" + key + ";type:" + type);
        new MomentPhotoServeImpl(context).getDownloadUrl(new FileUrlParam(key, type), moment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpSubscriber<String>() {
                    @Override
                    public void onHttpError(Throwable throwable) {
                        super.onHttpError(throwable);
                        LogUtil.d(AbsViewHolder.TAG, "getPhotoUrl#e:" + throwable);
                        if ("1".equals(type)) {
                            AbsViewHolder.this.getPhotoUrl(context, "2", moment.getResource(), moment, listener);
                        }
                    }

                    @Override
                    public void onNext(String url) {
                        super.onNext(url);
                        LogUtil.d(AbsViewHolder.TAG, "getPhotoUrl#url:" + url);
                        if (listener != null) {
                            listener.previewPhoto(url);
                        }
                    }
                });
    }

    protected void showReportBtnDialog(final AbsInteractionAdapter.IOnDialogClickLister dialogClickListener) {
        if (ModuleSwitchUtil.queryModuleSwitchByBoolean(this.holderContext, ModuleSwitchConstant.MODULE_REPORT_SUPPORT, false)) {
            DialogUtil.showDialog(com.xtc.moment.module.widget.DialogUtil.makeDoubleIconBtnDialog(this.holderContext,
                    new DoubleIconBtnBean(this.holderContext, false, UiConstants.Color.GRAY, 0, R.string.cancel,
                            new int[]{R.color.color_ffac35, R.color.color_ff6833}, R.drawable.ic_chat_report,
                            R.string.report_text_, true, new DoubleIconBtnBean.OnClickListener() {
                                @Override
                                public void onBottomBtnClick(Dialog dialog, View view) {
                                }

                                @Override
                                public void onLeftBtnClick(Dialog dialog, View view) {
                                    DialogUtil.dismissDialog(dialog);
                                }

                                @Override
                                public void onRightBtnClick(Dialog dialog, View view) {
                                    DialogUtil.dismissDialog(dialog);
                                    dialogClickListener.onRightBtnClick();
                                }
                            })));
        }
    }

    protected boolean isNet() {
        if (NetworkUtils.isConnected(this.holderContext)) {
            return false;
        }
        Context context = this.holderContext;
        com.xtc.ui.widget.toast.view.ToastUtil.showShortCover(context, context.getString(R.string.net_work_exception));
        return true;
    }

    protected void startReportActivity(String watchId, String momentId, String commentId) {
        if (isNet()) {
            Context context = this.holderContext;
            MomentBehavior.clickReportBtnHint(context, new PushReportBean(momentId, watchId, context.getString(R.string.net_work_exception)));
            return;
        }
        Intent intent = new Intent(this.holderContext, ReportActivity.class);
        intent.putExtra("momentId", momentId);
        intent.putExtra("commentId", commentId);
        intent.putExtra(ReportActivity.IN_FORM_SOURCE, 3);
        intent.putExtra(ReportActivity.FRIEND_WATCH_ID, watchId);
        intent.putExtra(ReportActivity.REPORT_TYPE, com.xtc.log.util.TextUtils.isEmpty(commentId) ? 1 : 2);
        MomentBehavior.clickReportBtn(this.holderContext, new PushReportBean(momentId, watchId));
        this.holderContext.startActivity(intent);
    }

    protected void startReportActivity(DbMoment moment, String commentId, String content, String momentContent,
            int contentType) {
        if (isNet()) {
            MomentBehavior.clickReportBtnHint(this.holderContext,
                    new PushReportBean(moment.getMomentId(), moment.getWatchId(),
                            this.holderContext.getString(R.string.net_work_exception)));
            return;
        }
        Intent intent = new Intent(this.holderContext, ReportActivity.class);
        intent.putExtra("momentId", moment.getMomentId());
        intent.putExtra("commentId", "");
        intent.putExtra(ReportActivity.FRIEND_WATCH_ID, moment.getWatchId());
        intent.putExtra(ReportActivity.IN_FORM_SOURCE, 6);
        intent.putExtra(ReportActivity.REPORT_TYPE, com.xtc.log.util.TextUtils.isEmpty(commentId) ? 1 : 2);
        intent.putExtra("moment_content", momentContent);
        if (!com.xtc.log.util.TextUtils.isEmpty(content)) {
            intent.putExtra(ReportActivity.CONTENT, content);
            intent.putExtra(ReportActivity.CONTENT_TYPE, contentType);
        }
        MomentBehavior.clickReportBtn(this.holderContext, new PushReportBean(moment.getMomentId(), moment.getWatchId()));
        this.holderContext.startActivity(intent);
    }

    protected boolean isDisableSend() {
        return this.illegalMessageHandler.isDisableSend();
    }

    protected boolean isDisableSend(boolean showToast) {
        boolean disabled = isDisableSend();
        if (showToast && disabled) {
            Context context = this.holderContext;
            com.xtc.ui.widget.toast.view.ToastUtil.showLongCover(context, context.getResources().getString(R.string.you_dis_say));
        }
        return disabled;
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