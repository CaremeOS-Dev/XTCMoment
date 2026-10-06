package com.xtc.moment.module.scope;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xtc.database.ormlite.CollectionUtil;
import com.xtc.moment.R;
import com.xtc.moment.module.bean.Friend;

import java.util.ArrayList;
import java.util.List;

/**
 * 可见/不可见好友列表适配器。
 */
public class VisibleFriendsAdapter extends RecyclerView.Adapter<VisibleFriendsAdapter.FriendViewHolder> {

    private Context context;
    private List<Friend> friends;

    public VisibleFriendsAdapter(Context context, List<Friend> friends) {
        this.context = context;
        this.friends = friends;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FriendViewHolder(LayoutInflater.from(this.context)
                .inflate(R.layout.item_every_friend_see, parent, false));
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        Friend friend = this.friends.get(position);
        if (friend == null) {
            return;
        }
        Glide.with(this.context)
                .load(friend.getFriendIcon())
                .apply(new RequestOptions().placeholder(R.drawable.default_custom_default))
                .into(holder.ivFriendHead);
        String name = friend.getName();
        if (TextUtils.isEmpty(name)) {
            name = this.context.getString(R.string.unknown_watch);
        }
        holder.tvFriendName.setText(name);
    }

    @Override
    public int getItemCount() {
        if (CollectionUtil.isEmpty(this.friends)) {
            this.friends = new ArrayList<>();
        }
        return this.friends.size();
    }

    public void resetFriends(List<Friend> friends) {
        this.friends = friends;
        notifyDataSetChanged();
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivFriendHead;
        private TextView tvFriendName;

        public FriendViewHolder(View itemView) {
            super(itemView);
            this.ivFriendHead = (ImageView) itemView.findViewById(R.id.iv_friend_head);
            this.tvFriendName = (TextView) itemView.findViewById(R.id.tv_friend_name);
        }
    }
}