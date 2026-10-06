package com.xtc.moment.module.publish.visible;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtc.contactapi.contact.manager.ContactManager;
import com.xtc.database.ormlite.CollectionUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.module.bean.FriendsVisibleBean;
import com.xtc.moment.module.publish.visible.friends.VisibleFriendsListActivity;
import com.xtc.moment.module.widget.MomentVisibleShowUtil;
import com.xtc.moment.util.SystemUtil;
import com.xtc.utils.ui.DimenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter of the visible range page: header, the four range options and a footer.
 */
public class VisibleTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "VisibleTypeActivity";

    private static final int HEAD_TYPE = -1;
    private static final int END_TYPE = -2;

    /** Number of friend names shown before the summary is collapsed. */
    private static final int SHOW_FRIENDS_NAME_MAX = 3;
    /** Vertical offset applied to the label when friend names are shown, in dp. */
    private static final float LABEL_TOP_MARGIN_DP = 10.0f;

    private final Context context;
    private final ContactManager contactManager;
    private final List<Integer> visibleTypeList = new ArrayList<Integer>();

    private FriendsVisibleBean friendsVisibleBean;
    private GetRecordCallback getRecordCallback;
    private FriendVisibleBeanCallBack setFriendVisibleBeanCallBack;

    /** Provides the last picked friend list for a range type. */
    public interface GetRecordCallback {
        List<String> onGetRecord(int type);
    }

    /** Notified when the range changed. */
    public interface FriendVisibleBeanCallBack {
        void setFriendVisibleBean(FriendsVisibleBean visibleBean);
    }

    public VisibleTypeAdapter(Context context, FriendsVisibleBean friendsVisibleBean) {
        this.context = context;
        this.contactManager = ContactManager.getInstance(context);
        this.friendsVisibleBean = friendsVisibleBean;
        this.visibleTypeList.add(HEAD_TYPE);
        this.visibleTypeList.add(0);
        this.visibleTypeList.add(3);
        this.visibleTypeList.add(1);
        this.visibleTypeList.add(2);
        this.visibleTypeList.add(END_TYPE);
    }

    /** Returns true for the two "selected friends" ranges. */
    boolean isShowPart(int type) {
        return type == 1 || type == 2;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEAD_TYPE) {
            return new HeadViewHolder(LayoutInflater.from(this.context)
                    .inflate(R.layout.item_friend_visible_head, parent, false));
        }
        if (viewType == END_TYPE) {
            return new EndViewHolder(LayoutInflater.from(this.context)
                    .inflate(R.layout.item_friend_visible_end, parent, false));
        }
        return new TypeViewHolder(LayoutInflater.from(this.context)
                .inflate(R.layout.item_friend_visible_type, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final int itemViewType = getItemViewType(position);
        if (itemViewType == HEAD_TYPE || itemViewType == END_TYPE) {
            return;
        }
        TypeViewHolder holder = (TypeViewHolder) viewHolder;
        if (itemViewType == 0) {
            holder.tvVisibleType.setText(this.context.getString(R.string.all_friends_able_visible));
            holder.tvVisibleDetails.setText(this.context.getString(R.string.all_friends_able_visible_details));
        } else if (itemViewType == 1) {
            holder.tvVisibleType.setText(this.context.getString(R.string.part_friends_able_visible));
            setFriendsShow(holder);
            if (this.friendsVisibleBean.getType() == itemViewType) {
                resetFriendsShow(holder);
            }
            holder.ivSelected.setVisibility(View.VISIBLE);
        } else if (itemViewType == 2) {
            holder.tvVisibleType.setText(this.context.getString(R.string.part_friends_unable_visible));
            setFriendsShow(holder);
            if (this.friendsVisibleBean.getType() == itemViewType) {
                resetFriendsShow(holder);
            }
            holder.ivSelected.setVisibility(View.VISIBLE);
        } else if (itemViewType == 3) {
            holder.tvVisibleType.setText(this.context.getString(R.string.no_friends_able_visible));
            holder.tvVisibleDetails.setText(this.context.getString(R.string.no_friends_able_visible_details));
        }
        if (itemViewType == this.friendsVisibleBean.getType()) {
            holder.ivChosen.setImageResource(R.drawable.ic_selected);
        } else {
            holder.ivChosen.setImageResource(R.drawable.ic_selected_no);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemUtil.isFastDoubleClick()) {
                    LogUtil.i(TAG, "onClick: typeViewHolder.itemView click too fast.");
                    return;
                }
                if (itemViewType != friendsVisibleBean.getType()) {
                    friendsVisibleBean.setType(itemViewType);
                    friendsVisibleBean.setFriends(getRecordCallback.onGetRecord(itemViewType));
                    setFriendVisibleBeanCallBack.setFriendVisibleBean(friendsVisibleBean);
                    if (isShowPart(itemViewType)
                            && CollectionUtil.isEmpty(friendsVisibleBean.getFriends())) {
                        VisibleFriendsListActivity.startForResult((Activity) context, friendsVisibleBean);
                    }
                    setFriendsVisibleBean(friendsVisibleBean);
                    return;
                }
                if (isShowPart(itemViewType)) {
                    VisibleFriendsListActivity.startForResult((Activity) context, friendsVisibleBean);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.visibleTypeList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return this.visibleTypeList.get(position).intValue();
    }

    public void setFriendsVisibleBean(FriendsVisibleBean friendsVisibleBean) {
        this.friendsVisibleBean = friendsVisibleBean;
        notifyDataSetChanged();
    }

    private void setFriendsShow(TypeViewHolder holder) {
        holder.tvVisibleDetails.setVisibility(View.GONE);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) holder.tvVisibleType.getLayoutParams();
        params.gravity = android.view.Gravity.CENTER_VERTICAL;
        params.topMargin = DimenUtil.dp2px(this.context, 0.0f);
    }

    private void resetFriendsShow(TypeViewHolder holder) {
        List<String> friends = this.friendsVisibleBean.getFriends();
        if (CollectionUtil.isEmpty(friends)) {
            return;
        }
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) holder.tvVisibleType.getLayoutParams();
        params.gravity = android.view.Gravity.NO_GRAVITY;
        params.topMargin = DimenUtil.dp2px(this.context, LABEL_TOP_MARGIN_DP);
        holder.tvVisibleDetails.setVisibility(View.VISIBLE);
        MomentVisibleShowUtil.showFriendsDetails(holder.tvVisibleDetails, friends, this.context);
    }

    public void setFriendVisibleBeanCallBack(FriendVisibleBeanCallBack callBack) {
        this.setFriendVisibleBeanCallBack = callBack;
    }

    public void setGetRecordCallback(GetRecordCallback callBack) {
        this.getRecordCallback = callBack;
    }

    /** View holder for one range option. */
    public class TypeViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivChosen;
        private final ImageView ivSelected;
        private final TextView tvVisibleDetails;
        private final TextView tvVisibleType;

        public TypeViewHolder(View view) {
            super(view);
            this.tvVisibleType = (TextView) view.findViewById(R.id.tv_visible_type);
            this.ivChosen = (ImageView) view.findViewById(R.id.iv_chosen);
            this.tvVisibleDetails = (TextView) view.findViewById(R.id.tv_visible_type_details);
            this.ivSelected = (ImageView) view.findViewById(R.id.iv_selected);
        }
    }

    /** View holder for the header. */
    public class HeadViewHolder extends RecyclerView.ViewHolder {
        public HeadViewHolder(View view) {
            super(view);
        }
    }

    /** View holder for the footer. */
    public class EndViewHolder extends RecyclerView.ViewHolder {
        public EndViewHolder(View view) {
            super(view);
        }
    }
}