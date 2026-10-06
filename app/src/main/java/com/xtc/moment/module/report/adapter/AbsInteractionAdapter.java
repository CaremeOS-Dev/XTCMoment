package com.xtc.moment.module.report.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.xtc.contactapi.contact.bean.ContactBean;
import com.xtc.contactapi.contact.manager.ContactManager;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.manager.ReportDataRecorder;
import com.xtc.moment.module.comment.CommentLbsActivity;
import com.xtc.moment.module.comment.CommentLbsDetailActivity;
import com.xtc.moment.module.illegal.handler.IllegalMessageHandler;
import com.xtc.moment.module.report.ReportActivity;
import com.xtc.moment.module.share.holder.AbsViewHolder;
import com.xtc.moment.module.widget.DoubleIconBtnBean;
import com.xtc.moment.module.widget.NormalThreeIconDialog;
import com.xtc.moment.module.widget.VerticallyLinearLayoutManager;
import com.xtc.moment.net.bean.MomentLbs;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.third.bean.PushReportBean;
import com.xtc.moment.third.behavior.MomentBehavior;
import com.xtc.moment.util.ClickUtils;
import com.xtc.moment.util.MomentLikeViewWindow;
import com.xtc.moment.util.MomentTypeUtil;
import com.xtc.moment.util.ToastUtil;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;
import com.xtc.moment.widget.LbsLayout;
import com.xtc.ui.widget.UiConstants;
import com.xtc.ui.widget.dialog.bean.icon.NormalIconDialogBean;
import com.xtc.ui.widget.dialog.bean.icon.ThreeIconBtnBean;
import com.xtc.ui.widget.util.DialogUtil;
import com.xtc.utils.common.CollectionUtil;
import com.xtc.utils.system.NetworkUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态列表交互相关的公共适配器逻辑：点赞、评论删除、举报入口、LBS 跳转等。
 */
public abstract class AbsInteractionAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

    public static final String PART_REFRESH_PRAISE = "part_refresh_praise";
    protected static final String PART_REFRESH_COMMENT = "part_refresh_comment";
    protected static final String PART_REFRESH_VISIBLE_PIC = "part_refresh_visible_pic";
    protected static final String PART_REFRESH_SHARE_REMINDER = "part_refresh_share_reminder";

    /** 点赞后短时间内不允许再次取消，避免重复请求。 */
    private int cancelLikeSuccessDelayMillis = 3000;

    private final String TAG = getLogTag();
    private final Context mContext;

    protected List<DbMoment> mData = new ArrayList<>();
    protected View mHeaderView;
    protected View mFooterView;
    protected View mEmptyView;
    protected VerticallyLinearLayoutManager verticallyLinearLayoutManager;
    protected MomentLikeViewWindow momentLikeViewWindow;
    protected ConcurrentHashMap<String, Boolean> refreshCommentMap = new ConcurrentHashMap<>();
    protected long lastCancelTime = 0;

    public OnLikeMomentListener mListener;
    public OnDeleteItemListener listener;
    public OnDeleteCommentListener deleteCommentListener;
    public OnMomentCommentListener momentCommentListener;
    public OnPreviewMomentListener previewMomentListener;
    protected OnLikeClickListener onLikeClickListener;

    public interface IOnDialogClickLister {
        void onRightBtnClick();
    }

    public interface OnDeleteItemListener {
        void onDeleteItem(DbMoment moment);
    }

    public interface OnDeleteCommentListener {
        void onDeleteComment(DbMomentComment comment, DbMoment moment, boolean isSelf);
    }

    public interface OnMomentCommentListener {
        void comment(DbMoment moment);

        void reply(DbMoment moment, DbMomentComment comment);

        void loadMoreComment(DbMoment moment);
    }

    public interface OnPreviewMomentListener {
        void onPreviewMoment(DbMoment moment);

        void preViewVideo(String videoUrl, boolean autoPlay);

        void preViewH5(String h5Url, String title);
    }

    public interface OnLikeMomentListener {
        void likeMoment(String momentId, String watchId);

        void cancelLikeMoment(String momentId, String watchId, DbMoment moment);

        void likeAdvertise(String momentId, String watchId);

        void cancelLikeAdvertise(String momentId, String watchId, DbMoment moment);
    }

    public interface OnLikeClickListener {
        void onClick(DbMoment moment);

        void cancelLike(DbMoment moment);
    }

    public abstract String getLogTag();

    public AbsInteractionAdapter(Context context) {
        this.mContext = context;
    }

    /**
     * 处理评论删除入口：根据开关决定弹出“删除 + 举报”或仅“举报”。
     */
    protected void handleCommentDelete(IOnDialogClickLister dialogClickLister,
            OnDeleteCommentListener deleteCommentListener, DbMomentComment comment, DbMoment moment, boolean isSelf) {
        LogUtil.d(this.TAG, "处理与自己有关的动态？：" + isSelf);
        boolean reportSupport = ModuleSwitchUtil.queryModuleSwitchByBoolean(this.mContext,
                ModuleSwitchConstant.MODULE_REPORT_SUPPORT, false);
        boolean commentDeleteSupport = ModuleSwitchUtil.queryModuleSwitchByBoolean(this.mContext,
                ModuleSwitchConstant.MODULE_SWITCH_MOMENT_COMMENT_DELETE, true);
        if (reportSupport && commentDeleteSupport && isSelf) {
            showDeleteAndReportDialog(dialogClickLister, deleteCommentListener, comment, moment, this.mContext);
            return;
        }
        if (reportSupport) {
            showReportBtnDialog(dialogClickLister);
        } else if (deleteCommentListener != null && isSelf) {
            deleteCommentListener.onDeleteComment(comment, moment, true);
        }
    }

    private void showDeleteAndReportDialog(final IOnDialogClickLister dialogClickLister,
            final OnDeleteCommentListener deleteCommentListener, final DbMomentComment comment,
            final DbMoment moment, Context context) {
        NormalIconDialogBean dialogBean = new NormalIconDialogBean(2, false, context, null,
                new int[]{R.color.color_ffac35, R.color.color_ff6833}, R.drawable.ic_chat_report,
                R.string.report_text_, UiConstants.Color.RED, R.drawable.delete, R.string.delete,
                R.string.cancel, new ThreeIconBtnBean.OnClickListener() {
                    @Override
                    public void onLeftBtnClick(Dialog dialog, View view) {
                        DialogUtil.dismissDialog(dialog);
                        dialogClickLister.onRightBtnClick();
                    }

                    @Override
                    public void onRightBtnClick(Dialog dialog, View view) {
                        if (deleteCommentListener != null) {
                            deleteCommentListener.onDeleteComment(comment, moment, false);
                        }
                        DialogUtil.dismissDialog(dialog);
                    }

                    @Override
                    public void onBottomBtnClick(Dialog dialog, View view) {
                        DialogUtil.dismissDialog(dialog);
                    }
                });
        NormalThreeIconDialog dialog = new NormalThreeIconDialog(context, true, true);
        dialog.initData(dialogBean);
        DialogUtil.showDialog(dialog);
    }

    protected void showReportBtnDialog(final IOnDialogClickLister dialogClickLister) {
        if (!ModuleSwitchUtil.queryModuleSwitchByBoolean(this.mContext,
                ModuleSwitchConstant.MODULE_REPORT_SUPPORT, false)) {
            return;
        }
        DialogUtil.showDialog(com.xtc.moment.module.widget.DialogUtil.makeDoubleIconBtnDialog(this.mContext,
                new DoubleIconBtnBean(this.mContext, false, UiConstants.Color.GRAY, 0, R.string.cancel,
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
                                dialogClickLister.onRightBtnClick();
                            }
                        })));
    }

    protected boolean isNet() {
        if (NetworkUtils.isConnected(this.mContext)) {
            return false;
        }
        Context context = this.mContext;
        com.xtc.ui.widget.toast.view.ToastUtil.showShortCover(context, context.getString(R.string.net_work_exception));
        return true;
    }

    /** 跳转到举报页面；无网络时只记录埋点并提示。 */
    protected void startReportActivity(String friendWatchId, String momentId, String commentId, String momentWatchId) {
        if (isNet()) {
            Context context = this.mContext;
            MomentBehavior.clickReportBtnHint(context,
                    new PushReportBean(momentId, friendWatchId, context.getString(R.string.net_work_exception)));
            return;
        }
        Intent intent = new Intent(this.mContext, ReportActivity.class);
        intent.putExtra(ReportActivity.MOMENT_ID, momentId);
        intent.putExtra(ReportActivity.COMMENT_ID, commentId);
        intent.putExtra(ReportActivity.FRIEND_WATCH_ID, friendWatchId);
        intent.putExtra(ReportActivity.IN_FORM_SOURCE, 3);
        intent.putExtra(ReportActivity.MOMENT_WATCH_ID, momentWatchId);
        intent.putExtra(ReportActivity.REPORT_TYPE, TextUtils.isEmpty(commentId)
                ? ReportActivity.MOMENT_TYPE : ReportActivity.COMMENT_TYPE);
        MomentBehavior.clickReportBtn(this.mContext, new PushReportBean(momentId, friendWatchId));
        this.mContext.startActivity(intent);
    }

    protected boolean isDisableSend() {
        return IllegalMessageHandler.getInstance(this.mContext).isDisableSend();
    }

    protected boolean isDisableSend(boolean showTip) {
        boolean disableSend = isDisableSend();
        if (showTip && disableSend) {
            Context context = this.mContext;
            com.xtc.ui.widget.toast.view.ToastUtil.showLongCover(context, context.getResources().getString(R.string.you_dis_say));
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

    public void setOnDeleteItemListener(OnDeleteItemListener listener) {
        this.listener = listener;
    }

    public void setOnDeleteCommentListener(OnDeleteCommentListener listener) {
        this.deleteCommentListener = listener;
    }

    public void setOnMomentCommentListener(OnMomentCommentListener listener) {
        this.momentCommentListener = listener;
    }

    public void setOnPreviewMomentListener(OnPreviewMomentListener listener) {
        this.previewMomentListener = listener;
    }

    public void setOnLikeMomentListener(OnLikeMomentListener listener) {
        this.mListener = listener;
    }

    public void setOnLikeClickListener(OnLikeClickListener listener) {
        this.onLikeClickListener = listener;
    }

    /** 评论数量变化后局部刷新对应的动态条目。 */
    protected void refreshCommentData(DbMomentComment comment, boolean isAdd) {
        LogUtil.d(this.TAG, "refreshCommentData dbMomentComment = " + comment + " null? "
                + CollectionUtil.isEmpty(this.mData));
        if (comment == null || CollectionUtil.isEmpty(this.mData)) {
            return;
        }
        int index = -1;
        for (int i = 0; i < this.mData.size(); i++) {
            DbMoment moment = this.mData.get(i);
            if (moment.getMomentId() != null && moment.getMomentId().equals(comment.getMomentId())) {
                index = i;
                break;
            }
        }
        LogUtil.d(this.TAG, "dest index = " + index);
        if (index == -1) {
            return;
        }
        this.verticallyLinearLayoutManager.setScrollEnabled(false);
        DbMoment moment = this.mData.get(index);
        int commentsTotalCount = moment.getCommentsTotalCount();
        if (isAdd) {
            calculateAddCommentCount(commentsTotalCount, comment, moment);
        } else {
            handleOtherCommentScene(comment, commentsTotalCount, moment);
        }
        notifyItemChanged(getHolderPosition(moment), PART_REFRESH_COMMENT);
        this.mData.set(index, moment);
        this.verticallyLinearLayoutManager.setScrollEnabled(true);
    }

    private void handleOtherCommentScene(DbMomentComment comment, int commentsTotalCount, DbMoment moment) {
        List<DbMomentComment> comments = moment.getComments();
        if (CollectionUtil.isEmpty(comments) || !comments.contains(comment)) {
            return;
        }
        boolean removed = comments.remove(comment);
        if (comments.size() > 5 || moment.getCommentsTotalCount() <= 5) {
            boolean refreshing = this.refreshCommentMap.containsKey(comment.getMomentId());
            LogUtil.d(this.TAG, "refreshCommentData: " + refreshing + "  " + comment.getMomentId()
                    + ",remove - " + removed + " - " + comments.size() + " total - " + commentsTotalCount);
            int total = commentsTotalCount;
            if (refreshing && removed && total > 0) {
                total--;
                moment.setCommentsTotalCount(total);
            } else if (CollectionUtil.isEmpty(comments) || (removed && total > 5)) {
                total = total <= 5 ? 0 : total - 1;
                moment.setCommentsTotalCount(total);
            }
            if (comments.size() > total) {
                moment.setCommentsTotalCount(comments.size());
            }
            moment.setComments(comments);
        }
    }

    private void calculateAddCommentCount(int commentsTotalCount, DbMomentComment comment, DbMoment moment) {
        List<DbMomentComment> comments = moment.getComments();
        if (comments == null) {
            comments = new ArrayList<>();
        }
        LogUtil.d(this.TAG, " momentComments.size = " + comments.size());
        if (comments.size() < 5) {
            comments.add(comment);
        }
        int size = comments.size();
        int total = commentsTotalCount;
        if (size >= 5 || total <= 5) {
            total++;
        }
        if (size > total) {
            total = size;
        }
        moment.setCommentsTotalCount(total);
        moment.setComments(comments);
    }

    public int getHolderPosition(DbMoment moment) {
        List<DbMoment> data = this.mData;
        if (data == null || data.isEmpty()) {
            return -1;
        }
        if (this.mHeaderView != null) {
            return this.mData.indexOf(moment) + 1;
        }
        return this.mData.indexOf(moment);
    }

    /** 点赞按钮点击：自己点自己进入点赞列表，他人点赞做网络请求。 */
    public void onMomentLikeClick(AbsViewHolder holder, View view) {
        if (!ClickUtils.isFastClick()) {
            LogUtil.i(this.TAG, "click isFastClick");
            return;
        }
        String watchId = AccountInfoServerImpl.getInstance(this.mContext).getWatchAccountInfo()
                .getWatchId(this.mContext);
        int adapterPosition = holder.getAdapterPosition();
        int dataItemPosition = getDataItemPosition(adapterPosition);
        if (CollectionUtil.isEmpty(this.mData) || dataItemPosition < 0 || dataItemPosition >= this.mData.size()) {
            LogUtil.w(this.TAG, "onClick: error index");
            return;
        }
        DbMoment moment = this.mData.get(dataItemPosition);
        LogUtil.d(this.TAG, "onClick：" + moment + ", adapterPosition: " + adapterPosition
                + ", dataItemPosition: " + dataItemPosition);
        if (watchId != null && watchId.equals(moment.getWatchId())) {
            holder.momentLike.startMomentLikesActivity(moment, this.mContext);
            return;
        }
        if (isDisableSend(true)) {
            LogUtil.i(this.TAG, "disable send");
            return;
        }
        if (moment.isEnableLike()) {
            this.mListener.likeMoment(moment.getMomentId(), moment.getWatchId());
            this.momentLikeViewWindow.showLikeAnimation(view);
            MomentBehavior.likeClick(this.mContext, moment.getWatchId(), moment.getContent());
            this.lastCancelTime = System.currentTimeMillis();
            return;
        }
        if (!ModuleSwitchUtil.queryModuleSwitchByBoolean(this.mContext,
                ModuleSwitchConstant.MODULE_SWITCH_MOMENT_CANCEL_PRAISE, false)) {
            Context context = this.mContext;
            ToastUtil.showShortCover(context, context.getString(R.string.moment_give_like));
            LogUtil.i(this.TAG, "取消点赞全网开关关闭");
            return;
        }
        long now = System.currentTimeMillis();
        long lastTime = this.lastCancelTime;
        if (now - lastTime <= this.cancelLikeSuccessDelayMillis && lastTime != 0) {
            LogUtil.i(this.TAG, "fast click");
            return;
        }
        if (MomentTypeUtil.isOfficialType(moment.getType().intValue())) {
            this.mListener.cancelLikeAdvertise(moment.getMomentId(), moment.getWatchId(), moment);
            LogUtil.i(this.TAG, "advCancelLikedClick 取消点赞");
            MomentBehavior.advCancelLikedClick(this.mContext, moment.getMomentId());
            return;
        }
        this.mListener.cancelLikeMoment(moment.getMomentId(), moment.getWatchId(), moment);
    }

    public int getDataItemPosition(int adapterPosition) {
        return this.mHeaderView != null ? adapterPosition - 1 : adapterPosition;
    }

    /** 点击评论的删除入口。 */
    public void dealCommentDeleteClick(DbMoment moment, final DbMomentComment comment, String myWatchId, boolean isSelf) {
        if (!(myWatchId != null && myWatchId.equals(comment.getWatchId()))) {
            handleCommentDelete(new IOnDialogClickLister() {
                @Override
                public void onRightBtnClick() {
                    ReportDataRecorder.recordMomentComment(comment);
                    AbsInteractionAdapter.this.startReportActivity(comment.getWatchId(), comment.getMomentId(),
                            comment.getCommentId(), comment.getMomentWatchId());
                }
            }, this.deleteCommentListener, comment, moment, isSelf);
            return;
        }
        if (this.deleteCommentListener != null) {
            this.deleteCommentListener.onDeleteComment(comment, moment, true);
        }
    }

    protected boolean isActivityDestroyed() {
        Activity activity = getActivityContext();
        return activity == null || activity.isFinishing() || activity.isDestroyed();
    }

    private Activity getActivityContext() {
        Context context = this.mContext;
        if (context instanceof Activity) {
            return (Activity) context;
        }
        return null;
    }

    /** 点击动态的位置信息：自己可评价，好友则查看评价详情。 */
    protected void dealLbsClick(LbsLayout lbsLayout, final DbMoment moment) {
        LogUtil.i(this.TAG, "dealLbsClick");
        if (moment == null) {
            LogUtil.i(this.TAG, "dealLbsClick, momentBean is empty");
            return;
        }
        String location = moment.getLocation();
        if (TextUtils.isEmpty(location) || lbsLayout == null || !moment.isLbsSwitch()) {
            LogUtil.i(this.TAG, "lbs not support, location = " + location + " lbsSwitch = " + moment.isLbsSwitch());
            return;
        }
        lbsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String watchId = AccountInfoServerImpl.getInstance(AbsInteractionAdapter.this.mContext)
                        .getWatchAccountInfo().getWatchId(AbsInteractionAdapter.this.mContext);
                if (watchId.equals(moment.getWatchId())) {
                    LogUtil.i(AbsInteractionAdapter.this.TAG, "click self lbs");
                    MomentLbs momentLbs = moment.getMomentLbs();
                    if (momentLbs == null || momentLbs.hasEvaluation()) {
                        CommentLbsDetailActivity.start(AbsInteractionAdapter.this.mContext, "", moment.getMomentLbs());
                    } else {
                        CommentLbsActivity.start(AbsInteractionAdapter.this.mContext, moment.getMomentId(),
                                watchId, moment.getLocation());
                    }
                    return;
                }
                LogUtil.i(AbsInteractionAdapter.this.TAG, "click friends lbs");
                ContactBean contactBean = ContactManager.getInstance(AbsInteractionAdapter.this.mContext)
                        .getContactWithoutShortNumberByWatchIdSync(moment.getWatchId());
                CommentLbsDetailActivity.start(AbsInteractionAdapter.this.mContext,
                        contactBean != null ? contactBean.getName() : "", moment.getMomentLbs());
            }
        });
    }
}
