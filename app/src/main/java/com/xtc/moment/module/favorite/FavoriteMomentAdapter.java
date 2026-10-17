package com.xtc.moment.module.favorite;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.xtc.contactapi.contact.bean.ContactBean;
import com.xtc.contactapi.contact.manager.ContactManager;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.report.adapter.AbsInteractionAdapter;
import com.xtc.moment.module.share.ShareActivity;
import com.xtc.moment.module.share.adapter.ShareAdapter;
import com.xtc.moment.module.share.holder.AbsViewHolder;
import com.xtc.moment.module.widget.VerticallyLinearLayoutManager;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.util.DressUtil;
import com.xtc.moment.util.LikeDrawableCache;
import com.xtc.moment.util.MomentTypeUtil;
import com.xtc.moment.util.SystemUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 收藏列表适配器：卡片样式复用个人动态（{@link ShareAdapter}），只按「收藏」这个场景改三件事。
 *
 * <ol>
 *     <li>作者按逐条解析（收藏是混排的），联系人被删时回退到快照里保存的昵称与头像；</li>
 *     <li>动态已被删除的条目：显示「该动态已被删除」，点赞与评论都不再响应，评论列表只读；</li>
 *     <li>整页不支持长按（收藏列表不提供删除/举报入口）。</li>
 * </ol>
 */
public class FavoriteMomentAdapter extends ShareAdapter {

    private static final String TAG = "XTC_MOMENT_FavoriteMomentAdapter";

    private final ContactManager contactManager;

    /** 当前登录账号的 watchId：收藏里可能混有自己的动态，需要逐条判断。 */
    private final String mAccountWatchId;

    /** 已被删除的动态 id：这些条目用快照渲染且不可互动。 */
    private Set<String> deletedIds;

    public FavoriteMomentAdapter(Activity activity, VerticallyLinearLayoutManager layoutManager,
            LikeDrawableCache likeDrawableCache) {
        super(activity, false, null, null, layoutManager, null, likeDrawableCache);
        // 收藏列表不支持长按。
        this.longClickDisabled = true;
        this.contactManager = ContactManager.getInstance(activity);
        this.mAccountWatchId = AccountInfoServerImpl.getInstance(activity).getWatchAccountInfo()
                .getWatchId(activity);
    }

    @Override
    public String getLogTag() {
        return TAG;
    }

    /** 用一次加载的结果整体替换列表数据。 */
    public void setFavorites(List<DbMoment> moments, Map<String, List<DbLikeMessage>> praiseRecords,
            Set<String> deletedIds) {
        this.deletedIds = deletedIds;
        this.mData.clear();
        if (moments != null && !moments.isEmpty()) {
            this.mData.addAll(moments);
        }
        refreshPraiseRecordMap(praiseRecords);
        notifyDataSetChanged();
    }

    /** 收藏列表里取消收藏后移除该条。 */
    public void removeFavorite(String momentId) {
        if (TextUtils.isEmpty(momentId) || this.mData.isEmpty()) {
            return;
        }
        for (int index = 0; index < this.mData.size(); index++) {
            DbMoment item = this.mData.get(index);
            if (item != null && momentId.equals(item.getMomentId())) {
                this.mData.remove(index);
                notifyDataSetChanged();
                return;
            }
        }
    }

    private boolean isDeleted(DbMoment moment) {
        return moment != null && this.deletedIds != null && this.deletedIds.contains(moment.getMomentId());
    }

    /** 该条动态是不是自己发的。 */
    private boolean isSelfMoment(DbMoment moment) {
        return moment != null && !TextUtils.isEmpty(this.mAccountWatchId)
                && this.mAccountWatchId.equals(moment.getWatchId());
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        applyFavoriteState(holder, position);
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        // 空 payload 会走两参重载，删除态在那边已经处理过，这里只补局部刷新。
        if (payloads == null || payloads.isEmpty()) {
            return;
        }
        // 收藏态局部刷新只动图标，删除态与作者信息保持原样。
        if (AbsInteractionAdapter.PART_REFRESH_FAVORITE.equals(payloads.get(0))) {
            return;
        }
        applyFavoriteState(holder, position);
    }

    /** 已被删除的动态不能再评论，评论入口直接不展示（绑定末尾的显隐统一走这个判断）。 */
    @Override
    protected boolean shouldShowCommentIcon(DbMoment moment) {
        return !isDeleted(moment);
    }

    /** 删除态的展示与互动开关。 */
    private void applyFavoriteState(AbsViewHolder holder, int position) {
        if (holder == null || holder.itemView == null || checkTypeIsHandle(position)) {
            return;
        }
        int dataPosition = getDataItemPosition(position);
        if (dataPosition < 0 || dataPosition >= this.mData.size()) {
            return;
        }
        DbMoment moment = this.mData.get(dataPosition);
        bindAuthorClick(moment, holder);
        boolean deleted = isDeleted(moment);
        // 已删除的动态评论只读：不回复、不删除、「展开」也不跳详情页。
        if (holder.momentCommentView != null) {
            holder.momentCommentView.setInteractionEnabled(!deleted);
            // 收藏列表整体不支持长按：评论行的「长按删除」入口也一并摘掉（删除态本来就已被只读拦住）。
            holder.momentCommentView.setOnCommentDeleteListener(null);
        }
        View hint = holder.itemView.findViewById(R.id.tv_moment_deleted_hint);
        if (hint != null) {
            hint.setVisibility(deleted ? View.VISIBLE : View.GONE);
        }
        if (deleted) {
            // 点赞文案里的昵称是可点击的点赞入口，删除态要一并摘掉。
            holder.setOnLikeClickListener(null);
            refreshTvLikes(holder, moment);
        } else {
            holder.setOnLikeClickListener(this.onLikeClickListener);
        }
        // 与绑定末尾同一套判断，避免两处规则走岔。
        if (this.commentSwitch && shouldShowCommentIcon(moment)) {
            holder.showCommentIcon();
        } else {
            holder.hideCommentIcon();
        }
    }

    @Override
    public void onMomentLikeClick(AbsViewHolder holder, View view) {
        // 已删除的动态不能点赞；未删除的走原有逻辑。
        if (holder != null && isDeleted(holder.getDbMoment())) {
            LogUtil.i(TAG, "onMomentLikeClick: moment deleted, ignore");
            return;
        }
        super.onMomentLikeClick(holder, view);
    }

    /** 头像点击：打开该条动态作者的主页动态列表。 */
    private void bindAuthorClick(final DbMoment moment, AbsViewHolder holder) {
        View icon = holder.ivIcon;
        if (moment == null || icon == null) {
            return;
        }
        final String authorWatchId = moment.getWatchId();
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemUtil.isFastDoubleClick()) {
                    LogUtil.i(TAG, "onClick: click too fast.");
                    return;
                }
                if (moment.getType() != null && MomentTypeUtil.isOfficialType(moment.getType().intValue())) {
                    LogUtil.d(TAG, "官方动态不响应头像点击");
                    return;
                }
                if (TextUtils.isEmpty(authorWatchId)) {
                    return;
                }
                boolean isSelf = isSelfMoment(moment);
                Context context = view.getContext();
                Intent intent = new Intent(context, ShareActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_WATCH_ID, authorWatchId);
                intent.putExtra(Constants.INTENT_EXTRA_IS_SELF, isSelf);
                intent.putExtra(Constants.INTENT_EXTRA_ICON_PATH, resolveIconPath(moment, isSelf));
                intent.putExtra(Constants.INTENT_EXTRA_START_FROM_MOMENT, true);
                intent.putExtra(Constants.INTENT_EXTRA_NAME, resolveName(moment, isSelf));
                context.startActivity(intent);
            }
        });
    }

    @Override
    protected void setMomentName(DbMoment moment, AbsViewHolder holder, boolean isSelf, String name) {
        if (moment == null || moment.getType() == null
                || MomentTypeUtil.isOfficialType(moment.getType().intValue())) {
            super.setMomentName(moment, holder, isSelf, name);
            return;
        }
        boolean self = isSelfMoment(moment);
        String resolved = resolveName(moment, self);
        DressUtil.setNicknameSource(moment.getWatchId(), holder.tvName);
        holder.setTvName(self, resolved);
    }

    @Override
    protected void setMomentIcon(DbMoment moment, AbsViewHolder holder, boolean isSelf, String iconPath) {
        if (moment == null || moment.getType() == null
                || MomentTypeUtil.isOfficialType(moment.getType().intValue())) {
            super.setMomentIcon(moment, holder, isSelf, iconPath);
            return;
        }
        boolean self = isSelfMoment(moment);
        if (self) {
            holder.setIvIcon(AccountInfoServerImpl.getInstance(this.mContext).getMyHeadIconPath(), true);
            return;
        }
        ContactBean contactBean = this.contactManager
                .getContactWithoutShortNumberByWatchIdSync(moment.getWatchId());
        if (contactBean != null) {
            holder.setIvIcon(contactBean, false);
            return;
        }
        // 好友已被删除：回退到快照里保存的头像路径，保证卡片不空白。
        holder.setIvIcon(resolveIconPath(moment, false), false);
    }

    /** 昵称：自己取当前账号名，好友取联系人库，联系人被删时回退快照里的昵称。 */
    private String resolveName(DbMoment moment, boolean isSelf) {
        if (isSelf) {
            String myName = AccountInfoServerImpl.getInstance(this.mContext).getWatchAccountInfo()
                    .getName(this.mContext);
            return TextUtils.isEmpty(myName) ? this.mContext.getString(R.string.unknown_watch) : myName;
        }
        ContactBean contact = this.contactManager
                .getContactWithoutShortNumberByWatchIdSync(moment.getWatchId());
        if (contact != null && !TextUtils.isEmpty(contact.getName())) {
            return contact.getName();
        }
        if (!TextUtils.isEmpty(moment.getName())) {
            return moment.getName();
        }
        return this.mContext.getString(R.string.unknown_watch);
    }

    /**
     * 头像路径：自己取账号头像，好友取联系人头像，联系人被删时回退快照里的路径。
     *
     * <p>自己的头像必须走 {@code getMyHeadIconPath()}：它读的是本地已经落盘的头像文件，
     * 主列表也是用它。{@code WatchAccountInfo.getIcon()} 取的是账号库里那个字段，
     * 经常是空的，用它会让卡片退回默认头像。
     */
    private String resolveIconPath(DbMoment moment, boolean isSelf) {
        if (isSelf) {
            return AccountInfoServerImpl.getInstance(this.mContext).getMyHeadIconPath();
        }
        ContactBean contact = this.contactManager
                .getContactWithoutShortNumberByWatchIdSync(moment.getWatchId());
        if (contact != null && !TextUtils.isEmpty(contact.getPhotoPath())) {
            return contact.getPhotoPath();
        }
        return moment.getIconPath();
    }
}
