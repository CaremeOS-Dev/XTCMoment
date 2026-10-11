package com.xtc.moment.module.share.adapter;

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
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.xtc.contactapi.contact.bean.ContactBean;
import com.xtc.contactapi.contact.manager.ContactManager;
import com.xtc.contactapi.contacthead.config.ContactHeadManagerConfig;
import com.xtc.contactapi.contacthead.impl.ContactHeadManager;
import com.xtc.contactapi.contacthead.interfaces.IShowHeadToViewStrategy;
import com.xtc.httplib.net.HttpSubscriber;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.manager.ReportDataRecorder;
import com.xtc.moment.module.bean.CloudFileResource;
import com.xtc.moment.module.bean.CropTransform;
import com.xtc.moment.module.bean.LivePhotoMsg;
import com.xtc.moment.module.bean.MultiPhotoContent;
import com.xtc.moment.module.bean.PhotoMsg;
import com.xtc.moment.module.bean.ShareAppMoment;
import com.xtc.moment.module.bean.ShareImageMoment;
import com.xtc.moment.module.bean.ShareTextPublish;
import com.xtc.moment.module.bean.ShareVideoMoment;
import com.xtc.moment.module.bean.ShareWebMoment;
import com.xtc.moment.module.bean.SmallPicSouce;
import com.xtc.moment.module.bean.VideoKeyOrToken;
import com.xtc.moment.module.bean.VideoMsg;
import com.xtc.moment.module.personalinfo.AccountInfoActivity;
import com.xtc.moment.module.personalinfo.constant.PersonalInfoConstant;
import com.xtc.moment.module.playvideo.PlayVideoActivity;
import com.xtc.moment.module.publish.multi.SaveDynamic;
import com.xtc.moment.module.publish.multi.adapter.BaseOverlayPageAdapter;
import com.xtc.moment.module.publish.multi.view.PointerViewPager;
import com.xtc.moment.module.report.adapter.AbsInteractionAdapter;
import com.xtc.moment.module.scope.FriendsVisibleRangeActivity;
import com.xtc.moment.module.share.holder.AbsViewHolder;
import com.xtc.moment.module.share.holder.ShareVideoHolder;
import com.xtc.moment.module.viewholder.comment.MomentCommentAdapter;
import com.xtc.moment.module.widget.CommentIconView;
import com.xtc.moment.module.widget.MainMomentCommentView;
import com.xtc.moment.module.widget.MomentContentView;
import com.xtc.moment.module.widget.MomentLikeView;
import com.xtc.moment.module.widget.MomentReminderView;
import com.xtc.moment.module.widget.PhotoPreviewActivity;
import com.xtc.moment.module.widget.VerticallyLinearLayoutManager;
import com.xtc.moment.module.widget.livephotoView.PlayLivePhotoActivity;
import com.xtc.moment.net.MomentPhotoServeHttpProxy;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.serve.bean.DownloadUrlVo;
import com.xtc.moment.serve.bean.FileBatchUrlParam;
import com.xtc.moment.serve.bean.FileUrlParam;
import com.xtc.moment.serve.impl.MomentPhotoServeImpl;
import com.xtc.moment.util.ClickUtils;
import com.xtc.moment.util.DressUtil;
import com.xtc.moment.util.FileManager;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.LikeDrawableCache;
import com.xtc.moment.util.MomentLikeViewWindow;
import com.xtc.moment.util.MomentTypeUtil;
import com.xtc.moment.util.RxUtils;
import com.xtc.moment.util.ShareAppStartUtil;
import com.xtc.moment.util.StartWebUtils;
import com.xtc.moment.util.SystemUtil;
import com.xtc.moment.util.ToastUtil;
import com.xtc.moment.util.Utils;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;
import com.xtc.moment.widget.LbsLayout;
import com.xtc.shareapi.share.bean.DialogBitmapArgs;
import com.xtc.system.account.constant.NotificationFlag;
import com.xtc.utils.common.CollectionUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.storage.FileUtils;
import com.xtc.utils.ui.DimenUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 分享动态列表适配器，负责创建各类 ViewHolder、局部刷新与图片预览跳转。
 */
public class ShareAdapter extends AbsInteractionAdapter<AbsViewHolder>
        implements LifecycleOwner, View.OnClickListener, IShowHeadToViewStrategy {

    private static final int ITEM_TYPE_EMPTY = 2;
    private static final int ITEM_TYPE_FOOTER = 11;
    private static final int ITEM_TYPE_HEADER = 10;
    private static final int ITEM_TYPE_LIVE_PHOTO = 12;
    private static final int ITEM_TYPE_NORMAL = 1;
    private static final int ITEM_TYPE_PHOTO = 4;
    private static final int ITEM_TYPE_SHARE_APP = 9;
    private static final int ITEM_TYPE_SHARE_H5 = 14;
    private static final int ITEM_TYPE_SHARE_IMAGE = 8;
    private static final int ITEM_TYPE_SHARE_LIVE_PHOTO = 13;
    private static final int ITEM_TYPE_SHARE_TEXT = 7;
    private static final int ITEM_TYPE_SHARE_VIDEO = 15;
    private static final int ITEM_TYPE_VIDEO = 6;
    private static final int ITEM_TYPE_VOICE = 5;
    private static final String TAG = "XTC_MOMENT_ShareAdapter";

    private boolean commentSwitch;
    private ContactManager contactManager;
    private boolean isSupportPersonalCenter;
    private LifecycleRegistry lifecycleRegistry;
    private String mAccountWatchId;
    protected Context mContext;
    private String mIconPath;
    private boolean mIsSelf;
    private LikeDrawableCache mLikeDrawableCache;
    private String mName;
    private ContactHeadManager manager;
    private Map<String, List<DbLikeMessage>> praiseRecordMap;
    private boolean rangeSwitch;
    private String selfWatchId;
    private final String watchId;

    /** 内容点击回调。 */
    public interface OnContentOnClickListener {
        void preVideoView(String videoData);

        void previewH5(String url);

        void previewLivePhoto(LivePhotoMsg livePhotoMsg);

        void previewPhoto(PhotoMsg photoMsg);

        void previewPhoto(String url);
    }

    /** 内容长按回调。 */
    public interface OnContentOnLongClickListener {
        void deleteItem(DbMoment moment);
    }
    @Override
    public String getLogTag() {
        return TAG;
    }


    public ShareAdapter(Activity activity, boolean isSelf, String name, String iconPath,
            VerticallyLinearLayoutManager layoutManager, String watchId, LikeDrawableCache likeDrawableCache) {
        super(activity);
        this.selfWatchId = "";
        this.commentSwitch = true;
        this.rangeSwitch = true;
        this.mContext = activity;
        this.mIsSelf = isSelf;
        this.mName = name;
        this.mIconPath = iconPath;
        this.verticallyLinearLayoutManager = layoutManager;
        this.mAccountWatchId = AccountInfoServerImpl.getInstance(this.mContext).getWatchAccountInfo()
                .getWatchId(this.mContext);
        this.watchId = watchId;
        this.manager = new ContactHeadManagerConfig.Builder().context(this.mContext)
                .showHeadToViewStrategy(this).build();
        this.selfWatchId = AccountInfoServerImpl.getInstance(this.mContext).getWatchAccountInfo().getWatchId(activity);
        this.lifecycleRegistry = new LifecycleRegistry(this);
        this.lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        this.momentLikeViewWindow = new MomentLikeViewWindow();
        this.momentLikeViewWindow.initLikeAnimation(this.mContext, this, this.lifecycleRegistry);
        this.mLikeDrawableCache = likeDrawableCache;
    }

    public void updateInfo(String iconPath, String name) {
        this.mName = name;
        this.mIconPath = iconPath;
    }

    @Override
    public Lifecycle getLifecycle() {
        return this.lifecycleRegistry;
    }

    @Override
    public void showContactPortrait(Context context, ContactHeadManager contactHeadManager, ContactBean contactBean,
            View view, BitmapDrawable bitmapDrawable) {
        view.setTag(R.string.contact_head_dislocation_tag, null);
        view.setTag(R.string.contact_head_last_update_tag, null);
        DrawableCrossFadeFactory crossFadeFactory = new DrawableCrossFadeFactory.Builder(250)
                .setCrossFadeEnabled(true).build();
        Glide.with(context).load(bitmapDrawable)
                .apply(new RequestOptions().error(R.drawable.default_custom_default)
                        .placeholder(R.drawable.default_custom_default))
                .transition(DrawableTransitionOptions.withCrossFade(crossFadeFactory))
                .into((ImageView) view);
    }

    public void removeData(DbMoment moment) {
        if (this.mData == null || moment == null) {
            LogUtil.d(TAG, "removeData fail");
            return;
        }
        LogUtil.d(TAG, "mData:" + this.mData.toString() + ";dbMoment:" + moment.toString());
        for (DbMoment item : this.mData) {
            if (!TextUtils.isEmpty(item.getMomentId()) && item.getMomentId().equals(moment.getMomentId())) {
                this.mData.remove(item);
                notifyDataSetChanged();
                return;
            }
        }
    }

    public void removeComment(DbMomentComment targetComment) {
        if (this.mData == null || targetComment == null) {
            LogUtil.d(TAG, "removeData fail");
            return;
        }
        for (DbMoment moment : this.mData) {
            if (com.xtc.log.util.TextUtils.isEmpty(moment.getMomentId())
                    || !moment.getMomentId().equals(targetComment.getMomentId())) {
                continue;
            }
            List<DbMomentComment> comments = moment.getComments();
            if (comments == null || comments.size() <= 0) {
                return;
            }
            for (DbMomentComment comment : comments) {
                if (com.xtc.log.util.TextUtils.isEmpty(comment.getCommentId())
                        || !comment.getCommentId().equals(targetComment.getCommentId())) {
                    continue;
                }
                boolean removed = comments.remove(comment);
                if (comments.size() <= 5 && moment.getCommentsTotalCount() > 5) {
                    moment.setComments(comments);
                    notifyItemChanged(getHolderPosition(moment), "part_refresh_comment");
                    break;
                }
                Boolean refreshFlag = this.refreshCommentMap.get(comment.getMomentId());
                LogUtil.d(TAG, "refreshCommentData: " + refreshFlag + "  " + comment.getMomentId() + removed
                        + "   -" + comments.size() + moment.getCommentsTotalCount());
                if (refreshFlag != null && refreshFlag.booleanValue() && removed) {
                    int total = moment.getCommentsTotalCount();
                    if (total > 0) {
                        moment.setCommentsTotalCount(total - 1);
                    }
                } else if (CollectionUtil.isEmpty(comments) || (removed && moment.getCommentsTotalCount() > 5)) {
                    if (moment.getCommentsTotalCount() <= 5) {
                        moment.setCommentsTotalCount(0);
                    } else {
                        int total = moment.getCommentsTotalCount();
                        if (total > 0) {
                            moment.setCommentsTotalCount(total - 1);
                        }
                    }
                }
                LogUtil.d(TAG, "refreshCommentData: " + refreshFlag + "  " + comment.getMomentId() + removed
                        + "   -" + comments.size() + moment.getCommentsTotalCount());
                if (comments.size() > moment.getCommentsTotalCount()) {
                    moment.setCommentsTotalCount(comments.size());
                }
                LogUtil.d(TAG, "refreshCommentData: " + refreshFlag + "  " + targetComment.getMomentId() + removed
                        + "   -" + comments.size() + moment.getCommentsTotalCount());
                moment.setComments(comments);
                notifyItemChanged(getHolderPosition(moment), "part_refresh_comment");
                break;
            }
        }
    }

    public void refreshReminder(DbMoment moment) {
        refreshData(moment, "part_refresh_share_reminder");
    }

    public void refreshVisible(DbMoment moment) {
        refreshData(moment, "part_refresh_visible_pic");
    }

    private void refreshData(DbMoment moment, String payload) {
        if (this.mData == null || this.mData.isEmpty()) {
            return;
        }
        int index = 0;
        while (index < this.mData.size() && (this.mData.get(index) == null
                || this.mData.get(index).getMomentId() == null
                || !this.mData.get(index).getMomentId().equals(moment.getMomentId()))) {
            index++;
        }
        if (index < this.mData.size()) {
            this.verticallyLinearLayoutManager.setScrollEnabled(false);
            this.mData.set(index, moment);
            LogUtil.d(TAG, "part_refresh_holder:" + getHolderPosition(moment));
            notifyItemChanged(getHolderPosition(moment), payload);
            this.verticallyLinearLayoutManager.setScrollEnabled(true);
        }
    }

    public void addComment(DbMomentComment comment) {
        if (this.mData == null || comment == null) {
            LogUtil.d(TAG, "addComment fail");
            return;
        }
        this.refreshCommentMap.put(comment.getMomentId(), true);
        for (DbMoment moment : this.mData) {
            if (com.xtc.log.util.TextUtils.isEmpty(moment.getMomentId())
                    || !moment.getMomentId().equals(comment.getMomentId())) {
                continue;
            }
            List<DbMomentComment> comments = moment.getComments();
            if (comments == null) {
                comments = new ArrayList<>();
            }
            comments.add(comment);
            moment.setComments(comments);
            int total = moment.getCommentsTotalCount();
            int size = comments.size();
            if (size >= 5 || total <= 5) {
                total++;
            }
            if (size > total) {
                total = size;
            }
            moment.setCommentsTotalCount(total);
            notifyItemChanged(getHolderPosition(moment), "part_refresh_comment");
            return;
        }
    }

    public List<DbLikeMessage> getLikeMaps(String momentId) {
        Map<String, List<DbLikeMessage>> map = this.praiseRecordMap;
        if (map == null) {
            return new ArrayList<>();
        }
        return map.get(momentId);
    }

    public void setLastCancelTime(long lastCancelTime) {
        this.lastCancelTime = lastCancelTime;
    }

    public void addCommentData(DbMomentComment comment) {
        refreshCommentData(comment, true);
    }
    @Override
    public AbsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogUtil.i(TAG, "onCreateViewHolder" + viewType);
        final AbsViewHolder holder;
        if (viewType == ITEM_TYPE_EMPTY) {
            ViewGroup emptyParent = (ViewGroup) this.mEmptyView.getParent();
            if (emptyParent != null) {
                emptyParent.removeView(this.mEmptyView);
            }
            holder = new ShareInnerViewHolder(this.mEmptyView);
        } else if (viewType == ITEM_TYPE_PHOTO) {
            holder = new ShareInnerPhotoViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recycle_share_photo, parent, false));
        } else if (viewType == 28) {
            holder = new PhotoViewHolders(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recycle_photos, parent, false));
        } else if (viewType == 29) {
            holder = new ShareVideoViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recycle_personal_video, parent, false));
        } else {
            switch (viewType) {
                case ITEM_TYPE_VIDEO:
                    holder = new ShareVideoViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_recycle_personal_video, parent, false));
                    break;
                case ITEM_TYPE_SHARE_TEXT:
                    holder = new ShareTextInnerViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_recycle_personal_share_text, parent, false));
                    break;
                case ITEM_TYPE_SHARE_IMAGE:
                    holder = new ShareImageInnerViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_recycle_personal_share_image, parent, false));
                    break;
                case ITEM_TYPE_SHARE_APP:
                    holder = new ShareAppInnerViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_recycle_personal_share_app, parent, false));
                    break;
                case ITEM_TYPE_HEADER:
                    ViewGroup headerParent = (ViewGroup) this.mHeaderView.getParent();
                    if (headerParent != null) {
                        headerParent.removeView(this.mHeaderView);
                    }
                    holder = new ShareInnerViewHolder(this.mHeaderView);
                    break;
                case ITEM_TYPE_FOOTER:
                    ViewGroup footerParent = (ViewGroup) this.mFooterView.getParent();
                    if (footerParent != null) {
                        footerParent.removeView(this.mFooterView);
                    }
                    holder = new ShareInnerViewHolder(this.mFooterView);
                    break;
                case ITEM_TYPE_LIVE_PHOTO:
                    holder = new ShareLivePhotoInnerViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_recycle_personal_live_photo, parent, false));
                    break;
                case ITEM_TYPE_SHARE_LIVE_PHOTO:
                    holder = new ShareLivePhotoShareViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_recycle_personal_share_live_photo, parent, false));
                    break;
                case ITEM_TYPE_SHARE_H5:
                    holder = new ShareWebInnerViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_recycle_personal_share_web, parent, false));
                    break;
                case ITEM_TYPE_SHARE_VIDEO:
                    holder = new ShareVideoHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_recycle_personal_share_video, parent, false));
                    break;
                default:
                    holder = new ShareInnerViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_recycle_share, parent, false));
                    break;
            }
        }
        if (holder.llLbs != null) {
            holder.llLbs.setBackground(null);
            holder.llLbs.setClickable(true);
        }
        if (holder.ivMomentComment != null) {
            holder.ivMomentComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LogUtil.i(TAG, "ivMomentComment onclick = " + holder.getDbMoment());
                    if (SystemUtil.isFastDoubleClick()) {
                        LogUtil.i(TAG, "onClick: click too fast.");
                        return;
                    }
                    if (!commentSwitch || isDisableSend(true) || momentCommentListener == null) {
                        return;
                    }
                    momentCommentListener.comment(holder.getDbMoment());
                }
            });
        }
        if (holder.momentCommentView != null) {
            holder.momentCommentView.setOnMomentCommentListener(new MomentCommentAdapter.OnMomentCommentListener() {
                @Override
                public void onLoadMoreCommentClick(int index, DbMoment moment) {
                }

                @Override
                public void onMomentCommentClick(int index, DbMomentComment comment) {
                    if (!commentSwitch || isDisableSend(true)) {
                        LogUtil.d(TAG, "onMomentCommentClick: commentSwitch = " + commentSwitch);
                        return;
                    }
                    LogUtil.d(TAG, "index: 点击" + index + ";dbMomentComment:" + comment);
                    if (momentCommentListener != null) {
                        momentCommentListener.reply(holder.getDbMoment(), comment);
                    }
                }
            });
        }
        if (holder.momentLike != null) {
            holder.momentLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onMomentLikeClick(holder, view);
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position, List<Object> payloads) {
        holder.setHolderContext(this.mContext);
        holder.setmIsSelf(this.mIsSelf);
        holder.setManager(this.manager);
        holder.setOnLikeClickListener(this.onLikeClickListener);
        holder.setLastCancelTime(this.lastCancelTime);
        holder.setmLikeAnimationWindow(this.momentLikeViewWindow);
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            if (checkTypeIsHandle(position)) {
                return;
            }
            String payload = (String) payloads.get(0);
            final DbMoment moment = this.mData.get(getDataItemPosition(position));
            holder.setDbMoment(moment);
            LogUtil.d(TAG, "局部刷新payload==" + payload + ",moment:" + moment.toString());
            if (AbsInteractionAdapter.PART_REFRESH_PRAISE.equals(payload)) {
                if (!this.selfWatchId.equals(moment.getWatchId()) && holder.momentLike != null) {
                    holder.momentLike.setLike(this.mContext, !moment.isEnableLike());
                }
                if (holder.momentLike != null) {
                    holder.momentLike.setCount(moment.getLikeTotal() != null ? moment.getLikeTotal().intValue() : 0);
                }
                holder.setTvLikes(moment, this.praiseRecordMap, this.mAccountWatchId, this.mLikeDrawableCache);
            } else if ("part_refresh_comment".equals(payload)) {
                List<DbMomentComment> comments = moment.getComments();
                this.mAccountWatchId = AccountInfoServerImpl.getInstance(this.mContext).getWatchAccountInfo()
                        .getWatchId(this.mContext);
                setMomentCommentData(holder, moment, comments);
            } else if ("part_refresh_visible_pic".equals(payload)) {
                if (holder.ivMomentRange == null || -1 == moment.getPermissionType()) {
                    return;
                }
                holder.momentCommentView.refreshVisible(moment.getPermissionType());
                if (moment.getPermissionType() == 0) {
                    holder.ivMomentRange.setVisibility(View.GONE);
                } else {
                    holder.ivMomentRange.setVisibility(View.VISIBLE);
                    holder.ivMomentRange.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (SystemUtil.isFastDoubleClick()) {
                                LogUtil.i(TAG, "onClick: click too fast.");
                                return;
                            }
                            Intent intent = new Intent(mContext, FriendsVisibleRangeActivity.class);
                            intent.putExtra("moment_id", moment.getMomentId());
                            mContext.startActivity(intent);
                        }
                    });
                }
            } else if ("part_refresh_share_reminder".equals(payload)) {
                setMomentReminder(holder, moment);
            }
        }
        if (this.commentSwitch) {
            holder.showCommentIcon();
        } else {
            holder.hideCommentIcon();
        }
    }

    private boolean checkTypeIsHandle(int position) {
        int viewType = getItemViewType(position);
        return viewType == ITEM_TYPE_HEADER || viewType == ITEM_TYPE_FOOTER || viewType == ITEM_TYPE_EMPTY;
    }

    private void setMomentCommentData(final AbsViewHolder holder, DbMoment moment, List<DbMomentComment> comments) {
        holder.hideRecyclerComment();
        if (comments != null && !comments.isEmpty()) {
            holder.momentCommentView.setDatas(this.mContext, comments, this.mAccountWatchId, moment);
            holder.momentCommentView.setOnCommentDeleteListener(new MomentCommentAdapter.OnCommentDeleteListener() {
                @Override
                public void onCommentDeleteClick(int index, DbMomentComment comment) {
                    dealCommentDeleteClick(holder.getDbMoment(), comment, selfWatchId, mIsSelf);
                }
            });
        } else {
            holder.hideComment();
        }
    }

    public void initSwitch() {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                commentSwitch = ModuleSwitchUtil.queryModuleSwitchByBoolean(mContext,
                        ModuleSwitchConstant.MODULE_SWITCH_MOMENT_COMMENT, false);
                isSupportPersonalCenter = ModuleSwitchUtil.queryModuleSwitchByBoolean(mContext,
                        ModuleSwitchConstant.MODULE_SWITCH_PERSONAL_CENTER, false);
                rangeSwitch = ModuleSwitchUtil.queryModuleSwitchByBoolean(mContext,
                        ModuleSwitchConstant.MODULE_VISIBLE_RANGE, false);
            }
        });
    }
    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position) {
        if (checkTypeIsHandle(position)) {
            return;
        }
        final DbMoment moment = this.mData.get(getDataItemPosition(holder.getAdapterPosition()));
        holder.setDbMoment(moment);
        LogUtil.d(TAG, "onBindViewHolder() returned: " + position);
        setMomentName(moment, holder, this.mIsSelf, this.mName);
        setMomentIcon(moment, holder, this.mIsSelf, this.mIconPath);
        setMomentReminder(holder, moment);
        holder.ivIcon.setOnClickListener(this);
        holder.tvName.getPaint().setShader(null);
        DressUtil.setNicknameSource(moment.getWatchId(), holder.tvName);
        holder.setTvTime(this.mContext, moment.getCreateTime().longValue());
        holder.setTvLikes(moment, this.praiseRecordMap, this.mAccountWatchId, this.mLikeDrawableCache);
        int type = moment.getType().intValue();
        if (holder.ivMomentRange != null && -1 != moment.getPermissionType() && moment.getPermissionType() != 0
                && this.mIsSelf && MomentTypeUtil.ableChangeVisibleRangeType(type) && this.rangeSwitch) {
            holder.ivMomentRange.setVisibility(View.VISIBLE);
            holder.ivMomentRange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemUtil.isFastDoubleClick()) {
                        LogUtil.i(TAG, "onClick: click too fast.");
                        return;
                    }
                    Intent intent = new Intent(mContext, FriendsVisibleRangeActivity.class);
                    LogUtil.d(TAG, "click go to FriendsVisibleRangeActivity :" + moment);
                    intent.putExtra("moment_id", moment.getMomentId());
                    mContext.startActivity(intent);
                }
            });
        } else if (holder.ivMomentRange != null) {
            holder.ivMomentRange.setVisibility(View.GONE);
        }
        if (holder.ivShareReportIcon != null) {
            holder.ivShareReportIcon.setVisibility(View.GONE);
        }
        setMomentCommentData(holder, moment, moment.getComments());
        LogUtil.i(TAG, " onBindViewHolder " + moment.getType());
        int momentType = moment.getType().intValue();
        if (momentType == 11) {
            setPhotoData(holder, moment, position);
        } else {
            switch (momentType) {
                case 0:
                case 1:
                case 3:
                case 7:
                    setTextData(holder, moment);
                    break;
                case 2:
                    setLocationData(holder, moment);
                    break;
                case 4:
                case 5:
                case 6:
                case 8:
                case 9:
                    setPhotoData(holder, moment, position);
                    break;
                default:
                    switch (momentType) {
                        case 22:
                        case 23:
                        case 24:
                        case 25:
                        case 26:
                        case 27:
                            break;
                        default:
                            holder.setTvContent(moment.getResource(), this.mContext.getString(R.string.no_support_tips));
                            dealMomentBackground(holder, moment);
                            break;
                    }
                    // 原逻辑无 break，默认分支继续走图片加载
                    setPhotoData(holder, moment, position);
                    break;
            }
        }
        if (holder.momentLike == null) {
            return;
        }
        String selfId = this.selfWatchId;
        boolean isSelfMoment = selfId != null && selfId.equals(moment.getWatchId());
        int likeTotal = moment.getLikeTotal() == null ? 0 : moment.getLikeTotal().intValue();
        if (isSelfMoment) {
            holder.momentLike.setLike(this.mContext, false);
        } else {
            holder.momentLike.setLike(this.mContext, !moment.isEnableLike());
        }
        holder.momentLike.setCount(likeTotal);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() != R.id.iv_icon) {
            return;
        }
        if (!ClickUtils.isFastClick()) {
            LogUtil.w(TAG, "click too fast");
            return;
        }
        if (com.xtc.log.util.TextUtils.isEmpty(this.watchId)) {
            LogUtil.w(TAG, "watchId is empty");
            return;
        }
        if (this.mIsSelf) {
            if (!this.isSupportPersonalCenter) {
                LogUtil.d(TAG, "onClick : 当前机型不支持个人中心");
                return;
            }
            Intent oldPersonalMainIntent = getOldPersonalMainIntent();
            if (oldPersonalMainIntent.resolveActivityInfo(this.mContext.getPackageManager(), 65536) != null) {
                LogUtil.i(TAG, "start 旧版个人中心主页");
                this.mContext.startActivity(oldPersonalMainIntent);
                return;
            }
            Intent newPersonalMainIntent = getNewPersonalMainIntent();
            if (newPersonalMainIntent.resolveActivityInfo(this.mContext.getPackageManager(), 65536) != null) {
                LogUtil.i(TAG, "start 新版个人中心主页");
                this.mContext.startActivity(newPersonalMainIntent);
                return;
            }
        }
        AccountInfoActivity.start(this.mContext, this.watchId, this.mName, this.mIsSelf);
    }

    private Intent getOldPersonalMainIntent() {
        Intent intent = new Intent();
        intent.setClassName(PersonalInfoConstant.PersonalMainIntent.SETTING_PACKAGE_NAME,
                PersonalInfoConstant.PersonalMainIntent.PERSONAL_ACTIVITY);
        intent.setFlags(NotificationFlag.NOTIFICATION_FLAG_CUSTOM);
        return intent;
    }

    private Intent getNewPersonalMainIntent() {
        Intent intent = new Intent();
        intent.setClassName("com.xtc.personalcenter", PersonalInfoConstant.PersonalMainIntent.PERSONAL_ACTIVITY_NEW);
        intent.setFlags(NotificationFlag.NOTIFICATION_FLAG_CUSTOM);
        return intent;
    }

    private void setMomentReminder(AbsViewHolder holder, DbMoment moment) {
        if (holder.momentReminderView == null) {
            return;
        }
        if (!com.xtc.log.util.TextUtils.isEmpty(moment.getReminderContent())) {
            holder.momentReminderView.setVisibility(View.VISIBLE);
            holder.momentReminderView.setReminderText(moment.getReminderContent());
            holder.momentReminderView.setReminderTextUrl(moment.getReminderUrl());
            return;
        }
        holder.momentReminderView.setVisibility(View.GONE);
    }

    protected void setMomentIcon(DbMoment moment, AbsViewHolder holder, boolean isSelf, String iconPath) {
        if (MomentTypeUtil.isOfficialType(moment.getType().intValue())) {
            if (com.xtc.log.util.TextUtils.isEmpty(moment.getIconPath())) {
                holder.setIvIcon(R.drawable.i11_genius_rabbit, isSelf);
            } else {
                holder.setIvIcon(moment.getIconPath(), isSelf);
            }
            return;
        }
        if (!this.mIsSelf) {
            if (this.contactManager == null) {
                this.contactManager = ContactManager.getInstance(this.mContext);
            }
            holder.setIvIcon(this.contactManager.getContactWithoutShortNumberByWatchIdSync(moment.getWatchId()), isSelf);
        } else {
            ContactBean contactBean = new ContactBean();
            contactBean.setContactServerId(moment.getWatchId());
            contactBean.setPhotoPath(this.mIconPath);
            holder.setIvIcon(contactBean, isSelf);
        }
    }

    protected void setMomentName(DbMoment moment, AbsViewHolder holder, boolean isSelf, String name) {
        if (MomentTypeUtil.isOfficialType(moment.getType().intValue())) {
            if (com.xtc.log.util.TextUtils.isEmpty(moment.getName())) {
                holder.setTvName(false, this.mContext.getString(R.string.rabbit));
            } else {
                holder.setTvName(false, moment.getName());
            }
            return;
        }
        holder.setTvName(isSelf, name);
    }
    private void dealMomentBackground(final AbsViewHolder holder, DbMoment moment) {
        String location = moment.getLocation();
        if (holder.llLbs != null) {
            holder.llLbs.setVisibility((com.xtc.log.util.TextUtils.isEmpty(location) || !moment.isLbsSwitch())
                    ? View.GONE : View.VISIBLE);
            holder.llLbs.setLocation(location);
            holder.llLbs.setOnLongClickListener(null);
            dealLbsClick(holder.llLbs, moment);
        }
        holder.setContentVisibility(true);
        if (holder.ivBanner == null) {
            return;
        }
        if (moment.getEmotionId() == 0) {
            holder.ivBanner.setVisibility(View.GONE);
        } else if (com.xtc.log.util.TextUtils.isEmpty(moment.getMomentBgPath())) {
            holder.ivBanner.setVisibility(View.GONE);
        } else {
            holder.ivBanner.setVisibility(View.VISIBLE);
            Glide.with(this.mContext).load(moment.getMomentBgPath()).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                    if (drawable instanceof GifDrawable) {
                        GifDrawable gifDrawable = (GifDrawable) drawable;
                        gifDrawable.setLoopCount(1);
                        gifDrawable.start();
                    }
                    holder.ivBanner.setImageDrawable(drawable);
                }
            });
        }
    }

    private void setLocationData(AbsViewHolder holder, DbMoment moment) {
        String location = moment.getLocation();
        if (holder.llLbs != null) {
            holder.llLbs.setVisibility(View.GONE);
            holder.llLbs.setOnLongClickListener(null);
        }
        if (com.xtc.log.util.TextUtils.isEmpty(location) || !moment.isLbsSwitch()) {
            holder.setContentVisibility(true);
            holder.setTvContent(R.drawable.location, moment.getContent());
            holder.ivBanner.setVisibility(View.VISIBLE);
            holder.ivBanner.setImageResource(R.drawable.circle_location_illustration);
        } else if (holder.llLbs != null) {
            holder.setContentVisibility(false);
            holder.llLbs.setVisibility(View.VISIBLE);
            holder.llLbs.setLocation(location);
            dealLbsClick(holder.llLbs, moment);
            holder.ivBanner.setVisibility(View.GONE);
        }
        holder.setContentOnLongClickListener(moment, new OnContentOnLongClickListener() {
            @Override
            public void deleteItem(DbMoment targetMoment) {
                LogUtil.d(TAG, "deleteItem:momentBean" + targetMoment);
                if (listener != null) {
                    listener.onDeleteItem(targetMoment);
                }
            }
        });
    }

    private void setTextData(AbsViewHolder holder, DbMoment moment) {
        holder.setTvContent(moment.getResource(), moment.getContent());
        dealMomentBackground(holder, moment);
        holder.setContentOnLongClickListener(moment, new OnContentOnLongClickListener() {
            @Override
            public void deleteItem(DbMoment targetMoment) {
                LogUtil.d(TAG, "deleteItem:momentBean" + targetMoment);
                if (listener != null) {
                    listener.onDeleteItem(targetMoment);
                }
            }
        });
    }

    private void setPhotoData(AbsViewHolder holder, final DbMoment moment, int position) {
        if (holder == null) {
            LogUtil.d(TAG, "setPhotoData: holder");
            notifyDataSetChanged();
            return;
        }
        if (holder.ivContent != null) {
            holder.ivContent.setTag(R.id.chat_msg_item_photo_iv, moment.getResource());
            LogUtil.d(TAG, "setPhotoData()重用了：" + holder.ivContent.getTag(R.id.chat_msg_item_photo_iv)
                    + "\n" + moment.getResource() + "\n" + moment.getContent() + "  \n  PPPPPPPPPPPPP ***************** " + position);
        }
        holder.loadImage(this.mContext, moment, holder);
        dealMomentBackground(holder, moment);
        holder.setContentOnClickListener(moment, new OnContentOnClickListener() {
            @Override
            public void previewPhoto(PhotoMsg photoMsg) {
                previewPhotoLayout(photoMsg, moment.getMomentId(), moment.getWatchId());
            }

            @Override
            public void previewPhoto(String url) {
                previewPhotoLayout(url, moment.getMomentId());
            }

            @Override
            public void preVideoView(String videoData) {
                if (TextUtils.isEmpty(videoData)) {
                    return;
                }
                Intent intent = new Intent(mContext, PlayVideoActivity.class);
                intent.addFlags(NotificationFlag.NOTIFICATION_FLAG_CUSTOM);
                intent.putExtra(PlayVideoActivity.VIDEO_MSG_DATA, videoData);
                mContext.startActivity(intent);
            }

            @Override
            public void previewLivePhoto(LivePhotoMsg livePhotoMsg) {
                previewLivePhotoLayout(livePhotoMsg);
            }

            @Override
            public void previewH5(String url) {
                if (TextUtils.isEmpty(url)) {
                    return;
                }
                StartWebUtils.startH5Activity(mContext, url);
            }
        });
        holder.setContentOnLongClickListener(moment, new OnContentOnLongClickListener() {
            @Override
            public void deleteItem(DbMoment targetMoment) {
                LogUtil.d(TAG, "deleteItem:momentBean" + targetMoment);
                if (listener != null) {
                    listener.onDeleteItem(targetMoment);
                }
            }
        });
        LogUtil.d(TAG, "发图片:momentBean:" + moment.toString() + ";position:" + position);
    }

    @Override
    public int getItemViewType(int position) {
        if (this.mHeaderView != null && position == 0) {
            return ITEM_TYPE_HEADER;
        }
        if (this.mFooterView != null && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        }
        if (this.mEmptyView == null || this.mData.size() != 0) {
            return getMomentType(this.mData.get(getDataItemPosition(position)));
        }
        return ITEM_TYPE_EMPTY;
    }

    private static int getMomentType(DbMoment moment) {
        LogUtil.i(TAG, "getMomentType" + moment.getType());
        int type = moment.getType().intValue();
        if (type != 11) {
            switch (type) {
                case 4:
                case 5:
                    return ITEM_TYPE_PHOTO;
                case 6:
                    return ITEM_TYPE_VIDEO;
                case 7:
                    return ITEM_TYPE_SHARE_TEXT;
                case 8:
                    return ITEM_TYPE_SHARE_IMAGE;
                case 9:
                    return ITEM_TYPE_SHARE_APP;
                default:
                    switch (type) {
                        case 22:
                            return ITEM_TYPE_LIVE_PHOTO;
                        case 23:
                            return ITEM_TYPE_SHARE_LIVE_PHOTO;
                        case 24:
                            return ITEM_TYPE_SHARE_VIDEO;
                        case 25:
                            return ITEM_TYPE_SHARE_H5;
                        case 26:
                            return 28;
                        case 27:
                            return 29;
                        default:
                            return ITEM_TYPE_NORMAL;
                    }
            }
        }
        return ITEM_TYPE_PHOTO;
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

    public void setHeaderView(View view) {
        this.mHeaderView = view;
        notifyItemInserted(0);
    }

    public void setFooterView(View view) {
        this.mFooterView = view;
        notifyItemInserted(getItemCount() - 1);
    }

    public void setEmptyView(View view) {
        this.mEmptyView = view;
        notifyDataSetChanged();
    }

    public List<DbMoment> getData() {
        return this.mData;
    }

    public void refreshData(DbMoment moment) {
        if (moment == null || this.mData == null || this.mData.isEmpty()) {
            return;
        }
        int index = 0;
        while (index < this.mData.size() && (this.mData.get(index) == null
                || this.mData.get(index).getMomentId() == null
                || !this.mData.get(index).getMomentId().equals(moment.getMomentId()))) {
            index++;
        }
        if (index < this.mData.size()) {
            this.mData.set(index, moment);
        }
    }

    public void addData(List<DbMoment> moments) {
        if (moments == null || moments.isEmpty()) {
            return;
        }
        if (this.mData == null) {
            this.mData = new ArrayList<>();
        }
        int startPosition = this.mData.size();
        int addedCount = 0;
        boolean changed = false;
        for (DbMoment moment : moments) {
            if (moment != null && !this.mData.contains(moment)) {
                this.mData.add(moment);
                addedCount++;
                changed = true;
            }
        }
        if (changed) {
            this.verticallyLinearLayoutManager.setScrollEnabled(false);
            if (this.mHeaderView != null) {
                startPosition++;
            }
            notifyItemChanged(startPosition, Integer.valueOf(addedCount));
            this.verticallyLinearLayoutManager.setScrollEnabled(true);
        }
    }

    public void refreshPraiseRecordMap(Map<String, List<DbLikeMessage>> map) {
        if (this.praiseRecordMap == null) {
            this.praiseRecordMap = map;
            return;
        }
        for (Map.Entry<String, List<DbLikeMessage>> entry : map.entrySet()) {
            this.praiseRecordMap.put(entry.getKey(), entry.getValue());
        }
    }

    public void notifyItemByMomentId(String momentId) {
        if (CollectionUtil.isEmpty(this.mData) || this.mData.size() <= 0
                || com.xtc.log.util.TextUtils.isEmpty(momentId)) {
            return;
        }
        int index = -1;
        for (int i = 0; i < this.mData.size(); i++) {
            if (this.mData.get(i).getMomentId().equals(momentId)) {
                index = i;
                break;
            }
        }
        notifyItemChanged(index + 1, AbsInteractionAdapter.PART_REFRESH_PRAISE);
    }

    public void refreshPraiseRecordMapCancel(String selfWatchId) {
        Map<String, List<DbLikeMessage>> map = this.praiseRecordMap;
        if (map == null) {
            LogUtil.i(TAG, "refreshPraiseRecordMapCancel: praiseRecordMap == null");
            return;
        }
        for (Map.Entry<String, List<DbLikeMessage>> entry : map.entrySet()) {
            List<DbLikeMessage> likeMessages = entry.getValue();
            for (int i = 0; i < likeMessages.size(); i++) {
                if (likeMessages.get(i).getWatchId().equals(selfWatchId)) {
                    likeMessages.remove(i);
                    break;
                }
            }
            this.praiseRecordMap.put(entry.getKey(), likeMessages);
        }
    }

    @Override
    public void setOnLikeClickListener(AbsInteractionAdapter.OnLikeClickListener listener) {
        this.onLikeClickListener = listener;
    }

    public DbMoment getLastData() {
        if (CollectionUtil.isEmpty(this.mData)) {
            return null;
        }
        return this.mData.get(this.mData.size() - 1);
    }
    public void previewPhotoLayout(PhotoMsg photoMsg, String momentId, String watchId) {
        LogUtil.d(TAG, "previewPhotoLayout#photoMsg:" + photoMsg);
        if (SystemUtil.isFastDoubleClick()) {
            LogUtil.d(TAG, "isFastDoubleClick");
            return;
        }
        if (photoMsg == null) {
            return;
        }
        Intent intent = new Intent(this.mContext, PhotoPreviewActivity.class);
        intent.putExtra(PhotoPreviewActivity.EXTAR_LOCAL_PATH, photoMsg.getLocalPath());
        intent.putExtra(PhotoPreviewActivity.EXTRA_DOWNLOAD_URL, getPhotoDownloadUrl(photoMsg));
        intent.putExtra(PhotoPreviewActivity.EXTRA_MSG_ID, momentId);
        intent.putExtra("watchId", watchId);
        intent.putExtra(PhotoPreviewActivity.TRACK_MD5_VALUE, photoMsg.getTrackMd5Value());
        this.mContext.startActivity(intent);
    }

    public void previewPhotoLayout(String url, String momentId) {
        LogUtil.d(TAG, "previewPhotoLayout#url:" + url);
        if (SystemUtil.isFastDoubleClick()) {
            LogUtil.d(TAG, "isFastDoubleClick");
            return;
        }
        if (url == null || com.xtc.log.util.TextUtils.isEmpty(url)) {
            return;
        }
        Intent intent = new Intent(this.mContext, PhotoPreviewActivity.class);
        intent.putExtra(PhotoPreviewActivity.EXTRA_DOWNLOAD_URL, url);
        intent.putExtra(PhotoPreviewActivity.EXTRA_MSG_ID, momentId);
        this.mContext.startActivity(intent);
    }

    public void previewLivePhotoLayout(LivePhotoMsg livePhotoMsg) {
        if (livePhotoMsg == null || livePhotoMsg.getVideoMsg() == null) {
            return;
        }
        VideoMsg videoMsg = livePhotoMsg.getVideoMsg();
        String localPath = livePhotoMsg.getLocalPath();
        boolean videoHasDownload = true;
        String photoDownloadUrl;
        if (TextUtils.isEmpty(localPath) || !FileUtils.exists(localPath)) {
            SmallPicSouce smallPic = livePhotoMsg.getSmallPic();
            photoDownloadUrl = getPhotoDownloadUrl(livePhotoMsg);
            if (!(smallPic != null && System.currentTimeMillis() < smallPic.getUrlDeadline())) {
                Context context = this.mContext;
                ToastUtil.showShortCover(context, context.getString(R.string.image_invalid));
                return;
            }
        } else {
            photoDownloadUrl = localPath;
        }
        Intent intent = new Intent(this.mContext, PlayLivePhotoActivity.class);
        String localVideoPath = videoMsg.getLocalVideoPath();
        if (TextUtils.isEmpty(localVideoPath) || !FileUtils.exists(localVideoPath)) {
            CloudFileResource transfer = videoMsg.getTransfer();
            if (transfer == null) {
                LogUtil.d(TAG, "CloudFileResource null");
                return;
            }
            localVideoPath = FileManager.getLivePhotoCachePath() + transfer.getKey();
            if (!FileUtils.exists(localVideoPath)) {
                String downloadUrl = videoMsg.getTransfer().getDownloadUrl();
                if (!(System.currentTimeMillis() < videoMsg.getTransfer().getUrlDeadline())) {
                    Context context = this.mContext;
                    ToastUtil.showShortCover(context, context.getString(R.string.image_invalid));
                    return;
                }
                intent.putExtra(PlayLivePhotoActivity.VIDEO_OUT_PATH, localVideoPath);
                localVideoPath = downloadUrl;
                videoHasDownload = false;
            }
        }
        intent.putExtra("video_has_download", videoHasDownload);
        intent.putExtra("video_thumnail_path", photoDownloadUrl);
        intent.putExtra("video_file_path", localVideoPath);
        this.mContext.startActivity(intent);
    }

    public String getPhotoDownloadUrl(PhotoMsg photoMsg) {
        SmallPicSouce smallPic = photoMsg.getSmallPic();
        CloudFileResource source = photoMsg.getSource();
        if (smallPic != null) {
            return smallPic.getDownloadUrl();
        }
        if (source != null) {
            return source.getDownloadUrl();
        }
        return null;
    }

    private void dislplayNetPhoto(final Context context, final String key, final ImageView imageView,
            final boolean roundedCorner, final DbMoment moment, final AbsViewHolder holder, final String content) {
        int type = moment.getType().intValue();
        if (type == 6 || type == 24 || type == 27) {
            VideoMsg videoMsg = JSONUtil.fromJSON(content, VideoMsg.class);
            CloudFileResource icon = videoMsg.getIcon();
            CloudFileResource source = videoMsg.getSource();
            long urlDeadline = Long.MAX_VALUE;
            String downloadUrl = null;
            String cloudKey = null;
            if (icon != null) {
                urlDeadline = icon.getUrlDeadline();
                downloadUrl = icon.getDownloadUrl();
                cloudKey = icon.getKey();
            } else if (source != null) {
                urlDeadline = source.getUrlDeadline();
                downloadUrl = source.getDownloadUrl();
                cloudKey = source.getKey();
            }
            final String finalDownloadUrl = downloadUrl;
            VideoKeyOrToken videoKeyOrToken = JSONUtil.fromJSON(moment.getResource(), VideoKeyOrToken.class);
            LogUtil.i(TAG, "dislplayNetPhoto " + moment + "\n videoKeyOrToken :" + videoKeyOrToken);
            LogUtil.d(TAG, "videoMsg = " + videoMsg.toString());
            final String picKey = (!com.xtc.log.util.TextUtils.isEmpty(cloudKey) || videoKeyOrToken == null)
                    ? key : videoKeyOrToken.getPicKey();
            if (System.currentTimeMillis() > urlDeadline) {
                LogUtil.i(TAG, "重新获取下载地址");
                getServerDownloadUrl(context, picKey, imageView, roundedCorner, moment, videoMsg, holder);
                return;
            }
            if (com.xtc.log.util.TextUtils.isEmpty(finalDownloadUrl)) {
                if (com.xtc.log.util.TextUtils.isEmpty(videoMsg.getSource().getDownloadUrl())) {
                    getServerDownloadUrl(context, picKey, imageView, roundedCorner, moment, videoMsg, holder);
                } else {
                    loadNoFailureUrl(context, picKey, imageView, roundedCorner, moment,
                            videoMsg.getSource().getDownloadUrl(), holder);
                }
                return;
            }
            loadNoFailureUrl(context, picKey, imageView, roundedCorner, moment, finalDownloadUrl, holder);
            return;
        }
        pullNewUrl(context, key, imageView, roundedCorner, moment, holder, Long.MAX_VALUE, null, null);
    }

    private void pullNewUrl(final Context context, final String key, final ImageView imageView,
            final boolean roundedCorner, final DbMoment moment, final AbsViewHolder holder, long deadline,
            String fallbackDownloadUrl, String fallbackKey) {
        int retryCount = moment.getRetryCount();
        LogUtil.d(TAG, "pullNewUrl: " + moment.getMomentId());
        if (retryCount > 2) {
            LogUtil.d(TAG, "pullNewUrl: 已经重试过两次了 1 不做重试操作！" + moment.getMomentId());
            return;
        }
        moment.setRetryCount(retryCount + 1);
        PhotoMsg photoMsg = JSONUtil.fromJSON(moment.getContent(), PhotoMsg.class);
        LogUtil.d(TAG, "dislplayNetPhoto: else " + photoMsg);
        if (photoMsg == null) {
            return;
        }
        SmallPicSouce smallPic = photoMsg.getSmallPic();
        CloudFileResource source = photoMsg.getSource();
        long urlDeadline;
        final String downloadUrl;
        String cloudKey;
        if (smallPic != null) {
            urlDeadline = smallPic.getUrlDeadline();
            downloadUrl = smallPic.getDownloadUrl();
            cloudKey = smallPic.getKey();
        } else if (source != null) {
            urlDeadline = source.getUrlDeadline();
            downloadUrl = source.getDownloadUrl();
            cloudKey = source.getKey();
        } else {
            urlDeadline = deadline;
            downloadUrl = fallbackDownloadUrl;
            cloudKey = fallbackKey;
        }
        if (System.currentTimeMillis() > urlDeadline) {
            new MomentPhotoServeImpl(context).getDownloadUrl(new FileUrlParam(cloudKey, photoMsg.getType()), photoMsg, moment)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new HttpSubscriber<String>() {
                        @Override
                        public void onHttpError(Throwable throwable) {
                        }

                        @Override
                        public void onNext(String url) {
                            ShareAdapter.this.loadNoFailureUrl(context, key, imageView, roundedCorner, moment, url, holder);
                        }
                    });
            return;
        }
        HandlerUtil.runOnUIThreadNoCheck(new Runnable() {
            @Override
            public void run() {
                ShareAdapter.this.loadNoFailureUrl(context, key, imageView, roundedCorner, moment, downloadUrl, holder);
            }
        });
    }

    private VideoMsg convertToVideoMsg(String content) {
        return JSONUtil.fromJSON(content, VideoMsg.class);
    }

    private void getServerDownloadUrl(final Context context, String key, final ImageView imageView,
            final boolean roundedCorner, final DbMoment moment, VideoMsg videoMsg, final AbsViewHolder holder) {
        int retryCount = moment.getRetryCount();
        LogUtil.d(TAG, "pullNewUrl: " + moment.getMomentId());
        if (retryCount > 2) {
            LogUtil.d(TAG, "pullNewUrl: 已经重试过两次了 不做重试操作！" + moment.getMomentId());
            return;
        }
        moment.setRetryCount(retryCount + 1);
        MomentPhotoServeImpl photoServe = new MomentPhotoServeImpl(context);
        ArrayList<String> keys = new ArrayList<>();
        CloudFileResource icon = videoMsg.getIcon();
        CloudFileResource source = videoMsg.getSource();
        if (icon != null) {
            keys.add(icon.getKey());
        }
        if (source != null) {
            keys.add(source.getKey());
        }
        photoServe.getDownloadBatchUrl(new FileBatchUrlParam(keys), moment, videoMsg)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpSubscriber<DownloadUrlVo>() {
                    @Override
                    public void onHttpError(Throwable throwable) {
                    }

                    @Override
                    public void onNext(DownloadUrlVo downloadUrlVo) {
                        LogUtil.i(TAG, "onNext onNext ： " + downloadUrlVo);
                        List<CloudFileResource> urls = downloadUrlVo.getUrls();
                        for (int i = 0; i < urls.size(); i++) {
                            CloudFileResource resource = urls.get(i);
                            if (resource != null && Utils.isIconKey(resource.getKey())) {
                                LogUtil.i(TAG, "glideWithInto  getServerDownloadUrl UIR ");
                                glideWithInto(context, imageView, resource.getDownloadUrl(), roundedCorner, moment, holder);
                                return;
                            }
                        }
                    }
                });
    }

    private void loadNoFailureUrl(Context context, String key, ImageView imageView, boolean roundedCorner,
            DbMoment moment, String downloadUrl, AbsViewHolder holder) {
        if (TextUtils.isEmpty(downloadUrl)) {
            return;
        }
        LogUtil.i(TAG, "如果未失效直接显示图片" + imageView.getTag(R.id.chat_msg_item_photo_iv));
        LogUtil.i(TAG, "glideWithInto loadNoFailureUrl  downloadUrl " + downloadUrl);
        if (key != null && key.equals(imageView.getTag(R.id.chat_msg_item_photo_iv))) {
            LogUtil.d(TAG, "loadNoFailureUrl: -- downloadUrl=" + downloadUrl);
            glideWithInto(context, imageView, downloadUrl, roundedCorner, moment, holder);
            return;
        }
        LogUtil.d(TAG, "控件被复用了1，不加载图片");
    }
    private void loadImageWithKey(final Context context, final ImageView imageView, String key, final String type,
            final DbMoment moment, final boolean roundedCorner, final AbsViewHolder holder) {
        VideoKeyOrToken videoKeyOrToken;
        final String picKey = ((moment.getType().intValue() == 6 || moment.getType().intValue() == 24
                || moment.getType().intValue() == 27)
                && (videoKeyOrToken = JSONUtil.fromJSON(moment.getResource(), VideoKeyOrToken.class)) != null)
                ? videoKeyOrToken.getPicKey() : key;
        new MomentPhotoServeImpl(context).getDownloadUrl(new FileUrlParam(picKey, type), moment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpSubscriber<String>() {
                    @Override
                    public void onHttpError(Throwable throwable) {
                        LogUtil.d(TAG, "loadImageWithKey#e" + throwable);
                        if ("1".equals(type)) {
                            loadImageWithKey(context, imageView, picKey, "2", moment, roundedCorner, holder);
                        }
                    }

                    @Override
                    public void onNext(String url) {
                        LogUtil.e(TAG, "onNext: getKey " + url);
                        if (TextUtils.isEmpty(url)) {
                            return;
                        }
                        LogUtil.i(TAG, "loadNoFailureUrltag" + imageView.getTag(R.id.chat_msg_item_photo_iv)
                                + "momentBean.getResource()  " + moment.getResource());
                        LogUtil.i(TAG, "glideWithInto loadImageWithKey  RUI 2" + url);
                        if (moment.getResource().equals(imageView.getTag(R.id.chat_msg_item_photo_iv))) {
                            glideWithInto(context, imageView, url, roundedCorner, moment, holder);
                        } else {
                            LogUtil.d(TAG, "控件被复用了，不加载图片");
                        }
                    }
                });
    }

    private boolean isContextAvailable(Context context) {
        if (context == null) {
            return false;
        }
        if (!(context instanceof Activity)) {
            return true;
        }
        Activity activity = (Activity) context;
        return !(activity.isDestroyed() || activity.isFinishing());
    }

    public void glideWithInto(final Context context, final ImageView imageView, String url, boolean roundedCorner,
            final DbMoment moment, final AbsViewHolder holder) {
        if (!isContextAvailable(context)) {
            LogUtil.i(TAG, "glideWithInto context is unAvailable");
            return;
        }
        LogUtil.i(TAG, "glideWithInto URI " + url + "   " + context);
        final String resource = moment.getResource();
        imageView.setImageDrawable(null);
        RequestOptions options = new RequestOptions().error(R.drawable.pi_friends_default)
                .override(imageView.getWidth(), imageView.getHeight())
                .dontAnimate()
                .signature(new ObjectKey(url));
        final RequestOptions playLogoOptions = new RequestOptions().placeholder(R.drawable.pi_friends_default)
                .override(imageView.getWidth(), imageView.getHeight())
                .dontAnimate()
                .signature(new ObjectKey(Integer.valueOf(R.drawable.pi_friends_default)));
        ShareVideoViewHolder videoHolder = null;
        if ((moment.getType().intValue() == 6 || moment.getType().intValue() == 24
                || moment.getType().intValue() == 27) && (holder instanceof ShareVideoViewHolder)) {
            videoHolder = (ShareVideoViewHolder) holder;
            videoHolder.videoPlayLogo.setVisibility(View.VISIBLE);
        }
        final ShareVideoViewHolder playLogoHolder = videoHolder;
        if (roundedCorner) {
            options = options.transform((Transformation<Bitmap>) new MultiTransformation(
                    new CenterCrop(), new RoundedCorners(DimenUtil.dp2px(context, 4.0f))));
        }
        if (8 == moment.getType().intValue()) {
            LogUtil.d(TAG, "share image load");
            ShareImageMoment shareImageMoment = JSONUtil.fromJSON(moment.getContent(), ShareImageMoment.class);
            if (shareImageMoment != null && shareImageMoment.getDialogBitmapArgs() != null
                    && shareImageMoment.getMessageBitmapArgs() != null) {
                imageView.getLayoutParams().height = shareImageMoment.getMessageBitmapArgs().getHeight();
                imageView.getLayoutParams().width = shareImageMoment.getMessageBitmapArgs().getWidth();
                DialogBitmapArgs dialogBitmapArgs = shareImageMoment.getDialogBitmapArgs();
                Glide.with(context).load(url)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onResourceReady(Drawable drawable, Object model, Target<Drawable> target,
                                    DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onLoadFailed(GlideException exception, Object model, Target<Drawable> target,
                                    boolean isFirstResource) {
                                HandlerUtil.runOnUIThreadNoCheck(new Runnable() {
                                    @Override
                                    public void run() {
                                        pullNewUrl(mContext, moment.getResource(), imageView, false, moment, holder,
                                                Long.MAX_VALUE, null, null);
                                    }
                                });
                                return false;
                            }
                        })
                        .apply(new RequestOptions().error(R.drawable.ic_selfie_album_default_custom)
                                .override(imageView.getWidth(), imageView.getHeight())
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .signature(new ObjectKey(url))
                                .transform((Transformation<Bitmap>) new CropTransform(dialogBitmapArgs.getCropWidth(),
                                        dialogBitmapArgs.getCropHeight(), dialogBitmapArgs.getCutStart(),
                                        dialogBitmapArgs.getCutTop())))
                        .into(new DrawableImageViewTarget(imageView) {
                            @Override
                            public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                                ImageView view = getView();
                                if (view == null || resource == null) {
                                    LogUtil.e(TAG, "glideWithInfo DialogBitmapArgs: set image error. view: " + view
                                            + ", tag: " + resource);
                                    return;
                                }
                                if (resource.equals(view.getTag(R.id.chat_msg_item_photo_iv))) {
                                    super.onResourceReady(drawable, transition);
                                    LogUtil.i(TAG, "glideWithInfo DialogBitmapArgs complete!!! tag: " + resource);
                                    return;
                                }
                                LogUtil.w(TAG, "glideWithInfo DialogBitmapArgs onResourceReady: view is recycled. tag: "
                                        + resource + ", iv.tag: " + view.getTag());
                            }
                        });
                return;
            }
        }
        Glide.with(context).load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException exception, Object model, Target<Drawable> target,
                            boolean isFirstResource) {
                        if (playLogoHolder == null || playLogoHolder.videoPlayLogo == null) {
                            if (mContext != null) {
                                imageView.setImageDrawable(mContext.getResources()
                                        .getDrawable(R.drawable.pi_friends_default));
                            }
                            HandlerUtil.runOnUIThreadNoCheck(new Runnable() {
                                @Override
                                public void run() {
                                    pullNewUrl(mContext, moment.getResource(), imageView, false, moment, holder,
                                            Long.MAX_VALUE, null, null);
                                }
                            });
                            return false;
                        }
                        playLogoHolder.videoPlayLogo.setVisibility(View.GONE);
                        playLogoHolder.videoPlayLogo.setVisibility(View.INVISIBLE);
                        getServerDownloadUrl(mContext, moment.getResource(), imageView, true, moment,
                                convertToVideoMsg(moment.getContent()), holder);
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable drawable, Object model, Target<Drawable> target,
                            DataSource dataSource, boolean isFirstResource) {
                        if (playLogoHolder != null && playLogoHolder.videoPlayLogo != null) {
                            playLogoHolder.videoPlayLogo.setVisibility(View.VISIBLE);
                        }
                        return false;
                    }
                })
                .apply(options)
                .into(new DrawableImageViewTarget(imageView) {
                    @Override
                    public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                        ImageView view = getView();
                        if (playLogoHolder != null && playLogoHolder.videoPlayLogo != null) {
                            playLogoHolder.videoPlayLogo.setVisibility(View.VISIBLE);
                            Glide.with(context).load(R.drawable.ic_friends_play).apply(playLogoOptions)
                                    .into(playLogoHolder.videoPlayLogo);
                        }
                        imageView.setImageDrawable(drawable);
                        if (view == null || resource == null) {
                            LogUtil.e(TAG, "onResourceReady: set image error. view: " + view + ", tag: " + resource);
                            return;
                        }
                        if (resource.equals(view.getTag(R.id.chat_msg_item_photo_iv))) {
                            LogUtil.i(TAG, "load local image complete!!! tag: " + resource);
                            return;
                        }
                        LogUtil.w(TAG, "onResourceReady: view is recycled. tag: " + resource + ", iv.tag: " + view.getTag());
                    }
                });
    }

    private void configAndLoadAdvantisePhoto(Context context, final DbMoment moment, final ImageView imageView) {
        Glide.with(context).load(moment.getResource())
                .apply(new RequestOptions().error(R.drawable.pi_friends_default)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .override(imageView.getWidth(), imageView.getHeight())
                        .transform((Transformation<Bitmap>) new RoundedCorners(DimenUtil.dp2px(context, 4.0f)))
                        .dontAnimate()
                        .signature(new ObjectKey(String.valueOf(Math.random()))))
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                        loadAdvantagePhoto(moment, drawable, imageView);
                    }
                });
    }

    private void loadAdvantagePhoto(DbMoment moment, Drawable drawable, ImageView imageView) {
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        int width = imageView.getWidth();
        int height = imageView.getHeight();
        int top = 0;
        int left = 0;
        if (moment.getScaleType() == 2) {
            if (intrinsicWidth * height > width * intrinsicHeight) {
                float scaledHeight = width * (intrinsicHeight / height);
                left = (int) ((intrinsicWidth - scaledHeight) * 0.5f);
                intrinsicWidth = ((int) scaledHeight) + left;
            } else {
                float scaledWidth = height * (intrinsicWidth / width);
                top = (int) ((intrinsicHeight - scaledWidth) * 0.5f);
                intrinsicHeight = ((int) scaledWidth) + top;
                left = 0;
            }
            LogUtil.d(TAG, "left:" + left + ";top:" + top + ";right:" + intrinsicWidth + ";bottom:" + intrinsicHeight);
            imageView.setImageBitmap(Bitmap.createBitmap(((BitmapDrawable) drawable).getBitmap(), left, top,
                    intrinsicWidth - left, intrinsicHeight - top));
            return;
        }
        if (moment.getScaleType() == 0) {
            if (intrinsicWidth * height > width * intrinsicHeight) {
                intrinsicWidth = (int) (width * (intrinsicHeight / height));
            } else {
                intrinsicHeight = (int) (height * (intrinsicWidth / width));
            }
            LogUtil.d(TAG, "left:0;top:0;right:" + intrinsicWidth + ";bottom:" + intrinsicHeight);
            imageView.setImageBitmap(Bitmap.createBitmap(((BitmapDrawable) drawable).getBitmap(), 0, 0,
                    intrinsicWidth, intrinsicHeight));
            return;
        }
        if (moment.getScaleType() == 1) {
            if (intrinsicWidth * height > width * intrinsicHeight) {
                left = (int) (intrinsicWidth - (width * (intrinsicHeight / height)));
            } else {
                top = (int) (intrinsicHeight - (height * (intrinsicWidth / width)));
                left = 0;
            }
            LogUtil.d(TAG, "left:" + left + ";top:" + top + ";right:" + intrinsicWidth + ";bottom:" + intrinsicHeight);
            imageView.setImageBitmap(Bitmap.createBitmap(((BitmapDrawable) drawable).getBitmap(), left, top,
                    intrinsicWidth - left, intrinsicHeight - top));
            return;
        }
        imageView.setImageDrawable(drawable);
    }

    private void getPhotoUrl(final Context context, String key, final String type, final DbMoment moment,
            final OnContentOnClickListener listener) {
        LogUtil.d(TAG, "getPhotoUrl#key:" + key + ";type:" + type);
        new MomentPhotoServeImpl(context).getDownloadUrl(new FileUrlParam(key, type), moment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpSubscriber<String>() {
                    @Override
                    public void onHttpError(Throwable throwable) {
                        super.onHttpError(throwable);
                        LogUtil.d(TAG, "getPhotoUrl#e:" + throwable);
                        if ("1".equals(type)) {
                            getPhotoUrl(context, "2", moment.getResource(), moment, listener);
                        }
                    }

                    @Override
                    public void onNext(String url) {
                        super.onNext(url);
                        LogUtil.d(TAG, "getPhotoUrl#url:" + url);
                        if (listener != null) {
                            listener.previewPhoto(url);
                        }
                    }
                });
    }
    /** 普通图片动态的 ViewHolder。 */
    public class ShareInnerPhotoViewHolder extends AbsViewHolder {
        public ShareInnerPhotoViewHolder(View view) {
            super(view);
            this.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
            this.tvName = (TextView) view.findViewById(R.id.tv_name);
            this.tvTime = (TextView) view.findViewById(R.id.tv_time);
            this.llLbs = (LbsLayout) view.findViewById(R.id.ll_lbs);
            this.ivContent = (ImageView) view.findViewById(R.id.chat_msg_item_photo_iv);
            this.ivContent.setImageResource(R.drawable.pi_friends_default);
            this.tvLikes = (TextView) view.findViewById(R.id.tv_likes);
            this.ivBanner = (ImageView) view.findViewById(R.id.iv_banner);
            this.momentCommentView = (MainMomentCommentView) view.findViewById(R.id.moment_comment);
            this.commentRecyclerView = (RecyclerView) view.findViewById(R.id.moment_comment_rec);
            this.commentRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            this.ivShareReportIcon = (ImageView) view.findViewById(R.id.iv_share_account_report);
            if (this.tvLikes != null) {
                this.tvLikes.setHighlightColor(0);
            }
            this.ivMomentRange = (ImageView) view.findViewById(R.id.iv_moment_range);
            this.ivMomentComment = (CommentIconView) view.findViewById(R.id.iv_moment_comment);
            this.momentLike = (MomentLikeView) view.findViewById(R.id.moment_like);
            this.momentReminderView = (MomentReminderView) view.findViewById(R.id.moment_reminder);
        }

        @Override
        public void setContentOnClickListener(final DbMoment moment, final OnContentOnClickListener listener) {
            this.ivContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LogUtil.d(TAG, "setContentOnClickListener#onClick#momentBean:" + moment);
                    String content = moment.getContent();
                    if (content != null && !com.xtc.log.util.TextUtils.isEmpty(content)) {
                        PhotoMsg photoMsg = JSONUtil.fromJSON(content, PhotoMsg.class);
                        if (listener != null) {
                            if (moment.getType().intValue() == 6 || moment.getType().intValue() == 24
                                    || moment.getType().intValue() == 27) {
                                listener.preVideoView(JSONUtil.toJSON(moment));
                            } else {
                                listener.previewPhoto(photoMsg);
                            }
                        }
                        return;
                    }
                    getPhotoUrl(mContext, "1", moment.getResource(), moment, listener);
                }
            });
        }

        @Override
        public void setContentOnLongClickListener(DbMoment moment, OnContentOnLongClickListener listener) {
            onLongClick(this.ivContent, moment, listener);
        }

        @Override
        public void loadImage(Context context, DbMoment moment, AbsViewHolder holder) {
            if (this.ivContent == null) {
                LogUtil.d(TAG, "mContent == null");
                return;
            }
            boolean roundedCorner = moment.getType().intValue() != 9;
            if (11 == moment.getType().intValue()) {
                if (com.xtc.log.util.TextUtils.isEmpty(moment.getResource())) {
                    return;
                }
                configAndLoadAdvantisePhoto(mContext, moment, this.ivContent);
                return;
            }
            LogUtil.d(TAG, "loadImage#momentBean:" + moment);
            String content = moment.getContent();
            String resource = moment.getResource();
            if (content != null && !com.xtc.log.util.TextUtils.isEmpty(content) && content.contains("source")) {
                PhotoMsg photoMsg = JSONUtil.fromJSON(content, PhotoMsg.class);
                if (photoMsg == null) {
                    LogUtil.d(TAG, "photoMsg == null");
                    loadImageWithKey(mContext, this.ivContent, resource, "1", moment, roundedCorner, holder);
                    return;
                }
                LogUtil.d(TAG, "PhotoMsg = " + photoMsg);
                if (!TextUtils.isEmpty(photoMsg.getLocalPath())) {
                    LogUtil.d(TAG, "photoMsg.getLocalPath():" + photoMsg.getLocalPath());
                    if (!new File(photoMsg.getLocalPath()).exists()) {
                        LogUtil.d(TAG, "!photoFile.exists()");
                        dislplayNetPhoto(context, resource, this.ivContent, roundedCorner, moment, holder, content);
                        return;
                    }
                    LogUtil.d(TAG, "load local image!!");
                    loadDiskPhoto(photoMsg, roundedCorner, moment, holder);
                    LogUtil.d(TAG, "load local image complete!!!");
                    return;
                }
                LogUtil.d(TAG, "photoMsg.getLocalPath()==empty");
                dislplayNetPhoto(context, resource, this.ivContent, roundedCorner, moment, holder, content);
                return;
            }
            loadImageWithKey(mContext, this.ivContent, resource, "1", moment, roundedCorner, holder);
        }

        void loadDiskPhoto(PhotoMsg photoMsg, boolean roundedCorner, final DbMoment moment,
                final RecyclerView.ViewHolder holder) {
            String resource = moment.getResource();
            this.ivContent.setImageDrawable(null);
            RequestOptions options = new RequestOptions().error(R.drawable.ic_selfie_album_default)
                    .placeholder(R.drawable.ic_selfie_album_default)
                    .override(this.ivContent.getWidth(), this.ivContent.getHeight())
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .signature(new ObjectKey(photoMsg.getLocalPath()));
            if (roundedCorner) {
                options = options.transform((Transformation<Bitmap>) new MultiTransformation(
                        new CenterCrop(), new RoundedCorners(DimenUtil.dp2px(mContext, 4.0f))));
            }
            if (resource == null || !resource.equals(this.ivContent.getTag(R.id.chat_msg_item_photo_iv))) {
                LogUtil.i(TAG, "loadDiskPhoto: 控件被复用了. photoKey: " + resource + ", view.tag: " + this.ivContent.getTag());
                return;
            }
            if (11 == moment.getType().intValue()) {
                Glide.with(mContext).load(photoMsg.getLocalPath())
                        .apply(new RequestOptions().error(R.drawable.ic_selfie_album_default)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .override(this.ivContent.getWidth(), this.ivContent.getHeight())
                                .transform((Transformation<Bitmap>) new RoundedCorners(DimenUtil.dp2px(mContext, 4.0f)))
                                .dontAnimate()
                                .signature(new ObjectKey(String.valueOf(Math.random()))))
                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                                loadAdvantagePhoto(moment, drawable, ShareInnerPhotoViewHolder.this.ivContent);
                            }
                        });
                return;
            }
            if (8 == moment.getType().intValue()) {
                LogUtil.d(TAG, "share image load");
                ShareImageMoment shareImageMoment = JSONUtil.fromJSON(moment.getContent(), ShareImageMoment.class);
                if (shareImageMoment != null && shareImageMoment.getDialogBitmapArgs() != null
                        && shareImageMoment.getMessageBitmapArgs() != null) {
                    DialogBitmapArgs dialogBitmapArgs = shareImageMoment.getDialogBitmapArgs();
                    options = new RequestOptions().error(R.drawable.ic_selfie_album_default_custom)
                            .placeholder(R.drawable.ic_selfie_album_default_custom)
                            .override(this.ivContent.getWidth(), this.ivContent.getHeight())
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .signature(new ObjectKey(photoMsg.getLocalPath()))
                            .transform((Transformation<Bitmap>) new CropTransform(dialogBitmapArgs.getCropWidth(),
                                    dialogBitmapArgs.getCropHeight(), dialogBitmapArgs.getCutStart(),
                                    dialogBitmapArgs.getCutTop()));
                }
            }
            final ShareVideoViewHolder videoHolder = holder instanceof ShareVideoViewHolder ? (ShareVideoViewHolder) holder : null;
            Glide.with(mContext).load(photoMsg.getLocalPath()).apply(options)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(GlideException exception, Object model, Target<Drawable> target,
                                boolean isFirstResource) {
                            if (videoHolder != null && videoHolder.videoPlayLogo != null) {
                                videoHolder.videoPlayLogo.setVisibility(View.GONE);
                                videoHolder.videoPlayLogo.setVisibility(View.INVISIBLE);
                            }
                            ShareInnerPhotoViewHolder.this.ivContent.setImageDrawable(ContextCompat.getDrawable(mContext,
                                    R.drawable.pi_friends_default));
                            HandlerUtil.runOnUIThreadNoCheck(new Runnable() {
                                @Override
                                public void run() {
                                    pullNewUrl(mContext, moment.getResource(), ShareInnerPhotoViewHolder.this.ivContent,
                                            false, moment, (AbsViewHolder) holder, Long.MAX_VALUE, null, null);
                                }
                            });
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(Drawable drawable, Object model, Target<Drawable> target,
                                DataSource dataSource, boolean isFirstResource) {
                            if (videoHolder != null && videoHolder.videoPlayLogo != null) {
                                videoHolder.videoPlayLogo.setVisibility(View.VISIBLE);
                            }
                            return false;
                        }
                    })
                    .into(new DrawableImageViewTarget(this.ivContent) {
                        @Override
                        public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                            ImageView view = getView();
                            if (videoHolder != null && videoHolder.videoPlayLogo != null) {
                                videoHolder.videoPlayLogo.setVisibility(View.VISIBLE);
                                Glide.with(mContext).load(R.drawable.ic_friends_play).into(videoHolder.videoPlayLogo);
                            }
                            view.setImageDrawable(drawable);
                        }
                    });
            LogUtil.d("load local image complete!!!");
        }
    }
    /** 视频动态的 ViewHolder。 */
    public class ShareVideoViewHolder extends ShareInnerPhotoViewHolder {
        public TextView videoContent;
        public ImageView videoPlayLogo;

        public ShareVideoViewHolder(View view) {
            super(view);
            this.videoPlayLogo = (ImageView) view.findViewById(R.id.iv_share_item_video_logo);
            this.ivContent = (ImageView) view.findViewById(R.id.chat_msg_item_photo_iv);
            this.videoContent = (TextView) view.findViewById(R.id.share_video_content);
            this.momentReminderView = (MomentReminderView) view.findViewById(R.id.moment_reminder);
        }

        @Override
        public void loadImage(Context context, DbMoment moment, AbsViewHolder holder) {
            if (this.ivContent == null) {
                LogUtil.d(TAG, "mContent == null");
                return;
            }
            boolean roundedCorner = moment.getType().intValue() != 9;
            if (11 == moment.getType().intValue()) {
                if (com.xtc.log.util.TextUtils.isEmpty(moment.getResource())) {
                    return;
                }
                configAndLoadAdvantisePhoto(mContext, moment, this.ivContent);
                return;
            }
            String content = moment.getContent();
            String resource = moment.getResource();
            VideoKeyOrToken videoKeyOrToken = JSONUtil.fromJSON(moment.getResource(), VideoKeyOrToken.class);
            String videoKeyOrTokenContent = videoKeyOrToken.getContent();
            this.videoContent.setVisibility(View.GONE);
            LogUtil.i(TAG, "videoKeyOrTokenContent 视频文本" + content + " momentBean " + videoKeyOrTokenContent);
            if (moment.getType().intValue() == 27) {
                MultiPhotoContent multiPhotoContent = JSONUtil.fromJSON(content, MultiPhotoContent.class);
                if (multiPhotoContent != null) {
                    this.videoContent.setVisibility(View.VISIBLE);
                    this.videoContent.setText(multiPhotoContent.getContent());
                    videoKeyOrTokenContent = multiPhotoContent.getVideoMsgContent();
                }
                LogUtil.i(TAG, "loadImage#momentBean: " + moment + "\n videoKeyOrToken :" + videoKeyOrToken);
                loadImageWithKey(mContext, this.ivContent, resource, "1", moment, roundedCorner, holder);
                return;
            }
            String loadContent = com.xtc.log.util.TextUtils.isEmpty(videoKeyOrTokenContent) ? content : videoKeyOrTokenContent;
            LogUtil.i(TAG, "loadImage#momentBean: " + moment + "\n videoKeyOrToken :" + videoKeyOrToken);
            if (loadContent != null && !com.xtc.log.util.TextUtils.isEmpty(loadContent)) {
                if (loadContent.contains("source")) {
                    dealLoadView(context, moment, holder, roundedCorner, loadContent, resource);
                    return;
                }
                ShareVideoMoment shareVideoMoment = JSONUtil.fromJSON(moment.getContent(), ShareVideoMoment.class);
                if (!com.xtc.log.util.TextUtils.isEmpty(shareVideoMoment.getLocalThumbnailPath())) {
                    LogUtil.d(TAG, "shareVideoMoment.getLocalPath():" + shareVideoMoment.getLocalThumbnailPath());
                    if (!new File(shareVideoMoment.getLocalThumbnailPath()).exists()) {
                        LogUtil.d(TAG, "!photoFile.exists()");
                        getVideoPic(context, moment, holder, roundedCorner, resource, shareVideoMoment.getSource(), loadContent);
                        return;
                    }
                    LogUtil.d(TAG, "load local image!!");
                    PhotoMsg photoMsg = new PhotoMsg();
                    photoMsg.setLocalPath(shareVideoMoment.getLocalThumbnailPath());
                    loadDiskPhoto(photoMsg, roundedCorner, moment, holder);
                    LogUtil.d(TAG, "load local image complete!!!");
                    return;
                }
                LogUtil.d(TAG, "photoMsg.getLocalPath()==empty");
                getVideoPic(context, moment, holder, roundedCorner, resource, shareVideoMoment.getSource(), loadContent);
                return;
            }
            loadImageWithKey(mContext, this.ivContent, resource, "1", moment, roundedCorner, holder);
        }

        private void getVideoPic(Context context, DbMoment moment, AbsViewHolder holder, boolean roundedCorner,
                String key, CloudFileResource source, String content) {
            if (source != null) {
                dislplayNetPhoto(context, key, this.ivContent, roundedCorner, moment, holder, content);
            } else {
                loadImageWithKey(mContext, this.ivContent, key, "1", moment, roundedCorner, holder);
            }
        }

        private void dealLoadView(Context context, DbMoment moment, AbsViewHolder holder, boolean roundedCorner,
                String content, String resource) {
            VideoMsg videoMsg = JSONUtil.fromJSON(content, VideoMsg.class);
            if (videoMsg == null) {
                LogUtil.d(TAG, "videoMsg == null");
                loadImageWithKey(mContext, this.ivContent, resource, "1", moment, roundedCorner, holder);
                return;
            }
            LogUtil.d(TAG, "VideoMsg = " + videoMsg);
            if (!com.xtc.log.util.TextUtils.isEmpty(videoMsg.getLocalThumbnailPath())) {
                LogUtil.d(TAG, "videoMsg.getLocalPath():" + videoMsg.getLocalThumbnailPath());
                if (!new File(videoMsg.getLocalThumbnailPath()).exists()) {
                    LogUtil.d(TAG, "!videoMsg.exists()");
                    getVideoPic(context, moment, holder, roundedCorner, resource, videoMsg.getSource(), content);
                    return;
                }
                LogUtil.d(TAG, "load local image!!");
                PhotoMsg photoMsg = new PhotoMsg();
                photoMsg.setLocalPath(videoMsg.getLocalThumbnailPath());
                loadDiskPhoto(photoMsg, roundedCorner, moment, holder);
                LogUtil.d(TAG, "load local image complete!!!");
                return;
            }
            LogUtil.d(TAG, "videoMsg.getLocalPath()==empty");
            CloudFileResource source = videoMsg.getSource();
            LogUtil.i(TAG, "videoMsg  source " + source);
            getVideoPic(context, moment, holder, roundedCorner, resource, source, content);
        }

        @Override
        public void setContentOnLongClickListener(DbMoment moment, OnContentOnLongClickListener listener) {
            onLongClick(this.ivContent, moment, listener);
            onLongClick(this.videoContent, moment, listener);
        }
    }
    /** 分享网页动态的 ViewHolder。 */
    public class ShareWebInnerViewHolder extends ShareInnerPhotoViewHolder {
        ImageView ivAppIcon;
        MomentContentView mDescriptions;
        MomentContentView mNoSupport;
        RelativeLayout rlShareContent;
        TextView tvAppName;

        public ShareWebInnerViewHolder(View view) {
            super(view);
            this.mDescriptions = (MomentContentView) view.findViewById(R.id.tv_moment_description);
            this.mNoSupport = (MomentContentView) view.findViewById(R.id.tv_no_support);
            this.rlShareContent = (RelativeLayout) view.findViewById(R.id.rl_share_content);
            this.ivAppIcon = (ImageView) view.findViewById(R.id.iv_app_icon);
            this.tvAppName = (TextView) view.findViewById(R.id.tv_app_name);
            this.momentReminderView = (MomentReminderView) view.findViewById(R.id.moment_reminder);
        }

        @Override
        public void loadImage(Context context, DbMoment moment, AbsViewHolder holder) {
            if (TextUtils.isEmpty(moment.getResource())) {
                LogUtil.w(TAG, "momentBean.getResource == null");
                return;
            }
            super.loadImage(context, moment, holder);
            ShareWebMoment shareWebMoment = JSONUtil.fromJSON(moment.getContent(), ShareWebMoment.class);
            if (shareWebMoment == null) {
                this.mNoSupport.setVisibility(View.VISIBLE);
                this.rlShareContent.setVisibility(View.GONE);
                return;
            }
            this.mNoSupport.setVisibility(View.GONE);
            this.rlShareContent.setVisibility(View.VISIBLE);
            String desc = shareWebMoment.getDesc();
            if (TextUtils.isEmpty(desc)) {
                this.mDescriptions.setVisibility(View.GONE);
            } else {
                LogUtil.d(TAG, "Description:" + desc);
                this.mDescriptions.setVisibility(View.VISIBLE);
                this.mDescriptions.setContext(context);
                this.mDescriptions.setText(desc);
            }
            this.tvAppName.setText(shareWebMoment.getAppName());
            if (shareWebMoment.getAppIcon() != null) {
                Glide.with(context).load(shareWebMoment.getAppIcon())
                        .apply(new RequestOptions().signature(new ObjectKey(String.valueOf(Math.random()))))
                        .into(this.ivAppIcon);
            }
        }

        @Override
        public void setContentOnClickListener(final DbMoment moment, final OnContentOnClickListener listener) {
            if (moment == null) {
                LogUtil.d(TAG, "click share app, moment bean is null! ");
                return;
            }
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemUtil.isFastDoubleClick()) {
                        LogUtil.d(TAG, "isFastDoubleClick");
                        return;
                    }
                    if (listener == null || !mIsSelf || moment == null) {
                        return;
                    }
                    ShareWebMoment shareWebMoment = JSONUtil.fromJSON(moment.getContent(), ShareWebMoment.class);
                    if (shareWebMoment == null || com.xtc.log.util.TextUtils.isEmpty(shareWebMoment.getWebLink())) {
                        LogUtil.e(TAG, "shareWebMoment or WebLink is null");
                        return;
                    }
                    Uri.Builder builder = Uri.parse(shareWebMoment.getWebLink()).buildUpon();
                    builder.appendQueryParameter("momentWatchId", moment.getWatchId());
                    builder.appendQueryParameter("momentId", moment.getMomentId());
                    listener.previewH5(builder.build().toString());
                }
            });
        }

        @Override
        public void setContentOnLongClickListener(DbMoment moment, OnContentOnLongClickListener listener) {
            onLongClick(this.itemView, moment, listener);
        }
    }

    /** 分享应用动态的 ViewHolder。 */
    public class ShareAppInnerViewHolder extends ShareInnerPhotoViewHolder {
        ViewGroup rlShareContent;
        TextView tvAppName;
        TextView tvDesc;
        MomentContentView tvNoSupport;

        public ShareAppInnerViewHolder(View view) {
            super(view);
            this.tvAppName = (TextView) view.findViewById(R.id.tv_app_name);
            this.tvDesc = (TextView) view.findViewById(R.id.tv_desc);
            this.momentCommentView = (MainMomentCommentView) view.findViewById(R.id.moment_comment);
            this.commentRecyclerView = (RecyclerView) view.findViewById(R.id.moment_comment_rec);
            this.commentRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            this.tvNoSupport = (MomentContentView) view.findViewById(R.id.tv_no_support);
            this.rlShareContent = (ViewGroup) view.findViewById(R.id.rl_share_content);
            this.ivShareReportIcon = (ImageView) view.findViewById(R.id.iv_share_account_report);
            this.momentReminderView = (MomentReminderView) view.findViewById(R.id.moment_reminder);
        }

        @Override
        public void loadImage(Context context, DbMoment moment, AbsViewHolder holder) {
            ShareAppMoment shareAppMoment = JSONUtil.fromJSON(moment.getContent(), ShareAppMoment.class);
            if (shareAppMoment == null) {
                this.tvNoSupport.setVisibility(View.VISIBLE);
                this.rlShareContent.setVisibility(View.GONE);
                return;
            }
            this.tvNoSupport.setVisibility(View.GONE);
            this.rlShareContent.setVisibility(View.VISIBLE);
            super.loadImage(context, moment, holder);
            this.tvAppName.setText(shareAppMoment.getAppName());
            this.tvDesc.setText(shareAppMoment.getDesc());
        }

        @Override
        public void setContentOnClickListener(final DbMoment moment, OnContentOnClickListener listener) {
            if (moment == null) {
                LogUtil.d(TAG, "click share app, moment bean is null! ");
                return;
            }
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShareAppStartUtil.startApp(mContext, moment);
                }
            });
        }

        @Override
        public void setContentOnLongClickListener(DbMoment moment, OnContentOnLongClickListener listener) {
            onLongClick(this.itemView, moment, listener);
        }
    }

    /** 分享图片动态的 ViewHolder。 */
    public class ShareImageInnerViewHolder extends ShareInnerPhotoViewHolder {
        ImageView ivAppIcon;
        RelativeLayout rlShareContent;
        TextView tvAppName;
        MomentContentView tvNoSupport;

        public ShareImageInnerViewHolder(View view) {
            super(view);
            this.ivAppIcon = (ImageView) view.findViewById(R.id.iv_app_icon);
            this.tvAppName = (TextView) view.findViewById(R.id.tv_app_name);
            this.momentCommentView = (MainMomentCommentView) view.findViewById(R.id.moment_comment);
            this.commentRecyclerView = (RecyclerView) view.findViewById(R.id.moment_comment_rec);
            this.commentRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            this.tvNoSupport = (MomentContentView) view.findViewById(R.id.tv_no_support);
            this.rlShareContent = (RelativeLayout) view.findViewById(R.id.rl_share_content);
            this.ivShareReportIcon = (ImageView) view.findViewById(R.id.iv_share_account_report);
            this.momentReminderView = (MomentReminderView) view.findViewById(R.id.moment_reminder);
        }

        @Override
        public void loadImage(Context context, DbMoment moment, AbsViewHolder holder) {
            ShareImageMoment shareImageMoment = JSONUtil.fromJSON(moment.getContent(), ShareImageMoment.class);
            if (shareImageMoment == null) {
                LogUtil.d(TAG, "share image moment is null!");
                this.tvNoSupport.setVisibility(View.VISIBLE);
                this.rlShareContent.setVisibility(View.GONE);
                return;
            }
            this.tvNoSupport.setVisibility(View.GONE);
            this.rlShareContent.setVisibility(View.VISIBLE);
            super.loadImage(context, moment, holder);
            this.tvAppName.setText(shareImageMoment.getAppName());
            if (shareImageMoment.getAppIcon() != null) {
                Glide.with(context).load(shareImageMoment.getAppIcon())
                        .apply(new RequestOptions().signature(new ObjectKey(String.valueOf(Math.random()))))
                        .into(this.ivAppIcon);
            }
        }
    }

    /** 分享文本动态的 ViewHolder。 */
    class ShareTextInnerViewHolder extends ShareInnerViewHolder {
        ImageView appIcon;
        TextView appName;
        RelativeLayout rlShareContent;
        MomentContentView tvNoSupport;

        public ShareTextInnerViewHolder(View view) {
            super(view);
            this.appIcon = (ImageView) view.findViewById(R.id.iv_app_icon);
            this.appName = (TextView) view.findViewById(R.id.tv_app_name);
            this.tvNoSupport = (MomentContentView) view.findViewById(R.id.tv_no_support);
            this.rlShareContent = (RelativeLayout) view.findViewById(R.id.rl_share_content);
            this.momentCommentView = (MainMomentCommentView) view.findViewById(R.id.moment_comment);
            this.commentRecyclerView = (RecyclerView) view.findViewById(R.id.moment_comment_rec);
            this.commentRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            this.ivShareReportIcon = (ImageView) view.findViewById(R.id.iv_share_account_report);
            this.momentReminderView = (MomentReminderView) view.findViewById(R.id.moment_reminder);
        }

        @Override
        public void setTvContent(String resource, String content) {
            ShareTextPublish shareTextPublish = JSONUtil.fromJSON(content, ShareTextPublish.class);
            if (shareTextPublish == null) {
                this.tvNoSupport.setVisibility(View.VISIBLE);
                this.rlShareContent.setVisibility(View.GONE);
                return;
            }
            this.tvNoSupport.setVisibility(View.GONE);
            this.rlShareContent.setVisibility(View.VISIBLE);
            this.tvContent.setContext(mContext);
            this.tvContent.setText(shareTextPublish.getContent());
            this.appName.setText(shareTextPublish.getAppName());
            if (shareTextPublish.getAppIcon() != null) {
                Glide.with(this.appIcon.getContext()).load(shareTextPublish.getAppIcon())
                        .apply(new RequestOptions().signature(new ObjectKey(String.valueOf(Math.random()))))
                        .into(this.appIcon);
            }
        }

        @Override
        public void setTvContent(int resId, String content) {
            this.tvContent.setContext(mContext);
            this.tvContent.setRichText(resId, content);
        }
    }
    /** 实况照片动态的 ViewHolder。 */
    class ShareLivePhotoInnerViewHolder extends ShareInnerPhotoViewHolder {
        public ShareLivePhotoInnerViewHolder(View view) {
            super(view);
            this.momentReminderView = (MomentReminderView) view.findViewById(R.id.moment_reminder);
        }

        @Override
        public void loadImage(Context context, DbMoment moment, AbsViewHolder holder) {
            super.loadImage(context, moment, holder);
        }

        @Override
        public void setContentOnClickListener(final DbMoment moment, final OnContentOnClickListener listener) {
            if (this.ivContent == null) {
                LogUtil.w(TAG, "LivePhotoViewHolder#setContentOnClickListener: ivContent is null");
                return;
            }
            this.ivContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (moment == null) {
                        LogUtil.w(TAG, "LivePhotoViewHolder#onClick: dbMoment is null");
                        return;
                    }
                    String content = moment.getContent();
                    if (content != null && !com.xtc.log.util.TextUtils.isEmpty(content)) {
                        LivePhotoMsg livePhotoMsg = JSONUtil.fromJSON(content, LivePhotoMsg.class);
                        if (listener != null) {
                            listener.previewLivePhoto(livePhotoMsg);
                        }
                        return;
                    }
                    getPhotoUrl(mContext, moment.getResource(), "1", moment, listener);
                }
            });
        }
    }

    /** 分享实况照片动态的 ViewHolder。 */
    class ShareLivePhotoShareViewHolder extends ShareLivePhotoInnerViewHolder {
        ImageView ivAppIcon;
        RelativeLayout rlShareContent;
        TextView tvAppName;
        MomentContentView tvNoSupport;

        public ShareLivePhotoShareViewHolder(View view) {
            super(view);
            this.ivAppIcon = (ImageView) view.findViewById(R.id.iv_app_icon);
            this.tvAppName = (TextView) view.findViewById(R.id.tv_app_name);
            this.rlShareContent = (RelativeLayout) view.findViewById(R.id.rl_share_content);
            this.tvNoSupport = (MomentContentView) view.findViewById(R.id.tv_no_support);
            this.momentReminderView = (MomentReminderView) view.findViewById(R.id.moment_reminder);
        }

        @Override
        public void loadImage(Context context, DbMoment moment, AbsViewHolder holder) {
            ShareImageMoment shareImageMoment = JSONUtil.fromJSON(moment.getContent(), ShareImageMoment.class);
            if (shareImageMoment == null) {
                LogUtil.w(TAG, "share image moment is null!");
                this.tvNoSupport.setVisibility(View.VISIBLE);
                this.rlShareContent.setVisibility(View.GONE);
                return;
            }
            this.tvNoSupport.setVisibility(View.GONE);
            this.rlShareContent.setVisibility(View.VISIBLE);
            super.loadImage(context, moment, holder);
            this.tvAppName.setText(shareImageMoment.getAppName());
            if (shareImageMoment.getAppIcon() != null) {
                Glide.with(context).load(shareImageMoment.getAppIcon())
                        .apply(new RequestOptions().signature(new ObjectKey(String.valueOf(Math.random()))))
                        .into(this.ivAppIcon);
            }
        }
    }

    /** 通用文本动态的 ViewHolder。 */
    class ShareInnerViewHolder extends AbsViewHolder {
        public ShareInnerViewHolder(View view) {
            super(view);
            this.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
            this.tvName = (TextView) view.findViewById(R.id.tv_name);
            this.tvTime = (TextView) view.findViewById(R.id.tv_time);
            this.tvContent = (MomentContentView) view.findViewById(R.id.tv_content);
            this.tvLikes = (TextView) view.findViewById(R.id.tv_likes);
            this.llLbs = (LbsLayout) view.findViewById(R.id.ll_lbs);
            this.ivBanner = (ImageView) view.findViewById(R.id.iv_banner);
            this.momentCommentView = (MainMomentCommentView) view.findViewById(R.id.moment_comment);
            this.commentRecyclerView = (RecyclerView) view.findViewById(R.id.moment_comment_rec);
            if (this.commentRecyclerView != null) {
                this.commentRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            }
            this.ivShareReportIcon = (ImageView) view.findViewById(R.id.iv_share_account_report);
            if (this.tvLikes != null) {
                this.tvLikes.setHighlightColor(0);
            }
            this.ivMomentRange = (ImageView) view.findViewById(R.id.iv_moment_range);
            this.momentLike = (MomentLikeView) view.findViewById(R.id.moment_like);
            this.ivMomentComment = (CommentIconView) view.findViewById(R.id.iv_moment_comment);
            this.momentReminderView = (MomentReminderView) view.findViewById(R.id.moment_reminder);
        }

        @Override
        public void setTvContent(String resource, String content) {
            this.tvContent.setContext(mContext);
            this.tvContent.setRichText(resource, content);
        }

        @Override
        public void setTvContent(int resId, String content) {
            this.tvContent.setContext(mContext);
            this.tvContent.setRichText(resId, content);
        }

        @Override
        public void setContentVisibility(boolean visible) {
            super.setContentVisibility(visible);
            this.tvContent.setVisibility(visible ? View.VISIBLE : View.GONE);
        }

        @Override
        public void setContentOnLongClickListener(DbMoment moment, OnContentOnLongClickListener listener) {
            onLongClick(this.tvContent, moment, listener);
            if (this.llLbs != null) {
                onLongClick(this.llLbs, moment, listener);
            }
        }
    }

    private void onLongClick(View view, final DbMoment moment, final OnContentOnLongClickListener listener) {
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view2) {
                if (listener != null && mIsSelf) {
                    listener.deleteItem(moment);
                    return true;
                }
                if (mIsSelf || !supportReportType(moment)) {
                    return true;
                }
                showReportBtnDialog(new AbsInteractionAdapter.IOnDialogClickLister() {
                    @Override
                    public void onRightBtnClick() {
                        ReportDataRecorder.recordDbMoment(moment);
                        startReportActivity(moment.getWatchId(), moment.getMomentId(), "", null);
                    }
                });
                return true;
            }
        });
    }
    /** 多图动态的 ViewHolder。 */
    public class PhotoViewHolders extends AbsViewHolder {
        private final MomentPhotoServeHttpProxy momentPhotoServeHttpProxy;
        private ArrayList<String> photosPathKey;
        private final BaseOverlayPageAdapter simpleOverlayAdapter;
        private final TextView tvContent;
        private PointerViewPager vp;

        public PhotoViewHolders(View view) {
            super(view);
            this.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
            this.tvName = (TextView) view.findViewById(R.id.tv_name);
            this.tvTime = (TextView) view.findViewById(R.id.tv_time);
            this.vp = (PointerViewPager) view.findViewById(R.id.share_vp);
            this.tvLikes = (TextView) view.findViewById(R.id.tv_likes);
            this.llLbs = (LbsLayout) view.findViewById(R.id.ll_lbs);
            this.tvContent = (TextView) view.findViewById(R.id.share_content);
            this.momentCommentView = (MainMomentCommentView) view.findViewById(R.id.moment_comment);
            this.commentRecyclerView = (RecyclerView) view.findViewById(R.id.moment_comment_rec);
            this.commentRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            this.ivShareReportIcon = (ImageView) view.findViewById(R.id.iv_share_account_report);
            this.momentPhotoServeHttpProxy = new MomentPhotoServeHttpProxy(mContext);
            this.simpleOverlayAdapter = new BaseOverlayPageAdapter(mContext, true);
            this.ivMomentRange = (ImageView) view.findViewById(R.id.iv_moment_range);
            this.momentLike = (MomentLikeView) view.findViewById(R.id.moment_like);
            this.ivMomentComment = (CommentIconView) view.findViewById(R.id.iv_moment_comment);
            this.momentReminderView = (MomentReminderView) view.findViewById(R.id.moment_reminder);
        }

        @Override
        public void loadImage(Context context, DbMoment moment, AbsViewHolder holder) {
            super.loadImage(context, moment, holder);
            LogUtil.i(TAG, "loadImage momentBean" + moment.getContent());
            this.photosPathKey = new ArrayList<>();
            String content = moment.getContent();
            String photoListContent = moment.getPhotoListContent();
            LogUtil.i(TAG, "publishContent " + photoListContent);
            SaveDynamic.saveIsMomentPhotoView(context, false);
            String[] resourceKeys = moment.getResource().split(",");
            for (String resourceKey : resourceKeys) {
                this.photosPathKey.add(resourceKey);
                LogUtil.i(TAG, "split 拆分 " + resourceKey);
            }
            this.tvContent.setVisibility(View.GONE);
            MultiPhotoContent multiPhotoContent = JSONUtil.fromJSON(content, MultiPhotoContent.class);
            if (!TextUtils.isEmpty(content) && multiPhotoContent != null
                    && !com.xtc.log.util.TextUtils.isEmpty(multiPhotoContent.getContent())) {
                this.tvContent.setVisibility(View.VISIBLE);
                this.tvContent.setText(multiPhotoContent.getContent());
            }
            PhotoMsg photoMsg = JSONUtil.fromJSON(photoListContent, PhotoMsg.class);
            if (photoMsg != null && !com.xtc.database.ormlite.CollectionUtil.isEmpty(photoMsg.getPhotoPathLoad())) {
                ArrayList<String> photoPathLoad = photoMsg.getPhotoPathLoad();
                for (int i = 0; i < photoPathLoad.size(); i++) {
                    File file = new File(photoPathLoad.get(i));
                    LogUtil.i(TAG, " photoPathLoad " + photoPathLoad);
                    if (!file.exists() && photoPathLoad.size() != resourceKeys.length && file.length() == 0) {
                        LogUtil.i(TAG, "网络数据");
                        dislplayNetPhoto();
                    } else if (photoPathLoad.size() == resourceKeys.length) {
                        LogUtil.i(TAG, "本地缓存");
                        this.simpleOverlayAdapter.refreshView(this.vp, photoPathLoad);
                        this.vp.setAdapter(this.simpleOverlayAdapter);
                        this.simpleOverlayAdapter.setTransformer(this.vp);
                        this.vp.setOffscreenPageLimit(3);
                    }
                }
                return;
            }
            dislplayNetPhoto();
        }

        private void dislplayNetPhoto() {
            this.momentPhotoServeHttpProxy.getDownloadBatchUrl(new FileBatchUrlParam(this.photosPathKey))
                    .map(new Func1<DownloadUrlVo, ArrayList<String>>() {
                        @Override
                        public ArrayList<String> call(DownloadUrlVo downloadUrlVo) {
                            List<CloudFileResource> urls = downloadUrlVo.getUrls();
                            ArrayList<String> paths = new ArrayList<>();
                            for (int i = 0; i < urls.size(); i++) {
                                String downloadUrl = urls.get(i).getDownloadUrl();
                                LogUtil.i(TAG, "getDownloadUrl" + downloadUrl);
                                paths.add(downloadUrl);
                            }
                            return paths;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<ArrayList<String>>() {
                        @Override
                        public void call(ArrayList<String> paths) {
                            LogUtil.i(TAG, "path批量获取网络图片 " + paths.toString());
                            simpleOverlayAdapter.refreshView(vp, paths);
                            vp.setAdapter(simpleOverlayAdapter);
                            simpleOverlayAdapter.setTransformer(vp);
                            vp.setOffscreenPageLimit(3);
                        }
                    }, RxUtils.logError(TAG));
        }

        @Override
        public void setContentOnLongClickListener(final DbMoment moment, final OnContentOnLongClickListener listener) {
            super.setContentOnLongClickListener(moment, listener);
            BaseOverlayPageAdapter overlayAdapter = this.simpleOverlayAdapter;
            if (overlayAdapter != null) {
                overlayAdapter.setOnClickReport(new BaseOverlayPageAdapter.onClickReport() {
                    @Override
                    public void report(final int position) {
                        if (!moment.getWatchId().equals(selfWatchId)) {
                            showReportBtnDialog(new AbsInteractionAdapter.IOnDialogClickLister() {
                                @Override
                                public void onRightBtnClick() {
                                    if (position >= 0 && position < photosPathKey.size()) {
                                        startReportActivity(moment, "", photosPathKey.get(position), null, 2);
                                    } else {
                                        LogUtil.d(TAG, "position越界");
                                        ToastUtil.showShortCover(mContext, R.string.report_falied);
                                    }
                                }
                            });
                        } else {
                            listener.deleteItem(moment);
                            LogUtil.i(TAG, "simpleOverlayAdapter" + selfWatchId);
                        }
                    }
                });
            }
            this.tvContent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (!moment.getWatchId().equals(selfWatchId)) {
                        if (!supportReportType(moment)) {
                            return false;
                        }
                        showReportBtnDialog(new AbsInteractionAdapter.IOnDialogClickLister() {
                            @Override
                            public void onRightBtnClick() {
                                String content = tvContent.getText().toString();
                                LogUtil.i(TAG, "momentContent " + content);
                                startReportActivity(moment, "", content, null, 1);
                            }
                        });
                        return false;
                    }
                    listener.deleteItem(moment);
                    return false;
                }
            });
        }
    }
}
