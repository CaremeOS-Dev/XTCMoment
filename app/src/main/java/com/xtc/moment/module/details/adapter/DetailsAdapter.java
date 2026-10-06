package com.xtc.moment.module.details.adapter;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.xtc.contactapi.contact.bean.ContactBean;
import com.xtc.contactapi.contact.manager.ContactManager;
import com.xtc.contactapi.contacthead.impl.ContactHeadManager;
import com.xtc.contactapi.contacthead.interfaces.IShowHeadToViewStrategy;
import com.xtc.log.LogUtil;
import com.xtc.moment.LogTag;
import com.xtc.moment.R;
import com.xtc.moment.asynclayout.AsyncLayoutLoader;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.manager.ReportDataRecorder;
import com.xtc.moment.module.MomentAdapter;
import com.xtc.moment.module.bean.LivePhotoMsg;
import com.xtc.moment.module.bean.PhotoMsg;
import com.xtc.moment.module.bean.ShareWebMoment;
import com.xtc.moment.module.report.adapter.AbsInteractionAdapter;
import com.xtc.moment.module.scope.FriendsVisibleRangeActivity;
import com.xtc.moment.module.viewholder.AbsViewHolder;
import com.xtc.moment.module.viewholder.LivePhotoViewHolder;
import com.xtc.moment.module.viewholder.PhotoViewHolder;
import com.xtc.moment.module.viewholder.PhotosViewHolder;
import com.xtc.moment.module.viewholder.ShareAppViewHolder;
import com.xtc.moment.module.viewholder.ShareImageViewHolder;
import com.xtc.moment.module.viewholder.ShareLivePhotoViewHolder;
import com.xtc.moment.module.viewholder.ShareTextViewHolder;
import com.xtc.moment.module.viewholder.ShareVideoViewHolder;
import com.xtc.moment.module.viewholder.ShareWebViewHolder;
import com.xtc.moment.module.viewholder.VideoViewHolder;
import com.xtc.moment.module.viewholder.ViewHolder;
import com.xtc.moment.module.viewholder.comment.MomentCommentAdapter;
import com.xtc.moment.module.widget.AbsMomentView;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.serve.IAccountInfoServe;
import com.xtc.moment.third.behavior.MomentBehavior;
import com.xtc.moment.util.ClickUtils;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.MomentLikeViewWindow;
import com.xtc.moment.util.MomentTypeUtil;
import com.xtc.moment.util.SystemUtil;
import com.xtc.moment.util.TimeUtils;
import com.xtc.moment.util.ToastUtil;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;
import com.xtc.utils.common.CollectionUtil;
import com.xtc.utils.encode.JSONUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 动态详情页适配器：第 0 项是动态本体（含评论区），最后一项是底部占位。
 *
 * <p>除展示外，还负责点赞、评论、删除评论、可见范围入口以及局部刷新。
 */
public class DetailsAdapter extends AbsInteractionAdapter<RecyclerView.ViewHolder>
        implements LifecycleOwner, IShowHeadToViewStrategy {

    private static final String TAG = LogTag.tag("DetailsAdapter");

    private static final String PART_REFRESH_PRAISE = "part_refresh_praise_details";
    private static final String PART_REFRESH_COMMENT = "part_refresh_comment_details";
    private static final String PART_REFRESH_PUSH_LIKE = "part_refresh_praise_push_like_details";
    private static final String PART_REFRESH_LOCAL_PATH = "PART_REFRESH_LOCAL_PATH_details";
    private static final String PART_REFRESH_VISIBLE_PIC = "part_refresh_visible_pic";

    private static final int TYPE_HEADER = 10;
    private static final int TYPE_FOOTER = 11;

    private Context mContext;
    private LifecycleRegistry lifecycleRegistry;
    private final MomentLikeViewWindow momentLikeViewWindow;
    private ContactManager contactManager;
    private IAccountInfoServe accountInfoServe;

    private AbsViewHolder headerHolder;
    private AbsViewHolder absViewHolder;
    private View mFooterView;
    private DbMoment momentBean;
    private List<DbMomentComment> dbMomentComments;
    private String selfWatchId = "";
    private String myName = "";
    private String myIconPath = "";
    private final String meString;
    private boolean commentSwitch;
    private long lastCancelTime = 0;
    private int cancelLikeSuccessDelayMillis = 3000;

    @Override
    public void showContactPortrait(Context context, ContactHeadManager headManager, ContactBean contactBean,
            View view, BitmapDrawable headBitmap) {
    }

    public DetailsAdapter(Context context) {
        super(context);
        this.mContext = context;
        this.lifecycleRegistry = new LifecycleRegistry(this);
        this.lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        this.momentLikeViewWindow = new MomentLikeViewWindow();
        this.momentLikeViewWindow.initLikeAnimation(context, this, this.lifecycleRegistry);
        this.contactManager = ContactManager.getInstance(context);
        this.commentSwitch = ModuleSwitchUtil.queryModuleSwitchByBoolean(context,
                ModuleSwitchConstant.MODULE_SWITCH_MOMENT_COMMENT, false);
        LogUtil.d(TAG, "MomentAdapter: commentSwitch 120 = " + this.commentSwitch);
        this.accountInfoServe = AccountInfoServerImpl.getInstance(context);
        this.selfWatchId = this.accountInfoServe.getWatchAccountInfo().getWatchId(context);
        this.myName = this.accountInfoServe.getWatchAccountInfo().getName(context);
        this.myIconPath = this.accountInfoServe.getMyHeadIconPath();
        this.meString = context.getString(R.string.me);
        this.dbMomentComments = new ArrayList<>();
    }

    /** 好友被删除后，移除其评论。 */
    public void deleteFriendInfo(String watchId) {
        if (TextUtils.isEmpty(watchId)) {
            return;
        }
        boolean removed = false;
        if (!CollectionUtil.isEmpty(this.dbMomentComments)) {
            Iterator<DbMomentComment> iterator = this.dbMomentComments.iterator();
            while (iterator.hasNext()) {
                DbMomentComment comment = iterator.next();
                if (isDeleteComment(watchId, comment)) {
                    iterator.remove();
                    removed = true;
                    LogUtil.i(TAG, "remove friend DbMomentComment:" + comment.toString());
                }
            }
        }
        if (removed) {
            notifyDataSetChanged();
        }
    }

    private boolean isDeleteComment(String watchId, DbMomentComment comment) {
        return comment != null
                && ((!TextUtils.isEmpty(comment.getWatchId()) && comment.getWatchId().equals(watchId))
                || (!TextUtils.isEmpty(comment.getReplyId()) && comment.getReplyId().equals(watchId)));
    }

    public void setMoment(DbMoment moment) {
        this.momentBean = moment;
        LogUtil.d(TAG, "setMoment: " + this.momentBean);
    }

    public void setDbMomentComments(List<DbMomentComment> comments) {
        ArrayList<DbMomentComment> normalizedComments = new ArrayList<>();
        for (DbMomentComment comment : comments) {
            if (!TextUtils.isEmpty(comment.getWatchId()) && this.selfWatchId.equals(comment.getWatchId())) {
                comment.setWatchName(this.meString);
            }
            if (!TextUtils.isEmpty(comment.getReplyId()) && this.selfWatchId.equals(comment.getReplyId())) {
                comment.setReplyName(this.meString);
            }
            normalizedComments.add(comment);
        }
        this.dbMomentComments.addAll(normalizedComments);
    }

    public void setFooterView(View footerView) {
        this.mFooterView = footerView;
    }

    public int getMomentContentHeight() {
        AbsViewHolder holder = this.absViewHolder;
        if (holder != null) {
            return holder.getMomentContentHeight();
        }
        return 0;
    }

    public void setCancelLikeTime(long cancelLikeTime) {
        this.lastCancelTime = cancelLikeTime;
    }

    public void refreshLikeData(DbMoment moment) {
        this.momentBean.setEnableLike(moment.isEnableLike());
        refreshData(moment, PART_REFRESH_PRAISE);
    }

    public void refreshPushLikeData() {
        DbMoment moment = this.momentBean;
        moment.setLikeTotal(Integer.valueOf(moment.getLikeTotal().intValue() + 1));
        refreshData(this.momentBean, PART_REFRESH_PUSH_LIKE);
    }

    public void refreshLocalPathData(DbMoment moment) {
        this.momentBean = moment;
        refreshData(moment, PART_REFRESH_LOCAL_PATH);
    }

    public void refreshVisiblePicData(DbMoment moment) {
        this.momentBean = moment;
        refreshData(moment, PART_REFRESH_VISIBLE_PIC);
    }

    public void refreshLikeView(boolean liked) {
        DbMoment moment = this.momentBean;
        moment.setEnableLike(!moment.isEnableLike());
        if (liked) {
            moment.setLikeTotal(Integer.valueOf(moment.getLikeTotal().intValue() + 1));
        } else {
            moment.setLikeTotal(Integer.valueOf(moment.getLikeTotal().intValue() - 1));
        }
        refreshData(this.momentBean, PART_REFRESH_PRAISE);
    }

    private void refreshData(DbMoment moment, String payload) {
        notifyItemChanged(0, payload);
    }

    public void addCommentData(DbMomentComment comment) {
        refreshCommentData(comment, true);
    }

    public void removeCommentData(DbMomentComment comment) {
        refreshCommentData(comment, false);
    }

    @Override
    public void refreshCommentData(DbMomentComment comment, boolean isAdd) {
        if (comment == null || this.momentBean == null) {
            return;
        }
        if (isAdd) {
            if (this.dbMomentComments == null) {
                this.dbMomentComments = new ArrayList<>();
            }
            this.dbMomentComments.add(comment);
            DbMoment moment = this.momentBean;
            moment.setCommentsTotalCount(moment.getCommentsTotalCount() + 1);
        } else {
            List<DbMomentComment> comments = this.dbMomentComments;
            if (comments != null && !comments.isEmpty() && this.dbMomentComments.contains(comment)) {
                this.dbMomentComments.remove(comment);
                DbMoment moment = this.momentBean;
                moment.setCommentsTotalCount(moment.getCommentsTotalCount() - 1);
            }
        }
        this.momentBean.setComments(this.dbMomentComments);
        notifyItemChanged(0, PART_REFRESH_COMMENT);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogUtil.d(TAG, "onCreateViewHolder: ");
        if (viewType == TYPE_HEADER) {
            this.headerHolder = getHeaderView(parent);
            if (this.headerHolder.commentRecyclerView != null) {
                this.headerHolder.commentAdapter.setOnMomentCommentListener(new MomentCommentAdapter.OnMomentCommentListener() {
                    @Override
                    public void onLoadMoreCommentClick(int position, DbMoment moment) {
                    }

                    @Override
                    public void onMomentCommentClick(int position, DbMomentComment comment) {
                        if (!DetailsAdapter.this.commentSwitch || DetailsAdapter.this.isDisableSend(true)) {
                            LogUtil.d(TAG, "onMomentCommentClick: commentSwitch = "
                                    + DetailsAdapter.this.commentSwitch);
                            return;
                        }
                        LogUtil.d(TAG, "index:" + position + ";dbMomentComment:" + comment);
                        if (DetailsAdapter.this.momentCommentListener == null
                                || DetailsAdapter.this.checkMomentIsNull("onMomentCommentClick")) {
                            return;
                        }
                        DetailsAdapter.this.momentCommentListener.reply(
                                DetailsAdapter.this.headerHolder.getDbMoment(), comment);
                    }
                });
                this.headerHolder.commentAdapter.setOnCommentDeleteListener(new MomentCommentAdapter.OnCommentDeleteListener() {
                    @Override
                    public void onCommentDeleteClick(int position, DbMomentComment comment) {
                        LogUtil.d(TAG, "index: 详情" + position + ";dbMomentComment:" + comment + "===="
                                + DetailsAdapter.this.headerHolder.getDbMoment().getType());
                        DbMoment moment = DetailsAdapter.this.headerHolder.getDbMoment();
                        boolean isSelf = DetailsAdapter.this.selfWatchId != null
                                && DetailsAdapter.this.selfWatchId.equals(moment.getWatchId());
                        DetailsAdapter.this.dealCommentDeleteClick(moment, comment,
                                DetailsAdapter.this.selfWatchId, isSelf);
                    }
                });
            }
            if (this.headerHolder.ivMomentComment != null) {
                this.headerHolder.ivMomentComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (SystemUtil.isFastDoubleClick()) {
                            LogUtil.i(TAG, "onClick: click too fast.");
                            return;
                        }
                        if (!DetailsAdapter.this.commentSwitch || DetailsAdapter.this.isDisableSend(true)
                                || DetailsAdapter.this.momentCommentListener == null
                                || DetailsAdapter.this.checkMomentIsNull("ivMomentComment")) {
                            return;
                        }
                        DetailsAdapter.this.momentCommentListener.comment(
                                DetailsAdapter.this.headerHolder.getDbMoment());
                    }
                });
            }
            if (this.headerHolder.momentLike != null) {
                this.headerHolder.momentLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!ClickUtils.isFastClick()) {
                            LogUtil.i(TAG, "click isFastClick");
                            return;
                        }
                        String watchId = AccountInfoServerImpl.getInstance(DetailsAdapter.this.mContext)
                                .getWatchAccountInfo().getWatchId(DetailsAdapter.this.mContext);
                        int adapterPosition = DetailsAdapter.this.headerHolder.getAdapterPosition();
                        int dataItemPosition = DetailsAdapter.this.getDataItemPosition(adapterPosition);
                        if (DetailsAdapter.this.checkMomentIsNull("momentLike")) {
                            return;
                        }
                        DbMoment moment = DetailsAdapter.this.momentBean;
                        LogUtil.d(TAG, "onClick：" + moment + ", adapterPosition: " + adapterPosition
                                + ", dataItemPosition: " + dataItemPosition);
                        if (watchId != null && watchId.equals(moment.getWatchId())) {
                            DetailsAdapter.this.headerHolder.momentLike
                                    .startMomentLikesActivity(moment, DetailsAdapter.this.mContext);
                            return;
                        }
                        if (DetailsAdapter.this.isDisableSend(true)) {
                            return;
                        }
                        if (moment.isEnableLike()) {
                            if (MomentTypeUtil.isOfficialType(moment.getType().intValue())) {
                                DetailsAdapter.this.mListener.likeAdvertise(moment.getMomentId(),
                                        moment.getWatchId());
                                MomentBehavior.advLikedClick(DetailsAdapter.this.mContext, moment.getMomentId());
                            } else {
                                DetailsAdapter.this.mListener.likeMoment(moment.getMomentId(),
                                        moment.getWatchId());
                            }
                            DetailsAdapter.this.momentLikeViewWindow.showLikeAnimation(view);
                            MomentBehavior.likeClick(DetailsAdapter.this.mContext, moment.getWatchId(),
                                    moment.getContent());
                            DetailsAdapter.this.lastCancelTime = System.currentTimeMillis();
                            return;
                        }
                        if (!ModuleSwitchUtil.queryModuleSwitchByBoolean(DetailsAdapter.this.mContext,
                                ModuleSwitchConstant.MODULE_SWITCH_MOMENT_CANCEL_PRAISE, false)) {
                            ToastUtil.showShortCover(DetailsAdapter.this.mContext,
                                    DetailsAdapter.this.mContext.getString(R.string.moment_give_like));
                            LogUtil.i(TAG, "取消点赞全网开关关闭");
                            return;
                        }
                        if (System.currentTimeMillis() - DetailsAdapter.this.lastCancelTime
                                <= DetailsAdapter.this.cancelLikeSuccessDelayMillis
                                && DetailsAdapter.this.lastCancelTime != 0) {
                            LogUtil.i(TAG, "fast click");
                            return;
                        }
                        if (MomentTypeUtil.isOfficialType(moment.getType().intValue())) {
                            DetailsAdapter.this.mListener.cancelLikeAdvertise(moment.getMomentId(),
                                    moment.getWatchId(), moment);
                            MomentBehavior.advCancelLikedClick(DetailsAdapter.this.mContext, moment.getMomentId());
                            return;
                        }
                        DetailsAdapter.this.mListener.cancelLikeMoment(moment.getMomentId(), moment.getWatchId(),
                                moment);
                    }
                });
            }
            return this.headerHolder;
        }
        return new ViewHolder(this.mFooterView);
    }

    private boolean checkMomentIsNull(String caller) {
        if (this.headerHolder == null) {
            return true;
        }
        boolean momentNull = this.momentBean == null;
        boolean holderMomentNull = this.headerHolder.getDbMoment() == null;
        LogUtil.d(TAG, caller + "checkMomentIsNull: " + momentNull + holderMomentNull);
        return holderMomentNull || momentNull;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            LogUtil.d(TAG, "全局刷新，payload is empty" + this.mContext);
            onBindViewHolder(holder, position);
            return;
        }
        String payload = (String) payloads.get(0);
        AbsViewHolder absHolder = (AbsViewHolder) holder;
        absHolder.setContext(this.mContext);
        absHolder.setDbMoment(this.momentBean);
        if (PART_REFRESH_PRAISE.equals(payload)) {
            if (!this.selfWatchId.equals(this.momentBean.getWatchId())) {
                absHolder.momentLike.setLike(this.mContext, !this.momentBean.isEnableLike());
            }
            absHolder.momentLike.setCount(this.momentBean.getLikeTotal() != null
                    ? this.momentBean.getLikeTotal().intValue() : 0);
            return;
        }
        if (PART_REFRESH_COMMENT.equals(payload)) {
            List<DbMomentComment> comments = this.momentBean.getComments();
            absHolder.showRecyclerComment();
            if (comments != null && !comments.isEmpty()) {
                absHolder.commentRecyclerView.setAdapter(absHolder.commentAdapter);
                absHolder.commentAdapter.setDatas(this.mContext, comments, this.selfWatchId, this.momentBean);
            } else {
                absHolder.commentRecyclerView.setVisibility(View.GONE);
            }
            return;
        }
        if (PART_REFRESH_LOCAL_PATH.equals(payload)) {
            setContentOnClickListener(this.mContext, absHolder, this.momentBean);
            return;
        }
        if (PART_REFRESH_PUSH_LIKE.equals(payload)) {
            absHolder.momentLike.setCount(this.momentBean.getLikeTotal() != null
                    ? this.momentBean.getLikeTotal().intValue() : 0);
            return;
        }
        if (!PART_REFRESH_VISIBLE_PIC.equals(payload) || absHolder.ivMomentRange == null
                || -1 == this.momentBean.getPermissionType()) {
            return;
        }
        if (this.momentBean.getPermissionType() == 0) {
            absHolder.ivMomentRange.setVisibility(View.GONE);
        } else {
            absHolder.ivMomentRange.setVisibility(View.VISIBLE);
            absHolder.ivMomentRange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemUtil.isFastDoubleClick()) {
                        LogUtil.i(TAG, "onClick: click too fast.");
                        return;
                    }
                    Intent intent = new Intent(DetailsAdapter.this.mContext, FriendsVisibleRangeActivity.class);
                    intent.putExtra("moment_id", DetailsAdapter.this.momentBean.getMomentId());
                    DetailsAdapter.this.mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        LogUtil.d(TAG, "onBindViewHolder() called with: position = [" + position + "]");
        int itemViewType = getItemViewType(position);
        if (this.momentBean == null || isActivityDestroyed()) {
            return;
        }
        LogUtil.d(TAG, "onBindViewHolder: " + this.selfWatchId + "  " + this.momentBean.getWatchId());
        boolean isOfficialType = MomentTypeUtil.isOfficialType(this.momentBean);
        String myWatchId = this.selfWatchId;
        boolean isSelf = myWatchId != null && myWatchId.equals(this.momentBean.getWatchId());
        if (itemViewType != TYPE_HEADER) {
            return;
        }
        String friendName = null;
        String photoPath;
        ContactBean contactBean;
        if (isSelf) {
            if (com.xtc.log.util.TextUtils.isEmpty(this.myName)
                    || this.myName.equals(this.mContext.getString(R.string.unknown_watch))) {
                this.myName = AccountInfoServerImpl.getInstance(this.mContext).getWatchAccountInfo()
                        .getName(this.mContext);
            }
            friendName = com.xtc.log.util.TextUtils.isEmpty(this.myName)
                    ? this.mContext.getString(R.string.unknown_watch) : this.myName;
            photoPath = this.myIconPath;
            LogUtil.i(TAG, "moment adapter my name:" + friendName + ",iconPath:" + photoPath);
            contactBean = null;
        } else {
            ContactBean friendContact = this.contactManager.getContactByWatchIdSync(this.momentBean.getWatchId());
            if (friendContact != null) {
                friendName = friendContact.getName();
                photoPath = friendContact.getPhotoPath();
            } else if (MomentTypeUtil.isOfficialType(this.momentBean.getType().intValue())) {
                photoPath = "";
            } else {
                LogUtil.d(TAG, "showContact is null");
                return;
            }
            LogUtil.i(TAG, "moment adapter friend name:" + friendName + ",iconPath:" + photoPath);
            contactBean = friendContact;
        }
        AbsViewHolder absHolder = (AbsViewHolder) holder;
        this.absViewHolder = absHolder;
        absHolder.setContext(this.mContext);
        absHolder.setDbMoment(this.momentBean);
        absHolder.momentView.setContext(this.mContext);
        absHolder.showRecyclerComment();
        absHolder.momentView.rlMomentSender.setVisibility(View.VISIBLE);
        if (absHolder.commentRecyclerView != null && absHolder.commentAdapter != null) {
            List<DbMomentComment> comments = this.dbMomentComments;
            if (comments != null && !comments.isEmpty()) {
                absHolder.commentRecyclerView.setAdapter(absHolder.commentAdapter);
                absHolder.commentAdapter.setDatas(this.mContext, this.dbMomentComments, this.selfWatchId,
                        this.momentBean);
            } else {
                absHolder.commentRecyclerView.setVisibility(View.GONE);
            }
        }
        setOfficialLabel(absHolder, this.momentBean);
        setMomentName(this.momentBean, absHolder, isSelf, friendName);
        setMomentIcon(this.momentBean, absHolder, isSelf, photoPath, contactBean);
        int momentType = this.momentBean.getType().intValue();
        if (momentType == 0 || momentType == 1 || momentType == 3 || momentType == 7) {
            setTextData(absHolder, this.momentBean, isSelf || isOfficialType);
        } else if (momentType == 2) {
            setLocationData(absHolder, this.momentBean, isSelf);
        } else if (momentType == 10) {
            setOfficialTextData(absHolder, this.momentBean, isSelf || isOfficialType);
        } else if (isPhotoType(momentType)) {
            setPhotoData(absHolder, this.momentBean, this.mContext, !isSelf || isOfficialType);
        } else if (isSharePhotoType(momentType)) {
            setPhotoData(absHolder, this.momentBean, this.mContext, true);
        } else {
            setUnknownData(absHolder, this.momentBean);
        }
        int likeTotal = this.momentBean.getLikeTotal() == null ? 0 : this.momentBean.getLikeTotal().intValue();
        if (isSelf) {
            absHolder.momentLike.setLike(this.mContext, false);
        } else {
            absHolder.momentLike.setLike(this.mContext, !this.momentBean.isEnableLike());
        }
        absHolder.tvTime.setText(TimeUtils.getTime(this.mContext, this.momentBean.getCreateTime().longValue()));
        absHolder.momentLike.setCount(likeTotal);
        absHolder.momentView.setIconOnClickListener(this.momentBean);
    }

    private static boolean isPhotoType(int momentType) {
        switch (momentType) {
            case 4:
            case 5:
            case 6:
            case 8:
            case 9:
            case 11:
            case 12:
            case 13:
            case 14:
                return true;
            default:
                return false;
        }
    }

    private static boolean isSharePhotoType(int momentType) {
        switch (momentType) {
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
                return true;
            default:
                return false;
        }
    }
    private void setOfficialLabel(AbsViewHolder holder, DbMoment moment) {
        if (!MomentTypeUtil.isOfficialType(moment.getType().intValue())) {
            if (holder.momentView.ivOfficialEnterpriseIcon != null) {
                holder.momentView.ivOfficialEnterpriseIcon.setVisibility(View.GONE);
            }
            if (holder.momentView.ivOfficialEnterpriseIconBg != null) {
                holder.momentView.ivOfficialEnterpriseIconBg.setVisibility(View.GONE);
            }
            return;
        }
        if (MomentTypeUtil.isXTCOfficialType(moment.getOfficial())) {
            holder.momentView.ivOfficialLabel.setVisibility(View.VISIBLE);
            holder.momentView.ivOfficialEnterpriseIcon.setVisibility(View.GONE);
            holder.momentView.ivOfficialEnterpriseIconBg.setVisibility(View.GONE);
            return;
        }
        if (holder.momentView.ivOfficialLabel != null) {
            holder.momentView.ivOfficialLabel.setVisibility(View.GONE);
        }
        if (holder.momentView.ivOfficialEnterpriseIcon != null) {
            holder.momentView.ivOfficialEnterpriseIcon.setVisibility(View.VISIBLE);
        }
        if (holder.momentView.ivOfficialEnterpriseIconBg != null) {
            holder.momentView.ivOfficialEnterpriseIconBg.setVisibility(View.VISIBLE);
        }
    }

    private void setMomentIcon(DbMoment moment, AbsViewHolder holder, boolean isSelf, String iconPath,
            ContactBean contactBean) {
        if (MomentTypeUtil.isOfficialType(moment.getType().intValue())) {
            if (com.xtc.log.util.TextUtils.isEmpty(moment.getIconPath())) {
                holder.momentView.setIcon(R.drawable.i11_genius_rabbit);
            } else {
                holder.momentView.setIcon(moment.getIconPath());
            }
            holder.momentView.mIcon.setTag(R.string.contact_head_last_update_tag, null);
            return;
        }
        closeLable(holder);
        holder.momentView.mIcon.setTag(R.string.contact_head_last_update_tag, null);
        holder.momentView.setIcon(iconPath);
    }

    private void closeLable(AbsViewHolder holder) {
        if (holder.momentView.ivOfficialLabel != null) {
            holder.momentView.ivOfficialLabel.setVisibility(View.GONE);
        }
        if (holder.momentView.ivOfficialEnterpriseIcon != null) {
            holder.momentView.ivOfficialEnterpriseIcon.setVisibility(View.GONE);
        }
        if (holder.momentView.ivOfficialEnterpriseIconBg != null) {
            holder.momentView.ivOfficialEnterpriseIconBg.setVisibility(View.GONE);
        }
    }

    private void setMomentName(DbMoment moment, AbsViewHolder holder, boolean isSelf, String name) {
        if (MomentTypeUtil.isOfficialType(moment.getType().intValue())) {
            if (com.xtc.log.util.TextUtils.isEmpty(moment.getName())) {
                holder.momentView.setTvName(false, this.mContext.getString(R.string.rabbit));
            } else {
                holder.momentView.setTvName(false, moment.getName());
            }
            return;
        }
        holder.momentView.setTvName(isSelf, name);
    }

    private void setUnknownData(AbsViewHolder holder, DbMoment moment) {
        holder.momentView.setContent(0, this.mContext.getString(R.string.no_support_tips),
                this.mContext.getResources().getColor(R.color.normal_text));
        dealMomentBackground(holder, moment);
    }

    private void setTextData(AbsViewHolder holder, DbMoment moment, boolean isSelf) {
        holder.momentView.setContent(moment.getResource(), moment.getContent(),
                this.mContext.getResources().getColor(R.color.normal_text));
        dealMomentBackground(holder, moment);
        onContentLongClick(holder.momentView, moment, isSelf);
        dealMomentVisible(holder, moment, isSelf);
    }

    private void setOfficialTextData(AbsViewHolder holder, DbMoment moment, boolean isSelf) {
        holder.momentView.setContent(moment.getResource(), moment.getDescription(),
                this.mContext.getResources().getColor(R.color.normal_text));
        dealMomentBackground(holder, moment);
        onContentLongClick(holder.momentView, moment, isSelf);
    }

    private void dealMomentBackground(final AbsViewHolder holder, DbMoment moment) {
        if (moment.getEmotionId() == 0) {
            holder.ivBanner.setVisibility(View.GONE);
            return;
        }
        holder.ivBanner.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(moment.getMomentBgPath())) {
            holder.ivBanner.setVisibility(View.GONE);
            return;
        }
        holder.ivBanner.setVisibility(View.VISIBLE);
        Glide.with(this.mContext).load(moment.getMomentBgPath()).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                if (resource instanceof GifDrawable) {
                    GifDrawable gifDrawable = (GifDrawable) resource;
                    gifDrawable.setLoopCount(1);
                    gifDrawable.start();
                }
                holder.ivBanner.setImageDrawable(resource);
            }
        });
    }

    private void onContentLongClick(AbsMomentView momentView, final DbMoment moment, final boolean isSelf) {
        momentView.setContentOnLongClickListener(this.mContext, this.momentBean,
                new AbsMomentView.OnContentOnLongClickListener() {
                    @Override
                    public void deleteItem(DbMoment deletedMoment) {
                        LogUtil.d(TAG, "deleteItem:momentBean" + deletedMoment);
                        if (DetailsAdapter.this.listener != null && isSelf) {
                            DetailsAdapter.this.listener.onDeleteItem(deletedMoment);
                            return;
                        }
                        if (isSelf || !DetailsAdapter.this.supportReportType(deletedMoment)) {
                            return;
                        }
                        DetailsAdapter.this.showReportBtnDialog(new AbsInteractionAdapter.IOnDialogClickLister() {
                            @Override
                            public void onRightBtnClick() {
                                ReportDataRecorder.recordDbMoment(moment);
                                DetailsAdapter.this.startReportActivity(moment.getWatchId(), moment.getMomentId(),
                                        "", null);
                            }
                        });
                    }
                });
    }

    private void setPhotoData(AbsViewHolder holder, DbMoment moment, Context context, boolean isSelf) {
        if (holder == null) {
            notifyDataSetChanged();
            return;
        }
        holder.momentView.setTags(R.id.moment, moment.getResource());
        holder.momentView.loadImage(context, moment);
        setContentOnClickListener(context, holder, moment);
        onContentLongClick(holder.momentView, moment, isSelf);
        dealMomentBackground(holder, moment);
        dealMomentVisible(holder, moment, isSelf);
    }

    private void setLocationData(AbsViewHolder holder, DbMoment moment, boolean isSelf) {
        if (holder == null) {
            notifyDataSetChanged();
            return;
        }
        holder.momentView.setContent(R.drawable.location, moment.getContent(),
                this.mContext.getResources().getColor(R.color.color_a6bfd1));
        holder.ivBanner.setVisibility(View.VISIBLE);
        holder.ivBanner.setImageResource(R.drawable.circle_location_illustration);
        onContentLongClick(holder.momentView, moment, isSelf);
        dealMomentVisible(holder, moment, isSelf);
    }

    private void setContentOnClickListener(final Context context, AbsViewHolder holder, final DbMoment moment) {
        if (context == null || holder == null || holder.momentView == null || moment == null) {
            LogUtil.e(TAG, "setContentOnClickListener() called with: mContext = [" + context + "], holder = ["
                    + holder + "], momentBean = [" + moment + "]");
            notifyDataSetChanged();
            return;
        }
        holder.momentView.setContentOnClickListener(context, moment, new AbsMomentView.OnContentOnClickListener() {
            @Override
            public void previewPhoto(PhotoMsg photoMsg) {
                new MomentAdapter((Activity) context, null)
                        .previewPhotoLayout(photoMsg, moment.getMomentId(), moment.getWatchId());
                if (DetailsAdapter.this.previewMomentListener != null) {
                    DetailsAdapter.this.previewMomentListener.onPreviewMoment(moment);
                }
            }

            @Override
            public void previewLivePhoto(LivePhotoMsg livePhotoMsg) {
                new MomentAdapter((Activity) context, null)
                        .previewLivePhotoLayout(livePhotoMsg, moment.getMomentId());
                if (DetailsAdapter.this.previewMomentListener != null) {
                    DetailsAdapter.this.previewMomentListener.onPreviewMoment(moment);
                }
            }

            @Override
            public void previewPhoto(String photoPath) {
                new MomentAdapter((Activity) context, null)
                        .previewPhotoLayout(photoPath, moment.getMomentId());
                if (DetailsAdapter.this.previewMomentListener != null) {
                    DetailsAdapter.this.previewMomentListener.onPreviewMoment(moment);
                }
            }

            @Override
            public void preVideoView(String videoPath, boolean autoPlay) {
                if (DetailsAdapter.this.previewMomentListener != null) {
                    DetailsAdapter.this.previewMomentListener.preViewVideo(videoPath, autoPlay);
                }
            }

            @Override
            public void previewH5() {
                if (DetailsAdapter.this.previewMomentListener == null || moment == null) {
                    return;
                }
                if (SystemUtil.isFastDoubleClick()) {
                    LogUtil.d(TAG, "isFastDoubleClick previewH5");
                    return;
                }
                HandlerUtil.runOnBackground(new Runnable() {
                    @Override
                    public void run() {
                        ShareWebMoment shareWebMoment =
                                JSONUtil.fromJSON(moment.getContent(), ShareWebMoment.class);
                        if (shareWebMoment == null || TextUtils.isEmpty(shareWebMoment.getWebLink())) {
                            return;
                        }
                        Uri.Builder uriBuilder = Uri.parse(shareWebMoment.getWebLink()).buildUpon();
                        uriBuilder.appendQueryParameter("momentWatchId", moment.getWatchId());
                        uriBuilder.appendQueryParameter("momentId", moment.getMomentId());
                        DetailsAdapter.this.previewMomentListener.preViewH5(uriBuilder.build().toString(),
                                moment.getResource());
                    }
                });
            }
        });
    }

    private void dealMomentVisible(AbsViewHolder holder, final DbMoment moment, boolean isSelf) {
        if (holder.ivMomentRange == null || -1 == moment.getPermissionType()
                || !ModuleSwitchUtil.queryModuleSwitchByBoolean(this.mContext,
                ModuleSwitchConstant.MODULE_VISIBLE_RANGE, false)) {
            return;
        }
        int momentType = moment.getType().intValue();
        if (moment.getPermissionType() != 0 && isSelf && momentType != 11 && momentType != 12
                && momentType != 13 && momentType != 14 && momentType != 25) {
            holder.ivMomentRange.setVisibility(View.VISIBLE);
            holder.ivMomentRange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemUtil.isFastDoubleClick()) {
                        LogUtil.i(TAG, "onClick: click too fast.");
                        return;
                    }
                    Intent intent = new Intent(DetailsAdapter.this.mContext, FriendsVisibleRangeActivity.class);
                    intent.putExtra("moment_id", moment.getMomentId());
                    DetailsAdapter.this.mContext.startActivity(intent);
                }
            });
        } else {
            holder.ivMomentRange.setVisibility(View.GONE);
        }
    }

    @Override
    public int getDataItemPosition(int adapterPosition) {
        return this.headerHolder != null ? adapterPosition - 1 : adapterPosition;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        if (this.mFooterView == null || position != getItemCount() - 1) {
            return super.getItemViewType(position);
        }
        return TYPE_FOOTER;
    }

    @Override
    public int getItemCount() {
        return this.momentBean != null ? 2 : 0;
    }

    private AbsViewHolder getHeaderView(ViewGroup parent) {
        AsyncLayoutLoader asyncLayoutLoader = AsyncLayoutLoader.getInstance();
        int momentType = MomentTypeUtil.getMomentType(this.momentBean);
        LayoutInflater inflater = LayoutInflater.from(this.mContext);
        switch (momentType) {
            case 4:
                return new PhotoViewHolder(asyncLayoutLoader.inflateView(
                        R.layout.item_recycle_photo_moment, inflater, parent));
            case 6:
                return new VideoViewHolder(asyncLayoutLoader.inflateView(
                        R.layout.item_recycle_video_moment, inflater, parent));
            case 7:
                return new ShareTextViewHolder(asyncLayoutLoader.inflateView(
                        R.layout.item_recycle_share_text_moment, inflater, parent));
            case 8:
                return new ShareImageViewHolder(asyncLayoutLoader.inflateView(
                        R.layout.item_recycle_share_image_moment, inflater, parent));
            case 9:
                return new ShareAppViewHolder(asyncLayoutLoader.inflateView(
                        R.layout.item_recycle_share_app_moment, inflater, parent));
            case 20:
                return new PhotoViewHolder(asyncLayoutLoader.inflateView(
                        R.layout.item_recycle_official_photo_moment, inflater, parent));
            case 21:
                return new PhotoViewHolder(asyncLayoutLoader.inflateView(
                        R.layout.item_recycle_official_photo_text_moment, inflater, parent));
            case 22:
                return new PhotoViewHolder(asyncLayoutLoader.inflateView(
                        R.layout.item_recycle_official_photo_text_moment_h5, inflater, parent));
            case 23:
                return new LivePhotoViewHolder(asyncLayoutLoader.inflateView(
                        R.layout.item_recycle_live_photo_moment, inflater, parent));
            case 24:
                return new ShareLivePhotoViewHolder(asyncLayoutLoader.inflateView(
                        R.layout.item_recycle_share_live_photo_moment, inflater, parent));
            case 25:
                return new VideoViewHolder(asyncLayoutLoader.inflateView(
                        R.layout.item_recycle_official_video_moment, inflater, parent));
            case 26:
                return new ShareVideoViewHolder(asyncLayoutLoader.inflateView(
                        R.layout.item_recycle_share_video_moment, inflater, parent));
            case 27:
                return new ShareWebViewHolder(asyncLayoutLoader.inflateView(
                        R.layout.item_recycle_share_web_moment, inflater, parent));
            case 28:
                PhotosViewHolder photosViewHolder = new PhotosViewHolder(asyncLayoutLoader.inflateView(
                        R.layout.item_recycle_photos_moment, inflater, parent));
                LogUtil.i(TAG, "加载多图View");
                return photosViewHolder;
            case 29:
                return new VideoViewHolder(asyncLayoutLoader.inflateView(
                        R.layout.item_recycle_video_moment, inflater, parent));
            default:
                return new ViewHolder(asyncLayoutLoader.inflateView(
                        R.layout.item_recycle_moment, inflater, parent));
        }
    }

    @Override
    public String getLogTag() {
        return TAG;
    }

    @Override
    public Lifecycle getLifecycle() {
        return this.lifecycleRegistry;
    }
}