package com.xtc.moment.module.like;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xtc.log.LogUtil;
import com.xtc.log.util.TextUtils;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.bean.MomentNewMsgBean;
import com.xtc.moment.module.like.viewholder.AbsViewHolder;
import com.xtc.moment.module.like.viewholder.LikeViewHolder;
import com.xtc.moment.module.like.viewholder.LivePhotoViewHolder;
import com.xtc.moment.module.like.viewholder.OfficialPhotoH5ViewHolder;
import com.xtc.moment.module.like.viewholder.OfficialPhotoViewHolderComment;
import com.xtc.moment.module.like.viewholder.PhotoViewHolder;
import com.xtc.moment.module.like.viewholder.PhotoViewsHolder;
import com.xtc.moment.module.like.viewholder.ShareAppViewHolder;
import com.xtc.moment.module.like.viewholder.ShareImageViewHolder;
import com.xtc.moment.module.like.viewholder.ShareLivePhotoViewHolder;
import com.xtc.moment.module.like.viewholder.ShareTextViewHolder;
import com.xtc.moment.module.like.viewholder.ShareVideoViewHolder;
import com.xtc.moment.module.like.viewholder.ShareWebViewHolder;
import com.xtc.moment.module.like.viewholder.VideoViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 新消息列表适配器，按动态类型复用分享模块的 ViewHolder。
 */
public class NewLikeAdapter extends RecyclerView.Adapter<AbsViewHolder> {

    private static final String TAG = NewLikeAdapter.class.getSimpleName();

    private static final int ITEM_TYPE_NORMAL = 1;
    private static final int ITEM_TYPE_EMPTY = 2;
    private static final int ITEM_TYPE_PHOTO = 4;
    private static final int ITEM_TYPE_VOICE = 5;
    private static final int ITEM_TYPE_VIDEO = 6;
    private static final int ITEM_TYPE_SHARE_TEXT = 7;
    private static final int ITEM_TYPE_SHARE_IMAGE = 8;
    private static final int ITEM_TYPE_SHARE_APP = 9;
    private static final int ITEM_TYPE_HEADER = 10;
    private static final int ITEM_TYPE_FOOTER = 11;
    private static final int ITEM_TYPE_OFFICIAL_PHOTO = 20;
    private static final int ITEM_TYPE_OFFICIAL_PHOTO_TEXT = 21;
    private static final int ITEM_TYPE_OFFICIAL_PHOTO_H5 = 22;
    private static final int ITEM_TYPE_LIVE_PHOTO = 23;
    private static final int ITEM_TYPE_SHARE_LIVE_PHOTO = 24;
    private static final int ITEM_TYPE_OFFICIAL_VIDEO = 25;
    private static final int ITEM_TYPE_SHARE_H5 = 26;
    private static final int ITEM_TYPE_SHARE_VIDEO = 27;

    private Context mContext;
    private List<MomentNewMsgBean<DbMoment>> mData;
    private View mHeaderView;
    private View mFooterView;
    private View mEmptyView;

    public NewLikeAdapter(Context context, List<MomentNewMsgBean<DbMoment>> data) {
        this.mData = new ArrayList<>();
        this.mContext = context;
        if (data != null) {
            this.mData = data;
        }
    }

    public void setData(List<MomentNewMsgBean<DbMoment>> data) {
        if (data == null) {
            return;
        }
        this.mData.clear();
        this.mData.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public AbsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AbsViewHolder holder;
        LogUtil.d("moment", "viewType:" + viewType);
        if (viewType == ITEM_TYPE_PHOTO) {
            holder = new PhotoViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recycle_photo_comment, parent, false));
        } else {
            switch (viewType) {
                case ITEM_TYPE_VIDEO:
                    holder = new VideoViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_recycle_video_comment, parent, false));
                    break;
                case ITEM_TYPE_SHARE_TEXT:
                    holder = new ShareTextViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_recycle_share_text_comment, parent, false));
                    break;
                case ITEM_TYPE_SHARE_IMAGE:
                    holder = new ShareImageViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_recycle_share_image_comment, parent, false));
                    break;
                case ITEM_TYPE_SHARE_APP:
                    holder = new ShareAppViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_recycle_share_app_comment, parent, false));
                    break;
                case ITEM_TYPE_HEADER:
                    ViewGroup headerParent = (ViewGroup) this.mHeaderView.getParent();
                    if (headerParent != null) {
                        headerParent.removeView(this.mHeaderView);
                    }
                    holder = new LikeViewHolder(this.mHeaderView);
                    break;
                case ITEM_TYPE_FOOTER:
                    ViewGroup footerParent = (ViewGroup) this.mFooterView.getParent();
                    if (footerParent != null) {
                        footerParent.removeView(this.mFooterView);
                    }
                    holder = new LikeViewHolder(this.mFooterView);
                    break;
                default:
                    switch (viewType) {
                        case ITEM_TYPE_OFFICIAL_PHOTO:
                            holder = new PhotoViewHolder(LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_recycle_official_photo_comment, parent, false));
                            break;
                        case ITEM_TYPE_OFFICIAL_PHOTO_TEXT:
                            holder = new OfficialPhotoViewHolderComment(LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_recycle_official_photo_text_comment, parent, false));
                            break;
                        case ITEM_TYPE_OFFICIAL_PHOTO_H5:
                            holder = new OfficialPhotoH5ViewHolder(LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_recycle_official_photo_text_comment_h5, parent, false));
                            break;
                        case ITEM_TYPE_LIVE_PHOTO:
                            holder = new LivePhotoViewHolder(LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_recycle_live_photo_comment, parent, false));
                            break;
                        case ITEM_TYPE_SHARE_LIVE_PHOTO:
                            holder = new ShareLivePhotoViewHolder(LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_recycle_share_live_photo_comment, parent, false));
                            break;
                        case ITEM_TYPE_OFFICIAL_VIDEO:
                            holder = new VideoViewHolder(LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_recycle_official_video_comment, parent, false));
                            break;
                        case ITEM_TYPE_SHARE_H5:
                            holder = new ShareWebViewHolder(LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_recycle_share_web_comment, parent, false));
                            break;
                        case ITEM_TYPE_SHARE_VIDEO:
                            holder = new ShareVideoViewHolder(LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_recycle_share_video_comment, parent, false));
                            break;
                        case 28:
                            holder = new PhotoViewsHolder(LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_recycle_photos_comment, parent, false));
                            break;
                        case 29:
                            holder = new VideoViewHolder(LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_recycle_video_comment, parent, false));
                            break;
                        default:
                            holder = new LikeViewHolder(LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_recycle_comment, parent, false));
                            break;
                    }
                    break;
            }
        }
        if (holder.llLbs != null) {
            holder.llLbs.setBackground(null);
            holder.llLbs.setClickable(false);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == ITEM_TYPE_HEADER || itemViewType == ITEM_TYPE_FOOTER
                || itemViewType == ITEM_TYPE_EMPTY) {
            return;
        }
        MomentNewMsgBean<DbMoment> message = this.mData.get(getDataItemPosition(position));
        LogUtil.d(TAG, "onBindViewHolder#momentNewMsgBean:" + message);
        holder.setAccountName(TextUtils.isEmpty(message.getCommentName())
                ? this.mContext.getString(R.string.me) : message.getCommentName());
        holder.setTime(this.mContext, message.getCreateTime().longValue());
        holder.momentView.setContext(this.mContext);
        int messageType = message.getType();
        if (messageType == 1) {
            holder.setCommentType(1, this.mContext.getResources().getDrawable(R.drawable.circle_like_pressed), message);
        } else if (messageType == 2) {
            holder.setCommentType(2, null, message);
            holder.setComment(TextUtils.isEmpty(message.getContent()) ? "" : message.getContent());
        }
        holder.hideView();
        if (message == null || !(message.getBean() instanceof DbMoment)) {
            return;
        }
        DbMoment moment = message.getBean();
        int momentType = moment.getType().intValue();
        if (momentType == 0 || momentType == 1 || momentType == 3) {
            setTextData(holder, moment);
            return;
        }
        if (momentType == 2) {
            setLocationData(holder, moment);
            return;
        }
        if (momentType == 10) {
            setAdvertiseTextData(holder, moment);
            return;
        }
        if (!isKnownPhotoType(momentType)) {
            setUnknownData(holder, moment);
        }
        setPhotoData(holder, moment, this.mContext);
    }

    /** 分享模块支持直接展示图片/视频内容的动态类型。 */
    private static boolean isKnownPhotoType(int momentType) {
        switch (momentType) {
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 11:
            case 12:
            case 13:
            case 14:
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

    private void setUnknownData(AbsViewHolder holder, DbMoment moment) {
        holder.momentView.setContentSize(14L);
        holder.momentView.setContent(0, this.mContext.getString(R.string.no_support_tips),
                this.mContext.getResources().getColor(R.color.normal_text));
        dealLbs(holder, moment);
    }

    private void setTextData(AbsViewHolder holder, DbMoment moment) {
        holder.momentView.setContentSize(14L);
        holder.momentView.setContent(moment.getResource(), moment.getContent(),
                this.mContext.getResources().getColor(R.color.normal_text));
        dealLbs(holder, moment);
    }

    private void setAdvertiseTextData(AbsViewHolder holder, DbMoment moment) {
        holder.momentView.setContentSize(14L);
        holder.momentView.setContent(moment.getResource(), moment.getDescription(),
                this.mContext.getResources().getColor(R.color.normal_text));
        dealLbs(holder, moment);
    }

    private void setPhotoData(AbsViewHolder holder, DbMoment moment, Context context) {
        if (holder == null) {
            notifyDataSetChanged();
            return;
        }
        int momentType = moment.getType().intValue();
        if (momentType == 14 || momentType == 25 || momentType == 8) {
            holder.momentView.setContentSize(11L);
        } else if (momentType == 9) {
            holder.momentView.setContentSize(12L);
        } else if (momentType == 10) {
            holder.momentView.setContentSize(13L);
        }
        holder.momentView.setTags(R.id.moment, moment.getResource());
        LogUtil.i(TAG, "setPhotoData" + moment);
        holder.momentView.loadImage(context, moment);
        dealLbs(holder, moment);
    }

    private void dealLbs(AbsViewHolder holder, DbMoment moment) {
        String location = moment.getLocation();
        if (holder.llLbs != null) {
            holder.llLbs.setVisibility(
                    (TextUtils.isEmpty(location) || !moment.isLbsSwitch()) ? View.GONE : View.VISIBLE);
            holder.llLbs.setLocation(location);
        }
        holder.momentView.setContentVisibility(true);
    }

    private void setLocationData(AbsViewHolder holder, DbMoment moment) {
        if (holder == null) {
            notifyDataSetChanged();
            return;
        }
        String location = moment.getLocation();
        if (holder.llLbs != null) {
            holder.llLbs.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(location) || !moment.isLbsSwitch()) {
            holder.momentView.setContentVisibility(true);
            holder.momentView.setContent(R.drawable.location, moment.getContent(),
                    this.mContext.getResources().getColor(R.color.color_888888));
            holder.momentView.setContentSize(13L);
        } else if (holder.llLbs != null) {
            holder.momentView.setContentVisibility(false);
            holder.llLbs.setVisibility(View.VISIBLE);
            holder.llLbs.setLocation(location);
        }
    }

    @Override
    public int getItemCount() {
        List<MomentNewMsgBean<DbMoment>> data = this.mData;
        int size = data != null ? data.size() : 0;
        if (hasEmptyView() && size == 0) {
            size++;
        }
        if (hasHeaderView()) {
            size++;
        }
        return hasFooterView() ? size + 1 : size;
    }

    private int getDataItemPosition(int position) {
        return hasHeaderView() ? position - 1 : position;
    }

    public int getHolderPosition(MomentNewMsgBean message) {
        List<MomentNewMsgBean<DbMoment>> data = this.mData;
        if (data == null || data.isEmpty()) {
            return -1;
        }
        if (!hasHeaderView()) {
            return this.mData.indexOf(message);
        }
        return this.mData.indexOf(message) + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasHeaderView() && position == 0) {
            return ITEM_TYPE_HEADER;
        }
        if (hasFooterView() && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        }
        if (this.mEmptyView == null || this.mData.size() != 0) {
            return getMomentNewMsgType(this.mData.get(getDataItemPosition(position)));
        }
        return ITEM_TYPE_EMPTY;
    }

    private static int getMomentNewMsgType(MomentNewMsgBean message) {
        if (message.getBean() == null || !(message.getBean() instanceof DbMoment)) {
            return ITEM_TYPE_NORMAL;
        }
        int momentType = ((DbMoment) message.getBean()).getType().intValue();
        switch (momentType) {
            case 0:
            case 1:
            case 2:
            case 3:
                return ITEM_TYPE_NORMAL;
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
                switch (momentType) {
                    case 11:
                        return ITEM_TYPE_OFFICIAL_PHOTO;
                    case 12:
                        return ITEM_TYPE_OFFICIAL_PHOTO_TEXT;
                    case 13:
                        return ITEM_TYPE_OFFICIAL_VIDEO;
                    case 14:
                        return ITEM_TYPE_OFFICIAL_PHOTO_H5;
                    default:
                        switch (momentType) {
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

    private boolean hasHeaderView() {
        return this.mHeaderView != null;
    }

    private boolean hasFooterView() {
        return this.mFooterView != null;
    }

    private boolean hasEmptyView() {
        return this.mEmptyView != null;
    }
}