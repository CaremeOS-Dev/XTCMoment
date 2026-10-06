package com.xtc.moment.module.publish.visible.friends;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xtc.database.ormlite.CollectionUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.module.bean.Friend;
import com.xtc.moment.module.bean.FriendsVisibleBean;
import com.xtc.moment.util.SystemUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter of the "select friends" page.
 */
public class VisibleFriendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "VisibleFriendsAdapter";

    private static final int HEAD_TYPE = -1;
    private static final int END_TYPE = -2;
    private static final int NORMAL = 1;

    /** Visible range: only the selected friends can see the moment. */
    private static final int TYPE_INCLUDE = 1;
    /** Visible range: the selected friends cannot see the moment. */
    private static final int TYPE_EXCLUDE = 2;

    private final Context context;
    private final List<Friend> friends;
    private final FriendsVisibleBean friendsVisibleBean;
    private List<String> selectedFriends;

    private SelectedFriendsCallback friendsCallback;

    /** Notified when the selected friend list changes. */
    public interface SelectedFriendsCallback {
        void onDataSelected(List<String> selectedWatchIds);
    }

    public VisibleFriendsAdapter(Context context, List<Friend> friends, FriendsVisibleBean visibleBean,
                                 SelectedFriendsCallback callback) {
        this.context = context;
        this.friendsCallback = callback;
        this.friends = friends;
        this.friendsVisibleBean = visibleBean;
        this.selectedFriends = visibleBean.getFriends();
        if (this.selectedFriends == null) {
            this.selectedFriends = new ArrayList<String>();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEAD_TYPE) {
            return new HeadViewHolder(LayoutInflater.from(this.context)
                    .inflate(R.layout.item_friends_range_head, parent, false));
        }
        if (viewType == END_TYPE) {
            return new EndViewHolder(LayoutInflater.from(this.context)
                    .inflate(R.layout.item_friends_range_end, parent, false));
        }
        return new FriendsViewHolder(LayoutInflater.from(this.context)
                .inflate(R.layout.item_friends_range, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (position == 0) {
            HeadViewHolder holder = (HeadViewHolder) viewHolder;
            int type = this.friendsVisibleBean.getType();
            if (type == TYPE_INCLUDE) {
                holder.tittle.setText(dealTittle(this.context.getString(R.string.choose_friends_able_see)));
            } else if (type == TYPE_EXCLUDE) {
                holder.tittle.setText(dealTittle(this.context.getString(R.string.choose_friends_unable_see)));
            }
            return;
        }
        if (position == getItemCount() - 1) {
            return;
        }
        final FriendsViewHolder holder = (FriendsViewHolder) viewHolder;
        final Friend friend = this.friends.get(position - 1);
        if (friend == null) {
            return;
        }
        holder.tvFriendName.setText(friend.getName());
        Glide.with(this.context).load(friend.getFriendIcon())
                .apply(new RequestOptions().placeholder(R.drawable.default_custom_default))
                .into(holder.ivFriendHead);
        if (!CollectionUtil.isEmpty(this.selectedFriends)) {
            if (this.selectedFriends.contains(friend.getWatchId())) {
                holder.ivSelected.setImageResource(R.drawable.ic_selected_green);
            } else {
                holder.ivSelected.setImageResource(R.drawable.ic_selected_no_green);
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dealClick(friend.getWatchId(), holder.ivSelected);
            }
        });
    }

    /** Removes the "%1$s"/"%2$s" placeholders and colours the first part of the title. */
    private SpannableString dealTittle(String title) {
        int firstIndex = title.indexOf("%1$s");
        String withoutFirst = title.replace("%1$s", "");
        int secondIndex = withoutFirst.indexOf("%2$s");
        SpannableString spannable = new SpannableString(withoutFirst.replace("%2$s", ""));
        spannable.setSpan(new ForegroundColorSpan(
                        this.context.getResources().getColor(R.color.color_24df1a)),
                firstIndex, secondIndex, 33);
        return spannable;
    }

    private void dealClick(String watchId, ImageView selectedIcon) {
        if (SystemUtil.isFasterDoubleClick()) {
            LogUtil.i(TAG, "onClick: friendsViewHolder.itemView cilck too fast.");
            return;
        }
        if (!CollectionUtil.isEmpty(this.selectedFriends) && this.selectedFriends.contains(watchId)) {
            this.selectedFriends.remove(watchId);
            selectedIcon.setImageResource(R.drawable.ic_selected_no_green);
        } else {
            this.selectedFriends.add(watchId);
            selectedIcon.setImageResource(R.drawable.ic_selected_green);
        }
        this.friendsCallback.onDataSelected(this.selectedFriends);
    }

    @Override
    public int getItemCount() {
        if (CollectionUtil.isEmpty(this.friends)) {
            return 2;
        }
        return this.friends.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        int size = CollectionUtil.isEmpty(this.friends) ? 2 : 2 + this.friends.size();
        if (position == 0) {
            return HEAD_TYPE;
        }
        return position == size - 1 ? END_TYPE : NORMAL;
    }

    /** View holder of one friend row. */
    public class FriendsViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivFriendHead;
        private final ImageView ivSelected;
        private final TextView tvFriendName;

        public FriendsViewHolder(View view) {
            super(view);
            this.ivFriendHead = (ImageView) view.findViewById(R.id.iv_friend_head);
            this.ivSelected = (ImageView) view.findViewById(R.id.iv_selected);
            this.tvFriendName = (TextView) view.findViewById(R.id.tv_frined_name);
        }
    }

    /** View holder of the header. */
    public class HeadViewHolder extends RecyclerView.ViewHolder {

        final TextView tittle;

        public HeadViewHolder(View view) {
            super(view);
            this.tittle = (TextView) view.findViewById(R.id.tv_friends_head);
        }
    }

    /** View holder of the footer. */
    public class EndViewHolder extends RecyclerView.ViewHolder {
        public EndViewHolder(View view) {
            super(view);
        }
    }
}