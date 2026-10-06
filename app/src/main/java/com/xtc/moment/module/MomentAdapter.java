package com.xtc.moment.module;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.os.MessageQueue;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.request.transition.TransitionFactory;
import com.xtc.contactapi.contact.bean.ContactBean;
import com.xtc.contactapi.contact.manager.ContactManager;
import com.xtc.contactapi.contacthead.impl.ContactHeadManager;
import com.xtc.contactapi.contacthead.interfaces.IShowHeadToViewStrategy;
import com.xtc.log.LogUtil;
import com.xtc.log.util.TextUtils;
import com.xtc.moment.LogTag;
import com.xtc.moment.MomentApp;
import com.xtc.moment.R;
import com.xtc.moment.asynclayout.AsyncLayoutLoader;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.manager.ReportDataRecorder;
import com.xtc.moment.module.bean.CloudFileResource;
import com.xtc.moment.module.bean.Friend;
import com.xtc.moment.module.bean.LivePhotoMsg;
import com.xtc.moment.module.bean.PhotoMsg;
import com.xtc.moment.module.bean.ShareWebMoment;
import com.xtc.moment.module.bean.SmallPicSouce;
import com.xtc.moment.module.bean.VideoMsg;
import com.xtc.moment.module.report.adapter.AbsInteractionAdapter;
import com.xtc.moment.module.scope.FriendsVisibleRangeActivity;
import com.xtc.moment.module.viewholder.AbsViewHolder;
import com.xtc.moment.module.viewholder.LivePhotoViewHolder;
import com.xtc.moment.module.viewholder.PhotoViewHolder;
import com.xtc.moment.module.viewholder.PhotosViewHolder;
import com.xtc.moment.module.viewholder.ShareAppViewHolder;
import com.xtc.moment.module.viewholder.ShareImageViewHolder;
import com.xtc.moment.module.viewholder.ShareLivePhotoViewHolder;
import com.xtc.moment.module.viewholder.ShareMultiPhotosViewHolder;
import com.xtc.moment.module.viewholder.ShareTextViewHolder;
import com.xtc.moment.module.viewholder.ShareVideoViewHolder;
import com.xtc.moment.module.viewholder.ShareWebViewHolder;
import com.xtc.moment.module.viewholder.VideoViewHolder;
import com.xtc.moment.module.viewholder.ViewHolder;
import com.xtc.moment.module.viewholder.comment.MomentCommentAdapter;
import com.xtc.moment.module.widget.AbsMomentView;
import com.xtc.moment.module.widget.PhotoPreviewActivity;
import com.xtc.moment.module.widget.VerticallyLinearLayoutManager;
import com.xtc.moment.module.widget.livephotoView.PlayLivePhotoActivity;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.serve.IAccountInfoServe;
import com.xtc.moment.third.behavior.MomentBehavior;
import com.xtc.moment.util.ClickUtils;
import com.xtc.moment.util.DressUtil;
import com.xtc.moment.util.FileManager;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.MomentLikeViewWindow;
import com.xtc.moment.util.MomentTypeUtil;
import com.xtc.moment.util.OfficialMomentTypeUtil;
import com.xtc.moment.util.SystemUtil;
import com.xtc.moment.util.TimeUtils;
import com.xtc.moment.util.ToastUtil;
import com.xtc.moment.util.Utils;
import com.xtc.moment.util.ViewUtils;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;
import com.xtc.system.account.constant.NotificationFlag;
import com.xtc.utils.common.CollectionUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.storage.FileUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 好友圈主页动态列表适配器。
 *
 * <p>负责按动态类型创建对应的列表项 ViewHolder，绑定头像、昵称、正文、位置、图片、
 * 点赞与评论摘要等数据，并处理点赞、评论、长按删除/举报、图片预览等交互。
 */
public class MomentAdapter extends AbsInteractionAdapter<AbsViewHolder>
        implements LifecycleOwner, IShowHeadToViewStrategy {

    private static final String TAG = LogTag.tag("MomentAdapter");

    /** 局部刷新：图片本地路径变化。 */
    private static final String PART_REFRESH_LOCAL_PATH = "PART_REFRESH_LOCAL_PATH";
    /** 局部刷新：温馨提醒内容变化。 */
    private static final String PART_REFRESH_MOMENT_REMINDER = "part_refresh_kind_reminder";
    /** 局部刷新：收到点赞推送后的数量变化。 */
    private static final String PART_REFRESH_PUSH_LIKE = "part_refresh_praise_push_like";
    /** 局部刷新：可见范围图标变化。 */
    private static final String PART_REFRESH_VISIBLE_PIC = "part_refresh_visible_pic";

    private final AsyncLayoutLoader asyncLayoutLoader;
    private final LayoutInflater from;
    private final LifecycleRegistry lifecycleRegistry;
    private final Context mContext;

    /** 取消点赞后再次允许取消的冷却时间，单位毫秒。 */
    private int cancelLikeSuccessDelayMillis = 3000;
    /** 评论功能总开关。 */
    private boolean commentSwitch = true;
    private List<Friend> friendList;
    /** 好友 watchId 集合，用于过滤异常动态。 */
    private Map<String, String> friendWatchIds = new HashMap<>();
    private long lastCancelTime;
    /** 当前置顶的官方动态。 */
    private DbMoment mToppingMoment;
    private OnViewAttachedToWindowListener mViewAttachedToWindowListener;
    private String myIconPath = "";
    private String myName = "";
    private boolean needNotify;
    private String selfWatchId = "";

    /** 列表项挂载/卸载回调。 */
    public interface OnViewAttachedToWindowListener {
        void onViewAttachedToWindow(AbsViewHolder viewHolder);

        void onViewDetachedFromWindow(AbsViewHolder viewHolder);
    }

    public MomentAdapter(Activity activity, VerticallyLinearLayoutManager layoutManager) {
        super(activity);
        this.mContext = activity;
        this.asyncLayoutLoader = AsyncLayoutLoader.getInstance();
        this.verticallyLinearLayoutManager = layoutManager;
        this.from = LayoutInflater.from(activity);
        this.lifecycleRegistry = new LifecycleRegistry(this);
        this.lifecycleRegistry.markState(Lifecycle.State.RESUMED);
        this.momentLikeViewWindow = new MomentLikeViewWindow();
        this.momentLikeViewWindow.initLikeAnimation(this.mContext, this, this.lifecycleRegistry);
    }
    @Override
    public AbsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogUtil.i(TAG, "onCreateViewHolder" + viewType);
        final AbsViewHolder viewHolder;
        if (viewType == MomentTypeUtil.ITEM_TYPE_EMPTY) {
            // 空视图：从原父容器上摘下来再复用，避免同一个 View 同时挂在两个 RecyclerView 上。
            ViewGroup emptyParent = (ViewGroup) this.mEmptyView.getParent();
            if (emptyParent != null) {
                emptyParent.removeView(this.mEmptyView);
            }
            viewHolder = new ViewHolder(this.mEmptyView);
        } else if (viewType == 4) {
            viewHolder = new PhotoViewHolder(this.asyncLayoutLoader.inflateView(
                    R.layout.item_recycle_photo_moment, this.from, parent));
        } else {
            switch (viewType) {
                case 6:
                    viewHolder = new VideoViewHolder(this.asyncLayoutLoader.inflateView(
                            R.layout.item_recycle_video_moment, this.from, parent));
                    break;
                case 7:
                    viewHolder = new ShareTextViewHolder(this.asyncLayoutLoader.inflateView(
                            R.layout.item_recycle_share_text_moment, this.from, parent));
                    break;
                case 8:
                    viewHolder = new ShareImageViewHolder(this.asyncLayoutLoader.inflateView(
                            R.layout.item_recycle_share_image_moment, this.from, parent));
                    break;
                case 9:
                    viewHolder = new ShareAppViewHolder(this.asyncLayoutLoader.inflateView(
                            R.layout.item_recycle_share_app_moment, this.from, parent));
                    break;
                case 10:
                    ViewGroup headerParent = (ViewGroup) this.mHeaderView.getParent();
                    if (headerParent != null) {
                        headerParent.removeView(this.mHeaderView);
                    }
                    viewHolder = new ViewHolder(this.mHeaderView);
                    break;
                case 11:
                    ViewGroup footerParent = (ViewGroup) this.mFooterView.getParent();
                    if (footerParent != null) {
                        footerParent.removeView(this.mFooterView);
                    }
                    viewHolder = new ViewHolder(this.mFooterView);
                    break;
                default:
                    switch (viewType) {
                        case 20:
                            viewHolder = new PhotoViewHolder(this.asyncLayoutLoader.inflateView(
                                    R.layout.item_recycle_official_photo_moment, this.from, parent));
                            break;
                        case 21:
                            viewHolder = new PhotoViewHolder(this.asyncLayoutLoader.inflateView(
                                    R.layout.item_recycle_official_photo_text_moment, this.from, parent));
                            break;
                        case 22:
                            viewHolder = new PhotoViewHolder(this.asyncLayoutLoader.inflateView(
                                    R.layout.item_recycle_official_photo_text_moment_h5, this.from, parent));
                            break;
                        case 23:
                            viewHolder = new LivePhotoViewHolder(this.asyncLayoutLoader.inflateView(
                                    R.layout.item_recycle_live_photo_moment, this.from, parent));
                            break;
                        case 24:
                            viewHolder = new ShareLivePhotoViewHolder(this.asyncLayoutLoader.inflateView(
                                    R.layout.item_recycle_share_live_photo_moment, this.from, parent));
                            break;
                        case 25:
                            viewHolder = new VideoViewHolder(this.asyncLayoutLoader.inflateView(
                                    R.layout.item_recycle_official_video_moment, this.from, parent));
                            break;
                        case 26:
                            viewHolder = new ShareVideoViewHolder(this.asyncLayoutLoader.inflateView(
                                    R.layout.item_recycle_share_video_moment, this.from, parent));
                            break;
                        case 27:
                            viewHolder = new ShareWebViewHolder(LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_recycle_share_web_moment, parent, false));
                            break;
                        case 28:
                            viewHolder = new PhotosViewHolder(this.asyncLayoutLoader.inflateView(
                                    R.layout.item_recycle_photos_moment, this.from, parent));
                            LogUtil.i(TAG, "加载多图View");
                            break;
                        case 29:
                            viewHolder = new VideoViewHolder(this.asyncLayoutLoader.inflateView(
                                    R.layout.item_recycle_video_moment, this.from, parent));
                            break;
                        case 30:
                            viewHolder = new ShareMultiPhotosViewHolder(this.asyncLayoutLoader.inflateView(
                                    R.layout.item_recycle_multo_photos_moment, this.from, parent));
                            break;
                        default:
                            viewHolder = new ViewHolder(this.asyncLayoutLoader.inflateView(
                                    R.layout.item_recycle_moment, this.from, parent));
                            break;
                    }
                    break;
            }
        }
        bindViewHolderListeners(viewHolder);
        return viewHolder;
    }

    /** 为列表项上的点赞、评论入口绑定点击事件。 */
    private void bindViewHolderListeners(final AbsViewHolder viewHolder) {
        if (viewHolder.momentLike != null) {
            viewHolder.momentLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onMomentLikeClick(viewHolder, view);
                }
            });
        }
        if (viewHolder.ivMomentComment != null) {
            viewHolder.ivMomentComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LogUtil.i(TAG, "ivMomentComment onclick = " + viewHolder.getDbMoment());
                    if (SystemUtil.isFastDoubleClick()) {
                        LogUtil.i(TAG, "onClick: click too fast.");
                        return;
                    }
                    if (!MomentAdapter.this.commentSwitch || MomentAdapter.this.isDisableSend(true)
                            || MomentAdapter.this.momentCommentListener == null) {
                        return;
                    }
                    MomentAdapter.this.momentCommentListener.comment(viewHolder.getDbMoment());
                }
            });
        }
        if (viewHolder.momentCommentView != null) {
            viewHolder.momentCommentView.setOnMomentCommentListener(new MomentCommentAdapter.OnMomentCommentListener() {
                @Override
                public void onMomentCommentClick(int position, DbMomentComment comment) {
                    if (!MomentAdapter.this.commentSwitch || MomentAdapter.this.isDisableSend(true)) {
                        LogUtil.d(TAG, "onMomentCommentClick: commentSwitch = " + MomentAdapter.this.commentSwitch);
                        return;
                    }
                    LogUtil.d(TAG, "index: 点击" + position + ";dbMomentComment:" + comment
                            + viewHolder.getDbMoment().getType());
                    if (MomentAdapter.this.momentCommentListener != null) {
                        MomentAdapter.this.momentCommentListener.reply(viewHolder.getDbMoment(), comment);
                    }
                }

                @Override
                public void onLoadMoreCommentClick(int position, DbMoment moment) {
                    if (MomentAdapter.this.momentCommentListener != null) {
                        MomentAdapter.this.momentCommentListener.loadMoreComment(moment);
                    }
                }
            });
            viewHolder.momentCommentView.setOnCommentDeleteListener(new MomentCommentAdapter.OnCommentDeleteListener() {
                @Override
                public void onCommentDeleteClick(int position, DbMomentComment comment) {
                    LogUtil.d(TAG, "index: 删除" + position + ";dbMomentComment:" + comment
                            + viewHolder.getDbMoment().getType());
                    DbMoment moment = viewHolder.getDbMoment();
                    boolean isSelf = MomentAdapter.this.selfWatchId != null
                            && MomentAdapter.this.selfWatchId.equals(moment.getWatchId());
                    dealCommentDeleteClick(moment, comment, MomentAdapter.this.selfWatchId, isSelf);
                }
            });
        }
    }
    private void onMomentLikeClick(AbsViewHolder viewHolder, View view) {
        if (!ClickUtils.isFastClick()) {
            LogUtil.i(TAG, "click isFastClick");
            return;
        }
        String selfId = AccountInfoServerImpl.getInstance(this.mContext).getWatchAccountInfo()
                .getWatchId(this.mContext);
        int adapterPosition = viewHolder.getAdapterPosition();
        int dataPosition = getDataItemPosition(adapterPosition);
        if (CollectionUtil.isEmpty(this.mData) || dataPosition < 0 || dataPosition >= this.mData.size()) {
            LogUtil.w(TAG, "onClick: error index");
            return;
        }
        DbMoment moment = this.mData.get(dataPosition);
        LogUtil.d(TAG, "onClick：" + moment + ", adapterPosition: " + adapterPosition
                + ", dataItemPosition: " + dataPosition);
        // 点击自己的动态头像区域时进入点赞列表。
        if (selfId != null && selfId.equals(moment.getWatchId())) {
            viewHolder.momentLike.startMomentLikesActivity(moment, this.mContext);
            return;
        }
        if (isDisableSend(true)) {
            LogUtil.i(TAG, "disable send");
            return;
        }
        if (moment.isEnableLike()) {
            if (MomentTypeUtil.isOfficialType(moment.getType().intValue())) {
                LogUtil.i(TAG, "广告点赞" + moment.getContent() + "     " + moment.getMomentId() + moment);
                this.mListener.likeAdvertise(moment.getMomentId(), moment.getWatchId());
                MomentBehavior.advLikedClick(this.mContext, moment.getMomentId());
            } else {
                LogUtil.i(TAG, "普通朋友点赞" + moment.getContent());
                this.mListener.likeMoment(moment.getMomentId(), moment.getWatchId());
            }
            this.momentLikeViewWindow.showLikeAnimation(view);
            MomentBehavior.likeClick(this.mContext, moment.getWatchId(), moment.getContent());
            this.lastCancelTime = System.currentTimeMillis();
            return;
        }
        if (!ModuleSwitchUtil.queryModuleSwitchByBoolean(this.mContext,
                ModuleSwitchConstant.MODULE_SWITCH_MOMENT_CANCEL_PRAISE, false)) {
            Context context = this.mContext;
            ToastUtil.showShortCover(context, context.getString(R.string.moment_give_like));
            LogUtil.i(TAG, "取消点赞全网开关关闭");
            return;
        }
        long now = System.currentTimeMillis();
        long lastTime = this.lastCancelTime;
        if (now - lastTime <= this.cancelLikeSuccessDelayMillis && lastTime != 0) {
            LogUtil.i(TAG, "fast click");
            return;
        }
        if (MomentTypeUtil.isOfficialType(moment.getType().intValue())) {
            this.mListener.cancelLikeAdvertise(moment.getMomentId(), moment.getWatchId(), moment);
            LogUtil.i(TAG, "advCancelLikedClick 取消点赞");
            MomentBehavior.advCancelLikedClick(this.mContext, moment.getMomentId());
            return;
        }
        this.mListener.cancelLikeMoment(moment.getMomentId(), moment.getWatchId(), moment);
    }

    @Override
    public void onBindViewHolder(AbsViewHolder viewHolder, int position, List<Object> payloads) {
        this.needNotify = false;
        if (payloads.isEmpty()) {
            onBindViewHolder(viewHolder, position);
            return;
        }
        String payload = (String) payloads.get(0);
        int dataPosition = getDataItemPosition(position);
        final DbMoment moment = this.mData.get(dataPosition);
        viewHolder.setContext(this.mContext);
        viewHolder.setDbMoment(moment);
        viewHolder.setDatePosition(dataPosition);
        LogUtil.d(TAG, "局部刷新payload==" + payload + ",moment:" + moment.getMomentId()
                + "\n" + moment.hashCode());
        if (AbsInteractionAdapter.PART_REFRESH_PRAISE.equals(payload)
                || AbsInteractionAdapter.PART_REFRESH_COMMENT.equals(payload)) {
            if (!this.selfWatchId.equals(moment.getWatchId())) {
                viewHolder.momentLike.setLike(this.mContext, !moment.isEnableLike());
            }
            viewHolder.momentLike.setCount(moment.getLikeTotal() != null ? moment.getLikeTotal().intValue() : 0);
            setMomentCommentData(viewHolder, moment, moment.getComments());
            setOfficialLabel(viewHolder, moment);
            return;
        }
        if (PART_REFRESH_LOCAL_PATH.equals(payload)) {
            setContentOnClickListener(this.mContext, viewHolder, moment);
            return;
        }
        if (PART_REFRESH_PUSH_LIKE.equals(payload)) {
            viewHolder.momentLike.setCount(moment.getLikeTotal() != null ? moment.getLikeTotal().intValue() : 0);
            return;
        }
        if (PART_REFRESH_VISIBLE_PIC.equals(payload)) {
            if (viewHolder.ivMomentRange == null) {
                return;
            }
            viewHolder.momentCommentView.refreshVisible(moment.getPermissionType());
            if (moment.getPermissionType() == 0 || -1 == moment.getPermissionType()) {
                viewHolder.ivMomentRange.setVisibility(View.GONE);
            } else {
                viewHolder.ivMomentRange.setVisibility(View.VISIBLE);
                viewHolder.ivMomentRange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (SystemUtil.isFastDoubleClick()) {
                            LogUtil.i(TAG, "onClick: click too fast.");
                            return;
                        }
                        Intent intent = new Intent(MomentAdapter.this.mContext, FriendsVisibleRangeActivity.class);
                        intent.putExtra(FriendsVisibleRangeActivity.MOMENT_ID, moment.getMomentId());
                        MomentAdapter.this.mContext.startActivity(intent);
                    }
                });
            }
            return;
        }
        if (PART_REFRESH_MOMENT_REMINDER.equals(payload)) {
            setMomentReminder(viewHolder, moment);
        }
    }

    /** 设置官方认证标识（XTCOfficial 与普通企业号展示不同图标）。 */
    private void setOfficialLabel(AbsViewHolder viewHolder, DbMoment moment) {
        if (!MomentTypeUtil.isOfficialType(moment.getType().intValue())) {
            if (viewHolder.momentView.ivOfficialEnterpriseIcon != null) {
                viewHolder.momentView.ivOfficialEnterpriseIcon.setVisibility(View.GONE);
            }
            if (viewHolder.momentView.ivOfficialEnterpriseIconBg != null) {
                viewHolder.momentView.ivOfficialEnterpriseIconBg.setVisibility(View.GONE);
            }
            return;
        }
        if (MomentTypeUtil.isXTCOfficialType(moment.getOfficial())) {
            viewHolder.momentView.ivOfficialLabel.setVisibility(View.VISIBLE);
            viewHolder.momentView.ivOfficialEnterpriseIcon.setVisibility(View.GONE);
            viewHolder.momentView.ivOfficialEnterpriseIconBg.setVisibility(View.GONE);
            return;
        }
        if (viewHolder.momentView.ivOfficialLabel != null) {
            viewHolder.momentView.ivOfficialLabel.setVisibility(View.GONE);
        }
        if (viewHolder.momentView.ivOfficialEnterpriseIcon != null) {
            viewHolder.momentView.ivOfficialEnterpriseIcon.setVisibility(View.VISIBLE);
        }
        if (viewHolder.momentView.ivOfficialEnterpriseIconBg != null) {
            viewHolder.momentView.ivOfficialEnterpriseIconBg.setVisibility(View.VISIBLE);
        }
    }

    /** 设置评论摘要：主线程空闲时再绑定，避免滚动过程中掉帧。 */
    private void setMomentCommentData(final AbsViewHolder viewHolder, final DbMoment moment,
            final List<DbMomentComment> comments) {
        viewHolder.hideRecyclerComment();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Looper.getMainLooper().getQueue().addIdleHandler(new MessageQueue.IdleHandler() {
                @Override
                public boolean queueIdle() {
                    viewHolder.momentCommentView.setDatas(MomentAdapter.this.mContext, comments,
                            MomentAdapter.this.selfWatchId, moment);
                    return false;
                }
            });
        } else {
            viewHolder.momentCommentView.setDatas(this.mContext, comments, this.selfWatchId, moment);
        }
    }
    @Override
    public void onBindViewHolder(final AbsViewHolder viewHolder, int position) {
        int itemViewType = getItemViewType(position);
        // 头部、尾部与空视图不需要绑定数据。
        if (itemViewType == MomentTypeUtil.ITEM_TYPE_HEADER
                || itemViewType == MomentTypeUtil.ITEM_TYPE_FOOTER
                || itemViewType == MomentTypeUtil.ITEM_TYPE_EMPTY) {
            return;
        }
        long bindStartTime = SystemClock.elapsedRealtime();
        int dataPosition = getDataItemPosition(position);
        final DbMoment moment = this.mData.get(dataPosition);
        viewHolder.setContext(this.mContext);
        viewHolder.setDbMoment(moment);
        viewHolder.momentView.setDbMoment(moment);
        viewHolder.momentView.setTextSupportExpand(true);
        viewHolder.setDatePosition(dataPosition);
        viewHolder.momentView.setContext(this.mContext);
        if (moment == null || TextUtils.isEmpty(moment.getWatchId()) || moment.getCreateTime() == null) {
            return;
        }
        LogUtil.d(TAG, "bind MomentId:" + moment.getMomentId() + ",dataPosition:" + dataPosition);
        boolean isOfficial = MomentTypeUtil.isOfficialType(moment);
        String selfId = this.selfWatchId;
        final boolean isSelf = selfId != null && selfId.equals(moment.getWatchId());

        // 解析展示用的昵称、头像路径与联系人信息。
        final String displayName;
        final String displayIconPath;
        ContactBean contactBean;
        if (isSelf) {
            if (TextUtils.isEmpty(this.myName) || this.myName.equals(this.mContext.getString(R.string.unknown_watch))) {
                this.myName = AccountInfoServerImpl.getInstance(this.mContext).getWatchAccountInfo()
                        .getName(this.mContext);
            }
            contactBean = null;
            displayName = TextUtils.isEmpty(this.myName)
                    ? this.mContext.getString(R.string.unknown_watch) : this.myName;
            displayIconPath = this.myIconPath;
        } else {
            ContactBean matched = ContactManager.getInstance(this.mContext)
                    .getContactWithoutShortNumberByWatchIdSync(moment.getWatchId());
            if (matched != null) {
                displayName = matched.getName();
                contactBean = matched;
                displayIconPath = matched.getPhotoPath();
            } else if (!advertisement(moment.getType().intValue())) {
                LogUtil.d(TAG, "showContact is null");
                displayName = moment.getName();
                displayIconPath = "";
                contactBean = matched;
            } else {
                LogUtil.d(TAG, "show advertisement");
                displayName = null;
                displayIconPath = "";
                contactBean = matched;
            }
        }
        viewHolder.momentView.mTvName.getPaint().setShader(null);
        viewHolder.showView();

        final ContactBean finalContact = contactBean;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Looper.getMainLooper().getQueue().addIdleHandler(new MessageQueue.IdleHandler() {
                @Override
                public boolean queueIdle() {
                    DressUtil.setNicknameSource(moment.getWatchId(), viewHolder.momentView.mTvName);
                    MomentAdapter.this.setMomentName(moment, viewHolder, isSelf, displayName);
                    MomentAdapter.this.setMomentIcon(moment, viewHolder, isSelf, displayIconPath, finalContact);
                    return false;
                }
            });
        } else {
            DressUtil.setNicknameSource(moment.getWatchId(), viewHolder.momentView.mTvName);
            setMomentName(moment, viewHolder, isSelf, displayName);
            setMomentIcon(moment, viewHolder, isSelf, displayIconPath, finalContact);
        }

        // 按动态类型分别绑定正文、图片或位置。
        int type = moment.getType().intValue();
        switch (type) {
            case 0:
            case 1:
            case 3:
            case 7:
                setTextData(viewHolder, moment, isSelf || isOfficial);
                break;
            case 2:
                setLocationData(viewHolder, moment, isSelf);
                break;
            case 4:
            case 5:
            case 6:
            case 8:
            case 9:
                setPhotoData(viewHolder, moment, this.mContext, !isSelf || isOfficial, true);
                break;
            case 10:
                setOfficialTextData(viewHolder, moment, isSelf || isOfficial);
                break;
            case 11:
            case 12:
            case 13:
            case 14:
                setPhotoData(viewHolder, moment, this.mContext, isSelf || isOfficial, false);
                break;
            default:
                switch (type) {
                    case 22:
                    case 23:
                    case 24:
                    case 25:
                    case 26:
                    case 27:
                    case 28:
                        setPhotoData(viewHolder, moment, this.mContext, true, true);
                        break;
                    default:
                        setUnknownData(viewHolder, moment);
                        break;
                }
                break;
        }

        int likeCount = moment.getLikeTotal() == null ? 0 : moment.getLikeTotal().intValue();
        if (isSelf) {
            viewHolder.momentLike.setLike(this.mContext, false);
        } else {
            viewHolder.momentLike.setLike(this.mContext, !moment.isEnableLike());
        }
        setTvTime(viewHolder, moment);
        viewHolder.momentLike.setCount(likeCount);
        viewHolder.momentView.setIconOnClickListener(moment);
        setMomentCommentData(viewHolder, moment, moment.getComments());
        setMomentReminder(viewHolder, moment);
        if (this.commentSwitch) {
            viewHolder.showComment();
        } else {
            viewHolder.hideComment();
        }
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (MomentAdapter.this.listener == null || !isSelf) {
                    return true;
                }
                MomentAdapter.this.listener.onDeleteItem(moment);
                return true;
            }
        });
        setOfficialLabel(viewHolder, moment);
        if (SystemUtil.isHighMachine()) {
            MomentBehavior.uploadMomentItemCatonPoint(moment.getType().intValue(), bindStartTime);
        }
    }

    /** 设置温馨提醒内容与跳转链接。 */
    private void setMomentReminder(AbsViewHolder viewHolder, DbMoment moment) {
        if (viewHolder.momentReminderView == null) {
            return;
        }
        if (!TextUtils.isEmpty(moment.getReminderContent())) {
            viewHolder.momentReminderView.setVisibility(View.VISIBLE);
            viewHolder.momentReminderView.setReminderText(moment.getReminderContent());
            viewHolder.momentReminderView.setReminderTextUrl(moment.getReminderUrl());
            return;
        }
        viewHolder.momentReminderView.setVisibility(View.GONE);
    }

    /** 时间文案需要查询数据库，放到后台线程计算后再回主线程展示。 */
    private void setTvTime(final AbsViewHolder viewHolder, final DbMoment moment) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                final String time = TimeUtils.getTime(MomentAdapter.this.mContext, moment.getCreateTime());
                HandlerUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        viewHolder.tvTime.setText(time);
                    }
                });
            }
        });
    }
    /** 设置头像：官方动态使用内置图标，普通好友使用联系人头像（圆形裁剪）。 */
    private void setMomentIcon(DbMoment moment, AbsViewHolder viewHolder, boolean isSelf,
            String iconPath, ContactBean contactBean) {
        if (MomentTypeUtil.isOfficialType(moment.getType().intValue())) {
            if (TextUtils.isEmpty(moment.getIconPath())) {
                viewHolder.momentView.setIcon(R.drawable.i11_genius_rabbit);
            } else {
                viewHolder.momentView.setIcon(moment.getIconPath());
            }
            viewHolder.momentView.mIcon.setTag(R.string.contact_head_last_update_tag, null);
            return;
        }
        if (!isSelf && contactBean != null) {
            Glide.with(this.mContext)
                    .load(contactBean.getPhotoPath())
                    .apply(new RequestOptions()
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .override(this.mContext.getResources().getDimensionPixelSize(
                                            R.dimen.dimen_item_avatar_size_width_max),
                                    this.mContext.getResources().getDimensionPixelSize(
                                            R.dimen.dimen_item_avatar_size_height_max))
                            .placeholder(R.drawable.default_custom_default)
                            .transform((Transformation<Bitmap>) new CircleCrop()))
                    .into(viewHolder.momentView.mIcon);
        } else {
            viewHolder.momentView.mIcon.setTag(R.string.contact_head_last_update_tag, null);
            viewHolder.momentView.setIcon(iconPath);
        }
        closeLable(viewHolder);
    }

    /** 隐藏官方认证相关图标。 */
    private void closeLable(AbsViewHolder viewHolder) {
        if (viewHolder.momentView.ivOfficialLabel != null) {
            viewHolder.momentView.ivOfficialLabel.setVisibility(View.GONE);
        }
        if (viewHolder.momentView.ivOfficialEnterpriseIcon != null) {
            viewHolder.momentView.ivOfficialEnterpriseIcon.setVisibility(View.GONE);
        }
        if (viewHolder.momentView.ivOfficialEnterpriseIconBg != null) {
            viewHolder.momentView.ivOfficialEnterpriseIconBg.setVisibility(View.GONE);
        }
    }

    /** 设置昵称：官方动态默认展示“兔子”，自己高亮显示。 */
    private void setMomentName(DbMoment moment, AbsViewHolder viewHolder, boolean isSelf, String name) {
        if (MomentTypeUtil.isOfficialType(moment.getType().intValue())) {
            if (TextUtils.isEmpty(moment.getName())) {
                viewHolder.momentView.setTvName(false, this.mContext.getString(R.string.rabbit));
            } else {
                viewHolder.momentView.setTvName(false, moment.getName());
            }
            return;
        }
        viewHolder.momentView.setTvName(isSelf, name);
    }

    /** 未知动态类型兜底文案。 */
    private void setUnknownData(AbsViewHolder viewHolder, DbMoment moment) {
        viewHolder.momentView.setContent(0, this.mContext.getString(R.string.no_support_tips),
                this.mContext.getResources().getColor(R.color.normal_text));
        dealMomentBackground(viewHolder, moment);
    }

    /** 文本动态：正文取自 content 字段。 */
    private void setTextData(AbsViewHolder viewHolder, DbMoment moment, boolean isSelf) {
        viewHolder.momentView.setContent(moment.getResource(), moment.getContent(),
                this.mContext.getResources().getColor(R.color.normal_text));
        LogUtil.d(TAG, "setTextData() : isSelf means owner or adverting : isSelf = " + isSelf);
        dealMomentBackground(viewHolder, moment);
        setContentOnLongLister(viewHolder, moment, isSelf, this.mContext);
        dealMomentVisible(viewHolder, moment, isSelf);
    }

    /** 官方文本动态：正文取自 description 字段。 */
    private void setOfficialTextData(AbsViewHolder viewHolder, DbMoment moment, boolean isSelf) {
        viewHolder.momentView.setContent(moment.getResource(), moment.getDescription(),
                this.mContext.getResources().getColor(R.color.normal_text));
        dealMomentBackground(viewHolder, moment);
        setContentOnLongLister(viewHolder, moment, isSelf, this.mContext);
    }

    /** 可见范围图标：仅本人动态且开关开启时展示。 */
    private void dealMomentVisible(AbsViewHolder viewHolder, final DbMoment moment, boolean isSelf) {
        if (viewHolder.ivMomentRange == null
                || !ModuleSwitchUtil.queryModuleSwitchByBoolean(this.mContext,
                        ModuleSwitchConstant.MODULE_VISIBLE_RANGE, false)) {
            return;
        }
        boolean shouldShow = moment.getPermissionType() != 0
                && moment.getPermissionType() != -1
                && isSelf
                && moment.getType().intValue() != 25;
        if (shouldShow) {
            viewHolder.ivMomentRange.setVisibility(View.VISIBLE);
            viewHolder.ivMomentRange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemUtil.isFastDoubleClick()) {
                        LogUtil.i(TAG, "onClick: click too fast.");
                        return;
                    }
                    Intent intent = new Intent(MomentAdapter.this.mContext, FriendsVisibleRangeActivity.class);
                    intent.putExtra(FriendsVisibleRangeActivity.MOMENT_ID, moment.getMomentId());
                    MomentAdapter.this.mContext.startActivity(intent);
                }
            });
        } else {
            viewHolder.ivMomentRange.setVisibility(View.GONE);
        }
    }

    /** 处理位置展示、动态背景图（表情）等与正文区域相关的元素。 */
    private void dealMomentBackground(final AbsViewHolder viewHolder, DbMoment moment) {
        String location = moment.getLocation();
        boolean hideLbs = TextUtils.isEmpty(location) || !moment.isLbsSwitch();
        if (viewHolder.ivLbs != null) {
            viewHolder.ivLbs.setVisibility(hideLbs ? View.GONE : View.VISIBLE);
            if (!hideLbs) {
                ViewUtils.setGlideImgWithAlpha(this.mContext, viewHolder.ivLbs,
                        R.drawable.circle_location_illustration);
            }
        }
        if (viewHolder.llLbs != null) {
            viewHolder.llLbs.setOnLongClickListener(null);
            viewHolder.llLbs.setVisibility(hideLbs ? View.GONE : View.VISIBLE);
            viewHolder.llLbs.setLocation(location);
            dealLbsClick(viewHolder.llLbs, moment);
        }
        viewHolder.momentView.setContentVisibility(true);
        if (moment.getEmotionId() == 0) {
            viewHolder.ivBanner.setVisibility(View.GONE);
            return;
        }
        if (TextUtils.isEmpty(moment.getMomentBgPath())) {
            viewHolder.ivBanner.setVisibility(View.GONE);
            return;
        }
        if (hideLbs) {
            viewHolder.ivBanner.setVisibility(View.VISIBLE);
            viewHolder.ivBanner.setBackground(null);
            Glide.with(this.mContext)
                    .load(moment.getMomentBgPath())
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable drawable,
                                Transition<? super Drawable> transition) {
                            if (drawable instanceof GifDrawable) {
                                GifDrawable gifDrawable = (GifDrawable) drawable;
                                gifDrawable.setLoopCount(1);
                                gifDrawable.start();
                            }
                            viewHolder.ivBanner.setImageDrawable(drawable);
                        }
                    });
        } else {
            viewHolder.ivBanner.setVisibility(View.GONE);
            viewHolder.ivBanner.setImageDrawable(null);
        }
    }

    /** 正文长按：自己的动态走删除，他人的走举报。 */
    private void setContentOnLongLister(AbsViewHolder viewHolder, DbMoment moment, final boolean isSelf,
            Context context) {
        viewHolder.momentView.setContentOnLongClickListener(context, moment,
                new AbsMomentView.OnContentOnLongClickListener() {
                    @Override
                    public void deleteItem(DbMoment clickedMoment) {
                        dealLongClick(clickedMoment, isSelf);
                    }
                });
    }

    private void dealLongClick(final DbMoment moment, boolean isSelf) {
        LogUtil.d(TAG, "deleteItem:momentBean" + moment);
        if (this.listener != null && isSelf) {
            this.listener.onDeleteItem(moment);
            return;
        }
        if (isSelf || !supportReportType(moment)) {
            return;
        }
        showReportBtnDialog(new AbsInteractionAdapter.IOnDialogClickLister() {
            @Override
            public void onRightBtnClick() {
                ReportDataRecorder.recordDbMoment(moment);
                startReportActivity(moment.getWatchId(), moment.getMomentId(), "", null);
            }
        });
    }
    /** 图片/视频/分享类动态：加载内容图并绑定点击、长按事件。 */
    private void setPhotoData(final AbsViewHolder viewHolder, final DbMoment moment, final Context context,
            boolean isSelf, boolean asyncLoad) {
        if (viewHolder == null) {
            notifyDataSetChanged();
            return;
        }
        LogUtil.d(TAG, "setPhotoData: isSelf = [" + isSelf + "]");
        viewHolder.momentView.setTags(R.id.moment, moment.getResource());
        if (asyncLoad) {
            // 图片加载放到后台线程，避免主线程做 IO 造成卡顿。
            HandlerUtil.runOnBackground(new Runnable() {
                @Override
                public void run() {
                    viewHolder.momentView.loadImage(context, moment);
                }
            });
        } else {
            viewHolder.momentView.loadImage(context, moment);
        }
        setContentOnClickListener(context, viewHolder, moment);
        setContentOnLongLister(viewHolder, moment, isSelf, context);
        dealMomentBackground(viewHolder, moment);
        dealMomentVisible(viewHolder, moment, isSelf);
    }

    /** 正文点击：预览图片、播放视频、打开 H5。 */
    private void setContentOnClickListener(Context context, AbsViewHolder viewHolder, final DbMoment moment) {
        if (context == null || viewHolder == null || viewHolder.momentView == null || moment == null) {
            LogUtil.e(TAG, "setContentOnClickListener() called with: mContext = [" + context + "], holder = ["
                    + viewHolder + "], momentBean = [" + moment + "]");
            notifyDataSetChanged();
            return;
        }
        viewHolder.momentView.setContentOnClickListener(context, moment, new AbsMomentView.OnContentOnClickListener() {
            @Override
            public void previewPhoto(PhotoMsg photoMsg) {
                previewPhotoLayout(photoMsg, moment.getMomentId(), moment.getWatchId());
                if (MomentAdapter.this.previewMomentListener != null) {
                    MomentAdapter.this.previewMomentListener.onPreviewMoment(moment);
                }
            }

            @Override
            public void previewLivePhoto(LivePhotoMsg livePhotoMsg) {
                previewLivePhotoLayout(livePhotoMsg, moment.getMomentId());
                if (MomentAdapter.this.previewMomentListener != null) {
                    MomentAdapter.this.previewMomentListener.onPreviewMoment(moment);
                }
            }

            @Override
            public void previewPhoto(String localPath) {
                previewPhotoLayout(localPath, moment.getMomentId());
                if (MomentAdapter.this.previewMomentListener != null) {
                    MomentAdapter.this.previewMomentListener.onPreviewMoment(moment);
                }
            }

            @Override
            public void preVideoView(String videoPath, boolean isShare) {
                if (MomentAdapter.this.previewMomentListener != null) {
                    MomentAdapter.this.previewMomentListener.preViewVideo(videoPath, isShare);
                }
            }

            @Override
            public void previewH5() {
                if (MomentAdapter.this.previewMomentListener == null) {
                    return;
                }
                if (SystemUtil.isFastDoubleClick()) {
                    LogUtil.d(TAG, "isFastDoubleClick previewH5");
                    return;
                }
                if (moment.getType().intValue() == 25) {
                    // 分享 H5 动态：在跳转链接上补上动态 id，便于 H5 埋点。
                    HandlerUtil.runOnBackground(new Runnable() {
                        @Override
                        public void run() {
                            ShareWebMoment shareWebMoment = (ShareWebMoment) JSONUtil.fromJSON(
                                    moment.getContent(), ShareWebMoment.class);
                            if (shareWebMoment == null || shareWebMoment.getWebLink() == null) {
                                return;
                            }
                            Uri.Builder builder = Uri.parse(shareWebMoment.getWebLink()).buildUpon();
                            builder.appendQueryParameter("momentWatchId", moment.getWatchId());
                            builder.appendQueryParameter("momentId", moment.getMomentId());
                            MomentAdapter.this.previewMomentListener.preViewH5(
                                    builder.build().toString(), moment.getResource());
                        }
                    });
                } else {
                    MomentAdapter.this.previewMomentListener.preViewH5(moment.getDataUrl(), moment.getResource());
                }
            }
        });
    }

    /** 位置动态：展示位置背景图与位置文案。 */
    private void setLocationData(AbsViewHolder viewHolder, final DbMoment moment, final boolean isSelf) {
        if (viewHolder == null) {
            notifyDataSetChanged();
            return;
        }
        viewHolder.ivBanner.setVisibility(View.VISIBLE);
        viewHolder.ivBanner.setImageResource(R.drawable.circle_location_illustration);
        String location = moment.getLocation();
        if (viewHolder.llLbs != null) {
            viewHolder.llLbs.setVisibility(View.GONE);
            viewHolder.llLbs.setOnLongClickListener(null);
        }
        if (TextUtils.isEmpty(location) || !moment.isLbsSwitch()) {
            viewHolder.momentView.setContentVisibility(true);
            viewHolder.momentView.setContent(R.drawable.location, moment.getContent(),
                    this.mContext.getResources().getColor(R.color.color_a6bfd1));
        } else if (viewHolder.llLbs != null) {
            viewHolder.momentView.setContentVisibility(false);
            viewHolder.llLbs.setVisibility(View.VISIBLE);
            viewHolder.llLbs.setLocation(location);
            dealLbsClick(viewHolder.llLbs, moment);
            viewHolder.llLbs.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    dealLongClick(moment, isSelf);
                    return false;
                }
            });
        }
        setContentOnLongLister(viewHolder, moment, isSelf, this.mContext);
        dealMomentVisible(viewHolder, moment, isSelf);
    }

    /** 打开图片预览页。 */
    public void previewPhotoLayout(PhotoMsg photoMsg, String momentId, String watchId) {
        LogUtil.d(TAG, "previewPhotoLayout#photoMsg:" + photoMsg);
        if (SystemUtil.isFastDoubleClick()) {
            LogUtil.d(TAG, "isFastDoubleClick");
            return;
        }
        if (photoMsg == null) {
            return;
        }
        String localPath = photoMsg.getLocalPath();
        Intent intent = new Intent(this.mContext, PhotoPreviewActivity.class);
        String downloadUrl = getPhotoDownloadUrl(photoMsg);
        intent.putExtra(PhotoPreviewActivity.EXTAR_LOCAL_PATH, localPath);
        intent.putExtra(PhotoPreviewActivity.EXTRA_DOWNLOAD_URL, downloadUrl);
        intent.putExtra(PhotoPreviewActivity.EXTRA_MSG_ID, momentId);
        intent.putExtra(PhotoPreviewActivity.WATCH_ID, watchId);
        intent.putExtra(PhotoPreviewActivity.TRACK_MD5_VALUE, photoMsg.getTrackMd5Value());
        intent.addFlags(NotificationFlag.NOTIFICATION_FLAG_CUSTOM);
        this.mContext.startActivity(intent);
    }

    /** 打开实况照片播放页：本地文件缺失时回退到网络地址。 */
    public void previewLivePhotoLayout(LivePhotoMsg livePhotoMsg, String momentId) {
        if (livePhotoMsg == null || livePhotoMsg.getVideoMsg() == null) {
            return;
        }
        VideoMsg videoMsg = livePhotoMsg.getVideoMsg();
        String localPath = livePhotoMsg.getLocalPath();
        boolean videoDownloaded = true;
        String photoDownloadUrl;
        if (android.text.TextUtils.isEmpty(localPath) || !FileUtils.exists(localPath)) {
            SmallPicSouce smallPic = livePhotoMsg.getSmallPic();
            photoDownloadUrl = getPhotoDownloadUrl(livePhotoMsg);
            if (smallPic == null || System.currentTimeMillis() >= smallPic.getUrlDeadline()) {
                Context context = this.mContext;
                ToastUtil.showShortCover(context, context.getString(R.string.image_invalid));
                return;
            }
        } else {
            photoDownloadUrl = localPath;
        }
        Intent intent = new Intent(this.mContext, PlayLivePhotoActivity.class);
        String localVideoPath = videoMsg.getLocalVideoPath();
        if (android.text.TextUtils.isEmpty(localVideoPath) || !FileUtils.exists(localVideoPath)) {
            CloudFileResource transfer = videoMsg.getTransfer();
            if (transfer == null) {
                LogUtil.d(TAG, "CloudFileResource null");
                return;
            }
            localVideoPath = FileManager.getLivePhotoCachePath() + transfer.getKey();
            if (!FileUtils.exists(localVideoPath)) {
                String downloadUrl = videoMsg.getTransfer().getDownloadUrl();
                if (System.currentTimeMillis() >= videoMsg.getTransfer().getUrlDeadline()) {
                    Context context = this.mContext;
                    ToastUtil.showShortCover(context, context.getString(R.string.image_invalid));
                    return;
                }
                intent.putExtra(PlayLivePhotoActivity.VIDEO_OUT_PATH, localVideoPath);
                localVideoPath = downloadUrl;
                videoDownloaded = false;
            }
        }
        intent.putExtra(PlayLivePhotoActivity.VIDEO_HAS_DOWNLOAD, videoDownloaded);
        intent.putExtra(PlayLivePhotoActivity.VIDEO_THUMNAIL_PATH, photoDownloadUrl);
        intent.putExtra(PlayLivePhotoActivity.VIDEO_FILE_PATH, localVideoPath);
        this.mContext.startActivity(intent);
    }

    /** 打开图片预览页（仅有网络地址的场景）。 */
    public void previewPhotoLayout(String downloadUrl, String momentId) {
        LogUtil.d(TAG, "previewPhotoLayout#url:" + downloadUrl);
        if (SystemUtil.isFastDoubleClick()) {
            LogUtil.d(TAG, "isFastDoubleClick");
            return;
        }
        if (downloadUrl == null || TextUtils.isEmpty(downloadUrl)) {
            return;
        }
        Intent intent = new Intent(this.mContext, PhotoPreviewActivity.class);
        intent.putExtra(PhotoPreviewActivity.EXTRA_DOWNLOAD_URL, downloadUrl);
        intent.putExtra(PhotoPreviewActivity.EXTRA_MSG_ID, momentId);
        this.mContext.startActivity(intent);
    }

    /** 优先使用未过期的小图地址，否则回退到原图地址。 */
    private String getPhotoDownloadUrl(PhotoMsg photoMsg) {
        SmallPicSouce smallPic = photoMsg.getSmallPic();
        CloudFileResource source = photoMsg.getSource();
        long now = System.currentTimeMillis();
        if (smallPic != null && now < smallPic.getUrlDeadline()) {
            return smallPic.getDownloadUrl();
        }
        if (source != null) {
            return source.getDownloadUrl();
        }
        return null;
    }
    @Override
    public int getItemViewType(int position) {
        if (this.mHeaderView != null && position == 0) {
            return MomentTypeUtil.ITEM_TYPE_HEADER;
        }
        if (this.mFooterView != null && position == getItemCount() - 1) {
            return MomentTypeUtil.ITEM_TYPE_FOOTER;
        }
        if (this.mEmptyView == null || this.mData.size() != 0) {
            return MomentTypeUtil.getMomentType(this.mData.get(getDataItemPosition(position)));
        }
        return MomentTypeUtil.ITEM_TYPE_EMPTY;
    }

    @Override
    public int getItemCount() {
        int size = this.mData != null ? this.mData.size() : 0;
        if (this.mEmptyView != null && size == 0) {
            size++;
        }
        if (this.mHeaderView != null) {
            size++;
        }
        return this.mFooterView != null ? size + 1 : size;
    }

    public void setHeaderView(View headerView) {
        this.mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public View getHeaderView() {
        return this.mHeaderView;
    }

    public void setFooterView(View footerView) {
        this.mFooterView = footerView;
        notifyItemInserted(getItemCount() - 1);
    }

    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
        notifyDataSetChanged();
    }

    /** 按动态 id 查找列表中的动态。 */
    public DbMoment getMoment(String momentId) {
        for (DbMoment moment : this.mData) {
            if (moment.getMomentId().equals(momentId)) {
                return moment;
            }
        }
        return null;
    }

    public boolean contains(DbMoment moment) {
        if (this.mData == null || this.mData.isEmpty() || moment == null) {
            return false;
        }
        return this.mData.contains(moment);
    }

    /** 删除单条动态，并同步清理置顶引用。 */
    public void removeData(DbMoment moment) {
        if (this.mData == null || moment == null) {
            LogUtil.d(TAG, "removeData fail");
            return;
        }
        DbMoment topping = this.mToppingMoment;
        if (topping != null && Objects.equals(topping.getMomentId(), moment.getMomentId())) {
            this.mToppingMoment = null;
        }
        for (DbMoment item : this.mData) {
            if (!android.text.TextUtils.isEmpty(item.getMomentId())
                    && item.getMomentId().equals(moment.getMomentId())) {
                this.mData.remove(item);
                this.needNotify = true;
                notifyDataSetChanged();
                return;
            }
        }
    }

    /** 批量删除动态。 */
    public void removeDatas(List<DbMoment> moments) {
        if (this.mData == null || CollectionUtil.isEmpty(moments)) {
            LogUtil.d(TAG, "removeDatas fail");
            return;
        }
        HashMap<String, Boolean> momentIds = new HashMap<>();
        for (DbMoment moment : moments) {
            if (moment != null && !TextUtils.isEmpty(moment.getMomentId())) {
                momentIds.put(moment.getMomentId(), true);
            }
        }
        DbMoment topping = this.mToppingMoment;
        if (topping != null && momentIds.containsKey(topping.getMomentId())) {
            this.mToppingMoment = null;
        }
        Iterator<DbMoment> iterator = this.mData.iterator();
        while (iterator.hasNext()) {
            if (momentIds.containsKey(iterator.next().getMomentId())) {
                iterator.remove();
                this.needNotify = true;
            }
        }
        if (this.needNotify) {
            notifyDataSetChanged();
        }
    }

    /** 在指定位置插入一条动态，返回其在列表中的实际位置。 */
    public int addData(int index, DbMoment moment) {
        if (this.mData == null) {
            this.mData = new ArrayList<>();
        }
        if (moment == null || this.mData.contains(moment)) {
            Utils.logMoment(TAG, "dbMoment is null or existed:", moment);
            return -1;
        }
        if (this.mToppingMoment != null && index == 0) {
            LogUtil.d(TAG, "addData: when mToppingMoment != null");
            index = 1;
        }
        this.verticallyLinearLayoutManager.setScrollEnabled(false);
        this.mData.add(index, moment);
        int holderPosition = this.mHeaderView != null ? index + 1 : index;
        notifyItemInserted(holderPosition);
        this.verticallyLinearLayoutManager.setScrollEnabled(true);
        Utils.logMoment(TAG, "addData: index = [" + index + "]", moment);
        return holderPosition;
    }

    /** 批量插入动态，必要时按时间重排并保持置顶动态在最前。 */
    public void addData(List<DbMoment> moments) {
        if (moments == null || moments.isEmpty()) {
            return;
        }
        if (this.mToppingMoment != null
                && System.currentTimeMillis() >= this.mToppingMoment.getTopExpireTime()) {
            LogUtil.d(TAG, "置顶消息过期了：" + this.mToppingMoment);
            this.mToppingMoment = null;
        }
        if (this.mData == null) {
            this.mData = new ArrayList<>();
        }
        int size = this.mData.size();
        boolean hasInsert = false;
        int insertCount = 0;
        for (DbMoment moment : moments) {
            if (moment == null || this.mData.contains(moment)) {
                continue;
            }
            // 仅插入官方动态或已是好友的动态，避免展示异常数据。
            if (advertisement(moment.getType().intValue())
                    || this.friendWatchIds.get(moment.getWatchId()) != null) {
                if (OfficialMomentTypeUtil.isTopOfficialMoment(moment)) {
                    LogUtil.d(TAG, "当前需要置顶消息：" + moment);
                    this.mToppingMoment = moment;
                }
                Utils.logMoment(TAG, "addData: ", moment);
                this.mData.add(moment);
                insertCount++;
                hasInsert = true;
            } else {
                Utils.logMoment(TAG, "异常动态: ", moment);
            }
        }
        if (hasInsert) {
            this.verticallyLinearLayoutManager.setScrollEnabled(false);
            this.needNotify = true;
            if (this.mHeaderView != null) {
                size++;
            }
            notifyItemChanged(size, Integer.valueOf(insertCount));
            this.verticallyLinearLayoutManager.setScrollEnabled(true);
        }

        // 检查插入后是否出现时间倒序，需要重排。
        boolean needResort = false;
        int cursor = 0;
        while (true) {
            int next = cursor + 1;
            if (next >= this.mData.size()) {
                break;
            }
            if (this.mData.get(cursor).getCreateTime() < this.mData.get(next).getCreateTime()
                    && !OfficialMomentTypeUtil.isTopOfficialMoment(this.mData.get(cursor))) {
                needResort = true;
                break;
            }
            cursor = next;
        }
        if (needResort) {
            LogUtil.w(TAG, "moment data resort");
            Collections.sort(this.mData, new Comparator<DbMoment>() {
                @Override
                public int compare(DbMoment first, DbMoment second) {
                    return second.getCreateTime().compareTo(first.getCreateTime());
                }
            });
            if (this.mToppingMoment != null) {
                LogUtil.d(TAG, "重新排序后操作了置顶消息");
                this.mData.remove(this.mToppingMoment);
                this.mData.add(0, this.mToppingMoment);
                LogUtil.d(TAG, "addData: mToppingMoment = [" + this.mToppingMoment + "]");
            }
            this.verticallyLinearLayoutManager.setScrollEnabled(false);
            this.needNotify = true;
            notifyDataSetChanged();
            this.verticallyLinearLayoutManager.setScrollEnabled(true);
        } else if (this.mToppingMoment != null && this.mData.indexOf(this.mToppingMoment) != 0) {
            LogUtil.d(TAG, "没有排序，需要单独置顶消息");
            this.mData.remove(this.mToppingMoment);
            this.mData.add(0, this.mToppingMoment);
            this.verticallyLinearLayoutManager.setScrollEnabled(false);
            this.needNotify = true;
            notifyDataSetChanged();
            this.verticallyLinearLayoutManager.setScrollEnabled(true);
            LogUtil.d(TAG, "addData: mToppingMoment = [" + this.mToppingMoment + "]");
        }
        LogUtil.d(TAG, "notify：" + hasInsert + ";notifyCount:" + insertCount + ";resort:" + needResort);
    }

    public void clearData() {
        if (this.mData == null || this.mData.size() <= 0) {
            return;
        }
        this.mData.clear();
    }

    public DbMoment getData(int position) {
        int dataPosition = getDataItemPosition(position);
        if (dataPosition < 0 || dataPosition >= this.mData.size()) {
            return null;
        }
        return this.mData.get(dataPosition);
    }

    public List<DbMoment> getData() {
        return this.mData;
    }

    public int indexOf(DbMoment moment) {
        if (moment == null) {
            return -1;
        }
        return this.mData.indexOf(moment);
    }

    public DbMoment getLastData() {
        if (CollectionUtil.isEmpty(this.mData)) {
            return null;
        }
        return this.mData.get(this.mData.size() - 1);
    }
    public void refreshLikeData(DbMoment moment) {
        refreshData(moment, AbsInteractionAdapter.PART_REFRESH_PRAISE);
    }

    public void refreshPushLikeData(DbMoment moment) {
        refreshData(moment, PART_REFRESH_PUSH_LIKE);
    }

    public void refreshLocalPathData(DbMoment moment) {
        refreshData(moment, PART_REFRESH_LOCAL_PATH);
    }

    public void refreshVisiblePicData(DbMoment moment) {
        refreshData(moment, PART_REFRESH_VISIBLE_PIC);
    }

    public void refreshMomentReminder(DbMoment moment) {
        refreshData(moment, PART_REFRESH_MOMENT_REMINDER);
    }

    /** 按 momentId 找到对应列表项并触发局部刷新。 */
    private void refreshData(DbMoment moment, String refreshType) {
        LogUtil.d(TAG, "dbMoment: " + moment + ", refreshType: " + refreshType);
        if (this.mData == null || this.mData.isEmpty()) {
            return;
        }
        int index = 0;
        while (index < this.mData.size()
                && (this.mData.get(index) == null
                        || this.mData.get(index).getMomentId() == null
                        || !this.mData.get(index).getMomentId().equals(moment.getMomentId()))) {
            index++;
        }
        if (index >= this.mData.size()) {
            return;
        }
        DbMoment old = this.mData.get(index);
        this.verticallyLinearLayoutManager.setScrollEnabled(false);
        if (old != null) {
            LogUtil.d(TAG, "通过点赞查到的dbMoment对象总评论条数为：" + old.getCommentsTotalCount());
            moment.setCommentsTotalCount(old.getCommentsTotalCount());
        }
        this.mData.set(index, moment);
        LogUtil.d(TAG, "part_refresh_holder:" + getHolderPosition(moment));
        notifyItemChanged(getHolderPosition(moment), refreshType);
        this.verticallyLinearLayoutManager.setScrollEnabled(true);
    }

    /** 评论列表更新后刷新对应动态条目。 */
    public void refreshMomentCommentData(DbMoment moment) {
        LogUtil.e(TAG, "refreshMomentCommentData: " + moment);
        if (this.mData == null || this.mData.isEmpty()) {
            return;
        }
        int index = -1;
        Iterator<DbMoment> iterator = this.mData.iterator();
        while (iterator.hasNext()) {
            DbMoment item = iterator.next();
            if (item.getMomentId() != null && item.getMomentId().equals(moment.getMomentId())) {
                index = this.mData.indexOf(item);
                break;
            }
        }
        if (index == -1 || moment.getComments() == null) {
            return;
        }
        this.verticallyLinearLayoutManager.setScrollEnabled(false);
        this.mData.set(index, moment);
        notifyItemChanged(getHolderPosition(moment), AbsInteractionAdapter.PART_REFRESH_COMMENT);
        this.verticallyLinearLayoutManager.setScrollEnabled(true);
    }

    public void addCommentData(DbMomentComment comment) {
        LogUtil.d(TAG, "addCommentData: " + comment.getMomentId());
        this.refreshCommentMap.put(comment.getMomentId(), true);
        refreshCommentData(comment, true);
    }

    public void removeCommentData(DbMomentComment comment) {
        LogUtil.d(TAG, "removeCommentData: " + comment.getMomentId());
        refreshCommentData(comment, false);
    }

    /** 删除好友后，移除其动态与相关评论。 */
    public void deleteFriendInfo(String watchId) {
        if (TextUtils.isEmpty(watchId)) {
            return;
        }
        boolean changed;
        if (this.mData == null || this.mData.isEmpty()) {
            changed = false;
        } else {
            Iterator<DbMoment> momentIterator = this.mData.iterator();
            changed = false;
            while (momentIterator.hasNext()) {
                DbMoment moment = momentIterator.next();
                if (watchId.equals(moment.getWatchId())) {
                    momentIterator.remove();
                    LogUtil.i(TAG, "remove friend moment:" + moment);
                    changed = true;
                }
                if (!CollectionUtil.isEmpty(moment.getComments())) {
                    Iterator<DbMomentComment> commentIterator = moment.getComments().iterator();
                    while (commentIterator.hasNext()) {
                        DbMomentComment comment = commentIterator.next();
                        if (isDeleteComment(watchId, comment)) {
                            commentIterator.remove();
                            moment.setCommentsTotalCount(moment.getCommentsTotalCount() - 1);
                            LogUtil.i(TAG, "remove friend DbMomentComment:" + comment);
                            changed = true;
                        }
                    }
                }
            }
        }
        List<Friend> friends = this.friendList;
        if (friends != null && !friends.isEmpty()) {
            Iterator<Friend> friendIterator = this.friendList.iterator();
            while (friendIterator.hasNext()) {
                Friend friend = friendIterator.next();
                if (watchId.equals(friend.getWatchId())) {
                    friendIterator.remove();
                    LogUtil.i(TAG, "remove friend:" + friend);
                    changed = true;
                }
            }
        }
        if (changed) {
            this.verticallyLinearLayoutManager.setScrollEnabled(false);
            notifyDataSetChanged();
            this.verticallyLinearLayoutManager.setScrollEnabled(true);
        }
    }

    /** 评论的作者或回复对象是该好友时需要一并删除。 */
    private boolean isDeleteComment(String watchId, DbMomentComment comment) {
        return comment != null
                && ((!TextUtils.isEmpty(comment.getWatchId()) && comment.getWatchId().equals(watchId))
                        || (!TextUtils.isEmpty(comment.getReplyId()) && comment.getReplyId().equals(watchId)));
    }

    public void setFriendList(List<Friend> friends) {
        this.friendList = friends;
        for (int i = 0; i < friends.size(); i++) {
            Friend friend = friends.get(i);
            if (friend != null && !TextUtils.isEmpty(friend.getWatchId())) {
                this.friendWatchIds.put(friend.getWatchId(), friend.getWatchId());
            }
        }
        String selfId = MomentApp.getWatchId();
        this.friendWatchIds.put(selfId, selfId);
        this.verticallyLinearLayoutManager.setScrollEnabled(false);
        notifyDataSetChanged();
        this.verticallyLinearLayoutManager.setScrollEnabled(true);
    }

    public void onResume() {
        if (this.needNotify) {
            this.needNotify = false;
            notifyDataSetChanged();
        }
    }

    private boolean advertisement(int type) {
        return MomentTypeUtil.isOfficialType(type);
    }

    @Override
    public void showContactPortrait(Context context, ContactHeadManager headManager, ContactBean contactBean,
            View view, BitmapDrawable headBitmap) {
        view.setTag(R.string.contact_head_dislocation_tag, null);
        view.setTag(R.string.contact_head_last_update_tag, null);
        DrawableCrossFadeFactory crossFadeFactory = new DrawableCrossFadeFactory.Builder(250)
                .setCrossFadeEnabled(true)
                .build();
        Glide.with(context)
                .load(headBitmap)
                .apply(new RequestOptions()
                        .error(R.drawable.default_custom_default)
                        .placeholder(R.drawable.default_custom_default)
                        .centerCrop())
                .transition(DrawableTransitionOptions.with((TransitionFactory<Drawable>) crossFadeFactory))
                .into((ImageView) view);
    }

    public void setCancelLikeTime(long cancelTime) {
        this.lastCancelTime = cancelTime;
    }

    /** 刷新当前账号信息（昵称、头像、评论开关）。 */
    public void refreshSelfInfo() {
        IAccountInfoServe accountInfoServe = AccountInfoServerImpl.getInstance(this.mContext);
        this.selfWatchId = accountInfoServe.getWatchAccountInfo().getWatchId(this.mContext);
        this.myName = accountInfoServe.getWatchAccountInfo().getName(this.mContext);
        this.myIconPath = accountInfoServe.getMyHeadIconPath();
        this.commentSwitch = ModuleSwitchUtil.queryModuleSwitchByBoolean(this.mContext,
                ModuleSwitchConstant.MODULE_SWITCH_MOMENT_COMMENT, false);
        LogUtil.d(TAG, "MomentAdapter: commentSwitch 120 = " + this.commentSwitch);
    }

    @Override
    public void onViewAttachedToWindow(AbsViewHolder viewHolder) {
        super.onViewAttachedToWindow(viewHolder);
        OnViewAttachedToWindowListener listener = this.mViewAttachedToWindowListener;
        if (listener != null) {
            listener.onViewAttachedToWindow(viewHolder);
        }
    }

    @Override
    public void onViewDetachedFromWindow(AbsViewHolder viewHolder) {
        super.onViewDetachedFromWindow(viewHolder);
        OnViewAttachedToWindowListener listener = this.mViewAttachedToWindowListener;
        if (listener != null) {
            listener.onViewDetachedFromWindow(viewHolder);
        }
    }

    public void setViewAttachedToWindow(OnViewAttachedToWindowListener listener) {
        this.mViewAttachedToWindowListener = listener;
    }

    @Override
    public Lifecycle getLifecycle() {
        return this.lifecycleRegistry;
    }

    /** 刷新点赞动画资源（特权装扮变更后调用）。 */
    public void refreshPrerogativeLike() {
        if (this.momentLikeViewWindow != null) {
            this.momentLikeViewWindow.initAnim();
        }
    }

    @Override
    public String getLogTag() {
        return TAG;
    }
}
