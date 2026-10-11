package com.xtc.moment.module.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.xtc.contactapi.contact.bean.ContactBean;
import com.xtc.contactapi.contact.manager.ContactManager;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.share.ShareActivity;
import com.xtc.moment.module.share.adapter.ShareAdapter;
import com.xtc.moment.module.share.holder.AbsViewHolder;
import com.xtc.moment.module.widget.VerticallyLinearLayoutManager;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.util.DressUtil;
import com.xtc.moment.util.LikeDrawableCache;
import com.xtc.moment.util.MomentTypeUtil;
import com.xtc.moment.util.SystemUtil;

/**
 * 搜索结果列表适配器：卡片样式完全复用个人动态（{@link ShareAdapter}），
 * 只把「作者维度」改成逐条解析——个人主页一屏只有一个人，搜索结果是混排的。
 */
public class SearchMomentAdapter extends ShareAdapter {

    private static final String TAG = "XTC_MOMENT_SearchMomentAdapter";

    private final ContactManager contactManager;

    /** 当前登录账号的 watchId：搜索结果里可能混有自己的动态，需要逐条判断。 */
    private final String mAccountWatchId;

    public SearchMomentAdapter(Activity activity, boolean isSelf, String name, String iconPath,
            VerticallyLinearLayoutManager layoutManager, String watchId, LikeDrawableCache likeDrawableCache) {
        super(activity, isSelf, name, iconPath, layoutManager, watchId, likeDrawableCache);
        this.contactManager = ContactManager.getInstance(activity);
        this.mAccountWatchId = AccountInfoServerImpl.getInstance(activity).getWatchAccountInfo()
                .getWatchId(activity);
    }

    @Override
    public String getLogTag() {
        return TAG;
    }

    /** 该条动态是不是自己发的。 */
    private boolean isSelfMoment(DbMoment moment) {
        return moment != null && !TextUtils.isEmpty(this.mAccountWatchId)
                && this.mAccountWatchId.equals(moment.getWatchId());
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (holder == null || holder.itemView == null) {
            return;
        }
        int dataPosition = getDataItemPosition(position);
        if (dataPosition < 0 || dataPosition >= this.mData.size()) {
            return;
        }
        bindAuthorClick(this.mData.get(dataPosition), holder);
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
                boolean isSelf = isSelfMoment(moment);
                Context context = view.getContext();
                Intent intent = new Intent(context, ShareActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_WATCH_ID, authorWatchId);
                intent.putExtra(Constants.INTENT_EXTRA_IS_SELF, isSelf);
                intent.putExtra(Constants.INTENT_EXTRA_ICON_PATH, resolveIconPath(isSelf, authorWatchId));
                intent.putExtra(Constants.INTENT_EXTRA_START_FROM_MOMENT, true);
                intent.putExtra(Constants.INTENT_EXTRA_NAME, resolveName(isSelf, authorWatchId));
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
        String resolved = resolveName(self, moment.getWatchId());
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
        ContactBean contactBean = self ? null
                : this.contactManager.getContactWithoutShortNumberByWatchIdSync(moment.getWatchId());
        if (contactBean != null) {
            holder.setIvIcon(contactBean, false);
            return;
        }
        holder.setIvIcon(resolveIconPath(self, moment.getWatchId()), self);
    }

    /** 昵称：自己取当前账号名，好友取联系人库，兜底「未命名」。 */
    private String resolveName(boolean isSelf, String watchId) {
        if (isSelf) {
            String myName = AccountInfoServerImpl.getInstance(this.mContext).getWatchAccountInfo()
                    .getName(this.mContext);
            return TextUtils.isEmpty(myName) ? this.mContext.getString(R.string.unknown_watch) : myName;
        }
        ContactBean contact = this.contactManager.getContactWithoutShortNumberByWatchIdSync(watchId);
        if (contact == null || TextUtils.isEmpty(contact.getName())) {
            return this.mContext.getString(R.string.unknown_watch);
        }
        return contact.getName();
    }

    /** 头像路径：自己取账号头像，好友取联系人头像。 */
    private String resolveIconPath(boolean isSelf, String watchId) {
        if (isSelf) {
            return AccountInfoServerImpl.getInstance(this.mContext).getWatchAccountInfo().getIcon();
        }
        ContactBean contact = this.contactManager.getContactWithoutShortNumberByWatchIdSync(watchId);
        return contact == null ? null : contact.getPhotoPath();
    }
}
